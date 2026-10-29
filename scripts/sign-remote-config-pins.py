#!/usr/bin/env python3
"""Create the public, signed tls_pinning_keyset value. Never uploads to Firebase.

Uses Python's standard library and OpenSSL. Private keys must already exist in an
external, non-Git directory; the script never generates or copies private keys.
"""
import argparse
import base64
import json
import os
from pathlib import Path
import re
import stat
import subprocess
import sys
import tempfile
import time

ROOT = Path(__file__).resolve().parents[1]
MAX_LONG = 2**63 - 1
ID = re.compile(r'[A-Za-z0-9][A-Za-z0-9._-]{0,63}\Z')
P256_SPKI_PREFIX = bytes.fromhex('3059301306072a8648ce3d020106082a8648ce3d03010703420004')


class SigningError(Exception):
    pass


def require(condition, message):
    if not condition:
        raise SigningError(message)


def b64url(value):
    return base64.urlsafe_b64encode(value).rstrip(b'=').decode('ascii')


def properties(path):
    """Read Java .properties continuations/escapes, matching Properties.load(InputStream)."""
    try:
        lines = path.read_text(encoding='iso-8859-1').splitlines()
    except OSError:
        raise SigningError('Cannot read the public build properties file.') from None
    result = {}
    logical = ''

    def unescape(value):
        return re.sub(r'\\u([0-9a-fA-F]{4})|\\(.)', lambda m: chr(int(m[1], 16)) if m[1]
                      else {'t': '\t', 'n': '\n', 'r': '\r', 'f': '\f'}.get(m[2], m[2]), value)

    for line in lines:
        logical += line.lstrip()
        trailing = len(logical) - len(logical.rstrip('\\'))
        if trailing % 2:
            logical = logical[:-1]
            continue
        line, logical = logical, ''
        if not line or line.startswith(('#', '!')):
            continue
        match = re.match(r'((?:\\.|[^=:\s])+)(?:\s*[=:]\s*|\s+)(.*)\Z', line)
        if match:
            result[unescape(match[1])] = unescape(match[2]).strip()
    require(not logical, 'Unterminated properties continuation.')
    return result


def setting(values, name, default=''):
    return os.environ.get(name, values.get(name, default)).strip()


def pairs(items, label):
    result = {}
    for item in items:
        name, sep, value = item.partition('=')
        require(sep and ID.fullmatch(name) and value, f'Invalid {label}; use KEY_ID=VALUE.')
        require(name not in result, f'Duplicate {label} identifier.')
        result[name] = value
    return result


def decode_public_key(value):
    try:
        return base64.b64decode(value + '=' * (-len(value) % 4), altchars=b'-_', validate=True)
    except (ValueError, TypeError):
        raise SigningError('Invalid trusted public verification key.') from None


def validate_hosts(encoded):
    try:
        def unique_object(items):
            require(len(items) == len(dict(items)), 'Duplicate JSON field in pin configuration.')
            return dict(items)
        hosts = json.loads(encoded, object_pairs_hook=unique_object)
        require(isinstance(hosts, list) and 1 <= len(hosts) <= 10, 'Expected 1 to 10 pinned hosts.')
        names = set()
        for host in hosts:
            require(isinstance(host, dict) and set(host) == {'hostname', 'pins'}, 'Invalid host schema.')
            name, pins = host['hostname'], host['pins']
            require(isinstance(name, str) and re.fullmatch(r'[a-z0-9](?:[a-z0-9.-]{0,251}[a-z0-9])?', name)
                    and name not in names, 'Invalid or duplicate hostname.')
            names.add(name)
            require(isinstance(pins, list) and 1 <= len(pins) <= 5, 'Expected 1 to 5 pins per host.')
            require(all(isinstance(pin, str) for pin in pins) and len(set(pins)) == len(pins),
                    'Invalid or duplicate pin.')
            for pin in pins:
                require(pin.startswith('sha256/') and
                        len(base64.b64decode(pin[7:], validate=True)) == 32, 'Invalid SHA-256 SPKI pin.')
        return hosts
    except (ValueError, TypeError, KeyError):
        raise SigningError('Invalid SIMLO_TLS_DEFAULT_PINS; values were not logged.') from None


def outside_project(path):
    return not path.is_relative_to(ROOT)


