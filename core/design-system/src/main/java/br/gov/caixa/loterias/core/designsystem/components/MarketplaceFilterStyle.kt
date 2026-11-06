package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

/** Cores e métricas nativas de TextView de activity_filtra_marketplace.xml e item_dezena.xml. */
object LoteriasCaixaDSMarketplaceFilterStyle {
    val canvas = Color.White
    val label = Color.Black
    val hint = LoteriasCaixaDSColorTokens.AccountTermsText
    val betText = LoteriasCaixaDSColorTokens.SocialGraphite
    val accent = Color(0xFF005EA8)
    val heading = LoteriasCaixaDSColorTokens.CaixaBlue
    val line = Color(0xFFD9D9D9)
    val dialogHeading get() = loteriasCaixaDSMarketplaceFilterText(14).copy(lineHeight = 18.9.sp)
    val bottomInset = 5.dp
    val outlinedButtonCorner = 4.dp
    val filledButtonCorner = 5.dp
}

fun loteriasCaixaDSMarketplaceFilterText(size: Int = 14, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = LoteriasCaixaDSFontFamily,
    fontSize = size.sp,
    fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)
