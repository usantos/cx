package br.gov.caixa.loterias.core.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import okio.BufferedSink
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.network.logging.SanitizedNetworkInspector

class SanitizedNetworkInspectorTest {
    @Test fun `inspection copies redact secrets and preserve the real one shot call`() = MockWebServer().use { server ->
        server.enqueue(MockResponse().setBody("{\"payload\":1}"))
        var writes = 0
        var observed: Request? = null
        val inspector = Interceptor { chain -> observed = chain.request(); chain.proceed(chain.request()) }
        val client = OkHttpClient.Builder().addInterceptor(SanitizedNetworkInspector(inspector)).build()
        val body = object : RequestBody() {
            override fun contentType() = "application/json".toMediaType()
            override fun contentLength() = 26L
            override fun isOneShot() = true
            override fun writeTo(sink: BufferedSink) { writes++; sink.writeUtf8("{\"cpf\":\"private-document\"}") }
        }
        val request = Request.Builder().url(server.url("/apostas?mes=45&cpf=private-query"))
            .header("Authorization", "Bearer private-bearer").post(body).build()
        client.newCall(request).execute().use { response ->
            assertEquals("{\"payload\":1}", response.body.string())
            assertEquals(request.url, response.request.url)
        }
        val actual = server.takeRequest()
        assertEquals(1, writes)
        assertEquals("Bearer private-bearer", actual.getHeader("Authorization"))
        assertTrue(actual.path!!.contains("cpf=private-query"))
        assertEquals("{\"cpf\":\"private-document\"}", actual.body.readUtf8())
        assertFalse(observed!!.url.toString().contains("private-query"))
        assertFalse(observed!!.header("Authorization")!!.contains("private-bearer"))
        val buffer = Buffer(); observed!!.body!!.writeTo(buffer)
        assertFalse(buffer.readUtf8().contains("private-document"))
    }
}