def private_key_path(value):
    path = Path(value).expanduser().absolute()
    try:
        resolved = path.resolve(strict=True)
        require(outside_project(path) and outside_project(resolved), 'Private keys must remain outside the project.')
        info = resolved.stat()
        require(stat.S_ISREG(info.st_mode) and info.st_nlink == 1, 'Private key must be a regular file without hard links.')
        require(info.st_uid == os.getuid() and stat.S_IMODE(info.st_mode) & 0o077 == 0,
                'Private key must belong to this user with permissions 0600 or 0400.')
        for location in (path.parent, resolved.parent):
            probe = subprocess.run(['git', '-C', str(location), 'rev-parse', '--git-dir'],
                                   capture_output=True, timeout=10)
            require(probe.returncode != 0,
                    'Private keys must remain outside every Git working tree.')
            require(not any((parent / '.git').exists() for parent in (location, *location.parents)),
                    'Private keys must remain outside Git repositories.')
        return resolved
    except OSError:
        raise SigningError('Cannot access external private key; path/value was not logged.') from None


def openssl(arguments, data=None):
    try:
        completed = subprocess.run(['openssl', *arguments], input=data, capture_output=True,
                                   timeout=30, env={**os.environ, 'LC_ALL': 'C'})
    except (OSError, subprocess.TimeoutExpired):
        raise SigningError('OpenSSL unavailable or timed out; no key material was logged.') from None
    require(completed.returncode == 0, 'OpenSSL operation failed; check key type/passphrase. Details were suppressed.')
    return completed.stdout


def publish_files(outputs):
    """Exclusive creation: never overwrite an envelope, key file, or existing symlink."""
    for path, _ in outputs:
        require(not path.exists() and not path.is_symlink(), 'Output already exists; choose a new version/output path.')
    written = []
    try:
        for path, content in outputs:
            with path.open('xb') as target:
                written.append(path)
                os.chmod(path, 0o600)
                target.write(content)
                target.flush()
                os.fsync(target.fileno())
    except OSError:
        for path in written:
            path.unlink(missing_ok=True)
        raise SigningError('Cannot write output files; no existing file was replaced.') from None


