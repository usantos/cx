package br.gov.caixa.loterias.feature.tickets.domain.model

import java.math.BigDecimal

data class Purchase(
    val id: Long,
    val date: String,
    val status: String,
    val paymentMethod: String,
    val total: BigDecimal,
    val pixCopyPaste: String = "",
    val paymentExpiresAt: String = "",
    val statusId: Long = 0,
    val paymentMethodId: Long = 0,
    val finalizedTime: String = "",
    val endToEndId: String = "",
)
