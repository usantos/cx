package br.gov.caixa.loterias.core.domain.betting

import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.GenerateRapidBets
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetRapidBetConfiguration
import br.gov.caixa.loterias.feature.betting.domain.model.RapidBetConfiguration
import br.gov.caixa.loterias.feature.betting.domain.repository.RapidBettingRepository
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveRapidBetConfiguration

class RapidBettingUseCasesTest {
    private val modality = LotteryModality(2, "Mega-Sena", "MEGA_SENA")
    private val valid = RapidBetConfiguration(
        BigDecimal("30"), BigDecimal("100"), BigDecimal.ZERO,
        setOf(1), setOf(2), listOf(modality), setOf(9),
    )

    @Test fun `valid operations delegate to repository including server default quantity zero`() = runTest {
        val repository = FakeRepository(valid)
        assertEquals(valid, (GetRapidBetConfiguration(repository)() as ServiceResult.Success).value)
        assertEquals(valid, (SaveRapidBetConfiguration(repository)(valid) as ServiceResult.Success).value)
        assertTrue(GenerateRapidBets(repository)(0) is ServiceResult.Success)
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

    private fun assertFailure(result: ServiceResult<*>) = assertTrue(result is ServiceResult.Failed)

    private class FakeRepository(private val value: RapidBetConfiguration, private val fail: Boolean = false) : RapidBettingRepository {
        var saved: RapidBetConfiguration? = null
        var quantity: Int? = null
        private fun <T> result(value: T): ServiceResult<T> = if (fail) ServiceResult.Failed(AppError.Unexpected) else ServiceResult.Success(value)
        override suspend fun configuration() = result(value)
        override suspend fun save(configuration: RapidBetConfiguration) = result(configuration.also { saved = it })
        override suspend fun generate(quantity: Int) = result(ShoppingCart(1, BigDecimal.ZERO, emptyList()).also { this.quantity = quantity })
    }
}
