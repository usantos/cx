package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.feature.betting.presentation.mapper.availableNumberQuantities
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction

internal fun reduceBettingGameParameters(state: BettingGameUiState, action: BettingGameAction): BettingGameUiState = when (action) {
    BettingGameAction.ParametersLoading -> reduceBettingGameParametersLoading(state)
    BettingGameAction.GameUnavailable -> reduceBettingGameGameUnavailable(state)
    is BettingGameAction.ParametersLoaded -> reduceBettingGameParametersLoaded(state, action)
    else -> state
}

private fun reduceBettingGameParametersLoading(state: BettingGameUiState): BettingGameUiState = run {
    val it = state
    it.copy(loading = true, errorMessage = null, addedToCart = false)
}

private fun reduceBettingGameGameUnavailable(state: BettingGameUiState): BettingGameUiState = run {
    val it = state
    it.copy(game = null)
}

private fun reduceBettingGameParametersLoaded(state: BettingGameUiState, action: BettingGameAction.ParametersLoaded): BettingGameUiState =
    run {
        val it = state
        val quantities = availableNumberQuantities(action.game)
        val restoredNumbers = action.seed.initialNumbers.distinct().filter { it in 1..action.game.maximumNumber }
            .take(action.game.maximumSelection).toSet()
        val restoredColumns = List(SUPER_SEVEN_COLUMNS) { column ->
            action.seed.initialSuperSevenColumns.getOrNull(column).orEmpty().distinct()
                .filter { it in 0..MAXIMUM_SUPER_SEVEN_DIGIT }.toSet()
        }
        val restoredLotecaOutcomes = action.seed.initialLotecaSelections
            .filter { selection -> action.game.matches.any { it.number == selection.match.number } }
            .associate { selection ->
                selection.match.number to buildSet {
                    if (selection.homeWin) add(LotecaOutcome.HOME)
                    if (selection.draw) add(LotecaOutcome.DRAW)
                    if (selection.awayWin) add(LotecaOutcome.AWAY)
                }
            }
        val restoredSelectionSize = if (action.game.modality.id == SUPER_SEVEN_ID) {
            restoredColumns.sumOf(Set<Int>::size)
        } else if (action.game.modality.id == LOTECA_ID) {
            restoredLotecaOutcomes.values.sumOf(Set<LotecaOutcome>::size)
        } else {
            restoredNumbers.size
        }
        val restoredQuantity = restoredSelectionSize.takeIf { it in quantities }
        val restoredTeam = restoredBettingTeam(action)
        val restoredMonth = restoredBettingMonth(action)
        it.copy(
            loading = false,
            game = action.game,
            minimumCartAmount = action.parameters.minimumCartAmount,
            dailyLimit = action.parameters.dailyLimit,
            numberQuantity = restoredQuantity ?: quantities.firstOrNull() ?: action.game.minimumSelection,
            selectedNumbers = restoredNumbers,
            recurringDraws = action.seed.initialRecurringDraws.takeIf { it in action.game.recurringOptions } ?: 0,
            selectedClovers = action.seed.initialClovers.distinct()
                .filter { it in 1..action.game.maximumCloverNumber }.toSet(),
            selectedTeam = restoredTeam,
            teamQuery = restoredTeam?.let { team ->
                listOf(team.name, team.state).filter(String::isNotBlank).joinToString("-")
            }.orEmpty(),
            lotecaOutcomes = restoredLotecaOutcomes,
            lotogolScores = action.seed.initialLotogolSelections.associate { selection ->
                selection.match.number to (selection.homeScore to selection.awayScore)
            },
            superSevenColumns = restoredColumns,
            selectedLuckyMonth = restoredMonth,
            generateMirror = action.seed.initialMirror && action.game.acceptsMirror,
            surpriseCloverQuantity = action.game.minimumClovers,
            manualCloverQuantity = action.seed.initialClovers.distinct().size
                .coerceIn(action.game.minimumClovers, action.game.maximumClovers.coerceAtLeast(action.game.minimumClovers)),
            errorMessage = null
        )
    }

private const val SUPER_SEVEN_COLUMNS = 7

private const val MAXIMUM_SUPER_SEVEN_DIGIT = 9

private fun restoredBettingTeam(action: BettingGameAction.ParametersLoaded) = action.seed.initialTeam?.let { selectedSeed ->
    action.game.teams.firstOrNull { it.number == selectedSeed.number } ?: selectedSeed
}

private fun restoredBettingMonth(action: BettingGameAction.ParametersLoaded) = action.seed.initialLuckyMonth?.let { selectedSeed ->
    action.game.luckyMonths.firstOrNull { it.number == selectedSeed.number } ?: selectedSeed
}
