package br.gov.caixa.loterias.feature.payment.domain.model

data class PurchaseSubmission(
    val purchaseId: Long,
    val pollingIntervalMillis: Long,
    val pollingAttempts: Int,
    val status: Int,
) {
    val completed: Boolean get() = purchasePaymentCompleted(status)
    val rejected: Boolean get() = purchasePaymentRejected(status)
}
