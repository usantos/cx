package br.gov.caixa.loterias.core.designsystem

import br.gov.caixa.loterias.core.designsystem.components.cart.cartSummaryAmount
import br.gov.caixa.loterias.core.designsystem.components.cart.cartSummaryCountLabel
import br.gov.caixa.loterias.core.designsystem.components.cart.isCartSummaryVisible
import java.math.BigDecimal
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CaixaDSCartSummaryRulesTest {
    @Test fun `summary appears only for a positive unrounded amount`() {
        assertFalse(isCartSummaryVisible(BigDecimal.ZERO))
        assertFalse(isCartSummaryVisible(BigDecimal("0.00")))
        assertFalse(isCartSummaryVisible(BigDecimal("-5.00")))
        assertTrue(isCartSummaryVisible(BigDecimal("0.0001")))
        assertTrue(isCartSummaryVisible(BigDecimal("25.50")))
    }

    @Test fun `legacy overflow is ellipsis while count stays numeric through 999`() {
        assertEquals("0", cartSummaryCountLabel(0))
        assertEquals("0", cartSummaryCountLabel(-1))
        assertEquals("1", cartSummaryCountLabel(1))
        assertEquals("999", cartSummaryCountLabel(999))
        assertEquals("...", cartSummaryCountLabel(1000))
        assertEquals("...", cartSummaryCountLabel(Int.MAX_VALUE))
    }

    @Test fun `amount is Brazilian currency independent of device locale without losing precision`() {
        val previous = Locale.getDefault()
        try {
            Locale.setDefault(Locale.US)
            assertEquals("R$ 0,00", cartSummaryAmount(BigDecimal.ZERO))
            assertEquals("R$ 25,50", cartSummaryAmount(BigDecimal("25.50")))
            assertEquals("R$ 1.234.567.890.123.456.789,12", cartSummaryAmount(BigDecimal("1234567890123456789.12")))
        } finally {
            Locale.setDefault(previous)
        }
    }
}
