package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R

internal object MarketplacePurchaseLayout {
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
internal fun LoteriasCaixaDSMarketplacePurchaseControls(
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
            style = LocalTextStyle.current.merge(LoteriasCaixaDSMarketplaceStyle.text(MarketplacePurchaseLayout.ACTION_TEXT_SIZE)),
            softWrap = false
        ).size.width.toDp() + MarketplacePurchaseLayout.actionTextInset * 2
    }
    MarketplacePurchaseRows(
        minimumActionWidth,
        counter = { counterModifier ->
            MarketplaceShareCounter(shares, canDecrease, canIncrease, color, contentColor, onDecrease, onIncrease, counterModifier)
        },
        action = { actionModifier ->
            MarketplaceAddAction(adding, canAdd, color, contentColor, onAdd, actionModifier)
        }
    )
}
