package br.gov.caixa.loterias.core.common.text

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class CapturedTextTemplateTest {
    @Test fun `formatting uses the captured locale after the process locale changes`() {
        val original = Locale.getDefault()
        try {
            val template = CapturedTextTemplate("%1\$s: %2\$,.2f", "pt-BR")
            Locale.setDefault(Locale.US)
            assertEquals("Valor: 1.234,50", template.format("Valor", AMOUNT))
            Locale.setDefault(Locale.FRANCE)
            assertEquals("Valor: 1.234,50", template.format("Valor", AMOUNT))
        } finally {
            Locale.setDefault(original)
        }
    }
}

private const val AMOUNT = 1234.5
