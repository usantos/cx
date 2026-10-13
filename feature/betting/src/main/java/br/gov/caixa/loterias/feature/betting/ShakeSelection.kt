package br.gov.caixa.loterias.feature.betting

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
    if (loading || addedToCart || duplicateConfirmation || mode != BetEntryMode.MANUAL || !game.open || game.modality.id == LOTOGOL_ID) return this
    fun numbers(target: Int, old: Set<Int>, range: IntRange): Set<Int> {
        if (target <= 0 || target > range.count()) return old
        val result = (if (renew) emptySet() else old).toMutableSet()
        range.filterNot { it in result }.shuffled(random).take((target - result.size).coerceAtLeast(0)).forEach { result += it }
        if (renew && result == old && result.size < range.count()) {
            result.remove(result.first()); result += range.filterNot { it in old }.random(random)
        }
        return result
    }
    if (game.modality.id == LOTECA_ID) {
        val old = game.matches.map { lotecaOutcomes[it.number].orEmpty() }
        val doubles = old.count { it.size == 2 }; val triples = old.count { it.size == 3 }
        val price = game.prices.firstOrNull { it.doubleQuantity == doubles && it.tripleQuantity == triples && doubles + triples > 0 }
            ?: game.prices.firstOrNull { it.doubleQuantity >= doubles && it.tripleQuantity >= triples && it.doubleQuantity + it.tripleQuantity <= old.size }
            ?: return this
        val available = (List(price.tripleQuantity) { 3 } + List(price.doubleQuantity) { 2 } + List(old.size - price.tripleQuantity - price.doubleQuantity) { 1 }).toMutableList()
        val counts = mutableMapOf<Int, Int>()
        old.indices.shuffled(random).sortedByDescending { if (renew) 0 else old[it].size }.forEach { i ->
            val viable = available.filter { it >= if (renew) 0 else old[i].size }
            if (viable.isEmpty()) return this
            val count = viable.random(random); counts[i] = count; available.remove(count)
        }
        val outcomes = game.matches.mapIndexed { i, match ->
            val base = if (renew) emptySet() else old[i]
            match.number to (base + LotecaOutcome.entries.filterNot { it in base }.shuffled(random).take(counts.getValue(i) - base.size))
        }.toMap().toMutableMap()
        if (renew && outcomes == lotecaOutcomes) {
            val match = game.matches.firstOrNull { outcomes[it.number]!!.size < 3 }
            match?.let { val set = outcomes[it.number]!!; outcomes[it.number] = set - set.first() + LotecaOutcome.entries.first { it !in set } }
        }
        return copy(lotecaOutcomes = outcomes, errorMessage = null)
    }
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
                val column = columns.first(); val old = column.toSet()
                column.remove(column.first()); column += (0..9).filterNot { it in old }.random(random)
            }
            copy(superSevenColumns = columns.map { it.toSet() }, errorMessage = null)
        } else copy(selectedNumbers = numbers(numberQuantity, selectedNumbers, 1..game.maximumNumber), generateMirror = false, errorMessage = null)
    }
    if (selectionStep == BetSelectionStep.COMPLEMENT) state = when (game.modality.id) {
        TIMEMANIA_ID -> game.teams.filterNot { renew && it == selectedTeam }.randomOrNull(random)?.let { state.copy(selectedTeam = it, teamQuery = it.name) } ?: state
        DIA_DE_SORTE_ID -> game.luckyMonths.filterNot { renew && it == selectedLuckyMonth }.randomOrNull(random)?.let { state.copy(selectedLuckyMonth = it) } ?: state
        MAIS_MILIONARIA_ID -> state.copy(selectedClovers = numbers(manualCloverQuantity, selectedClovers, 1..game.maximumCloverNumber))
        else -> state
    }
    return state
}
