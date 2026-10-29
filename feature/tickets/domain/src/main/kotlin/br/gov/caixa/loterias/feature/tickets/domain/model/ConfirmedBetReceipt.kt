package br.gov.caixa.loterias.feature.tickets.domain.model

import java.math.BigDecimal

data class ConfirmedBetReceipt(
    val nsbi: String = "",
    val paymentDate: String = "",
    val paymentTime: String = "",
    val paymentLocation: String = "",
    val paymentNsu: String = "",
    val netPrize: BigDecimal? = null,
    val awards: List<CheckedTicketAward> = emptyList(),
)
