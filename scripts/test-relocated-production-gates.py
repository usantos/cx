#!/usr/bin/env python3
"""Exercise actual Gradle gates against forbidden production artifacts in moved roots.
Run serially with builds. Every fixture is owned by this script and removed in finally.
"""
from pathlib import Path
import os
import subprocess

ROOT = Path(__file__).resolve().parent.parent
HOST = ROOT / 'feature/home/presentation/src/host/kotlin/br/gov/caixa/loterias/feature/home/presentation/host/components'
CASES = [
    ('ForbiddenFeatureHostActivity.kt', 'verifyComposeMigration', 'package br.gov.caixa.loterias.feature.home.presentation.host.components\nclass ForbiddenFeatureHostActivity\n'),
    ('ForbiddenFeatureHost.java', 'verifyComposeMigration', 'package br.gov.caixa.loterias.feature.home.presentation.host.components;\nclass ForbiddenFeatureHost {}\n'),
    ('ForbiddenFeatureHostColor.kt', 'verifyCaixaDS', 'package br.gov.caixa.loterias.feature.home.presentation.host.components\nval forbiddenColor = androidx.compose.ui.graphics.Color(0xff123456)\n'),
]
if __name__ == '__main__':
    env = dict(os.environ, JAVA_HOME='/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home')
    HOST.mkdir(parents=True, exist_ok=True)
    for name, gate, body in CASES:
        fixture = HOST / name
        if fixture.exists():
            raise RuntimeError(f'Refusing to overwrite {fixture}')
        try:
            fixture.write_text(body)
            result = subprocess.run([str(ROOT / 'gradlew'), gate, '--offline', '--console=plain'], cwd=ROOT, env=env, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
            assert result.returncode != 0 and name in result.stdout, f'{gate} accepted relocated {name}:\n{result.stdout}'
            print(f'{gate} rejected relocated {name}', flush=True)
        finally:
            fixture.unlink(missing_ok=True)
