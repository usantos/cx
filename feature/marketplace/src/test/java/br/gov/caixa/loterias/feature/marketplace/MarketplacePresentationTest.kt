package br.gov.caixa.loterias.feature.marketplace

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test

class MarketplacePresentationTest {
    @Test fun `estimated prize words use the legacy magnitude and truncate one decimal`() {
        assertEquals("(R$ 150 Milhões)", marketplacePrizeWords(BigDecimal("150000000")))
        assertEquals("(R$ 7,6 Milhões)", marketplacePrizeWords(BigDecimal("7654321")))
        assertEquals("(R$ 1,9 Milhão)", marketplacePrizeWords(BigDecimal("1999999")))
        assertEquals("", marketplacePrizeWords(BigDecimal("999")))
    }
    @Test fun `draw date accepts backend Brazilian and ISO formats without inventing a date`() {
        assertEquals("14/10", marketplaceDrawDate("14/10/2026 21:00:00"))
        assertEquals("14/10", marketplaceDrawDate("2026-10-14T21:00:00"))
        assertEquals("", marketplaceDrawDate(""))
    }
}
