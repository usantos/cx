package br.gov.caixa.loterias.feature.tickets.presentation.screen

import br.gov.caixa.loterias.feature.tickets.domain.model.TicketPeriod

data class ConfirmedBetsListActions(val openBetFilters: () -> Unit, val retryBets: () -> Unit, val selectPeriod: (TicketPeriod) -> Unit)
