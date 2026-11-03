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
    # O manifesto usa caminhos Git com '/', inclusive quando o Gradle executa no Windows.
    module = module.replace('\\', '/')
    sources = []
    # O parser Java de recursos não suporta o pacote 0x80 dos splits; o parser nativo do SDK 36 preserva os recursos reais.
    native_resources = module == "app" or (module.startswith("feature/") and module.endswith("/presentation"))
    for name in ['build.gradle', 'build.gradle.kts']:
        build = root / module / name
        if build.is_file():
            declaration = re.sub(r'/\*[\s\S]*?\*/|//[^\n]*', '', build.read_text())
            native_resources |= bool(re.search(r"['\"]com\.android\.dynamic-feature['\"]", declaration))
    sdk = 36 if native_resources else 35
    prefix = f'{module}/src/androidTest/'
    for line in manifest.read_text().splitlines():
        entry = line.strip().replace('\\', '/')
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
        parameterized = bool(re.search(
            r'@(?:org\.junit\.runner\.)?RunWith\(\s*(?:org\.junit\.runners\.)?Parameterized::class\s*\)', content))
        runner = ('org.robolectric.ParameterizedRobolectricTestRunner'
                  if '@RunWith(Parameterized::class)' in content
                  else 'org.robolectric.RobolectricTestRunner')
        if native_resources:
            runner = ('br.gov.caixa.loterias.core.testing.runner.DynamicFeatureParameterizedRobolectricTestRunner'
                      if parameterized else 'br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner')
            host = 'br.gov.caixa.loterias.apostas.HomeDrawerTestActivity'
            rule_declaration = r'^([ \t]*@get:Rule[ \t]*(?:\n[ \t]*)?val\s+\w+\s*=\s*)'
            content, compose_rule_count = re.subn(
                rule_declaration + r'createComposeRule\(\)',
                lambda match: match[1] + f'createAndroidComposeRule<{host}>()', content, flags=re.MULTILINE)
            if compose_rule_count:
                preserve_compose_import = bool(re.search(r'\bcreateComposeRule\s*\(', content))
                content = re.sub(r'^import (androidx\.compose\.ui\.test\.junit4(?:\.v2)?)\.createComposeRule[ \t]*$',
                                 lambda match: (match[0] + '\n' if preserve_compose_import else '') +
                                 f'import {match[1]}.createAndroidComposeRule', content, flags=re.MULTILINE)
            content = re.sub(
                rule_declaration + r'createAndroidComposeRule<(?:androidx\.activity\.)?ComponentActivity>\(\)',
                lambda match: match[1] + f'createAndroidComposeRule<{host}>()', content, flags=re.MULTILINE)
        if runner == 'org.robolectric.ParameterizedRobolectricTestRunner':
            content = content.replace('@Parameterized.Parameters',
                                      '@org.robolectric.ParameterizedRobolectricTestRunner.Parameters')
        content = re.sub(r'^import org.junit.runner.RunWith\s*\n', '', content, flags=re.MULTILINE)
        content = re.sub(r'^@RunWith\([^\n]+\)\s*\n', '', content, flags=re.MULTILINE)
        annotation = (
            f'@org.junit.runner.RunWith({runner}::class)\n'
            f'@org.robolectric.annotation.Config(sdk = [{sdk}], application = android.app.Application::class, '
            'qualifiers = "w411dp-h891dp-xhdpi")\n'
            '@org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)\n'
        )
        if native_resources:
            annotation += '@org.robolectric.annotation.ResourcesMode(org.robolectric.annotation.ResourcesMode.Mode.NATIVE)\n'
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
