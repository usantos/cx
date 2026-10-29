package br.gov.caixa.loterias.feature.betting.domain.rule

import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice

/** Limites de seleção e combinações permitidas pelos contratos existentes das loterias. */
object LotterySelectionPolicy {
    fun superSevenMaximumPerColumn(numberQuantity: Int): Int = when (numberQuantity) {
        in 0..7 -> 1
        in 8..14 -> 2
        else -> 3
    }

    fun hasLotecaPrice(prices: List<BetPrice>, doubles: Int, triples: Int): Boolean = prices.any {
        it.doubleQuantity == doubles && it.tripleQuantity == triples
    }
}
