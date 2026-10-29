package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction

internal fun reduceBettingSelectionReset(state: BettingGameUiState, action: BettingGameAction): BettingGameUiState = when (action) {
    is BettingGameAction.ModeSelected -> reduceBettingGameModeSelected(state, action)
    BettingGameAction.SelectionCleared -> reduceBettingGameSelectionCleared(state)
    BettingGameAction.CurrentStepCleared -> reduceBettingGameCurrentStepCleared(state)
    else -> state
}

private fun reduceBettingGameModeSelected(state: BettingGameUiState, action: BettingGameAction.ModeSelected): BettingGameUiState = run {
    val it = state
    if (action.mode == it.mode) return@run it
    it.copy(
        mode = action.mode,
        selectionStep = BetSelectionStep.NUMBERS,
        selectedNumbers = emptySet(),
        selectedClovers = emptySet(),
        selectedTeam = null,
        teamQuery = "",
        selectedLuckyMonth = null,
        superSevenColumns = List(SUPER_SEVEN_COLUMNS) { emptySet() },
        lotecaOutcomes = emptyMap(),
        lotogolScores = emptyMap(),
        surpriseQuantity = if (action.mode == BetEntryMode.SURPRISE) 1 else it.surpriseQuantity,
        surpriseCloverQuantity = if (action.mode == BetEntryMode.SURPRISE) it.game?.minimumClovers ?: 0 else it.surpriseCloverQuantity,
        manualCloverQuantity = it.game?.minimumClovers ?: 0,
        generateMirror = false,
        errorMessage = null
    )
}

private fun reduceBettingGameSelectionCleared(state: BettingGameUiState): BettingGameUiState = run {
    val it = state
    it.copy(
        selectedNumbers = emptySet(),
        selectedClovers = emptySet(),
        selectedTeam = null,
        teamQuery = "",
        lotecaOutcomes = emptyMap(),
        lotogolScores = emptyMap(),
        superSevenColumns = List(SUPER_SEVEN_COLUMNS) { emptySet() },
        selectedLuckyMonth = null,
        generateMirror = false,
        selectionStep = BetSelectionStep.NUMBERS,
        errorMessage = null
    )
}

private fun reduceBettingGameCurrentStepCleared(state: BettingGameUiState): BettingGameUiState = run {
    val current = state
    if (current.selectionStep != BetSelectionStep.COMPLEMENT) {
        return@run current.copy(
            selectedNumbers = emptySet(),
            superSevenColumns = List(SUPER_SEVEN_COLUMNS) { emptySet() },
            lotecaOutcomes = emptyMap(),
            lotogolScores = emptyMap(),
            generateMirror = false,
            errorMessage = null
        )
    }
    when (current.game?.modality?.id) {
        TIMEMANIA_ID -> current.copy(selectedTeam = null, teamQuery = "", errorMessage = null)
        DIA_DE_SORTE_ID -> current.copy(selectedLuckyMonth = null, errorMessage = null)
        MAIS_MILIONARIA_ID -> current.copy(selectedClovers = emptySet(), errorMessage = null)
        else -> current
    }
}

private const val SUPER_SEVEN_COLUMNS = 7
