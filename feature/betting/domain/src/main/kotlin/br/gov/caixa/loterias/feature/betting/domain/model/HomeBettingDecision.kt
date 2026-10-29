package br.gov.caixa.loterias.feature.betting.domain.model

import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination

sealed interface HomeBettingDecision {
    data class Ready(val target: HomeBettingTarget) : HomeBettingDecision
    data class CompleteAccount(val destination: PostLoginDestination) : HomeBettingDecision
}
