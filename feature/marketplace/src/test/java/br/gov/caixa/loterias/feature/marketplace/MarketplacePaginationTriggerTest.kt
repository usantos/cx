package br.gov.caixa.loterias.feature.marketplace

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MarketplacePaginationTriggerTest {
    @Test fun `only a scrolled list at its bottom requests another available page`() {
        assertTrue(shouldLoadMarketplacePage(true, false, true, false))
        assertFalse(shouldLoadMarketplacePage(false, false, true, false))
        assertFalse(shouldLoadMarketplacePage(true, true, true, false))
    }
    @Test fun `loading or exhausted search cannot issue another pagination request`() {
        assertFalse(shouldLoadMarketplacePage(true, false, true, true))
        assertFalse(shouldLoadMarketplacePage(true, false, false, false))
    }
}
