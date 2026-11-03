package br.gov.caixa.loterias.feature.support

import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.configuration.model.AppModulesConfiguration
import br.gov.caixa.loterias.core.domain.configuration.repository.AppModulesConfigurationRepository
import br.gov.caixa.loterias.feature.support.domain.delivery.SupportDeliveryState
import br.gov.caixa.loterias.feature.support.domain.delivery.SupportModuleDelivery
import br.gov.caixa.loterias.feature.support.presentation.dynamic.SupportModuleViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SupportModuleViewModelTest {
    @get:Rule val main = MainDispatcherRule()

    @Test fun disabledNeverRequestsAndReenableOpensWithExplicitRetryAfterFailure() = runTest {
        val config = Configuration()
        var installs = 0
        val delivery = object : SupportModuleDelivery {
            override fun install() = flow {
                installs++
                emit(if (installs == 1) SupportDeliveryState.Failed else SupportDeliveryState.Ready)
            }
        }
        val model =
            SupportModuleViewModel(br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase(config), delivery)
        val store = ViewModelStore().also { it.put("module", model) }
        runCurrent()
        assertEquals(SupportDeliveryState.Disabled, model.state.value)
        assertEquals(0, installs)
        config.enable(true)
        runCurrent()
        assertEquals(SupportDeliveryState.Failed, model.state.value)
        assertEquals(1, installs)
        model.retry()
        model.retry()
        runCurrent()
        assertEquals(SupportDeliveryState.Ready, model.state.value)
        assertEquals(2, installs)
        config.enable(false)
        runCurrent()
        assertEquals(SupportDeliveryState.Disabled, model.state.value)
        model.retry()
        runCurrent()
        assertEquals(2, installs)
        config.enable(true)
        runCurrent()
        assertEquals(SupportDeliveryState.Ready, model.state.value)
        store.clear()
    }

    @Test fun disablingCancelsListenerAndRejectsLateCompletion() = runTest {
        val config = Configuration().apply { enable(true) }
        val events = MutableStateFlow<SupportDeliveryState>(SupportDeliveryState.Pending)
        val delivery = object : SupportModuleDelivery {
            override fun install() = events
        }
        val model =
            SupportModuleViewModel(br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase(config), delivery)
        val store = ViewModelStore().also { it.put("module", model) }
        runCurrent()
        events.value = SupportDeliveryState.Downloading(DOWNLOADED_BYTES, TOTAL_BYTES)
        runCurrent()
        assertEquals(events.value, model.state.value)
        events.value = SupportDeliveryState.Confirmation(DELIVERY_SESSION_ID)
        runCurrent()
        assertEquals(events.value, model.state.value)
        config.enable(false)
        runCurrent()
        events.value = SupportDeliveryState.Ready
        runCurrent()
        assertEquals(SupportDeliveryState.Disabled, model.state.value)
        assertEquals(0, events.subscriptionCount.value)
        store.clear()
    }

    @Test fun confirmationDeclinedAndEntryFailureAreRecoverableWithoutOverwritingDisabled() = runTest {
        val config = Configuration().apply { enable(true) }
        val events = MutableStateFlow<SupportDeliveryState>(SupportDeliveryState.Confirmation(DELIVERY_SESSION_ID))
        val model =
            SupportModuleViewModel(
                br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase(config),
                object : SupportModuleDelivery {
                    override fun install() = events
                }
            )
        val store = ViewModelStore().also { it.put("module", model) }
        runCurrent()
        model.confirmationDeclined()
        assertEquals(SupportDeliveryState.Failed, model.state.value)
        events.value = SupportDeliveryState.Ready
        model.retry()
        runCurrent()
        model.entryUnavailable()
        assertEquals(SupportDeliveryState.Failed, model.state.value)
        config.enable(false)
        runCurrent()
        model.confirmationDeclined()
        model.entryUnavailable()
        assertEquals(SupportDeliveryState.Disabled, model.state.value)
        store.clear()
    }

    @Test fun resultFromAnOlderConfirmationCannotDeclineTheCurrentSession() = runTest {
        val config = Configuration().apply { enable(true) }
        val events = MutableStateFlow<SupportDeliveryState>(SupportDeliveryState.Confirmation(DELIVERY_SESSION_ID))
        val model = SupportModuleViewModel(
            br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase(config),
            object : SupportModuleDelivery {
                override fun install() = events
            }
        )
        val store = ViewModelStore().also { it.put("module", model) }
        runCurrent()
        val oldResult = model.confirmationHandler(DELIVERY_SESSION_ID)
        events.value = SupportDeliveryState.Confirmation(NEXT_SESSION_ID)
        runCurrent()
        oldResult(false)
        assertEquals(events.value, model.state.value)
        val currentResult = model.confirmationHandler(NEXT_SESSION_ID)
        config.enable(false)
        runCurrent()
        config.enable(true)
        runCurrent()
        currentResult(false)
        assertEquals(events.value, model.state.value)
        model.confirmationHandler(NEXT_SESSION_ID)(true)
        assertEquals(events.value, model.state.value)
        store.clear()
    }

    @Test fun compatibilityForegroundStopsItsSharedRefreshOnOwnerClear() = runTest {
        val config = Configuration()
        val model = SupportModuleViewModel(
            br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase(config),
            object : SupportModuleDelivery {
                override fun install() = flow<SupportDeliveryState> { error("Não deve instalar") }
            }
        )
        val store = ViewModelStore().also { it.put("module", model) }
        model.foreground(true)
        runCurrent()
        assertEquals(1, config.refreshes)
        store.clear()
        advanceTimeBy(COMPATIBILITY_REFRESH_INTERVAL)
        runCurrent()
        assertEquals(1, config.refreshes)
    }

    private class Configuration : AppModulesConfigurationRepository {
        override val configuration = MutableStateFlow(AppModulesConfiguration())
        var refreshes = 0
        override suspend fun refresh() {
            refreshes++
        }
        fun enable(value: Boolean) {
            configuration.value = AppModulesConfiguration(if (value) setOf(AppModule.SUPPORT_FAQ) else emptySet())
        }
    }
}

private const val DOWNLOADED_BYTES = 5L
private const val TOTAL_BYTES = 10L
private const val DELIVERY_SESSION_ID = 9

private const val NEXT_SESSION_ID = 10

private const val COMPATIBILITY_REFRESH_INTERVAL = 30_000L
