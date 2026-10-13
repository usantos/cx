package br.gov.caixa.loterias.core.auth

import org.junit.Assert.*
import org.junit.Test

class BiometricAttemptRegistryTest {
    @Test fun `cancelled prompt cannot complete a later login`() {
        val tokens = SessionTokenStore()
        val prompts = BiometricAttemptRegistry(tokens)
        val cancelled = prompts.begin("first", "owner")
        val latest = prompts.begin("second", "owner")
        assertFalse(tokens.isAuthorizationCurrent(cancelled.ticket))
        assertTrue(tokens.isAuthorizationCurrent(latest.ticket))
        assertNull(prompts.consume("first"))
        assertFalse(tokens.completeAuthorization(cancelled.ticket, "old"))
        assertSame(latest, prompts.consume("second"))
        assertNull(prompts.consume("second"))
    }
    @Test fun `logout invalidates prompt and token exchange callback`() {
        val tokens = SessionTokenStore()
        val prompts = BiometricAttemptRegistry(tokens)
        val pending = prompts.begin("prompt", "owner")
        tokens.clearForEndSession()
        assertFalse(tokens.isAuthorizationCurrent(pending.ticket))
        assertFalse(tokens.completeAuthorization(pending.ticket, "late"))
        prompts.cancel()
        assertNull(prompts.consume("prompt"))
        assertNull(tokens.token())
    }
    @Test fun `mismatching result never consumes valid prompt`() {
        val prompts = BiometricAttemptRegistry(SessionTokenStore())
        val valid = prompts.begin("unpredictable", "owner")
        assertNull(prompts.consume(null))
        assertNull(prompts.consume("forged"))
        assertSame(valid, prompts.consume("unpredictable"))
    }
}
