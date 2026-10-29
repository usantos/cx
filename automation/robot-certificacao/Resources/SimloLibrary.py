"""Conexão Appium e palavras-chave sem expor credenciais ao log do Robot."""
import base64
import json
import logging
from pathlib import Path
import sys
from urllib.parse import urlencode
from urllib.request import Request, urlopen
sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from Resources.Configuracao import carregar_configuracao
from robot.api.deco import keyword
from robot.libraries.BuiltIn import BuiltIn
from appium import webdriver
from appium.options.common import AppiumOptions
from appium.webdriver.appium_connection import AppiumConnection


def obter_token(auth):
    if auth['mode'] == 'none':
        return None
    if auth['mode'] == 'bearer':
        return auth['token']
    body = {'grant_type':'client_credentials'}
    headers = {'Content-Type':'application/x-www-form-urlencoded', 'Accept':'application/json'}
    if auth.get('clientAuthentication', 'basic') == 'basic':
        credential = base64.b64encode(f"{auth['clientId']}:{auth['clientSecret']}".encode()).decode()
        headers['Authorization'] = 'Basic ' + credential
    else:
        body.update(client_id=auth['clientId'], client_secret=auth['clientSecret'])
    if auth.get('scope'):
        body['scope'] = auth['scope']
    try:
        request = Request(auth['tokenUrl'], data=urlencode(body).encode(), headers=headers, method='POST')
        with urlopen(request, timeout=20) as response:
            data = json.loads(response.read(65537))
        token = data.get('access_token')
        if not isinstance(token, str) or not token or data.get('token_type','').lower() != 'bearer':
            raise ValueError('Resposta inválida')
        return token
    except Exception:
        raise RuntimeError('Falha ao autenticar na farm. Confira contrato OAuth, credenciais e conectividade; resposta omitida.') from None


def criar_conexao(url, token):
    # A credencial pertence a esta conexão; não altera headers globais de outros clientes.
    class ConexaoDaSessao(AppiumConnection):
        @classmethod
        def get_remote_connection_headers(cls, parsed_url, keep_alive=True):
            headers = super().get_remote_connection_headers(parsed_url, keep_alive)
            if token:
                headers['Authorization'] = 'Bearer ' + token
            return headers
    return ConexaoDaSessao(remote_server_addr=url, keep_alive=True)


class SimloLibrary:
    ROBOT_LIBRARY_SCOPE = 'SUITE'
    ROBOT_AUTO_KEYWORDS = False

    def __init__(self, localizadores=None):
        path = Path(localizadores or Path(__file__).resolve().parents[1]/'TestData/locators.json')
        self.localizadores = json.loads(path.read_text(encoding='utf-8'))
        self.configuracao = None

    def _appium(self):
        return BuiltIn().get_library_instance('AppiumLibrary')

    def _localizador(self, nome):
        if nome not in self.localizadores:
            raise AssertionError(f'Localizador não cadastrado: {nome}')
        return self.localizadores[nome]

    @keyword('Abrir Sessao SIMLO')
    def abrir_sessao(self):
        self.configuracao = carregar_configuracao()
        token = obter_token(self.configuracao.authentication)
        for logger in ('selenium.webdriver.remote.remote_connection', 'urllib3.connectionpool'):
            logging.getLogger(logger).setLevel(logging.WARNING)
        try:
            driver = webdriver.Remote(command_executor=criar_conexao(self.configuracao.url, token),
                options=AppiumOptions().load_capabilities(self.configuracao.capabilities), strict_ssl=True)
        except Exception:
            raise RuntimeError('Não foi possível abrir sessão Appium. Confira servidor, autenticação, UDID, UiAutomator2 e instalação do APK correto.') from None
        # Adaptador para o cache da versão 2.1.0, fixada e testada neste pacote.
        self._appium()._cache.register(driver, 'SIMLO')
        self._appium().set_appium_timeout(self.configuracao.timeout)
        self._appium().activate_application(self.configuracao.capabilities['appium:appPackage'])
        driver.switch_to.context('NATIVE_APP')

    @keyword('Esperar Alvo SIMLO')
    def esperar_alvo(self, nome):
        self._appium().wait_until_element_is_visible(self._localizador(nome))

    @keyword('Tocar Alvo SIMLO')
    def tocar_alvo(self, nome):
        self.esperar_alvo(nome)
        self._appium().click_element(self._localizador(nome))

    @keyword('Confirmar Ausencia Do Alvo SIMLO')
    def confirmar_ausencia(self, nome):
        self._appium().wait_until_page_does_not_contain_element(self._localizador(nome))
