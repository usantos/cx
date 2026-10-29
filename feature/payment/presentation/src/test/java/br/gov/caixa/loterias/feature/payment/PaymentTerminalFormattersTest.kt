package br.gov.caixa.loterias.feature.payment

import androidx.compose.ui.text.font.FontWeight
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.payment.presentation.mapper.legacyPaymentTextStyle
import br.gov.caixa.loterias.feature.payment.presentation.mapper.legacyProcessingText

class PaymentTerminalFormattersTest {
    @Test fun plainAndEmptyMessagesKeepTheirTextWithoutEmphasis() {
        listOf("", "Mensagem comum").forEach { text ->
            val actual = legacyProcessingText(text)
            assertEquals(text, actual.text)
            assertTrue(actual.spanStyles.isEmpty())
        }
    }

    @Test fun eachRecognizedExpressionKeepsItsExactEmphasisRange() {
        listOf(Triple("Compra EFETIVADA.", 7, 16), Triple("A compra não garante participação", 9, 20), Triple("Veja Minhas Compras", 5, 19)).forEach { (text, start, end) ->
            val actual = legacyProcessingText(text)
            assertEquals(text, actual.text)
            val span = actual.spanStyles.single()
            assertEquals(start, span.start)
            assertEquals(end, span.end)
            assertEquals(FontWeight.SemiBold, span.item.fontWeight)
        }
    }

    @Test fun emphasisPriorityAndFirstOccurrenceStayUnchanged() {
        val priority = legacyProcessingText("Minhas Compras: não garante; EFETIVADA.").spanStyles.single()
        assertEquals(29, priority.start)
        assertEquals(38, priority.end)
        val repeated = legacyProcessingText("Minhas Compras e Minhas Compras").spanStyles.single()
        assertEquals(0, repeated.start)
        assertEquals(14, repeated.end)
    }

    @Test fun cardAndPixLegacyStyleKeepsFontAndPlatformPadding() {
        assertEquals(CaixaDSFontFamily, legacyPaymentTextStyle.fontFamily)
        assertEquals(true, legacyPaymentTextStyle.platformStyle?.paragraphStyle?.includeFontPadding)
    }
}
