"""Regressões de resultados: falha, skip e dry-run não viram certificação aprovada."""
import importlib.util
from pathlib import Path
import tempfile
import unittest
ROOT=Path(__file__).resolve().parents[1]
spec=importlib.util.spec_from_file_location('relatorio',ROOT/'Scripts/gerar_relatorio_pdf.py')
module=importlib.util.module_from_spec(spec);spec.loader.exec_module(module)

class RelatorioTest(unittest.TestCase):
    def test_preserva_falhas_e_modo_offline(self):
        xml='<robot><suite><metadata><item name="Modo">VALIDACAO_OFFLINE_SEM_APARELHO</item></metadata><test name="CTA_01"><status status="PASS"/></test><test name="CTA_02"><status status="FAIL">Erro</status></test><test name="CTA_03"><status status="SKIP"/></test></suite></robot>'
        with tempfile.TemporaryDirectory() as folder:
            p=Path(folder)/'output.xml';p.write_text(xml)
            data=module.ler_resultados(p)
            self.assertEqual(data['counts'],{'PASS':1,'FAIL':1,'SKIP':1})
            self.assertTrue(data['offline'])
    def test_detecta_metadata_real_do_robot_7(self):
        with tempfile.TemporaryDirectory() as folder:
            xml=Path(folder)/'output.xml'
            xml.write_text('<robot><suite><meta name="Modo">VALIDACAO_OFFLINE_SEM_APARELHO</meta><test name="CTA_01"><status status="PASS"/></test></suite></robot>')
            self.assertTrue(module.ler_resultados(xml)['offline'])

    def test_gera_pdf_real_sem_perder_status(self):
        with tempfile.TemporaryDirectory() as folder:
            xml=Path(folder)/'output.xml';pdf=Path(folder)/'relatorio.pdf'
            xml.write_text('<robot><suite><test name="CTA_01"><status status="FAIL"/></test></suite></robot>')
            result=module.gerar(xml,pdf)
            self.assertEqual(result['counts']['FAIL'],1)
            self.assertTrue(pdf.read_bytes().startswith(b'%PDF'))

    def test_xml_sem_teste_nao_gera_aprovacao(self):
        with tempfile.TemporaryDirectory() as folder:
            p=Path(folder)/'output.xml';p.write_text('<robot><suite/></robot>')
            with self.assertRaises(ValueError):module.ler_resultados(p)
if __name__=='__main__':unittest.main()
