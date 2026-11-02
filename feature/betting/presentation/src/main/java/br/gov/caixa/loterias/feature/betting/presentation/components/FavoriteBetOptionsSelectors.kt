package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.feature.betting.R

@Composable
internal fun FavoriteContestSelector(
    contestOptions: List<Pair<Int, String>>,
    contestType: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    onSelect: (Int) -> Unit,
) {
    FavoritePickerHeading(stringResource(R.string.favorite_contest_type), onToggle)
    if (expanded && contestOptions.isNotEmpty()) {
        FavoriteComposePicker(
            contestOptions.map {
                it.second
            },
            contestOptions.indexOfFirst { it.first == contestType }.coerceAtLeast(0),
            Modifier.fillMaxWidth().padding(start = PickerInset, top = PickerInset, end = PickerInset).height(PickerHeight),
            label = stringResource(R.string.favorite_contest_type)
        ) { index -> onSelect(contestOptions[index].first) }
    }
}

@Composable
internal fun FavoriteRecurringSelector(
    options: List<Int>,
    selectionIndex: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    onSelect: (Int) -> Unit,
) {
    FavoritePickerHeading(stringResource(R.string.favorite_recurring_draws), onToggle)
    if (expanded) {
        FavoriteComposePicker(
            options.map(Int::toString),
            selectionIndex,
            Modifier.fillMaxWidth().padding(
                start = PickerInset,
                top = PickerInset,
                end = PickerInset
            ).height(PickerHeight),
            label = stringResource(R.string.favorite_recurring_draws)
        ) {
            onSelect(options[it])
        }
    }
}

private val PickerHeight = 100.dp
private val PickerInset = 20.dp
