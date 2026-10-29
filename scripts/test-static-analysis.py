#!/usr/bin/env python3
"""Integration control: both real Gradle analyzers must reject a new Kotlin violation.

Run without another Gradle invocation in this checkout. Creates one uniquely named
probe in an existing source root, never edits existing sources or baselines, and
removes the probe in finally. This is opt-in; ordinary checks do not inject code.
"""
from pathlib import Path
import subprocess
import tempfile
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parent.parent
PROBE = ROOT / 'core/domain/src/test/kotlin/QualityGateStaticRegressionProbe.kt'


def main():
    if PROBE.exists():
        raise RuntimeError(f'Refusing to overwrite {PROBE}')
    PROBE.parent.mkdir(parents=True, exist_ok=True)
    baselines = [ROOT / 'config/detekt/baseline.xml', ROOT / 'config/ktlint/baseline.xml']
    original = {path: path.read_bytes() for path in baselines}
    try:
        PROBE.write_text('package qualitygateprobe\n\nimport java.util.*\n\n'
                         'fun qualityGateStaticProbe( unused: String ) = Unit\n')
        with tempfile.TemporaryDirectory(prefix='simlo-static-control-') as directory:
            for task, report, required in (
                ('detekt', 'detekt/detekt.xml', 'WildcardImport'),
                ('ktlintCheck', 'ktlint/ktlint.xml', 'standard:parameter-list-spacing'),
            ):
                log = Path(directory) / f'{task}.log'
                with log.open('w') as output:
                    result = subprocess.run([str(ROOT / 'gradlew'), task, '--console=plain'],
                                            cwd=ROOT, stdout=output, stderr=subprocess.STDOUT)
                if result.returncode == 0:
                    raise AssertionError(f'{task} accepted new violations: {log.read_text()}')
                xml = ET.parse(ROOT / 'build/reports' / report).getroot()
                findings = [error for file in xml.findall('file') if PROBE.name in file.get('name', '')
                            for error in file.findall('error')]
                if not any(required in error.get('source', '') for error in findings):
                    raise AssertionError(f'{task} failed without detecting probe {required}: {log.read_text()}')
                print(f'PASS: {task} rejected new {required} in a file absent from its baseline.')
        if any(path.read_bytes() != data for path, data in original.items()):
            raise AssertionError('A validation task changed its baseline')
    finally:
        PROBE.unlink(missing_ok=True)
    print('PASS: probe removed; reviewed baselines unchanged.')


if __name__ == '__main__':
    main()
