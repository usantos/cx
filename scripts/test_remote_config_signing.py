"""CLI contract tests; all real private test keys exist only outside Git/project."""
import base64
import json
import os
from pathlib import Path
import subprocess
import sys
import tempfile
import time
import unittest

ROOT = Path(__file__).resolve().parents[1]
SCRIPT = ROOT / 'scripts/sign-remote-config-pins.py'


class RemoteConfigSigningTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix='simlo-signing-test-')
        self.addCleanup(self.temp.cleanup)
        self.work = Path(self.temp.name)
        self.keys = {}
        for name in ('root-a', 'root-b', 'root-c'):
            key = self.work / (name + '.pem')
            subprocess.run(['openssl', 'genpkey', '-algorithm', 'EC', '-pkeyopt',
                            'ec_paramgen_curve:P-256', '-out', str(key)],
                           check=True, capture_output=True)
            key.chmod(0o600)
            self.keys[name] = key
        self.properties = self.work / 'build-security.properties'
        self.hosts = [{'hostname': 'api.example.test', 'pins':
                      ['sha256/' + base64.b64encode(bytes(32)).decode()]}]
        self.properties.write_text('SIMLO_TLS_DEFAULT_PINS=' + json.dumps(self.hosts) + '\n')
        self.output = self.work / 'remote-config.json'

    def run_cli(self, extra=(), signers=None, env=None):
        command = [sys.executable, str(SCRIPT), '--properties', str(self.properties),
                   '--version', '7', '--valid-days', '30', '--output', str(self.output)]
        for name, key in (signers or list(self.keys.items())[:2]):
            command += ['--signer', name + '=' + str(key)]
        return subprocess.run(command + list(extra), capture_output=True, text=True,
                              env={**os.environ, 'SIMLO_TLS_DEFAULT_PINS': json.dumps(self.hosts),
                                   'SIMLO_KEYSET_TRUSTED_SIGNERS': '', 'SIMLO_KEYSET_SIGNATURE_THRESHOLD': '2', **(env or {})})

    def test_generates_current_pins_and_independently_verifiable_signatures(self):
        result = self.run_cli()
        self.assertEqual(0, result.returncode, result.stderr)
        envelope = json.loads(self.output.read_text())
        payload = base64.urlsafe_b64decode(envelope['payload'] + '==')
        data = json.loads(payload)
        self.assertEqual(self.hosts, data['hosts'])
        self.assertEqual(7, data['version'])
        self.assertLess(abs(data['issuedAtEpochSeconds'] - int(time.time())), 10)
        self.assertEqual(30 * 86400, data['expiresAtEpochSeconds'] - data['issuedAtEpochSeconds'])
        self.assertEqual(['root-a', 'root-b'], [s['keyId'] for s in envelope['signatures']])
        public_properties = self.output.with_suffix('.signers.properties').read_text()
        self.assertNotIn('PRIVATE KEY', public_properties)
        self.assertIn('SIMLO_KEYSET_SIGNATURE_THRESHOLD=2', public_properties)
        for signature in envelope['signatures']:
            name = signature['keyId']
            pub = self.work / (name + '.pub.pem')
            pub.write_bytes(subprocess.run(['openssl', 'pkey', '-in', str(self.keys[name]),
                            '-pubout'], check=True, capture_output=True).stdout)
            sig = self.work / 'signature.der'
            sig.write_bytes(base64.urlsafe_b64decode(signature['value'] + '=='))
            args = ['openssl', 'dgst', '-sha256', '-verify', str(pub), '-signature', str(sig)]
            self.assertEqual(0, subprocess.run(args, input=payload, capture_output=True).returncode)
            self.assertNotEqual(0, subprocess.run(args, input=payload + b' ', capture_output=True).returncode)
        for key in self.keys.values():
            self.assertNotIn(key.read_text(), result.stdout + result.stderr + self.output.read_text())

    def test_refuses_duplicate_keys_under_different_ids(self):
        result = self.run_cli(signers=[('root-a', self.keys['root-a']), ('root-b', self.keys['root-a'])])
        self.assertNotEqual(0, result.returncode)
        self.assertFalse(self.output.exists())

    def test_refuses_private_key_path_inside_project_without_reading_it(self):
        with tempfile.TemporaryDirectory(dir=ROOT / 'build') as folder:
            fake = Path(folder) / 'not-a-private-key.pem'
            fake.write_text('not a key')
            fake.chmod(0o600)
            result = self.run_cli(signers=[('root-a', fake), ('root-b', self.keys['root-b'])])
        self.assertNotEqual(0, result.returncode)
        self.assertIn('outside', result.stderr)
        self.assertFalse(self.output.exists())

    def test_refuses_key_path_in_other_git_repository(self):
        repo = self.work / 'other-repository'
        repo.mkdir()
        subprocess.run(['git', 'init', '-q', str(repo)], check=True)
        fake = repo / 'not-a-private-key.pem'
        fake.write_text('not a key')
        fake.chmod(0o600)
        result = self.run_cli(signers=[('root-a', fake), ('root-b', self.keys['root-b'])])
        self.assertNotEqual(0, result.returncode)
        self.assertIn('Git', result.stderr)
        self.assertFalse(self.output.exists())

    def test_refuses_key_path_in_bare_git_repository(self):
        repo = self.work / 'bare.git'
        subprocess.run(['git', 'init', '--bare', '-q', str(repo)], check=True)
        fake = repo / 'not-a-private-key.pem'
        fake.write_text('not a key')
        fake.chmod(0o600)
        result = self.run_cli(signers=[('root-a', fake), ('root-b', self.keys['root-b'])])
        self.assertNotEqual(0, result.returncode)
        self.assertIn('Git', result.stderr)

    def test_refuses_duplicate_identifiers_and_unsupported_curve(self):
        result = self.run_cli(signers=[('root-a', self.keys['root-a']), ('root-a', self.keys['root-b'])])
        self.assertNotEqual(0, result.returncode)
        other = self.work / 'other-curve.pem'
        subprocess.run(['openssl', 'genpkey', '-algorithm', 'EC', '-pkeyopt',
                        'ec_paramgen_curve:P-384', '-out', str(other)], check=True, capture_output=True)
        other.chmod(0o600)
        result = self.run_cli(signers=[('root-a', other), ('root-b', self.keys['root-b'])])
        self.assertNotEqual(0, result.returncode)
        self.assertFalse(self.output.exists())

    def test_reads_java_properties_escapes_and_preserves_current_pins(self):
        self.properties.write_text('SIMLO_TLS_DEFAULT_PINS=' + json.dumps(self.hosts).replace(':', r'\:') + '\n')
        environment = {**os.environ, 'SIMLO_KEYSET_TRUSTED_SIGNERS': '', 'SIMLO_KEYSET_SIGNATURE_THRESHOLD': '2'}
        environment.pop('SIMLO_TLS_DEFAULT_PINS', None)
        command = [sys.executable, str(SCRIPT), '--properties', str(self.properties), '--version', '1',
                   '--output', str(self.output), '--signer', 'root-a=' + str(self.keys['root-a']),
                   '--signer', 'root-b=' + str(self.keys['root-b'])]
        result = subprocess.run(command, capture_output=True, text=True, env=environment)
        self.assertEqual(0, result.returncode, result.stderr)
        payload = json.loads(self.output.read_text())['payload']
        self.assertEqual(self.hosts, json.loads(base64.urlsafe_b64decode(payload + '=='))['hosts'])

    def test_refuses_overwrite_and_weak_key_permissions(self):
        self.output.write_text('preserve me')
        self.assertNotEqual(0, self.run_cli().returncode)
        self.assertEqual('preserve me', self.output.read_text())
        self.output.unlink()
        self.keys['root-a'].chmod(0o644)
        result = self.run_cli()
        self.assertNotEqual(0, result.returncode)
        self.assertFalse(self.output.exists())

    def test_refuses_invalid_pins_versions_and_validity(self):
        for extra, env in [(['--version', '0'], {}), (['--previous-version', '7'], {}),
                           (['--valid-days', '181'], {}), ([], {'SIMLO_TLS_DEFAULT_PINS': '[]'}),
                           ([], {'SIMLO_TLS_DEFAULT_PINS': '[{"hostname":"api.test","pins":["sha256/invalid"]}]'})]:
            with self.subTest(extra=extra, env=env):
                self.assertNotEqual(0, self.run_cli(extra, env=env).returncode)
                self.assertFalse(self.output.exists())

    def test_requires_quorum_and_matches_existing_trusted_public_keys(self):
        self.assertNotEqual(0, self.run_cli(signers=[('root-a', self.keys['root-a'])]).returncode)
        pub = subprocess.run(['openssl', 'pkey', '-in', str(self.keys['root-c']), '-pubout',
                              '-outform', 'DER'], check=True, capture_output=True).stdout
        encoded = base64.urlsafe_b64encode(pub).rstrip(b'=').decode()
        result = self.run_cli(env={'SIMLO_KEYSET_TRUSTED_SIGNERS': 'root-a=' + encoded})
        self.assertNotEqual(0, result.returncode)
        self.assertFalse(self.output.exists())

    def test_supports_encrypted_private_key_without_passphrase_on_command_line(self):
        encrypted = self.work / 'encrypted.pem'
        env = {**os.environ, 'SIMLO_TEST_KEY_PASSPHRASE': 'test-only-ephemeral-passphrase'}
        subprocess.run(['openssl', 'pkey', '-in', str(self.keys['root-a']), '-aes-256-cbc',
                        '-passout', 'env:SIMLO_TEST_KEY_PASSPHRASE', '-out', str(encrypted)],
                       check=True, capture_output=True, env=env)
        encrypted.chmod(0o600)
        result = self.run_cli(['--passphrase-env', 'root-a=SIMLO_TEST_KEY_PASSPHRASE'],
                             signers=[('root-a', encrypted), ('root-b', self.keys['root-b'])], env=env)
        self.assertEqual(0, result.returncode, result.stderr)
        self.assertNotIn(env['SIMLO_TEST_KEY_PASSPHRASE'], result.stdout + result.stderr)


if __name__ == '__main__':
    unittest.main()
