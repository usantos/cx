#!/usr/bin/env python3
"""Exercise the packaged vendor TrustManager, without requests or account data."""
import argparse
import io
import os
from pathlib import Path
import subprocess
import tempfile
import zipfile


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--aar', type=Path, required=True)
    args = parser.parse_args()
    original = Path(__file__).resolve().parents[1] / 'third-party/FilaBRAAdapter-2.6.5.1.aar'
    with zipfile.ZipFile(original) as before, zipfile.ZipFile(args.aar) as after:
        assert before.namelist() == after.namelist(), 'AAR entries changed'
        for name in before.namelist():
            if name == 'classes.jar':
                with zipfile.ZipFile(io.BytesIO(before.read(name))) as x, zipfile.ZipFile(io.BytesIO(after.read(name))) as y:
                    assert x.namelist() == y.namelist(), 'JAR entries changed'
                    assert [n for n in x.namelist() if x.read(n) != y.read(n)] == ['a/d.class'], 'Unexpected SDK changes'
            else:
                assert before.read(name) == after.read(name), 'Unexpected AAR change: ' + name
    java_home = Path(os.environ['JAVA_HOME']) / 'bin'
    with tempfile.TemporaryDirectory(prefix='filabra-tls-test-') as tmp:
        root = Path(tmp)
        with zipfile.ZipFile(args.aar) as aar:
            (root / 'sdk.jar').write_bytes(aar.read('classes.jar'))
        subprocess.run(['openssl', 'req', '-x509', '-newkey', 'rsa:2048', '-nodes',
                        '-keyout', str(root / 'test.key'), '-out', str(root / 'test.crt'),
                        '-days', '1', '-subj', '/CN=localhost', '-addext', 'subjectAltName=DNS:localhost'],
                       check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        (root / 'TrustProbe.java').write_text('''
import java.io.*;
import java.security.cert.*;
import javax.net.ssl.*;
public class TrustProbe {
 public static void main(String[] args) throws Exception {
  X509TrustManager trust=(X509TrustManager)Class.forName("a.d").getConstructor().newInstance();
  X509Certificate cert=(X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new FileInputStream(args[0]));
  boolean accepted;
  try { trust.checkServerTrusted(new X509Certificate[]{cert}, "RSA"); accepted=true; }
  catch(CertificateException e) { accepted=false; }
  if(accepted!=Boolean.parseBoolean(args[1])) throw new AssertionError("Unexpected server certificate acceptance: "+accepted);
  if(trust.getAcceptedIssuers()==null || trust.getAcceptedIssuers().length==0) throw new AssertionError("No platform issuers");
 }
}
''')
        subprocess.run([str(java_home / 'javac'), str(root / 'TrustProbe.java')], check=True)
        command = [str(java_home / 'java'), '-cp', str(root) + os.pathsep + str(root / 'sdk.jar')]
        subprocess.run(command + ['TrustProbe', str(root / 'test.crt'), 'false'], check=True)
        subprocess.run([str(java_home / 'keytool'), '-importcert', '-noprompt', '-alias', 'test-only',
                        '-file', str(root / 'test.crt'), '-keystore', str(root / 'test.p12'),
                        '-storepass', 'test-only-password'], check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        subprocess.run(command + ['-Djavax.net.ssl.trustStore=' + str(root / 'test.p12'),
                                 '-Djavax.net.ssl.trustStorePassword=test-only-password',
                                 'TrustProbe', str(root / 'test.crt'), 'true'], check=True)
    print('PASS: untrusted certificate rejected; explicitly trusted control accepted.')


if __name__ == '__main__':
    main()
