package br.gov.caixa.loterias.core.designsystem

import br.gov.caixa.loterias.core.designsystem.components.input.caixaDSMoneyInputAmount
import br.gov.caixa.loterias.core.designsystem.components.input.caixaDSMoneyInputFormat
import java.math.BigDecimal
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class CaixaDSMoneyInputTest {
    private val locale = Locale.forLanguageTag("pt-BR")

    @Test fun `typed digits append as cents and keep currency formatting`() {
        var value = ""
        val expected = listOf("R$ 0,01", "R$ 0,12", "R$ 1,23", "R$ 12,34")
        "1234".forEachIndexed { index, digit ->
            value = caixaDSMoneyInputFormat(value + digit, locale)
            assertEquals(expected[index], value.replace('\u00a0', ' '))
        }
        assertEquals(BigDecimal("12.34"), caixaDSMoneyInputAmount(value))
    }

    @Test fun `backspace removes a cent digit and clearing produces zero`() {
        val value = caixaDSMoneyInputFormat("3050", locale)
        assertEquals(BigDecimal("30.50"), caixaDSMoneyInputAmount(value))
        assertEquals("R$ 3,05", caixaDSMoneyInputFormat(value.dropLast(1), locale).replace('\u00a0', ' '))
        assertEquals("R$ 0,00", caixaDSMoneyInputFormat("", locale).replace('\u00a0', ' '))
    }

    @Test fun `paste preserves digit based amounts and groups thousands`() {
        assertEquals("R$ 9.999,99", caixaDSMoneyInputFormat("999999", locale).replace('\u00a0', ' '))
        assertEquals(BigDecimal("9999.99"), caixaDSMoneyInputAmount("R$\u00a09.999,99"))
        assertEquals(BigDecimal("30.50"), caixaDSMoneyInputAmount("30,50"))
    }
}
