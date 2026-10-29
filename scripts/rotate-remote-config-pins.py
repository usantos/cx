#!/usr/bin/env python3
"""Add a certificate SPKI pin to a verified previous manifest and sign its next version.

Only generates local public artifacts for tls_pinning_keyset; never contacts Firebase.
"""
import argparse
import base64
import hashlib
import importlib.util
import json
import os
import stat
from pathlib import Path
import re
import sys
import tempfile

_spec = importlib.util.spec_from_file_location('pin_signing', Path(__file__).with_name('sign-remote-config-pins.py'))
signing = importlib.util.module_from_spec(_spec)
_spec.loader.exec_module(signing)


def read_limited(path, maximum, label):
    try:
        with path.expanduser().open('rb') as source:
            result = source.read(maximum + 1)
        signing.require(0 < len(result) <= maximum, f'{label} is empty or exceeds the size limit.')
        return result
    except OSError:
        raise signing.SigningError(f'Cannot read {label}; contents were not logged.') from None


def strict_json(data):
    def unique(items):
        signing.require(len(items) == len(dict(items)), 'Duplicate JSON fields.')
        return dict(items)
    try:
        return json.loads(data, object_pairs_hook=unique)
    except (ValueError, UnicodeError):
        raise signing.SigningError('Invalid JSON; contents were not logged.') from None


def verified_previous(path, values):
    envelope = strict_json(read_limited(path, 256 * 1024, 'previous signed envelope'))
    signing.require(isinstance(envelope, dict) and set(envelope) == {'payload', 'signatures'},
                    'Expected a signed envelope with payload and signatures.')
    signing.require(isinstance(envelope['payload'], str), 'Invalid previous payload.')
    payload_bytes = signing.decode_public_key(envelope['payload'])
    signing.require(0 < len(payload_bytes) <= 64 * 1024, 'Previous payload exceeds the app size limit.')
    roots = signing.pairs(signing.setting(values, 'SIMLO_KEYSET_TRUSTED_SIGNERS').split(',')
                          if signing.setting(values, 'SIMLO_KEYSET_TRUSTED_SIGNERS') else [], 'trusted signer')
    threshold = int(signing.setting(values, 'SIMLO_KEYSET_SIGNATURE_THRESHOLD', '2'))
    signing.require(1 <= threshold <= len(roots), 'Configure the app trusted public signers before rotating pins.')
    decoded = {name: signing.decode_public_key(key) for name, key in roots.items()}
    signing.require(all(len(key) == 91 and key.startswith(signing.P256_SPKI_PREFIX) for key in decoded.values())
                    and len(set(decoded.values())) == len(decoded), 'Invalid trusted P-256 public signers.')
    signatures = envelope['signatures']
    signing.require(isinstance(signatures, list) and 1 <= len(signatures) <= 10, 'Invalid previous signature list.')
    checked, accepted = set(), 0
    # Nenhuma chave é confiável apenas porque o envelope anterior inclui seu identificador.
    with tempfile.TemporaryDirectory(prefix='simlo-previous-public-verification-') as folder:
        public = Path(folder) / 'public.pem'
        signature_file = Path(folder) / 'signature.der'
        for signature in signatures:
            signing.require(isinstance(signature, dict) and set(signature) == {'keyId', 'value'} and
                            isinstance(signature['keyId'], str) and isinstance(signature['value'], str),
                            'Invalid signature schema.')
            name = signature['keyId']
            signing.require(name not in checked, 'Duplicate previous signer identifier.')
            checked.add(name)
            if name not in decoded:
                continue
            try:
                public.write_bytes(signing.openssl(['pkey', '-pubin', '-inform', 'DER', '-pubout'], decoded[name]))
                signature_file.write_bytes(signing.decode_public_key(signature['value']))
                signing.openssl(['dgst', '-sha256', '-verify', str(public), '-signature', str(signature_file)], payload_bytes)
                accepted += 1
            except signing.SigningError:
                continue
    signing.require(accepted >= threshold, 'Previous envelope signature quorum is invalid; rotation refused.')
    payload = strict_json(payload_bytes)
    signing.require(isinstance(payload, dict) and set(payload) ==
                    {'version', 'issuedAtEpochSeconds', 'expiresAtEpochSeconds', 'hosts'}, 'Invalid previous payload schema.')
    signing.require(type(payload['version']) is int and 0 < payload['version'] < signing.MAX_LONG,
                    'Previous version is invalid or exhausted.')
    issued, expires = payload['issuedAtEpochSeconds'], payload['expiresAtEpochSeconds']
    signing.require(type(issued) is int and type(expires) is int and 0 <= issued < expires <= signing.MAX_LONG
                    and expires - issued <= 180 * 86400, 'Invalid previous validity window.')
    # Um envelope autenticado expirado ainda é um histórico válido de versão/pins para renovação.
    hosts = signing.validate_hosts(json.dumps(payload['hosts']))
    return payload['version'], hosts


