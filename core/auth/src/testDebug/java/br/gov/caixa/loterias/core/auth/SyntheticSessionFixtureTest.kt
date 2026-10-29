package br.gov.caixa.loterias.core.auth

import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.auth.fixture.SyntheticSessionFixture
import br.gov.caixa.loterias.core.auth.session.JwtCurrentUserIdentityProvider
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore

class SyntheticSessionFixtureTest {
    @Test fun `fixed synthetic fixture seeds only an empty debug session`() {
        val store = SessionTokenStore()
        assertTrue(SyntheticSessionFixture.install(store))
        val identity = store.captureIdentity()!!
        assertEquals("a".repeat(64), identity.ownerKey)
        val user = JwtCurrentUserIdentityProvider(store).currentUser()
        assertNotNull(user)
        assertEquals("Apostador de teste", user!!.name)
        assertEquals("00000000000", user.cpf)
        assertEquals("teste@example.invalid", user.email)
        assertEquals("01/01/1990", user.birthDate)
        assertSame(identity, store.captureIdentity())
        assertEquals(store.token(), store.tokenFor(identity))
        assertTrue(store.token()!!.endsWith(".visual-fixture"))
        assertFalse(SyntheticSessionFixture.install(store))
        assertSame(identity, store.captureIdentity())
    }
    @Test fun `fixture cannot override active login or pending authorization`() {
        val store = SessionTokenStore()
        val pending = store.beginAuthorization()
        assertFalse(SyntheticSessionFixture.install(store))
        assertTrue(store.isAuthorizationCurrent(pending))
        assertTrue(store.completeAuthorization(pending, "actual-session", "id", "b".repeat(64)))
        val identity = store.captureIdentity()!!
        assertFalse(SyntheticSessionFixture.install(store))
        assertSame(identity, store.captureIdentity())
        assertEquals("actual-session", store.token())
    }
}
