#!/usr/bin/env python3
"""Opt-in integration check of real Gradle architecture gates; restores every injected fixture."""
from contextlib import contextmanager
from pathlib import Path
import argparse
import json
import os
import subprocess


@contextmanager
def fixture(path: Path, content: str, append: bool = False):
    original = path.read_bytes() if path.exists() else None
    path.parent.mkdir(parents=True, exist_ok=True)
    try:
        path.write_bytes((original if append and original else b'') + content.encode())
        yield
    finally:
        if original is None:
            path.unlink(missing_ok=True)
        else:
            path.write_bytes(original)
        assert (path.read_bytes() if path.exists() else None) == original


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--output', type=Path, required=True)
    args = parser.parse_args()
    root = Path(__file__).resolve().parent.parent
    args.output.mkdir(parents=True, exist_ok=True)
    domain = 'feature/support/domain/src/main/kotlin/br/gov/caixa/loterias/feature/support/domain/ArchitectureFixture20261019.kt'
    presentation = 'feature/support/presentation/src/main/java/br/gov/caixa/loterias/feature/support/presentation/components/ArchitectureFixture20261019.kt'
    tests = [
        ('domain-android', domain, 'package br.gov.caixa.loterias.feature.support.domain\nimport android.content.Context\nclass ArchitectureFixture20261019(val context: Context)\n', False, 'verifyCleanArchitecture', 'Domain'),
        ('domain-data-dependency', 'feature/support/domain/build.gradle', "\ndependencies { implementation project(':core:network') }\n", True, 'verifyFeatureModules', 'Domain dependency on outer layer'),
        ('foreign-source', 'feature/support/presentation/build.gradle', "\nandroid.sourceSets.main.java.srcDir(rootProject.file('core/common/src/main/java'))\n", True, 'verifyFeatureModules', 'fontes externas'),
        ('duplicate-source', 'feature/account/presentation/build.gradle', "\nandroid.sourceSets.main.java.srcDir('../data/src/main/kotlin')\n", True, 'verifyFeatureModules', 'fontes externas'),
        ('cycle', 'feature/cart/api/build.gradle', "\ndependencies { api project(':feature:betting:domain') }\n", True, 'verifyFeatureModules', 'Module dependency cycle'),
        ('app-dependency', 'feature/marketplace-filter/presentation/build.gradle', "\ndependencies { implementation project(':app') }\n", True, 'verifyFeatureModules', 'production dependency on application'),
        ('shared-navigation-color', 'core/navigation/src/main/java/br/gov/caixa/loterias/core/navigation/ArchitectureFixture20261019.kt', 'package br.gov.caixa.loterias.core.navigation\nfun unwantedColor() = androidx.compose.ui.graphics.Color(0xFF112233)\n', False, 'verifyCaixaDS', 'Cores literais'),
        ('feature-controller', presentation, 'package br.gov.caixa.loterias.feature.support.presentation.components\nclass ArchitectureFixtureActivity\n', False, 'verifyComposeMigration', 'Activities/Fragments de feature'),
        ('nested-java', 'feature/cart/transport/src/main/java/ArchitectureFixture20261019.java', 'class ArchitectureFixture20261019 {}\n', False, 'verifyComposeMigration', 'usar Kotlin'),
        ('xml-layout', 'feature/home/presentation/src/main/res/layout/architecture_fixture_20261019.xml', '<LinearLayout/>\n', False, 'verifyComposeMigration', 'XML não permitido'),
    ]
    results = []
    for name, relative, text, append, task, expected in tests:
        log = args.output / (name + '.log')
        with fixture(root / relative, text, append):
            with log.open('w') as stream:
                result = subprocess.run(['./gradlew', task, '--offline', '--console=plain'], cwd=root,
                                        env=os.environ.copy(), stdout=stream, stderr=subprocess.STDOUT)
            output = log.read_text()
            assert result.returncode != 0 and expected in output, (name, result.returncode, log)
        results.append({'fixture': name, 'gate': task, 'exit': result.returncode, 'diagnostic': expected, 'restored': True})
        print(name + ': rejected and restored', flush=True)
    with (args.output / 'restored-baseline.log').open('w') as stream:
        result = subprocess.run(['./gradlew', 'verifyCleanArchitecture', 'verifyCaixaDS', 'verifyComposeMigration', '--offline', '--console=plain'], cwd=root,
                                env=os.environ.copy(), stdout=stream, stderr=subprocess.STDOUT)
    assert result.returncode == 0, 'Restored baseline did not pass; inspect restored-baseline.log'
    (args.output / 'results.json').write_text(json.dumps(results, indent=2) + '\n')
    print('All ten real gate fixtures rejected; restored baseline passed.', flush=True)


if __name__ == '__main__':
    main()
