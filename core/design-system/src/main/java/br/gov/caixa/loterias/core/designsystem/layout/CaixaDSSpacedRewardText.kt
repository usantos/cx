package br.gov.caixa.loterias.core.designsystem.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily

/** Rótulos do resumo de prêmios renderizados com a fonte Caixa DS e as métricas de texto do Compose. */
@Composable
fun CaixaDSPrizeMessage(message: String, contentColor: Color, modifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = uiStrings.getString(LocalizedR.string.ui_pode_comemorar_f533cc),
            modifier = Modifier.fillMaxWidth().padding(top = 15.dp, bottom = 5.333.dp),
            color = contentColor,
            fontFamily = CaixaDSRegularFontFamily,
            fontSize = 14.sp,
            lineHeight = TextUnit.Unspecified,
            textAlign = TextAlign.Center,
            style = CaixaDS.typography.bodyMedium.copy(
                fontFamily = CaixaDSRegularFontFamily,
                fontSize = 14.sp,
                lineHeight = TextUnit.Unspecified,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            )
        )
        Text(
            text = message,
            modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
            color = contentColor,
            fontFamily = CaixaDSFontFamily,
            fontSize = 20.099976.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.1.sp,
            textAlign = TextAlign.Center,
            style = CaixaDS.typography.bodyLarge.copy(
                fontFamily = CaixaDSFontFamily,
                fontSize = 20.099976.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 30.1.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            )
        )
    }
}
