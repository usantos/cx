package br.gov.caixa.loterias.core.network

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.junit.Assert.*
import org.junit.Test

class ConfirmedBetRangeApiTest {
    @Test fun `both PDF endpoints disable legacy crypto`() = runTest {
        MockWebServer().use { server ->
            repeat(2) { server.enqueue(MockResponse().setBody("JVBERi0xLjcK")) }
            val api = Retrofit.Builder().baseUrl(server.url("/rest/v1/"))
                .addConverterFactory(GsonConverterFactory.create()).build().create(LegacyBackendApi::class.java)
            api.betReceiptPdf(7).close()
            api.prizeReceiptPdf(7).close()
            for (path in listOf("comprovante-aposta-app-pdf", "comprovante-premio-app-pdf")) {
                val request = server.takeRequest()
                assertEquals("/rest/v1/apostas/7/$path", request.path)
                assertEquals("true", request.getHeader("Disable-Crypto"))
            }
        }
    }

    @Test fun `range query repeats concursos instead of collapsing to last value`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setHeader("Content-Type", "application/json").setBody("""{"payload":[]}"""))
            val api = Retrofit.Builder().baseUrl(server.url("/rest/v1/"))
                .addConverterFactory(GsonConverterFactory.create()).build().create(LegacyBackendApi::class.java)
            api.resultsForContests("megasena", listOf(2800,2801,2802))
            val request = server.takeRequest()
            assertEquals(listOf("2800", "2801", "2802"), request.requestUrl!!.queryParameterValues("concursos"))
            assertEquals("megasena", request.requestUrl!!.queryParameter("modalidade"))
        }
    }
    @Test fun `range and receipt fixtures contain usable business payload`() {
        val gson = com.google.gson.Gson()
        val range = MockBackendFixtures.resolve("/rest/v1/resultados", MockBackendScenario.HAPPY_PATH, contests = listOf(2800,2801))
        val rows = com.google.gson.JsonParser.parseString(range.body).asJsonObject.getAsJsonArray("payload")
        assertEquals(2, rows.size())
        assertEquals(2801, gson.fromJson(rows[1], DrawResultDto::class.java).contest!!.number)
        val listing = MockBackendFixtures.resolve("/rest/v1/apostas", MockBackendScenario.HAPPY_PATH)
        val status = com.google.gson.JsonParser.parseString(listing.body).asJsonObject
            .getAsJsonObject("payload").getAsJsonArray("resultado")[0].asJsonObject.getAsJsonObject("situacao")
        assertEquals(11, status["valor"].asInt)
        assertEquals("PAGA", status["descricao"].asString)
        val receipt = MockBackendFixtures.resolve("/rest/v1/apostas/7/comprovante", MockBackendScenario.HAPPY_PATH)
        val bet = com.google.gson.JsonParser.parseString(receipt.body).asJsonObject.getAsJsonObject("payload").getAsJsonObject("aposta")
        assertEquals(7L, bet["id"].asLong)
        assertTrue(bet["numerosSelecionados"].asJsonArray.size() > 0)
    }
}
