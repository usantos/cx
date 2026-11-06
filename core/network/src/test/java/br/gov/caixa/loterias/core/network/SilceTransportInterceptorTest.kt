package br.gov.caixa.loterias.core.network

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import br.gov.caixa.loterias.core.network.interceptor.SingleAttemptTransactionInterceptor
import br.gov.caixa.loterias.core.network.interceptor.SilceTransportInterceptor

class SilceTransportInterceptorTest {
    @Test fun `receipt downloads with Disable Crypto retain ordinary paths for bet and prize`() = withServer { server, client ->
        for (kind in listOf("aposta", "premio")) {
            val path = BASE + "apostas/7/comprovante-$kind-app-pdf"
            client.newCall(Request.Builder().url(server.url(path))
                .header("Disable-Crypto", "true").header("Authorization", "Bearer receipt-test").build())
                .execute().close()
            val sent = server.takeRequest()
            assertEquals(path, sent.path)
            assertEquals("true", sent.getHeader("Disable-Crypto"))
            assertEquals("security=false", sent.getHeader("Cookie"))
            assertEquals("3", sent.getHeader("Subcanal"))
            assertEquals("application/json", sent.getHeader("Content-Type"))
            assertEquals("Bearer receipt-test", sent.getHeader("Authorization"))
        }
    }

    @Test
    fun `startup uses legacy base64 path and channel headers`() = withServer { server, client ->
        client.newCall(Request.Builder().url(server.url(BASE + "app/valida-versao/1.24.0")).build()).execute().close()
        val request = server.takeRequest()
        assertEquals(BASE + "YXBwL3ZhbGlkYS12ZXJzYW8vMS4yNC4w", request.path)
        assertEquals("3", request.getHeader("Subcanal"))
        assertEquals("security=false", request.getHeader("Cookie"))
        assertEquals("application/json", request.getHeader("Content-Type"))
    }

    @Test
    fun `queries retain unicode duplicate parameters and legacy framing`() = withServer { server, client ->
        val url = server.url(BASE + "duvidas").newBuilder()
            .addQueryParameter("secao", "2").addQueryParameter("nome", "São Paulo & interior")
            .addQueryParameter("secao", "3").addQueryParameter("ignored", null).build()
        client.newCall(Request.Builder().url(url).build()).execute().close()
        val wire = server.takeRequest().requestUrl!!
        assertEquals(BASE + "ZHV2aWRhcw==/", wire.encodedPath)
        assertEquals(setOf("q"), wire.queryParameterNames)
        val envelope = wire.queryParameter("q")!!.toByteArray(Charsets.ISO_8859_1)
        assertEquals("secao==2&&nome==São Paulo & interior&&secao==3&&", decrypt(envelope).toString(Charsets.UTF_8))
    }

    @Test
    fun `legacy json body uses ISO bytes and base64 envelope without changing authorization`() = withServer { server, client ->
        val body = "{\"nome\":\"João\"}"
        val request = Request.Builder().url(server.url(BASE + "cadastro"))
            .header("Authorization", "Bearer test-token")
            .post(body.toRequestBody("application/json".toMediaType())).build()
        client.newCall(request).execute().close()
        val sent = server.takeRequest()
        assertEquals("Bearer test-token", sent.getHeader("Authorization"))
        assertEquals(body, decrypt(Base64.getDecoder().decode(sent.body.readUtf8())).toString(Charsets.ISO_8859_1))
    }

    @Test
    fun `cloud contracts retain standard paths query and json`() = withServer { server, client ->
        client.newCall(Request.Builder().url(server.url(BASE + "publico/modalidades?id=3"))
            .post("{}".toRequestBody("application/json".toMediaType())).build()).execute().close()
        val request = server.takeRequest()
        assertEquals(BASE + "publico/modalidades?id=3", request.path)
        assertEquals("{}", request.body.readUtf8())
        client.newCall(Request.Builder().url(server.url(BASE + "logado/combos")).build()).execute().close()
        assertEquals(BASE + "logado/combos", server.takeRequest().path)
    }

