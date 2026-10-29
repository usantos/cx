"""Valida configuração sem consultar a rede nem registrar credenciais."""
from dataclasses import dataclass, field
import json
import os
from pathlib import Path
from urllib.parse import urlsplit

class ConfiguracaoInvalida(ValueError):
    """Configuração insuficiente ou incompatível com a execução solicitada."""

@dataclass(repr=False)
class Configuracao:
    target: str
    environment: str
    url: str
    capabilities: dict
    timeout: int
    authentication: dict = field(repr=False)

    def __repr__(self):
        return f'Configuracao(target={self.target!r}, environment={self.environment!r}, credenciais=ocultas)'

def url_validada(value, https=False):
    if not isinstance(value, str):
        raise ConfiguracaoInvalida('URL ausente ou inválida.')
    try:
        u = urlsplit(value)
        _ = u.port
    except ValueError:
        raise ConfiguracaoInvalida('URL inválida.') from None
    if (u.scheme not in (('https',) if https else ('http','https')) or not u.hostname
            or u.username or u.password or u.query or u.fragment):
        raise ConfiguracaoInvalida('URL deve ter host válido, sem credenciais, query ou fragmento; farm exige HTTPS.')
    if not https and u.scheme == 'http' and u.hostname not in ('localhost','127.0.0.1','::1'):
        raise ConfiguracaoInvalida('HTTP permitido somente para servidor Appium local.')
    return value

def construir_configuracao(data, env=None):
    env = os.environ if env is None else env
    if not isinstance(data, dict):
        raise ConfiguracaoInvalida('A configuração deve ser um objeto JSON.')
    target = env.get('SIMLO_TARGET', data.get('target', 'local'))
    environment = env.get('SIMLO_ENVIRONMENT', data.get('environment', 'tqs'))
    udid = env.get('SIMLO_UDID', data.get('udid', ''))
    timeout = data.get('timeout', 40)
    if target not in ('local','farm') or environment not in ('tqs','prd','automacao'):
        raise ConfiguracaoInvalida('Destino ou ambiente inválido: use local/farm e tqs/prd/automacao.')
    if not isinstance(udid, str) or not udid.strip() or udid.startswith('<'):
        raise ConfiguracaoInvalida('Informe o UDID do dispositivo em SIMLO_UDID ou environments.json.')
    if type(timeout) is not int or not 1 <= timeout <= 300:
        raise ConfiguracaoInvalida('timeout deve ser um inteiro de 1 a 300 segundos.')
    url = url_validada(env.get('SIMLO_REMOTE_URL', data.get('remoteUrl', 'http://127.0.0.1:4723/' if target=='local' else '')), target=='farm')
    auth = dict(data.get('authentication', {}))
    if target == 'local':
        auth = {'mode':'none'}
    elif auth.get('mode') not in ('none','bearer','oauth_client_credentials'):
        raise ConfiguracaoInvalida('Configure explicitamente authentication.mode conforme contrato da farm.')
    if auth['mode'] == 'bearer':
        auth['token'] = env.get('SIMLO_FARM_BEARER_TOKEN', '')
        if not auth['token']:
            raise ConfiguracaoInvalida('SIMLO_FARM_BEARER_TOKEN é obrigatório para autenticação bearer.')
    if auth['mode'] == 'oauth_client_credentials':
        auth['tokenUrl'] = url_validada(env.get('SIMLO_FARM_TOKEN_URL', auth.get('tokenUrl', '')), True)
        auth['clientId'] = env.get('SIMLO_FARM_CLIENT_ID', data.get('oauthClientId', ''))
        auth['clientSecret'] = env.get('SIMLO_FARM_CLIENT_SECRET', data.get('oauthClientSecret', ''))
        if not auth['clientId'] or not auth['clientSecret']:
            raise ConfiguracaoInvalida('Configure client ID e secret da farm fora do repositório.')
        if auth.get('clientAuthentication', 'basic') not in ('basic','body'):
            raise ConfiguracaoInvalida('clientAuthentication deve ser basic ou body conforme contrato da farm.')
    caps = {
        'platformName':'Android', 'appium:automationName':'UiAutomator2',
        'appium:udid':udid.strip(), 'appium:deviceName':data.get('deviceName','Android SIMLO'),
        'appium:appPackage':'br.gov.caixa.loterias.apostas' + ('' if environment=='prd' else '.hmp'),
        'appium:appActivity':'br.gov.caixa.loterias.apostas.MainActivity',
        'appium:appWaitActivity':'br.gov.caixa.loterias.apostas.MainActivity',
        'appium:noReset':True, 'appium:fullReset':False,
        'appium:newCommandTimeout':180,
        'appium:settings[disableIdLocatorAutocompletion]':True,
    }
    extras = data.get('extraCapabilities', {})
    if not isinstance(extras, dict):
        raise ConfiguracaoInvalida('extraCapabilities deve ser objeto JSON.')
    # Nenhum override de pacote, reinstalação, segurança ou credenciais é aceito silenciosamente.
    allowed = {'appium:platformVersion','appium:systemPort','appium:mjpegServerPort'}
    if any(k not in allowed for k in extras):
        raise ConfiguracaoInvalida('Capacidade adicional não permitida; adapte o conector com revisão do contrato da farm.')
    caps.update(extras)
    return Configuracao(target, environment, url, caps, timeout, auth)

def carregar_configuracao(path=None):
    path = Path(path or os.environ.get('SIMLO_CONFIG', Path(__file__).resolve().parents[1]/'environments.json'))
    try:
        data = json.loads(path.read_text(encoding='utf-8')) if path.exists() else {}
    except (OSError, ValueError):
        raise ConfiguracaoInvalida('Não foi possível ler environments.json; confira o JSON local.') from None
    return construir_configuracao(data)
