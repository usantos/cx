package br.gov.caixa.loterias.feature.account

import org.junit.Assert.*
import br.gov.caixa.loterias.feature.account.domain.model.EndSessionLaunch
import br.gov.caixa.loterias.feature.account.domain.model.EndSessionOutcome
import android.content.Intent
import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.auth.gateway.EndSessionGateway
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.ClearLocalSessionDataUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.SignOutSessionUseCase
import br.gov.caixa.loterias.feature.account.domain.model.SessionSignOutResult
import br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.account.presentation.state.LogoutNotice
import br.gov.caixa.loterias.feature.account.presentation.state.LogoutUiState
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LogoutViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class LogoutViewModelTest {
    @get:Rule val dispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())
    private fun viewModel(gateway: FakeGateway, clear: ClearLocalSessionDataUseCase = ClearLocalSessionDataUseCase {
        gateway.calls += "cleanup"
    }, remote: SignOutSessionUseCase = SignOutSessionUseCase { SessionSignOutResult.Confirmed(gateway.identity) },
    ) = LogoutViewModel(gateway, gateway, clear, remote, gateway, FixedCoroutineDispatchers(dispatcherRule.dispatcher))

    @Test fun `local logout precedes preparation and browser launches once`() {
        val gateway = FakeGateway()
        val vm = viewModel(gateway)
        assertTrue(vm.start())
        val launch = requireNotNull(vm.state.value.launch)
        assertEquals(listOf("local", "cleanup", "prepare"), gateway.calls)
        assertFalse(vm.start())
        assertFalse(vm.takeLaunch(EndSessionLaunch()))
        vm.complete { launch -> gateway.complete(launch, null) } // Um callback anterior ao despacho não é um resultado.
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
            EndSessionOutcome.UNCONFIRMED to LogoutNotice.UNCONFIRMED
        )) {
            val gateway = FakeGateway().apply { this.outcome = outcome }
            val vm = viewModel(gateway)
            vm.start()
            val launch = requireNotNull(vm.state.value.launch)
            vm.takeLaunch(launch)
            vm.complete { launch -> gateway.complete(launch, null) }
            vm.complete { launch -> gateway.complete(launch, null) }
            assertEquals(notice, vm.state.value.notice)
            assertFalse(vm.state.value.waitingForBrowser)
            assertEquals(listOf("local", "cleanup", "prepare", "complete", "discard"), gateway.calls)
            if (notice != null) assertFalse(vm.start())
            vm.dismissNotice()
            assertEquals(LogoutUiState(), vm.state.value)
            assertTrue(vm.start()) // Somente uma nova ação explícita inicia outra requisição.
            vm.launchFailed(launch) // Uma falha de lançamento antiga não deve interromper a nova requisição.
            assertNotNull(vm.state.value.launch)
        }
    }

    @Test fun `local failure blocks navigation and never opens browser`() {
        val gateway = FakeGateway().apply { failure = "local" }
        val vm = viewModel(gateway)
        assertTrue(vm.start())
        assertFalse(vm.state.value.localSessionEnded)
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
        vm.complete { launch -> gateway.complete(launch, null) }
        assertEquals(LogoutNotice.UNCONFIRMED, vm.state.value.notice)
        assertEquals(listOf("local", "cleanup", "prepare", "discard"), gateway.calls)
    }

    @Test fun `malformed callback is unconfirmed and cleanup still runs`() {
        val gateway = FakeGateway().apply { failure = "complete" }
        val vm = viewModel(gateway)
        vm.start()
        vm.takeLaunch(requireNotNull(vm.state.value.launch))
        vm.complete { launch -> gateway.complete(launch, null) }
        assertEquals(LogoutNotice.UNCONFIRMED, vm.state.value.notice)
        assertEquals(listOf("local", "cleanup", "prepare", "complete", "discard"), gateway.calls)
    }

    @Test fun `mock mode never launches real browser or claims remote confirmation`() {
        val gateway = FakeGateway()
        val vm = viewModel(gateway)
        assertTrue(vm.start(simulated = true))
        assertEquals(LogoutNotice.SIMULATED, vm.state.value.notice)
        assertEquals(listOf("local", "cleanup"), gateway.calls)
        vm.complete { launch -> gateway.complete(launch, null) }
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
        vm.complete { launch -> gateway.complete(launch, null) }
        assertEquals(listOf("local", "cleanup", "prepare", "discard"), gateway.calls)
        ViewModelStore().apply {
            put("idle", viewModel(gateway))
            clear()
        }
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
        vm.complete { launch -> gateway.complete(launch, null) }
        assertTrue(vm.state.value.clearingLocalData)
        cleanup.complete(Unit)
        assertFalse(vm.state.value.clearingLocalData)
        assertNotNull(vm.state.value.launch)
        assertEquals(listOf("local", "prepare"), gateway.calls)
    }

    @Test fun `cleanup failure cannot be dismissed and explicit retry does not repeat local logout`() {
        val gateway = FakeGateway()
        var attempts = 0
        val vm = viewModel(
            gateway,
            ClearLocalSessionDataUseCase {
                attempts++
                if (attempts == 1) error("write failure")
            }
        )
        assertTrue(vm.start()) // A memória já foi invalidada; o hospedeiro deve remover as telas autenticadas.
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
        val vm = viewModel(
            gateway,
            ClearLocalSessionDataUseCase {
                if (cancelled) throw CancellationException("cancelled")
            }
        )
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

    @Test fun `server rejection retains credentials and explicit retry sends once before clearing`() {
        val gateway = FakeGateway()
        var fail = true
        var requests = 0
        val failure = ServiceFailure.ConnectionFailed(ServiceConnectionFailure.TIMEOUT)
        val vm = viewModel(
            gateway,
            remote = SignOutSessionUseCase {
                requests++
                gateway.calls += "server"
                if (fail) SessionSignOutResult.Rejected(failure) else SessionSignOutResult.Confirmed(gateway.identity)
            }
        )
        assertTrue(vm.start())
        assertEquals(LogoutNotice.SERVER_FAILURE, vm.state.value.notice)
        assertEquals(failure, vm.state.value.serverFailure)
        assertFalse(vm.state.value.localSessionEnded)
        assertEquals(listOf("server"), gateway.calls)
        assertFalse(vm.start())
        fail = false
        vm.retryServerLogout()
        assertEquals(2, requests)
        assertEquals(listOf("server", "server", "local", "cleanup", "prepare"), gateway.calls)
        assertTrue(vm.state.value.localSessionEnded)
        vm.retryServerLogout()
        assertEquals(2, requests)
    }

    @Test fun `pending server request blocks duplicate and does not clear or open browser`() = runTest(dispatcherRule.dispatcher) {
        val gateway = FakeGateway()
        val reply = CompletableDeferred<SessionSignOutResult>()
        val vm = viewModel(gateway, remote = SignOutSessionUseCase { reply.await() })
        vm.start()
        assertTrue(vm.state.value.signingOut)
        assertFalse(vm.state.value.localSessionEnded)
        assertTrue(gateway.calls.isEmpty())
        assertFalse(vm.start())
        reply.complete(SessionSignOutResult.Confirmed(gateway.identity))
        assertEquals(listOf("local", "cleanup", "prepare"), gateway.calls)
        assertTrue(vm.state.value.localSessionEnded)
    }

    @Test fun `local clear retry after confirmed server logout never repeats remote transaction`() {
        val gateway = FakeGateway().apply { failure = "local" }
        var requests = 0
        val vm = viewModel(
            gateway,
            remote = SignOutSessionUseCase {
                requests++
                SessionSignOutResult.Confirmed(gateway.identity)
            }
        )
        vm.start()
        assertEquals(LogoutNotice.LOCAL_FAILURE, vm.state.value.notice)
        gateway.failure = null
        vm.dismissNotice()
        vm.start()
        assertEquals(1, requests)
        assertEquals(listOf("local", "local", "cleanup", "prepare"), gateway.calls)
    }

    @Test fun `simulation skips SILCE and cleanup retry does not replay confirmed logout`() {
        val gateway = FakeGateway()
        val vm = viewModel(gateway, remote = SignOutSessionUseCase { error("Simulation must not call SILCE") })
        vm.start(simulated = true)
        assertEquals(LogoutNotice.SIMULATED, vm.state.value.notice)
        assertTrue(vm.state.value.localSessionEnded)
    }

    @Test fun `late server success for replaced session cannot clear newer credentials`() = runTest(dispatcherRule.dispatcher) {
        val gateway = FakeGateway()
        val oldIdentity = gateway.identity
        val reply = CompletableDeferred<SessionSignOutResult>()
        val vm = viewModel(gateway, remote = SignOutSessionUseCase { reply.await() })
        vm.start()
        gateway.identity = AuthenticatedSessionIdentity("b".repeat(64))
        reply.complete(SessionSignOutResult.Confirmed(oldIdentity))
        assertEquals(LogoutNotice.SERVER_FAILURE, vm.state.value.notice)
        assertFalse(vm.state.value.localSessionEnded)
        assertTrue(gateway.calls.isEmpty())
    }

    @Test fun `destruction during server request prevents local clear even after reply`() = runTest(dispatcherRule.dispatcher) {
        val gateway = FakeGateway()
        val reply = CompletableDeferred<SessionSignOutResult>()
        val vm = viewModel(gateway, remote = SignOutSessionUseCase { reply.await() })
        val store = ViewModelStore().apply { put("logout", vm) }
        vm.start()
        store.clear()
        reply.complete(SessionSignOutResult.Confirmed(gateway.identity))
        assertTrue(gateway.calls.isEmpty())
        assertFalse(vm.state.value.localSessionEnded)
    }

    @Test fun `retrying cleanup never repeats confirmed server logout`() {
        val gateway = FakeGateway()
        var fail = true
        var requests = 0
        val vm = viewModel(
            gateway,
            ClearLocalSessionDataUseCase { check(!fail) },
            SignOutSessionUseCase {
                requests++
                SessionSignOutResult.Confirmed(gateway.identity)
            }
        )
        vm.start()
        assertEquals(LogoutNotice.LOCAL_DATA_FAILURE, vm.state.value.notice)
        fail = false
        vm.retryLocalCleanup()
        assertEquals(1, requests)
        assertEquals(listOf("local", "prepare"), gateway.calls)
    }

    private class FakeGateway :
        AuthenticationGateway,
        EndSessionGateway,
        AuthenticatedSessionIdentityProvider {
        var identity = AuthenticatedSessionIdentity("a".repeat(64))
        override fun captureIdentity() = identity
        override fun isCurrent(identity: AuthenticatedSessionIdentity) = this.identity === identity
        val calls = mutableListOf<String>()
        var failure: String? = null
        var outcome = EndSessionOutcome.RETURNED
        private fun call(name: String) {
            calls += name
            check(failure != name)
        }
        override fun logout() = call("local")
        override fun prepare(): EndSessionLaunch {
            call("prepare")
            return EndSessionLaunch()
        }
        override fun complete(launch: EndSessionLaunch, result: Intent?): EndSessionOutcome {
            call("complete")
            return outcome
        }
        override fun discard(launch: EndSessionLaunch) = call("discard")
        override fun intentFor(launch: EndSessionLaunch): Intent = error("Only the UI launches the browser")
        override fun createAuthorizationIntent(): Intent = error("Logout must not authorize")
        override suspend fun completeAuthorization(result: Intent?) = error("Logout must not exchange tokens")
        override fun cancelAuthorization() = error("Local logout must invalidate authorization")
    }
}
