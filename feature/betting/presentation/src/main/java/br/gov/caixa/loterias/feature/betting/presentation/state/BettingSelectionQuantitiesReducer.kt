package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction

internal fun reduceBettingSelectionQuantities(state: BettingGameUiState, action: BettingGameAction): BettingGameUiState = when (action) {
    is BettingGameAction.NumberQuantitySelected -> reduceBettingGameNumberQuantitySelected(state, action)
    is BettingGameAction.RecurringDrawsSelected -> reduceBettingGameRecurringDrawsSelected(state, action)
    is BettingGameAction.SpecialRecurringUnavailable -> reduceBettingGameSpecialRecurringUnavailable(state, action)
    BettingGameAction.SurpriseQuantityIncreased -> reduceBettingGameSurpriseQuantityIncreased(state)
    BettingGameAction.SurpriseQuantityDecreased -> reduceBettingGameSurpriseQuantityDecreased(state)
    BettingGameAction.SurpriseCloversIncreased -> reduceBettingGameSurpriseCloversIncreased(state)
    BettingGameAction.SurpriseCloversDecreased -> reduceBettingGameSurpriseCloversDecreased(state)
    else -> state
}

private fun reduceBettingGameNumberQuantitySelected(
    state: BettingGameUiState,
    action: BettingGameAction.NumberQuantitySelected,
): BettingGameUiState = run {
    val current = state
    if (action.quantity !in current.numberQuantityOptions) return@run current
    if (current.game?.modality?.id == LOTECA_ID) {
        val increased = action.quantity > current.numberQuantity && action.warn

        return@run current.copy(
            numberQuantity = action.quantity,
            lotecaOutcomes = if (action.quantity < current.numberQuantity) emptyMap() else current.lotecaOutcomes,
            warningMessage = if (increased) {
                action.message
            } else {
                current.warningMessage
            },
            errorMessage = null
        )
    }
    val shouldWarn = action.quantity > current.numberQuantityOptions.firstOrNull().orZero() && action.warn

    val selectedQuantity = if (current.game?.modality?.id == SUPER_SEVEN_ID) {
        current.superSevenColumns.sumOf(Set<Int>::size)
    } else {
        current.selectedNumbers.size
    }
    val mustClearSelection = selectedQuantity > action.quantity
    current.copy(
        numberQuantity = action.quantity,
        selectedNumbers = if (mustClearSelection) emptySet() else current.selectedNumbers,
        superSevenColumns = if (current.game?.modality?.id == SUPER_SEVEN_ID && mustClearSelection) {
            List(SUPER_SEVEN_COLUMNS) { emptySet() }
        } else {
            current.superSevenColumns
        },
        generateMirror = false,
        selectionStep = BetSelectionStep.NUMBERS,
        manualCloverQuantity = current.game?.minimumClovers ?: 0,
        selectedClovers = emptySet(),
        warningMessage = if (shouldWarn) {
            action.message
        } else {
            current.warningMessage
        }
    )
}

private fun reduceBettingGameRecurringDrawsSelected(
    state: BettingGameUiState,
    action: BettingGameAction.RecurringDrawsSelected,
): BettingGameUiState = run {
    val current = state
    if (action.draws !in current.recurringOptions) current else current.copy(recurringDraws = action.draws)
}

private fun reduceBettingGameSpecialRecurringUnavailable(
    state: BettingGameUiState,
    action: BettingGameAction.SpecialRecurringUnavailable,
): BettingGameUiState = run {
    val it = state
    it.copy(warningMessage = action.message)
}

private fun reduceBettingGameSurpriseQuantityIncreased(state: BettingGameUiState): BettingGameUiState = run {
    val current = state
    // Como no contador legado, quantidadeSurpresinhas não é um limite superior.
    // Os limites do carrinho e do valor diário são validados ao adicionar os jogos selecionados.
    current.copy(surpriseQuantity = current.surpriseQuantity + 1)
}

private fun reduceBettingGameSurpriseQuantityDecreased(state: BettingGameUiState): BettingGameUiState = run {
    val current = state
    current.copy(surpriseQuantity = (current.surpriseQuantity - 1).coerceAtLeast(1))
}

private fun reduceBettingGameSurpriseCloversIncreased(state: BettingGameUiState): BettingGameUiState = run {
    val current = state
    val game = current.game ?: return@run current
    current.copy(surpriseCloverQuantity = (current.surpriseCloverQuantity + 1).coerceAtMost(game.maximumClovers))
}

private fun reduceBettingGameSurpriseCloversDecreased(state: BettingGameUiState): BettingGameUiState = run {
    val current = state
    val game = current.game ?: return@run current
    current.copy(surpriseCloverQuantity = (current.surpriseCloverQuantity - 1).coerceAtLeast(game.minimumClovers))
}

private fun Int?.orZero() = this ?: 0

private const val SUPER_SEVEN_COLUMNS = 7
