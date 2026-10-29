#!/usr/bin/env python3
"""Validate feature ownership and layer direction, including independently compiled feature modules."""
from pathlib import Path
import argparse
import re
import sys

DOMAIN_OUTER = re.compile(r'^(?:android(?:x)?\.|retrofit2\.|okhttp3\.|br\.gov\.caixa\.loterias\.core\.(?:data|network|auth|preferences|database|datastore|queue|navigation|presentation|designsystem)\.|br\.gov\.caixa\.loterias\.feature\.\w+\.(?:presentation|data)\.)')
PRESENTATION_OUTER = re.compile(r'^br\.gov\.caixa\.loterias\.(?:core\.(?:data|network|preferences|database|datastore)\.|feature\.\w+\.data\.)')
VM_OUTER = re.compile(r'^(?:android\.|br\.gov\.caixa\.loterias\.core\.auth\.)')
NON_CODE = re.compile(r'"""[\s\S]*?"""+|"(?:\\.|[^"\\])*"|/\*[\s\S]*?\*/|//[^\n]*')
DATA_ACCESS_TYPE = re.compile(r'\b(?:[A-Z]\w*)?(?:Repository|DataSource)(?:Impl)?\b')
USE_CASE_PACKAGE = re.compile(r'\.domain(?:\.\w+)*\.usecase\.')
USE_CASE_TYPE = re.compile(r'\b(?:[A-Z]\w*)?UseCases?(?:Impl)?\b')
DATASOURCE_TYPE = re.compile(r'\b(?:[A-Z]\w*)?DataSource(?:Impl)?\b')
LOCATION_RESOLUTION = re.compile(r'\bGeocoder\b|\b(?:getLastKnownLocation|getCurrentLocation|getFromLocation)\s*\(|::\s*(?:getLastKnownLocation|getCurrentLocation|getFromLocation)\b')
DATA_TO_PRESENTATION = re.compile(r'\bbr\.gov\.caixa\.loterias\.(?:feature\.\w+\.presentation|core\.(?:presentation|designsystem|navigation))\.')


# Redutores puros de estado da funcionalidade; migração completa Redux/MVVM autorizada pelo usuário.
REDUX_FEATURES = {
    'account', 'betting', 'cart', 'home', 'marketplace', 'marketplace-filter', 'notifications',
    'onboarding', 'payment', 'responsible-gaming', 'results', 'retail', 'support', 'tickets',
}


def approved_state_reducer(rel: Path, feature: str, package: str, filename: str) -> bool:
    expected = 'br.gov.caixa.loterias.feature.' + feature.replace('-', '') + '.presentation.state'
    return (feature in REDUX_FEATURES and package == expected and filename.endswith('Reducer.kt')
            and any(rel == Path('feature', feature, 'presentation', 'src', 'main', language,
                                *expected.split('.'), filename) for language in ('java', 'kotlin')))


REDUCER_DECLARATION = re.compile(
    r'\bfun\s+(?:[\w.]+\.)?reduce\w*\s*\(|\b(?:class|object|interface)\s+\w*Reducer\b'
)
REDUCER_EFFECT_DEPENDENCY = re.compile(
    r'\b(?:android|androidx|kotlinx\.coroutines|org\.reduxkotlin|retrofit2|okhttp3)\.'
    r'|\bjava\.(?:io|nio|net|time|util)\.'
    r'|\bkotlin\.(?:io|random)\.|\bkotlin\.time\.Clock\b'
    r'|\bbr\.gov\.caixa\.loterias\.core\.(?:auth|data|network|preferences|database|datastore|queue)\.'
)
REDUCER_EFFECT_OPERATION = re.compile(
    r'\b(?:var|suspend)\b'
    r'|\b(?:launch|async|runBlocking|withContext|delay|dispatch|subscribe|unsubscribe|'
    r'currentTimeMillis|nanoTime|elapsedRealtime|now|random|nextInt|nextLong|nextDouble|'
    r'print|println|readLine|readln|readText|writeText|appendText|readBytes|writeBytes|'
    r'mutableListOf|mutableSetOf|mutableMapOf|mutableStateOf|MutableStateFlow)\s*(?:<[^>]+>\s*)?(?:\(|\{)'
    r'|::\s*(?:launch|async|now|currentTimeMillis|println|readText|writeText)\b'
)
REDUCER_LOCALIZATION = re.compile(
    r'\bpresentation\.formatting\.localizedMessage\b|\bTextCatalog\b|\bResourceBundle\b'
    r'|\bLocale\.getDefault\s*\('
)


