package br.gov.caixa.loterias.feature.tickets.presentation.screen

data class ManualTicketActions(
    val changeTicketCode: (String) -> Unit,
    val changeTicketRetailerCode: (String) -> Unit,
    val checkTicket: () -> Unit,
)
