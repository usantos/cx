#!/usr/bin/env python3
"""Reuse explicitly selected behavioral UI suites as local Robolectric tests.

O runner, a configuração e o import da captura local são adaptados. Asserções e
corpos dos testes permanecem intactos; fontes em dispositivo continuam independentes.
"""
import argparse
from pathlib import Path
import re
import shutil


def prepare(root, module, manifest, destination):
    sources = []
    prefix = f'{module}/src/androidTest/'
    for line in manifest.read_text().splitlines():
        entry = line.strip()
        helper = entry.startswith('helper:')
        if helper:
            entry = entry.removeprefix('helper:')
        if not entry or entry.startswith('#') or not entry.startswith(prefix):
            continue
        source = (root / entry).resolve()
        if not source.is_relative_to((root / module / 'src/androidTest').resolve()):
            raise ValueError(f'UI suite outside module: {entry}')
        if not source.is_file():
            raise ValueError(f'Missing UI suite: {entry}')
        sources.append((source, helper))
    if destination.exists():
        shutil.rmtree(destination)
    destination.mkdir(parents=True)
    for source, helper in sources:
        content = source.read_text()
        content = re.sub(r'^import androidx\.compose\.ui\.test\.captureToImage(?=\s*$)',
                         'import br.gov.caixa.loterias.core.designsystem.testing.captureToImage',
                         content, flags=re.MULTILINE)
        if (re.search(r'^import androidx\.compose\.ui\.test\.\*\s*$', content, re.MULTILINE)
                and re.search(r'\.\s*captureToImage\s*\(', content)
                and 'import br.gov.caixa.loterias.core.designsystem.testing.captureToImage' not in content):
            content = re.sub(r'^(import androidx\.compose\.ui\.test\.\*)',
                             r'\1\nimport br.gov.caixa.loterias.core.designsystem.testing.captureToImage',
                             content, count=1, flags=re.MULTILINE)
        package = re.search(r'^package ([\w.]+)\s*$', content, re.MULTILINE)
        if package is None:
            raise ValueError(f'Missing package in {source}')
        if helper:
            output = destination / package.group(1).replace('.', '/') / source.name
            output.parent.mkdir(parents=True, exist_ok=True)
            output.write_text(content)
            continue
        runner = ('org.robolectric.ParameterizedRobolectricTestRunner'
                  if '@RunWith(Parameterized::class)' in content
                  else 'org.robolectric.RobolectricTestRunner')
        if runner.endswith('ParameterizedRobolectricTestRunner'):
            content = content.replace('@Parameterized.Parameters',
                                      '@org.robolectric.ParameterizedRobolectricTestRunner.Parameters')
        content = re.sub(r'^import org.junit.runner.RunWith\s*\n', '', content, flags=re.MULTILINE)
        content = re.sub(r'^@RunWith\([^\n]+\)\s*\n', '', content, flags=re.MULTILINE)
        annotation = (
            f'@org.junit.runner.RunWith({runner}::class)\n'
            '@org.robolectric.annotation.Config(sdk = [35], application = android.app.Application::class, '
            'qualifiers = "w411dp-h891dp-xhdpi")\n'
            '@org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)\n'
        )
        content, count = re.subn(r'^(class \w+Test\b)', lambda m: annotation + m.group(1),
                                 content, count=1, flags=re.MULTILINE)
        if count != 1:
            raise ValueError(f'Expected one top-level test class in {source}')
        output = destination / package.group(1).replace('.', '/') / source.name
        output.parent.mkdir(parents=True, exist_ok=True)
        output.write_text(content)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--root', required=True, type=Path)
    parser.add_argument('--module', required=True)
    parser.add_argument('--manifest', required=True, type=Path)
    parser.add_argument('--output', required=True, type=Path)
    arguments = parser.parse_args()
    prepare(arguments.root, arguments.module, arguments.manifest, arguments.output)
