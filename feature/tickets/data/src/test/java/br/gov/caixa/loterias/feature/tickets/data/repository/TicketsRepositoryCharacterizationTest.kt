package br.gov.caixa.loterias.feature.tickets.data.repository

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.core.network.model.AppParametersDto
import br.gov.caixa.loterias.core.network.model.AppParametersMapDto
import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.lang.reflect.Proxy
import java.math.BigDecimal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.feature.tickets.data.repository.TicketsRepositoryImpl
import br.gov.caixa.loterias.feature.tickets.data.datasource.remote.TicketsRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.tickets.data.datasource.remote.TicketsRemoteDataSource
import br.gov.caixa.loterias.core.data.datasource.remote.AppConfigurationRemoteDataSource

class TicketsRepositoryCharacterizationTest {
    @Test fun `receipt loading propagates cancellation from each remote stage`() = runTest {
        for (stage in listOf("check", "receipt", "prize")) {
            val cancellation = CancellationException(stage)
            val source = ReceiptSource().apply { failureStage = stage; failure = cancellation }
            val original = bet(if (stage == "check") 4 else 100)
            val observed = runCatching { TicketsRepositoryImpl(source).loadConfirmedBet(original) }.exceptionOrNull()

            assertSame(stage, cancellation, observed)
            assertEquals(stage, when (stage) {
                "check" -> listOf("check")
                "receipt" -> listOf("receipt")
                else -> listOf("receipt", "prize")
            }, source.calls)
        }
    }

    @Test fun `receipt hydration retains list combo recurring origin and nonzero list prize`() = runTest {
        val source = ReceiptSource().apply {
            receipt = json("""{"aposta":{"id":7,"modalidade":"QUINA","situacao":{"valor":4,"descricao":"EFETIVADA"},"combo":false,"quantidadeTeimosinhas":2},"nsbi":"receipt"}""")
        }
        val loaded = TicketsRepositoryImpl(source).loadConfirmedBet(
            bet(9).copy(status = "Lista", combo = true, originalRecurringBet = true, prize = BigDecimal("12.50")),
        ) as ServiceResult.Success

        with(loaded.value) {
            assertEquals(9L, statusId)
            assertEquals("Lista", status)
            assertTrue(combo)
            assertTrue(originalRecurringBet)
            assertEquals(BigDecimal("12.50"), prize)
            assertEquals("receipt", receipt?.nsbi)
        }
        assertEquals(listOf("receipt"), source.calls)
    }

    @Test fun `receipt for another bet cannot hydrate the requested bet`() = runTest {
        val source = ReceiptSource().apply { receipt = json("""{"id":8,"modalidade":"QUINA"}""") }
        val result = TicketsRepositoryImpl(source).loadConfirmedBet(bet(9)) as ServiceResult.Failed

        assertTrue(result.error is AppError.Validation)
        assertEquals(listOf("receipt"), source.calls)
    }

    @Test fun `winning receipt requires actual prize and liquid amount`() = runTest {
        for (prize in listOf("{}", """{"premio":{}}""", """{"premio":{"valorLiquido":"invalid"}}""")) {
            val source = ReceiptSource().apply { prizeDetails = json(prize) }
            val result = TicketsRepositoryImpl(source).loadConfirmedBet(bet(100))

            assertTrue(prize, result is ServiceResult.Failed && result.error is AppError.Validation)
            assertEquals(listOf("receipt", "prize"), source.calls)
        }
    }

    @Test fun `history retries absent configuration then caches the first actual parameters`() = runTest {
        var configurationCalls = 0
        val configuration = object : AppConfigurationRemoteDataSource {
            override suspend fun isVersionSupported(versionName: String) = true
            override suspend fun appParameters(): AppParametersMapDto? {
                configurationCalls++
                return if (configurationCalls == 1) null else AppParametersMapDto(AppParametersDto())
            }
        }
        val source = historySource(configuration)

        repeat(3) { source.confirmedBets(emptyMap()) }

        assertEquals(2, configurationCalls)
    }

    @Test fun `history does not cache configuration failures and preserves cancellation`() = runTest {
        val cancelled = CancellationException("configuration")
        var configurationCalls = 0
        val configuration = object : AppConfigurationRemoteDataSource {
            override suspend fun isVersionSupported(versionName: String) = true
            override suspend fun appParameters(): AppParametersMapDto {
                configurationCalls++
                if (configurationCalls == 1) throw cancelled
                return AppParametersMapDto(AppParametersDto())
            }
        }
        val source = historySource(configuration)

        assertSame(cancelled, runCatching { source.confirmedBets(emptyMap()) }.exceptionOrNull())
        source.confirmedBets(emptyMap())
        source.confirmedBets(emptyMap())
        assertEquals(2, configurationCalls)
    }

    private fun historySource(configuration: AppConfigurationRemoteDataSource): TicketsRemoteDataSourceImpl {
        val api = Proxy.newProxyInstance(BackendApi::class.java.classLoader, arrayOf(BackendApi::class.java)) { _, method, _ ->
            require(method.name == "confirmedBets")
            ApiEnvelope(json("[]"))
        } as BackendApi
        return TicketsRemoteDataSourceImpl(
            BackendApiProvider().apply { replace(api) }, configurationSource = configuration,
        )
    }

    private class ReceiptSource : TicketsRemoteDataSource {
        var receipt: JsonElement = json("""{"aposta":{"id":7,"modalidade":"QUINA","situacao":{"valor":4}}}""")
        var prizeDetails: JsonElement = json("""{"premio":{"valorLiquido":10}}""")
        var failureStage: String? = null
        var failure: Exception? = null
        val calls = mutableListOf<String>()
        private fun response(stage: String, payload: JsonElement): JsonElement {
            calls += stage
            if (failureStage == stage) throw requireNotNull(failure)
            return payload
        }
        override suspend fun checkConfirmedBet(betId: Long) = response("check", json("""{"valor":100,"descricao":"Premiada"}"""))
        override suspend fun confirmedBetReceipt(betId: Long, paid: Boolean, historical: Boolean) = response("receipt", receipt)
        override suspend fun confirmedBetPrizeDetails(betId: Long) = response("prize", prizeDetails)
        override suspend fun confirmedBets(filters: Map<String, String>): JsonElement? = null
        override suspend fun purchases(filters: Map<String, String>): JsonElement? = null
        override suspend fun purchaseBets(purchaseId: Long): JsonElement? = null
        override suspend fun checkTicket(code: String, qrCode: Boolean): JsonElement? = null
        override suspend fun confirmedBetsConfiguration(): JsonElement? = null
        override suspend fun purchaseMonths(): JsonElement? = null
        override suspend fun purchasePaymentMethods(): JsonElement? = null
        override suspend fun purchaseStatuses(paymentMethodId: Long?): JsonElement? = null
    }

    companion object {
        private fun json(value: String): JsonElement = JsonParser.parseString(value)
        private fun bet(status: Long) = ConfirmedBet(7, "Quina", 1, "", "", emptyList(), statusId = status)
    }
}