def navigation_starts_coroutines(code: str) -> bool:
    names = {'produceState', 'rememberCoroutineScope', 'CoroutineScope', 'withContext', 'launch', 'async'}
    for imported, alias in re.findall(r'^import\s+([\w.]+)\s+as\s+(\w+)', code, re.M):
        if imported.rsplit('.', 1)[-1] in names:
            names.add(alias)
    return any(re.search(r'\b' + re.escape(name) + r'\s*(?:<[^>]+>\s*)?[({]', code) for name in names)


def di_implements_usecase(code: str) -> bool:
    aliases = {alias for imported, alias in re.findall(r'^import\s+([\w.]+)\s+as\s+(\w+)', code, re.M)
               if imported.rsplit('.', 1)[-1].endswith(('UseCase', 'UseCases'))}
    names = r'(?:\w*UseCases?' + (r'|' + '|'.join(re.escape(alias) for alias in aliases) if aliases else '') + r')'
    return bool(re.search(r'\b' + names + r'\s*\{', code))


def is_presentation_reducer(source: Path, package: str, code: str) -> bool:
    return ('reducer' in package.split('.') or 'reducers' in package.split('.')
            or source.stem.endswith('Reducer') or bool(REDUCER_DECLARATION.search(code)))


def reducer_has_effects(code: str, application_access: bool) -> bool:
    # Proteção lexical, não uma prova de pureza: proíbe dependências reconhecíveis,
    # declarações mutáveis, chamadas de efeitos e referências, mesmo totalmente qualificadas.
    return bool(REDUCER_EFFECT_DEPENDENCY.search(code) or REDUCER_EFFECT_OPERATION.search(code)
                or DATA_ACCESS_TYPE.search(code) or application_access)


def is_viewmodel(source: Path, package: str, base: str, code: str) -> bool:
    if source.name.endswith('ViewModel.kt') or package == base + '.presentation.viewmodel' or package.startswith(base + '.presentation.viewmodel.'):
        return True
    names = {'ViewModel', 'AndroidViewModel'}
    names.update(alias for alias in re.findall(r'^import\s+androidx\.lifecycle\.(?:Android)?ViewModel\s+as\s+(\w+)', code, re.M))
    return any(re.search(r':\s*(?:androidx\.lifecycle\.)?' + re.escape(name) + r'\s*\(', code) for name in names)


def kotlin_code(text: str) -> str:
    return NON_CODE.sub(lambda m: re.sub(r'[^\n]', ' ', m[0]), text)


def use_case_implementations(root: Path, features: list[Path]) -> set[str]:
    folders = [root / 'core/domain/src/main']
    for feature in features:
        folders.extend(feature / path for path in ('domain/src/main', 'api/src/main', 'src/domain', 'src/api'))
    symbols = set()
    for folder in folders:
        for source in sorted(folder.rglob('*.kt')) if folder.exists() else []:
            code = kotlin_code(source.read_text())
            package = re.search(r'^package\s+([\w.]+)', code, re.M)
            if not package:
                continue
            aliases = {alias: imported for imported, alias in re.findall(r'^import\s+([\w.]+)\s+as\s+(\w+)', code, re.M)}
            for declaration in re.finditer(r'\b(?:class|object)\s+(\w+)((?:(?!\b(?:class|interface|object)\s)[^{}])*)', code):
                name = declaration[1]
                parent = re.search(r'(?:\)|\b' + re.escape(name) + r')\s*:\s*([\w.]+)', declaration[0])
                if parent and USE_CASE_TYPE.fullmatch(aliases.get(parent[1], parent[1]).rsplit('.', 1)[-1]):
                    symbols.add(package[1] + '.' + name)
    return symbols


