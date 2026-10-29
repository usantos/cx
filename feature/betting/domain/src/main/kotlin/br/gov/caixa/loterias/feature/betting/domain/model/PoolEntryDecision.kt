package br.gov.caixa.loterias.feature.betting.domain.model

import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination

sealed interface PoolEntryDecision {
    data class Ready(val request: PoolEntryRequest, val showTutorial: Boolean) : PoolEntryDecision
    data class CompleteAccount(val destination: PostLoginDestination) : PoolEntryDecision
}
