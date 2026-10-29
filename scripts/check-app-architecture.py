#!/usr/bin/env python3
"""Validate application host ownership, layer direction and MVVM boundaries."""
from pathlib import Path
import argparse
import json
import re
import sys

BASE = 'br.gov.caixa.loterias.apostas'
LAYERS = {'composition', 'navigation', 'di', 'environment', 'initialization', 'runtime', 'theme'}
RETIRED_HOST_TYPES = {
    BASE + '.' + name for name in ('AppContainer', 'EnvironmentSettings', 'FirebaseRemotePublicKeySetSource')
} | {
    BASE + '.di.' + name for name in ('ApplicationModule', 'AppConfigurationBindings', 'AppSessionBindings',
                                     'HomePreferencesModule', 'ConfigurationModule', 'NetworkModule',
                                     'AppCoreDataCompositionModule')
} | {BASE + '.navigation.AppFeatureNavigationDependencies'}
RETIRED_ROOT_FUNCTIONS = {'LoteriasApp', 'FilaBraAppGate', 'FilaBraQueueScreen', 'mockStartDestination', 'startupDestination'}
RETIRED_LAYER_FUNCTIONS = {
    BASE + '.composition.startup.FilaBraQueueScreen',
    BASE + '.navigation.route.prizeRedirectRoute',
    BASE + '.navigation.route.serviceRedirectRoute',
}
DOMAIN_OUTER = re.compile(r'^(?:android(?:x)?\.|retrofit2\.|okhttp3\.|br\.gov\.caixa\.loterias\.core\.(?:auth|data|network|preferences|database|datastore|queue|navigation|presentation|designsystem)\.|br\.gov\.caixa\.loterias\.apostas\.(?:data|integration|composition|presentation|navigation)\.|br\.gov\.caixa\.loterias\.feature\.\w+\.(?:data|presentation)\.)')
PRESENTATION_OUTER = re.compile(r'^(?:br\.gov\.caixa\.loterias\.core\.(?:auth|data|network|preferences|database|datastore|queue)\.|br\.gov\.caixa\.loterias\.feature\.\w+\.data\.|br\.gov\.caixa\.loterias\.apostas\.(?:AppContainer|data|integration|composition)(?:\.|$))')


def _transport_dto_contracts(root: Path) -> set[str]:
    contracts = set()
    for source in (root / 'feature').glob('*/transport/src/main/*/**/*.kt'):
        feature = source.relative_to(root / 'feature').parts[0].replace('-', '')
        expected = 'br.gov.caixa.loterias.feature.' + feature + '.data.dto'
        code = re.sub(r'"""[\s\S]*?"""|"(?:\\.|[^"\\])*"|/\*[\s\S]*?\*/|//[^\n]*', '', source.read_text())
        package = re.search(r'^package\s+([\w.]+)', code, re.M)
        if package and package[1] == expected:
            contracts.update(expected + '.' + name for name in re.findall(r'^data\s+class\s+(\w+Dto)\b', code, re.M))
    return contracts


