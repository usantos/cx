"""Runner adaptation must preserve test assertions and remain inside its module."""
import importlib.util
from pathlib import Path
import tempfile
import unittest

spec = importlib.util.spec_from_file_location('ui_adapter', Path(__file__).with_name('prepare-local-ui-tests.py'))
adapter = importlib.util.module_from_spec(spec)
spec.loader.exec_module(adapter)


class LocalUiAdapterTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.module = 'feature/example'
        self.manifest = self.root / 'suites.txt'
        self.destination = self.root / 'generated'

    def source(self, content, name='ExampleTest.kt'):
        path = self.root / self.module / 'src/androidTest/java/sample' / name
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(content)
        return path

    def run_adapter(self, entries):
        self.manifest.write_text('\n'.join(entries))
        adapter.prepare(self.root, self.module, self.manifest, self.destination)

    def test_assertions_preserved_and_original_untouched(self):
        body = 'class ExampleTest {\n    @Test fun check() { assertEquals(7, actual()) }\n}\n'
        original = 'package sample\nimport org.junit.runner.RunWith\n@RunWith(AndroidJUnit4::class)\n' + body
        path = self.source(original)
        self.run_adapter([str(path.relative_to(self.root))])
        generated = (self.destination / 'sample/ExampleTest.kt').read_text()
        self.assertEqual(path.read_text(), original)
        self.assertTrue(generated.endswith(body))
        self.assertIn('RobolectricTestRunner::class', generated)
        self.assertNotIn('@RunWith(AndroidJUnit4', generated)
        self.assertIn('Mode.NATIVE', generated)

    def test_local_capture_import_changes_without_altering_assertions_or_device(self):
        body = 'class ExampleTest {\n @Test fun pixels() = assertEquals(1, node.captureToImage().width)\n}\n'
        original = 'package sample\nimport androidx.compose.ui.test.captureToImage\n' + body
        path = self.source(original)
        self.run_adapter([str(path.relative_to(self.root))])
        generated = (self.destination / 'sample/ExampleTest.kt').read_text()
        self.assertIn('import br.gov.caixa.loterias.core.designsystem.testing.captureToImage', generated)
        self.assertTrue(generated.endswith(body))
        self.assertEqual(path.read_text(), original)

    def test_capture_import_text_inside_body_is_not_rewritten(self):
        body = 'class ExampleTest {\n val label = "import androidx.compose.ui.test.captureToImage"\n}\n'
        path = self.source('package sample\n' + body)
        self.run_adapter([str(path.relative_to(self.root))])
        generated = (self.destination / 'sample/ExampleTest.kt').read_text()
        self.assertTrue(generated.endswith(body))

    def test_wildcard_capture_uses_local_import_and_preserves_device_body(self):
        body = 'class ExampleTest {\n @Test fun pixels() = assertEquals(1, node.captureToImage().width)\n}\n'
        original = 'package sample\nimport androidx.compose.ui.test.*\n' + body
        path = self.source(original)
        self.run_adapter([str(path.relative_to(self.root))])
        generated = (self.destination / 'sample/ExampleTest.kt').read_text()
        self.assertIn('import br.gov.caixa.loterias.core.designsystem.testing.captureToImage', generated)
        self.assertTrue(generated.endswith(body))
        self.assertEqual(path.read_text(), original)

    def test_wildcard_without_capture_does_not_add_capture_dependency(self):
        path = self.source('package sample\nimport androidx.compose.ui.test.*\nclass ExampleTest {}\n')
        self.run_adapter([str(path.relative_to(self.root))])
        generated = (self.destination / 'sample/ExampleTest.kt').read_text()
        self.assertNotIn('core.designsystem.testing.captureToImage', generated)

    def test_parameterized_cases_and_parameters_are_preserved(self):
        path = self.source('package sample\n@RunWith(Parameterized::class)\nclass ExampleTest(val id: Long) {\n'
                           ' @Parameterized.Parameters(name = "{0}") fun values() = listOf(1L, 2L)\n}\n')
        self.run_adapter([str(path.relative_to(self.root))])
        generated = (self.destination / 'sample/ExampleTest.kt').read_text()
        self.assertIn('ParameterizedRobolectricTestRunner::class', generated)
        self.assertIn('ParameterizedRobolectricTestRunner.Parameters(name = "{0}")', generated)
        self.assertIn('listOf(1L, 2L)', generated)

    def test_helpers_are_copied_without_test_annotations(self):
        content = 'package sample\nfun assertPixels() = assertEquals(1, pixel)\n'
        path = self.source(content, 'Assertions.kt')
        self.run_adapter(['helper:' + str(path.relative_to(self.root))])
        self.assertEqual((self.destination / 'sample/Assertions.kt').read_text(), content)

    def test_stale_generated_suites_are_removed(self):
        path = self.source('package sample\nclass ExampleTest {}\n')
        self.run_adapter([str(path.relative_to(self.root))])
        self.run_adapter([])
        self.assertEqual(list(self.destination.rglob('*.kt')), [])

    def test_path_traversal_and_missing_source_fail_closed(self):
        self.manifest.write_text(self.module + '/src/androidTest/../../../../../external.kt')
        with self.assertRaises(ValueError):
            adapter.prepare(self.root, self.module, self.manifest, self.destination)
        self.manifest.write_text(self.module + '/src/androidTest/Missing.kt')
        with self.assertRaises(ValueError):
            adapter.prepare(self.root, self.module, self.manifest, self.destination)

    def test_other_modules_and_comments_are_not_copied(self):
        self.run_adapter(['# comment', 'different/src/androidTest/Elsewhere.kt'])
        self.assertEqual(list(self.destination.rglob('*.kt')), [])


if __name__ == '__main__':
    unittest.main()
