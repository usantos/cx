package br.gov.caixa.loterias.feature.tickets

fun shouldShowInvalidTeimosinhaAlert(
    isOriginalRecurringBet: Boolean,
    hasPrizeRedemption: Boolean,
) = isOriginalRecurringBet && hasPrizeRedemption

fun betDetailRoute(isOriginalRecurringBet: Boolean, hasPrizeRedemption: Boolean) =
    "tickets/bet-detail?original=$isOriginalRecurringBet&resgate=$hasPrizeRedemption"

internal enum class TeimosinhaNotice { ORIGINAL_PAID, FIRST_CONTEST }

internal fun teimosinhaNotice(bet: br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet): TeimosinhaNotice? = when {
    bet.recurringDraws <= 0 || bet.statusId == 100L -> null
    !bet.exchange && bet.statusId == 11L -> TeimosinhaNotice.ORIGINAL_PAID
    bet.status.contains("não apurado", ignoreCase = true) -> null
    else -> TeimosinhaNotice.FIRST_CONTEST
}
