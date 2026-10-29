package br.gov.caixa.loterias.feature.tickets.domain.model

data class ConfirmedBetsPage(val bets: List<ConfirmedBet>, val backend: ConfirmedBetsBackend = ConfirmedBetsBackend.SILCE)
