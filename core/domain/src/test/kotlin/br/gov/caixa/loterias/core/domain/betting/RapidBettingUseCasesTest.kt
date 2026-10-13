package br.gov.caixa.loterias.core.domain.betting

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RapidBettingUseCasesTest {
    private val modality = LotteryModality(2, "Mega-Sena", "MEGA_SENA")
    private val valid = RapidBetConfiguration(
        BigDecimal("30"), BigDecimal("100"), BigDecimal.ZERO,
        setOf(1), setOf(2), listOf(modality), setOf(9),
    )

    @Test fun `valid operations delegate to repository including server default quantity zero`() = runTest {
        val repository = FakeRepository(valid)
        assertEquals(valid, (GetRapidBetConfiguration(repository)() as AppResult.Success).value)
        assertEquals(valid, (SaveRapidBetConfiguration(repository)(valid) as AppResult.Success).value)
        assertTrue(GenerateRapidBets(repository)(0) is AppResult.Success)
        assertEquals(valid, repository.saved)
        assertEquals(0, repository.quantity)
    }

    @Test fun `configuration validation covers amounts modalities and conflicting numbers`() = runTest {
        val repository = FakeRepository(valid)
        assertFailure(SaveRapidBetConfiguration(repository)(valid.copy(minimumAmount = BigDecimal("-1"))))
        assertFailure(SaveRapidBetConfiguration(repository)(valid.copy(maximumAmount = BigDecimal("20"))))
        assertFailure(SaveRapidBetConfiguration(repository)(valid.copy(minimumPrize = BigDecimal("-1"))))
        assertFailure(SaveRapidBetConfiguration(repository)(valid.copy(modalities = emptyList())))
        assertFailure(SaveRapidBetConfiguration(repository)(valid.copy(excludedNumbers = setOf(1))))
        assertFailure(GenerateRapidBets(repository)(-1))
    }

    @Test fun `repository failures become app failures`() = runTest {
        val repository = FakeRepository(valid, fail = true)
        assertFailure(GetRapidBetConfiguration(repository)())
        assertFailure(SaveRapidBetConfiguration(repository)(valid))
        assertFailure(GenerateRapidBets(repository)(1))
    }

    private fun assertFailure(result: AppResult<*>) = assertTrue(result is AppResult.Failure)

    private class FakeRepository(private val value: RapidBetConfiguration, private val fail: Boolean = false) : RapidBettingRepository {
        var saved: RapidBetConfiguration? = null
        var quantity: Int? = null
        private fun check() { if (fail) error("failure") }
        override suspend fun configuration() = value.also { check() }
        override suspend fun save(configuration: RapidBetConfiguration) = configuration.also { check(); saved = it }
        override suspend fun generate(quantity: Int) = ShoppingCart(1, BigDecimal.ZERO, emptyList()).also { check(); this.quantity = quantity }
    }
}
