"""Regressões para configuração de sessão e prevenção de vazamento de secrets."""
import copy
import json
import sys
import unittest
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from Resources.Configuracao import construir_configuracao, ConfiguracaoInvalida

BASE = {"target": "local", "environment": "tqs", "udid": "dispositivo-teste"}

class ConfiguracaoTest(unittest.TestCase):
    def test_tqs_usa_activity_real_e_preserva_dados(self):
        c = construir_configuracao(BASE, {})
        self.assertEqual(c.capabilities['appium:appPackage'], 'br.gov.caixa.loterias.apostas.hmp')
        self.assertEqual(c.capabilities['appium:appActivity'], 'br.gov.caixa.loterias.apostas.MainActivity')
        self.assertTrue(c.capabilities['appium:noReset'])
        self.assertNotIn('appium:app', c.capabilities)
    def test_prd_eh_explicito(self):
        c = construir_configuracao({**BASE, 'environment': 'prd'}, {})
        self.assertEqual(c.capabilities['appium:appPackage'], 'br.gov.caixa.loterias.apostas')
    def test_env_tem_precedencia(self):
        c = construir_configuracao(BASE, {'SIMLO_UDID': 'outro', 'SIMLO_ENVIRONMENT': 'prd'})
        self.assertEqual(c.capabilities['appium:udid'], 'outro')
        self.assertEqual(c.environment, 'prd')
    def test_farm_usa_https_e_nao_exige_servidor_local(self):
        c = construir_configuracao({**BASE, 'target':'farm', 'remoteUrl':'https://farm.example/wd/hub', 'authentication':{'mode':'none'}}, {})
        self.assertEqual(c.url, 'https://farm.example/wd/hub')
    def test_rejeita_farm_http_e_url_com_senha(self):
        for url in ['http://farm.example/wd/hub', 'https://user:senha@farm.example/wd/hub', 'https://farm.example/wd/hub?token=secreto']:
            with self.subTest(url=url), self.assertRaises(ConfiguracaoInvalida):
                construir_configuracao({**BASE,'target':'farm','remoteUrl':url,'authentication':{'mode':'none'}}, {})
    def test_rejeita_ambiente_udid_e_tipo_invalidos(self):
        for change in [{'environment':'outro'}, {'udid':''}, {'timeout':True}, {'timeout':0}, {'target':'outro'}]:
            with self.subTest(change=change), self.assertRaises(ConfiguracaoInvalida):
                construir_configuracao({**BASE, **change}, {})
    def test_caps_nao_podem_trocar_pacote_ou_desativar_tls(self):
        for caps in [{'appium:appPackage':'outro'}, {'acceptInsecureCerts': True}, {'oauthClientSecret':'secreto'}]:
            with self.subTest(caps=caps), self.assertRaises(ConfiguracaoInvalida):
                construir_configuracao({**BASE,'extraCapabilities':caps}, {})
    def test_farm_exige_modo_autenticacao_explicito(self):
        with self.assertRaises(ConfiguracaoInvalida):
            construir_configuracao({**BASE,'target':'farm','remoteUrl':'https://farm.example/wd/hub'}, {})
    def test_oauth_sem_endpoint_fornecido_falha_sem_mostrar_secret(self):
        with self.assertRaises(ConfiguracaoInvalida) as result:
            construir_configuracao({**BASE,'target':'farm','remoteUrl':'https://farm.example/wd/hub','authentication':{'mode':'oauth_client_credentials'},'oauthClientId':'id','oauthClientSecret':'NAO_IMPRIMIR'}, {})
        self.assertNotIn('NAO_IMPRIMIR', str(result.exception))
    def test_repr_nao_expoe_credencial(self):
        c = construir_configuracao({**BASE,'target':'farm','remoteUrl':'https://farm.example/wd/hub','authentication':{'mode':'bearer'}}, {'SIMLO_FARM_BEARER_TOKEN':'NAO_IMPRIMIR'})
        self.assertNotIn('NAO_IMPRIMIR', repr(c))
    def test_nao_muda_dicionario_recebido(self):
        original=copy.deepcopy(BASE);construir_configuracao(BASE,{})
        self.assertEqual(original, BASE)

if __name__ == '__main__': unittest.main()
