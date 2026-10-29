package br.gov.caixa.loterias.feature.betting.data.repository

import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.RapidBetConfiguration
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.feature.cart.data.dto.FavoriteCartBetDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryModalityDto
import br.gov.caixa.loterias.feature.betting.data.dto.RapidConfigurationDto
import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.RapidBettingRemoteDataSource
import br.gov.caixa.loterias.feature.betting.data.repository.RapidBettingRepositoryImpl
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.RapidBettingRemoteDataSourceImpl

class RapidBettingRepositoryImplTest {
    @Test fun `maps configuration saves stable payload and generates cart`() = runTest {
        val source = FakeSource(
            RapidConfigurationDto(
                minimumAmount = BigDecimal("30"), maximumAmount = BigDecimal("100"), minimumPrize = BigDecimal("1"),
                requiredNumbers = listOf(-1, 1, 1), excludedNumbers = listOf(0, 2),
                modalities = listOf(LotteryModalityDto(2, "Mega-Sena", "Especial", "MEGA_SENA"), LotteryModalityDto()),
                favoriteBets = listOf(FavoriteCartBetDto(9), FavoriteCartBetDto()),
            ),
        )
        val repository = RapidBettingRepositoryImpl(source)
        val loaded = repository.configuration().success()
        assertEquals(setOf(1), loaded.requiredNumbers)
        assertEquals(setOf(2), loaded.excludedNumbers)
        assertEquals(listOf(2), loaded.modalities.map { it.id })
        assertEquals(setOf(9L), loaded.favoriteBetIds)

        val saved = repository.save(loaded).success()
        assertEquals(loaded, saved)
        assertEquals("MEGA_SENA", source.saved?.modalities?.single()?.managerName)
        assertEquals(listOf(9L), source.saved?.favoriteBets?.mapNotNull { it.id })

        assertEquals(7L, repository.generate(0).success().id)
        assertEquals(0, source.quantity)
    }

    @Test fun `null optional values map to safe defaults and save falls back to request`() = runTest {
        val source = FakeSource(RapidConfigurationDto(), saveReturnsNull = true)
        val repository = RapidBettingRepositoryImpl(source)
        val empty = repository.configuration().success()
        assertEquals(BigDecimal.ZERO, empty.minimumAmount)
        assertTrue(empty.modalities.isEmpty())

        val configured = RapidBetConfiguration(
            BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO, emptySet(), emptySet(),
            listOf(LotteryModality(2, "Mega", "MEGA_SENA")),
        )
        assertEquals(configured.copy(modalities = listOf(configured.modalities.single().copy(managerName = "MEGA_SENA"))), repository.save(configured).success())
    }

    @Test fun `missing configuration is rejected`() = runTest {
        val repository = RapidBettingRepositoryImpl(FakeSource(null))
        val result = repository.configuration()
        assertTrue(result is br.gov.caixa.loterias.core.domain.service.model.ServiceResult.Failed)

    }

    @Test fun `retrofit data source delegates every rapid endpoint`() = runTest {
        val configuration = RapidConfigurationDto(minimumAmount = BigDecimal.ONE)
        val provider = BackendApiProvider()
        var generatedQuantity: Int? = null
        provider.replace(java.lang.reflect.Proxy.newProxyInstance(
            BackendApi::class.java.classLoader,
            arrayOf(BackendApi::class.java),
        ) { _, method, arguments ->
            when (method.name) {
                "rapidConfiguration" -> ApiEnvelope(configuration)
                "saveRapidConfiguration" -> ApiEnvelope(arguments?.first() as RapidConfigurationDto)
                "generateRapidBets" -> {
                    generatedQuantity = arguments?.first() as Int
                    ApiEnvelope(CartDto(id = 8, total = BigDecimal.ZERO))
                }
                else -> error("Unexpected ${method.name}")
            }
        } as BackendApi)
        val source = RapidBettingRemoteDataSourceImpl(provider)
        assertEquals(configuration, source.configuration())
        assertEquals(configuration, source.save(configuration))
        assertEquals(8L, source.generate(4)?.id)
        assertEquals(4, generatedQuantity)
    }

    @Test fun `rapid service failures preserve login and throttling decisions`() = runTest {
        val source = object : RapidBettingRemoteDataSource {
            var status = 401
            override suspend fun configuration(): RapidConfigurationDto? = throw br.gov.caixa.loterias.core.network.error.BackendHttpFailure(status,
                br.gov.caixa.loterias.core.network.error.BackendErrorMetadata(message = "Aguarde"))
            override suspend fun save(configuration: RapidConfigurationDto): RapidConfigurationDto? = configuration()
            override suspend fun generate(quantity: Int): CartDto? = throw br.gov.caixa.loterias.core.network.error.BackendHttpFailure(status, null)
        }
        val repository = RapidBettingRepositoryImpl(source)
        val login = repository.configuration() as br.gov.caixa.loterias.core.domain.service.model.ServiceResult.Rejected
        assertEquals(br.gov.caixa.loterias.core.domain.service.model.ServiceNoticeReason.LOGIN_REQUIRED,
            (login.failure as br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.NoticeRequired).notice.reason)
        source.status = 429
        val throttled = repository.configuration() as br.gov.caixa.loterias.core.domain.service.model.ServiceResult.Rejected
        assertEquals("Aguarde", (throttled.failure as br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.NoticeRequired).notice.message)
    }

    @Test fun `rapid service cancellation propagates unchanged`() = runTest {
        val cancellation = kotlinx.coroutines.CancellationException("cancelled")
        val source = object : RapidBettingRemoteDataSource {
            override suspend fun configuration(): RapidConfigurationDto? = throw cancellation
            override suspend fun save(configuration: RapidConfigurationDto): RapidConfigurationDto? = throw cancellation
            override suspend fun generate(quantity: Int): CartDto? = throw cancellation
        }
        try {
            RapidBettingRepositoryImpl(source).configuration()
            error("Cancellation swallowed")
        } catch (actual: kotlinx.coroutines.CancellationException) { assertTrue(actual === cancellation) }
    }

    private fun <T> br.gov.caixa.loterias.core.domain.service.model.ServiceResult<T>.success(): T =
        (this as br.gov.caixa.loterias.core.domain.service.model.ServiceResult.Success).value

    private class FakeSource(
        private val value: RapidConfigurationDto?,
        private val saveReturnsNull: Boolean = false,
    ) : RapidBettingRemoteDataSource {
        var saved: RapidConfigurationDto? = null
        var quantity: Int? = null
        override suspend fun configuration() = value
        override suspend fun save(configuration: RapidConfigurationDto): RapidConfigurationDto? {
            saved = configuration
            return if (saveReturnsNull) null else configuration
        }
        override suspend fun generate(quantity: Int): CartDto {
            this.quantity = quantity
            return CartDto(id = 7, total = BigDecimal.ZERO)
        }
    }
}
