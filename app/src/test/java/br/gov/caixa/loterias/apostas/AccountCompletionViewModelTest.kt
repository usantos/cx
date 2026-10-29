package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.feature.account.presentation.viewmodel.AccountCompletionViewModel
import br.gov.caixa.loterias.core.testing.coroutines.MainDispatcherRule
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginAccessResult
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import java.math.BigDecimal
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountCompletionViewModelTest {
    @get:Rule val main = MainDispatcherRule()
    private class Identities : AuthenticatedSessionIdentityProvider {
        var current: AuthenticatedSessionIdentity? = AuthenticatedSessionIdentity("a".repeat(64))
        override fun captureIdentity() = current
        override fun isCurrent(identity: AuthenticatedSessionIdentity) = current === identity
    }
    private val verified = PostLoginAccessResult.Verified(AccountAccess(PostLoginDestination.HOME, false, BigDecimal.ZERO))
    @Test fun `verification cannot overlap and loading ends only after navigation acknowledges the result`() = runTest {
        val ids = Identities(); var calls = 0; val response = CompletableDeferred<PostLoginAccessResult>()
        val vm = AccountCompletionViewModel(ids) { calls++; response.await() }
        vm.recheck(); vm.recheck(); runCurrent(); assertEquals(1, calls); assertTrue(vm.state.value.checking)
        response.complete(verified); runCurrent()
        val outcome = vm.state.value.outcome!!
        assertSame(ids.current, outcome.identity); assertEquals(PostLoginDestination.HOME, outcome.destination)
        assertTrue(vm.state.value.checking); vm.acknowledge(outcome)
        assertFalse(vm.state.value.checking); assertNull(vm.state.value.outcome)
    }
    @Test fun `obsolete account cannot produce navigation or error and can be checked again`() = runTest {
        val ids = Identities(); val response = CompletableDeferred<PostLoginAccessResult>()
        val vm = AccountCompletionViewModel(ids) { response.await() }
        vm.recheck(); runCurrent(); ids.current = AuthenticatedSessionIdentity("b".repeat(64))
        response.complete(verified); runCurrent()
        assertFalse(vm.state.value.checking); assertNull(vm.state.value.outcome); assertNull(vm.state.value.error)
        vm.recheck(); runCurrent(); assertSame(ids.current, vm.state.value.outcome?.identity)
        vm.acknowledge(vm.state.value.outcome!!)
    }
    @Test fun `business failure can be dismissed and retried without leaving loading or previous error`() = runTest {
        val ids = Identities(); var calls = 0
        val vm = AccountCompletionViewModel(ids) { if (++calls == 1) PostLoginAccessResult.BlockedAccountCheckFailed else verified }
        vm.recheck(); runCurrent(); assertFalse(vm.state.value.checking); assertNotNull(vm.state.value.error)
        vm.dismissError(); assertNull(vm.state.value.error)
        vm.recheck(); runCurrent(); assertNotNull(vm.state.value.outcome); assertNull(vm.state.value.error)
        vm.acknowledge(vm.state.value.outcome!!)
    }
    @Test fun `logout reset cancels verification and never leaks a late result`() = runTest {
        val ids = Identities(); var cancelled = false; var calls = 0
        val vm = AccountCompletionViewModel(ids) {
            if (++calls == 1) try { awaitCancellation() } finally { cancelled = true } else verified
        }
        vm.recheck(); runCurrent(); vm.reset(); runCurrent()
        assertTrue(cancelled); assertFalse(vm.state.value.checking); assertNull(vm.state.value.outcome); assertNull(vm.state.value.error)
        vm.recheck(); runCurrent()
        assertEquals(2, calls); assertNotNull(vm.state.value.outcome); assertNull(vm.state.value.error)
        vm.acknowledge(vm.state.value.outcome!!)
    }
    @Test fun `host disposal reset clears a completed account error before checking again`() = runTest {
        val ids = Identities(); var calls = 0
        val vm = AccountCompletionViewModel(ids) {
            if (++calls == 1) PostLoginAccessResult.BlockedAccountCheckFailed else verified
        }
        vm.recheck(); runCurrent()
        assertNotNull(vm.state.value.error); assertFalse(vm.state.value.checking)
        vm.reset()
        assertEquals(br.gov.caixa.loterias.feature.account.presentation.state.AccountCompletionState(), vm.state.value)
        vm.recheck(); runCurrent()
        assertEquals(2, calls); assertNull(vm.state.value.error); assertNotNull(vm.state.value.outcome)
        vm.acknowledge(vm.state.value.outcome!!)
    }
    @Test fun `missing identity and coroutine cancellation never become an account error`() = runTest {
        val ids = Identities(); ids.current = null
        val vm = AccountCompletionViewModel(ids) { fail("No identity must not verify"); verified }
        vm.recheck(); runCurrent(); assertFalse(vm.state.value.checking); assertNull(vm.state.value.error)
        ids.current = AuthenticatedSessionIdentity("a".repeat(64))
        val cancelled = AccountCompletionViewModel(ids) { throw CancellationException("destination left") }
        cancelled.recheck(); runCurrent(); assertFalse(cancelled.state.value.checking); assertNull(cancelled.state.value.error)
    }
}
