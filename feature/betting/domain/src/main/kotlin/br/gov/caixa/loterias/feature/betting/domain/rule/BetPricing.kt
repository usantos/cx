package br.gov.caixa.loterias.feature.betting.domain.rule

import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import java.math.BigDecimal

/** A consulta compartilhada retorna ausência explicitamente: a apresentação pode mostrar zero; o cadastro deve rejeitá-lo. */
object BetPricing {
    fun numericPrice(game: LotteryGameParameters, numbers: Int, clovers: Int): BetPrice? =
        game.prices.firstOrNull { it.numberQuantity == numbers && it.cloverQuantity == clovers }
            ?: game.prices.firstOrNull { it.numberQuantity == numbers }

    fun lotecaPrice(game: LotteryGameParameters, doubles: Int, triples: Int): BetPrice? =
        game.prices.firstOrNull { it.doubleQuantity == doubles && it.tripleQuantity == triples }

    fun total(unitPrice: BigDecimal, recurringDraws: Int, quantity: Int, mirror: Boolean = false): BigDecimal = unitPrice
        .multiply(BigDecimal.valueOf(recurringDraws.coerceAtLeast(1).toLong()))
        .multiply(BigDecimal.valueOf(quantity.coerceAtLeast(1).toLong()))
        .multiply(if (mirror) BigDecimal.valueOf(2) else BigDecimal.ONE)

    fun availableNumberQuantities(game: LotteryGameParameters): List<Int> = if (game.modality.id == LOTECA) {
        (MINIMUM_LOTECA_PROGNOSES..MAXIMUM_LOTECA_PROGNOSES).toList()
    } else {
        game.prices.map { it.numberQuantity }
            .filter { it in game.minimumSelection..game.maximumSelection }.distinct().sorted()
            .ifEmpty {
                if (game.minimumSelection <=
                    game.maximumSelection
                ) {
                    (game.minimumSelection..game.maximumSelection).toList()
                } else {
                    emptyList()
                }
            }
    }

    private const val MINIMUM_LOTECA_PROGNOSES = 15
    private const val MAXIMUM_LOTECA_PROGNOSES = 26
    private const val LOTECA = 19
}
