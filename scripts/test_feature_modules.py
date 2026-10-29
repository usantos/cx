import importlib.util
from pathlib import Path
import tempfile
import unittest

spec = importlib.util.spec_from_file_location('feature_modules', Path(__file__).with_name('check-feature-modules.py'))
policy = importlib.util.module_from_spec(spec)
spec.loader.exec_module(policy)


class FeatureModulesTest(unittest.TestCase):
    def test_feature_tests_cannot_keep_foreign_core_or_app_namespaces(self):
        for layer, package in [('data/src/test', 'br.gov.caixa.loterias.core.data'),
                               ('data/src/androidTest', 'br.gov.caixa.loterias.core.preferences'),
                               ('presentation/src/test', 'br.gov.caixa.loterias.apostas')]:
            with self.subTest(layer=layer), tempfile.TemporaryDirectory() as folder:
                root = Path(folder)
                source = root / 'feature/support' / layer / 'kotlin/ExampleTest.kt'
                source.parent.mkdir(parents=True)
                source.write_text('package ' + package + '\nclass ExampleTest')
                self.assertTrue(any('feature source must use its owning namespace' in error for error in policy.check(root)))

    def test_rejects_duplicate_dependency_in_the_same_configuration(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            build = root / 'feature/marketplace/presentation/build.gradle'
            build.parent.mkdir(parents=True)
            build.write_text("implementation project(':feature:marketplace-filter:presentation')\n"
                             "implementation project(':feature:marketplace-filter:presentation')\n")
            self.assertTrue(any('duplicate dependency' in error for error in policy.check(root)))

    def test_rejects_cycles_between_real_implementation_dependencies(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            for name, other in [('account', 'cart'), ('cart', 'account')]:
                build = root / 'feature' / name / 'domain' / 'build.gradle'
                build.parent.mkdir(parents=True)
                build.write_text("dependencies {\n api project(':feature:" + other + ":domain')\n}\n")
            self.assertTrue(any('cycle' in error for error in policy.check(root)))

    def test_independent_contract_breaks_a_cycle_without_ui_dependencies(self):
        graph = {':account': {':cart:api'}, ':cart': {':account', ':betting'}, ':betting': {':account', ':cart:api'}, ':cart:api': set()}
        self.assertEqual([], policy.cycles(graph))

    def test_rejects_external_feature_source_compilation(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            build = root / 'core/domain/build.gradle'
            build.parent.mkdir(parents=True)
            build.write_text("sourceSets.main.kotlin.srcDirs(rootProject.featureLayerRoots('domain'))")
            self.assertTrue(any('outside its own module' in error for error in policy.check(root)))

    def test_rejects_legacy_host_even_without_helper_call(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            source = root / 'feature/home/src/host/kotlin/Home.kt'
            source.parent.mkdir(parents=True)
            source.write_text('class Home')
            self.assertTrue(any('src/host' in error for error in policy.check(root)))

    def test_rejects_aggregated_roots_inside_presentation(self):
        for layer in ('host', 'domain', 'data', 'platform'):
            with self.subTest(layer=layer), tempfile.TemporaryDirectory() as folder:
                root = Path(folder)
                source = root / 'feature/home/presentation/src' / layer / 'kotlin/Home.kt'
                source.parent.mkdir(parents=True)
                source.write_text('class Home')
                self.assertTrue(any('aggregated source root remains' in error for error in policy.check(root)))

    def test_rejects_legacy_type_names_in_every_module_and_source_set(self):
        for path, declaration in (
            ('app/src/test/kotlin/SessionTest.kt', 'class LegacySessionTest'),
            ('core/design-system/src/main/kotlin/Panels.kt', 'data class CaixaDSLegacyPanel(val id: Int)'),
            ('feature/cart/domain/src/main/kotlin/Cart.kt', 'sealed interface LegacyCart'),
            ('feature/home/presentation/src/androidTest/java/ScreenTest.java', 'public class HomeLegacyTest {}'),
        ):
            with self.subTest(path=path), tempfile.TemporaryDirectory() as folder:
                root = Path(folder)
                source = root / path
                source.parent.mkdir(parents=True)
                source.write_text(declaration)
                self.assertTrue(any('Legacy type name' in error for error in policy.check(root)))

    def test_rejects_java_and_escaped_kotlin_types_and_explicit_jvm_facade(self):
        declarations = (
            ('Mode.java', 'enum LegacyMode { OPEN }'),
            ('Mode.kt', 'enum class FeatureLEGACYMode { OPEN }'),
            ('State.java', 'record LegacyState(int value) {}'),
            ('State.kt', 'class `LegacyState`'),
            ('Helpers.kt', '@file:kotlin.jvm.JvmName("LegacyFacade")\nfun helper() = Unit'),
        )
        for name, code in declarations:
            with self.subTest(name=name), tempfile.TemporaryDirectory() as folder:
                root = Path(folder)
                source = root / 'core/common/src/main/kotlin' / name
                source.parent.mkdir(parents=True)
                source.write_text(code)
                self.assertTrue(any('Legacy type name' in error for error in policy.check(root)))

    def test_rejects_legacy_kotlin_file_facade(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            source = root / 'core/design-system/src/main/kotlin/LegacyButtons.kt'
            source.parent.mkdir(parents=True)
            source.write_text('fun primaryButton() = Unit')
            self.assertTrue(any('Legacy source filename' in error for error in policy.check(root)))

    def test_allows_reference_resources_comments_and_strings(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            source = root / 'feature/cart/presentation/src/main/kotlin/Cart.kt'
            source.parent.mkdir(parents=True)
            source.write_text('// class LegacyComment\n/* interface LegacyDescription */\n'
                              'class Cart(val source: String = "class LegacyWireValue")\n'
                              'val doc = \"\"\"@file:JvmName("LegacyFacade")\"\"\"')
            resource = root / 'feature/cart/presentation/src/test/resources/legacy-contract.json'
            resource.parent.mkdir(parents=True)
            resource.write_text('{}')
            self.assertEqual([], policy.check(root))

    def test_test_only_reverse_dependency_does_not_create_production_cycle(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            for name, configuration, other in [('data', 'implementation', 'domain'), ('domain', 'testImplementation', 'data')]:
                build = root / 'core' / name / 'build.gradle'
                build.parent.mkdir(parents=True)
                build.write_text(configuration + " project(':core:" + other + "')\n")
            self.assertEqual([], policy.check(root))


if __name__ == '__main__':
    unittest.main()
