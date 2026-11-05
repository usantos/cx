package br.gov.caixa.loterias.feature.tickets.domain.model

import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.core.domain.contract.tickets.model.Purchase

data class PurchaseDetails(
    val purchase: Purchase,
    val bets: List<ConfirmedBet>,
    val serverDateTime: String = "",
)