def certificate_pin(path, hostname):
    data = read_limited(path, 1024 * 1024, 'public server certificate')
    signing.require(not re.search(br'-----BEGIN [A-Z0-9 ]*PRIVATE KEY-----', data),
                    'Supply only the public certificate; never a private key or combined key file.')
    normalized = None
    for encoding in ('PEM', 'DER'):
        try:
            normalized = signing.openssl(['x509', '-inform', encoding, '-outform', 'PEM'], data)
            break
        except signing.SigningError:
            continue
    signing.require(normalized is not None, 'Cannot parse a PEM or DER X.509 public certificate.')
    match = signing.openssl(['x509', '-noout', '-checkhost', hostname], normalized)
    signing.require(match.strip().endswith(b'does match certificate'), 'Certificate hostname does not match --hostname.')
    try:
        signing.openssl(['x509', '-noout', '-checkend', '0'], normalized)
    except signing.SigningError:
        raise signing.SigningError('Certificate has expired; rotation refused.') from None
    public = signing.openssl(['x509', '-pubkey', '-noout'], normalized)
    spki = signing.openssl(['pkey', '-pubin', '-outform', 'DER'], public)
    return 'sha256/' + base64.b64encode(hashlib.sha256(spki).digest()).decode('ascii')


def local_defaults_update(path, values, remote_hosts):
    """Prepare a local-only union, preserving unrelated Java properties verbatim."""
    path = path.expanduser().absolute()
    signing.require(not path.is_symlink(), 'Local properties must not be a symlink.')
    info = path.stat()
    signing.require(stat.S_ISREG(info.st_mode) and info.st_nlink == 1,
                    'Local properties must be a regular file without hard links.')
    original = path.read_bytes()
    local_encoded = values.get('SIMLO_TLS_DEFAULT_PINS', '').strip()
    merged = signing.validate_hosts(local_encoded) if local_encoded else []
    for remote in remote_hosts:
        local = next((host for host in merged if host['hostname'] == remote['hostname']), None)
        if local is None:
            merged.append({'hostname': remote['hostname'], 'pins': list(remote['pins'])})
        else:
            local['pins'].extend(pin for pin in remote['pins'] if pin not in local['pins'])
    signing.validate_hosts(json.dumps(merged))
    source = original.decode('iso-8859-1')
    newline = '\r\n' if '\r\n' in source else '\n'
    replacement = 'SIMLO_TLS_DEFAULT_PINS=' + json.dumps(merged, separators=(',', ':')) + newline
    result, block, logical, found = [], [], '', False
    for line in source.splitlines(keepends=True):
        block.append(line)
        fragment = line.rstrip('\r\n').lstrip()
        # Comentários nunca participam de continuações de propriedades Java.
        comment = not logical and fragment.startswith(('#', '!'))
        logical += fragment
        trailing = len(logical) - len(logical.rstrip('\\'))
        if trailing % 2 and not comment:
            logical = logical[:-1]
            continue
        match = None if comment else re.match(r'((?:\\.|[^=:\s])+)(?:\s*[=:]\s*|\s+)(.*)\Z', logical)
        key = match[1] if match else ''
        key = re.sub(r'\\u([0-9a-fA-F]{4})|\\(.)',
                     lambda m: chr(int(m[1], 16)) if m[1] else m[2], key)
        if key == 'SIMLO_TLS_DEFAULT_PINS':
            if not found:
                result.append(replacement)
            found = True
        else:
            result.extend(block)
        block, logical = [], ''
    signing.require(not block, 'Unterminated properties continuation; local defaults not changed.')
    if not found:
        if result and not result[-1].endswith(('\n', '\r')):
            result.append(newline)
        result.append(replacement)
    return path, original, ''.join(result).encode('iso-8859-1'), stat.S_IMODE(info.st_mode)


