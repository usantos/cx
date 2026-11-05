package br.gov.caixa.loterias.core.domain.contract.tickets.model

import br.gov.caixa.loterias.core.domain.contract.tickets.model.CheckedTicketAward
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
