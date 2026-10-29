#!/usr/bin/env python3
"""Exercise the real command used by verifyCoverage, including failure exits."""
import importlib.util
import json
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest

CHECKER = Path(__file__).with_name('verify-jacoco-coverage.py')
spec = importlib.util.spec_from_file_location('coverage_checker', CHECKER)
checker = importlib.util.module_from_spec(spec)
spec.loader.exec_module(checker)


class CoverageGateTest(unittest.TestCase):
    def setUp(self):
        self.directory = tempfile.TemporaryDirectory()
        self.addCleanup(self.directory.cleanup)

    def report(self, name, missed, covered):
        path = Path(self.directory.name, name + '.xml')
        path.write_text(f'<report name="fixture"><counter type="LINE" missed="{missed}" covered="{covered}"/></report>')
        return path

    def run_gate(self, report, *modules):
        args = [sys.executable, str(CHECKER), '--aggregate', str(report)]
        for name, path in modules:
            args += ['--module', f'{name}={path}']
        return subprocess.run(args, text=True, capture_output=True, check=False)

    def test_below_95_blocks(self):
        result = self.run_gate(self.report('aggregate', 7, 93))
        self.assertEqual(result.returncode, 1)
        self.assertIn('93.00%', result.stdout)
        self.assertIn('Minimum Required: 95.00%', result.stdout)

    def test_failure_emits_real_coverage_summary(self):
        summary = Path(self.directory.name, 'summary.json')
        result = subprocess.run([sys.executable, str(CHECKER), '--aggregate',
                                 str(self.report('aggregate', 7, 93)), '--summary', str(summary)],
                                text=True, capture_output=True)
        self.assertEqual(result.returncode, 1)
        data = json.loads(summary.read_text())
        self.assertFalse(data['passed'])
        self.assertEqual(data['percentage'], '93.00')
        self.assertEqual(data['eligible'], 100)

    def test_exact_95_blocks(self):
        self.assertEqual(self.run_gate(self.report('aggregate', 5, 95)).returncode, 1)

    def test_strictly_above_95_passes(self):
        self.assertEqual(self.run_gate(self.report('aggregate', 4999, 95001)).returncode, 0)

    def test_display_rounding_cannot_pass(self):
        result = self.run_gate(self.report('aggregate', 5001, 94999))
        self.assertEqual(result.returncode, 1)
        self.assertIn('95.00%', result.stdout)

    def test_weighted_total_not_module_average(self):
        small = self.report('small', 0, 1)
        large = self.report('large', 60, 940)
        result = self.run_gate(self.report('aggregate', 60, 941), (':small', small), (':large', large))
        self.assertEqual(result.returncode, 1)
        self.assertIn(':large: 94.00%', result.stdout)

    def test_no_test_module_remains_missed(self):
        result = self.run_gate(self.report('aggregate', 10, 95),
                               (':tested', self.report('tested', 0, 95)),
                               (':noTests', self.report('noTests', 10, 0)))
        self.assertEqual(result.returncode, 1)
        self.assertIn(':noTests: 0.00%', result.stdout)

    def test_interface_only_module_has_no_eligible_lines(self):
        module = Path(self.directory.name, 'interfaces.xml')
        module.write_text('<report><counter type="CLASS" missed="1" covered="0"/></report>')
        result = self.run_gate(self.report('aggregate', 4, 96),
                               (':impl', self.report('impl', 4, 96)), (':interfaces', module))
        self.assertEqual(result.returncode, 0)
        self.assertIn(':interfaces: N/A', result.stdout)

    def test_module_with_instructions_but_no_lines_fails_closed(self):
        module = Path(self.directory.name, 'noDebugInfo.xml')
        module.write_text('<report><counter type="INSTRUCTION" missed="1" covered="0"/></report>')
        self.assertEqual(self.run_gate(self.report('aggregate', 5, 95), (':invalid', module)).returncode, 2)

    def test_missing_report_fails_closed(self):
        self.assertEqual(self.run_gate(Path(self.directory.name, 'absent.xml')).returncode, 2)

    def test_empty_denominator_fails_closed(self):
        self.assertEqual(self.run_gate(self.report('aggregate', 0, 0)).returncode, 2)

    def test_module_mismatch_fails_closed(self):
        self.assertEqual(self.run_gate(self.report('aggregate', 5, 95),
                                      (':missing', self.report('module', 0, 95))).returncode, 2)

    def test_missing_line_counter_fails_closed(self):
        path = Path(self.directory.name, 'invalid.xml')
        path.write_text('<report><counter type="INSTRUCTION" missed="0" covered="100"/></report>')
        self.assertEqual(self.run_gate(path).returncode, 2)


if __name__ == '__main__':
    unittest.main()
