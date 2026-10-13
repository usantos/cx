package br.gov.caixa.loterias.feature.account

import android.content.Intent
import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.auth.*
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.account.ClearLocalSessionDataUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LogoutViewModelTest {
    @get:Rule val dispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())
    private fun viewModel(gateway: FakeGateway, clear: ClearLocalSessionDataUseCase = ClearLocalSessionDataUseCase {
        gateway.calls += "cleanup"
    }) = LogoutViewModel(gateway, gateway, clear, FixedCoroutineDispatchers(dispatcherRule.dispatcher))

    @Test fun `local logout precedes preparation and browser launches once`() {
        val gateway = FakeGateway()
        val vm = viewModel(gateway)
        assertTrue(vm.start())
        val launch = requireNotNull(vm.state.value.launch)
        assertEquals(listOf("local", "cleanup", "prepare"), gateway.calls)
        assertFalse(vm.start())
        assertFalse(vm.takeLaunch(EndSessionLaunch()))
        vm.complete(null) // A callback before dispatch is not a result.
        vm.launchFailed(EndSessionLaunch())
        vm.dismissNotice()
        assertSame(launch, vm.state.value.launch)
        assertTrue(vm.takeLaunch(launch))
        assertFalse(vm.takeLaunch(launch))
        assertFalse(vm.start())
        assertTrue(vm.state.value.waitingForBrowser)
        assertNull(vm.state.value.launch)
        assertFalse(gateway.calls.contains("complete"))
    }

    @Test fun `every outcome consumes once without authorization or replay`() {
        for ((outcome, notice) in listOf(
            EndSessionOutcome.RETURNED to null,
            EndSessionOutcome.CANCELLED to LogoutNotice.CANCELLED,
            EndSessionOutcome.UNCONFIRMED to LogoutNotice.UNCONFIRMED,
        )) {
            val gateway = FakeGateway().apply { this.outcome = outcome }
            val vm = viewModel(gateway)
            vm.start()
            val launch = requireNotNull(vm.state.value.launch)
            vm.takeLaunch(launch)
            vm.complete(null)
            vm.complete(null)
            assertEquals(notice, vm.state.value.notice)
            assertFalse(vm.state.value.waitingForBrowser)
            assertEquals(listOf("local", "cleanup", "prepare", "complete", "discard"), gateway.calls)
            if (notice != null) assertFalse(vm.start())
            vm.dismissNotice()
            assertEquals(LogoutUiState(), vm.state.value)
            assertTrue(vm.start()) // Only a new explicit action starts another request.
            vm.launchFailed(launch) // Old launch failure must not interrupt the new request.
            assertNotNull(vm.state.value.launch)
        }
    }

    @Test fun `local failure blocks navigation and never opens browser`() {
        val gateway = FakeGateway().apply { failure = "local" }
        val vm = viewModel(gateway)
        assertFalse(vm.start())
        assertEquals(LogoutNotice.LOCAL_FAILURE, vm.state.value.notice)
        assertEquals(listOf("local"), gateway.calls)
        assertFalse(vm.start())
        vm.dismissNotice()
        gateway.failure = null
        assertTrue(vm.start())
    }

    @Test fun `preparation failure leaves local session ended without automatic retry`() {
        val gateway = FakeGateway().apply { failure = "prepare" }
        val vm = viewModel(gateway)
        assertTrue(vm.start())
        assertEquals(LogoutNotice.UNCONFIRMED, vm.state.value.notice)
        assertFalse(vm.state.value.waitingForBrowser)
        assertEquals(listOf("local", "cleanup", "prepare"), gateway.calls)
        assertFalse(vm.start())
    }

    @Test fun `browser launch failure discards request and cannot later confirm`() {
        val gateway = FakeGateway().apply { failure = "discard" }
        val vm = viewModel(gateway)
        vm.start()
        val launch = requireNotNull(vm.state.value.launch)
        vm.takeLaunch(launch)
        vm.launchFailed(launch)
        vm.complete(null)
        assertEquals(LogoutNotice.UNCONFIRMED, vm.state.value.notice)
        assertEquals(listOf("local", "cleanup", "prepare", "discard"), gateway.calls)
    }

    @Test fun `malformed callback is unconfirmed and cleanup still runs`() {
        val gateway = FakeGateway().apply { failure = "complete" }
        val vm = viewModel(gateway)
        vm.start()
        vm.takeLaunch(requireNotNull(vm.state.value.launch))
        vm.complete(null)
        assertEquals(LogoutNotice.UNCONFIRMED, vm.state.value.notice)
        assertEquals(listOf("local", "cleanup", "prepare", "complete", "discard"), gateway.calls)
    }

    @Test fun `mock mode never launches real browser or claims remote confirmation`() {
        val gateway = FakeGateway()
        val vm = viewModel(gateway)
        assertTrue(vm.start(simulated = true))
        assertEquals(LogoutNotice.SIMULATED, vm.state.value.notice)
        assertEquals(listOf("local", "cleanup"), gateway.calls)
        vm.complete(null)
        assertEquals(LogoutNotice.SIMULATED, vm.state.value.notice)
        vm.dismissNotice()
        assertEquals(LogoutUiState(), vm.state.value)
    }

    @Test fun `clearing host releases request without authorizing or keeping a callback alive`() {
        val gateway = FakeGateway()
        val vm = viewModel(gateway)
        val store = ViewModelStore().apply { put("logout", vm) }
        vm.start()
        store.clear()
        vm.complete(null)
        assertEquals(listOf("local", "cleanup", "prepare", "discard"), gateway.calls)
        ViewModelStore().apply { put("idle", viewModel(gateway)); clear() }
        assertEquals(4, gateway.calls.size)
    }

    @Test fun `pending cleanup blocks another start and browser until it completes`() = runTest(dispatcherRule.dispatcher) {
        val gateway = FakeGateway()
        val cleanup = CompletableDeferred<Unit>()
        val vm = viewModel(gateway, ClearLocalSessionDataUseCase { cleanup.await() })
        assertTrue(vm.start())
        assertTrue(vm.state.value.clearingLocalData)
        assertEquals(listOf("local"), gateway.calls)
        assertFalse(vm.start())
        vm.dismissNotice()
        vm.retryLocalCleanup()
        vm.complete(null)
        assertTrue(vm.state.value.clearingLocalData)
        cleanup.complete(Unit)
        assertFalse(vm.state.value.clearingLocalData)
        assertNotNull(vm.state.value.launch)
        assertEquals(listOf("local", "prepare"), gateway.calls)
    }

    @Test fun `cleanup failure cannot be dismissed and explicit retry does not repeat local logout`() {
        val gateway = FakeGateway()
        var attempts = 0
        val vm = viewModel(gateway, ClearLocalSessionDataUseCase {
            attempts++
            if (attempts == 1) error("write failure")
        })
        assertTrue(vm.start()) // Memory has already been invalidated; host must remove authenticated screens.
        assertEquals(LogoutNotice.LOCAL_DATA_FAILURE, vm.state.value.notice)
        vm.dismissNotice()
        assertEquals(LogoutNotice.LOCAL_DATA_FAILURE, vm.state.value.notice)
        assertFalse(vm.start())
        assertEquals(listOf("local"), gateway.calls)
        vm.retryLocalCleanup()
        assertEquals(2, attempts)
        assertEquals(listOf("local", "prepare"), gateway.calls)
        assertNotNull(vm.state.value.launch)
        vm.retryLocalCleanup()
        assertEquals(2, attempts)
    }

    @Test fun `cancelled cleanup never launches browser and can be retried explicitly`() {
        val gateway = FakeGateway()
        var cancelled = true
        val vm = viewModel(gateway, ClearLocalSessionDataUseCase {
            if (cancelled) throw CancellationException("cancelled")
        })
        assertTrue(vm.start())
        assertEquals(LogoutNotice.LOCAL_DATA_FAILURE, vm.state.value.notice)
        assertEquals(listOf("local"), gateway.calls)
        cancelled = false
        vm.retryLocalCleanup()
        assertNotNull(vm.state.value.launch)
        assertEquals(listOf("local", "prepare"), gateway.calls)
    }

    @Test fun `host destruction during cleanup cannot launch SSO when delayed write returns`() = runTest(dispatcherRule.dispatcher) {
        val gateway = FakeGateway()
        val cleanup = CompletableDeferred<Unit>()
        val vm = viewModel(gateway, ClearLocalSessionDataUseCase { cleanup.await() })
        val store = ViewModelStore().apply { put("logout", vm) }
        vm.start()
        store.clear()
        cleanup.complete(Unit)
        assertEquals(listOf("local"), gateway.calls)
        assertNull(vm.state.value.launch)
    }

    @Test fun `mock logout also waits for successful cleanup`() {
        val gateway = FakeGateway()
        var fail = true
        val vm = viewModel(gateway, ClearLocalSessionDataUseCase { check(!fail) })
        vm.start(simulated = true)
        assertEquals(LogoutNotice.LOCAL_DATA_FAILURE, vm.state.value.notice)
        fail = false
        vm.retryLocalCleanup()
        assertEquals(LogoutNotice.SIMULATED, vm.state.value.notice)
        assertEquals(listOf("local"), gateway.calls)
    }

    private class FakeGateway : AuthenticationGateway, EndSessionGateway {
        val calls = mutableListOf<String>()
        var failure: String? = null
        var outcome = EndSessionOutcome.RETURNED
        private fun call(name: String) { calls += name; check(failure != name) }
        override fun logout() = call("local")
        override fun prepare(): EndSessionLaunch { call("prepare"); return EndSessionLaunch() }
        override fun complete(launch: EndSessionLaunch, result: Intent?): EndSessionOutcome {
            call("complete"); return outcome
        }
        override fun discard(launch: EndSessionLaunch) = call("discard")
        override fun intentFor(launch: EndSessionLaunch): Intent = error("Only the UI launches the browser")
        override fun createAuthorizationIntent(): Intent = error("Logout must not authorize")
        override suspend fun completeAuthorization(result: Intent?) = error("Logout must not exchange tokens")
        override fun cancelAuthorization() = error("Local logout must invalidate authorization")
    }
}
