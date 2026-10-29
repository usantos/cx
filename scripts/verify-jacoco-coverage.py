#!/usr/bin/env python3
"""Fail closed on JaCoCo's actual, weighted production LINE counters (>95%)."""
import argparse
import json
from decimal import Decimal
from pathlib import Path
import sys
import xml.etree.ElementTree as ET

MINIMUM = Decimal('95')


def line_counts(path, allow_empty_module=False):
    root = ET.parse(path).getroot()
    if root.tag != 'report':
        raise ValueError(f'{path}: expected a JaCoCo report')
    counters = [c for c in root.findall('counter') if c.get('type') == 'LINE']
    if not counters and allow_empty_module:
        # Bytecode apenas de classes abstratas/interfaces pode legitimamente não ter linhas executáveis.
        instructions = [c for c in root.findall('counter') if c.get('type') == 'INSTRUCTION']
        if not instructions or all(int(c.get('missed', '-1')) == 0 and int(c.get('covered', '-1')) == 0 for c in instructions):
            return 0, 0
    if len(counters) != 1:
        raise ValueError(f'{path}: missing or ambiguous root LINE counter')
    missed, covered = (int(counters[0].get(key, '-1')) for key in ('missed', 'covered'))
    if min(missed, covered) < 0:
        raise ValueError(f'{path}: invalid LINE counters')
    return missed, covered


def percentage(missed, covered):
    total = missed + covered
    return Decimal(covered) * 100 / total if total else None


def verify(aggregate, modules, output=print, summary_path=None):
    missed, covered = line_counts(aggregate)
    module_totals = [0, 0]
    module_results = []
    output('JaCoCo production LINE coverage by module (unit tests):')
    for name, path in modules:
        module_missed, module_covered = line_counts(path, allow_empty_module=True)
        module_totals[0] += module_missed
        module_totals[1] += module_covered
        ratio = percentage(module_missed, module_covered)
        module_results.append({'module': name, 'missed': module_missed, 'covered': module_covered,
                               'eligible': module_missed + module_covered,
                               'percentage': str(ratio) if ratio is not None else None})
        label = f'{ratio:.2f}%' if ratio is not None else 'N/A (no eligible lines)'
        output(f'  {name}: {label} ({module_covered}/{module_missed + module_covered}; missed={module_missed})')
    if modules and tuple(module_totals) != (missed, covered):
        raise ValueError('Aggregate LINE counters differ from the module sum; check duplicate classes/source files')
    ratio = percentage(missed, covered)
    if ratio is None:
        raise ValueError('No eligible production lines; coverage cannot be validated')
    output(f'JaCoCo Line Coverage: {ratio:.2f}% ({covered}/{missed + covered}; missed={missed})')
    output(f'Minimum Required: {MINIMUM:.2f}% (strictly greater)')
    # Compara contagens inteiras exatas: a exibição arredondada nunca deve admitir 94.999...%.
    passed = covered * 100 > (missed + covered) * 95
    if summary_path is not None:
        summary_path.parent.mkdir(parents=True, exist_ok=True)
        summary_path.write_text(json.dumps({
            'passed': passed, 'percentage': f'{ratio:.2f}', 'minimum': str(MINIMUM),
            'covered': covered, 'missed': missed, 'eligible': covered + missed,
            'modules': module_results,
        }, indent=2) + '\n')
    output('COVERAGE PASSED' if passed else 'COVERAGE FAILED: production LINE coverage must be greater than 95%')
    return passed


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--aggregate', required=True, type=Path)
    parser.add_argument('--summary', type=Path, help='Write current measured coverage JSON, including below-threshold results')
    parser.add_argument('--module', action='append', default=[], metavar='NAME=XML')
    args = parser.parse_args(argv)
    try:
        modules = [tuple(item.split('=', 1)) for item in args.module]
        if any(len(item) != 2 for item in modules):
            raise ValueError('--module requires NAME=XML')
        if len({name for name, _ in modules}) != len(modules):
            raise ValueError('Duplicate module reports')
        return 0 if verify(args.aggregate, modules, summary_path=args.summary) else 1
    except (OSError, ET.ParseError, ValueError) as error:
        print(f'COVERAGE FAILED: {error}', file=sys.stderr)
        return 2


if __name__ == '__main__':
    sys.exit(main())
