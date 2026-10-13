package br.gov.caixa.loterias.core.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class SessionTokenStoreTest {
    @Test fun `ID token is retained only for the committed session and extracted once at logout`() {
        val store = SessionTokenStore()
        val old = store.beginAuthorization()
        val current = store.beginAuthorization()
        assertFalse(store.completeAuthorization(old, "old-access", "old-id"))
        assertTrue(store.completeAuthorization(current, "access", " id-token "))
        store.cancelAuthorization(current)
        assertEquals("access", store.token())
        assertEquals("id-token", store.clearForEndSession())
        assertNull(store.token())
        assertNull(store.clearForEndSession())
        assertFalse(store.completeAuthorization(current, "late-access", "late-id"))
        assertNull(store.clearForEndSession())
    }

    @Test fun `replacement and clear discard hints and access token is never a substitute`() {
        val store = SessionTokenStore()
        store.completeAuthorization(store.beginAuthorization(), "access", " ")
        assertNull(store.clearForEndSession())
        store.completeAuthorization(store.beginAuthorization(), "access", "id")
        store.replace("other-access")
        assertNull(store.clearForEndSession())
        store.completeAuthorization(store.beginAuthorization(), "access", "id")
        store.clear()
        assertNull(store.clearForEndSession())
        store.replace("access-only")
        assertNull(store.clearForEndSession())
    }

    @Test fun `cancelled new attempt preserves existing session hint but logout rejects in flight publication`() {
        val store = SessionTokenStore()
        store.completeAuthorization(store.beginAuthorization(), "access", "id")
        val attempt = store.beginAuthorization()
        store.cancelAuthorization(attempt)
        assertEquals("id", store.clearForEndSession())
        repeat(100) {
            val ticket = store.beginAuthorization()
            val start = java.util.concurrent.CountDownLatch(1)
            val worker = Thread { start.await(); store.completeAuthorization(ticket, "access", "id") }
            worker.start()
            start.countDown()
            val hint = store.clearForEndSession()
            worker.join()
            assertTrue(hint == null || hint == "id")
            assertNull(store.token())
            assertNull(store.clearForEndSession())
        }
    }

    @Test
    fun `normalizes replaces and clears token`() {
        val store = SessionTokenStore()
        assertNull(store.token())
        store.replace("  access-token  ")
        assertEquals("access-token", store.token())
        store.replace("  ")
        assertNull(store.token())
        store.replace("another")
        store.clear()
        assertNull(store.token())
    }

    @Test fun `only current attempt can publish a nonblank token once`() {
        val store = SessionTokenStore()
        val old = store.beginAuthorization()
        val current = store.beginAuthorization()
        assertFalse(store.completeAuthorization(old, "late"))
        assertFalse(store.completeAuthorization(current, " "))
        assertNull(store.token())
        assertTrue(store.completeAuthorization(current, " current "))
        assertEquals("current", store.token())
        assertFalse(store.completeAuthorization(current, "duplicate"))
        val superseded = store.beginAuthorization()
        store.replace("replacement")
        assertFalse(store.completeAuthorization(superseded, "late"))
        assertEquals("replacement", store.token())
    }

    @Test fun `logout racing completion cannot end with restored credentials`() {
        repeat(100) {
            val store = SessionTokenStore()
            val attempt = store.beginAuthorization()
            val start = java.util.concurrent.CountDownLatch(1)
            val worker = Thread { start.await(); store.completeAuthorization(attempt, "late") }
            worker.start()
            start.countDown()
            store.clear()
            worker.join()
            assertNull(store.token())
            assertFalse(store.completeAuthorization(attempt, "replay"))
        }
    }

    @Test fun `cleanup targets its attempt without cancelling a newer login or clearing a committed session`() {
        val store = SessionTokenStore()
        val old = store.beginAuthorization()
        val fresh = store.beginAuthorization()
        store.cancelAuthorization(old)
        assertTrue(store.completeAuthorization(fresh, "fresh-session"))
        store.cancelAuthorization(fresh)
        assertEquals("fresh-session", store.token())
        val failed = store.beginAuthorization()
        store.cancelAuthorization(failed)
        assertFalse(store.completeAuthorization(failed, "late"))
        assertEquals("fresh-session", store.token())
    }
}
