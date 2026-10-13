package br.gov.caixa.loterias.core.auth

import org.junit.Assert.*
import org.junit.Test

class AuthorizationAttemptRegistryTest {
    @Test fun `only exact pending state consumes the original request once`() {
        val tokens = SessionTokenStore()
        val registry = AuthorizationAttemptRegistry<Any>(tokens)
        assertNull(registry.consume("missing"))
        assertTrue(runCatching { registry.begin(" ", Any()) }.isFailure)
        val request = Any()
        val pending = registry.begin("private-state", request)
        assertFalse(pending.toString().contains("private-state"))
        assertNull(registry.consume(null))
        assertNull(registry.consume("PRIVATE-STATE"))
        assertNull(registry.consume("private-state "))
        assertSame(pending, registry.consume("private-state"))
        assertSame(request, pending.request)
        assertEquals("private-state", pending.state)
        assertNull(registry.consume("private-state"))
        assertTrue(tokens.completeAuthorization(pending.ticket, "valid-token"))
        assertFalse(tokens.completeAuthorization(pending.ticket, "replayed-token"))
        assertEquals("valid-token", tokens.token())
    }

    @Test fun `replacement rejects old state without consuming the newer request`() {
        val tokens = SessionTokenStore()
        val registry = AuthorizationAttemptRegistry<String>(tokens)
        val old = registry.begin("old", "old-request")
        val fresh = registry.begin("new", "new-request")
        assertNull(registry.consume("old"))
        assertFalse(tokens.completeAuthorization(old.ticket, "old-token"))
        assertSame(fresh, registry.consume("new"))
        assertTrue(tokens.completeAuthorization(fresh.ticket, "fresh-token"))
    }

    @Test fun `logout prevents a late exchange from restoring the session whether response was consumed or not`() {
        for (consume in listOf(false, true)) {
            val tokens = SessionTokenStore().apply { replace("existing-session") }
            val registry = AuthorizationAttemptRegistry<String>(tokens)
            val pending = registry.begin("state", "request")
            if (consume) registry.consume("state")
            registry.logout()
            assertNull(registry.consume("state"))
            assertFalse(tokens.completeAuthorization(pending.ticket, "late-token"))
            assertNull(tokens.token())
        }
    }

    @Test fun `cancel invalidates pending and in flight exchanges without logging out the existing session`() {
        for (consume in listOf(false, true)) {
            val tokens = SessionTokenStore().apply { replace("existing-session") }
            val registry = AuthorizationAttemptRegistry<String>(tokens)
            val pending = registry.begin("state", "request")
            if (consume) registry.consume("state")
            registry.cancel()
            assertNull(registry.consume("state"))
            assertFalse(tokens.completeAuthorization(pending.ticket, "late-token"))
            assertEquals("existing-session", tokens.token())
        }
    }
}