def commit_local_defaults(update):
    path, original, updated, mode = update
    temporary = None
    try:
        with tempfile.NamedTemporaryFile(dir=path.parent, prefix='.tls-defaults-', delete=False) as target:
            temporary = Path(target.name)
            os.chmod(temporary, mode)
            target.write(updated)
            target.flush()
            os.fsync(target.fileno())
        signing.require(not path.is_symlink() and path.read_bytes() == original,
                        'Local properties changed during signing; update refused.')
        os.replace(temporary, path)
    finally:
        if temporary is not None:
            temporary.unlink(missing_ok=True)


def rotate(args):
    values = signing.properties(args.properties.expanduser())
    previous_version, hosts = verified_previous(args.previous, values)
    host = next((entry for entry in hosts if entry['hostname'] == args.hostname), None)
    signing.require(host is not None, 'Hostname must already exist in the verified previous manifest.')
    new_pin = certificate_pin(args.certificate, args.hostname)
    if new_pin not in host['pins']:
        host['pins'].append(new_pin)
    # Revalida os limites após adicionar, sem descartar silenciosamente nenhum pin existente.
    signing.validate_hosts(json.dumps(hosts))
    update = local_defaults_update(args.properties, values, hosts) if args.update_local_defaults else None
    args.previous_version = previous_version
    args.version = args.version if args.version is not None else previous_version + 1
    args.signer = args.signer or [name + '=' + str(Path.home() / '.config/simlo-signers' / (name + '.pem'))
                                  for name in ('root-a', 'root-b')]
    signing.generate(args, hosts_override=hosts)
    if update is not None:
        try:
            commit_local_defaults(update)
        except (OSError, signing.SigningError):
            # generate() cria estes de forma exclusiva; remove os artefatos desta execução se a atualização falhou.
            output = args.output.expanduser().absolute()
            public_output = args.signers_output.expanduser().absolute() if args.signers_output else output.with_suffix('.signers.properties')
            for artifact in (output, public_output):
                artifact.unlink(missing_ok=True)
            raise signing.SigningError('Local defaults update failed; generated outputs removed and existing properties preserved.') from None
        print('Updated SIMLO_TLS_DEFAULT_PINS in local properties; existing local pins and other settings preserved.')
    print('Preserved every previous host/pin; certificate pin checked for', args.hostname + '.')
    print('Previous version:', previous_version, '| New version:', args.version)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--certificate', type=Path, required=True, help='Public leaf certificate in PEM or DER')
    parser.add_argument('--hostname', required=True, help='Existing pinned hostname, without URL scheme/path')
    parser.add_argument('--previous', type=Path, required=True, help='Latest published signed envelope JSON')
    parser.add_argument('--properties', type=Path, default=signing.ROOT / '.local/build-security.properties')
    parser.add_argument('--output', type=Path, required=True)
    parser.add_argument('--update-local-defaults', action='store_true',
                        help='Also atomically merge pins into SIMLO_TLS_DEFAULT_PINS in --properties')
    parser.add_argument('--signers-output', type=Path)
    parser.add_argument('--version', type=int, help='Defaults to authenticated previous version + 1')
    parser.add_argument('--valid-days', type=int, default=30)
    parser.add_argument('--issued-at', type=int)
    parser.add_argument('--signer', action='append', help='KEY_ID=/EXTERNAL/KEY.pem; defaults to external root-a/root-b')
    parser.add_argument('--passphrase-env', action='append', default=[], metavar='KEY_ID=ENV_NAME')
    args = parser.parse_args()
    try:
        rotate(args)
        return 0
    except (signing.SigningError, OSError, ValueError, TypeError):
        failure = sys.exc_info()[1]
        print('Error:', str(failure) if isinstance(failure, signing.SigningError)
              else 'Rotation failed; details suppressed.', file=sys.stderr)
        return 1


if __name__ == '__main__':
    sys.exit(main())
