package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.feature.betting.domain.rule.LotterySelectionPolicy
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction

internal fun reduceBettingSportsSelection(state: BettingGameUiState, action: BettingGameAction): BettingGameUiState = when (action) {
    is BettingGameAction.TeamQueryChanged -> reduceBettingGameTeamQueryChanged(state, action)
    is BettingGameAction.TeamSelected -> reduceBettingGameTeamSelected(state, action)
    is BettingGameAction.LotecaOutcomeToggled -> reduceBettingGameLotecaOutcomeToggled(state, action)
    is BettingGameAction.LotogolScoreSelected -> reduceBettingGameLotogolScoreSelected(state, action)
    else -> state
}

private fun reduceBettingGameTeamQueryChanged(state: BettingGameUiState, action: BettingGameAction.TeamQueryChanged): BettingGameUiState =
    run {
        val it = state
        it.copy(teamQuery = action.value.take(TEAM_QUERY_LENGTH), errorMessage = null)
    }

private fun reduceBettingGameTeamSelected(state: BettingGameUiState, action: BettingGameAction.TeamSelected): BettingGameUiState = run {
    val current = state
    if (action.team !in current.game?.teams.orEmpty()) {
        current
    } else {
        current.copy(
            selectedTeam = action.team,
            teamQuery = listOf(action.team.name, action.team.state).filter(String::isNotBlank).joinToString("-"),
            errorMessage = null
        )
    }
}

private fun reduceBettingGameLotecaOutcomeToggled(
    state: BettingGameUiState,
    action: BettingGameAction.LotecaOutcomeToggled,
): BettingGameUiState = run {
    val current = state
    val game = current.game ?: return@run current
    if (game.modality.id != LOTECA_ID || game.matches.none { it.number == action.matchNumber }) return@run current
    val selected = current.lotecaOutcomes[action.matchNumber].orEmpty()
    if (action.outcome in selected) {
        val updated = selected - action.outcome
        val outcomes = if (updated.isEmpty()) {
            current.lotecaOutcomes - action.matchNumber
        } else {
            current.lotecaOutcomes + (action.matchNumber to updated)
        }
        val doubles = outcomes.values.count { it.size == 2 }
        val triples = outcomes.values.count { it.size == TRIPLE_OUTCOMES }
        if ((doubles > 0 || triples > 0) && !LotterySelectionPolicy.hasLotecaPrice(game.prices, doubles, triples)) {
            return@run current.copy(
                warningMessage = action.combinationMessage
            )
        }
        return@run current.copy(lotecaOutcomes = outcomes, errorMessage = null)
    }

    val currentPrognoses = current.lotecaOutcomes.values.sumOf(Set<LotecaOutcome>::size)
    if (currentPrognoses >= MAX_LOTECA_PROGNOSES) {
        return@run current.copy(
            warningMessage = action.maximumMessage
        )
    }

    val updated = selected + action.outcome
    val outcomes = current.lotecaOutcomes + (action.matchNumber to updated)
    val doubles = outcomes.values.count { it.size == 2 }
    val triples = outcomes.values.count { it.size == TRIPLE_OUTCOMES }
    if ((doubles > 0 || triples > 0) && !LotterySelectionPolicy.hasLotecaPrice(game.prices, doubles, triples)) {
        return@run current.copy(
            warningMessage = action.combinationMessage
        )
    }

    val prognosisCount = currentPrognoses + 1
    current.copy(
        lotecaOutcomes = outcomes,
        numberQuantity = maxOf(current.numberQuantity, prognosisCount),
        errorMessage = null
    )
}

private fun reduceBettingGameLotogolScoreSelected(
    state: BettingGameUiState,
    action: BettingGameAction.LotogolScoreSelected,
): BettingGameUiState = run {
    val current = state
    if (action.score !in 0..MAXIMUM_LOTOGOL_SCORE ||
        current.game?.matches?.none { it.number == action.matchNumber } != false
    ) {
        return@run current
    }
    val previous = current.lotogolScores[action.matchNumber] ?: (null to null)
    val updated = if (action.home) action.score to previous.second else previous.first to action.score
    current.copy(
        lotogolScores = current.lotogolScores + (action.matchNumber to updated),
        errorMessage = null
    )
}

private const val MAX_LOTECA_PROGNOSES = 26

private const val TEAM_QUERY_LENGTH = 60
private const val TRIPLE_OUTCOMES = 3
private const val MAXIMUM_LOTOGOL_SCORE = 5
