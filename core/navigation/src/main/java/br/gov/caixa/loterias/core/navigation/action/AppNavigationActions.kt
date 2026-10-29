package br.gov.caixa.loterias.core.navigation.action

import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision

/** Transições entre fluxos de features; as funções de registro apenas conectam estas funções de retorno. */
class AppNavigationActions(
    val handleBetEntry: (BetEntryDecision, Boolean) -> Unit,
    val onPrizeBetEntry: (BetEntryDecision) -> Unit,
    val onPrizeRedirect: (br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect) -> Unit,
    val onTicketServiceRedirect: (br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect) -> Unit,
    val onPaymentRedirect: (br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect) -> Unit,
    val onPaymentMoreBets: () -> Unit,
)
