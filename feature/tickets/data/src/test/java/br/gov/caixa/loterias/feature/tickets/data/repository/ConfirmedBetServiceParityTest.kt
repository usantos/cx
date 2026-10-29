package br.gov.caixa.loterias.feature.tickets.data.repository

import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.core.network.model.AppParametersDto
import br.gov.caixa.loterias.core.network.model.AppParametersMapDto
import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.api.BetsHistoryApi
import br.gov.caixa.loterias.core.network.provider.BetsHistoryApiProvider
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.lang.reflect.Proxy
import br.gov.caixa.loterias.feature.tickets.data.repository.TicketsRepositoryImpl
import br.gov.caixa.loterias.feature.tickets.data.datasource.remote.TicketsRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.tickets.data.datasource.remote.TicketsRemoteDataSource
import br.gov.caixa.loterias.core.data.datasource.remote.AppConfigurationRemoteDataSource

class ConfirmedBetServiceParityTest {
    private val calls = mutableListOf<String>()
    private var redirect: String? = null
    private var prizeRedirect: String? = null
    private fun source() = TicketsRemoteDataSourceImpl(BackendApiProvider().apply {
        replace(Proxy.newProxyInstance(BackendApi::class.java.classLoader, arrayOf(BackendApi::class.java)) { _, method, _ ->
            calls += method.name
            when(method.name) {
                "checkBet" -> ApiEnvelope(JsonParser.parseString("""{"valor":100,"descricao":"PREMIADA"}"""), redirect = redirect)
                "betReceipt" -> ApiEnvelope(JsonParser.parseString("""{"aposta":{"id":7,"modalidade":"MEGA_SENA","concursoInicial":2800,"numerosSelecionados":[[1,2,3,4,5,6]],"situacao":{"valor":4,"descricao":"EFETIVADA"}}}"""))
                "prizeDetails" -> ApiEnvelope(JsonParser.parseString("""{"aposta":{"id":7,"modalidade":"MEGA_SENA"},"premio":{"valorLiquido":10.5,"concursos":[{"concurso":{"numero":2800},"situacao":"PREMIADA","faixas":[{"nome":"Sena","quantidadeAcertos":6,"valorLiquido":10.5}]}]}}"""), redirect = prizeRedirect)
                "prizeReceipt", "historicalPrizeReceipt" -> ApiEnvelope(JsonParser.parseString("""{"aposta":{"id":7,"modalidade":"MEGA_SENA","concursoInicial":2800,"numerosSelecionados":[[1,2,3,4,5,6]],"situacao":{"valor":4,"descricao":"EFETIVADA"}},"premio":{"valorLiquido":10.5,"dataPagamento":"13/10/2026","localPagamento":"Lotérica"}}"""))
                else -> error("Unexpected call: ${method.name}")
            }
        } as BackendApi)
    })
    private fun bet(status: Long) = ConfirmedBet(7, "Mega Sena", 2800, "", "", emptyList(), statusId = status, combo = true)

