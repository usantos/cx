package br.gov.caixa.loterias.core.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BearerTokenInterceptorTest {
    @Test
    fun `adds trimmed bearer token`() = withServer { server ->
        val client = client(" token-value ")
        client.newCall(Request.Builder().url(server.url("/")).build()).execute().close()
        assertEquals("Bearer token-value", server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun `does not add blank token`() = withServer { server ->
        val client = client("  ")
        client.newCall(Request.Builder().url(server.url("/")).build()).execute().close()
        assertNull(server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun `preserves authorization already supplied by request`() = withServer { server ->
        val request = Request.Builder()
            .url(server.url("/"))
            .header("Authorization", "Custom value")
            .build()
        client("token").newCall(request).execute().close()
        assertEquals("Custom value", server.takeRequest().getHeader("Authorization"))
    }

    private fun client(token: String?) = OkHttpClient.Builder()
        .addInterceptor(BearerTokenInterceptor { token })
        .build()

    private fun withServer(block: (MockWebServer) -> Unit) {
        MockWebServer().use { server ->
            server.enqueue(MockResponse())
            block(server)
        }
    }
}
