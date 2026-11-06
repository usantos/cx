package br.gov.caixa.loterias.core.network

import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.client.NetworkClientFactory
import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.core.network.api.AccountVerificationSessionExpiredException

class SessionLogoutApiTest {
    @Test fun `logout uses captured bearer bodyless POST without retries redirects or replacement token`() = runTest {
        var observed: Request? = null
        var requests = 0
        val builder = OkHttpClient.Builder().addInterceptor { chain ->
            requests++
            val response = chain.proceed(chain.request())
            observed = response.request
            response
        }
        val api = NetworkClientFactory.createSessionLogout("https://mock.caixa.gov.br/rest/v1/", builder,
            mockOptions = MockBackendOptions(enabled = true))
        val session = AccountVerificationSession({ "captured-secret" }, { true })
        val response = api.signOut(session)
        response.body()?.close()
        assertEquals(1, requests)
        val request = requireNotNull(observed)
        assertEquals("POST", request.method)
        assertEquals("/rest/v1/usuarios/sair", request.url.encodedPath)
        assertNull(request.url.query)
        assertEquals(0L, request.body!!.contentLength())
        assertEquals("Bearer captured-secret", request.header("Authorization"))
        assertSame(session, request.tag(AccountVerificationSession::class.java))
        assertFalse(builder.build().followRedirects)
        assertFalse(builder.build().followSslRedirects)
        assertFalse(builder.build().retryOnConnectionFailure)
    }

    @Test fun `expired identity prevents any authenticated dispatch`() = runTest {
        var reachedMock = false
        val builder = OkHttpClient.Builder().addInterceptor { chain ->
            val response = chain.proceed(chain.request())
            reachedMock = true
            response
        }
        val api = NetworkClientFactory.createSessionLogout("https://mock.caixa.gov.br/rest/v1/", builder,
            mockOptions = MockBackendOptions(enabled = true))
        val failure = runCatching { api.signOut(AccountVerificationSession({ "old" }, { false })) }.exceptionOrNull()
        assertTrue(failure is AccountVerificationSessionExpiredException)
        assertFalse(reachedMock)
    }
}
