package br.gov.caixa.loterias.core.domain.account

import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity

class AuthenticatedSessionIdentityTest {
    @Test fun `identity is a redacted lease not an equality match on owner hash`() {
        val first = AuthenticatedSessionIdentity("a".repeat(64))
        val second = AuthenticatedSessionIdentity("a".repeat(64))
        assertNotEquals(first, second)
        assertEquals("a".repeat(64), first.ownerKey)
        assertEquals("AuthenticatedSessionIdentity(redacted)", first.toString())
        for (invalid in listOf("", "access-token", "A".repeat(64), "a".repeat(63), "a".repeat(65))) {
            assertTrue(runCatching { AuthenticatedSessionIdentity(invalid) }.isFailure)
        }
    }
}
