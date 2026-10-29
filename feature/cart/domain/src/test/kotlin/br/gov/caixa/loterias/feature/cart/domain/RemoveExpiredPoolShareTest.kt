package br.gov.caixa.loterias.feature.cart.domain

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveExpiredPoolShare
import java.math.BigDecimal
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.cart.domain.repository.ExpiredPoolShareRepository
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.CartPoolShare
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem

class RemoveExpiredPoolShareTest {
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private val share = CartItem(19, CartItemKind.POOL_SHARE, "Cota", "", BigDecimal.ONE,
        poolShare = CartPoolShare("pool", 2, 10, "2026-10-14T12:00:00Z"))

    @Test fun `valid reservation retains item and displaying identity through repository`() = runBlocking {
        val updated = ShoppingCart(7, BigDecimal.ZERO, emptyList())
        var calls = 0
        val useCase = RemoveExpiredPoolShare(ExpiredPoolShareRepository { item, owner ->
            calls++
            assertSame(share, item)
            assertSame(identity, owner)
            updated
        })

        val result = useCase(share, identity) as AppResult.Success

        assertSame(updated, result.value)
        assertEquals(1, calls)
    }

    @Test fun `domain rejects wrong item kind and missing reservation before accessing repository`() = runBlocking {
        var calls = 0
        val useCase = RemoveExpiredPoolShare(ExpiredPoolShareRepository { _, _ ->
            calls++
            error("Invalid items must never reach storage")
        })

        for (invalid in listOf(share.copy(kind = CartItemKind.INDIVIDUAL_BET), share.copy(poolShare = null))) {
            val result = useCase(invalid, identity) as AppResult.Failure
            assertTrue(result.error is AppError.Validation)
            assertEquals("A remoção automática exige uma reserva de cota", (result.error as AppError.Validation).reason)
        }
        assertEquals(0, calls)
    }
}
