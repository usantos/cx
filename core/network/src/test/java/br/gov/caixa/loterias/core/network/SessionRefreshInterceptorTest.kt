package br.gov.caixa.loterias.core.network

import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.BufferedSink
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.network.interceptor.SingleAttemptTransactionInterceptor
import br.gov.caixa.loterias.core.network.interceptor.SessionRefreshHandler
import br.gov.caixa.loterias.core.network.interceptor.SessionRefreshLease
import br.gov.caixa.loterias.core.network.interceptor.SessionRefreshInterceptor

class SessionRefreshInterceptorTest {
    private class Lease(var prepared: String? = "Bearer refreshed", var renewed: String? = "Bearer rotated") : SessionRefreshLease {
        var preparations = 0
        var renewals = 0
        override fun prepare(): String? { preparations++; return prepared }
        override fun refreshAfterUnauthorized(): String? { renewals++; return renewed }
    }

    @Test fun `preflight injects refreshed token and authenticated 401 retries same original body once`() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(401))
            server.enqueue(MockResponse().setBody("ok"))
            val lease = Lease()
            val client = client(lease)
            client.newCall(Request.Builder().url(server.url("/purchase")).header("Authorization", "Bearer old")
                .post("payload".toRequestBody()).build()).execute().use { assertEquals(200, it.code) }
            val first = server.takeRequest(); val second = server.takeRequest()
            assertEquals("Bearer refreshed", first.getHeader("Authorization"))
            assertEquals("Bearer rotated", second.getHeader("Authorization"))
            assertEquals("payload", first.body.readUtf8()); assertEquals("payload", second.body.readUtf8())
            assertEquals(1, lease.preparations); assertEquals(1, lease.renewals)
        }
    }

    @Test fun `second 401 is terminal and 503 does not refresh or repeat transaction`() {
        for (code in listOf(401, 503)) MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(code).setHeader("Retry-After", "0"))
            server.enqueue(MockResponse().setResponseCode(401))
            val lease = Lease()
            client(lease).newCall(Request.Builder().url(server.url("/transaction"))
                .header("Authorization", "Bearer old").post("body".toRequestBody()).build()).execute().use {
                assertEquals(if (code == 401) 401 else 503, it.code)
            }
            assertEquals(if (code == 401) 2 else 1, server.requestCount)
            assertEquals(if (code == 401) 1 else 0, lease.renewals)
        }
    }

    @Test fun `stale lease fails before wire and failed 401 refresh does not replay`() {
        MockWebServer().use { server ->
            val lease = Lease(prepared = null)
            assertThrows(IOException::class.java) {
                client(lease).newCall(Request.Builder().url(server.url("/")).header("Authorization", "Bearer old").build()).execute()
            }
            assertEquals(0, server.requestCount)
            lease.prepared = "Bearer old"; lease.renewed = null
            server.enqueue(MockResponse().setResponseCode(401))
            client(lease).newCall(Request.Builder().url(server.url("/")).header("Authorization", "Bearer old").build())
                .execute().use { assertEquals(401, it.code) }
            assertEquals(1, server.requestCount)
        }
    }

    @Test fun `public call bypasses refresh and one shot original is never replayed`() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(200))
            val lease = Lease()
            client(lease).newCall(Request.Builder().url(server.url("/public")).build()).execute().close()
            assertEquals(0, lease.preparations)
            val body = object : RequestBody() {
                override fun contentType() = null
                override fun isOneShot() = true
                override fun writeTo(sink: BufferedSink) { sink.writeUtf8("once") }
            }
            server.enqueue(MockResponse().setResponseCode(401))
            client(lease).newCall(Request.Builder().url(server.url("/transaction"))
                .header("Authorization", "Bearer old").post(body).build()).execute().use { assertEquals(401, it.code) }
            assertEquals(0, lease.renewals)
            assertNull(server.takeRequest(1, TimeUnit.SECONDS)?.getHeader("Authorization"))
            assertNotNull(server.takeRequest(1, TimeUnit.SECONDS))
            assertEquals(2, server.requestCount)
        }
    }

    private fun client(lease: Lease) = OkHttpClient.Builder().retryOnConnectionFailure(false)
        .addInterceptor(SessionRefreshInterceptor(SessionRefreshHandler { lease }))
        .addInterceptor(SingleAttemptTransactionInterceptor()).build()
}
