package br.gov.caixa.loterias.feature.betting.domain.model

import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination

sealed interface BetEntryDecision {
    data class Ready(val game: LotteryGameParameters, val parameters: BettingParameters? = null) : BetEntryDecision
    data class CompleteAccount(val destination: PostLoginDestination) : BetEntryDecision
}