def check(root: Path, only: str | None = None) -> list[str]:
    errors = []
    features = sorted((root / 'feature').iterdir()) if (root / 'feature').exists() else []
    implementations = use_case_implementations(root, features)
    implementation_access = re.compile(r'\b(?:' + '|'.join(re.escape(name) for name in sorted(implementations)) + r')\b') if implementations else None
    for feature in features:
        if not feature.is_dir() or (only and feature.name != only):
            continue
        base = 'br.gov.caixa.loterias.feature.' + feature.name.replace('-', '')
        for source_set, layer in [('domain', 'domain'), ('api', 'domain'), ('data', 'data'), ('transport', 'data'), ('platform', 'data'), ('main', 'presentation'), ('host', 'presentation')]:
            for language in ('java', 'kotlin'):
                folders = [feature / 'src' / source_set / language]
                if source_set in ('domain', 'api', 'data', 'transport'):
                    folders.append(feature / source_set / 'src' / 'main' / language)
                if source_set in ('main', 'host'):
                    folders.append(feature / 'presentation' / 'src' / source_set / language)
                for folder in folders:
                    for source in sorted(folder.rglob('*.kt')) if folder.exists() else []:
                        text = source.read_text()
                        code = kotlin_code(text)
                        rel = source.relative_to(root)
                        match = re.search(r'^package\s+([\w.]+)', text, re.M)
                        package = match[1] if match else ''
                        if package == 'br.gov.caixa.loterias.apostas' or package.startswith('br.gov.caixa.loterias.apostas.'):
                            errors.append(f'{rel}: feature source cannot use the retired APP namespace')
                        if re.search(r'(?m)^\s*(?:public\s+)?typealias\s+', text):
                            errors.append(f'{rel}: feature source cannot publish public compatibility aliases')
                        if re.search(r'@(?:kotlin\.)?Deprecated\b', text):
                            errors.append(f'{rel}: feature source cannot declare deprecated APIs')
                        if re.search(r'private val implementation = ' + re.escape(base) + r'\.domain\.usecase\.', text):
                            errors.append(f'{rel}: use-case compatibility facade must use its existing implementation directly')
                        if package == base + '.presentation.host' or package.startswith(base + '.presentation.host.'):
                            errors.append(f'{rel}: feature source cannot restore the retired presentation host namespace')
                        if re.search(r'(?m)^\s*(?:public\s+)?val\s+(\w+)\s+get\(\)\s*=\s*' + re.escape(base) + r'\.domain\.\1\b', text):
                            errors.append(f'{rel}: feature source cannot publish a public object compatibility alias')
                        integration = layer == 'presentation' and (package == base + '.navigation' or package.startswith(base + '.navigation.') or package == base + '.di' or package.startswith(base + '.di.'))
                        if not integration and not package.startswith(base + '.' + layer + '.') and package != base + '.' + layer:
                            errors.append(f'{rel}: expected {base}.{layer} ownership, got {package}')
                        if package != '.'.join(source.relative_to(folder).parts[:-1]):
                            errors.append(f'{rel}: package does not match source path')
                        viewmodel = is_viewmodel(source, package, base, code)
                        imports = re.findall(r'^import\s+([\w.*]+)', text, re.M)
                        for imported in imports:
                            if layer == 'domain' and DOMAIN_OUTER.match(imported):
                                errors.append(f'{rel}: Domain imports outer layer {imported}')
                            approved_ticket_fixture = (
                                str(rel) == 'feature/tickets/presentation/src/main/java/br/gov/caixa/loterias/feature/tickets/navigation/TicketsNavigationDependenciesAdapter.kt'
                                and imported == 'br.gov.caixa.loterias.feature.tickets.data.fixture.mockConfirmedBet'
                            )
                            if layer == 'presentation' and not package.startswith(base + '.di') and PRESENTATION_OUTER.match(imported) and not approved_ticket_fixture:
                                errors.append(f'{rel}: Presentation imports infrastructure {imported}')
                            if viewmodel and (VM_OUTER.match(imported) or imported.endswith('Repository')):
                                errors.append(f'{rel}: ViewModel imports concrete/platform dependency {imported}')
                        application_access = (USE_CASE_TYPE.search(code) or USE_CASE_PACKAGE.search(code)
                                              or (implementation_access is not None and implementation_access.search(code))
                                              or any(symbol.rsplit('.', 1)[0] + '.*' in imports and re.search(r'\b' + re.escape(symbol.rsplit('.', 1)[-1]) + r'\b', code) for symbol in implementations))
                        composable_names = {'Composable'} | set(re.findall(r'^import\s+androidx\.compose\.runtime\.Composable\s+as\s+(\w+)', code, re.M))
                        composable = any(re.search(r'@(?:androidx\.compose\.runtime\.)?' + re.escape(name) + r'\b', code) for name in composable_names)
                        navigation = package == base + '.navigation' or package.startswith(base + '.navigation.')
                        if navigation and (composable or re.search(r'\bcomposable\s*\(', code)) and navigation_starts_coroutines(code):
                            errors.append(f'{rel}: Navigation must delegate asynchronous work to a ViewModel or platform adapter')
                        if package.startswith(base + '.di') and di_implements_usecase(code):
                            errors.append(f'{rel}: DI must construct Domain UseCase implementations, not implement application operations inline')
                        if layer == 'presentation' and not integration:
                            if LOCATION_RESOLUTION.search(code):
                                errors.append(f'{rel}: Presentation cannot resolve location/geocoding; use Domain operations backed by Data adapters')
                            if DATA_ACCESS_TYPE.search(code):
                                errors.append(f'{rel}: Presentation must use UseCases, not Repository/DataSource contracts or implementations')
                            view_package = any(package == base + '.presentation.' + name or package.startswith(base + '.presentation.' + name + '.') for name in ('route', 'screen', 'components'))
                            if not viewmodel and (view_package or composable) and application_access:
                                errors.append(f'{rel}: View must use ViewModel state/actions, not invoke UseCases directly')
                        if viewmodel and re.search(
                            r'\bbr\.gov\.caixa\.loterias\.feature\.\w+\.presentation\.(?:route|screen|components)\.', code
                        ):
                            errors.append(f'{rel}: ViewModel cannot depend on View implementations; use pure state/coordinators')
                        coordinator = layer == 'presentation' and (
                            package == base + '.presentation.coordinator'
                            or package.startswith(base + '.presentation.coordinator.')
                            or source.stem.endswith('Coordinator')
                        )
                        if coordinator and re.search(
                            r'\bbr\.gov\.caixa\.loterias\.feature\.\w+\.presentation\.(?:route|screen|components)\.', code
                        ):
                            errors.append(f'{rel}: Coordinator cannot depend on View implementations; use pure state/Domain policies')
                        if viewmodel and re.search(r'\bandroidx\.compose\.', code):
                            errors.append(f'{rel}: ViewModel cannot depend on Compose; keep UI interactions in the View')
                        if layer == 'data':
                            if DATA_TO_PRESENTATION.search(code):
                                errors.append(f'{rel}: Data cannot depend on Presentation')
                            if application_access:
                                errors.append(f'{rel}: Data must implement data contracts; application UseCases belong to Domain')
                        if layer == 'domain' and DATASOURCE_TYPE.search(code):
                            errors.append(f'{rel}: DataSource belongs to Data; Domain accesses storage through Repository contracts')
                        for kind, declared in re.findall(r'\b(class|object|interface)\s+(\w+)', code):
                            if layer == 'domain' and kind != 'interface' and declared.endswith('RepositoryImpl'):
                                errors.append(f'{rel}: RepositoryImpl belongs to Data; Domain declares Repository contracts')
                            if kind != 'interface' and declared.endswith(('Repository', 'DataSource')):
                                errors.append(f'{rel}: Repository/DataSource implementation must end with Impl: {declared}')
                            if kind == 'interface' and declared.endswith(('RepositoryImpl', 'DataSourceImpl')):
                                errors.append(f'{rel}: Repository/DataSource contract cannot end with Impl: {declared}')
                        qualified_body = re.sub(r'^import[^\n]*', '', text, flags=re.M)
                        if layer == 'presentation' and not package.startswith(base + '.di') and re.search(r'br\.gov\.caixa\.loterias\.(?:core\.(?:data|network|preferences|database|datastore)|feature\.\w+\.data)\.', qualified_body):
                            errors.append(f'{rel}: Presentation has fully qualified infrastructure access')
                        if viewmodel and re.search(r'br\.gov\.caixa\.loterias\.core\.auth\.', qualified_body):
                            errors.append(f'{rel}: ViewModel has fully qualified auth infrastructure access')
                        if layer == 'domain' and re.search(r'\b(?:android|androidx|retrofit2|okhttp3)\.[A-Za-z]', re.sub(r'^import[^\n]*', '', text, flags=re.M)):
                            errors.append(f'{rel}: Domain has fully qualified framework access')
                        if viewmodel and re.search(r'\bandroid\.[A-Za-z]', re.sub(r'^import[^\n]*', '', text, flags=re.M)):
                            errors.append(f'{rel}: ViewModel has fully qualified Android dependency')
                        if layer == 'presentation' and re.search(r'\bMutableStateFlow\b', code):
                            errors.append(f'{rel}: Presentation UiState must use typed ReduxStateStore; writable Flow belongs only to the shared bridge')
                        pilot_reducer = approved_state_reducer(rel, feature.name, package, source.name)
                        if layer == 'presentation' and is_presentation_reducer(source, package, code) and not pilot_reducer:
                            errors.append(f'{rel}: presentation state reducers are prohibited outside feature-owned presentation/state/*Reducer.kt')
                        if pilot_reducer and reducer_has_effects(code, application_access):
                            errors.append(f'{rel}: state reducer must be pure; keep effects in the ViewModel')
                        if pilot_reducer and REDUCER_LOCALIZATION.search(code):
                            errors.append(f'{rel}: Reducer must receive already captured localized text; resolve locale and resources before dispatch')
    return errors


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parent.parent)
    parser.add_argument('--feature')
    args = parser.parse_args()
    errors = check(args.root, args.feature)
    if errors:
        print('\n'.join(errors), file=sys.stderr)
        print(f'Feature architecture rejected {len(errors)} violations.', file=sys.stderr)
        sys.exit(1)
    print('Feature ownership and layer boundaries verified.')
