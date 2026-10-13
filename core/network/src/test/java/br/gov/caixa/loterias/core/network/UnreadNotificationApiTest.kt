package br.gov.caixa.loterias.core.network

import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Test

class UnreadNotificationApiTest {
    @Test fun `unread GET carries bound bearer no query and no body`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setHeader("Content-Type", "application/json")
                .setBody("{\"payload\":[{\"id\":1},{\"id\":2}]}"))
            val api = Retrofit.Builder().baseUrl(server.url("/rest/v1/"))
                .client(OkHttpClient.Builder().addInterceptor(AccountVerificationSessionInterceptor()).build())
                .addConverterFactory(GsonConverterFactory.create()).build().create(NotificationReadApi::class.java)
            val response = api.unread(AccountVerificationSession({ "synthetic-token" }, { true }))
            assertEquals(2, response.body()!!.payload!!.size)
            val request = server.takeRequest()
            assertEquals("GET", request.method)
            assertEquals("/rest/v1/historico-notificacoes/nao-visualizadas", request.path)
            assertEquals("Bearer synthetic-token", request.getHeader("Authorization"))
            assertEquals(0L, request.bodySize)
        }
    }

    @Test fun `expired session cannot send unread GET`() = runTest {
        MockWebServer().use { server ->
            val api = Retrofit.Builder().baseUrl(server.url("/rest/v1/"))
                .client(OkHttpClient.Builder().addInterceptor(AccountVerificationSessionInterceptor()).build())
                .addConverterFactory(GsonConverterFactory.create()).build().create(NotificationReadApi::class.java)
            assertTrue(runCatching {
                api.unread(AccountVerificationSession({ "synthetic-token" }, { false }))
            }.exceptionOrNull() is AccountVerificationSessionExpiredException)
            assertEquals(0, server.requestCount)
        }
    }
}
