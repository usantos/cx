package br.gov.caixa.loterias.feature.betting.presentation.recovery

import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpoint
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome
fun BettingGameUiState.toCheckpoint(): BettingCheckpoint? = game?.let { current ->
    BettingCheckpoint(
        current.modality.id, current.contestType.uppercase(), current.contestNumber,
        mode.name, selectionStep.name, selectedNumbers, selectedClovers, selectedTeam?.number,
        teamQuery, selectedLuckyMonth?.number, lotecaOutcomes.mapValues { (_, value) -> value.map { it.name }.toSet() },
        lotogolScores, superSevenColumns, generateMirror, numberQuantity, recurringDraws, surpriseQuantity,
        surpriseCloverQuantity, manualCloverQuantity, favoriteName, favoriteExpanded
    ).also { it.validate() }
}

/** Os parâmetros atualizados fornecem todos os objetos e regras de elegibilidade; rótulos e imagens armazenados nunca são confiáveis. */
fun BettingCheckpoint.restoreInto(base: BettingGameUiState): BettingGameUiState {
    validate()
    val game = requireNotNull(base.game)
    require(
        game.modality.id == modalityId && game.contestNumber == contestNumber &&
            game.contestType.equals(contestType, true) && game.open
    )
    require(numbers.all { it in 1..game.maximumNumber } && numbers.size <= game.maximumSelection)
    require(clovers.all { it in 1..game.maximumCloverNumber } && clovers.size <= game.maximumClovers)
    require(numberQuantity in base.numberQuantityOptions.ifEmpty { (game.minimumSelection..game.maximumSelection).toList() })
    require(recurringDraws == 0 || recurringDraws in game.recurringOptions)
    require(surpriseQuantity <= game.maximumSurprises.coerceAtLeast(1))
    require(surpriseCloverQuantity in game.minimumClovers..game.maximumClovers.coerceAtLeast(game.minimumClovers))
    require(manualCloverQuantity in game.minimumClovers..game.maximumClovers.coerceAtLeast(game.minimumClovers))
    require(!mirror || game.acceptsMirror)
    require(loteca.keys.all { key -> game.matches.any { it.number == key } })
    require(lotogol.keys.all { key -> game.matches.any { it.number == key } })
    require(lotogol.values.all { score -> listOfNotNull(score.first, score.second).all { it in 0..MAX_LOTOGOL_SCORE } })
    require(superSeven.sumOf(Set<Int>::size) <= game.maximumSelection)
    val team = teamNumber?.let { number -> requireNotNull(game.teams.firstOrNull { it.number == number }) }
    val month = monthNumber?.let { number -> requireNotNull(game.luckyMonths.firstOrNull { it.number == number }) }
    return base.copy(
        loading = false, mode = BetEntryMode.valueOf(mode), selectionStep = BetSelectionStep.valueOf(step),
        selectedNumbers = numbers, selectedClovers = clovers, selectedTeam = team, teamQuery = teamQuery,
        selectedLuckyMonth = month, lotecaOutcomes = loteca.mapValues { (_, outcomes) -> outcomes.map(LotecaOutcome::valueOf).toSet() },
        lotogolScores = lotogol, superSevenColumns = superSeven, generateMirror = mirror,
        numberQuantity = numberQuantity, recurringDraws = recurringDraws, surpriseQuantity = surpriseQuantity,
        surpriseCloverQuantity = surpriseCloverQuantity, manualCloverQuantity = manualCloverQuantity,
        favoriteName = favoriteName, favoriteExpanded = favoriteExpanded, errorMessage = null, warningMessage = null,
        addedToCart = false, confirmedCart = null, duplicateConfirmation = false, dailyLimitConfirmation = false,
        favoriteSaved = false, favoriteOverwriteConfirmation = false, favoriteOverwriteMessage = null
    )
}

private const val MAX_LOTOGOL_SCORE = 5
