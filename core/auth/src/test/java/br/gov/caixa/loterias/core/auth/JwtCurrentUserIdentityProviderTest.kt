package br.gov.caixa.loterias.core.auth

import java.nio.charset.StandardCharsets
import java.util.Base64
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class JwtCurrentUserIdentityProviderTest {
    @Test
    fun `reads account identity claims from access token`() {
        val store = SessionTokenStore().apply {
            replace(jwt("""{"name":"Pessoa","cpf":"12345678901","email":"pessoa@caixa.gov.br","dataNascimento":"19900101"}"""))
        }
        assertEquals(
            AuthenticatedUser("Pessoa", "12345678901", "pessoa@caixa.gov.br", "19900101"),
            JwtCurrentUserIdentityProvider(store).currentUser(),
        )
    }

    @Test
    fun `rejects malformed or incomplete token`() {
        val store = SessionTokenStore()
        assertNull(JwtCurrentUserIdentityProvider(store).currentUser())
        store.replace("not-a-jwt")
        assertNull(JwtCurrentUserIdentityProvider(store).currentUser())
        store.replace(jwt("""{"email":"pessoa@caixa.gov.br"}"""))
        assertNull(JwtCurrentUserIdentityProvider(store).currentUser())
    }

    private fun jwt(payload: String): String {
        val encoded = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(payload.toByteArray(StandardCharsets.UTF_8))
        return "header.$encoded.signature"
    }
}
