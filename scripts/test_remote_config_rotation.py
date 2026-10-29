"""Certificate rotation CLI tests with ephemeral external signing keys/certificates."""
import base64
import json
import os
from pathlib import Path
import subprocess
import sys
import unittest

import test_remote_config_signing as signing_tests

ROOT = Path(__file__).resolve().parents[1]
SCRIPT = ROOT / 'scripts/rotate-remote-config-pins.py'


class RotationTest(unittest.TestCase):
    setUp = signing_tests.RemoteConfigSigningTest.setUp

    def prepare(self, extra_pins=0):
        # Inclui pins exclusivamente remotos para comprovar que o manifesto anterior é a fonte oficial.
        previous_hosts = json.loads(json.dumps(self.hosts))
        previous_hosts.append({'hostname': 'other.example.test', 'pins': list(self.hosts[0]['pins'])})
        for number in range(extra_pins):
            previous_hosts[0]['pins'].append('sha256/' + base64.b64encode(bytes([number + 1]) * 32).decode())
        result = signing_tests.RemoteConfigSigningTest.run_cli(
            self, env={'SIMLO_TLS_DEFAULT_PINS': json.dumps(previous_hosts)})
        self.assertEqual(0, result.returncode, result.stderr)
        self.previous = self.output
        public = self.output.with_suffix('.signers.properties').read_text()
        self.properties.write_text(self.properties.read_text() + public)
        self.rotated = self.work / 'rotation.json'
        self.certificate = self.work / 'new-server.pem'
        subprocess.run(['openssl', 'req', '-new', '-x509', '-key', str(self.keys['root-c']),
                        '-subj', '/CN=api.example.test', '-addext', 'subjectAltName=DNS:api.example.test',
                        '-days', '30', '-out', str(self.certificate)], check=True, capture_output=True)
        return previous_hosts

    def rotate(self, extra=()):
        command = [sys.executable, str(SCRIPT), '--properties', str(self.properties),
                   '--certificate', str(self.certificate), '--hostname', 'api.example.test',
                   '--previous', str(self.previous), '--output', str(self.rotated),
                   '--signer', 'root-a=' + str(self.keys['root-a']),
                   '--signer', 'root-b=' + str(self.keys['root-b'])]
        env = {**os.environ, 'SIMLO_TLS_DEFAULT_PINS': json.dumps(self.hosts),
               'SIMLO_KEYSET_SIGNATURE_THRESHOLD': '2'}
        env.pop('SIMLO_KEYSET_TRUSTED_SIGNERS', None)
        return subprocess.run(command + list(extra), capture_output=True, text=True, env=env)

    def payload(self, path):
        return json.loads(base64.urlsafe_b64decode(json.loads(path.read_text())['payload'] + '=='))

    def test_preserves_remote_pins_adds_certificate_and_increments_version(self):
        previous = self.prepare(extra_pins=1)
        original_properties = self.properties.read_bytes()
        result = self.rotate()
        self.assertEqual(0, result.returncode, result.stderr)
        payload = self.payload(self.rotated)
        self.assertEqual(8, payload['version'])
        self.assertEqual(previous[0]['pins'], payload['hosts'][0]['pins'][:-1])
        self.assertEqual(3, len(payload['hosts'][0]['pins']))
        self.assertEqual(previous[1:], payload['hosts'][1:])
        self.assertEqual(original_properties, self.properties.read_bytes())
        public = subprocess.run(['openssl', 'pkey', '-in', str(self.keys['root-c']), '-pubout',
                                 '-outform', 'DER'], capture_output=True, check=True).stdout
        import hashlib
        self.assertEqual('sha256/' + base64.b64encode(hashlib.sha256(public).digest()).decode(),
                         payload['hosts'][0]['pins'][-1])
        # Uma segunda rotação verifica as novas assinaturas e não deve duplicar o mesmo pin.
        second = self.work / 'rotation-next.json'
        result = self.rotate(['--previous', str(self.rotated), '--output', str(second)])
        self.assertEqual(0, result.returncode, result.stderr)
        self.assertEqual(9, self.payload(second)['version'])
        self.assertEqual(payload['hosts'], self.payload(second)['hosts'])

    def read_defaults(self):
        line = next(line for line in self.properties.read_text().splitlines()
                    if line.startswith('SIMLO_TLS_DEFAULT_PINS='))
        return json.loads(line.split('=', 1)[1])

    def test_opt_in_updates_defaults_and_preserves_local_and_remote_pins(self):
        self.prepare(extra_pins=1)
        local_only = 'sha256/' + base64.b64encode(bytes([99]) * 32).decode()
        defaults = json.loads(json.dumps(self.hosts))
        defaults[0]['pins'].append(local_only)
        defaults.append({'hostname': 'local-only.example.test', 'pins': [local_only]})
        content = self.properties.read_text()
        first, rest = content.split('\n', 1)
        self.properties.write_text('SIMLO_TLS_DEFAULT_PINS=' + json.dumps(defaults) + '\n' + rest +
                                   '# keep this comment\nCUSTOM_VALUE=a:b=c\n')
        result = self.rotate(['--update-local-defaults'])
        self.assertEqual(0, result.returncode, result.stderr)
        updated = self.read_defaults()
        self.assertEqual(defaults[0]['pins'], updated[0]['pins'][:2])
        self.assertEqual(defaults[1], updated[1])
        remote = self.payload(self.rotated)['hosts']
        self.assertTrue(set(remote[0]['pins']) <= set(updated[0]['pins']))
        self.assertIn(remote[1], updated)
        self.assertNotIn(local_only, remote[0]['pins'])
        self.assertIn('# keep this comment\nCUSTOM_VALUE=a:b=c\n', self.properties.read_text())
        self.assertEqual(content.split('\n', 1)[1].splitlines(),
                         self.rotated.with_suffix('.signers.properties').read_text().splitlines())

    def test_opt_in_rewrites_continued_property_and_keeps_crlf_and_permissions(self):
        import stat
        self.prepare()
        first, rest = self.properties.read_text().split('\n', 1)
        value = first.split('=', 1)[1]
        self.properties.write_bytes((' # comment\r\nSIMLO_TLS_DEFAULT_PINS=\\\r\n  ' + value + '\r\n' +
                                     rest.replace('\n', '\r\n') + 'OTHER=keep\r\n').encode())
        self.properties.chmod(0o640)
        result = self.rotate(['--update-local-defaults'])
        self.assertEqual(0, result.returncode, result.stderr)
        self.assertEqual(2, len(self.read_defaults()[0]['pins']))
        self.assertTrue(self.properties.read_bytes().startswith(b' # comment\r\nSIMLO_TLS_DEFAULT_PINS='))
        self.assertTrue(self.properties.read_bytes().endswith(b'OTHER=keep\r\n'))
        self.assertEqual(0o640, stat.S_IMODE(self.properties.stat().st_mode))

    def test_opt_in_signing_failure_leaves_defaults_unchanged(self):
        self.prepare()
        before = self.properties.read_bytes()
        result = self.rotate(['--update-local-defaults', '--signer', 'bad=' + str(self.keys['root-c'])])
        self.assertNotEqual(0, result.returncode)
        self.assertEqual(before, self.properties.read_bytes())
        self.assertFalse(self.rotated.exists())

    def test_opt_in_local_pin_limit_refuses_without_dropping_pins_or_generating_output(self):
        self.prepare()
        defaults = json.loads(json.dumps(self.hosts))
        defaults[0]['pins'] += ['sha256/' + base64.b64encode(bytes([n]) * 32).decode() for n in range(10, 14)]
        _, rest = self.properties.read_text().split('\n', 1)
        self.properties.write_text('SIMLO_TLS_DEFAULT_PINS=' + json.dumps(defaults) + '\n' + rest)
        before = self.properties.read_bytes()
        result = self.rotate(['--update-local-defaults'])
        self.assertNotEqual(0, result.returncode)
        self.assertEqual(before, self.properties.read_bytes())
        self.assertFalse(self.rotated.exists())

    def load_rotation_module(self):
        import importlib.util
        spec = importlib.util.spec_from_file_location('rotation_test_module', SCRIPT)
        module = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(module)
        return module

    def test_local_commit_failure_removes_only_new_artifacts(self):
        import argparse
        from unittest.mock import patch
        self.prepare()
        module = self.load_rotation_module()
        before = self.properties.read_bytes()
        args = argparse.Namespace(properties=self.properties, previous=self.previous,
                                  hostname='api.example.test', certificate=self.certificate,
                                  update_local_defaults=True, version=None, valid_days=30, issued_at=None,
                                  signer=['root-a=' + str(self.keys['root-a']), 'root-b=' + str(self.keys['root-b'])],
                                  passphrase_env=[], output=self.rotated, signers_output=None)
        with patch.object(module, 'commit_local_defaults', side_effect=OSError('simulated write failure')):
            with self.assertRaises(module.signing.SigningError):
                module.rotate(args)
        self.assertEqual(before, self.properties.read_bytes())
        self.assertFalse(self.rotated.exists())
        self.assertFalse(self.rotated.with_suffix('.signers.properties').exists())
        self.assertTrue(self.previous.exists())

    def test_local_commit_refuses_concurrent_changes(self):
        self.prepare()
        module = self.load_rotation_module()
        update = module.local_defaults_update(self.properties, module.signing.properties(self.properties), self.hosts)
        changed = self.properties.read_bytes() + b'# simultaneous edit\n'
        self.properties.write_bytes(changed)
        with self.assertRaises(module.signing.SigningError):
            module.commit_local_defaults(update)
        self.assertEqual(changed, self.properties.read_bytes())
        self.assertFalse(list(self.properties.parent.glob('.tls-defaults-*')))

    def test_opt_in_refuses_properties_symlink(self):
        self.prepare()
        alias = self.work / 'alias.properties'
        alias.symlink_to(self.properties)
        before = self.properties.read_bytes()
        result = self.rotate(['--update-local-defaults', '--properties', str(alias)])
        self.assertNotEqual(0, result.returncode)
        self.assertEqual(before, self.properties.read_bytes())
        self.assertFalse(self.rotated.exists())

    def test_supports_der_certificate_and_explicit_higher_version(self):
        self.prepare()
        der = self.work / 'server.cer'
        subprocess.run(['openssl', 'x509', '-in', str(self.certificate), '-outform', 'DER',
                        '-out', str(der)], check=True, capture_output=True)
        result = self.rotate(['--certificate', str(der), '--version', '12'])
        self.assertEqual(0, result.returncode, result.stderr)
        self.assertEqual(12, self.payload(self.rotated)['version'])

    def test_rejects_tampered_previous_manifest(self):
        self.prepare()
        envelope = json.loads(self.previous.read_text())
        payload = self.payload(self.previous)
        payload['version'] = 100
        envelope['payload'] = base64.urlsafe_b64encode(json.dumps(payload).encode()).rstrip(b'=').decode()
        self.previous.write_text(json.dumps(envelope))
        self.assertNotEqual(0, self.rotate().returncode)
        self.assertFalse(self.rotated.exists())

    def test_rejects_wrong_hostname_unknown_host_and_bad_version(self):
        self.prepare()
        for extra in [['--hostname', 'another.example.test'], ['--version', '7'], ['--version', '0']]:
            self.assertNotEqual(0, self.rotate(extra).returncode)
            self.assertFalse(self.rotated.exists())
        # Host correspondente no manifesto anterior, mas um certificado para outro servidor.
        other = self.work / 'other.pem'
        subprocess.run(['openssl', 'req', '-new', '-x509', '-key', str(self.keys['root-c']),
                        '-subj', '/CN=other.test', '-addext', 'subjectAltName=DNS:other.test',
                        '-days', '30', '-out', str(other)], check=True, capture_output=True)
        self.assertNotEqual(0, self.rotate(['--certificate', str(other)]).returncode)
        self.assertFalse(self.rotated.exists())

    def test_refuses_to_drop_old_pins_when_app_limit_is_reached(self):
        self.prepare(extra_pins=4)
        result = self.rotate()
        self.assertNotEqual(0, result.returncode)
        self.assertFalse(self.rotated.exists())

    def test_refuses_expired_certificate(self):
        self.prepare()
        expired = self.work / 'expired.pem'
        subprocess.run(['openssl', 'x509', '-in', str(self.certificate), '-signkey', str(self.keys['root-c']),
                        '-days', '0', '-out', str(expired)], check=True, capture_output=True)
        result = self.rotate(['--certificate', str(expired)])
        self.assertNotEqual(0, result.returncode)
        self.assertIn('expired', result.stderr)
        self.assertFalse(self.rotated.exists())

    def test_rejects_noncertificate_and_input_containing_private_key_header(self):
        self.prepare()
        fake = self.work / 'fake.pem'
        for value in ['not a certificate', '-----BEGIN PRIVATE KEY-----\nnot real key material']:
            fake.write_text(value)
            self.assertNotEqual(0, self.rotate(['--certificate', str(fake)]).returncode)
            self.assertFalse(self.rotated.exists())


if __name__ == '__main__':
    unittest.main()
