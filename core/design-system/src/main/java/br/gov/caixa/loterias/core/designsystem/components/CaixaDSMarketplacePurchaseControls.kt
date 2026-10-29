package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

private object MarketplacePurchaseLayout {
    val inset = 16.dp
    val gap = 12.dp
    val height = 48.dp
    val preferredCounterWidth = 159.dp
    val minimumCounterWidth = 144.dp
    val preferredActionWidth = 177.dp
    val verticalInset = 8.dp
    val underlineHorizontalInset = 8.dp
    val underlineVerticalInset = 4.dp
    val actionTextInset = 4.dp
    val actionCorner = 5.dp
    val controlCorner = 8.dp
    const val ACTION_TEXT_SIZE = 16
    const val QUANTITY_TEXT_SIZE = 18
    const val MINUS_TEXT_SIZE = 24
}

@Composable
internal fun CaixaDSMarketplacePurchaseControls(
    shares: Int,
    canDecrease: Boolean,
    canIncrease: Boolean,
    adding: Boolean,
    canAdd: Boolean,
    color: Color,
    contentColor: Color,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onAdd: () -> Unit,
) {
    val resources = LocalResources.current
    val label = resources.getString(R.string.ui_adicionar_ao_carrinho_2f424d)
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val minimumActionWidth = with(density) {
        measurer.measure(
            label,
            style = LocalTextStyle.current.merge(CaixaDSMarketplaceStyle.text(MarketplacePurchaseLayout.ACTION_TEXT_SIZE)),
            softWrap = false
        ).size.width.toDp() + MarketplacePurchaseLayout.actionTextInset * 2
    }
    BoxWithConstraints(
        Modifier.fillMaxWidth().padding(horizontal = MarketplacePurchaseLayout.inset, vertical = MarketplacePurchaseLayout.verticalInset)
    ) {
        if (maxWidth >= MarketplacePurchaseLayout.minimumCounterWidth + MarketplacePurchaseLayout.gap + minimumActionWidth) {
            val counterWidth = MarketplacePurchaseLayout.preferredCounterWidth.coerceAtMost(
                maxWidth - minimumActionWidth - MarketplacePurchaseLayout.gap
            )
            val actionWidth = MarketplacePurchaseLayout.preferredActionWidth.coerceAtLeast(minimumActionWidth)
                .coerceAtMost(maxWidth - counterWidth - MarketplacePurchaseLayout.gap)
            Row(
                Modifier.fillMaxWidth().height(MarketplacePurchaseLayout.height),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MarketplaceShareCounter(
                    shares,
                    canDecrease,
                    canIncrease,
                    color,
                    contentColor,
                    onDecrease,
                    onIncrease,
                    (Modifier.width(counterWidth).fillMaxHeight()).uiId("ds.0036")
                )
                Spacer(Modifier.width(MarketplacePurchaseLayout.gap))
                MarketplaceAddAction(
                    adding,
                    canAdd,
                    color,
                    contentColor,
                    onAdd,
                    (Modifier.width(actionWidth).fillMaxHeight())
                        .uiId("ds.0037")
                )
            }
        } else {
            Column(Modifier.fillMaxWidth()) {
                MarketplaceShareCounter(
                    shares,
                    canDecrease,
                    canIncrease,
                    color,
                    contentColor,
                    onDecrease,
                    onIncrease,
                    (
                        Modifier.width(MarketplacePurchaseLayout.preferredCounterWidth)
                            .height(MarketplacePurchaseLayout.height)
                        ).uiId("ds.0038")
                )
                Spacer(Modifier.height(MarketplacePurchaseLayout.gap))
                MarketplaceAddAction(
                    adding,
                    canAdd,
                    color,
                    contentColor,
                    onAdd,
                    (Modifier.fillMaxWidth().height(MarketplacePurchaseLayout.height)).uiId("ds.0039")
                )
            }
        }
    }
}

@Composable
private fun MarketplaceShareCounter(
    shares: Int,
    canDecrease: Boolean,
    canIncrease: Boolean,
    color: Color,
    contentColor: Color,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        MarketplaceShareButton("-", canDecrease, color, contentColor, onDecrease, (Modifier.weight(1f)).uiId("ds.0040"))
        Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
            Text(
                shares.toString(),
                color = CaixaDSMarketplaceStyle.arrow,
                style = CaixaDSMarketplaceStyle.text(MarketplacePurchaseLayout.QUANTITY_TEXT_SIZE)
            )
            Box(
                Modifier.align(
                    Alignment.BottomCenter
                ).padding(
                    horizontal = MarketplacePurchaseLayout.underlineHorizontalInset,
                    vertical = MarketplacePurchaseLayout.underlineVerticalInset
                )
                    .fillMaxWidth().height(1.dp).background(color)
            )
        }
        MarketplaceShareButton("+", canIncrease, color, contentColor, onIncrease, (Modifier.weight(1f)).uiId("ds.0041"))
    }
}

@Composable
private fun MarketplaceAddAction(
    adding: Boolean,
    enabled: Boolean,
    color: Color,
    contentColor: Color,
    onAdd: () -> Unit,
    modifier: Modifier,
) {
    val resources = LocalResources.current
    Box(
        modifier.background(
            color,
            RoundedCornerShape(MarketplacePurchaseLayout.actionCorner)
        ).uiId("ds.0042").clickable(enabled = enabled, onClick = onAdd),
        contentAlignment = Alignment.Center
    ) {
        Text(
            resources.getString(if (adding) R.string.ui_adicionando_6ae81e else R.string.ui_adicionar_ao_carrinho_2f424d),
            color = contentColor,
            style = CaixaDSMarketplaceStyle.text(MarketplacePurchaseLayout.ACTION_TEXT_SIZE)
        )
    }
}

@Composable
private fun MarketplaceShareButton(
    label: String,
    enabled: Boolean,
    color: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Box(
        modifier.fillMaxHeight().background(
            if (enabled) color else CaixaDSColorTokens.NotificationSearch,
            RoundedCornerShape(MarketplacePurchaseLayout.controlCorner)
        )
            .uiId("ds.0043").clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = contentColor,
            style = CaixaDSMarketplaceStyle.text(
                if (label ==
                    "-"
                ) {
                    MarketplacePurchaseLayout.MINUS_TEXT_SIZE
                } else {
                    MarketplacePurchaseLayout.ACTION_TEXT_SIZE
                }
            )
        )
    }
}
