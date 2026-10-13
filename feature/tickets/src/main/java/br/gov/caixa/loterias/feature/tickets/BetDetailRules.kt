package br.gov.caixa.loterias.feature.tickets

fun shouldShowInvalidTeimosinhaAlert(
    isOriginalRecurringBet: Boolean,
    hasPrizeRedemption: Boolean,
) = isOriginalRecurringBet && hasPrizeRedemption

fun betDetailRoute(isOriginalRecurringBet: Boolean, hasPrizeRedemption: Boolean) =
    "tickets/bet-detail?original=$isOriginalRecurringBet&resgate=$hasPrizeRedemption"
