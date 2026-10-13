package br.gov.caixa.loterias.feature.account

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily

/** Texto Compose com a fonte CAIXA e as métricas de espaçamento dos layouts instalados. */
@Composable
internal fun LegacyAccountCopy(value: String, size: Float, bold: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = value,
        modifier = modifier,
        color = CaixaDSColorTokens.LegacyAccountHeading,
        fontFamily = CaixaDSFontFamily,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontSize = size.sp,
        lineHeight = (size + 4f).sp,
        style = TextStyle(
            platformStyle = PlatformTextStyle(includeFontPadding = true),
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Proportional,
                trim = LineHeightStyle.Trim.None,
            ),
        ),
    )
}
