package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilter
import br.gov.caixa.loterias.core.domain.retail.Retailer
import org.junit.Assert.*
import org.junit.Test

class MarketplaceFilterEntryTest {
    private val baseline = MarketplaceFilter.forModality(2, 1)
    private val retailer = Retailer(123, "000123", "Escolhida", "", "", "", "", "SP", true)
    @Test fun favoriteRetailerOverridesGlobalLotteryAndReturnsToRetailerView() {
        val entry = marketplaceFilterEntry(baseline.withLottery("999"), baseline, null, retailer)
        assertEquals("123", entry.initialFilter.lottery)
        assertEquals("123", entry.clearFilter.lottery)
        assertEquals(2, entry.initialFilter.modalityId)
        assertEquals(retailer, entry.initialRetailer)
        assertEquals(retailer, entry.retailer)
        assertEquals("betting/pool-view", entry.resultDestination)
    }
    @Test fun ordinaryFilterKeepsAppliedLotteryAndReturnsToGeneralPools() {
        val filter = baseline.withLottery("123")
        val entry = marketplaceFilterEntry(filter, baseline, retailer, null)
        assertEquals(filter, entry.initialFilter)
        assertEquals(baseline, entry.clearFilter)
        assertEquals(retailer, entry.initialRetailer)
        assertNull(entry.retailer)
        assertEquals("betting/pools", entry.resultDestination)
    }
}
