package br.gov.caixa.loterias.core.designsystem

import br.gov.caixa.loterias.core.designsystem.components.input.loteriasCaixaDSMoneyInputAmount
import br.gov.caixa.loterias.core.designsystem.components.input.loteriasCaixaDSMoneyInputFormat
import java.math.BigDecimal
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class LoteriasCaixaDSMoneyInputTest {
    private val locale = Locale.forLanguageTag("pt-BR")

    @Test fun `typed digits append as cents and keep currency formatting`() {
        var value = ""
        val expected = listOf("R$ 0,01", "R$ 0,12", "R$ 1,23", "R$ 12,34")
        "1234".forEachIndexed { index, digit ->
            value = loteriasCaixaDSMoneyInputFormat(value + digit, locale)
            assertEquals(expected[index], value.replace('\u00a0', ' '))
        }
        assertEquals(BigDecimal("12.34"), loteriasCaixaDSMoneyInputAmount(value))
    }

    @Test fun `backspace removes a cent digit and clearing produces zero`() {
        val value = loteriasCaixaDSMoneyInputFormat("3050", locale)
        assertEquals(BigDecimal("30.50"), loteriasCaixaDSMoneyInputAmount(value))
        assertEquals("R$ 3,05", loteriasCaixaDSMoneyInputFormat(value.dropLast(1), locale).replace('\u00a0', ' '))
        assertEquals("R$ 0,00", loteriasCaixaDSMoneyInputFormat("", locale).replace('\u00a0', ' '))
    }

    @Test fun `paste preserves digit based amounts and groups thousands`() {
        assertEquals("R$ 9.999,99", loteriasCaixaDSMoneyInputFormat("999999", locale).replace('\u00a0', ' '))
        assertEquals(BigDecimal("9999.99"), loteriasCaixaDSMoneyInputAmount("R$\u00a09.999,99"))
        assertEquals(BigDecimal("30.50"), loteriasCaixaDSMoneyInputAmount("30,50"))
    }
}
