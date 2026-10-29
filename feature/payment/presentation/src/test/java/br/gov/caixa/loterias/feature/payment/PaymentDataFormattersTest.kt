package br.gov.caixa.loterias.feature.payment

import androidx.compose.ui.text.font.FontWeight
import java.math.BigDecimal
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.payment.presentation.mapper.paymentTopBarTitle
import br.gov.caixa.loterias.feature.payment.presentation.mapper.secondsUntil

class PaymentDataFormattersTest {
    private val now = Instant.parse("2026-10-18T15:00:00Z")

    @Test fun `pix deadline accepts explicit offset UTC and Sao Paulo local time`() {
        listOf("2026-10-18T12:05:00-03:00", "2026-10-18T15:05:00Z", "2026-10-18T12:05:00").forEach {
            assertEquals(300L, secondsUntil(it, now))
        }
    }

    @Test fun `pix deadline clamps expired invalid values and preserves epoch-second boundary`() {
        listOf("", "invalid", "2026-10-18T14:59:59Z").forEach { assertEquals(0L, secondsUntil(it, now)) }
        assertEquals(1L, secondsUntil("2026-10-18T15:00:01Z", now.plusMillis(999)))
    }

    @Test fun `amount keeps Brazilian separators and nonbreaking currency space`() {
        assertEquals("R$\u00a01.234,50", formatBrazilianCurrency(BigDecimal("1234.50")))
        assertEquals("R$\u00a00,00", formatBrazilianCurrency(BigDecimal.ZERO))
    }

    @Test fun `toolbar title emphasizes only pagamento`() {
        val title = paymentTopBarTitle()
        assertEquals("Forma de pagamento", title.text)
        val emphasis = title.spanStyles.single()
        assertEquals(9, emphasis.start)
        assertEquals(18, emphasis.end)
        assertEquals(FontWeight.Bold, emphasis.item.fontWeight)
    }
}
