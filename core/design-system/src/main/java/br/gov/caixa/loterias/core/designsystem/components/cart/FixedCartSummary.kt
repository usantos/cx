package br.gov.caixa.loterias.core.designsystem.components.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import java.math.BigDecimal

@Composable
internal fun LegacyCartSummary(total: BigDecimal, betCount: Int) {
    val tokens = LoteriasCaixaDSCartSummaryTokens
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Image(LoteriasCaixaDSCartSummaryBackground, null, Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
    Layout(modifier = Modifier.fillMaxSize(), content = {
        Text(
            cartSummaryAmount(total),
            color = Color.White,
            maxLines = 1,
            style = TextStyle(
                fontFamily = LoteriasCaixaDSFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            )
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_valor_atual_do_carrinho_2d85e6),
            color = Color.White,
            maxLines = 1,
            style = TextStyle(
                fontFamily = LoteriasCaixaDSFontFamily,
                fontSize = 12.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = true)
            )
        )
        Icon(LoteriasCaixaDSIcons.Cart, null, tint = Color.White)
        Box(Modifier.background(tokens.BadgeColor, CircleShape), contentAlignment = Alignment.Center) {
            Text(
                cartSummaryCountLabel(betCount),
                color = tokens.BadgeTextColor,
                maxLines = 1,
                style = TextStyle(
                    fontFamily = LoteriasCaixaDSFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 7.sp,
                    lineHeight = 10.sp
                )
            )
        }
    }) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val amountStart = tokens.AmountInset.roundToPx()
        val labelEnd = (width * tokens.LABEL_END_FRACTION).toInt()
        val amount = measurables[0].measure(
            Constraints(
                maxWidth = (labelEnd - amountStart).coerceAtLeast(0),
                minHeight = tokens.AmountHeight.roundToPx(),
                maxHeight = tokens.AmountHeight.roundToPx()
            )
        )
        val amountEnd = amountStart + amount.width
        val label = measurables[1].measure(
            Constraints(
                maxWidth = (labelEnd - amountEnd).coerceAtLeast(0),
                minHeight = tokens.LabelHeight.roundToPx(),
                maxHeight = tokens.LabelHeight.roundToPx()
            )
        )
        val iconSize = tokens.IconSize.roundToPx()
        val icon = measurables[2].measure(Constraints.fixed(iconSize, iconSize))
        val badgeSize = tokens.BadgeSize.roundToPx()
        val badge = measurables[BADGE_MEASURABLE_INDEX].measure(Constraints.fixed(badgeSize, badgeSize))
        layout(width, height) {
            amount.placeRelative(amountStart, (height - amount.height) / 2)
            label.placeRelative(amountEnd + (labelEnd - amountEnd - label.width) / 2, (height - label.height) / 2)
            icon.placeRelative(width - tokens.IconEndInset.roundToPx() - iconSize, tokens.IconTopInset.roundToPx())
            badge.placeRelative(width - tokens.BadgeEndInset.roundToPx() - badgeSize, tokens.BadgeTopInset.roundToPx())
        }
    }
}

private const val BADGE_MEASURABLE_INDEX = 3
