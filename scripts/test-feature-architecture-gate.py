#!/usr/bin/env python3
"""Exercise real feature gates; run serially and restore every source/config byte."""
from contextlib import contextmanager, ExitStack
from pathlib import Path
import argparse
import json
import subprocess


@contextmanager
def fixture(path, content):
    original = path.read_bytes() if path.exists() else None
    path.parent.mkdir(parents=True, exist_ok=True)
    try:
        path.write_text(content)
        yield
    finally:
        if original is None:
            path.unlink(missing_ok=True)
        else:
            path.write_bytes(original)
        assert (path.read_bytes() if path.exists() else None) == original


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--output', type=Path, default=Path('/tmp/features-cleanup-negative-gates-20261020.log'))
    args = parser.parse_args()
    root = Path(__file__).resolve().parent.parent
    base = 'br.gov.caixa.loterias.feature.support'
    folder = 'feature/support/domain/src/main/kotlin/' + base.replace('.', '/') + '/domain/'
    legacy = 'feature/support/presentation/src/main/kotlin/br/gov/caixa/loterias/apostas/RetiredFeatureFixture20261020.kt'
    cases = [
        (folder + 'FrameworkFixture20261020.kt', f'package {base}.domain\nimport android.content.Context\nclass FrameworkFixture20261020', 'Domain depende de camada externa'),
        (folder + 'AliasFixture20261020.kt', f'package {base}.domain\ntypealias AliasFixture20261020 = {base}.domain.SupportSection', 'public compatibility aliases'),
        (folder + 'DeprecatedFixture20261020.kt', f'package {base}.domain\n@Deprecated("Use current API")\nclass DeprecatedFixture20261020', 'deprecated APIs'),
        (legacy, 'package br.gov.caixa.loterias.apostas\nclass RetiredFeatureFixture20261020', 'retired APP namespace'),
        (folder + 'UseCaseFixture20261020.kt', f'package {base}.domain\nclass UseCaseFixture20261020 {{ private val implementation = {base}.domain.usecase.UseCaseFixture20261020() }}', 'use-case compatibility facade'),
    ]
    cases.extend([
        (folder + 'policy/ObjectAliasFixture20261020.kt', f'package {base}.domain.policy\nval SupportRules get() = {base}.domain.SupportRules', 'public object compatibility alias'),
        ('feature/support/presentation/src/main/kotlin/' + base.replace('.', '/') + '/presentation/host/route/HostFixture20261020.kt', f'package {base}.presentation.host.route\nclass HostFixture20261020', 'retired presentation host namespace'),
    ])
    logs = []
    for index, (relative, content, diagnostic) in enumerate(cases, 1):
        with ExitStack() as stack:
            path = root / relative
            if path.exists():
                raise RuntimeError('Refusing to overwrite source fixture: ' + relative)
            stack.enter_context(fixture(path, content + '\n'))
            if relative == legacy:
                inventory_path = root / 'config/host-public-compatibility.json'
                inventory = json.loads(inventory_path.read_text())
                inventory[legacy] = {'package': 'br.gov.caixa.loterias.apostas', 'reason': 'Negative gate fixture only.'}
                stack.enter_context(fixture(inventory_path, json.dumps(inventory, indent=2) + '\n'))
            args.output.parent.mkdir(parents=True, exist_ok=True)
            case_log = args.output.with_name(args.output.stem + f'-{index}' + args.output.suffix)
            with case_log.open('w') as stream:
                result = subprocess.run([str(root / 'gradlew'), 'verifyCleanArchitecture', '--offline', '--console=plain'], cwd=root, stdout=stream, stderr=subprocess.STDOUT)
            output = case_log.read_text()
            logs.append(output)
            assert result.returncode != 0 and relative in output and diagnostic in output, (relative, diagnostic, str(case_log))
        print(f'{index}/{len(cases)}: rejected and restored {Path(relative).name}', flush=True)
    args.output.write_text('\n'.join(logs))
    print(f'All {len(cases)} feature violations rejected by real Gradle gate; sources/inventory restored byte for byte.')


if __name__ == '__main__':
    main()
