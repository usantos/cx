package br.gov.caixa.loterias.feature.tickets.domain.model

data class PurchaseDetails(
    val purchase: Purchase,
    val bets: List<ConfirmedBet>,
    val serverDateTime: String = "",
)
