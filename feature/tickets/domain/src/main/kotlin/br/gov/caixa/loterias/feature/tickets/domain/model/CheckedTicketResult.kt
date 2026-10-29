package br.gov.caixa.loterias.feature.tickets.domain.model

import java.math.BigDecimal

/** Resultado de conferir-bilhete, distinto do contrato do histórico de apostas confirmadas. */
data class CheckedTicketResult(
    val bets: List<ConfirmedBet> = emptyList(),
    val statusId: Int? = null,
    val modalityId: Int? = null,
    val modalityName: String = "",
    val netPrize: BigDecimal? = null,
    val awards: List<CheckedTicketAward> = emptyList(),
)
