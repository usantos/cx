package br.gov.caixa.loterias.core.network

import br.gov.caixa.loterias.feature.retail.data.dto.PrizeRedemptionEnvelopeDto

import com.google.gson.Gson
import java.io.IOException
import kotlinx.coroutines.CancellationException
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import okio.BufferedSource
import org.junit.Assert.*
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import br.gov.caixa.loterias.core.network.error.BackendErrorMetadata
import br.gov.caixa.loterias.core.network.error.toBackendHttpFailure

class BackendHttpFailureTest {
    @Test fun `reads only business metadata and never retains raw error or secrets in exception text`() {
        val failure = exception("""{"codigo":"018003","mensagem":"Cadastrar?","payload":"private-token","tipo":1}""").toBackendHttpFailure()
        assertEquals(400, failure.statusCode)
        assertEquals("018003", failure.metadata?.code)
        assertEquals("Cadastrar?", failure.metadata?.message)
        assertEquals("1", failure.metadata?.type)
        assertNull(failure.metadata?.redirect)
        assertNull(failure.cause)
        assertEquals("Falha HTTP (400)", failure.message)
        assertEquals("BackendErrorMetadata(redacted)", failure.metadata.toString())
        val redirect = exception("""{"redirect":"login"}""", 401).toBackendHttpFailure()
        assertEquals("login", redirect.metadata?.redirect)
        assertNull(redirect.metadata?.code)
    }

    @Test fun `absent invalid and oversized envelopes preserve status without invented metadata`() {
        for (body in listOf("", "null", "[1]", "not-json", "x".repeat(65_537))) {
            val failure = exception(body, 503).toBackendHttpFailure()
            assertEquals(503, failure.statusCode)
            assertNull(failure.metadata)
        }
        assertEquals(BackendErrorMetadata(), exception("{}").toBackendHttpFailure().metadata)
        val exact = "{\"codigo\":\"018003\"}".padEnd(65_536)
        assertEquals("018003", exception(exact).toBackendHttpFailure().metadata?.code)
    }

    @Test fun `body closes on success invalid json IO failure and cancellation`() {
        for (injected in listOf(null, IOException("offline"), CancellationException("cancelled"))) {
            var closed = false
            val body = object : ResponseBody() {
                override fun contentType(): okhttp3.MediaType? = null
                override fun contentLength() = -1L
                override fun source(): BufferedSource {
                    injected?.let { throw it }
                    return Buffer().writeUtf8("malformed")
                }
                override fun close() { closed = true }
            }
            val result = runCatching { HttpException(Response.error<Unit>(400, body)).toBackendHttpFailure() }
            assertTrue(closed)
            if (injected == null) assertNull(result.getOrThrow().metadata)
            else assertSame(injected, result.exceptionOrNull())
        }
    }

    @Test fun `prize envelope distinguishes success payload from top level error and redirect`() {
        val empty = PrizeRedemptionEnvelopeDto()
        assertNull(empty.payload)
        assertNull(empty.code)
        assertNull(empty.redirect)
        assertNull(empty.message)
        val envelope = Gson().fromJson("""{"payload":"MI053","codigo":"018003","redirect":"login","mensagem":"Cadastrar?"}""", PrizeRedemptionEnvelopeDto::class.java)
        assertEquals("MI053", envelope.payload?.asString)
        assertEquals("018003", envelope.code)
        assertEquals("login", envelope.redirect)
        assertEquals("Cadastrar?", envelope.message)
        assertEquals("PrizeRedemptionEnvelopeDto(redacted)", envelope.toString())
    }

    private fun exception(body: String, status: Int = 400) = HttpException(Response.error<Unit>(status, body.toResponseBody()))
}
