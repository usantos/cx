package br.gov.caixa.loterias.core.domain.marketplace

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceBet
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePage
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail

class MarketplaceModelsTest {
    @Test
    fun `page and pool expose immutable values`() {
        val pool = MarketplacePool("B1", "Mega-Sena", 1, "Lotérica", "01/01", "10", 3, 2, 6, true)
        val first = MarketplacePage(listOf(pool), 0, 1, 1)
        val last = first.copy(currentPage = 1)
        assertTrue(first.canLoadMore)
        assertFalse(last.canLoadMore)
        assertEquals("B1", pool.code)
        assertEquals("Mega-Sena", pool.modality)
        assertEquals(1, pool.contest)
        assertEquals("Lotérica", pool.lotteryName)
        assertEquals("01/01", pool.drawDate)
        assertEquals("10", pool.sharePrice)
        assertEquals(3, pool.availableShares)
        assertEquals(2, pool.betQuantity)
        assertEquals(6, pool.numberQuantity)
        assertTrue(pool.favoriteLottery)
        assertEquals(first, first.copy())
        assertNotEquals(first, last)
        assertEquals(first.hashCode(), first.copy().hashCode())
        assertTrue(first.toString().contains("B1"))
    }

    @Test
    fun `detail and bet expose immutable values`() {
        val bet = MarketplaceBet(listOf(1, 2), listOf(3), true)
        val detail = MarketplacePoolDetail(
            "B1", "Mega-Sena", 1, 2, "Lotérica", "002", "Cidade - UF", "01/01 às 20:00",
            "100", 4, 10, 2, 6, "9", "1", "10", listOf(bet),
            contestType = 2,
            drawDate = "01/01/2026",
            serviceFeePerShare = BigDecimal.ONE,
            lastSharePriceWithFeeValue = BigDecimal.TEN,
        )
        assertEquals("B1", detail.code)
        assertEquals("Mega-Sena", detail.modality)
        assertEquals(1, detail.contest)
        assertEquals(2, detail.lotteryId)
        assertEquals("Lotérica", detail.lotteryName)
        assertEquals("002", detail.lotteryNumber)
        assertEquals("Cidade - UF", detail.cityAndState)
        assertEquals("01/01 às 20:00", detail.drawDateAndTime)
        assertEquals("100", detail.estimatedPrize)
        assertEquals(4, detail.availableShares)
        assertEquals(10, detail.totalShares)
        assertEquals(2, detail.betQuantity)
        assertEquals(6, detail.numberQuantity)
        assertEquals("9", detail.sharePriceWithoutFee)
        assertEquals("1", detail.lastShareFee)
        assertEquals("10", detail.lastSharePriceWithFee)
        assertEquals(listOf(bet), detail.bets)
        assertEquals(2, detail.contestType)
        assertEquals("01/01/2026", detail.drawDate)
        assertEquals(BigDecimal.ONE, detail.serviceFeePerShare)
        assertEquals(BigDecimal.TEN, detail.lastSharePriceWithFeeValue)
        assertEquals(listOf(1, 2), bet.numbers)
        assertEquals(listOf(3), bet.clovers)
        assertTrue(bet.surprise)
        assertEquals(detail, detail.copy())
        assertEquals(detail.hashCode(), detail.copy().hashCode())
        assertTrue(detail.toString().contains("B1"))
    }
}
