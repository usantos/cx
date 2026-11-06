package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId

@Composable
internal fun MarketplacePurchaseRows(
    minimumActionWidth: Dp,
    counter: @Composable (Modifier) -> Unit,
    action: @Composable (Modifier) -> Unit,
) {
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
                counter((Modifier.width(counterWidth).fillMaxHeight()).uiId("ds.0036"))
                Spacer(Modifier.width(MarketplacePurchaseLayout.gap))
                action(
                    (Modifier.width(actionWidth).fillMaxHeight())
                        .uiId("ds.0037")
                )
            }
        } else {
            Column(Modifier.fillMaxWidth()) {
                counter(
                    (
                        Modifier.width(MarketplacePurchaseLayout.preferredCounterWidth)
                            .height(MarketplacePurchaseLayout.height)
                        ).uiId("ds.0038")
                )
                Spacer(Modifier.height(MarketplacePurchaseLayout.gap))
                action((Modifier.fillMaxWidth().height(MarketplacePurchaseLayout.height)).uiId("ds.0039"))
            }
        }
    }
}
