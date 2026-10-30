"""Garante entrada estável no Play e preservação das configurações locais."""
import importlib.util
from pathlib import Path
import tempfile
import unittest
import xml.etree.ElementTree as ET


class AndroidStudioLauncherTest(unittest.TestCase):
    def setUp(self):
        spec = importlib.util.spec_from_file_location("studio_run", Path(__file__).with_name("configure-android-studio-run.py"))
        self.module = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(self.module)
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        target = self.root / ".run/SIMLO_App.run.xml"
        target.parent.mkdir()
        target.write_text((Path(__file__).resolve().parents[1] / ".run/SIMLO_App.run.xml").read_text())

    def test_shared_configuration_is_valid_and_disabled_alias_is_rejected(self):
        self.assertEqual([], self.module.configuration_problems(self.root))
        path = self.root / ".run/SIMLO_App.run.xml"
        path.write_text(path.read_text().replace("specific_activity", "default_activity"))
        self.assertTrue(self.module.configuration_problems(self.root))

    def test_local_app_is_migrated_without_touching_sample_or_erasing_data(self):
        path = self.root / ".idea/workspace.xml"
        path.parent.mkdir()
        original = '<project><component name="RunManager" selected="Android App.app"><configuration name="app" type="AndroidRunConfigurationType"><module name="cx.app"/><option name="MODE" value="default_activity"/><option name="TARGET_SELECTION_MODE" value="preservado"/></configuration><configuration name="sample" type="AndroidRunConfigurationType"><module name="cx.sample.design-system"/><option name="MODE" value="default_activity"/></configuration></component></project>'
        path.write_text(original)
        self.assertTrue(self.module.repair_workspace(self.root))
        manager = ET.parse(path).getroot().find('component')
        self.assertEqual('Android App.SIMLO App', manager.get('selected'))
        configs = manager.findall('configuration')
        self.assertEqual('simlo-app-android.app', configs[0].find('module').get('name'))
        options = {x.get('name'): x.get('value') for x in configs[0].findall('option')}
        self.assertEqual('specific_activity', options['MODE'])
        self.assertEqual('br.gov.caixa.loterias.apostas.MainActivity', options['ACTIVITY_CLASS'])
        self.assertEqual('false', options['CLEAR_APP_STORAGE'])
        self.assertEqual('false', options['SKIP_ACTIVITY_VALIDATION'])
        self.assertEqual('preservado', options['TARGET_SELECTION_MODE'])
        self.assertEqual('default_activity', configs[1].find('option').get('value'))
        backup = list((self.root / '.local/android-studio').glob('*.xml'))
        self.assertEqual(1, len(backup))
        self.assertEqual(original, backup[0].read_text())
        self.assertFalse(self.module.repair_workspace(self.root))

    def test_missing_workspace_is_not_created(self):
        self.assertFalse(self.module.repair_workspace(self.root))
        self.assertFalse((self.root / '.idea/workspace.xml').exists())
