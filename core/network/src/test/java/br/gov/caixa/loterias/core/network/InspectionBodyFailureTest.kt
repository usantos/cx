// Literais numéricos descrevem payloads sintéticos, níveis de API e limites esperados das fixtures.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.network

import br.gov.caixa.loterias.core.network.interceptor.CurlLoggingInterceptor
import br.gov.caixa.loterias.core.network.interceptor.NetworkDebugOptions
import br.gov.caixa.loterias.core.network.logging.NetworkTrafficSanitizer
import br.gov.caixa.loterias.core.network.logging.SanitizedNetworkInspector
import java.io.IOException
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import okio.BufferedSink
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class InspectionBodyFailureTest {
    @Test fun `readable request inspection redacts JSON and inspection serialization failure never blocks transport`() {
        for (failsInspection in listOf(false, true)) {
            var writes = 0
            var observed: Request? = null
            val json = "{\"cpf\":\"synthetic-private-document\",\"modality\":2}"
            val body = object : RequestBody() {
                override fun contentType() = "application/json".toMediaType()
                override fun contentLength() = json.toByteArray().size.toLong()
                override fun writeTo(sink: BufferedSink) {
                    writes++
                    if (failsInspection && writes == 1) throw IOException("private serialization detail")
                    sink.writeUtf8(json)
                }
            }
            val request = Request.Builder().url("https://example.test/read").post(body).build()
            val client = OkHttpClient.Builder()
                .addInterceptor(
                    SanitizedNetworkInspector(
                        Interceptor { chain ->
                            observed = chain.request()
                            chain.proceed(chain.request())
                        }
                    )
                )
                .addInterceptor { chain ->
                    assertSame(request.body, chain.request().body)
                    val original = Buffer()
                    chain.request().body!!.writeTo(original)
                    assertEquals(json, original.readUtf8())
                    Response.Builder().request(
                        chain.request()
                    ).protocol(Protocol.HTTP_1_1).code(200).message("OK").body("ok".toResponseBody()).build()
                }.build()
            client.newCall(request).execute().use { assertEquals("ok", it.body.string()) }
            val inspection = Buffer()
            observed!!.body!!.writeTo(inspection)
            val inspected = inspection.readUtf8()
            assertFalse(inspected.contains("synthetic-private-document"))
            assertFalse(inspected.contains("private serialization detail"))
            if (failsInspection) {
                assertEquals(
                    NetworkTrafficSanitizer.OMITTED_BODY,
                    inspected
                )
            } else {
                assertTrue(inspected.contains("\"modality\":2"))
            }
            assertEquals(2, writes)
        }
    }

    @Test fun `curl logger omits one shot body without consuming it before transport`() {
        val logs = mutableListOf<String>()
        var writes = 0
        val body = object : RequestBody() {
            override fun contentType() = "application/json".toMediaType()
            override fun contentLength() = 6L
            override fun isOneShot() = true
            override fun writeTo(sink: BufferedSink) {
                writes++
                sink.writeUtf8("secret")
            }
        }
        val client = OkHttpClient.Builder().addInterceptor(
            CurlLoggingInterceptor(NetworkDebugOptions(true, logs::add, includeBodies = true))
        )
            .addInterceptor { chain ->
                assertEquals(0, writes)
                val original = Buffer()
                chain.request().body!!.writeTo(original)
                assertEquals("secret", original.readUtf8())
                Response.Builder().request(
                    chain.request()
                ).protocol(Protocol.HTTP_1_1).code(200).message("OK").body("{}".toResponseBody()).build()
            }.build()
        client.newCall(Request.Builder().url("https://example.test/read").post(body).build()).execute().close()
        assertEquals(1, writes)
        assertTrue(logs.any { it.contains("one-shot or oversized request body omitted") })
        assertFalse(logs.joinToString().contains("secret"))
    }
}
