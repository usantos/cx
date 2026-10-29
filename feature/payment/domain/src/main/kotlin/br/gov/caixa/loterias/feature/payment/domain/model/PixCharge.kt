package br.gov.caixa.loterias.feature.payment.domain.model

import java.math.BigDecimal

data class PixCharge(
    val copyPaste: String,
    val transactionId: String,
    val qrCodeBase64: String,
    val createdAt: String,
    val expiresAt: String,
    val purchaseId: Long,
    val amount: BigDecimal,
)
