#!/usr/bin/env python3
"""Exercise the real DS ownership gate with temporary consumers; always restore source files."""
from contextlib import contextmanager
from pathlib import Path
import argparse
import json
import os
import subprocess


@contextmanager
def fixture(path, text):
    previous = path.read_bytes() if path.exists() else None
    path.parent.mkdir(parents=True, exist_ok=True)
    try:
        path.write_text(text)
        yield
    finally:
        if previous is None:
            path.unlink(missing_ok=True)
        else:
            path.write_bytes(previous)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--output', required=True, type=Path)
    args = parser.parse_args()
    args.output.mkdir(parents=True, exist_ok=True)
    root = Path(__file__).resolve().parent.parent
    location = root / 'feature/support/presentation/src/main/java/br/gov/caixa/loterias/feature/support/presentation/fixture/DesignSystemOwnershipFixture20261022.kt'
    prefix = 'package br.gov.caixa.loterias.feature.support.presentation.fixture\n'
    cases = [
        ('material-import', prefix + 'import androidx.compose.material3.Text\n@androidx.compose.runtime.Composable fun OwnershipFixture() { Text("Fixture") }\n', False, 'Controles básicos'),
        ('material-qualified', prefix + '@androidx.compose.runtime.Composable fun OwnershipFixture() { androidx.compose.material3.Text("Fixture") }\n', False, 'Controles básicos'),
        ('foundation-import', prefix + 'import androidx.compose.foundation.text.BasicTextField\n@androidx.compose.runtime.Composable fun OwnershipFixture() { BasicTextField("Fixture", {}) }\n', False, 'Controles básicos'),
        ('foundation-alias', prefix + 'import androidx.compose.foundation.text.BasicTextField as Input\n@androidx.compose.runtime.Composable fun OwnershipFixture() { Input("Fixture", {}) }\n', False, 'Controles básicos'),
        ('foundation-wildcard', prefix + 'import androidx.compose.foundation.text.*\n@androidx.compose.runtime.Composable fun OwnershipFixture() { BasicTextField("Fixture", {}) }\n', False, 'Controles básicos'),
        ('foundation-qualified', prefix + '@androidx.compose.runtime.Composable fun OwnershipFixture() { androidx.compose.foundation.text.BasicTextField("Fixture", {}) }\n', False, 'Controles básicos'),
        ('native-webview', prefix + 'class OwnershipFixture(val view: android.webkit.WebView)\n', False, 'WebViews'),
        ('android-view', prefix + 'import androidx.compose.ui.viewinterop.AndroidView\n', False, 'WebViews'),
        ('owned-controls', prefix + 'import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText\n@androidx.compose.runtime.Composable fun OwnershipFixture() { CaixaDSText("Fixture") }\n', True, 'BUILD SUCCESSFUL'),
        ('owned-basic-text-field', prefix + 'import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField as BasicTextField\n@androidx.compose.runtime.Composable fun OwnershipFixture() { BasicTextField("Fixture", {}) }\n', True, 'BUILD SUCCESSFUL'),
    ]
    results = []
    for name, source, allowed, diagnostic in cases:
        log = args.output / (name + '.log')
        with fixture(location, source), log.open('w') as output:
            result = subprocess.run(['./gradlew', 'verifyCaixaDS', '--offline', '--console=plain'], cwd=root, env=os.environ.copy(), stdout=output, stderr=subprocess.STDOUT)
        text = log.read_text()
        assert (result.returncode == 0) == allowed and diagnostic in text, (name, result.returncode, log)
        results.append(dict(case=name,allowed=allowed,exit=result.returncode,restored=True))
        print(name + ': ' + ('accepted' if allowed else 'rejected') + ', fixture restored', flush=True)
    with (args.output / 'restored-baseline.log').open('w') as output:
        result = subprocess.run(['./gradlew', 'verifyCaixaDS', '--offline', '--console=plain'], cwd=root, env=os.environ.copy(), stdout=output, stderr=subprocess.STDOUT)
    assert result.returncode == 0, 'Restored baseline failed'
    (args.output / 'results.json').write_text(json.dumps(results, indent=2) + '\n')
    print('Baseline restored and accepted.', flush=True)


if __name__ == '__main__':
    main()
