package br.gov.caixa.loterias.core.presentation

import java.math.BigDecimal
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency

class BrazilianCurrencyFormattingTest {
    @Test fun `currency preserves grouping decimal places nonbreaking space and sign`() {
        assertEquals("R$\u00a01.234,50", formatBrazilianCurrency(BigDecimal("1234.50")))
        assertEquals("R$\u00a00,00", formatBrazilianCurrency(BigDecimal.ZERO))
        assertEquals("-R$\u00a02,50", formatBrazilianCurrency(BigDecimal("-2.50")))
    }

    @Test fun `currency remains Brazilian when the device default locale differs`() {
        val original = Locale.getDefault()
        try {
            Locale.setDefault(Locale.US)
            assertEquals("R$\u00a01.234,50", formatBrazilianCurrency(BigDecimal("1234.50")))
        } finally {
            Locale.setDefault(original)
        }
    }
}
