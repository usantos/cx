"""Fixtures isoladas para o gate de identificadores de automação."""
import importlib.util
import json
import shutil
import tempfile
from pathlib import Path
import unittest
spec=importlib.util.spec_from_file_location('automation_ids',Path(__file__).with_name('check-automation-ids.py'))
gate=importlib.util.module_from_spec(spec);spec.loader.exec_module(gate)

class AutomationIdentityPolicyTest(unittest.TestCase):
    def test_new_control_without_id_is_rejected(self):
        self.assertTrue(gate.controle_sem_id('Button(onClick = {}) { Text("OK") }'))
    def test_both_design_system_prefixes_preserve_identity_policy(self):
        for prefix in ('CaixaDS', 'LoteriasCaixaDS'):
            with self.subTest(prefix=prefix):
                self.assertTrue(gate.controle_sem_id(prefix + 'Button(onClick = {}) {}'))
                self.assertEqual([], gate.controle_sem_id(prefix + 'Button(onClick = {}, modifier = Modifier.automationId("simlo.ok")) {}'))
    def test_named_modifier_with_explicit_id_is_accepted(self):
        self.assertEqual([],gate.controle_sem_id('Button(onClick = {}, modifier = Modifier.automationId("simlo.ok")) {}'))
    def test_positional_modifier_alias_is_accepted(self):
        self.assertEqual([],gate.controle_sem_id('TextButton({}, (cancelModifier).automationId("simlo.cancel")) {}'))
    def test_comments_and_strings_do_not_create_controls(self):
        self.assertEqual([],gate.controle_sem_id('// Button()\nval text = "Button()"'))
    def test_measurement_native_button_is_not_mounted_compose(self):
        self.assertEqual([],gate.controle_sem_id('android.widget.Button(context).apply {}'))
    def test_nested_control_cannot_supply_parent_identity(self):
        self.assertTrue(gate.controle_sem_id('Button(onClick = {}, modifier = Modifier) { Button(onClick = {}, modifier = Modifier.automationId("simlo.child")) {} }'))
