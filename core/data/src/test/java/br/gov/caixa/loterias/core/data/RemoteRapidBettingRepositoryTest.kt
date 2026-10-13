package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.betting.LotteryModality
import br.gov.caixa.loterias.core.domain.betting.RapidBetConfiguration
import br.gov.caixa.loterias.core.network.CartDto
import br.gov.caixa.loterias.core.network.ApiEnvelope
import br.gov.caixa.loterias.core.network.FavoriteCartBetDto
import br.gov.caixa.loterias.core.network.LotteryModalityDto
import br.gov.caixa.loterias.core.network.RapidConfigurationDto
import br.gov.caixa.loterias.core.network.LegacyBackendApi
import br.gov.caixa.loterias.core.network.LegacyBackendApiProvider
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteRapidBettingRepositoryTest {
    @Test fun `maps configuration saves stable payload and generates cart`() = runTest {
        val source = FakeSource(
            RapidConfigurationDto(
                minimumAmount = BigDecimal("30"), maximumAmount = BigDecimal("100"), minimumPrize = BigDecimal("1"),
                requiredNumbers = listOf(-1, 1, 1), excludedNumbers = listOf(0, 2),
                modalities = listOf(LotteryModalityDto(2, "Mega-Sena", "Especial", "MEGA_SENA"), LotteryModalityDto()),
                favoriteBets = listOf(FavoriteCartBetDto(9), FavoriteCartBetDto()),
            ),
        )
        val repository = RemoteRapidBettingRepository(source)
        val loaded = repository.configuration()
        assertEquals(setOf(1), loaded.requiredNumbers)
        assertEquals(setOf(2), loaded.excludedNumbers)
        assertEquals(listOf(2), loaded.modalities.map { it.id })
        assertEquals(setOf(9L), loaded.favoriteBetIds)

        val saved = repository.save(loaded)
        assertEquals(loaded, saved)
        assertEquals("MEGA_SENA", source.saved?.modalities?.single()?.managerName)
        assertEquals(listOf(9L), source.saved?.favoriteBets?.mapNotNull { it.id })

        assertEquals(7L, repository.generate(0).id)
        assertEquals(0, source.quantity)
    }

    @Test fun `null optional values map to safe defaults and save falls back to request`() = runTest {
        val source = FakeSource(RapidConfigurationDto(), saveReturnsNull = true)
        val repository = RemoteRapidBettingRepository(source)
        val empty = repository.configuration()
        assertEquals(BigDecimal.ZERO, empty.minimumAmount)
        assertTrue(empty.modalities.isEmpty())

        val configured = RapidBetConfiguration(
            BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO, emptySet(), emptySet(),
            listOf(LotteryModality(2, "Mega", "MEGA_SENA")),
        )
        assertEquals(configured.copy(modalities = listOf(configured.modalities.single().copy(managerName = "MEGA_SENA"))), repository.save(configured))
    }

    @Test fun `missing configuration is rejected`() = runTest {
        val repository = RemoteRapidBettingRepository(FakeSource(null))
        var failed = false
        try {
            repository.configuration()
        } catch (_: IllegalArgumentException) {
            failed = true
        }
        assertTrue(failed)
    }

    @Test fun `retrofit data source delegates every rapid endpoint`() = runTest {
        val configuration = RapidConfigurationDto(minimumAmount = BigDecimal.ONE)
        val provider = LegacyBackendApiProvider()
        var generatedQuantity: Int? = null
        provider.replace(java.lang.reflect.Proxy.newProxyInstance(
            LegacyBackendApi::class.java.classLoader,
            arrayOf(LegacyBackendApi::class.java),
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
        } as LegacyBackendApi)
        val source = RetrofitRapidBettingRemoteDataSource(provider)
        assertEquals(configuration, source.configuration())
        assertEquals(configuration, source.save(configuration))
        assertEquals(8L, source.generate(4)?.id)
        assertEquals(4, generatedQuantity)
    }

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
