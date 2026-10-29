package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.feature.betting.presentation.mapper.superSevenMaximumPerColumn
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction

internal fun reduceBettingSuperSevenSelection(state: BettingGameUiState, action: BettingGameAction): BettingGameUiState = when (action) {
    is BettingGameAction.SuperSevenDigitToggled -> reduceBettingGameSuperSevenDigitToggled(state, action)
    else -> state
}

private fun reduceBettingGameSuperSevenDigitToggled(
    state: BettingGameUiState,
    action: BettingGameAction.SuperSevenDigitToggled,
): BettingGameUiState = run {
    val current = state
    val game = current.game ?: return@run current
    if (game.modality.id != SUPER_SEVEN_ID || action.column !in 0 until SUPER_SEVEN_COLUMNS ||
        action.digit !in 0..MAXIMUM_SUPER_SEVEN_DIGIT
    ) {
        return@run current
    }
    val columns = current.superSevenColumns.toMutableList()
    val selected = columns[action.column]
    val total = columns.sumOf(Set<Int>::size)
    if (action.digit in selected) {
        val maximumPerColumn = superSevenMaximumPerColumn(current.numberQuantity)
        if (selected.size != maximumPerColumn) {
            return@run current.copy(
                warningMessage = action.messages.columnMinimum.format(selected.size)
            )
        }
        columns[action.column] = selected - action.digit
        return@run current.copy(
            superSevenColumns = columns,
            errorMessage = null
        )
    }
    if (total >= game.maximumSelection) {
        return@run current.copy(warningMessage = action.messages.selectionMaximum)
    }
    val prospectiveQuantity = if (total >= current.numberQuantity) {
        current.numberQuantityOptions.firstOrNull { it > current.numberQuantity } ?: current.numberQuantity
    } else {
        current.numberQuantity
    }
    val maximumPerColumn = superSevenMaximumPerColumn(prospectiveQuantity)
    if (selected.size >= maximumPerColumn) {
        return@run current.copy(
            warningMessage = action.messages.columnMaximum.format(maximumPerColumn)
        )
    }
    columns[action.column] = selected + action.digit
    val newTotal = total + 1
    current.copy(
        superSevenColumns = columns,
        numberQuantity = if (newTotal > current.numberQuantity) prospectiveQuantity else current.numberQuantity,
        errorMessage = null
    )
}

private const val SUPER_SEVEN_COLUMNS = 7

private const val MAXIMUM_SUPER_SEVEN_DIGIT = 9
