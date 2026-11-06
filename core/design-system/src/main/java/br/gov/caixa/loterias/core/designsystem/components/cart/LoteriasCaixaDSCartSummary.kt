package br.gov.caixa.loterias.core.designsystem.components.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import java.math.BigDecimal

object LoteriasCaixaDSCartSummaryTokens {
    val Height = 36.dp
    val AmountInset = 8.dp
    val AmountHeight = 20.dp
    val LabelHeight = 15.dp
    const val LABEL_END_FRACTION = 0.8f
    val IconSize = 28.dp
    val IconEndInset = 16.dp
    val IconTopInset = 5.dp
    val BadgeSize = 16.dp
    val BadgeEndInset = 8.dp
    val BadgeTopInset = 6.dp
    val BadgeColor = Color(0xFFFDAB01)
    val BadgeTextColor = Color(0xFF004680)
}

/** Caminhos exatos de ic_background_somador_carrinho.xml, esticados como o fundo legado. */
internal val LoteriasCaixaDSCartSummaryBackground: ImageVector by lazy {
    ImageVector.Builder("LoteriasCaixaDS.CartSummaryBackground", 1000.dp, 37.dp, 1000f, 37f).apply {
        addPath(PathParser().parsePathString("M0,1h1000v36h-1000z").toNodes(), fill = SolidColor(Color(0xFF005DA8)))
        addPath(
            PathParser().parsePathString("M818.5,2.53V36.46H794L818.5,2.53Z").toNodes(),
            fill = SolidColor(Color(0xFF004680)),
            stroke = SolidColor(Color(0xFF004680)),
            strokeLineWidth = 1f
        )
        addPath(PathParser().parsePathString("M819,1h181v36h-181z").toNodes(), fill = SolidColor(Color(0xFF004680)))
    }.build()
}

/**
 * Substituto Compose compartilhado de fragment_somador_carrinho.xml.
 * O hospedeiro fornece a contagem completa do retrato (não o número de cartões agrupados).
 * onClick está ausente no próprio carrinho; navegação e estado não pertencem ao design system.
 * Um total não positivo remove este componente. Reserva a altura no hospedeiro se seu espaço precisar permanecer.
 */
@Composable
fun LoteriasCaixaDSCartSummary(total: BigDecimal, betCount: Int, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (!isCartSummaryVisible(total)) return
    val safeBetCount = betCount.coerceAtLeast(0)
    val betsDescription = if (safeBetCount == 0) {
        uiStrings.getString(LocalizedR.string.format_cart_bets_zero)
    } else {
        uiStrings.getQuantityString(LocalizedR.plurals.format_cart_bets_description, safeBetCount, safeBetCount)
    }
    val tokens = LoteriasCaixaDSCartSummaryTokens
    val interaction = if (onClick == null) {
        Modifier
    } else {
        Modifier.uiId("ds.0075").clickable(
            role = Role.Button,
            onClickLabel = uiStrings.getString(LocalizedR.string.ui_abrir_carrinho_fcb650),
            onClick = onClick
        )
    }
    val enlarged = LocalDensity.current.fontScale > 1f
    val heightModifier = if (enlarged) Modifier.heightIn(min = tokens.Height) else Modifier.height(tokens.Height)
    Box(
        modifier.fillMaxWidth().then(heightModifier).then(interaction).semantics(mergeDescendants = true) {
            stateDescription = betsDescription
        }
    ) {
        if (enlarged) {
            Image(LoteriasCaixaDSCartSummaryBackground, null, Modifier.matchParentSize(), contentScale = ContentScale.FillBounds)
            AdaptiveCartSummary(total, betCount)
        } else {
            LegacyCartSummary(total, betCount)
        }
    }
}
