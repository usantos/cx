"""Host policy tests use isolated trees, never mutate production sources."""
import importlib.util
import json
from pathlib import Path
from tempfile import TemporaryDirectory
import unittest

spec = importlib.util.spec_from_file_location('app_architecture', Path(__file__).with_name('check-app-architecture.py'))
policy = importlib.util.module_from_spec(spec)
spec.loader.exec_module(policy)
BASE = 'br.gov.caixa.loterias.apostas'


class AppArchitectureTest(unittest.TestCase):
    def errors(self, package, body, name='Example.kt', compatibility=False):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            rel = Path('app/src/main/java') / package.replace('.', '/') / name
            path = root / rel
            path.parent.mkdir(parents=True)
            path.write_text(f'package {package}\n{body}\n')
            if compatibility:
                (root / 'config').mkdir()
                (root / 'config/app-compatibility.json').write_text(json.dumps({str(rel): {'kind': 'facade'}}))
            return policy.check(root)

    def test_flat_host_implementation_is_rejected(self):
        self.assertTrue(self.errors(BASE, 'class UnexpectedCoordinator'))

    def test_domain_rejects_android_and_concrete_auth(self):
        for dependency in ['android.content.Context', 'br.gov.caixa.loterias.core.auth.SessionTokenStore']:
            self.assertTrue(self.errors(BASE + '.domain.session', f'import {dependency}'))

    def test_domain_rejects_qualified_data_access(self):
        self.assertTrue(self.errors(BASE + '.domain.session', 'val value = br.gov.caixa.loterias.apostas.data.SessionStore()'))

    def test_viewmodel_rejects_platform_and_navigation(self):
        for dependency in ['android.app.Activity', 'androidx.navigation.NavController', BASE + '.AppContainer']:
            self.assertTrue(self.errors(BASE + '.presentation.session.viewmodel', f'import {dependency}\nclass SessionViewModel', 'SessionViewModel.kt'))

    def test_app_cannot_own_viewmodels_in_runtime_or_renamed_files(self):
        cases = [
            ('.runtime.update', 'ForceUpdateViewModel.kt', 'class ForceUpdateViewModel : androidx.lifecycle.ViewModel()'),
            ('.runtime.update', 'UpdateOwner.kt', 'import androidx.lifecycle.ViewModel as Owner\nclass UpdateOwner : Owner()'),
            ('.initialization.startup', 'Startup.kt', 'import androidx.lifecycle.ViewModel\nclass Startup : ViewModel()'),
            ('.presentation.session.viewmodel', 'SessionViewModel.kt', 'class SessionViewModel'),
        ]
        for package, name, body in cases:
            with self.subTest(package=package, name=name):
                errors = self.errors(BASE + package, body, name)
                self.assertTrue(any('App cannot own ViewModels' in e for e in errors), errors)

    def test_app_cannot_own_remote_config_client_in_any_layer(self):
        for body in ['import com.google.firebase.remoteconfig.FirebaseRemoteConfig\nclass ConfigClient',
                     'class ConfigClient { val remote = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance() }']:
            errors = self.errors(BASE + '.runtime.update', body, 'ConfigClient.kt')
            self.assertTrue(any('Remote Config client belongs to Data' in e for e in errors), errors)

    def test_startup_cannot_construct_feature_data_adapter(self):
        errors = self.errors(BASE + '.composition.startup',
            'import br.gov.caixa.loterias.feature.onboarding.data.repository.FirebaseForceUpdateRepositoryImpl\n'
            '@Composable fun ForceUpdateGate() { FirebaseForceUpdateRepositoryImpl() }')
        self.assertTrue(any('App cannot access feature Data' in e for e in errors), errors)

    def test_startup_composition_can_construct_owned_feature_dependencies(self):
        self.assertEqual([], self.errors(BASE + '.composition.startup',
            'import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.ForceUpdateViewModel\n'
            'import br.gov.caixa.loterias.feature.onboarding.di.createForceUpdateViewModel\n'
            '@Composable fun ForceUpdateGate() { viewModel { ForceUpdateViewModel() } }'))

    def test_ui_cannot_execute_repository_or_business_coroutines(self):
        for body in ['import br.gov.caixa.loterias.core.domain.configuration.AppConfigurationRepository', 'fun route() { rememberCoroutineScope().launch { load() } }']:
            self.assertTrue(self.errors(BASE + '.presentation.campaign.route', body))

    def test_mutable_stateflow_must_be_private(self):
        self.assertTrue(self.errors(BASE + '.presentation.session.viewmodel', 'class SessionViewModel { val state = MutableStateFlow(false) }', 'SessionViewModel.kt'))

    def test_presentation_can_use_domain_state_and_dispatch_actions(self):
        self.assertEqual([], self.errors(BASE + '.navigation.session', 'import br.gov.caixa.loterias.apostas.domain.session.VerifySession\nfun route() { LaunchedEffect(Unit) { viewModel.onResume() } }'))

    def test_android_adapter_can_use_framework(self):
        self.assertEqual([], self.errors(BASE + '.initialization.startup', 'import android.app.Activity\nclass QueueAdapter'))

    def test_startup_composition_can_render_host_specific_status_ui(self):
        self.assertEqual([], self.errors(BASE + '.composition.startup', '@Composable fun StartupStatusPanel() {}'))

    def test_root_navigation_composition_can_own_typed_graph_registry(self):
        self.assertEqual(
            [],
            self.errors(
                BASE + '.composition.navigation',
                'class AppFeatureGraphDependencies',
                'AppFeatureGraphDependencies.kt',
            ),
        )

    def test_other_nested_composition_packages_remain_reserved(self):
        self.assertTrue(self.errors(BASE + '.composition.session', '@Composable fun NestedCoordinator() {}'))

    def test_app_di_cannot_be_nested_under_composition(self):
        self.assertTrue(self.errors(BASE + '.composition.di', '@Module class DuplicateBindings'))

    def test_domain_can_use_feature_domain_contract(self):
        self.assertTrue(self.errors(BASE + '.domain.session', 'import br.gov.caixa.loterias.feature.account.domain.VerifyPostLoginAccessUseCase'))

    def test_inventory_cannot_authorize_another_compatibility_facade(self):
        errors = self.errors(BASE, 'class LegacyFacade', 'LegacyFacade.kt', True)
        self.assertTrue(any('only enumerate Android entry points' in error for error in errors), errors)

    def test_android_entry_inventory_remains_supported(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            rel = 'app/src/main/java/br/gov/caixa/loterias/apostas/push/LoteriasMessagingService.kt'
            source = root / rel
            source.parent.mkdir(parents=True)
            source.write_text(f'package {BASE}.push\nclass LoteriasMessagingService')
            (root / 'config').mkdir()
            (root / 'config/app-compatibility.json').write_text(json.dumps({rel: {'kind': 'android-entry'}}))
            self.assertEqual([], policy.check(root))

    def test_retired_layer_facades_are_rejected_even_with_renamed_files(self):
        for package, declaration in [
            (BASE + '.di', 'object AppConfigurationBindings'),
            (BASE + '.di', 'object AppSessionBindings'),
            (BASE + '.di', 'object NetworkModule'),
            (BASE + '.di', 'object ConfigurationModule'),
            (BASE + '.di', 'object HomePreferencesModule'),
            (BASE + '.di', 'object AppCoreDataCompositionModule'),
            (BASE + '.navigation', 'class AppFeatureNavigationDependencies'),
        ]:
            with self.subTest(declaration=declaration):
                errors = self.errors(package, declaration, 'RenamedFacade.kt')
                self.assertTrue(any('retired host facade' in error for error in errors), errors)

    def test_retired_layer_forwarders_are_rejected_even_with_renamed_files(self):
        for package, name in [
            (BASE + '.composition.startup', 'FilaBraQueueScreen'),
            (BASE + '.navigation.route', 'prizeRedirectRoute'),
            (BASE + '.navigation.route', 'serviceRedirectRoute'),
        ]:
            with self.subTest(name=name):
                errors = self.errors(package, f'fun {name}() = Unit', 'RenamedForwarder.kt')
                self.assertTrue(any('retired host facade' in error for error in errors), errors)

    def test_retired_flat_facade_functions_are_rejected_even_if_inventoried(self):
        for declaration in ['fun LoteriasApp() = Unit', 'fun FilaBraQueueScreen() = Unit',
                            'class EnvironmentSettings', 'class FirebaseRemotePublicKeySetSource']:
            with self.subTest(declaration=declaration):
                errors = self.errors(BASE, declaration, 'RenamedFacade.kt', True)
                self.assertTrue(any('retired host facade' in error for error in errors), errors)

    def test_wiring_cannot_hide_business_scope_in_composition(self):
        self.assertTrue(self.errors(BASE + '.composition.session', '@Composable fun route() { rememberCoroutineScope().launch { verify() } }'))

    def test_compatibility_facade_cannot_hide_business_scope(self):
        self.assertTrue(self.errors(BASE, 'class LegacyFacade { val scope = CoroutineScope() }', 'LegacyFacade.kt', True))

    def test_domain_rejects_qualified_auth_and_root_container(self):
        self.assertTrue(self.errors(BASE + '.domain.session', 'val tokens = br.gov.caixa.loterias.core.auth.SessionTokenStore()'))
        self.assertTrue(self.errors(BASE + '.domain.session', 'import ' + BASE + '.AppContainer'))

    def test_compatibility_di_cannot_become_another_provider(self):
        with TemporaryDirectory() as temp:
            root = Path(temp); rel = 'app/src/main/java/br/gov/caixa/loterias/apostas/di/LegacyModule.kt'
            source = root / rel; source.parent.mkdir(parents=True)
            source.write_text('package ' + BASE + '.di\n@Provides fun duplicate() = 1')
            (root / 'config').mkdir()
            (root / 'config/app-compatibility.json').write_text(json.dumps({rel: {'kind': 'di-facade'}}))
            self.assertTrue(policy.check(root))

    def test_release_source_is_not_excluded(self):
        with TemporaryDirectory() as temp:
            root = Path(temp); source = root / 'app/src/release/java/br/gov/caixa/loterias/apostas/Unexpected.kt'
            source.parent.mkdir(parents=True); source.write_text('package ' + BASE + '\nclass Unexpected')
            self.assertTrue(policy.check(root))

    def test_app_cannot_own_feature_or_shared_implementation_layers(self):
        for layer in ['domain', 'data', 'presentation', 'integration']:
            self.assertTrue(self.errors(BASE + '.' + layer + '.probe', 'class Unexpected'))

    def test_app_cannot_own_aggregate_remote_data_sources(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            package = BASE + '.composition.compatibility'
            source = root / 'app/src/main/java' / package.replace('.', '/') / 'LegacyRemoteDataSources.kt'
            source.parent.mkdir(parents=True)
            source.write_text('package ' + package + '\nclass LegacyRemoteDataSources')
            errors = policy.check(root)
            self.assertTrue(any('aggregate remote data source' in error for error in errors), errors)

    def test_shared_core_cannot_import_application_implementation(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            source = root / 'core/security/src/main/java/br/gov/caixa/loterias/core/security/Unexpected.kt'
            source.parent.mkdir(parents=True)
            source.write_text('package br.gov.caixa.loterias.core.security\nimport ' + BASE + '.BuildConfig\nclass Unexpected')
            self.assertTrue(policy.check(root))

    def test_preserved_old_package_in_core_requires_explicit_owner(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            source = root / 'core/domain/src/main/kotlin/br/gov/caixa/loterias/apostas/Unexpected.kt'
            source.parent.mkdir(parents=True)
            source.write_text('package ' + BASE + '\nclass Unexpected')
            self.assertTrue(policy.check(root))

    def test_only_network_can_use_actual_transport_dto_contracts(self):
        for owner, layer, declaration, allowed in [
            ('network', 'transport', 'data class SupportDto(val id: Int)', True),
            ('network', 'data', 'data class SupportDto(val id: Int)', False),
            ('network', 'transport', 'class SupportRepositoryImpl', False),
            ('common', 'transport', 'data class SupportDto(val id: Int)', False),
        ]:
            with self.subTest(owner=owner, layer=layer, declaration=declaration), TemporaryDirectory() as temp:
                root = Path(temp)
                dto_package = 'br.gov.caixa.loterias.feature.support.data.dto'
                contract = root / 'feature/support' / layer / 'src/main/kotlin' / dto_package.replace('.', '/') / 'SupportDto.kt'
                contract.parent.mkdir(parents=True)
                contract.write_text('package ' + dto_package + '\n' + declaration)
                imported = 'SupportRepositoryImpl' if 'RepositoryImpl' in declaration else 'SupportDto'
                source = root / 'core' / owner / 'src/main/kotlin/br/gov/caixa/loterias/core' / owner / 'Client.kt'
                source.parent.mkdir(parents=True)
                source.write_text('package br.gov.caixa.loterias.core.' + owner + '\nimport ' + dto_package + '.' + imported)
                errors = policy.check(root)
                if allowed:
                    self.assertEqual([], errors)
                else:
                    self.assertTrue(any('shared CORE cannot depend on feature implementation' in error for error in errors), errors)

    def test_core_cannot_depend_on_feature_implementation(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            source = root / 'core/security/src/main/java/br/gov/caixa/loterias/core/security/Unexpected.kt'
            source.parent.mkdir(parents=True)
            source.write_text('package br.gov.caixa.loterias.core.security\nimport br.gov.caixa.loterias.feature.home.presentation.screen.HomeScreen')
            self.assertTrue(policy.check(root))

    def test_core_viewmodels_cannot_import_repositories_or_context(self):
        for dependency in ['android.content.Context', 'br.gov.caixa.loterias.core.domain.configuration.AppConfigurationRepository']:
            with TemporaryDirectory() as temp:
                root = Path(temp)
                source = root / 'core/presentation/src/main/java/br/gov/caixa/loterias/core/presentation/UnexpectedViewModel.kt'
                source.parent.mkdir(parents=True)
                source.write_text('package br.gov.caixa.loterias.core.presentation\nimport ' + dependency + '\nclass UnexpectedViewModel')
                self.assertTrue(policy.check(root))

    def test_active_app_container_reference_is_rejected(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            source = root / 'app/src/main/java/br/gov/caixa/loterias/apostas/navigation/AppNavHost.kt'
            source.parent.mkdir(parents=True)
            source.write_text('package br.gov.caixa.loterias.apostas.navigation\nfun appNavHost(container: AppContainer) = container')
            self.assertTrue(policy.check(root))

    def test_retired_host_facades_cannot_return_through_compatibility_inventory(self):
        for package, name, declaration in [
            (BASE, 'AppContainer.kt', 'class AppContainer'),
            (BASE + '.di', 'ApplicationModule.kt', 'object ApplicationModule'),
        ]:
            with self.subTest(name=name):
                errors = self.errors(package, declaration, name, True)
                self.assertTrue(any('retired host facade' in error for error in errors), errors)

    def test_initialization_bindings_are_the_single_hilt_module(self):
        self.assertTrue(
            self.errors(
                BASE + '.di',
                'object AppInitializationBindings',
                'AppInitializationBindings.kt',
            ),
        )

    def test_app_source_rejects_deprecated_apis(self):
        self.assertTrue(self.errors(BASE + '.runtime', '@Deprecated("legacy") class OldRuntime'))

    def test_active_navigation_uses_composition_graph_registry(self):
        self.assertTrue(
            self.errors(
                BASE + '.navigation',
                'fun appNavHost(dependencies: AppFeatureNavigationDependencies) = dependencies',
                'AppNavHost.kt',
            ),
        )

    def test_app_cannot_depend_on_feature_data(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            gradle = root / 'app/build.gradle'
            gradle.parent.mkdir(parents=True)
            gradle.write_text("implementation project(':feature:account:data')")
            self.assertTrue(policy.check(root))

    def test_app_unit_tests_may_depend_on_feature_data_fixtures(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            gradle = root / 'app/build.gradle'
            gradle.parent.mkdir(parents=True)
            gradle.write_text("testImplementation project(':feature:account:data')")
            self.assertEqual([], policy.check(root))

    def test_app_di_cannot_bind_feature_repository_or_usecase(self):
        for provided_type in ['AccountRepository', 'GetAccountUseCase']:
            with TemporaryDirectory() as temp:
                root = Path(temp)
                source = root / 'app/src/main/java/br/gov/caixa/loterias/apostas/di/FeatureBindings.kt'
                source.parent.mkdir(parents=True)
                source.write_text('package br.gov.caixa.loterias.apostas.di\n@Provides fun bind(): br.gov.caixa.loterias.feature.account.domain.' + provided_type + ' = TODO()')
                self.assertTrue(policy.check(root))

    def test_app_di_cannot_bind_core_data_implementations(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            source = root / 'app/src/main/java/br/gov/caixa/loterias/apostas/di/DataBindings.kt'
            source.parent.mkdir(parents=True)
            source.write_text('''package br.gov.caixa.loterias.apostas.di
import br.gov.caixa.loterias.core.data.AppConfigurationRepositoryImpl
@Provides fun repository(): br.gov.caixa.loterias.core.domain.configuration.AppConfigurationRepository = AppConfigurationRepositoryImpl(TODO())
''')
            errors = policy.check(root)
            self.assertTrue(any('core data implementations' in error for error in errors), errors)

    def test_app_di_cannot_bind_account_authentication(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            source = root / 'app/src/main/java/br/gov/caixa/loterias/apostas/di/SessionBindings.kt'
            source.parent.mkdir(parents=True)
            source.write_text('''package br.gov.caixa.loterias.apostas.di
import br.gov.caixa.loterias.core.auth.AppAuthAuthenticationGateway
@Provides fun authentication(): AppAuthAuthenticationGateway = TODO()
''')
            errors = policy.check(root)
            self.assertTrue(any('account authentication providers' in error for error in errors), errors)

    def test_core_data_cannot_coordinate_application_use_cases(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            source = root / 'core/data/src/main/java/br/gov/caixa/loterias/core/data/configuration/Coordinator.kt'
            source.parent.mkdir(parents=True)
            source.write_text('package br.gov.caixa.loterias.core.data.configuration\nimport br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase\nclass Coordinator')
            self.assertTrue(any('CORE Data cannot coordinate UseCases' in error for error in policy.check(root)))

    def test_core_composable_cannot_coordinate_domain_loads(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            source = root / 'core/navigation/src/main/java/br/gov/caixa/loterias/core/navigation/state/Snapshot.kt'
            source.parent.mkdir(parents=True)
            source.write_text('package br.gov.caixa.loterias.core.navigation.state\n'
                              'import androidx.compose.runtime.Composable\n'
                              'import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase\n'
                              '@Composable fun snapshot(load: GetCurrentCartUseCase) { LaunchedEffect(load) { withContext(IO) { load() } } }')
            self.assertTrue(any('CORE View cannot coordinate domain loads' in error for error in policy.check(root)))

    def test_core_data_di_can_wire_domain_use_cases(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            source = root / 'core/data/src/main/java/br/gov/caixa/loterias/core/data/di/Bindings.kt'
            source.parent.mkdir(parents=True)
            source.write_text('package br.gov.caixa.loterias.core.data.di\nimport br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase\n@Module object Bindings')
            self.assertEqual([], policy.check(root))

    def test_core_viewmodel_cannot_import_repository_package_capability(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            source = root / 'core/presentation/src/main/java/br/gov/caixa/loterias/core/presentation/campaign/CampaignViewModel.kt'
            source.parent.mkdir(parents=True)
            source.write_text('package br.gov.caixa.loterias.core.presentation.campaign\nimport br.gov.caixa.loterias.core.domain.configuration.repository.CampaignConfiguration\nclass CampaignViewModel')
            self.assertTrue(any('ViewModel must consume domain operations' in error for error in policy.check(root)))

    def test_app_di_cannot_bind_nested_account_authentication(self):
        errors = self.errors(BASE + '.di',
                             'import br.gov.caixa.loterias.core.auth.gateway.AppAuthAuthenticationGateway\n'
                             '@Provides fun authentication(): AppAuthAuthenticationGateway = TODO()')
        self.assertTrue(any('account authentication providers' in error for error in errors), errors)

    def test_app_di_cannot_construct_core_network_api_providers(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            source = root / 'app/src/main/java/br/gov/caixa/loterias/apostas/di/NetworkBindings.kt'
            source.parent.mkdir(parents=True)
            source.write_text('''package br.gov.caixa.loterias.apostas.di
import br.gov.caixa.loterias.core.network.LoteriasApiProvider
@Provides fun apiProvider() = LoteriasApiProvider()
''')
            errors = policy.check(root)
            self.assertTrue(any('API provider construction belongs to core network' in error for error in errors), errors)

    def test_compatibility_facade_must_forward_to_registered_owner(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            rel = Path('app/src/main/java') / BASE.replace('.', '/') / 'LegacyFacade.kt'
            source = root / rel
            source.parent.mkdir(parents=True)
            target = 'br.gov.caixa.loterias.feature.account.presentation.RegisteredOwner'
            source.write_text(f'package {BASE}\nclass LegacyFacade {{ fun delegate() = {target}() }}')
            (root / 'config').mkdir()
            (root / 'config/app-compatibility.json').write_text(json.dumps({str(rel): {'kind': 'facade', 'forwards_to': target}}))
            self.assertTrue(policy.check(root))

    def test_navigation_adapter_must_be_owned_with_its_contract(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            contract = root / 'feature/account/presentation/src/main/java/br/gov/caixa/loterias/feature/account/navigation/AccountNavigationDependencies.kt'
            contract.parent.mkdir(parents=True)
            contract.write_text('package br.gov.caixa.loterias.feature.account.navigation\ninterface AccountNavigationDependencies')
            adapter = root / 'feature/home/presentation/src/main/java/br/gov/caixa/loterias/feature/home/navigation/AccountNavigationDependenciesAdapter.kt'
            adapter.parent.mkdir(parents=True)
            adapter.write_text('package br.gov.caixa.loterias.feature.home.navigation\nclass AccountNavigationDependenciesAdapter : br.gov.caixa.loterias.feature.account.navigation.AccountNavigationDependencies')
            self.assertTrue(policy.check(root))

    def test_core_cannot_restore_app_namespace_through_inventory(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            rel = 'core/network/src/main/java/br/gov/caixa/loterias/apostas/LegacyFactory.kt'
            source = root / rel
            source.parent.mkdir(parents=True)
            source.write_text(f'package {BASE}\nclass LegacyFactory')
            (root / 'config').mkdir()
            (root / 'config/host-public-compatibility.json').write_text(json.dumps({rel: {'package': BASE}}))
            errors = policy.check(root)
            self.assertTrue(any('CORE must use its owning core package' in error for error in errors), errors)

    def test_core_cannot_publish_deprecated_compatibility_api(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            source = root / 'core/common/src/main/java/br/gov/caixa/loterias/core/common/OldApi.kt'
            source.parent.mkdir(parents=True)
            source.write_text('package br.gov.caixa.loterias.core.common\n@Deprecated("Use current API")\nfun oldApi() = Unit')
            errors = policy.check(root)
            self.assertTrue(any('CORE source cannot declare deprecated APIs' in error for error in errors), errors)

    def test_core_cannot_publish_types_in_another_core_modules_package(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            source = root / 'core/network/src/main/java/br/gov/caixa/loterias/core/data/LeakedOwner.kt'
            source.parent.mkdir(parents=True)
            source.write_text('package br.gov.caixa.loterias.core.data\nclass LeakedOwner')
            errors = policy.check(root)
            self.assertTrue(any('CORE must use its owning core package' in error for error in errors), errors)



    def test_app_di_cannot_bind_feature_concrete_repository_or_datasource_impl(self):
        for implementation in ('repository.SupportRepositoryImpl', 'remote.SupportRemoteDataSourceImpl'):
            with self.subTest(implementation=implementation), TemporaryDirectory() as temp:
                root = Path(temp)
                source = root / 'app/src/main/java/br/gov/caixa/loterias/apostas/di/FeatureBindings.kt'
                source.parent.mkdir(parents=True)
                source.write_text('package br.gov.caixa.loterias.apostas.di\n@Provides fun bind() = br.gov.caixa.loterias.feature.support.data.' + implementation + '(TODO())')
                self.assertTrue(policy.check(root))


if __name__ == '__main__':
    unittest.main()
