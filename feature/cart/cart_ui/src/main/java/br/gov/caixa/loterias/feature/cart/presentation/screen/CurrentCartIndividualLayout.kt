package br.gov.caixa.loterias.feature.cart.presentation.screen

import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItemBadge
import kotlin.math.roundToInt

fun currentCartIndividualMeasurePolicy(item: CartItem, superSeven: Boolean, hasExtras: Boolean, hasIndicators: Boolean) =
    MeasurePolicy { measurables, constraints ->
        // Reproduz as âncoras independentes de ConstraintLayout da CX;
        // a coluna de preço não pode empurrar extras ou indicadores para baixo.
        val split = (constraints.maxWidth * NUMBERS_WIDTH_FRACTION).toInt()
        val left = constraints.copy(minWidth = 0, minHeight = 0, maxWidth = split - 8.dp.roundToPx())
        val right = constraints.copy(minWidth = 0, minHeight = 0, maxWidth = constraints.maxWidth - split)
        val numbers = measurables[0].measure(left)
        val contest = measurables[1].measure(right)
        val price = measurables[2].measure(right)
        val extra = measurables[EXTRAS_MEASURABLE_INDEX].measure(left.copy(maxWidth = split))
        val badges = measurables[BADGES_MEASURABLE_INDEX].measure(left.copy(maxWidth = split))
        val contestY = if (superSeven) -8.dp.roundToPx() else 0
        val extraY = numbers.height
        val priceY = if (hasExtras) {
            extraY + extra[FirstBaseline] - price[FirstBaseline]
        } else {
            contestY + contest.height + 12.dp.roundToPx()
        }
        val badgeY = when {
            hasExtras -> extraY + extra.height

            superSeven || (
                CartItemBadge.SURPRISE !in item.badges && item.selectedNumbers.flatten().size > NUMBERS_PER_LINE
                ) -> numbers.height

            else -> priceY + ((price.height - badges.height) / 2f).roundToInt()
        }
        val height = maxOf(
            numbers.height,
            contest.height,
            priceY + price.height,
            extraY + extra.height,
            if (hasIndicators) badgeY + badges.height else 0
        )
        layout(constraints.maxWidth, height) {
            numbers.placeRelative(0, 0)
            contest.placeRelative(constraints.maxWidth - contest.width, contestY)
            price.placeRelative(constraints.maxWidth - price.width, priceY)
            extra.placeRelative(0, extraY)
            badges.placeRelative(0, badgeY)
        }
    }

private const val NUMBERS_WIDTH_FRACTION = 0.75f
private const val EXTRAS_MEASURABLE_INDEX = 3
private const val BADGES_MEASURABLE_INDEX = 4
private const val NUMBERS_PER_LINE = 6
