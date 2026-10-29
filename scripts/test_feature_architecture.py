"""Exercise policy decisions with isolated source trees, never changing app sources."""
import importlib.util
from pathlib import Path
from tempfile import TemporaryDirectory
import unittest

spec = importlib.util.spec_from_file_location('feature_architecture', Path(__file__).with_name('check-feature-architecture.py'))
policy = importlib.util.module_from_spec(spec)
spec.loader.exec_module(policy)


class FeatureArchitectureTest(unittest.TestCase):
    def errors(self, source_set, package, body, name='Example.kt'):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            source_root = (root / 'feature/support/presentation/src' / source_set if source_set in ('main', 'host')
                           else root / 'feature/support' / source_set / 'src/main')
            target = source_root / 'kotlin' / package.replace('.', '/') / name
            target.parent.mkdir(parents=True)
            target.write_text(f'package {package}\n{body}\n')
            return policy.check(root)

    def test_transport_cannot_use_core_namespace_even_with_old_inventory(self):
        import json
        with TemporaryDirectory() as temp:
            root = Path(temp)
            rel = 'feature/support/transport/src/main/kotlin/br/gov/caixa/loterias/core/network/SupportDto.kt'
            source = root / rel
            source.parent.mkdir(parents=True)
            source.write_text('package br.gov.caixa.loterias.core.network\ndata class SupportDto(val id: Int)\n')
            (root / 'config').mkdir()
            (root / 'config/feature-transport-compatibility.json').write_text(json.dumps([rel]))
            errors = policy.check(root)
            self.assertTrue(any('expected br.gov.caixa.loterias.feature.support.data ownership' in error for error in errors), errors)

    def test_transport_can_publish_feature_owned_dtos(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            source = root / 'feature/support/transport/src/main/kotlin/br/gov/caixa/loterias/feature/support/data/dto/SupportDto.kt'
            source.parent.mkdir(parents=True)
            source.write_text('package br.gov.caixa.loterias.feature.support.data.dto\ndata class SupportDto(val id: Int)\n')
            self.assertEqual([], policy.check(root))

    def test_domain_rejects_repository_implementation(self):
        errors = self.errors('domain', 'br.gov.caixa.loterias.feature.support.domain.repository',
                             'class SupportRepositoryImpl : SupportRepository')
        self.assertTrue(any('RepositoryImpl belongs to Data' in error for error in errors), errors)

    def test_domain_rejects_android_in_relocated_root(self):
        self.assertTrue(any('Domain imports outer' in e for e in self.errors('domain', 'br.gov.caixa.loterias.feature.support.domain', 'import android.content.Context')))

    def test_domain_rejects_qualified_framework_access(self):
        self.assertTrue(any('fully qualified framework' in e for e in self.errors('domain', 'br.gov.caixa.loterias.feature.support.domain', 'class Rule(val context: android.content.Context)')))

    def test_presentation_rejects_feature_owned_data(self):
        self.assertTrue(any('Presentation imports infrastructure' in e for e in self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.screen', 'import br.gov.caixa.loterias.feature.support.data.SupportRepositoryImpl')))

    def test_fully_qualified_infrastructure_cannot_bypass_presentation_boundary(self):
        errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.screen', 'class Screen { val repository = br.gov.caixa.loterias.core.data.SupportRepositoryImpl() }')
        self.assertTrue(any('fully qualified infrastructure' in e for e in errors))

    def test_remote_data_can_use_transport_and_shared_domain(self):
        self.assertEqual([], self.errors('data', 'br.gov.caixa.loterias.feature.support.data.remote', 'import br.gov.caixa.loterias.core.network.LoteriasApiProvider\nimport br.gov.caixa.loterias.feature.support.domain.SupportSection'))

    def test_flat_presentation_is_rejected(self):
        self.assertTrue(any('ownership' in e for e in self.errors('main', 'br.gov.caixa.loterias.feature.support', 'class Screen')))

    def test_viewmodel_rejects_auth_implementation_and_android_results(self):
        errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.viewmodel', 'import br.gov.caixa.loterias.core.auth.AuthenticationGateway\nclass ExampleViewModel { fun complete(result: android.content.Intent?) {} }', 'ExampleViewModel.kt')
        self.assertTrue(any('concrete/platform' in e for e in errors))
        self.assertTrue(any('fully qualified Android' in e for e in errors))

    def test_viewmodel_cannot_retain_view_helpers_even_through_aliases(self):
        for part in ['route', 'screen', 'components']:
            for suffix in ['PrizeReceiptDelivery', '*']:
                body = 'import br.gov.caixa.loterias.feature.support.presentation.' + part + '.' + suffix
                if suffix != '*':
                    body += ' as Delivery'
                errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.viewmodel',
                                     body + '\nclass NoticeViewModel', 'NoticeViewModel.kt')
                self.assertTrue(any('ViewModel cannot depend on View implementations' in e for e in errors), errors)

    def test_viewmodel_cannot_construct_qualified_view_helper(self):
        errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.viewmodel',
            'class NoticeViewModel { val delivery = br.gov.caixa.loterias.feature.support.presentation.route.PrizeReceiptDelivery() }',
            'NoticeViewModel.kt')
        self.assertTrue(any('ViewModel cannot depend on View implementations' in e for e in errors), errors)

    def test_viewmodel_can_use_pure_delivery_state_and_coordinator(self):
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.viewmodel',
            'import br.gov.caixa.loterias.feature.support.presentation.coordinator.ReceiptDelivery\n'
            'import br.gov.caixa.loterias.feature.support.presentation.state.ReceiptDeliveryState\n'
            'class NoticeViewModel', 'NoticeViewModel.kt'))

    def test_view_helper_example_in_comments_or_strings_is_not_dependency(self):
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.viewmodel',
            '// br.gov.caixa.loterias.feature.support.presentation.route.Example\n'
            'class NoticeViewModel { val example = "br.gov.caixa.loterias.feature.support.presentation.route.Example" }',
            'NoticeViewModel.kt'))

    def test_coordinator_rejects_view_imports_aliases_wildcards_and_qualified_access(self):
        for part in ('route', 'screen', 'components'):
            prefix = 'br.gov.caixa.loterias.feature.support.presentation.' + part
            for body in ('import ' + prefix + '.helper as Helper',
                         'import ' + prefix + '.*',
                         'class Loader { val result = ' + prefix + '.helper() }'):
                errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.coordinator', body)
                self.assertTrue(any('Coordinator cannot depend on View implementations' in e for e in errors), errors)

    def test_coordinator_allows_pure_state_domain_and_view_text_examples(self):
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.coordinator',
            'import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState\n'
            'import br.gov.caixa.loterias.feature.support.domain.SupportSection\n'
            '// br.gov.caixa.loterias.feature.support.presentation.route.Example\n'
            'class Loader { val example = "br.gov.caixa.loterias.feature.support.presentation.screen.Example" }'))

    def test_platform_data_can_use_android(self):
        self.assertEqual([], self.errors('platform', 'br.gov.caixa.loterias.feature.support.data.local', 'import android.content.Context'))

    def test_viewmodel_can_use_domain_use_case(self):
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.viewmodel', 'import br.gov.caixa.loterias.feature.support.domain.GetSupportUseCase', 'ExampleViewModel.kt'))

    def test_presentation_rejects_reducer_state_transitions(self):
        errors = self.errors(
            'main',
            'br.gov.caixa.loterias.feature.support.presentation.mapper',
            'internal fun SupportUiState.reduce(action: SupportAction): SupportUiState = copy()',
            'SupportReducer.kt',
        )
        self.assertTrue(any('state reducers are prohibited' in error for error in errors))

    def test_ticket_navigation_fixture_adapter_may_use_the_registered_fixture(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            source = root / 'feature/tickets/presentation/src/main/java/br/gov/caixa/loterias/feature/tickets/navigation/TicketsNavigationDependenciesAdapter.kt'
            source.parent.mkdir(parents=True)
            source.write_text('package br.gov.caixa.loterias.feature.tickets.navigation\nimport br.gov.caixa.loterias.feature.tickets.data.fixture.mockConfirmedBet\nclass TicketsNavigationDependenciesAdapter { fun fixture() = mockConfirmedBet("") }')
            self.assertEqual([], policy.check(root))

    def test_feature_cannot_publish_public_compatibility_alias(self):
        errors = self.errors('domain', 'br.gov.caixa.loterias.feature.support.domain',
                             'typealias LegacySupport = br.gov.caixa.loterias.feature.support.domain.SupportSection')
        self.assertTrue(any('public compatibility aliases' in error for error in errors), errors)

    def test_feature_cannot_publish_deprecated_api(self):
        errors = self.errors('domain', 'br.gov.caixa.loterias.feature.support.domain',
                             '@Deprecated("Use current API")\nclass OldApi')
        self.assertTrue(any('deprecated APIs' in error for error in errors), errors)

    def test_feature_cannot_restore_app_package_through_inventory(self):
        import json
        with TemporaryDirectory() as temp:
            root = Path(temp)
            rel = 'feature/support/presentation/src/main/kotlin/br/gov/caixa/loterias/apostas/OldFacade.kt'
            source = root / rel
            source.parent.mkdir(parents=True)
            source.write_text('package br.gov.caixa.loterias.apostas\nclass OldFacade')
            (root / 'config').mkdir()
            (root / 'config/host-public-compatibility.json').write_text(json.dumps({rel: {'package': 'br.gov.caixa.loterias.apostas'}}))
            errors = policy.check(root)
            self.assertTrue(any('retired APP namespace' in error for error in errors), errors)

    def test_feature_cannot_wrap_its_existing_use_case(self):
        errors = self.errors('domain', 'br.gov.caixa.loterias.feature.support.domain',
                             'class GetSupport { private val implementation = br.gov.caixa.loterias.feature.support.domain.usecase.GetSupport() }')
        self.assertTrue(any('use-case compatibility facade' in error for error in errors), errors)

    def test_internal_row_shape_alias_is_not_a_public_facade(self):
        self.assertEqual([], self.errors('data', 'br.gov.caixa.loterias.feature.support.data.local',
                                        'internal typealias Row = Map<String, String?>'))

    def test_feature_cannot_republish_a_domain_object_through_a_property(self):
        errors = self.errors('domain', 'br.gov.caixa.loterias.feature.support.domain.policy',
                             'val SupportRules get() = br.gov.caixa.loterias.feature.support.domain.SupportRules')
        self.assertTrue(any('public object compatibility alias' in error for error in errors), errors)

    def test_feature_cannot_restore_the_retired_presentation_host_namespace(self):
        errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.host.route',
                             'class OldScreenFacade')
        self.assertTrue(any('retired presentation host namespace' in error for error in errors), errors)

    def test_indentation_cannot_hide_a_public_compatibility_alias(self):
        errors = self.errors('domain', 'br.gov.caixa.loterias.feature.support.domain',
                             '    public typealias OldSupport = br.gov.caixa.loterias.feature.support.domain.SupportSection')
        self.assertTrue(any('public compatibility aliases' in error for error in errors), errors)


    def test_mvvm_view_cannot_import_repository_contract_with_alias(self):
        errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.screen',
                             'import br.gov.caixa.loterias.feature.support.domain.SupportRepository as Store\nclass Help(val store: Store)')
        self.assertTrue(any('Presentation must use UseCases' in e for e in errors), errors)

    def test_mvvm_coordinator_cannot_access_fully_qualified_datasource(self):
        errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.coordinator',
                             'class Help(val source: br.gov.caixa.loterias.feature.support.domain.HelpDataSource)')
        self.assertTrue(any('Presentation must use UseCases' in e for e in errors), errors)

    def test_mvvm_view_cannot_call_usecase_directly(self):
        for name in ('route', 'screen', 'components'):
            with self.subTest(package=name):
                errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.' + name,
                                     'import br.gov.caixa.loterias.feature.support.domain.GetSupportUseCase\nclass Help(val load: GetSupportUseCase)')
                self.assertTrue(any('View must use ViewModel' in e for e in errors), errors)

    def test_mvvm_composable_cannot_bypass_viewmodel_with_qualified_usecase(self):
        errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.other',
                             '@Composable fun Help(load: br.gov.caixa.loterias.feature.support.domain.GetSupportUseCase) {}')
        self.assertTrue(any('View must use ViewModel' in e for e in errors), errors)

    def test_mvvm_renamed_file_and_aliased_supertype_still_identify_viewmodel(self):
        errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.helper',
                             'import androidx.lifecycle.ViewModel as StateHolder\nimport android.content.Context\nclass Help(val context: Context) : StateHolder()', 'Help.kt')
        self.assertTrue(any('concrete/platform' in e for e in errors), errors)

    def test_mvvm_qualified_supertype_still_identifies_viewmodel(self):
        errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.helper',
                             'class Help(val gateway: br.gov.caixa.loterias.core.auth.AuthenticationGateway) : androidx.lifecycle.ViewModel()', 'Help.kt')
        self.assertTrue(any('fully qualified auth' in e for e in errors), errors)

    def test_mvvm_viewmodel_cannot_import_compose_ui(self):
        errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.viewmodel',
                             'import androidx.compose.ui.platform.LocalContext\nclass HelpViewModel : ViewModel()', 'HelpViewModel.kt')
        self.assertTrue(any('ViewModel cannot depend on Compose' in e for e in errors), errors)

    def test_mvvm_data_cannot_depend_on_presentation(self):
        errors = self.errors('data', 'br.gov.caixa.loterias.feature.support.data.remote',
                             'import br.gov.caixa.loterias.feature.support.presentation.viewmodel.SupportViewModel\nclass RemoteHelp(val vm: SupportViewModel)')
        self.assertTrue(any('Data cannot depend on Presentation' in e for e in errors), errors)

    def test_mvvm_data_cannot_implement_application_usecase(self):
        errors = self.errors('data', 'br.gov.caixa.loterias.feature.support.data.remote',
                             'import br.gov.caixa.loterias.feature.support.domain.GetSupportUseCase\nclass RemoteHelp : GetSupportUseCase')
        self.assertTrue(any('Data must implement data contracts' in e for e in errors), errors)

    def test_mvvm_domain_cannot_define_datasource(self):
        errors = self.errors('domain', 'br.gov.caixa.loterias.feature.support.domain',
                             'interface HelpDataSource')
        self.assertTrue(any('DataSource belongs to Data' in e for e in errors), errors)

    def test_mvvm_view_can_use_models_and_viewmodel(self):
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.screen',
                                        'import br.gov.caixa.loterias.feature.support.domain.SupportSection\nimport br.gov.caixa.loterias.feature.support.presentation.viewmodel.SupportViewModel\n@Composable fun Help(vm: SupportViewModel, sections: List<SupportSection>) {}'))

    def test_mvvm_viewmodel_can_have_only_presentation_state(self):
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.viewmodel',
                                        'import androidx.lifecycle.ViewModel\nclass NoticeViewModel : ViewModel() { var visible = false }', 'NoticeViewModel.kt'))

    def test_mvvm_stateless_view_needs_no_empty_layers(self):
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.components',
                                        '@Composable fun Notice(text: String, onClose: () -> Unit) {}'))

    def test_mvvm_di_can_compose_usecase_repository_and_datasource(self):
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.di',
                                        'import br.gov.caixa.loterias.feature.support.domain.SupportRepository\nimport br.gov.caixa.loterias.feature.support.domain.GetSupportUseCase\nimport br.gov.caixa.loterias.feature.support.data.remote.SupportRemoteDataSource\nclass HelpModule'))

    def test_mvvm_role_names_in_comments_and_strings_are_not_dependencies(self):
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.screen',
                                        '// Example SupportRepository HelpDataSource GetSupportUseCase\nval documentation = "SupportRepository HelpDataSource GetSupportUseCase"'))



    def test_mvvm_view_cannot_import_concrete_usecase_without_role_suffix(self):
        errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.screen',
                             'import br.gov.caixa.loterias.feature.support.domain.usecase.LoadHelp\n@Composable fun Help(load: LoadHelp) {}')
        self.assertTrue(any('View must use ViewModel' in e for e in errors), errors)

    def test_mvvm_composable_alias_does_not_hide_view_role(self):
        errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.other',
                             'import androidx.compose.runtime.Composable as UI\nimport br.gov.caixa.loterias.feature.support.domain.GetSupportUseCase\n@UI fun Help(load: GetSupportUseCase) {}')
        self.assertTrue(any('View must use ViewModel' in e for e in errors), errors)

    def test_mvvm_root_domain_usecase_implementation_is_not_a_view_model(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            domain = root / 'feature/support/domain/src/main/kotlin/br/gov/caixa/loterias/feature/support/domain'
            domain.mkdir(parents=True)
            (domain / 'LoadHelp.kt').write_text('package br.gov.caixa.loterias.feature.support.domain\nclass HelpLabel(val text: String)\nclass LoadHelp : GetSupportUseCase { override fun invoke() {} }')
            view = root / 'feature/support/presentation/src/main/kotlin/br/gov/caixa/loterias/feature/support/presentation/screen/Help.kt'
            view.parent.mkdir(parents=True)
            for operation in ('br.gov.caixa.loterias.feature.support.domain.LoadHelp', '*'):
                with self.subTest(operation=operation):
                    imported = operation if operation != '*' else 'br.gov.caixa.loterias.feature.support.domain.*'
                    view.write_text('package br.gov.caixa.loterias.feature.support.presentation.screen\nimport ' + imported + '\n@Composable fun Help(load: LoadHelp) {}')
                    self.assertTrue(any('View must use ViewModel' in e for e in policy.check(root)))
            view.write_text('package br.gov.caixa.loterias.feature.support.presentation.screen\nimport br.gov.caixa.loterias.feature.support.domain.HelpLabel\n@Composable fun Help(label: HelpLabel) {}')
            self.assertEqual([], policy.check(root))

    def test_mvvm_data_cannot_construct_concrete_usecase(self):
        errors = self.errors('data', 'br.gov.caixa.loterias.feature.support.data.remote',
                             'import br.gov.caixa.loterias.feature.support.domain.usecase.LoadHelp\nclass Store(val load: LoadHelp)')
        self.assertTrue(any('Data must implement data contracts' in e for e in errors), errors)



    def test_mvvm_raw_json_ending_with_quote_cannot_hide_following_view_dependency(self):
        body = '\n'.join([
            'val example = ' + '"' * 4 + 'key":"value' + '"' * 4,
            '@Composable fun Help(load: br.gov.caixa.loterias.feature.support.domain.GetSupportUseCase) {}',
            'val label = "ready"',
        ])
        errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.screen', body)
        self.assertTrue(any('View must use ViewModel' in e for e in errors), errors)

    def test_repository_and_datasource_implementations_require_impl_suffix(self):
        for role in ('Repository', 'DataSource'):
            with self.subTest(role=role):
                errors = self.errors('data', 'br.gov.caixa.loterias.feature.support.data.remote',
                                     'class RemoteSupport' + role)
                self.assertTrue(any('implementation must end with Impl' in e for e in errors), errors)

    def test_data_contracts_do_not_use_impl_suffix(self):
        errors = self.errors('data', 'br.gov.caixa.loterias.feature.support.data.remote',
                             'interface SupportRemoteDataSourceImpl')
        self.assertTrue(any('contract cannot end with Impl' in e for e in errors), errors)

    def test_impl_suffix_keeps_contract_and_implementation_distinct(self):
        self.assertEqual([], self.errors('data', 'br.gov.caixa.loterias.feature.support.data.remote',
                                        'interface SupportRemoteDataSource\nclass SupportRemoteDataSourceImpl : SupportRemoteDataSource'))



    def test_mvvm_data_rejects_nested_domain_usecases_without_role_suffix(self):
        for operation in (
            'import br.gov.caixa.loterias.core.domain.push.usecase.RegisterPushDevice\nclass Runtime(val register: RegisterPushDevice)',
            'import br.gov.caixa.loterias.core.domain.push.usecase.RegisterPushDevice as Register\nclass Runtime(val register: Register)',
            'import br.gov.caixa.loterias.core.domain.push.usecase.*\nclass Runtime(val register: RegisterPushDevice)',
            'class Runtime(val register: br.gov.caixa.loterias.core.domain.push.usecase.RegisterPushDevice)',
        ):
            with self.subTest(operation=operation):
                errors = self.errors('data', 'br.gov.caixa.loterias.feature.support.data.remote', operation)
                self.assertTrue(any('Data must implement data contracts' in e for e in errors), errors)

    def test_mvvm_view_rejects_nested_domain_usecase_without_role_suffix(self):
        errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.screen',
                             'import br.gov.caixa.loterias.core.domain.push.usecase.RegisterPushDevice\n@Composable fun Help(register: RegisterPushDevice) {}')
        self.assertTrue(any('View must use ViewModel' in e for e in errors), errors)

    def test_mvvm_di_can_execute_nested_domain_usecase(self):
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.di',
                                        'import br.gov.caixa.loterias.core.domain.push.usecase.RegisterPushDevice\nclass Runtime(val register: RegisterPushDevice)'))

    def test_nested_usecase_mentions_in_comments_and_strings_are_not_dependencies(self):
        self.assertEqual([], self.errors('data', 'br.gov.caixa.loterias.feature.support.data.remote',
                                        '// br.gov.caixa.loterias.core.domain.push.usecase.RegisterPushDevice\nval description = "core.domain.push.usecase.RegisterPushDevice"'))


    def test_presentation_cannot_resolve_geocoder_even_with_alias_or_wildcard(self):
        for operation in (
            'import android.location.Geocoder\nfun resolve(context: Context) = Geocoder(context)',
            'import android.location.Geocoder as Resolver\nfun resolve(context: Context) = Resolver(context)',
            'import android.location.*\nfun resolve(context: Context) = Geocoder(context)',
            'fun resolve(context: Context) = android.location.Geocoder(context)',
        ):
            with self.subTest(operation=operation):
                errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.route', operation)
                self.assertTrue(any('Presentation cannot resolve location/geocoding' in e for e in errors), errors)

    def test_presentation_cannot_read_location_directly(self):
        for operation in (
            'fun locate(manager: LocationManager) = manager.getLastKnownLocation("gps")',
            'fun locate(manager: LocationManager) = manager?.getCurrentLocation("gps", null, executor, callback)',
            'fun country(resolver: Resolver) = resolver.getFromLocation(1.0, 2.0, 1)',
            'fun locate(manager: LocationManager) = manager::getLastKnownLocation',
        ):
            with self.subTest(operation=operation):
                errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.route', operation)
                self.assertTrue(any('Presentation cannot resolve location/geocoding' in e for e in errors), errors)

    def test_presentation_can_check_location_permission_providers_and_open_settings(self):
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.route',
                                        'import android.Manifest\nimport android.location.LocationManager\nimport android.provider.Settings\nfun enabled(manager: LocationManager) = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)\nval permission = Manifest.permission.ACCESS_FINE_LOCATION\nval settings = Settings.ACTION_LOCATION_SOURCE_SETTINGS'))

    def test_location_resolution_belongs_in_data_or_composition(self):
        body = 'import android.location.Geocoder\nfun locate(manager: LocationManager, context: Context) = Geocoder(context).getFromLocation(1.0, 2.0, 1)\nfun last(manager: LocationManager) = manager.getLastKnownLocation("gps")'
        for source_set, package in (
            ('data', 'br.gov.caixa.loterias.feature.support.data.local'),
            ('main', 'br.gov.caixa.loterias.feature.support.di'),
            ('main', 'br.gov.caixa.loterias.feature.support.navigation'),
        ):
            with self.subTest(package=package):
                self.assertEqual([], self.errors(source_set, package, body))

    def test_location_resolution_mentions_in_comments_and_strings_are_not_dependencies(self):
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.route',
                                        '// Geocoder(context).getFromLocation(1.0, 2.0, 1)\nval documentation = "getLastKnownLocation() getCurrentLocation()"'))


    def test_support_pilot_accepts_pure_reducer_and_immutable_models(self):
        body = """import br.gov.caixa.loterias.feature.support.presentation.model.SupportUiState
import br.gov.caixa.loterias.feature.support.presentation.model.SupportAction
import br.gov.caixa.loterias.feature.support.domain.SupportSection
internal data class Transition(val state: SupportUiState, val section: SupportSection)
internal fun reduceSupportState(state: SupportUiState, action: SupportAction): SupportUiState = state.copy()
"""
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.state', body, 'SupportReducer.kt'))

    def test_support_pilot_accepts_existing_reduce_function(self):
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.state',
                                        'fun reduce(state: SupportUiState, action: SupportAction) = state.copy()', 'SupportReducer.kt'))

    def test_reducer_name_variants_cannot_bypass_other_presentation_packages(self):
        for name, body in (
            ('Other.kt', 'fun reduceSupportState(state: SupportUiState) = state.copy()'),
            ('Other.kt', 'fun SupportUiState.reduceSupportState() = copy()'),
            ('OtherReducer.kt', 'class Transition'),
            ('Other.kt', 'object SupportReducer'),
            ('Other.kt', 'fun reduce(state: SupportUiState) = state.copy()'),
        ):
            with self.subTest(name=name, body=body):
                errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.other', body, name)
                self.assertTrue(any('state reducers are prohibited' in e for e in errors), errors)

    def test_pilot_permission_requires_exact_filename_and_package(self):
        for package, name in (
            ('br.gov.caixa.loterias.feature.support.presentation.state', 'Another.kt'),
            ('br.gov.caixa.loterias.feature.support.presentation.state.child', 'SupportReducer.kt'),
            ('br.gov.caixa.loterias.feature.support.presentation.reducer', 'SupportReducer.kt'),
        ):
            with self.subTest(package=package, name=name):
                errors = self.errors('main', package, 'fun reduceSupportState(state: SupportUiState) = state', name)
                self.assertTrue(any('state reducers are prohibited' in e for e in errors), errors)

    def test_pilot_permission_does_not_extend_to_another_feature(self):
        with TemporaryDirectory() as temp:
            root = Path(temp)
            path = root / 'feature/profile/presentation/src/main/kotlin/br/gov/caixa/loterias/feature/profile/presentation/state/SupportReducer.kt'
            path.parent.mkdir(parents=True)
            path.write_text('package br.gov.caixa.loterias.feature.profile.presentation.state\nfun reduceSupportState(state: ProfileUiState) = state')
            self.assertTrue(any('state reducers are prohibited' in e for e in policy.check(root)))

    def test_pilot_reducer_rejects_effect_imports_including_aliases(self):
        for imported in (
            'android.content.Context', 'androidx.compose.runtime.mutableStateOf',
            'kotlinx.coroutines.launch', 'kotlinx.coroutines.flow.MutableStateFlow',
            'br.gov.caixa.loterias.feature.support.domain.GetSupportUseCase',
            'br.gov.caixa.loterias.feature.support.domain.usecase.LoadHelp',
            'br.gov.caixa.loterias.feature.support.domain.SupportRepository',
            'br.gov.caixa.loterias.core.auth.AuthenticationGateway',
            'java.io.File', 'java.nio.file.Files', 'java.net.URL',
            'java.time.Clock', 'java.time.Instant', 'kotlin.time.Clock',
            'kotlin.random.Random', 'kotlin.io.println', 'org.reduxkotlin.createThreadSafeStore',
        ):
            with self.subTest(imported=imported):
                errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.state',
                                     'import ' + imported + ' as HiddenEffect\nfun reduceSupportState(state: SupportUiState) = state', 'SupportReducer.kt')
                self.assertTrue(any('state reducer must be pure' in e for e in errors), errors)

    def test_pilot_reducer_rejects_effect_calls_and_qualified_access(self):
        for effect in (
            'android.os.SystemClock.elapsedRealtime()', 'androidx.compose.runtime.mutableStateOf(false)',
            'kotlinx.coroutines.runBlocking {}', 'scope.launch { work() }', 'scope.async { work() }',
            'System.currentTimeMillis()', 'System.nanoTime()', 'Instant.now()', 'Clock.System.now()',
            'java.io.File(path)', 'println(state)', 'file.readText()', 'file.writeText(value)',
            'br.gov.caixa.loterias.feature.support.domain.usecase.LoadHelp()',
            'GetSupportUseCase()', 'store.dispatch(action)', 'random.nextInt()',
        ):
            with self.subTest(effect=effect):
                errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.state',
                                     'fun reduceSupportState(state: SupportUiState): SupportUiState { ' + effect + '; return state }', 'SupportReducer.kt')
                self.assertTrue(any('state reducer must be pure' in e for e in errors), errors)

    def test_pilot_reducer_rejects_mutable_state_and_suspend_operations(self):
        for body in ('var cached = false', 'val cache = mutableListOf<String>()',
                     'suspend fun reduceSupportState(state: SupportUiState) = state'):
            with self.subTest(body=body):
                errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.state', body, 'SupportReducer.kt')
                self.assertTrue(any('state reducer must be pure' in e for e in errors), errors)

    def test_reducer_mentions_in_comments_and_strings_are_not_code(self):
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.state',
                                        '// scope.launch {} Instant.now()\nval example = "println(state) mutableListOf()"\nfun reduceSupportState(state: SupportUiState) = state.copy()', 'SupportReducer.kt'))
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.model',
                                        '// fun reduceSupportState() {}\nval example = "object SupportReducer"', 'SupportUiState.kt'))


    def test_all_registered_feature_state_owners_accept_pure_reducers(self):
        names = ('account', 'betting', 'cart', 'home', 'marketplace', 'marketplace-filter', 'notifications',
                 'onboarding', 'payment', 'responsible-gaming', 'results', 'retail', 'support', 'tickets')
        for name in names:
            with self.subTest(feature=name), TemporaryDirectory() as temp:
                root = Path(temp)
                package = 'br.gov.caixa.loterias.feature.' + name.replace('-', '') + '.presentation.state'
                path = root / ('feature/' + name + '/presentation/src/main/kotlin/' + package.replace('.', '/') + '/FeatureStateReducer.kt')
                path.parent.mkdir(parents=True)
                path.write_text('package ' + package + '\nfun reduceFeatureState(state: UiState, action: Action) = state.copy()')
                self.assertEqual([], policy.check(root))

    def test_feature_state_cannot_bypass_redux_with_mutable_state_flow(self):
        for package in ('viewmodel', 'coordinator', 'route', 'state'):
            with self.subTest(package=package):
                errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.' + package,
                    'import kotlinx.coroutines.flow.MutableStateFlow\nval state = MutableStateFlow(UiState())')
                self.assertTrue(any('typed ReduxStateStore' in e for e in errors), errors)

    def test_feature_state_cannot_bypass_redux_with_qualified_mutable_flow(self):
        errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.viewmodel',
            'val state = kotlinx.coroutines.flow.MutableStateFlow(UiState())')
        self.assertTrue(any('typed ReduxStateStore' in e for e in errors), errors)

    def test_navigation_cannot_execute_repository_work_in_compose_coroutines(self):
        bodies = [
            'import androidx.compose.runtime.produceState\n'
            'fun graph() { composable("route") { produceState(false) { value = repository.enabled() } } }',
            'import androidx.compose.runtime.produceState as observe\n'
            '@Composable fun Gate() { observe(false) { value = preferences.enabled() } }',
            'import androidx.compose.runtime.rememberCoroutineScope\n'
            'fun graph() { composable("route") { val scope = rememberCoroutineScope(); scope.launch { load() } } }',
        ]
        for body in bodies:
            with self.subTest(body=body):
                errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.navigation', body)
                self.assertTrue(any('Navigation must delegate asynchronous work' in error for error in errors), errors)

    def test_navigation_can_compose_viewmodels_and_collect_read_only_state(self):
        body = ('import br.gov.caixa.loterias.feature.support.domain.usecase.GetFaqUseCase\n'
                'fun graph() { composable("route") { val model = viewModel { SupportViewModel(dependencies.getFaq) }; '
                'val state = model.state.collectAsStateWithLifecycle(); Screen(state, model::retry) } }')
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.navigation', body))

    def test_di_cannot_implement_application_usecases_inline(self):
        for body in (
            'fun provide(repository: SupportRepository): GetFaqUseCase = GetFaqUseCase { repository.load() }',
            'fun provide(repository: SupportRepository) = br.gov.caixa.loterias.feature.support.domain.usecase.GetFaqUseCase { repository.load() }',
            'import br.gov.caixa.loterias.feature.support.domain.usecase.GetFaqUseCase as Fetch\n'
            'fun provide(repository: SupportRepository) = Fetch { repository.load() }',
        ):
            with self.subTest(body=body):
                errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.di', body)
                self.assertTrue(any('DI must construct Domain UseCase implementations' in error for error in errors), errors)

    def test_di_can_construct_domain_operations_and_data_adapters(self):
        body = ('import br.gov.caixa.loterias.feature.support.domain.usecase.GetFaq\n'
                'import br.gov.caixa.loterias.feature.support.data.SupportRepositoryImpl\n'
                'fun provide(source: RemoteSource) = GetFaq(SupportRepositoryImpl(source))')
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.di', body))

    def test_reducer_cannot_resolve_localized_messages_even_through_alias(self):
        body = ('import br.gov.caixa.loterias.feature.support.presentation.formatting.localizedMessage as messageText\n'
                'fun reduceFaq(state: UiState, action: Action) = state.copy(message = messageText("failure"))')
        errors = self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.state', body, 'FaqReducer.kt')
        self.assertTrue(any('Reducer must receive already captured localized text' in error for error in errors), errors)

    def test_reducer_can_format_a_template_with_captured_locale(self):
        body = ('import br.gov.caixa.loterias.core.common.text.CapturedTextTemplate\n'
                'fun reduceFaq(state: UiState, action: Action) = state.copy(message = action.template.format(state.count))')
        self.assertEqual([], self.errors('main', 'br.gov.caixa.loterias.feature.support.presentation.state', body, 'FaqReducer.kt'))

if __name__ == '__main__':
    unittest.main()
