package br.gov.caixa.loterias.feature.tickets.domain.model

data class TicketPeriod(
    val month: Int,
    val year: Int? = null,
    val description: String,
)
