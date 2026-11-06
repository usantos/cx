package br.gov.caixa.loterias.core.domain.contract.betting.model

import br.gov.caixa.loterias.core.domain.contract.account.model.PostLoginDestination
import br.gov.caixa.loterias.core.domain.contract.betting.model.BettingParameters
import br.gov.caixa.loterias.core.domain.contract.betting.model.LotteryGameParameters

sealed interface BetEntryDecision {
    data class Ready(val game: LotteryGameParameters, val parameters: BettingParameters? = null) : BetEntryDecision
    data class CompleteAccount(val destination: PostLoginDestination) : BetEntryDecision
}
