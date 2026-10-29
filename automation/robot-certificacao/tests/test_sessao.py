"""Testes do conector sem abrir sessão nem consultar a farm."""
import sys
import unittest
from pathlib import Path
from unittest.mock import patch, MagicMock
sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from Resources.SimloLibrary import obter_token, criar_conexao, SimloLibrary
from Resources.Configuracao import construir_configuracao

class SessaoTest(unittest.TestCase):
    def test_bearer_nao_vaza_para_outra_conexao(self):
        from urllib.parse import urlsplit
        c = criar_conexao('https://farm.example/wd/hub', 'secreto')
        h = c.get_remote_connection_headers(urlsplit('https://farm.example/wd/hub/session'))
        self.assertEqual(h['Authorization'], 'Bearer secreto')
        other = criar_conexao('http://127.0.0.1:4723/', None)
        self.assertNotIn('Authorization', other.get_remote_connection_headers(urlsplit('http://127.0.0.1:4723/session')))
    def test_sem_auth_nao_faz_rede(self):
        with patch('Resources.SimloLibrary.urlopen') as request:
            self.assertIsNone(obter_token({'mode':'none'}));request.assert_not_called()
    def test_oauth_monta_formulario_e_valida_tipo(self):
        response=MagicMock();response.read.return_value=b'{"access_token":"token","token_type":"Bearer"}'
        response.__enter__.return_value=response
        with patch('Resources.SimloLibrary.urlopen',return_value=response) as request:
            self.assertEqual(obter_token({'mode':'oauth_client_credentials','tokenUrl':'https://farm.example/token','clientId':'id','clientSecret':'secret','clientAuthentication':'basic'}),'token')
            sent=request.call_args.args[0]
            self.assertIn(b'grant_type=client_credentials',sent.data)
            self.assertNotIn(b'secret',sent.data)
            self.assertTrue(sent.get_header('Authorization').startswith('Basic '))
    def test_oauth_erro_nao_expoe_resposta(self):
        with patch('Resources.SimloLibrary.urlopen',side_effect=RuntimeError('NAO_IMPRIMIR')):
            with self.assertRaises(RuntimeError) as result:
                obter_token({'mode':'oauth_client_credentials','tokenUrl':'https://farm.example/token','clientId':'id','clientSecret':'secret'})
        self.assertNotIn('NAO_IMPRIMIR',str(result.exception))
    def test_localizador_ausente_falha_explicitamente(self):
        lib=SimloLibrary()
        with self.assertRaises(AssertionError):lib._localizador('nao.existe')
    def test_localizadores_sao_unicos_e_sem_xpath_ou_coordenadas(self):
        lib=SimloLibrary()
        self.assertGreaterEqual(len(lib.localizadores),8)
        for value in lib.localizadores.values():
            self.assertTrue(value.startswith(('android=','accessibility_id=','id=')))

if __name__=='__main__':unittest.main()
