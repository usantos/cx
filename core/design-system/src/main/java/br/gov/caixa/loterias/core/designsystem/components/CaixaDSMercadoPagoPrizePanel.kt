package br.gov.caixa.loterias.core.designsystem.components

import br.gov.caixa.loterias.core.designsystem.R as LocalizedR

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
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS

/** Painel de premiação Mercado Pago do Caixa DS, renderizado inteiramente com primitivas Compose. */
@Composable
fun CaixaDSMercadoPagoPrizePanel(amount: String, modifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        modifier = modifier.background(CaixaDSColorTokens.FavoriteBlue),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = uiStrings.getString(LocalizedR.string.ui_premio_10aac1),
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            color = CaixaDSColorTokens.BetAction,
            fontFamily = CaixaDSRegularFontFamily,
            fontSize = 15.199982.sp,
            lineHeight = TextUnit.Unspecified,
            textAlign = TextAlign.Center,
            style = CaixaDS.typography.bodyMedium.copy(
                fontFamily = CaixaDSRegularFontFamily,
                fontSize = 15.199982.sp,
                lineHeight = TextUnit.Unspecified,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            )
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
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            )
        )
    }
}
