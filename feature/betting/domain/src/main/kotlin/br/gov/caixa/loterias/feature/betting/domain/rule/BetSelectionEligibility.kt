package br.gov.caixa.loterias.feature.betting.domain.rule

import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters

/** Dados de seleção fornecidos pela apresentação; a elegibilidade da loteria independe do estado e das enumerações da interface. */
data class BetSelectionStatus(
    val surprise: Boolean,
    val numberQuantity: Int,
    val numberCount: Int,
    val cloverCount: Int,
    val manualCloverQuantity: Int,
    val surpriseCloverQuantity: Int,
    val surpriseQuantity: Int,
    val teamSelected: Boolean,
    val monthSelected: Boolean,
    val superSevenColumns: List<Set<Int>>,
    val lotecaOutcomeCounts: Map<Int, Int>,
    val lotogolScores: Map<Int, Pair<Int?, Int?>>,
)

object BetSelectionEligibility {
    fun canRegister(game: LotteryGameParameters, selection: BetSelectionStatus): Boolean =
        game.open && if (selection.surprise) surpriseAllowed(game, selection) else manualAllowed(game, selection)

    private fun surpriseAllowed(game: LotteryGameParameters, s: BetSelectionStatus): Boolean =
        s.surpriseQuantity >= 1 && (game.modality.id != DIA_DE_SORTE || s.monthSelected) &&
            (game.modality.id != MAIS_MILIONARIA || s.surpriseCloverQuantity in game.minimumClovers..game.maximumClovers)

    private fun manualAllowed(game: LotteryGameParameters, s: BetSelectionStatus): Boolean = when (game.modality.id) {
        LOTECA -> lotecaAllowed(game, s)

        LOTOGOL -> game.matches.isNotEmpty() && game.matches.all { match ->
            s.lotogolScores[match.number]?.let { it.first != null && it.second != null } == true
        }

        SUPER_SEVEN -> s.superSevenColumns.size == SUPER_SEVEN_COLUMNS && s.superSevenColumns.all(Set<Int>::isNotEmpty) &&
            s.superSevenColumns.sumOf(Set<Int>::size) == s.numberQuantity

        DIA_DE_SORTE -> s.numberQuantity > 0 && s.numberCount == s.numberQuantity && s.monthSelected

        else -> numericAllowed(game, s)
    }

    private fun lotecaAllowed(game: LotteryGameParameters, s: BetSelectionStatus): Boolean {
        val doubles = s.lotecaOutcomeCounts.values.count { it == 2 }
        val triples = s.lotecaOutcomeCounts.values.count { it == TRIPLE_OUTCOMES }
        return game.matches.isNotEmpty() && game.matches.all { (s.lotecaOutcomeCounts[it.number] ?: 0) > 0 } &&
            s.lotecaOutcomeCounts.values.sum() == s.numberQuantity && (doubles > 0 || triples > 0) &&
            LotterySelectionPolicy.hasLotecaPrice(game.prices, doubles, triples)
    }

    private fun numericAllowed(game: LotteryGameParameters, s: BetSelectionStatus): Boolean {
        val cloversAllowed = if (game.modality.id == MAIS_MILIONARIA) {
            s.cloverCount == s.manualCloverQuantity &&
                s.manualCloverQuantity in game.minimumClovers..game.maximumClovers
        } else {
            s.cloverCount in game.minimumClovers..game.maximumClovers.coerceAtLeast(game.minimumClovers)
        }
        return s.numberQuantity > 0 && s.numberCount == s.numberQuantity && cloversAllowed &&
            (game.modality.id != TIMEMANIA || s.teamSelected)
    }

    private const val TRIPLE_OUTCOMES = 3
    private const val LOTECA = 19
    private const val LOTOGOL = 14
    private const val SUPER_SEVEN = 7
    private const val SUPER_SEVEN_COLUMNS = 7
    private const val DIA_DE_SORTE = 11
    private const val MAIS_MILIONARIA = 9
    private const val TIMEMANIA = 20
}
