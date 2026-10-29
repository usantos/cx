#!/usr/bin/env python3
"""Replace only the pinned SDK's AES-key PRNG; preserve its wire format and JNI."""
import argparse
import hashlib
import io
from pathlib import Path
import struct
import zipfile


INPUT_SHA256 = '82e586ee0417971b52decca1c23af6a933fd4c52a89ddc8b430d7433ccd01aad'
TARGET = 'br/com/topaz/n/c.class'


def secure_key_generator(data):
    """Rewrite a class-pool type, without changing bytecodes or pool indices.

    In this exact class Random is used exclusively by a(int), the AES-key
    generator. SecureRandom has the same constructor and nextInt contract.
    The immutable AAR digest gates this assumption before every rewrite.
    """
    source, output = io.BytesIO(data), io.BytesIO()
    header = source.read(10)
    if header[:4] != b'\xca\xfe\xba\xbe':
        raise ValueError('Invalid class file')
    output.write(header)
    count = struct.unpack('>H', header[8:10])[0]
    index, replacements = 1, 0
    sizes = {3: 4, 4: 4, 5: 8, 6: 8, 7: 2, 8: 2, 9: 4, 10: 4,
             11: 4, 12: 4, 15: 3, 16: 2, 17: 4, 18: 4, 19: 2, 20: 2}
    while index < count:
        tag_bytes = source.read(1)
        if not tag_bytes:
            raise ValueError('Truncated constant pool')
        tag = tag_bytes[0]
        output.write(tag_bytes)
        if tag == 1:
            length = struct.unpack('>H', source.read(2))[0]
            value = source.read(length)
            if len(value) != length:
                raise ValueError('Truncated UTF8 entry')
            if value == b'java/util/Random':
                value = b'java/security/SecureRandom'
                replacements += 1
            output.write(struct.pack('>H', len(value)))
            output.write(value)
        else:
            if tag not in sizes:
                raise ValueError('Unsupported constant pool tag')
            value = source.read(sizes[tag])
            if len(value) != sizes[tag]:
                raise ValueError('Truncated constant pool entry')
            output.write(value)
        index += 2 if tag in (5, 6) else 1
    if replacements != 1:
        raise ValueError('Expected exactly one Random type in the pinned class')
    output.write(source.read())
    return output.getvalue()


def rewrite_archive(data, target, replacement):
    result = io.BytesIO()
    with zipfile.ZipFile(io.BytesIO(data)) as original, zipfile.ZipFile(result, 'w') as patched:
        names = original.namelist()
        if len(names) != len(set(names)) or names.count(target) != 1:
            raise ValueError('Unexpected archive entries')
        for entry in original.infolist():
            content = original.read(entry)
            if entry.filename == target:
                content = replacement(content)
            entry.date_time = (1980, 1, 1, 0, 0, 0)
            patched.writestr(entry, content)
    return result.getvalue()


def harden(data):
    if hashlib.sha256(data).hexdigest() != INPUT_SHA256:
        raise ValueError('Topaz input changed: review key-generation hardening before building')
    return rewrite_archive(data, 'classes.jar',
                           lambda jar: rewrite_archive(jar, TARGET, secure_key_generator))


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--input', type=Path, required=True)
    parser.add_argument('--output', type=Path, required=True)
    parser.add_argument('--classes-output', type=Path)
    args = parser.parse_args()
    result = harden(args.input.read_bytes())
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_bytes(result)
    if args.classes_output:
        with zipfile.ZipFile(io.BytesIO(result)) as archive:
            args.classes_output.parent.mkdir(parents=True, exist_ok=True)
            args.classes_output.write_bytes(archive.read('classes.jar'))


if __name__ == '__main__':
    main()
