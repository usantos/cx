package br.gov.caixa.loterias.core.network

import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.core.network.api.AccountVerificationSessionExpiredException
import br.gov.caixa.loterias.core.network.api.AccountVerificationSessionInterceptor
import br.gov.caixa.loterias.core.network.api.NotificationInboxApi
import br.gov.caixa.loterias.core.network.client.NetworkClientFactory
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NotificationInboxApiTest {
    @Test fun `history endpoints carry captured bearer and no request payload`() = runTest {
        MockWebServer().use { server ->
            val api = api(server)
            val session = AccountVerificationSession({ "captured-token" }, { true })
            val calls: List<Pair<String, suspend () -> Unit>> = listOf(
                "GET historico-notificacoes" to suspend {
                    api.all(session)
                    Unit
                },
                "GET historico-notificacoes/nao-visualizadas" to suspend {
                    api.unread(session)
                    Unit
                },
                "PUT historico-notificacoes/7/visualiza" to suspend {
                    api.markRead(NOTIFICATION_ID, session).body()?.close()
                    Unit
                },
                "PUT historico-notificacoes/7/remove" to suspend {
                    api.remove(NOTIFICATION_ID, session).body()?.close()
                    Unit
                },
                "PUT historico-notificacoes/limpa" to suspend {
                    api.clear(session).body()?.close()
                    Unit
                }
            )
            for ((expected, call) in calls) {
                server.enqueue(MockResponse().setBody("{\"payload\":[]}"))
                call()
                val request = server.takeRequest()
                assertEquals(expected.substringBefore(' '), request.method)
                assertEquals("/rest/v1/" + expected.substringAfter(' '), request.path)
                assertEquals("Bearer captured-token", request.getHeader("Authorization"))
                assertEquals(0L, request.bodySize)
            }
        }
    }

    @Test fun `expired captured session cannot issue any history request`() = runTest {
        MockWebServer().use { server ->
            val api = api(server)
            val expired = AccountVerificationSession({ "captured-token" }, { false })
            assertTrue(runCatching { api.all(expired) }.exceptionOrNull() is AccountVerificationSessionExpiredException)
            assertTrue(runCatching { api.clear(expired) }.exceptionOrNull() is AccountVerificationSessionExpiredException)
            assertEquals(0, server.requestCount)
        }
    }

    @Test fun `production inbox client prevents transport redirects and automatic retries`() = runTest {
        var observed: okhttp3.Request? = null
        val builder = OkHttpClient.Builder().addInterceptor { chain ->
            chain.proceed(chain.request()).also { observed = it.request }
        }
        val api = NetworkClientFactory.createNotificationInbox(
            "https://mock.caixa.gov.br/rest/v1/",
            builder,
            mockOptions = MockBackendOptions(enabled = true)
        )
        val session = AccountVerificationSession({ "captured-token" }, { true })
        api.clear(session).body()?.close()
        val request = requireNotNull(observed)
        assertEquals("Bearer captured-token", request.header("Authorization"))
        assertSame(session, request.tag(AccountVerificationSession::class.java))
        assertFalse(builder.build().followRedirects)
        assertFalse(builder.build().followSslRedirects)
        assertFalse(builder.build().retryOnConnectionFailure)
    }

    private fun api(server: MockWebServer) = Retrofit.Builder().baseUrl(server.url("/rest/v1/"))
        .client(OkHttpClient.Builder().addInterceptor(AccountVerificationSessionInterceptor()).build())
        .addConverterFactory(GsonConverterFactory.create()).build().create(NotificationInboxApi::class.java)
    private companion object {
        const val NOTIFICATION_ID = 7L
    }
}
