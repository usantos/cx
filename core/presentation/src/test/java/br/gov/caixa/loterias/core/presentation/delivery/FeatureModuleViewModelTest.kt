package br.gov.caixa.loterias.core.presentation.delivery

import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.configuration.model.AppModulesConfiguration
import br.gov.caixa.loterias.core.domain.configuration.repository.AppModulesConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import br.gov.caixa.loterias.core.domain.delivery.InstallAppModuleUseCase
import br.gov.caixa.loterias.core.domain.delivery.ModuleDeliveryState
import br.gov.caixa.loterias.core.domain.delivery.ModuleInstallationRepository
import br.gov.caixa.loterias.core.testing.coroutines.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeatureModuleViewModelTest {
    @get:Rule val main = MainDispatcherRule()

    @Test fun independentModulesRejectLateAndUnrelatedEvents() = runTest {
        val configuration = Configuration(setOf(AppModule.RESULTS, AppModule.NOTIFICATIONS))
        val installer = Installer()
        val results = model(configuration, installer, AppModule.RESULTS, FeatureEntryAccess.OPTIONAL, false)
        val notifications = model(configuration, installer, AppModule.NOTIFICATIONS, FeatureEntryAccess.OPTIONAL, false)
        val owners = ViewModelStore().apply {
            put("results", results)
            put("notifications", notifications)
        }
        runCurrent()
        installer.events.getValue(AppModule.RESULTS).value = ModuleDeliveryState.Ready
        runCurrent()
        assertEquals(ModuleDeliveryState.Ready, results.state.value)
        assertEquals(ModuleDeliveryState.Pending, notifications.state.value)
        configuration.configuration.value = AppModulesConfiguration(setOf(AppModule.NOTIFICATIONS))
        runCurrent()
        installer.events.getValue(AppModule.RESULTS).value = ModuleDeliveryState.Ready
        runCurrent()
        assertEquals(ModuleDeliveryState.Disabled, results.state.value)
        assertEquals(ModuleDeliveryState.Pending, notifications.state.value)
        owners.clear()
    }

    @Test fun continuingJourneyKeepsItsPresentationButCannotStartANewWriteAfterOff() = runTest {
        val configuration = Configuration(setOf(AppModule.PAYMENT))
        val installer = Installer()
        installer.events.getValue(AppModule.PAYMENT).value = ModuleDeliveryState.Ready
        val useCase = AppModulesConfigurationUseCase(configuration)
        val model = model(configuration, installer, AppModule.PAYMENT, FeatureEntryAccess.OPTIONAL, true)
        val owners = ViewModelStore().apply { put("payment", model) }
        runCurrent()
        configuration.configuration.value = AppModulesConfiguration(emptySet())
        runCurrent()
        assertEquals(ModuleDeliveryState.Ready, model.state.value)
        assertFalse(useCase.isEnabled(AppModule.PAYMENT))
        assertEquals(1, installer.requests.count { it == AppModule.PAYMENT })
        owners.clear()
    }

    @Test fun disabledOptionalEntryDoesNotInstallButRequiredContinuationCanRecover() = runTest {
        val configuration = Configuration(emptySet())
        val installer = Installer()
        val owners = ViewModelStore()
        val optional = model(configuration, installer, AppModule.PAYMENT, FeatureEntryAccess.OPTIONAL, true)
        val continuation = model(configuration, installer, AppModule.ACCOUNT, FeatureEntryAccess.CONTINUATION, true)
        owners.put("optional", optional)
        owners.put("continuation", continuation)
        runCurrent()
        assertEquals(ModuleDeliveryState.Disabled, optional.state.value)
        optional.retry()
        assertEquals(listOf(AppModule.ACCOUNT), installer.requests)
        installer.events.getValue(AppModule.ACCOUNT).value = ModuleDeliveryState.Ready
        runCurrent()
        assertEquals(ModuleDeliveryState.Ready, continuation.state.value)
        owners.clear()
    }

    @Test fun confirmationRejectsStaleCallbackAndRetryStartsOnlyTheCurrentSession() = runTest {
        val configuration = Configuration(setOf(AppModule.RESULTS))
        val installer = Installer()
        val model = model(configuration, installer, AppModule.RESULTS, FeatureEntryAccess.OPTIONAL, false)
        val owners = ViewModelStore().apply { put("results", model) }
        runCurrent()
        installer.events.getValue(AppModule.RESULTS).value = ModuleDeliveryState.Confirmation(FIRST_SESSION)
        runCurrent()
        val stale = model.confirmationHandler(FIRST_SESSION)
        model.confirmationHandler(OTHER_SESSION)(false)
        assertEquals(ModuleDeliveryState.Confirmation(FIRST_SESSION), model.state.value)
        model.confirmationHandler(FIRST_SESSION)(true)
        assertEquals(ModuleDeliveryState.Confirmation(FIRST_SESSION), model.state.value)
        stale(false)
        assertEquals(ModuleDeliveryState.Failed, model.state.value)
        model.retry()
        runCurrent()
        installer.events.getValue(AppModule.RESULTS).value = ModuleDeliveryState.Ready
        runCurrent()
        stale(false)
        assertEquals(ModuleDeliveryState.Ready, model.state.value)
        model.entryUnavailable()
        assertEquals(ModuleDeliveryState.Failed, model.state.value)
        model.entryUnavailable()
        assertEquals(ModuleDeliveryState.Failed, model.state.value)
        configuration.configuration.value = AppModulesConfiguration(emptySet())
        runCurrent()
        model.retry()
        assertEquals(2, installer.requests.size)
        assertEquals(ModuleDeliveryState.Disabled, model.state.value)
        owners.clear()
    }

    private fun model(
        configuration: Configuration,
        installer: Installer,
        module: AppModule,
        access: FeatureEntryAccess,
        preserveActive: Boolean,
    ) = FeatureModuleViewModel(
        AppModulesConfigurationUseCase(configuration),
        InstallAppModuleUseCase(installer),
        module,
        access,
        preserveActive
    )

    private companion object {
        const val FIRST_SESSION = 7
        const val OTHER_SESSION = 8
    }

    private class Configuration(enabled: Set<AppModule>) : AppModulesConfigurationRepository {
        override val configuration = MutableStateFlow(AppModulesConfiguration(enabled))
        override suspend fun refresh() = Unit
    }
    private class Installer : ModuleInstallationRepository {
        val events = AppModule.entries.associateWith { MutableStateFlow<ModuleDeliveryState>(ModuleDeliveryState.Pending) }
        val requests = mutableListOf<AppModule>()
        override fun install(module: AppModule) = events.getValue(module).also { requests += module }
    }
}
