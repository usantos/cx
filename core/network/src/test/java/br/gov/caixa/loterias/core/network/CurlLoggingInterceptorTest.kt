package br.gov.caixa.loterias.core.network

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.core.network.interceptor.NetworkDebugOptions
import br.gov.caixa.loterias.core.network.interceptor.CurlLoggingInterceptor

class CurlLoggingInterceptorTest {
    @Test fun `opt in logs readable sanitized bodies without changing the response`() = MockWebServer().use { server ->
        val responseJson = """{"payload":{"totalRegistros":1,"cpf":"private-document","tokens":{"access":"private-token"}}}"""
        server.enqueue(MockResponse().setHeader("Content-Type", "application/json").setBody(responseJson))
        val logs = mutableListOf<String>()
        val client = OkHttpClient.Builder()
            .addInterceptor(CurlLoggingInterceptor(NetworkDebugOptions(true, logs::add, includeBodies = true)))
            .build()
        val request = Request.Builder().url(server.url("/apostas?mes=45&cpf=private-query"))
            .post("""{"modalidade":2,"usuario":{"senha":"private-password"}}""".toRequestBody("application/json".toMediaType())).build()

        client.newCall(request).execute().use { assertEquals(responseJson, it.body.string()) }

        val output = logs.joinToString("\n")
        assertTrue(output.contains("REQUEST BODY"))
        assertTrue(output.contains("RESPONSE BODY"))
        assertTrue(output.contains("\"totalRegistros\":1"))
        assertTrue(output.contains("\"modalidade\":2"))
        assertFalse(output.contains("private-"))
    }

    @Test fun `body logging stays off unless explicitly enabled`() = MockWebServer().use { server ->
        server.enqueue(MockResponse().setBody("{\"payload\":\"private-response\"}"))
        val logs = mutableListOf<String>()
        val client = OkHttpClient.Builder().addInterceptor(CurlLoggingInterceptor(NetworkDebugOptions(true, logs::add))).build()
        client.newCall(Request.Builder().url(server.url("/")).build()).execute().close()
        assertFalse(logs.joinToString().contains("private-response"))
        assertFalse(logs.joinToString().contains("RESPONSE BODY"))
    }

    @Test
    fun `disabled logging emits nothing`() = MockWebServer().use { server ->
        server.enqueue(MockResponse())
        val logs = mutableListOf<String>()
        val client = OkHttpClient.Builder()
            .addInterceptor(CurlLoggingInterceptor(NetworkDebugOptions(false, logs::add)))
            .build()

        client.newCall(Request.Builder().url(server.url("/")).build()).execute().close()

        assertTrue(logs.isEmpty())
    }

    @Test
    fun `curl redacts credentials query values and body`() = MockWebServer().use { server ->
        server.enqueue(MockResponse().setResponseCode(201))
        val logs = mutableListOf<String>()
        val client = OkHttpClient.Builder()
            .addInterceptor(CurlLoggingInterceptor(NetworkDebugOptions(true, logs::add)))
            .build()
        val request = Request.Builder()
            .url(server.url("/pay?token=secret-value&public=value"))
            .header("Authorization", "Bearer secret")
            .header("X-Trace", "safe")
            .post("{\"cpf\":\"fixture-cpf-content\"}".toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().close()

        val output = logs.joinToString("\n")
        assertTrue(output.contains("curl --request 'POST'"))
        assertTrue(output.contains("██REDACTED██"))
        assertTrue(output.contains("[BODY REDACTED]"))
        assertTrue(output.contains("HTTP POST"))
        assertFalse(output.contains("secret-value"))
        assertFalse(output.contains("Bearer secret"))
        assertFalse(output.contains("fixture-cpf-content"))
    }

    @Test
    fun `curl safely escapes single quotes`() {
        val interceptor = CurlLoggingInterceptor(NetworkDebugOptions(true))
        val request = Request.Builder().url("https://example.test/").header("X-Value", "it's-safe").build()

        assertTrue(interceptor.toCurl(request).contains("it'\"'\"'s-safe"))
    }

    @Test
    fun `curl redacts payment and personal query parameters`() {
        val interceptor = CurlLoggingInterceptor(NetworkDebugOptions(true))
        val request = Request.Builder()
            .url("https://example.test/pay?bin=123456&email=user@example.test&public_key=key")
            .build()

        val curl = interceptor.toCurl(request)

        assertFalse(curl.contains("123456"))
        assertFalse(curl.contains("user@example.test"))
        assertFalse(curl.contains("public_key=key"))
    }

    @Test
    fun `failed call emits sanitized failure`() {
        val logs = mutableListOf<String>()
        val client = OkHttpClient.Builder()
            .addInterceptor(CurlLoggingInterceptor(NetworkDebugOptions(true, logs::add)))
            .build()

        assertThrows(Exception::class.java) {
            client.newCall(Request.Builder().url("https://127.0.0.1:1/?password=nope").build()).execute()
        }
        val output = logs.joinToString("\n")
        assertTrue(output.contains("falha"))
        assertFalse(output.contains("nope"))
    }
}
