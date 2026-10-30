package br.gov.caixa.loterias.feature.betting.presentation.mapper

import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.DIA_DE_SORTE_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTECA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTOGOL_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome
import br.gov.caixa.loterias.feature.betting.presentation.state.MAIS_MILIONARIA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.SUPER_SEVEN_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.TIMEMANIA_ID
import kotlin.random.Random

internal fun BettingGameUiState.shakeComplete(): Boolean = when {
    game?.modality?.id == LOTECA_ID || game?.modality?.id == LOTOGOL_ID -> canAddToCart
    selectionStep == BetSelectionStep.NUMBERS -> numbersAreComplete
    game?.modality?.id == TIMEMANIA_ID -> selectedTeam != null
    game?.modality?.id == DIA_DE_SORTE_ID -> selectedLuckyMonth != null
    game?.modality?.id == MAIS_MILIONARIA_ID -> selectedClovers.size == manualCloverQuantity
    else -> canAddToCart
}

internal fun BettingGameUiState.withShake(renew: Boolean, random: Random = Random.Default): BettingGameUiState {
    val game = game ?: return this
    if (loading || addedToCart || duplicateConfirmation || mode != BetEntryMode.MANUAL ||
        game.modality.id == LOTOGOL_ID
    ) {
        return this
    }
    fun numbers(target: Int, old: Set<Int>, range: IntRange): Set<Int> {
        if (target <= 0 || target > range.count()) return old
        val result = (if (renew) emptySet() else old).toMutableSet()
        range.filterNot { it in result }.shuffled(random).take((target - result.size).coerceAtLeast(0)).forEach { result += it }
        if (renew && result == old && result.size < range.count()) {
            result.remove(result.first())
            result += range.filterNot { it in old }.random(random)
        }
        return result
    }
    if (game.modality.id == LOTECA_ID) return completeLoteca(random)
    var state = this
    if (selectionStep == BetSelectionStep.NUMBERS || renew) {
        state = if (game.modality.id == SUPER_SEVEN_ID) {
            val columns = (if (renew) List(7) { emptySet<Int>() } else superSevenColumns).map { it.toMutableSet() }
            columns.forEach { if (it.isEmpty()) it += random.nextInt(10) }
            val maxPerColumn = superSevenMaximumPerColumn(numberQuantity)
            while (columns.sumOf { it.size } < numberQuantity) {
                val possible = columns.filter { it.size < maxPerColumn }
                if (possible.isEmpty()) break
                val column = possible.random(random)
                column += (0..9).filterNot { it in column }.random(random)
            }
            if (renew && columns == superSevenColumns) {
                val column = columns.first()
                val old = column.toSet()
                column.remove(column.first())
                column += (0..9).filterNot { it in old }.random(random)
            }
            copy(superSevenColumns = columns.map { it.toSet() }, errorMessage = null)
        } else {
            copy(
                selectedNumbers = numbers(numberQuantity, selectedNumbers, 1..game.maximumNumber),
                generateMirror = false,
                errorMessage = null
            )
        }
    }
    if (selectionStep == BetSelectionStep.COMPLEMENT) {
        state = when (game.modality.id) {
            TIMEMANIA_ID -> game.teams.filterNot {
                renew && it == selectedTeam
            }.randomOrNull(random)?.let { state.copy(selectedTeam = it, teamQuery = it.name) }
                ?: state

            DIA_DE_SORTE_ID -> game.luckyMonths.filterNot {
                renew && it == selectedLuckyMonth
            }.randomOrNull(random)?.let { state.copy(selectedLuckyMonth = it) }
                ?: state

            MAIS_MILIONARIA_ID -> state.copy(selectedClovers = numbers(manualCloverQuantity, selectedClovers, 1..game.maximumCloverNumber))

            else -> state
        }
    }
    return state
}

/**
 * CX LotecaFragment: tenta todas as estratégias de preservação neste alvo antes de aumentá-lo. A renovação usa a
 * mesma conclusão com preservação na Loteca, diferentemente das modalidades numéricas.
 */
private fun BettingGameUiState.completeLoteca(random: Random): BettingGameUiState {
    val game = game ?: return this
    if (game.matches.isEmpty() || numberQuantity !in 15..26) return this
    val old = game.matches.map { lotecaOutcomes[it.number].orEmpty() }
    for (target in numberQuantity..26) {
        for (strategy in 0..3) {
            val preserved = old.indices.filter { old[it].size > strategy }
            val singles = preserved.count { old[it].size == 1 }
            val doubles = preserved.count { old[it].size == 2 }
            val triples = preserved.count { old[it].size == 3 }
            val candidates = game.prices.filter { price ->
                val remainingSingles = old.size - price.doubleQuantity - price.tripleQuantity
                price.doubleQuantity >= doubles && price.tripleQuantity >= triples &&
                    remainingSingles >= singles &&
                    old.size + price.doubleQuantity + 2 * price.tripleQuantity == target
            }
            val price = candidates.randomOrNull(random) ?: continue
            val free = old.indices.filterNot { it in preserved }.shuffled(random)
            val quantities = List(price.tripleQuantity - triples) { 3 } +
                List(price.doubleQuantity - doubles) { 2 } +
                List(old.size - price.tripleQuantity - price.doubleQuantity - singles) { 1 }
            val assigned = free.zip(quantities).toMap()
            val outcomes = game.matches.mapIndexed { index, match ->
                val quantity = assigned[index]
                val previous = old[index]
                // Mesmo uma linha flexibilizada preserva escolhas existentes quando a nova cardinalidade permite.
                val selection = if (quantity == null) {
                    previous
                } else {
                    val base = if (previous.size <= quantity) previous else emptySet()
                    base + LotecaOutcome.entries.filterNot { it in base }.shuffled(random).take(quantity - base.size)
                }
                match.number to selection
            }.toMap()
            return copy(
                lotecaOutcomes = outcomes,
                numberQuantity = target,
                errorMessage = null,
                warningMessage = if (target > numberQuantity) {
                    messageText("voce_aumentou_a_quantidade_de_nu_2d08ee")
                } else {
                    warningMessage
                }
            )
        }
    }
    return this
}
