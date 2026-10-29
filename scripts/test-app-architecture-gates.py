#!/usr/bin/env python3
"""Opt-in real Gradle host gate checks; restores each fixture byte for byte."""
from contextlib import ExitStack, contextmanager
from pathlib import Path
import argparse
import json
import os
import subprocess


@contextmanager
def fixture(path, content, append=False, allow_existing=False):
    original = path.read_bytes() if path.exists() else None
    if original is not None and not append and not allow_existing:
        raise FileExistsError(f"Refusing to replace an existing source fixture: {path}")
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
    parser.add_argument('--fixture', action='append', help='Run only the named fixture (repeatable).')
    args = parser.parse_args()
    root = Path(__file__).resolve().parent.parent
    args.output.mkdir(parents=True, exist_ok=True)
    base = 'br.gov.caixa.loterias.apostas'
    folder = 'app/src/main/java/' + base.replace('.', '/')
    tests = [
        ('app-data-layer', folder + '/data/probe/ArchitectureFixture20261019.kt', base + '.data.probe', 'class Unexpected', 'layer owner', False),
        ('core-app-import', 'core/security/src/main/java/br/gov/caixa/loterias/core/security/ArchitectureFixture20261019.kt', 'br.gov.caixa.loterias.core.security', 'import br.gov.caixa.loterias.apostas.BuildConfig\nclass Unexpected', 'cannot depend on APP implementation', False),
        ('core-feature-import', 'core/security/src/main/java/br/gov/caixa/loterias/core/security/ArchitectureFixture20261019.kt', 'br.gov.caixa.loterias.core.security', 'import br.gov.caixa.loterias.feature.home.presentation.screen.HomeScreen', 'cannot depend on feature implementation', False),
        ('core-vm-repository', 'core/presentation/src/main/java/br/gov/caixa/loterias/core/presentation/ProbeViewModel.kt', 'br.gov.caixa.loterias.core.presentation', 'import br.gov.caixa.loterias.core.domain.configuration.AppConfigurationRepository\nclass ProbeViewModel', 'ViewModel must consume domain operations', False),
        ('flat-host', folder + '/ArchitectureFixture20261019.kt', base, 'class Unexpected', 'layer owner', False),
        ('domain-android', folder + '/domain/probe/ArchitectureFixture20261019.kt', base + '.domain.probe', 'import android.content.Context\nclass Unexpected(val context: Context)', 'Domain imports outer layer', False),
        ('vm-navigation', folder + '/presentation/probe/ProbeViewModel.kt', base + '.presentation.probe', 'import androidx.navigation.NavController\nclass ProbeViewModel', 'presentation imports concrete/platform dependency', False),
        ('vm-mutable-flow', folder + '/presentation/probe/ProbeViewModel.kt', base + '.presentation.probe', 'class ProbeViewModel { val state = MutableStateFlow(false) }', 'MutableStateFlow must be private', False),
        ('ui-repository', folder + '/presentation/probe/ArchitectureFixture20261019.kt', base + '.presentation.probe', 'import br.gov.caixa.loterias.core.domain.configuration.AppConfigurationRepository', 'UI must dispatch business work', False),
        ('composition-coroutine', folder + '/composition/probe/ArchitectureFixture20261019.kt', base + '.composition.probe', '@Composable fun route() { rememberCoroutineScope().launch { verify() } }', 'host UI wiring must dispatch', False),
        ('compatibility-provider', folder + '/di/ArchitectureProviderFixture20261020.kt', base + '.di', 'object ObsoleteBindings { @Provides fun duplicate() = 1 }', 'compatibility DI must not duplicate', False),
        ('retired-di-facade', folder + '/di/ArchitectureFacadeFixture20261020.kt', base + '.di', 'object NetworkModule', 'retired host facade', False),
        ('retired-root-facade', folder + '/ArchitectureFacadeFixture20261020.kt', base, 'fun LoteriasApp() = Unit', 'retired host facade', False),
        ('retired-inventory-facade', folder + '/di/ArchitectureInventoryFixture20261020.kt', base + '.di', 'object ObsoleteBindings', 'only enumerate Android entry points', False),
        ('retired-core-package', 'core/network/src/main/java/br/gov/caixa/loterias/apostas/ArchitectureFixture20261020.kt', base, 'class Unexpected', 'CORE must use its owning core package', False),
        ('wrong-core-owner', 'core/network/src/main/java/br/gov/caixa/loterias/core/data/ArchitectureFixture20261020.kt', 'br.gov.caixa.loterias.core.data', 'class Unexpected', 'CORE must use its owning core package', False),
        ('deprecated-core-api', 'core/common/src/main/java/br/gov/caixa/loterias/core/common/ArchitectureFixture20261020.kt', 'br.gov.caixa.loterias.core.common', '@Deprecated("Use current API")\nclass Unexpected', 'CORE source cannot declare deprecated APIs', False),
        ('release-flat-host', folder.replace('/main/', '/release/') + '/ArchitectureFixture20261019.kt', base, 'class Unexpected', 'layer owner', False),
    ]
    if args.fixture:
        unknown = set(args.fixture) - {case[0] for case in tests}
        if unknown:
            parser.error('Unknown fixtures: ' + ', '.join(sorted(unknown)))
        tests = [case for case in tests if case[0] in args.fixture]
    results = []
    for name, relative, package, body, expected, append in tests:
        text = ('package ' + package + '\n' if package else '') + body + '\n'
        log = args.output / (name + '.log')
        with ExitStack() as stack:
            if name in ('compatibility-provider', 'retired-inventory-facade'):
                inventory_path = root / 'config/app-compatibility.json'
                inventory = json.loads(inventory_path.read_text())
                inventory[relative] = {'kind': 'di-facade'}
                stack.enter_context(fixture(inventory_path, json.dumps(inventory, indent=2) + '\n', allow_existing=True))
            stack.enter_context(fixture(root / relative, text, append))
            with log.open('w') as stream:
                result = subprocess.run(['./gradlew', 'verifyCleanArchitecture', '--offline', '--console=plain'], cwd=root,
                                        env=os.environ.copy(), stdout=stream, stderr=subprocess.STDOUT)
            assert result.returncode != 0 and expected in log.read_text(), (name, result.returncode, str(log))
        results.append({'fixture': name, 'exit': result.returncode, 'diagnostic': expected, 'restored': True})
        print(name + ': rejected and restored', flush=True)
    with (args.output / 'restored-baseline.log').open('w') as stream:
        result = subprocess.run(['./gradlew', 'verifyCleanArchitecture', 'verifyComposeMigration', 'verifyKotlinFormatting', '--offline', '--console=plain'],
                                cwd=root, env=os.environ.copy(), stdout=stream, stderr=subprocess.STDOUT)
    assert result.returncode == 0, 'Restored baseline did not pass'
    (args.output / 'results.json').write_text(json.dumps(results, indent=2) + '\n')
    print(f'All {len(results)} host/CORE fixtures rejected; restored baseline passed.', flush=True)


if __name__ == '__main__':
    main()
