package br.gov.caixa.loterias.core.designsystem.components.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import java.math.BigDecimal

/** Layout natural do corpo usado apenas quando o usuário amplia o texto do sistema. */
@Composable
internal fun AdaptiveCartSummary(total: BigDecimal, betCount: Int) {
    val tokens = LoteriasCaixaDSCartSummaryTokens
    val resources = androidx.compose.ui.platform.LocalResources.current
    val badgeSize = tokens.BadgeSize * LocalDensity.current.fontScale.coerceAtLeast(1f)
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(tokens.LABEL_END_FRACTION).padding(horizontal = tokens.AmountInset, vertical = tokens.AmountInset)) {
            Text(
                cartSummaryAmount(total),
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                style = TextStyle(
                    fontFamily = LoteriasCaixaDSFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)
                )
            )
            Text(
                resources.getString(LocalizedR.string.ui_valor_atual_do_carrinho_2d85e6),
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                style = TextStyle(
                    fontFamily = LoteriasCaixaDSFontFamily,
                    fontSize = 12.sp,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)
                )
            )
        }
        Box(Modifier.weight(1f - tokens.LABEL_END_FRACTION).heightIn(min = 48.dp)) {
            Icon(LoteriasCaixaDSIcons.Cart, null, Modifier.align(Alignment.Center).size(tokens.IconSize), tint = Color.White)
            Box(
                Modifier.align(Alignment.TopEnd).padding(end = tokens.BadgeEndInset)
                    .size(badgeSize).background(tokens.BadgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    cartSummaryCountLabel(betCount),
                    color = tokens.BadgeTextColor,
                    style = TextStyle(
                        fontFamily = LoteriasCaixaDSFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.sp,
                        lineHeight = 10.sp
                    )
                )
            }
        }
    }
}
