package br.gov.caixa.loterias.core.domain.marketplace

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import java.math.BigDecimal
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolOrder
import br.gov.caixa.loterias.feature.marketplace.domain.repository.MarketplacePurchaseRepository
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCart

class MarketplacePurchaseUseCaseTest {
    @Test
    fun `adds an available pool share to the cart`() = runBlocking {
        var received: MarketplacePoolOrder? = null
        val expected = ShoppingCart(7, BigDecimal.TEN, emptyList())
        val useCase = AddMarketplacePoolToCart(MarketplacePurchaseRepository { order ->
            received = order
            expected
        })
        val order = validOrder(requestedShares = 2)

        val result = useCase(order)

        assertEquals(AppResult.Success(expected), result)
        assertEquals(order, received)
        assertEquals(BigDecimal("19.00"), order.totalAmount)
    }

    @Test
    fun `rejects invalid pool purchase inputs without calling repository`() = runBlocking {
        var calls = 0
        val useCase = AddMarketplacePoolToCart(MarketplacePurchaseRepository {
            calls++
            ShoppingCart(0, BigDecimal.ZERO, emptyList())
        })
        val invalid = listOf(
            validOrder().copy(code = ""),
            validOrder().copy(modality = ""),
            validOrder().copy(contest = 0),
            validOrder().copy(requestedShares = 0),
            validOrder().copy(requestedShares = 4),
            validOrder().copy(firstSharePriceWithFee = BigDecimal.ZERO),
        )

        invalid.forEach { order ->
            val result = useCase(order)
            assertTrue(result is AppResult.Failure && result.error is AppError.Validation)
        }
        assertEquals(0, calls)
    }

    private fun validOrder(requestedShares: Int = 1) = MarketplacePoolOrder(
        code = "B1",
        modality = "MEGA_SENA",
        contest = 2900,
        contestType = 1,
        drawDate = "10/09/2026",
        availableShares = 3,
        requestedShares = requestedShares,
        serviceFeePerShare = BigDecimal.ONE,
        firstSharePriceWithFee = BigDecimal.TEN,
        regularSharePriceWithFee = BigDecimal("9.00"),
    )
}
