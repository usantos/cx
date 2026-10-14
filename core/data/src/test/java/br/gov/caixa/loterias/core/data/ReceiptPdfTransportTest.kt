package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.network.*
import kotlinx.coroutines.test.runTest
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test

class ReceiptPdfTransportTest {
    @Test fun `purchase details retain server clock from the actual response envelope`() = runTest {
        val body = """{"dataHoraServidor":"2020-01-01T15:00:00.000","payload":{"compra":{"id":9003,"dataHoraExpiracaoPagamento":"2020-01-01T15:10:00.000"},"apostas":[]}}"""
        val api = NetworkClientFactory.createLegacy("https://example.invalid/", AuthTokenProvider { null },
            OkHttpClient.Builder().addInterceptor { chain ->
                Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(200)
                    .message("OK").body(body.toResponseBody()).build()
            })
        val repository = RemoteTicketsRepository(RetrofitTicketsRemoteDataSource(LegacyBackendApiProvider().apply { replace(api) }))
        val purchase = br.gov.caixa.loterias.core.domain.tickets.Purchase(9003, "", "", "Pix", java.math.BigDecimal.TEN)
        assertEquals("2020-01-01T15:00:00.000", repository.purchaseDetails(purchase).serverDateTime)
    }

    @Test fun `receipt consumer decodes Base64 and preserves binary PDF for both endpoints`() = runTest {
        var bytes = ByteArray(0)
        val api = NetworkClientFactory.createLegacy("https://example.invalid/", AuthTokenProvider { null },
            OkHttpClient.Builder().addInterceptor { chain ->
                Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(200)
                    .message("OK").body(bytes.toResponseBody()).build()
            })
        val source = RetrofitRetailRemoteDataSource(LegacyBackendApiProvider().apply { replace(api) },
            PrizeBackendApiProvider())
        for (prize in listOf(false, true)) {
            for ((body, expected) in listOf(
                "JVBERi0xLjcK".toByteArray() to "%PDF-1.7\n".toByteArray(),
                byteArrayOf(37,80,68,70,45,49,46,55,10,-1,0) to byteArrayOf(37,80,68,70,45,49,46,55,10,-1,0),
            )) {
                bytes = body
                val actual = if (prize) source.prizeReceiptPdf(7) else source.betReceiptPdf(7)
                assertArrayEquals(expected, actual)
            }
        }
    }
}
