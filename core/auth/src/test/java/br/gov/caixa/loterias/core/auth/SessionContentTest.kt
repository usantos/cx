package br.gov.caixa.loterias.core.auth

import br.gov.caixa.loterias.core.domain.account.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class SessionContentTest {
    @Test fun `notification completion clears only exact owner and object while retaining cart and payment`() = runBlocking {
        val store = SessionTokenStore()
        val notification = content.latestNotification!!
        assertNull(store.capture(notification))
        val identity = login(store)
        assertFalse(store.clear(identity, notification))
        store.publish(identity, content)
        assertSame(identity, store.capture(notification))
        assertNull(store.capture(notification.copy()))
        assertFalse(store.clear(AuthenticatedSessionIdentity(identity.ownerKey), notification))
        val replacement = notification.copy(title = "Novo")
        store.publish(identity, content.copy(latestNotification = replacement))
        assertFalse(store.clear(identity, notification))
        assertTrue(store.clear(identity, replacement))
        assertNull(store.observe().first()!!.latestNotification)
        assertTrue(store.observe().first()!!.unidentifiedPayment)
        assertFalse(store.clear(identity, replacement))
        store.publish(identity, content)
        val attempt = store.beginAuthorization()
        assertNull(store.capture(notification))
        assertFalse(store.clear(identity, notification))
        store.cancelAuthorization(attempt)
        assertFalse(store.clear(identity, notification))
        assertTrue(store.clear(store.captureIdentity()!!, notification))
    }

    @Test fun `old notification completion racing new content never removes the new notification`() = runBlocking {
        repeat(100) {
            val store = SessionTokenStore()
            val identity = login(store)
            store.publish(identity, content)
            val replacement = content.copy(latestNotification = content.latestNotification!!.copy(id = 2))
            val start = java.util.concurrent.CountDownLatch(1)
            val thread = Thread { start.await(); store.clear(identity, content.latestNotification!!) }
            thread.start(); start.countDown()
            store.publish(identity, replacement)
            thread.join()
            assertSame(replacement.latestNotification, store.observe().first()!!.latestNotification)
        }
    }

    private val content = AuthenticatedSessionContent(null, AccountNotification(1, "Título", "Mensagem", "base64", null), true)
    private fun login(store: SessionTokenStore, owner: String = "a".repeat(64)): AuthenticatedSessionIdentity {
        assertTrue(store.completeAuthorization(store.beginAuthorization(), "access", "id-token", owner))
        return store.captureIdentity()!!
    }

    @Test fun `only exact active identity can publish and logout emits empty state`() = runBlocking {
        val store = SessionTokenStore()
        val values = mutableListOf<AuthenticatedSessionContent?>()
        val job = launch(Dispatchers.Unconfined) { ObserveAuthenticatedSessionContent(store)().collect { values += it } }
        try {
            assertNull(store.observe().first())
            assertFalse(store.publish(AuthenticatedSessionIdentity("a".repeat(64)), content))
            val identity = login(store)
            assertFalse(store.publish(AuthenticatedSessionIdentity(identity.ownerKey), content))
            assertTrue(store.publish(identity, content))
            assertSame(content, store.observe().first())
            assertTrue(store.publish(identity, null))
            assertNull(store.observe().first())
            assertTrue(store.publish(identity, content))
            assertEquals("id-token", store.clearForEndSession())
            assertNull(store.observe().first())
            assertFalse(store.publish(identity, content))
            assertNull(values.last())
            assertTrue(values.contains(content))
        } finally { job.cancel() }
    }

    @Test fun `new authorization hides old content and cancellation restores it without restoring the lease`() = runBlocking {
        val store = SessionTokenStore()
        val old = login(store)
        assertTrue(store.publish(old, content))
        val attempt = store.beginAuthorization()
        assertNull(store.observe().first())
        assertFalse(store.publish(old, content))
        store.cancelAuthorization(attempt)
        assertSame(content, store.observe().first())
        assertFalse(store.publish(old, content))
        val renewed = store.captureIdentity()!!
        assertTrue(store.publish(renewed, content.copy(unidentifiedPayment = false)))
        assertFalse(store.observe().first()!!.unidentifiedPayment)
    }

    @Test fun `new owner token replacement and local clear never retain previous metadata`() = runBlocking {
        val store = SessionTokenStore()
        val old = login(store)
        store.publish(old, content)
        val current = login(store, "b".repeat(64))
        assertNull(store.observe().first())
        assertFalse(store.publish(old, content))
        assertTrue(store.publish(current, content))
        store.replace("legacy-token")
        assertNull(store.observe().first())
        assertFalse(store.publish(current, content))
        val newer = login(store)
        store.publish(newer, content)
        store.clear()
        assertNull(store.observe().first())
        assertFalse(store.publish(newer, content))
    }

    @Test fun `logout racing content publication never leaves account data behind`() = runBlocking {
        repeat(100) {
            val store = SessionTokenStore()
            val identity = login(store)
            val start = java.util.concurrent.CountDownLatch(1)
            val thread = Thread { start.await(); store.publish(identity, content) }
            thread.start(); start.countDown()
            store.clear()
            thread.join()
            assertNull(store.observe().first())
            assertFalse(store.publish(identity, content))
        }
    }
}
