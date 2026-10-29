package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.feature.notifications.domain.push.PushRegistrationCoordinator
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class PushRegistrationCoordinatorTest {
    private class Identities : AuthenticatedSessionIdentityProvider {
        var current: AuthenticatedSessionIdentity? = AuthenticatedSessionIdentity("a".repeat(64))
        override fun captureIdentity() = current
        override fun isCurrent(identity: AuthenticatedSessionIdentity) = current === identity
    }
    @Test fun `a token retrieved for an old account is not registered to its replacement`() = runTest {
        val ids = Identities(); val token = CompletableDeferred<String?>(); val started = CompletableDeferred<Unit>()
        val registered = mutableListOf<String>()
        val coordinator = PushRegistrationCoordinator(ids) { registered += it; true }
        val refresh = async { coordinator.refresh { started.complete(Unit); token.await() } }
        started.await(); ids.current = AuthenticatedSessionIdentity("b".repeat(64)); token.complete("device-token")
        assertFalse(refresh.await()); assertTrue(registered.isEmpty())
        assertTrue(coordinator.refresh { "new-token" }); assertEquals(listOf("new-token"), registered)
    }
    @Test fun `missing identity and missing platform token never trigger registration`() = runTest {
        val ids = Identities(); val coordinator = PushRegistrationCoordinator(ids) { fail("Must not register"); false }
        ids.current = null; assertFalse(coordinator.refresh { fail("Must not retrieve"); null })
        ids.current = AuthenticatedSessionIdentity("a".repeat(64)); assertFalse(coordinator.refresh { null })
    }
    @Test fun `token cancellation propagates and direct refresh preserves the registration result`() = runTest {
        val ids = Identities(); val coordinator = PushRegistrationCoordinator(ids) { false }
        try { coordinator.refresh { throw CancellationException("Firebase cancelled") }; fail("Must propagate") }
        catch (_: CancellationException) { }
        assertFalse(coordinator.onNewToken("token"))
        val accepted = PushRegistrationCoordinator(ids) { it == "refreshed-token" }
        assertTrue(accepted.onNewToken("refreshed-token"))
    }
}