    @Test fun `ordinary SILCE bet loads receipt selections`() = runTest {
        val loaded = TicketsRepositoryImpl(source()).loadConfirmedBet(bet(9)) as ServiceResult.Success
        assertEquals(listOf("betReceipt"), calls)
        assertEquals(listOf(1,2,3,4,5,6), loaded.value.numbers)
        assertTrue(loaded.value.combo)
        assertEquals(java.math.BigDecimal.ZERO, loaded.value.prize)
        assertNull(loaded.value.receipt?.netPrize)
    }
    @Test fun `paid and historical paid bets use their distinct receipt endpoints`() = runTest {
        val repository = TicketsRepositoryImpl(source())
        val paid = repository.loadConfirmedBet(bet(11)) as ServiceResult.Success
        val historical = repository.loadConfirmedBet(bet(3)) as ServiceResult.Success
        assertEquals(11L, paid.value.statusId)
        assertEquals(3L, historical.value.statusId)
        assertTrue(paid.value.redeemedPrize)
        assertTrue(historical.value.redeemedPrize)
        assertEquals(listOf("prizeReceipt", "historicalPrizeReceipt"), calls)
    }
    @Test fun `microservice receipt preserves the list status`() = runTest {
        val loaded = TicketsRepositoryImpl(source()).loadConfirmedBet(bet(100).copy(backend = br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsBackend.MICROSERVICE)) as ServiceResult.Success
        assertEquals(100L, loaded.value.statusId)
        assertEquals(listOf("betReceipt", "prizeDetails"), calls)
    }
    @Test fun `unresolved status is checked before hydration and retained`() = runTest {
        val loaded = TicketsRepositoryImpl(source()).loadConfirmedBet(bet(101)) as ServiceResult.Success
        assertEquals(listOf("checkBet", "betReceipt", "prizeDetails"), calls)
        assertEquals(100L, loaded.value.statusId)
    }
    @Test fun `winner with zero list prize loads net amount and award breakdown`() = runTest {
        val loaded = TicketsRepositoryImpl(source()).loadConfirmedBet(bet(100)) as ServiceResult.Success
        assertEquals(listOf("betReceipt", "prizeDetails"), calls)
        assertEquals(java.math.BigDecimal("10.5"), loaded.value.prize)
        assertEquals(100L, loaded.value.statusId)
        assertEquals("Sena", loaded.value.receipt?.awards?.single()?.name)
        assertEquals(2800, loaded.value.receipt?.awards?.single()?.contest)
        assertFalse(loaded.value.redeemedPrize)
    }
    @Test fun `prize detail successful redirect rejects hydration and blocks actions`() = runTest {
        prizeRedirect = "login"
        val result = TicketsRepositoryImpl(source()).loadConfirmedBet(bet(100)) as ServiceResult.Rejected
        assertEquals(ServiceFailure.RedirectRequired(ServiceRedirect.LOGIN), result.failure)
        assertEquals(listOf("betReceipt", "prizeDetails"), calls)
    }
    @Test fun `successful check redirect stops hydration and propagates destination`() = runTest {
        redirect = "login"
        val result = TicketsRepositoryImpl(source()).loadConfirmedBet(bet(101)) as ServiceResult.Rejected
        assertEquals(ServiceFailure.RedirectRequired(ServiceRedirect.LOGIN), result.failure)
        assertEquals(listOf("checkBet"), calls)
    }
    @Test fun `malformed check response cannot reuse stale status as successful refresh`() = runTest {
        val remote = object : TicketsRemoteDataSource by source() {
            override suspend fun checkConfirmedBet(betId: Long): JsonElement? = JsonParser.parseString("""{"descricao":"Efetivada"}""")
        }
        val result = TicketsRepositoryImpl(remote).loadConfirmedBet(bet(4)) as ServiceResult.Failed
        assertEquals(br.gov.caixa.loterias.core.common.result.AppError.Validation("Situação da aposta incompleta"), result.error)
        assertTrue(calls.isEmpty())
    }

    @Test fun `Nova API successful recovery redirect is retained as service decision`() = runTest {
        val history = BetsHistoryApiProvider().apply {
            configureDynamicClients(setOf("synthetic.example")) {
                Proxy.newProxyInstance(BetsHistoryApi::class.java.classLoader, arrayOf(BetsHistoryApi::class.java)) { _, method, _ ->
                    when (method.name) {
                        "novaApiConfirmedBets" -> ApiEnvelope(JsonParser.parseString("[]"))
                        "recoverBet" -> ApiEnvelope<JsonElement>(redirect = "aceitar-termo-de-uso")
                        else -> error("Unexpected Nova call: ${method.name}")
                    }
                } as BetsHistoryApi
            }
        }
        val config = object : AppConfigurationRemoteDataSource {
            override suspend fun isVersionSupported(versionName: String) = true
            override suspend fun appParameters() = AppParametersMapDto(AppParametersDto(
                pilotBetsApiBaseUrl = "https://synthetic.example/", novaApiCpfRolloutPercentage = 100,
            ))
        }
        val remote = TicketsRemoteDataSourceImpl(BackendApiProvider(), history, config)
        remote.confirmedBets(emptyMap())
        val result = TicketsRepositoryImpl(remote).loadConfirmedBet(bet(9).copy(
            backend = br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsBackend.NOVA_API,
        )) as ServiceResult.Rejected
        assertEquals(ServiceFailure.RedirectRequired(ServiceRedirect.ACCEPT_TERMS), result.failure)
    }

    @Test fun `check transport failure stops receipt hydration`() = runTest {
        val remote = object : TicketsRemoteDataSource by source() {
            override suspend fun checkConfirmedBet(betId: Long): JsonElement? = throw java.io.IOException("offline")
        }
        assertTrue(TicketsRepositoryImpl(remote).loadConfirmedBet(bet(101)) is ServiceResult.Rejected)
        assertTrue(calls.isEmpty())
    }
}
