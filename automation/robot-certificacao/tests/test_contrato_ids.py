"""Garante que a entrega não aceite seletores fora do contrato versionado."""
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest
ROOT=Path(__file__).resolve().parents[1]
spec=importlib.util.spec_from_file_location('contrato',ROOT/'Scripts/validar_ids.py')
module=importlib.util.module_from_spec(spec);spec.loader.exec_module(module)

class ContratoIdsTest(unittest.TestCase):
    def test_entrega_usa_ids_declarados(self):
        self.assertEqual([],module.validar())
    def test_localizador_desconhecido_bloqueia(self):
        with tempfile.TemporaryDirectory() as folder:
            root=Path(folder);(root/'TestData').mkdir()
            (root/'TestData/android-ids.json').write_text('[]')
            (root/'TestData/locators.json').write_text('{"botao":"id=nao_existe"}')
            self.assertTrue(module.validar(root))
    def test_declaracao_multilinha_fora_do_inventario_bloqueia(self):
        with tempfile.TemporaryDirectory() as package_dir, tempfile.TemporaryDirectory() as android_dir:
            package=Path(package_dir);android=Path(android_dir);(package/'TestData').mkdir()
            (package/'TestData/android-ids.json').write_text('[]')
            (package/'TestData/locators.json').write_text('{}')
            source=android/'feature/home/presentation/src/main/Widget.kt'
            source.parent.mkdir(parents=True);source.write_text('Modifier.automationId(\n    "simlo.novo"\n)')
            helper=android/'core/design-system/src/main/java/br/gov/caixa/loterias/core/designsystem/automation/AutomationIdentity.kt'
            helper.parent.mkdir(parents=True);helper.write_text('testTagsAsResourceId = true')
            self.assertTrue(any('simlo.novo' in e for e in module.validar(package,android)))

    def test_id_novo_deve_ser_literal_no_namespace_simlo(self):
        for expression in ['"fora_do_padrao"', 'nomeDoUsuario']:
            with self.subTest(expression=expression), tempfile.TemporaryDirectory() as package_dir, tempfile.TemporaryDirectory() as android_dir:
                package=Path(package_dir);android=Path(android_dir);(package/'TestData').mkdir()
                (package/'TestData/android-ids.json').write_text('[]')
                (package/'TestData/locators.json').write_text('{}')
                source=android/'feature/home/presentation/src/main/Widget.kt'
                source.parent.mkdir(parents=True);source.write_text('Modifier.automationId('+expression+')')
                helper=android/'core/design-system/src/main/java/br/gov/caixa/loterias/core/designsystem/automation/AutomationIdentity.kt'
                helper.parent.mkdir(parents=True);helper.write_text('testTagsAsResourceId = true')
                self.assertTrue(module.validar(package,android))

    def test_ids_duplicados_em_componentes_distintos_bloqueiam(self):
        with tempfile.TemporaryDirectory() as folder:
            root=Path(folder);(root/'TestData').mkdir()
            (root/'TestData/android-ids.json').write_text(json.dumps([
                {'id':'simlo.home.0001','source':'a.kt'},
                {'id':'simlo.home.0001','source':'b.kt'},
            ]))
            (root/'TestData/locators.json').write_text('{}')
            self.assertTrue(module.validar(root))

    def test_reutilizar_id_em_novo_fonte_sem_atualizar_catalogo_bloqueia(self):
        with tempfile.TemporaryDirectory() as package_dir, tempfile.TemporaryDirectory() as android_dir:
            package=Path(package_dir);android=Path(android_dir);(package/'TestData').mkdir()
            rel='feature/home/presentation/src/main/A.kt'
            (package/'TestData/android-ids.json').write_text(json.dumps([
                {'id':'simlo.home.0001','key':'home.0001','source':rel},
            ]))
            (package/'TestData/locators.json').write_text('{}')
            for name in ['A.kt','B.kt']:
                source=android/'feature/home/presentation/src/main'/name
                source.parent.mkdir(parents=True,exist_ok=True);source.write_text('Modifier.uiId("home.0001")')
            helper=android/'core/design-system/src/main/java/br/gov/caixa/loterias/core/designsystem/automation/AutomationIdentity.kt'
            helper.parent.mkdir(parents=True);helper.write_text('testTagsAsResourceId = true')
            self.assertTrue(module.validar(package,android))

    def test_xpath_eh_rejeitado(self):
        with tempfile.TemporaryDirectory() as folder:
            root=Path(folder);(root/'TestData').mkdir()
            (root/'TestData/android-ids.json').write_text('[]')
            (root/'TestData/locators.json').write_text('{"botao":"xpath=//Button[1]"}')
            self.assertTrue(module.validar(root))
if __name__=='__main__':unittest.main()
