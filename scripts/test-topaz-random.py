#!/usr/bin/env python3
"""Verify AES-key entropy source, key-format compatibility and archive isolation."""
import argparse
import hashlib
import io
import os
from pathlib import Path
import subprocess
import tempfile
import zipfile


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--original', type=Path, required=True)
    parser.add_argument('--aar', type=Path, required=True)
    args = parser.parse_args()
    assert hashlib.sha256(args.original.read_bytes()).hexdigest() == \
        '82e586ee0417971b52decca1c23af6a933fd4c52a89ddc8b430d7433ccd01aad'
    with zipfile.ZipFile(args.original) as before, zipfile.ZipFile(args.aar) as after:
        assert before.namelist() == after.namelist(), 'AAR entries changed'
        for name in before.namelist():
            if name != 'classes.jar':
                assert before.read(name) == after.read(name), 'AAR resource/native changed: ' + name
        with zipfile.ZipFile(io.BytesIO(before.read('classes.jar'))) as x, \
                zipfile.ZipFile(io.BytesIO(after.read('classes.jar'))) as y:
            assert x.namelist() == y.namelist(), 'JAR entries changed'
            changes = [n for n in x.namelist() if x.read(n) != y.read(n)]
            assert changes == ['br/com/topaz/n/c.class'], 'Unexpected SDK changes: ' + repr(changes)
        classes = after.read('classes.jar')
    java = Path(os.environ['JAVA_HOME']) / 'bin'
    with tempfile.TemporaryDirectory(prefix='topaz-key-test-') as tmp:
        root = Path(tmp)
        (root / 'sdk.jar').write_bytes(classes)
        listing = subprocess.check_output([str(java / 'javap'), '-c', '-p', '-classpath',
                                           str(root / 'sdk.jar'), 'br.com.topaz.n.c'], text=True)
        assert 'class java/security/SecureRandom' in listing, 'AES-key generator lacks SecureRandom'
        assert 'java/util/Random' not in listing, 'Predictable PRNG still reachable in key class'
        (root / 'KeyProbe.java').write_text('''
import java.lang.reflect.*;
public class KeyProbe {
 public static void main(String[] args) throws Exception {
  Class<?> sdk=Class.forName("br.com.topaz.n.c");
  Object instance=sdk.getConstructor().newInstance();
  Method generate=sdk.getDeclaredMethod("a", int.class);
  generate.setAccessible(true);
  for(int size: new int[]{0,1,16,32,33}) {
   for(int i=0;i<128;i++) {
    String value=(String)generate.invoke(instance,size);
    if(value.length()!=size || !value.matches("[0-9a-f]*"))
     throw new AssertionError("AES-key wire format changed");
   }
  }
 }
}
''')
        subprocess.run([str(java / 'javac'), str(root / 'KeyProbe.java')], check=True)
        subprocess.run([str(java / 'java'), '-Xverify:all', '-cp',
                        str(root) + os.pathsep + str(root / 'sdk.jar'), 'KeyProbe'], check=True)
    print('PASS: SecureRandom in AES-key generator; 640 format controls; only one SDK class changed.')


if __name__ == '__main__':
    main()
