package br.gov.caixa.loterias.feature.cart

import br.gov.caixa.loterias.feature.cart.presentation.mapper.sectionTitle
import br.gov.caixa.loterias.feature.cart.presentation.mapper.label
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemBadge
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartText
import br.gov.caixa.loterias.feature.cart.presentation.mapper.currentCartModalityName
import br.gov.caixa.loterias.feature.cart.presentation.mapper.formatCartAmount

class CurrentCartFormattersTest {
    @Test fun sectionsKeepTheirIndividualPoolAndComboLabels() {
        assertEquals("Cotas Bolão", CartItemKind.POOL_SHARE.sectionTitle)
        assertEquals("Apostas Individuais", CartItemKind.INDIVIDUAL_BET.sectionTitle)
        assertEquals("Combo de Apostas", CartItemKind.COMBO.sectionTitle)
    }
    @Test fun badgesKeepTheSixLabelsUsedInSharedRows() {
        assertEquals(listOf("TROCA", "BOLÃO", "COMBO", "ESPELHO", "SURPRESINHA", "TEIMOSINHA"),
            listOf(CartItemBadge.EXCHANGE, CartItemBadge.POOL, CartItemBadge.COMBO, CartItemBadge.MIRROR, CartItemBadge.SURPRISE, CartItemBadge.RECURRING).map { it.label })
    }
    @Test fun amountsUseBrazilianCurrencyAndRegularSpacesIncludingNull() {
        assertEquals("R$ 1.234,56", formatCartAmount(BigDecimal("1234.56")))
        assertEquals("R$ 0,00", formatCartAmount(null))
    }
    @Test fun backendSpecialTitlesAndCanonicalModalityNamesKeepTheirOriginalRules() {
        fun item(code: String, title: String) = CartItem(1, CartItemKind.INDIVIDUAL_BET, title, "", BigDecimal.TEN, modalityCode = code)
        assertEquals("mega da virada", currentCartModalityName(item("MEGA_SENA", "Mega da Virada")))
        assertEquals("+milionária", currentCartModalityName(item("MAIS_MILIONARIA", "Milionária")))
        assertEquals("super sete", currentCartModalityName(item("SUPER_7", "Super 7")))
        assertEquals("sem cadastro", currentCartModalityName(item("UNKNOWN", "Sem Cadastro")))
    }
    @Test fun sharedTextStyleKeepsFontPaddingSizeAndWeightDefaults() {
        val normal = cartText()
        assertEquals(CaixaDSFontFamily, normal.fontFamily)
        assertEquals(14.sp, normal.fontSize)
        assertEquals(FontWeight.Normal, normal.fontWeight)
        assertEquals(true, normal.platformStyle?.paragraphStyle?.includeFontPadding)
        assertEquals(16.sp, cartText(16, FontWeight.Bold).fontSize)
        assertEquals(FontWeight.Bold, cartText(16, FontWeight.Bold).fontWeight)
    }
}
