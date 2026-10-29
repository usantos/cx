package br.gov.caixa.loterias.feature.tickets.domain.model

data class ConfirmedBetsConfiguration(
    val defaultOrdering: Long? = null,
    val defaultStatus: Long? = null,
    val defaultPeriod: TicketPeriod? = null,
    val statuses: List<TicketFilterOption> = emptyList(),
    val orderings: List<TicketFilterOption> = emptyList(),
    val periods: List<TicketPeriod> = emptyList(),
    val betTypes: List<TicketFilterOption> = emptyList(),
    val contestTypes: List<TicketFilterOption> = emptyList(),
)
