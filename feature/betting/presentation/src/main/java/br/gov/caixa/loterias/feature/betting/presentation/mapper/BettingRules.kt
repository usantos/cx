package br.gov.caixa.loterias.feature.betting.presentation.mapper

import br.gov.caixa.loterias.feature.betting.presentation.state.LOTECA_ID
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

internal fun toggleSelectedNumber(selected: Set<Int>, number: Int, allowedNumbers: IntRange, maximumSelection: Int): Set<Int> = when {
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

internal fun availableNumberQuantities(game: br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters): List<Int> =
    br.gov.caixa.loterias.feature.betting.domain.rule.BetPricing.availableNumberQuantities(game)

internal fun resolveBetPrice(
    game: br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters,
    numberQuantity: Int,
    cloverQuantity: Int,
): BigDecimal = br.gov.caixa.loterias.feature.betting.domain.rule.BetPricing.numericPrice(game, numberQuantity, cloverQuantity)?.amount
    ?: BigDecimal.ZERO

internal fun calculateBetTotal(unitPrice: BigDecimal, recurringDraws: Int, quantity: Int, mirror: Boolean = false): BigDecimal =
    br.gov.caixa.loterias.feature.betting.domain.rule.BetPricing.total(unitPrice, recurringDraws, quantity, mirror)
