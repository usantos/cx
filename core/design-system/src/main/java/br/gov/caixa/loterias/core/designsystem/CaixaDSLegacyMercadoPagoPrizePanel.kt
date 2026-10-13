package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Caixa DS Mercado Pago award panel, rendered entirely with Compose primitives. */
@Composable
fun CaixaDSLegacyMercadoPagoPrizePanel(amount: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.background(CaixaDSColorTokens.FavoriteBlue),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Prêmio",
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            color = CaixaDSColorTokens.LegacyBetAction,
            fontFamily = CaixaDSLegacyRegularFontFamily,
            fontSize = 15.199982.sp,
            lineHeight = TextUnit.Unspecified,
            textAlign = TextAlign.Center,
            style = CaixaDS.typography.bodyMedium.copy(
                fontFamily = CaixaDSLegacyRegularFontFamily,
                fontSize = 15.199982.sp,
                lineHeight = TextUnit.Unspecified,
                platformStyle = PlatformTextStyle(includeFontPadding = true),
            ),
        )
        Text(
            text = amount,
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp, bottom = 10.dp),
            color = CaixaDSColorTokens.White,
            fontFamily = CaixaDSFontFamily,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = TextUnit.Unspecified,
            textAlign = TextAlign.Center,
            style = CaixaDS.typography.bodyLarge.copy(
                fontFamily = CaixaDSFontFamily,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = TextUnit.Unspecified,
                platformStyle = PlatformTextStyle(includeFontPadding = true),
            ),
        )
    }
}