def generate(args, *, hosts_override=None):
    values = properties(args.properties.expanduser())
    hosts = validate_hosts(json.dumps(hosts_override) if hosts_override is not None
                           else setting(values, 'SIMLO_TLS_DEFAULT_PINS'))
    try:
        threshold = int(setting(values, 'SIMLO_KEYSET_SIGNATURE_THRESHOLD', '2'))
    except ValueError:
        raise SigningError('Invalid signature threshold.') from None
    require(1 <= threshold <= 10, 'Invalid signature threshold.')
    require(0 <= args.previous_version < args.version <= MAX_LONG,
            'Version must be positive and greater than --previous-version (maximum signed 64-bit integer).')
    require(1 <= args.valid_days <= 180, 'Validity must be between 1 and 180 days.')
    now = int(time.time())
    issued = args.issued_at if args.issued_at is not None else now
    expires = issued + args.valid_days * 86400
    require(0 <= issued <= now + 300 and now < expires <= MAX_LONG, 'Invalid issue/expiration time.')
    signers = pairs(args.signer, 'signer')
    passphrases = pairs(args.passphrase_env, 'passphrase environment')
    require(threshold <= len(signers) <= 10, 'Not enough distinct signers to satisfy the configured threshold.')
    require(set(passphrases) <= set(signers), 'Passphrase environment has an unknown signer identifier.')
    trusted = pairs(setting(values, 'SIMLO_KEYSET_TRUSTED_SIGNERS').split(',')
                    if setting(values, 'SIMLO_KEYSET_TRUSTED_SIGNERS') else [], 'trusted signer')
    public_keys = {name: decode_public_key(key) for name, key in trusted.items()}
    require(all(key.startswith(P256_SPKI_PREFIX) and len(key) == 91 for key in public_keys.values()),
            'Trusted verification keys must be EC P-256 SPKI DER.')
    require(len(set(public_keys.values())) == len(public_keys), 'Trusted signers must use distinct public keys.')
    if trusted:
        require(len(trusted) >= threshold, 'Configured trusted roots are below the signature threshold.')
    output = args.output.expanduser().absolute()
    public_output = args.signers_output.expanduser().absolute() if args.signers_output else output.with_suffix('.signers.properties')
    require(output != public_output, 'Envelope and public signer outputs must be different files.')
    for path in (output, public_output):
        require(path.parent.is_dir() and not path.exists() and not path.is_symlink(),
                'Output directory must exist and output files must not exist.')
    payload = json.dumps({'version': args.version, 'issuedAtEpochSeconds': issued,
                          'expiresAtEpochSeconds': expires, 'hosts': hosts},
                         ensure_ascii=True, separators=(',', ':')).encode('utf-8')
    require(len(payload) <= 64 * 1024, 'Payload exceeds the app limit of 64 KiB.')
    signatures, seen = [], set()
    for name, filename in signers.items():
        key = private_key_path(filename)
        passin = 'pass:'  # Frase secreta vazia: falha imediatamente em vez de solicitar entrada no stdin do payload.
        if name in passphrases:
            variable = passphrases[name]
            require(re.fullmatch(r'[A-Za-z_][A-Za-z0-9_]*', variable) and bool(os.environ.get(variable)),
                    'Missing or invalid passphrase environment variable.')
            passin = 'env:' + variable
        public = openssl(['pkey', '-in', str(key), '-passin', passin, '-pubout', '-outform', 'DER'])
        require(public.startswith(P256_SPKI_PREFIX) and len(public) == 91, 'Signer must be an EC P-256 private key.')
        require(public not in seen, 'Different signers must not use the same private key.')
        seen.add(public)
        if trusted:
            require(public_keys.get(name) == public, 'Signer does not match the configured trusted public key.')
        public_keys[name] = public
        signature = openssl(['dgst', '-sha256', '-sign', str(key), '-passin', passin], payload)
        # Verifica antes de gravar qualquer coisa; estes arquivos temporários contêm somente material público.
        with tempfile.TemporaryDirectory(prefix='simlo-public-verification-') as folder:
            pem = Path(folder) / 'public.pem'
            pem.write_bytes(openssl(['pkey', '-pubin', '-inform', 'DER', '-pubout'], public))
            der = Path(folder) / 'signature.der'
            der.write_bytes(signature)
            openssl(['dgst', '-sha256', '-verify', str(pem), '-signature', str(der)], payload)
        signatures.append({'keyId': name, 'value': b64url(signature)})
    envelope = json.dumps({'payload': b64url(payload), 'signatures': signatures}, indent=2).encode() + b'\n'
    public_config = ('# Public verification keys only; no private keys.\nSIMLO_KEYSET_TRUSTED_SIGNERS=' +
                     ','.join(name + '=' + b64url(key) for name, key in sorted(public_keys.items())) +
                     '\nSIMLO_KEYSET_SIGNATURE_THRESHOLD=' + str(threshold) + '\n').encode()
    publish_files([(output, envelope), (public_output, public_config)])
    print('Generated and verified tls_pinning_keyset: version', args.version,
          'with', len(signatures), 'signatures. Nothing was uploaded.')
    print('Envelope:', output)
    print('Public verification configuration:', public_output)
    if not trusted:
        print('Bootstrap: configure the exported public verification keys in the app build before publishing this manifest.')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--properties', type=Path, default=ROOT / '.local/build-security.properties')
    parser.add_argument('--signer', action='append', required=True, metavar='KEY_ID=/EXTERNAL/KEY.pem')
    parser.add_argument('--passphrase-env', action='append', default=[], metavar='KEY_ID=ENV_NAME')
    parser.add_argument('--version', type=int, required=True)
    parser.add_argument('--previous-version', type=int, default=0)
    parser.add_argument('--valid-days', type=int, default=30)
    parser.add_argument('--issued-at', type=int, help='UTC epoch seconds; defaults to current time')
    parser.add_argument('--output', type=Path, required=True)
    parser.add_argument('--signers-output', type=Path)
    args = parser.parse_args()
    try:
        generate(args)
        return 0
    except (SigningError, OSError, ValueError, subprocess.SubprocessError):
        # Evita rastreamentos de pilha/diagnósticos OpenSSL contendo material fornecido pelo operador.
        failure = sys.exc_info()[1]
        print('Error:', str(failure) if isinstance(failure, SigningError) else 'Signing failed; details suppressed.', file=sys.stderr)
        return 1


if __name__ == '__main__':
    sys.exit(main())
