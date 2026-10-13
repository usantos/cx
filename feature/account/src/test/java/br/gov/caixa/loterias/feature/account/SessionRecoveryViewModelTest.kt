package br.gov.caixa.loterias.feature.account

import android.content.Intent
import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.auth.AuthenticationGateway
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.account.LocalSessionRecovery
import br.gov.caixa.loterias.core.domain.account.ResumeLocalSessionCleanupUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionRecoveryViewModelTest {
    @get:Rule val rule = MainDispatcherRule()

    @Test fun `no pending operation releases entry without clearing an active session`() = runTest(rule.dispatcher) {
        val auth = Authentication()
        var calls = 0
        val vm = viewModel(auth) { calls++; LocalSessionRecovery.NONE_PENDING }
        assertEquals(SessionRecoveryState.CHECKING, vm.state.value)
        vm.retry(); vm.acknowledge()
        runCurrent()
        assertEquals(SessionRecoveryState.READY, vm.state.value)
        vm.retry(); vm.acknowledge()
        assertEquals(1, calls)
        assertEquals(0, auth.logouts)
    }

    @Test fun `recovered data requires acknowledgment and invalidates memory without SSO replay`() = runTest(rule.dispatcher) {
        val auth = Authentication()
        val completion = CompletableDeferred<LocalSessionRecovery>()
        val vm = viewModel(auth) { completion.await() }
        runCurrent()
        assertEquals(SessionRecoveryState.CHECKING, vm.state.value)
        completion.complete(LocalSessionRecovery.RECOVERED)
        runCurrent()
        assertEquals(SessionRecoveryState.AWAITING_ACKNOWLEDGMENT, vm.state.value)
        assertEquals(1, auth.logouts)
        vm.retry()
        vm.acknowledge(); vm.acknowledge()
        assertEquals(SessionRecoveryState.READY, vm.state.value)
        assertEquals(1, auth.logouts)
    }

    @Test fun `failure remains blocked and retries only after an explicit action`() = runTest(rule.dispatcher) {
        val auth = Authentication()
        var calls = 0
        val vm = viewModel(auth) {
            calls++
            check(calls > 1)
            LocalSessionRecovery.RECOVERED
        }
        runCurrent()
        assertEquals(SessionRecoveryState.FAILED, vm.state.value)
        vm.acknowledge()
        assertEquals(SessionRecoveryState.FAILED, vm.state.value)
        assertEquals(0, auth.logouts)
        vm.retry(); vm.retry()
        assertEquals(SessionRecoveryState.CHECKING, vm.state.value)
        runCurrent()
        assertEquals(SessionRecoveryState.AWAITING_ACKNOWLEDGMENT, vm.state.value)
        assertEquals(2, calls)
    }

    @Test fun `memory clear failure cannot be forgotten when disk marker is already gone`() = runTest(rule.dispatcher) {
        val auth = Authentication().apply { failLogout = true }
        var calls = 0
        val vm = viewModel(auth) {
            if (++calls == 1) LocalSessionRecovery.RECOVERED else LocalSessionRecovery.NONE_PENDING
        }
        runCurrent()
        assertEquals(SessionRecoveryState.FAILED, vm.state.value)
        auth.failLogout = false
        vm.retry()
        runCurrent()
        assertEquals(2, auth.logouts)
        assertEquals(SessionRecoveryState.AWAITING_ACKNOWLEDGMENT, vm.state.value)
    }

    @Test fun `cancellation and host destruction never release content`() = runTest(rule.dispatcher) {
        val auth = Authentication()
        val cancelled = viewModel(auth) { throw CancellationException("cancelled") }
        runCurrent()
        assertEquals(SessionRecoveryState.FAILED, cancelled.state.value)
        val delayed = CompletableDeferred<LocalSessionRecovery>()
        val vm = viewModel(auth) { delayed.await() }
        val store = ViewModelStore().apply { put("recovery", vm) }
        runCurrent()
        store.clear()
        delayed.complete(LocalSessionRecovery.RECOVERED)
        runCurrent()
        assertNotEquals(SessionRecoveryState.READY, vm.state.value)
        assertEquals(0, auth.logouts)
    }

    private fun viewModel(auth: Authentication, resume: ResumeLocalSessionCleanupUseCase) =
        SessionRecoveryViewModel(resume, auth, FixedCoroutineDispatchers(rule.dispatcher))

    private class Authentication : AuthenticationGateway {
        var logouts = 0
        var failLogout = false
        override fun logout() { logouts++; check(!failLogout) }
        override fun createAuthorizationIntent(): Intent = error("Entry recovery must not log in")
        override suspend fun completeAuthorization(result: Intent?) = error("Entry recovery must not exchange tokens")
        override fun cancelAuthorization() = error("No authorization is started")
    }
}