    @Test
    fun `other paths and origins receive no legacy headers or encoding`() = withServer { server, client ->
        client.newCall(Request.Builder().url(server.url("/oauth/token")).build()).execute().close()
        assertNull(server.takeRequest().getHeader("Subcanal"))
        MockWebServer().use { other ->
            other.start()
            other.enqueue(MockResponse().setBody("{}"))
            client.newCall(Request.Builder().url(other.url(BASE + "app/valida-versao/1.24.0")).build()).execute().close()
            assertNull(other.takeRequest().getHeader("Cookie"))
        }
        val cloudClient = OkHttpClient.Builder().addInterceptor(SilceTransportInterceptor(server.url("/carrinho/v1/"))).build()
        cloudClient.newCall(Request.Builder().url(server.url("/carrinho/v1/publico/modalidades")).build()).execute().close()
        assertNull(server.takeRequest().getHeader("Subcanal"))
    }

    @Test
    fun `v2 history keeps its plain path while encoding parameters like the legacy microservice`() {
        MockWebServer().use { server ->
            server.start()
            server.enqueue(MockResponse().setBody("{}"))
            val base = server.url("/silce-servico-rest/rest/v2/")
            val client = OkHttpClient.Builder().addInterceptor(SilceTransportInterceptor(base)).build()
            client.newCall(Request.Builder().url(base.resolve("apostas?pagina=1")!!).build()).execute().close()
            val wire = server.takeRequest().requestUrl!!
            assertEquals("/silce-servico-rest/rest/v2/apostas/", wire.encodedPath)
            assertEquals("pagina==1&&", decrypt(wire.queryParameter("q")!!.toByteArray(Charsets.ISO_8859_1)).toString(Charsets.UTF_8))
        }
    }

    @Test
    fun `empty post bodies use the legacy empty json object`() = withServer { server, client ->
        client.newCall(Request.Builder().url(server.url(BASE + "confirmar"))
            .post("".toRequestBody()).build()).execute().close()
        assertEquals("{}", decrypt(Base64.getDecoder().decode(server.takeRequest().body.readUtf8())).toString(Charsets.ISO_8859_1))
    }

    @Test fun `encoded SILCE transaction retains one shot guard against automatic 503 followup`() {
        MockWebServer().use { server ->
            server.start()
            server.enqueue(MockResponse().setResponseCode(503).setHeader("Retry-After", "0"))
            server.enqueue(MockResponse().setBody("{}"))
            val client = OkHttpClient.Builder().retryOnConnectionFailure(false)
                .addInterceptor(SingleAttemptTransactionInterceptor())
                .addInterceptor(SilceTransportInterceptor(server.url(BASE))).build()
            client.newCall(Request.Builder().url(server.url(BASE + "apostas/7/resgatar-premio"))
                .post("{}".toRequestBody()).build()).execute().use { assertEquals(503, it.code) }
            assertEquals(1, server.requestCount)
            assertEquals("{}", decrypt(Base64.getDecoder().decode(server.takeRequest().body.readUtf8())).toString(Charsets.ISO_8859_1))
        }
    }

    private fun decrypt(envelope: ByteArray): ByteArray {
        val key = Base64.getDecoder().decode("QEm06ZAThg1E314zziahsg==")
        val iv = Base64.getDecoder().decode("8hQLtNV44Y1bIeONmlr+Dg==")
        assertEquals("!#!#!", envelope.copyOfRange(envelope.size - 42, envelope.size - 37).toString(Charsets.ISO_8859_1))
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(iv))
        return cipher.doFinal(envelope.copyOfRange(0, envelope.size - 42))
    }

    private fun withServer(block: (MockWebServer, OkHttpClient) -> Unit) {
        MockWebServer().use { server ->
            server.start()
            repeat(4) { server.enqueue(MockResponse().setBody("{}")) }
            val client = OkHttpClient.Builder().addInterceptor(SilceTransportInterceptor(server.url(BASE))).build()
            block(server, client)
        }
    }

    private companion object { const val BASE = "/silce-servico-rest/rest/v1/" }
}
