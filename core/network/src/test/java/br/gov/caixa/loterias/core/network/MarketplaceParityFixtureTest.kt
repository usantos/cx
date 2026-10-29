package br.gov.caixa.loterias.core.network

import br.gov.caixa.loterias.feature.betting.data.dto.BettingParametersDto
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplacePageDto

import com.google.gson.Gson
import com.google.gson.JsonParser
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.mock.MockBackendScenario
import br.gov.caixa.loterias.core.network.interceptor.MockBackendInterceptor
import br.gov.caixa.loterias.core.network.mock.fixture.MockBackendFixtures

class MarketplaceParityFixtureTest {
    private val gson = Gson()
    private fun payload(path: String, scenario: String = "MARKETPLACE_PARITY") =
        JsonParser.parseString(MockBackendFixtures.resolve(path, MockBackendScenario.from(scenario)).body)
            .asJsonObject.get("payload")

    @Test fun `parity scenario supplies all four selectable pool modalities`() {
        val dto = gson.fromJson(payload("publico/parametros-simulacao"), BettingParametersDto::class.java)
        assertEquals(listOf(2, 8, 3, 9), dto.availablePoolModalities.orEmpty().map { it.modality?.id })
        assertEquals(listOf("MEGA_SENA", "LOTOFACIL", "QUINA", "MAIS_MILIONARIA"),
            dto.availablePoolModalities.orEmpty().map { it.modalityCode })
        assertEquals(4, dto.parameters.orEmpty().size)
        assertTrue(dto.availablePoolModalities.orEmpty().all { it.contestNumber != null && it.resaleClosingDate == "10/09/2099" })
    }

    @Test fun `pool fixture is complete for both native and Compose carousel and list`() {
        val raw = payload("boloes/recuperar-boloes-disponiveis")
        val dto = gson.fromJson(raw, MarketplacePageDto::class.java)
        assertEquals(6, dto.totalRecords)
        assertEquals(1, dto.currentPage)
        assertEquals(1, dto.lastPage)
        assertEquals((1..6).map { "B$it" }, dto.pools.orEmpty().map { it.code })
        dto.pools.orEmpty().forEach {
            assertEquals("MEGA_SENA", it.modality)
            assertEquals("12.50", it.lastSharePriceWithFee?.toPlainString())
            assertEquals("DF", it.state?.abbreviation)
            assertEquals("Brasília", it.city?.name)
            assertEquals("10.00", it.regularSharePriceWithFee?.subtract(it.serviceFeePerShare)?.toPlainString())
        }
        assertTrue(raw.asJsonObject.getAsJsonArray("cotas").all { it.asJsonObject.get("qtdCotasBolao").asInt == 1 })
    }

    @Test fun `regional parity searches do not repeat municipality pools in outer areas`() {
        val interceptor = MockBackendInterceptor(MockBackendOptions(true, MockBackendScenario.MARKETPLACE_PARITY))
        val client = okhttp3.OkHttpClient.Builder().addInterceptor(interceptor).build()
        for (area in 0..2) {
            val request = okhttp3.Request.Builder().url("https://mock.invalid/rest/v1/boloes/recuperar-boloes-disponiveis?tipoConsulta=$area").build()
            client.newCall(request).execute().use { response ->
                val pools = JsonParser.parseString(response.body!!.string()).asJsonObject.getAsJsonObject("payload").getAsJsonArray("cotas")
                assertEquals("area $area", if (area == 0) 6 else 0, pools.size())
            }
        }
    }

    @Test fun `October variant changes campaign configuration while preserving fixture data`() {
        val normal = payload("parametros/configuraveis-app").asJsonObject.getAsJsonObject("mapa")
        val october = payload("parametros/configuraveis-app", "MARKETPLACE_PARITY_OCTOBER").asJsonObject.getAsJsonObject("mapa")
        assertFalse(normal.get("vigenciaMesOutubroRosa").asBoolean)
        assertTrue(october.get("vigenciaMesOutubroRosa").asBoolean)
        assertTrue(october.get("vigenciaOutubroRosaMegaSena").asBoolean)
        assertEquals(payload("boloes/recuperar-boloes-disponiveis"), payload("boloes/recuperar-boloes-disponiveis", "MARKETPLACE_PARITY_OCTOBER"))
        assertEquals("Pessoa Mock", payload("usuarios/recuperar-dados").asJsonObject.get("nome").asString)
        assertEquals(6, payload("boloes/consultar-loterica-favorita").asJsonArray.size())
        assertEquals(payload("logado/carrinhos", "HAPPY_PATH"), payload("logado/carrinhos"))
    }
}