def check(root: Path) -> list[str]:
    errors = []
    transport_dtos = _transport_dto_contracts(root)
    transport_access = re.compile(r'(?<![\w.])(?:' + '|'.join(re.escape(name) for name in sorted(transport_dtos, key=len, reverse=True)) + r')(?!\w)') if transport_dtos else None
    inventory = root / 'config/app-compatibility.json'
    compatibility = json.loads(inventory.read_text()) if inventory.exists() else {}
    for path, entry in compatibility.items():
        if entry.get('kind') != 'android-entry':
            errors.append(f'{path}: APP compatibility inventory may only enumerate Android entry points')
        if not (root / path).exists():
            errors.append(f'{path}: registered Android entry point is missing')
    registered_owners = _registered_owner_index(root)
    source_texts = {}
    source_packages = {}
    for source_set in ('main', 'debug', 'release'):
        for language in ('java', 'kotlin'):
            folder = root / 'app/src' / source_set / language
            for source in sorted(folder.rglob('*.kt')) if folder.exists() else []:
                text = source.read_text()
                rel = str(source.relative_to(root))
                match = re.search(r'^package\s+([\w.]+)', text, re.M)
                package = match[1] if match else ''
                source_texts[rel] = text
                source_packages[rel] = package
                entry = compatibility.get(rel)
                declared_types = re.findall(r'\b(?:class|object|interface)\s+(\w+)', text)
                retired_type = any(package + '.' + name in RETIRED_HOST_TYPES for name in declared_types)
                retired_function = any(
                    (package == BASE and name in RETIRED_ROOT_FUNCTIONS)
                    or package + '.' + name in RETIRED_LAYER_FUNCTIONS
                    for name in re.findall(r'\bfun\s+(\w+)\s*\(', text)
                )
                if retired_type or retired_function:
                    errors.append(f'{rel}: retired host facade must not be restored; use the owning runtime or bindings')
                if package != '.'.join(source.relative_to(folder).parts[:-1]):
                    errors.append(f'{rel}: package does not match source path')
                if package == BASE + '.composition.di' or package.startswith(BASE + '.composition.di.'):
                    errors.append(f'{rel}: app DI providers must live in the app di layer')
                is_startup_composition = package == BASE + '.composition.startup' or package.startswith(BASE + '.composition.startup.')
                is_navigation_composition = package == BASE + '.composition.navigation'
                if package.startswith(BASE + '.composition.') and not is_startup_composition and not is_navigation_composition and not entry:
                    errors.append(f'{rel}: app composition package is reserved for the root composer')
                if is_navigation_composition and source.name != 'AppFeatureGraphDependencies.kt':
                    errors.append(f'{rel}: composition.navigation is reserved for the typed root graph registry')
                if re.search(r'\bclass\s+(?:RetrofitLoteriasRemoteDataSource|LegacyRemoteDataSources)\b', text):
                    errors.append(f'{rel}: app cannot own an aggregate remote data source')
                is_entry = package == BASE and source.name in ('MainActivity.kt', 'QueueActivity.kt', 'LoteriasApplication.kt')
                layer = package.removeprefix(BASE + '.').split('.')[0] if package.startswith(BASE + '.') else ''
                if not is_entry and not entry and layer not in LAYERS:
                    errors.append(f'{rel}: app implementation must have a layer owner or enumerated compatibility entry')
                if entry:
                    target = entry.get('forwards_to')
                    if target and target not in text:
                        errors.append(f'{rel}: compatibility facade no longer forwards to {target}')
                    if target and target not in registered_owners:
                        errors.append(f'{rel}: compatibility forwarding target is not a registered owner: {target}')
                    if entry.get('kind') in ('facade', 'di-facade') and re.search(r'\b(?:rememberCoroutineScope|withContext|CoroutineScope)\s*\(|\.launch\s*[({]', text):
                        errors.append(f'{rel}: compatibility facade contains asynchronous implementation')
                    if entry.get('kind') == 'di-facade' and '@Provides' in text:
                        errors.append(f'{rel}: compatibility DI must not duplicate actual Hilt providers')
                if re.search(r'@Deprecated\b', text):
                    errors.append(f'{rel}: app source cannot declare deprecated APIs')
                imports = re.findall(r'^import\s+([\w.*]+)', text, re.M)
                body = re.sub(r'^import[^\n]*', '', text, flags=re.M)
                host_code = re.sub(r'"""[\s\S]*?"""|"(?:\\.|[^"\\])*"|/\*[\s\S]*?\*/|//[^\n]*', '', text)
                vm_names = {'ViewModel', 'AndroidViewModel'}
                vm_names.update(re.findall(r'^import\s+androidx\.lifecycle\.(?:Android)?ViewModel\s+as\s+(\w+)', host_code, re.M))
                is_vm = source.name.endswith('ViewModel.kt') or any(
                    re.search(r':\s*(?:androidx\.lifecycle\.)?' + re.escape(name) + r'\s*\(', host_code)
                    for name in vm_names
                )
                if is_vm:
                    errors.append(f'{rel}: App cannot own ViewModels; state and effects belong to feature Presentation')
                if re.search(r'\bbr\.gov\.caixa\.loterias\.feature\.\w+\.data\.', host_code):
                    errors.append(f'{rel}: App cannot access feature Data; use feature-owned composition')
                if re.search(r'\bcom\.google\.firebase\.remoteconfig\.', host_code):
                    errors.append(f'{rel}: Remote Config client belongs to Data; App only composes its dependencies')
                if layer == 'domain':
                    for imported in imports:
                        if DOMAIN_OUTER.match(imported) or imported == BASE + '.AppContainer':
                            errors.append(f'{rel}: Domain imports outer layer {imported}')
                    if re.search(r'\b(?:android|androidx|retrofit2|okhttp3)\.[A-Za-z]|br\.gov\.caixa\.loterias\.(?:apostas\.(?:data|integration|composition|presentation|navigation)|core\.(?:auth|data|network|preferences|database|datastore|queue))\.', body):
                        errors.append(f'{rel}: Domain has qualified outer-layer access')
                if layer in ('composition', 'navigation') and '@Composable' in body and re.search(r'\b(?:rememberCoroutineScope|withContext|CoroutineScope)\s*\(|\.launch\s*[({]', body):
                    errors.append(f'{rel}: host UI wiring must dispatch business work to a ViewModel or platform adapter')
                if layer == 'presentation':
                    for imported in imports:
                        if PRESENTATION_OUTER.match(imported) or (is_vm and (imported.startswith(('android.', 'androidx.navigation.', 'androidx.compose.')) or imported.endswith('Repository'))):
                            errors.append(f'{rel}: presentation imports concrete/platform dependency {imported}')
                    if re.search(r'br\.gov\.caixa\.loterias\.apostas\.(?:data|integration|composition)\.|br\.gov\.caixa\.loterias\.core\.(?:auth|data|network|preferences|database|datastore)\.', body):
                        errors.append(f'{rel}: presentation has qualified infrastructure access')
                    if not is_vm and (any(i.endswith('Repository') for i in imports) or re.search(r'\b(?:rememberCoroutineScope|withContext|CoroutineScope)\s*\(|\.launch\s*[({]', body)):
                        errors.append(f'{rel}: UI must dispatch business work to a ViewModel')
                    if is_vm:
                        if re.search(r'\b(?:android|androidx\.navigation|androidx\.compose)\.[A-Za-z]', body):
                            errors.append(f'{rel}: ViewModel has qualified UI/platform access')
                        pattern = r'((?:(?:private|internal|public|protected|override|open|final|lateinit)\s+)*)\b(?:val|var)\s+\w+\s*(?::[^=\n]+)?=\s*(?:kotlinx\.coroutines\.flow\.)?MutableStateFlow'
                        for declaration in re.finditer(pattern, body):
                            if 'private' not in declaration[1].split():
                                errors.append(f'{rel}: MutableStateFlow must be private')

                if not entry and source.name != 'AppContainer.kt' and re.search(r'\bAppContainer\b', text):
                    errors.append(f'{rel}: active app code cannot reference AppContainer')
                if not entry and re.search(r'\bAppFeatureNavigationDependencies\b', text):
                    errors.append(f'{rel}: active app code must use the composition-owned AppFeatureGraphDependencies')
                if layer == 'di' and re.search(r'@(?:Provides|Binds)\b', body) and re.search(r'br\.gov\.caixa\.loterias\.feature\.\w+\.(?:data|domain)\.[\w.]*\b(?:\w*Repository(?:Impl)?|\w*UseCase(?:Impl)?|\w*DataSource(?:Impl)?)\b', body):
                    errors.append(f'{rel}: app DI cannot provide feature repositories, data sources or use cases')
                if layer == 'di' and re.search(r'@Provides\b', body) and re.search(r'br\.gov\.caixa\.loterias\.core\.data\.(?:(?:Remote|Retrofit)\w+|[\w.]*(?:Repository|DataSource)Impl)\b', text):
                    errors.append(f'{rel}: app DI cannot provide core data implementations')
                if layer == 'di' and re.search(r'@Provides\b', body) and re.search(r'br\.gov\.caixa\.loterias\.core\.auth\.(?:[\w.]+\.)?AppAuthAuthenticationGateway', text):
                    errors.append(f'{rel}: app DI cannot own account authentication providers')
                if layer == 'di' and source.name == 'AppInitializationBindings.kt' and (
                    '@Module' not in text or '@InstallIn(SingletonComponent::class)' not in text
                ):
                    errors.append(f'{rel}: application initialization bindings must be the installed Singleton Hilt module')
                if layer == 'di' and re.search(
                    r'@Provides\b[\s\S]{0,240}?fun\s+\w+\s*\([^)]*\)\s*(?::\s*[\w.]+)?\s*=\s*(?:[\w.]+\.)?\w*ApiProvider\s*\(',
                    body,
                ):
                    errors.append(f'{rel}: API provider construction belongs to core network')

    gradle = root / 'app/build.gradle'
    if gradle.exists():
        for dependency in re.findall(r"(?m)^\s*implementation\s+project\s*\(\s*['\"](:feature:[\w-]+:data)['\"]\s*\)", gradle.read_text()):
            errors.append(f'app/build.gradle: app cannot directly depend on feature Data module {dependency}')

    # CORE/features compilam seus próprios arquivos. Os pacotes JVM legados têm proprietários exatos e enumerados.
    moved_path = root / 'config/host-public-compatibility.json'
    moved = json.loads(moved_path.read_text()) if moved_path.exists() else {}
    for parent in ('core', 'feature'):
        for source in sorted((root / parent).rglob('*.kt')) if (root / parent).exists() else []:
            rel = str(source.relative_to(root))
            if '/build/' in rel or not re.search(r'/src/(?:main|debug|release)/(?:java|kotlin)/', rel):
                continue
            text = source.read_text()
            adapter = re.search(r'\bclass\s+\w+NavigationDependenciesAdapter\b[\s\S]*?:\s*(?:[\w.]+\.)?(\w+NavigationDependencies)\b', text)
            if parent == 'feature' and adapter:
                contract = adapter[1]
                declared_at = None
                for candidate in (root / 'feature').rglob(contract + '.kt'):
                    if '/build/' not in str(candidate) and re.search(r'\binterface\s+' + re.escape(contract) + r'\b', candidate.read_text()):
                        declared_at = candidate
                        break
                if declared_at is None or source.relative_to(root).parts[1] != declared_at.relative_to(root).parts[1]:
                    errors.append(f'{rel}: navigation dependency adapter must live in the feature that declares {contract}')
            package = re.search(r'^package\s+([\w.]+)', text, re.M)
            if package and (package[1] == BASE or package[1].startswith(BASE + '.')) and rel not in moved:
                errors.append(f'{rel}: preserved old package requires an exact physical owner')
            if parent == 'core':
                owner_package = 'br.gov.caixa.loterias.core.' + source.relative_to(root).parts[1].replace('-', '')
                if package and package[1] != owner_package and not package[1].startswith(owner_package + '.'):
                    errors.append(f'{rel}: CORE must use its owning core package {owner_package}')
                if re.search(r'@(?:kotlin\.)?Deprecated\b', text):
                    errors.append(f'{rel}: CORE source cannot declare deprecated APIs')
            code = re.sub(r'(?m)^\s*//[^\n]*', '', text)
            implementation_code = transport_access.sub('', code) if parent == 'core' and source.relative_to(root).parts[1] == 'network' and transport_access else code
            if parent == 'core' and re.search(r'br\.gov\.caixa\.loterias\.feature\.\w+\.(?:presentation|data|navigation|di)\.', implementation_code):
                errors.append(f'{rel}: shared CORE cannot depend on feature implementation')
            if parent == 'core' and source.relative_to(root).parts[1] == 'data' and not (package and re.search(r'\.di(?:\.|$)', package[1])) and re.search(r'\bbr\.gov\.caixa\.loterias\.(?:core\.domain|feature\.\w+\.domain)\.(?:\w+\.)*usecase\.', code):
                errors.append(f'{rel}: CORE Data cannot coordinate UseCases; application operations belong to Domain')
            core_view_code = re.sub(r'"""[\s\S]*?"""|"(?:\\.|[^"\\])*"|/\*[\s\S]*?\*/|//[^\n]*', '', text)
            if (parent == 'core' and re.search(r'@(?:androidx\.compose\.runtime\.)?Composable\b', core_view_code)
                    and re.search(r'\.domain\.(?:\w+\.)*usecase\.', core_view_code)
                    and re.search(r'\b(?:withContext|async)\s*(?:\(|\{)|\.launch\s*(?:\(|\{)', core_view_code)):
                errors.append(f'{rel}: CORE View cannot coordinate domain loads; dispatch to a Redux ViewModel')
            if source.name.endswith('ViewModel.kt'):
                imports = re.findall(r'^import\s+([\w.*]+)', text, re.M)
                if any(i.startswith(('android.', 'androidx.navigation.', 'androidx.compose.', 'br.gov.caixa.loterias.core.auth.')) or i.endswith('Repository') or re.search(r'\.domain\.(?:\w+\.)*repository\.', i) for i in imports):
                    errors.append(f'{rel}: ViewModel must consume domain operations without platform/repository dependencies')

            if rel not in moved and re.search(r'br\.gov\.caixa\.loterias\.apostas\.(?:AppContainer|BuildConfig|MainActivity|EnvironmentSettings|LoteriasApplication|LoteriasApp|composition|navigation|presentation|data|domain|integration|di)(?:\b|\.)', code):
                errors.append(f'{rel}: shared CORE/features cannot depend on APP implementation')
    for path, entry in moved.items():
        source = root / path
        if not source.exists():
            errors.append(f'{path}: registered physical owner is missing')
        elif not re.search(r'^package\s+' + re.escape(entry['package']) + r'$', source.read_text(), re.M):
            errors.append(f'{path}: preserved public package changed')
    return errors


def _registered_owner_index(root: Path) -> set[str]:
    """Index source packages and declarations once for compatibility-target checks."""
    owners: set[str] = set()
    for parent in ('app', 'core', 'feature'):
        source_root = root / parent
        if not source_root.exists():
            continue
        for source in source_root.rglob('*.kt'):
            if any(part in ('build', '.gradle') for part in source.parts):
                continue
            text = source.read_text()
            declared_package = re.search(r'^package\s+([\w.]+)', text, re.M)
            if declared_package:
                package = declared_package[1]
                owners.add(package)
                owners.update(
                    f'{package}.{symbol}'
                    for symbol in re.findall(r'\b(?:class|object|interface|typealias|fun)\s+(\w+)', text)
                )
    return owners


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parent.parent)
    args = parser.parse_args()
    errors = check(args.root)
    if errors:
        print('\n'.join(errors), file=sys.stderr)
        print(f'App architecture rejected {len(errors)} violations.', file=sys.stderr)
        sys.exit(1)
    print('Application host ownership and MVVM boundaries verified.')
