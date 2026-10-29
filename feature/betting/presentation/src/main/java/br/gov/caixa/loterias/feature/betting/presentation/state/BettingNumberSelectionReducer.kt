package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.feature.betting.presentation.mapper.toggleSelectedNumber
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction

internal fun reduceBettingNumberSelection(state: BettingGameUiState, action: BettingGameAction): BettingGameUiState = when (action) {
    is BettingGameAction.ShakeSelectionGenerated -> reduceBettingGameShakeSelectionGenerated(state, action)
    is BettingGameAction.NumberToggled -> reduceBettingGameNumberToggled(state, action)
    is BettingGameAction.LuckyMonthSelected -> reduceBettingGameLuckyMonthSelected(state, action)
    BettingGameAction.MirrorToggled -> reduceBettingGameMirrorToggled(state)
    is BettingGameAction.CloverToggled -> reduceBettingGameCloverToggled(state, action)
    is BettingGameAction.CloverQuantitySelected -> reduceBettingGameCloverQuantitySelected(state, action)
    else -> state
}

private fun reduceBettingGameShakeSelectionGenerated(
    state: BettingGameUiState,
    action: BettingGameAction.ShakeSelectionGenerated,
): BettingGameUiState = run {
    val current = state
    action.selection.applyTo(current)
}

private fun reduceBettingGameNumberToggled(state: BettingGameUiState, action: BettingGameAction.NumberToggled): BettingGameUiState = run {
    val current = state
    val game = current.game ?: return@run current
    if (action.number !in 1..game.maximumNumber) return@run current
    if (action.number in current.selectedNumbers) {
        val updated = current.selectedNumbers - action.number
        val quantity = maxOf(updated.size, game.minimumSelection)
        return@run current.copy(
            selectedNumbers = updated,
            numberQuantity = quantity,
            selectedClovers = if (quantity != current.numberQuantity) emptySet() else current.selectedClovers,
            manualCloverQuantity = if (quantity != current.numberQuantity) game.minimumClovers else current.manualCloverQuantity,
            generateMirror = current.generateMirror && updated.size == quantity,
            selectionStep = if (quantity != current.numberQuantity) BetSelectionStep.NUMBERS else current.selectionStep,
            errorMessage = null
        )
    }
    if (current.selectedNumbers.size >= current.numberQuantity) {
        if (current.numberQuantity >= game.maximumSelection) {
            return@run current.copy(
                warningMessage = action.maximumMessage
            )
        }
        val quantity = (current.numberQuantity + 1).coerceAtMost(game.maximumSelection)
        val shouldWarn = action.warn

        return@run current.copy(
            selectedNumbers = current.selectedNumbers + action.number,
            numberQuantity = quantity,
            selectedClovers = emptySet(),
            manualCloverQuantity = game.minimumClovers,
            generateMirror = false,
            selectionStep = BetSelectionStep.NUMBERS,
            warningMessage = if (shouldWarn) {
                action.increasedMessage
            } else {
                current.warningMessage
            },
            errorMessage = null
        )
    }
    current.copy(
        selectedNumbers = current.selectedNumbers + action.number,
        generateMirror = current.generateMirror && current.selectedNumbers.size + 1 == current.numberQuantity,
        errorMessage = null
    )
}

private fun reduceBettingGameLuckyMonthSelected(
    state: BettingGameUiState,
    action: BettingGameAction.LuckyMonthSelected,
): BettingGameUiState = run {
    val current = state
    if (action.month !in current.game?.luckyMonths.orEmpty()) {
        current
    } else {
        current.copy(
            selectedLuckyMonth = action.month,
            errorMessage = null
        )
    }
}

private fun reduceBettingGameMirrorToggled(state: BettingGameUiState): BettingGameUiState = run {
    val current = state
    val game = current.game ?: return@run current
    val available = game.acceptsMirror && when (current.mode) {
        BetEntryMode.MANUAL -> current.selectedNumbers.size == current.numberQuantity
        BetEntryMode.SURPRISE -> true
    }
    if (!available) current else current.copy(generateMirror = !current.generateMirror, errorMessage = null)
}

private fun reduceBettingGameCloverToggled(state: BettingGameUiState, action: BettingGameAction.CloverToggled): BettingGameUiState = run {
    val current = state
    val game = current.game ?: return@run current
    current.copy(
        selectedClovers = toggleSelectedNumber(
            current.selectedClovers,
            action.clover,
            1..game.maximumCloverNumber,
            if (game.modality.id == MAIS_MILIONARIA_ID) current.manualCloverQuantity else game.maximumClovers
        ),
        errorMessage = null
    )
}

private fun reduceBettingGameCloverQuantitySelected(
    state: BettingGameUiState,
    action: BettingGameAction.CloverQuantitySelected,
): BettingGameUiState = run {
    val current = state
    if (action.quantity !in current.cloverQuantityOptions || action.quantity == current.manualCloverQuantity) return@run current
    val shouldWarn = action.quantity > (current.game?.minimumClovers ?: action.quantity) && action.warn

    current.copy(
        manualCloverQuantity = action.quantity,
        selectedClovers = emptySet(),
        favoriteSaved = false,
        warningMessage = if (shouldWarn) action.message else current.warningMessage,
        errorMessage = null
    )
}

internal fun GeneratedBettingSelection.applyTo(state: BettingGameUiState): BettingGameUiState = state.copy(
    selectedNumbers = numbers, numberQuantity = quantity, superSevenColumns = columns,
    lotecaOutcomes = outcomes, selectedTeam = team, teamQuery = teamQuery, selectedLuckyMonth = month,
    selectedClovers = clovers, generateMirror = mirror, warningMessage = warning, errorMessage = error
)
