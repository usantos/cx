package br.gov.caixa.loterias.feature.cart.domain.model

import br.gov.caixa.loterias.core.domain.contract.account.model.PostLoginDestination

sealed interface CartPaymentAccountDecision {
    data object Allowed : CartPaymentAccountDecision
    data object LoginRequired : CartPaymentAccountDecision
    data object Suspended : CartPaymentAccountDecision
    data class CompleteAccount(val destination: PostLoginDestination) : CartPaymentAccountDecision
}
