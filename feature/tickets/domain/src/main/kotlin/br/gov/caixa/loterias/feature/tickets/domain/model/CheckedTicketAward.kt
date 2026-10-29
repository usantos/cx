package br.gov.caixa.loterias.feature.tickets.domain.model

import java.math.BigDecimal

data class CheckedTicketAward(
    val contest: Int?, val status: String, val name: String = "",
    val hitQuantity: Int? = null, val draw: Int? = null, val value: BigDecimal? = null,
)
