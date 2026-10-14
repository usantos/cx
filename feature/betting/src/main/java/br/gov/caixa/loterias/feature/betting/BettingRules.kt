package br.gov.caixa.loterias.feature.betting

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

internal fun toggleSelectedNumber(
    selected: Set<Int>,
    number: Int,
    allowedNumbers: IntRange,
    maximumSelection: Int,
): Set<Int> = when {
    number !in allowedNumbers -> selected
    number in selected -> selected - number
    selected.size >= maximumSelection.coerceAtLeast(0) -> selected
    else -> selected + number
}

internal fun canSubmitNumericBet(selected: Set<Int>, minimumSelection: Int, maximumSelection: Int): Boolean =
    selected.size in minimumSelection..maximumSelection

internal fun formatBetAmount(amount: BigDecimal?): String = NumberFormat
    .getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    .format(amount ?: BigDecimal.ZERO)
    .replace('\u00A0', ' ')
    .replace('\u202F', ' ')

internal fun availableNumberQuantities(game: br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters): List<Int> =
    if (game.modality.id == LOTECA_ID) (15..26).toList() else game.prices.map { it.numberQuantity }
        .filter { it in game.minimumSelection..game.maximumSelection }
        .distinct()
        .sorted()
        .ifEmpty {
            if (game.minimumSelection <= game.maximumSelection) {
                (game.minimumSelection..game.maximumSelection).toList()
            } else emptyList()
        }

internal fun resolveBetPrice(
    game: br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters,
    numberQuantity: Int,
    cloverQuantity: Int,
): BigDecimal = game.prices.firstOrNull {
    it.numberQuantity == numberQuantity && it.cloverQuantity == cloverQuantity
}?.amount ?: game.prices.firstOrNull {
    it.numberQuantity == numberQuantity
}?.amount ?: BigDecimal.ZERO

internal fun calculateBetTotal(unitPrice: BigDecimal, recurringDraws: Int, quantity: Int): BigDecimal =
    unitPrice
        .multiply(BigDecimal.valueOf(recurringDraws.coerceAtLeast(1).toLong()))
        .multiply(BigDecimal.valueOf(quantity.coerceAtLeast(1).toLong()))
