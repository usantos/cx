package br.gov.caixa.loterias.core.auth

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import org.junit.Assert.*
import kotlinx.coroutines.flow.first
import org.junit.Test
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore

class SessionIdentityTest {
    @Test fun `observed owner disappears during authorization and after logout`() = kotlinx.coroutines.runBlocking {
        val store = SessionTokenStore()
        assertNull(store.observeIdentities().first())
        store.completeAuthorization(store.beginAuthorization(), "access", "id", "a".repeat(64))
        val original = store.captureIdentity()!!
        assertSame(original, store.observeIdentities().first())
        val attempt = store.beginAuthorization()
        assertNull(store.observeIdentities().first())
        store.cancelAuthorization(attempt)
        assertNotSame(original, store.observeIdentities().first())
        assertSame(store.captureIdentity(), store.observeIdentities().first())
        store.clear()
        assertNull(store.observeIdentities().first())
    }

    private val owner = "a".repeat(64)

    @Test fun `session publication binds identity and token atomically without accepting forged leases`() {
        val store = SessionTokenStore()
        assertNull(store.captureIdentity())
        val fake = AuthenticatedSessionIdentity(owner)
        assertFalse(store.isCurrent(fake)); assertNull(store.tokenFor(fake))
        store.completeAuthorization(store.beginAuthorization(), "access", "id-token", owner)
        val identity = store.captureIdentity()!!
        assertEquals(owner, identity.ownerKey)
        assertTrue(store.isCurrent(identity)); assertEquals("access", store.tokenFor(identity))
        assertFalse(store.isCurrent(fake)); assertNull(store.tokenFor(fake))
        store.replace("replacement")
        assertNull(store.captureIdentity()); assertNull(store.tokenFor(identity))
    }

    @Test fun `cancelled new login cannot resurrect a previously captured lease`() {
        val store = SessionTokenStore()
        store.completeAuthorization(store.beginAuthorization(), "access", "id", owner)
        val old = store.captureIdentity()!!
        val attempt = store.beginAuthorization()
        assertNull(store.captureIdentity()); assertFalse(store.isCurrent(old)); assertNull(store.tokenFor(old))
        store.cancelAuthorization(attempt)
        val refreshed = store.captureIdentity()!!
        assertNotSame(old, refreshed); assertEquals(owner, refreshed.ownerKey)
        assertFalse(store.isCurrent(old)); assertNull(store.tokenFor(old))
        assertEquals("access", store.tokenFor(refreshed)); assertEquals("id", store.clearForEndSession())
        assertNull(store.captureIdentity()); assertFalse(store.isCurrent(refreshed))
    }

    @Test fun `stale completion and cleanup cannot replace a newer owner`() {
        val store = SessionTokenStore()
        val oldAttempt = store.beginAuthorization()
        val currentAttempt = store.beginAuthorization()
        assertTrue(store.completeAuthorization(currentAttempt, "new-access", "new-id", "b".repeat(64)))
        val current = store.captureIdentity()!!
        assertFalse(store.completeAuthorization(oldAttempt, "old-access", "old-id", owner))
        store.cancelAuthorization(oldAttempt)
        assertTrue(store.isCurrent(current)); assertEquals("new-access", store.tokenFor(current))
        store.clear()
        assertFalse(store.isCurrent(current)); assertNull(store.captureIdentity()); assertNull(store.tokenFor(current))
    }

    @Test fun `missing or malformed owner never becomes an authenticated identity`() {
        val store = SessionTokenStore()
        val attempt = store.beginAuthorization()
        assertTrue(runCatching { store.completeAuthorization(attempt, "access", "id", "cpf") }.isFailure)
        assertNull(store.token()); assertNull(store.captureIdentity())
        assertTrue(store.completeAuthorization(attempt, "access", "id"))
        assertNull(store.captureIdentity())
        assertEquals("access", store.token())
    }

    @Test fun `logout racing publication cannot leave token or identity behind`() {
        repeat(100) {
            val store = SessionTokenStore()
            val attempt = store.beginAuthorization()
            val start = java.util.concurrent.CountDownLatch(1)
            val worker = Thread { start.await(); store.completeAuthorization(attempt, "access", "id", owner) }
            worker.start(); start.countDown()
            store.clearForEndSession()
            worker.join()
            assertNull(store.token()); assertNull(store.captureIdentity())
            assertFalse(store.completeAuthorization(attempt, "late", "late-id", owner))
        }
    }
}
