package br.gov.caixa.loterias.core.network

import com.google.gson.JsonObject
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.interceptor.singleAttempt
import br.gov.caixa.loterias.core.network.client.NetworkClientFactory

class SingleAttemptTransactionInterceptorTest {
    @Test fun `body is one shot without altering payload metadata headers or request identity tag`() {
        val bytes = "{\"amount\":123,\"email\":\"pessoa@example.test\"}".encodeToByteArray()
        val tag = Any()
        val body = object : RequestBody() {
            override fun contentType() = "application/json; charset=utf-8".toMediaType()
            override fun contentLength() = bytes.size.toLong()
            override fun isDuplex() = true
            override fun writeTo(sink: BufferedSink) { sink.write(bytes) }
        }
        val original = Request.Builder().url("https://example.invalid/transaction").post(body)
            .header("Authorization", "private").tag(Any::class.java, tag).build()
        val prepared = original.singleAttempt()
        assertNotSame(original, prepared)
        assertFalse(original.body!!.isOneShot())
        assertEquals(original.url, prepared.url)
        assertEquals(original.method, prepared.method)
        assertEquals(original.headers, prepared.headers)
        assertSame(tag, prepared.tag(Any::class.java))
        with(prepared.body!!) {
            assertTrue(isOneShot())
            assertTrue(isDuplex())
            assertEquals(body.contentType(), contentType())
            assertEquals(body.contentLength(), contentLength())
            assertArrayEquals(bytes, Buffer().also { writeTo(it) }.readByteArray())
        }
        assertSame(prepared, prepared.singleAttempt())
        val get = Request.Builder().url("https://example.invalid/status").build()
        assertSame(get, get.singleAttempt())
    }

    @Test fun `purchase submission is one shot while polling remains an explicit GET`() = runTest {
        val requests = mutableListOf<Request>()
        val client = NetworkClientFactory.createPurchaseBackend("https://mock.caixa.gov.br/rest/v1/", AuthTokenProvider { null },
            OkHttpClient.Builder().addInterceptor { chain ->
                chain.proceed(chain.request()).also { requests += it.request }
            }, mockOptions = MockBackendOptions(true))
        client.submit(JsonObject())
        client.progress(1)
        assertEquals(listOf("POST", "GET"), requests.map { it.method })
        assertTrue(requests[0].body!!.isOneShot())
        assertFalse(requests[0].body!!.isDuplex())
        assertNull(requests[1].body)
    }
}
