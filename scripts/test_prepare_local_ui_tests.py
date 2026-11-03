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

    def test_base_presentation_that_renders_splits_uses_real_universal_resources(self):
        self.module = 'feature/example/presentation'
        body = 'class ExampleTest {\n @Test fun split() = assertEquals(7, resourceId())\n}\n'
        path = self.source('package sample\n' + body)
        (self.root / self.module / 'build.gradle').write_text("apply plugin: 'com.android.library'\n")
        self.run_adapter([str(path.relative_to(self.root))])
        generated = (self.destination / 'sample/ExampleTest.kt').read_text()
        self.assertIn('Config(sdk = [36]', generated)
        self.assertIn('DynamicFeatureRobolectricTestRunner::class', generated)
        self.assertTrue(generated.endswith(body))
        self.assertEqual(path.read_text(), 'package sample\n' + body)

    def test_dynamic_feature_uses_sdk36_and_native_resources_without_altering_assertions(self):
        body = 'class ExampleTest {\n @Test fun split() = assertEquals(7, resourceId())\n}\n'
        path = self.source('package sample\n' + body)
        (self.root / self.module / 'build.gradle').write_text("apply plugin: 'com.android.dynamic-feature'\n")
        self.run_adapter([str(path.relative_to(self.root))])
        generated = (self.destination / 'sample/ExampleTest.kt').read_text()
        self.assertIn('Config(sdk = [36]', generated)
        self.assertIn('DynamicFeatureRobolectricTestRunner::class', generated)
        self.assertIn('ResourcesMode(org.robolectric.annotation.ResourcesMode.Mode.NATIVE)', generated)
        self.assertTrue(generated.endswith(body))
        self.assertEqual(path.read_text(), 'package sample\n' + body)

    def test_dynamic_compose_rule_uses_real_base_host_and_preserves_test_methods(self):
        body = (' @Test fun check() {\n'
                ' val nested = createComposeRule()\n'
                ' assertEquals("createComposeRule()", actual())\n }\n}\n')
        original = ('package sample\nimport androidx.compose.ui.test.junit4.v2.createComposeRule\n'
                    'class ExampleTest {\n @get:Rule val compose = createComposeRule()\n' + body)
        path = self.source(original)
        (self.root / self.module / 'build.gradle').write_text("apply plugin: 'com.android.dynamic-feature'\n")
        self.run_adapter([str(path.relative_to(self.root))])
        generated = (self.destination / 'sample/ExampleTest.kt').read_text()
        self.assertIn('import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule', generated)
        self.assertIn('val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()', generated)
        self.assertTrue(generated.endswith(body))
        self.assertEqual(original, path.read_text())

    def test_dynamic_android_rule_replaces_only_component_activity_host(self):
        body = ' @Test fun check() { assertEquals(7, actual()) }\n}\n'
        original = ('package sample\nimport androidx.compose.ui.test.junit4.createAndroidComposeRule\n'
                    'class ExampleTest {\n @get:Rule val compose = createAndroidComposeRule<androidx.activity.ComponentActivity>()\n' + body)
        path = self.source(original)
        (self.root / self.module / 'build.gradle').write_text("apply plugin: 'com.android.dynamic-feature'\n")
        self.run_adapter([str(path.relative_to(self.root))])
        generated = (self.destination / 'sample/ExampleTest.kt').read_text()
        self.assertIn('val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()', generated)
        self.assertTrue(generated.endswith(body))
        self.assertEqual(original, path.read_text())

    def test_dynamic_parameterized_suite_preserves_each_case_with_native_runner(self):
        body = ('class ExampleTest(val id: Long) {\n'
                ' @Parameterized.Parameters(name = "{0}") fun values() = listOf(1L, 2L)\n'
                ' @Test fun check() { assertEquals(id, actual()) }\n}\n')
        original = 'package sample\n@RunWith(Parameterized::class)\n' + body
        path = self.source(original)
        (self.root / self.module / 'build.gradle').write_text("apply plugin: 'com.android.dynamic-feature'\n")
        self.run_adapter([str(path.relative_to(self.root))])
        generated = (self.destination / 'sample/ExampleTest.kt').read_text()
        self.assertIn('DynamicFeatureParameterizedRobolectricTestRunner::class', generated)
        self.assertIn('ResourcesMode.Mode.NATIVE)', generated)
        self.assertTrue(generated.endswith(body))
        self.assertEqual(path.read_text(), original)

    def test_library_remains_on_sdk35_without_native_resources(self):
        path = self.source('package sample\nclass ExampleTest {}\n')
        (self.root / self.module / 'build.gradle').write_text(
            "apply plugin: 'com.android.library'\n// com.android.dynamic-feature\n")
        self.run_adapter([str(path.relative_to(self.root))])
        generated = (self.destination / 'sample/ExampleTest.kt').read_text()
        self.assertIn('Config(sdk = [35]', generated)
        self.assertNotIn('ResourcesMode', generated)

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
