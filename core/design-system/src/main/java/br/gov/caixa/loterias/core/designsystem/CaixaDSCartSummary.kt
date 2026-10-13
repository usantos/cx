package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.math.BigDecimal

object CaixaDSCartSummaryTokens {
    val Height = 36.dp
    val AmountInset = 8.dp
    val AmountHeight = 20.dp
    val LabelHeight = 15.dp
    const val LabelEndFraction = 0.8f
    val IconSize = 28.dp
    val IconEndInset = 16.dp
    val IconTopInset = 5.dp
    val BadgeSize = 16.dp
    val BadgeEndInset = 8.dp
    val BadgeTopInset = 6.dp
    val BadgeColor = Color(0xFFFDAB01)
    val BadgeTextColor = Color(0xFF004680)
}

/** Exact paths of ic_background_somador_carrinho.xml, stretched like the legacy background. */
internal val CaixaDSCartSummaryBackground: ImageVector by lazy {
    ImageVector.Builder("CaixaDS.CartSummaryBackground", 1000.dp, 37.dp, 1000f, 37f).apply {
        addPath(PathParser().parsePathString("M0,1h1000v36h-1000z").toNodes(), fill = SolidColor(Color(0xFF005DA8)))
        addPath(PathParser().parsePathString("M818.5,2.53V36.46H794L818.5,2.53Z").toNodes(),
            fill = SolidColor(Color(0xFF004680)), stroke = SolidColor(Color(0xFF004680)), strokeLineWidth = 1f)
        addPath(PathParser().parsePathString("M819,1h181v36h-181z").toNodes(), fill = SolidColor(Color(0xFF004680)))
    }.build()
}

/**
 * Shared Compose replacement for fragment_somador_carrinho.xml.
 * The host supplies the complete snapshot count (not the number of grouped cards).
 * onClick is absent on the cart itself; no navigation or state is owned by the design system.
 * A nonpositive total removes this component. Reserve Height in the host if its slot must remain.
 */
@Composable
fun CaixaDSCartSummary(
    total: BigDecimal,
    betCount: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    if (!isCartSummaryVisible(total)) return
    val tokens = CaixaDSCartSummaryTokens
    val interaction = if (onClick == null) Modifier else Modifier.clickable(
        role = Role.Button, onClickLabel = "Abrir carrinho", onClick = onClick,
    )
    Box(modifier.fillMaxWidth().height(tokens.Height).then(interaction).semantics(mergeDescendants = true) {
        stateDescription = "${betCount.coerceAtLeast(0)} apostas"
    }) {
        Image(CaixaDSCartSummaryBackground, null, Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
        Layout(modifier = Modifier.fillMaxSize(), content = {
            Text(cartSummaryAmount(total), color = Color.White, maxLines = 1,
                style = TextStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)))
            Text("Valor atual do carrinho", color = Color.White, maxLines = 1,
                style = TextStyle(fontFamily = CaixaDSFontFamily, fontSize = 12.sp,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)))
            Icon(CaixaDSIcons.Cart, null, tint = Color.White)
            Box(Modifier.background(tokens.BadgeColor, CircleShape), contentAlignment = Alignment.Center) {
                Text(cartSummaryCountLabel(betCount), color = tokens.BadgeTextColor, maxLines = 1,
                    style = TextStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold,
                        fontSize = 7.sp, lineHeight = 10.sp))
            }
        }) { measurables, constraints ->
            val width = constraints.maxWidth
            val height = constraints.maxHeight
            val amountStart = tokens.AmountInset.roundToPx()
            val labelEnd = (width * tokens.LabelEndFraction).toInt()
            val amount = measurables[0].measure(Constraints(maxWidth = (labelEnd - amountStart).coerceAtLeast(0),
                minHeight = tokens.AmountHeight.roundToPx(), maxHeight = tokens.AmountHeight.roundToPx()))
            val amountEnd = amountStart + amount.width
            val label = measurables[1].measure(Constraints(maxWidth = (labelEnd - amountEnd).coerceAtLeast(0),
                minHeight = tokens.LabelHeight.roundToPx(), maxHeight = tokens.LabelHeight.roundToPx()))
            val iconSize = tokens.IconSize.roundToPx()
            val icon = measurables[2].measure(Constraints.fixed(iconSize, iconSize))
            val badgeSize = tokens.BadgeSize.roundToPx()
            val badge = measurables[3].measure(Constraints.fixed(badgeSize, badgeSize))
            layout(width, height) {
                amount.placeRelative(amountStart, (height - amount.height) / 2)
                label.placeRelative(amountEnd + (labelEnd - amountEnd - label.width) / 2, (height - label.height) / 2)
                icon.placeRelative(width - tokens.IconEndInset.roundToPx() - iconSize, tokens.IconTopInset.roundToPx())
                badge.placeRelative(width - tokens.BadgeEndInset.roundToPx() - badgeSize, tokens.BadgeTopInset.roundToPx())
            }
        }
    }
}
