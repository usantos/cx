"""Fixtures isoladas para o gate de identificadores de automação."""
import importlib.util
from pathlib import Path
import unittest
spec=importlib.util.spec_from_file_location('automation_ids',Path(__file__).with_name('check-automation-ids.py'))
gate=importlib.util.module_from_spec(spec);spec.loader.exec_module(gate)

class AutomationIdentityPolicyTest(unittest.TestCase):
    def test_new_control_without_id_is_rejected(self):
        self.assertTrue(gate.controle_sem_id('Button(onClick = {}) { Text("OK") }'))
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
if __name__=='__main__':unittest.main()
