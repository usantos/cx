package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.domain.environment.model.TestEnvironment
import androidx.lifecycle.SavedStateHandle
import br.gov.caixa.loterias.core.domain.configuration.usecase.CampaignConfigurationUseCases
import br.gov.caixa.loterias.feature.account.domain.environment.ChangeEnvironment
import br.gov.caixa.loterias.core.presentation.campaign.viewmodel.CampaignViewModel
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.EnvironmentViewModel
import br.gov.caixa.loterias.feature.onboarding.presentation.state.QueueGateState
import br.gov.caixa.loterias.feature.onboarding.presentation.state.QueueStatus
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.HostStartupViewModel
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.QueueGateViewModel
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaignSettings
import br.gov.caixa.loterias.core.testing.coroutines.MainDispatcherRule
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HostViewModelsTest {
    @get:Rule val main = MainDispatcherRule()

    @Test fun `same environment does not commit and concurrent selection cannot duplicate commit`() = runTest {
        val release = CompletableDeferred<Boolean>(); val requested = mutableListOf<TestEnvironment>()
        val vm = EnvironmentViewModel(SavedStateHandle(), TestEnvironment.EXTERNO_ESTEIRA,
            ChangeEnvironment { requested += it; release.await() })
        vm.select(TestEnvironment.EXTERNO_ESTEIRA); runCurrent(); assertTrue(requested.isEmpty())
        vm.select(TestEnvironment.PILOTO); vm.select(TestEnvironment.INTERNO); runCurrent()
        assertEquals(listOf(TestEnvironment.PILOTO), requested); assertTrue(vm.state.value.saving)
        release.complete(true); runCurrent(); assertTrue(vm.state.value.persisted); assertFalse(vm.state.value.saving)
    }
    @Test fun `failed commit retries same selection and successful restored commit never writes twice`() = runTest {
        var calls = 0; val saved = SavedStateHandle()
        val vm = EnvironmentViewModel(saved, TestEnvironment.EXTERNO_ESTEIRA, ChangeEnvironment {
            if (++calls == 1) error("disk unavailable") else true
        })
        vm.select(TestEnvironment.PILOTO); runCurrent(); assertTrue(vm.state.value.failed)
        vm.retry(); runCurrent(); assertEquals(2, calls); assertTrue(vm.state.value.persisted)
        val restored = EnvironmentViewModel(SavedStateHandle(mapOf("pending" to saved.get<String>("pending"),
            "persisted" to saved.get<Boolean>("persisted"))), TestEnvironment.EXTERNO_ESTEIRA,
            ChangeEnvironment { fail("Committed selection must not be applied twice"); false })
        restored.bind(TestEnvironment.EXTERNO_ESTEIRA); runCurrent(); assertTrue(restored.state.value.persisted)
        restored.bind(TestEnvironment.PILOTO); assertNull(restored.state.value.pending)
    }
    @Test fun `cancellation is not failure and committed active selection clears unfinished restored request`() = runTest {
        val vm = EnvironmentViewModel(SavedStateHandle(), TestEnvironment.EXTERNO_ESTEIRA,
            ChangeEnvironment { throw CancellationException("disposed") })
        vm.select(TestEnvironment.PILOTO); runCurrent(); assertFalse(vm.state.value.failed); assertFalse(vm.state.value.saving)
        vm.bind(TestEnvironment.PILOTO); assertNull(vm.state.value.pending)
        val restored = EnvironmentViewModel(SavedStateHandle(mapOf("pending" to "PILOTO")), TestEnvironment.PILOTO,
            ChangeEnvironment { fail("Already selected before process death"); false })
        restored.bind(TestEnvironment.PILOTO); runCurrent(); assertNull(restored.state.value.pending)
    }
    @Test fun `startup keeps two second splash while preparation and retries retain original error underlay`() = runTest {
        var calls = 0
        val vm = HostStartupViewModel { if (++calls == 1) AppResult.Failure(AppError.Network) else AppResult.Success(Unit) }
        vm.start(); runCurrent(); assertTrue(vm.state.value.securityFailed); assertFalse(vm.state.value.minimumSplashElapsed)
        vm.retry(); assertEquals(1, vm.state.value.attempt); assertFalse(vm.state.value.securityFailed)
        runCurrent(); assertTrue(vm.state.value.securityReady)
        advanceTimeBy(1999); runCurrent(); assertFalse(vm.state.value.minimumSplashElapsed)
        advanceTimeBy(1); runCurrent(); assertTrue(vm.state.value.minimumSplashElapsed); vm.stop()
    }
    @Test fun `startup disposal cancels preparation without converting cancellation into security error`() = runTest {
        var cancelled = false
        val vm = HostStartupViewModel { try { awaitCancellation() } finally { cancelled = true } }
        vm.start(); runCurrent(); vm.stop(); runCurrent()
        assertTrue(cancelled); assertFalse(vm.state.value.securityFailed)
    }
    @Test fun `queue retries offline and legacy SDK error deliberately passes the gate`() {
        val vm = QueueGateViewModel(); vm.noInternet(); assertEquals(QueueStatus.NO_INTERNET, vm.state.value.status)
        vm.retry(); assertEquals(QueueGateState(QueueStatus.WAITING, 1), vm.state.value)
        vm.failed(); org.junit.Assert.assertNotEquals(QueueStatus.PASSED, vm.state.value.status)
    }
    @Test fun `campaign resume refreshes on IO and pause cancels work before another resume`() = runTest {
        val io = StandardTestDispatcher(testScheduler, "campaign-io"); var refreshed = 0; var cancelled = 0
        val configuration = object : CampaignConfigurationUseCases {
            override val settings = MutableStateFlow(CommemorativeCampaignSettings())
            override suspend fun refresh() {
                assertEquals(io, currentCoroutineContext()[kotlin.coroutines.ContinuationInterceptor])
                refreshed++; try { awaitCancellation() } finally { cancelled++ }
            }
        }
        val vm = CampaignViewModel(configuration, FixedCoroutineDispatchers(io))
        vm.resumed(true); vm.resumed(true); runCurrent(); assertEquals(1, refreshed)
        vm.resumed(false); runCurrent(); assertEquals(1, cancelled)
        vm.resumed(true); runCurrent(); assertEquals(2, refreshed)
        vm.resumed(false); runCurrent(); assertEquals(2, cancelled)
    }
    @Test fun `disposing and rebinding an unfinished selection cannot hide the new loading indicator`() = runTest {
        var cancelled = 0
        val vm = EnvironmentViewModel(SavedStateHandle(), TestEnvironment.EXTERNO_ESTEIRA,
            ChangeEnvironment { try { awaitCancellation() } finally { cancelled++ } })
        vm.select(TestEnvironment.PILOTO); runCurrent()
        vm.unbind(); vm.bind(TestEnvironment.EXTERNO_ESTEIRA); runCurrent()
        assertEquals(1, cancelled); assertTrue("The replacement request is still running", vm.state.value.saving)
        assertFalse(vm.state.value.failed); vm.unbind(); runCurrent()
    }
    @Test fun `a late security result from a disposed startup cannot open its replacement`() = runTest {
        val oldResult = CompletableDeferred<AppResult<Unit>>(); var calls = 0
        val vm = HostStartupViewModel {
            if (++calls == 1) withContext(NonCancellable) { oldResult.await() } else awaitCancellation()
        }
        vm.start(); runCurrent(); vm.stop(); vm.start(); runCurrent()
        oldResult.complete(AppResult.Success(Unit)); runCurrent()
        assertFalse("Only the replacement preparation can open startup", vm.state.value.securityReady)
        vm.stop(); runCurrent()
    }
    @Test fun `a disposed queue callback cannot pass a replacement gate`() {
        val vm = QueueGateViewModel(); val old = vm.begin()
        vm.reset(); val current = vm.begin()
        assertFalse(vm.passed(old)); assertEquals(QueueStatus.WAITING, vm.state.value.status)
        assertTrue(vm.passed(current)); assertEquals(QueueStatus.PASSED, vm.state.value.status)
    }

    @Test fun `recreation waits for an uncancellable disk commit and recovers its acknowledgement without writing twice`() = runTest {
        val disk = CompletableDeferred<Unit>(); var durable = "EXTERNO_ESTEIRA"; var writes = 0
        val change = ChangeEnvironment { selected ->
                if (durable == selected.name) return@ChangeEnvironment true
                writes++
                withContext(NonCancellable) { disk.await(); durable = selected.name }
                true
            }
        val vm = EnvironmentViewModel(SavedStateHandle(), TestEnvironment.EXTERNO_ESTEIRA, change)
        vm.select(TestEnvironment.PILOTO); runCurrent()
        try {
            vm.unbind(); vm.bind(TestEnvironment.EXTERNO_ESTEIRA); runCurrent()
            disk.complete(Unit); runCurrent()
            assertEquals("A cancelled acknowledgement must not repeat the durable write", 1, writes)
            assertTrue(vm.state.value.persisted); assertFalse(vm.state.value.saving)
        } finally { disk.complete(Unit); vm.unbind(); runCurrent() }
    }

    @Test fun `stopped startup cannot expose its old ready state before the replacement start effect`() = runTest {
        val vm = HostStartupViewModel { AppResult.Success(Unit) }
        vm.start(); runCurrent(); advanceTimeBy(2000); runCurrent()
        assertTrue(vm.state.value.securityReady); assertTrue(vm.state.value.minimumSplashElapsed)
        vm.stop()
        assertFalse("Recreation must not compose the queue before security preparation", vm.state.value.securityReady)
        assertFalse(vm.state.value.minimumSplashElapsed)
    }

}