class DynamicFeatureIdentityPolicyTest(unittest.TestCase):
    def setUp(self):
        self.directory = tempfile.TemporaryDirectory()
        self.addCleanup(self.directory.cleanup)
        self.root = Path(self.directory.name)
        self.package = self.root / 'automation/robot-certificacao'
        (self.package / 'Scripts').mkdir(parents=True)
        (self.package / 'TestData').mkdir()
        shutil.copyfile(gate.ROOT / 'automation/robot-certificacao/Scripts/validar_ids.py',
                        self.package / 'Scripts/validar_ids.py')
        self.source = self.root / 'feature/support/faq/src/main/kotlin/Faq.kt'
        self.source.parent.mkdir(parents=True)
        (self.root / 'feature/support/faq/build.gradle').write_text("apply plugin: 'com.android.dynamic-feature'\n")
        (self.root / 'settings.gradle').write_text("include ':feature:support:faq'\n")
        self.helper = self.root / 'core/design-system/src/main/java/br/gov/caixa/loterias/core/designsystem/automation/AutomationIdentity.kt'
        self.helper.parent.mkdir(parents=True)
        self.helper.write_text('val testTagsAsResourceId = true')
        self.catalogue([])

    def catalogue(self, keys):
        records = [dict(id='simlo.' + key, key=key, source=self.source.relative_to(self.root).as_posix()) for key in keys]
        (self.package / 'TestData/android-ids.json').write_text(json.dumps(records))
        (self.package / 'TestData/locators.json').write_text('{}')

    def test_registered_dynamic_control_is_checked_and_accepted_with_catalogued_id(self):
        self.source.write_text('@Composable fun Faq() { Button(onClick = {}, modifier = Modifier.uiId("help.9001")) {} }')
        self.catalogue(['help.9001'])
        self.assertEqual([], gate.check(self.root))

    def test_kotlin_settings_and_plugin_also_include_the_dynamic_source(self):
        (self.root / 'settings.gradle').unlink()
        (self.root / 'settings.gradle.kts').write_text('include(":feature:support:faq")\n')
        (self.root / 'feature/support/faq/build.gradle').unlink()
        (self.root / 'feature/support/faq/build.gradle.kts').write_text('plugins { id("com.android.dynamic-feature") }\n')
        self.source.write_text('@Composable fun Faq() { Button(onClick = {}) {} }')
        self.assertTrue(any('controle sem automationId' in error for error in gate.check(self.root)))

    def test_comment_does_not_make_a_library_a_dynamic_module(self):
        (self.root / 'feature/support/faq/build.gradle').write_text(
            "apply plugin: 'com.android.library'\n// apply plugin: 'com.android.dynamic-feature'\n")
        self.source.write_text('@Composable fun Faq() { Button(onClick = {}) {} }')
        self.assertEqual([], gate.check(self.root))

    def test_registered_dynamic_control_without_id_is_rejected(self):
        self.source.write_text('@Composable fun Faq() { Button(onClick = {}) {} }')
        self.assertTrue(any('controle sem automationId' in error for error in gate.check(self.root)))

    def test_registered_dynamic_id_missing_from_catalogue_is_rejected(self):
        self.source.write_text('@Composable fun Faq() { Button(onClick = {}, modifier = Modifier.uiId("help.9001")) {} }')
        self.assertTrue(any('ID novo não incluído' in error for error in gate.check(self.root)))

    def test_registered_dynamic_id_must_be_literal(self):
        self.source.write_text('@Composable fun Faq() { Button(onClick = {}, modifier = Modifier.uiId(userId)) {} }')
        self.assertTrue(any('literal estável' in error for error in gate.check(self.root)))

    def test_shared_presentation_control_without_id_is_rejected(self):
        source = self.root / 'core/presentation/src/main/kotlin/ModuleState.kt'
        source.parent.mkdir(parents=True)
        source.write_text('@Composable fun ModuleState() { Button(onClick = {}) {} }')
        self.assertTrue(any('controle sem automationId' in error for error in gate.check(self.root)))

    def test_shared_presentation_id_missing_from_catalogue_is_rejected(self):
        source = self.root / 'core/presentation/src/main/kotlin/ModuleState.kt'
        source.parent.mkdir(parents=True)
        source.write_text('@Composable fun ModuleState() { Button(onClick = {}, modifier = Modifier.uiId("modules.0001")) {} }')
        self.assertTrue(any('ID novo não incluído' in error for error in gate.check(self.root)))

    def test_shared_presentation_id_is_checked_and_accepted_when_catalogued(self):
        self.source = self.root / 'core/presentation/src/main/kotlin/ModuleState.kt'
        self.source.parent.mkdir(parents=True)
        self.source.write_text('@Composable fun ModuleState() { Button(onClick = {}, modifier = Modifier.uiId("modules.0001")) {} }')
        self.catalogue(['modules.0001'])
        self.assertEqual([], gate.check(self.root))

    def test_unregistered_and_generated_sources_do_not_expand_the_inventory(self):
        self.source.write_text('@Composable fun Faq() { Button(onClick = {}, modifier = Modifier.uiId("help.9001")) {} }')
        self.catalogue(['help.9001'])
        for relative in ['feature/support/temporary/src/main/kotlin/Scratch.kt',
                         'feature/support/faq/build/generated/Scratch.kt']:
            source = self.root / relative
            source.parent.mkdir(parents=True, exist_ok=True)
            source.write_text('@Composable fun Scratch() { Button(onClick = {}) {} }')
        (self.root / 'feature/support/temporary/build.gradle').write_text("apply plugin: 'com.android.dynamic-feature'\n")
        self.assertEqual([], gate.check(self.root))

if __name__=='__main__':unittest.main()
