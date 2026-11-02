package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
internal fun MarketplaceShareCounter(
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
                color = LoteriasCaixaDSMarketplaceStyle.arrow,
                style = LoteriasCaixaDSMarketplaceStyle.text(MarketplacePurchaseLayout.QUANTITY_TEXT_SIZE)
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
internal fun MarketplaceShareButton(
    label: String,
    enabled: Boolean,
    color: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Box(
        modifier.fillMaxHeight().background(
            if (enabled) color else LoteriasCaixaDSColorTokens.NotificationSearch,
            RoundedCornerShape(MarketplacePurchaseLayout.controlCorner)
        )
            .uiId("ds.0043").clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = contentColor,
            style = LoteriasCaixaDSMarketplaceStyle.text(
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
