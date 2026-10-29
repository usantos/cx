package br.gov.caixa.loterias.feature.payment.domain.model

/** A ausência de continuação significa que o protocolo de processamento do cartão terminou, não que todas as apostas foram efetivadas. */
data class PurchaseProgress(
    val purchaseId: Long,
    val status: Int,
    val nextPoll: PurchasePolling? = null,
    val redirect: PurchaseRedirect? = null,
    val notice: PurchaseNotice? = null,
) {
    val completed: Boolean get() = redirect == null && notice == null && purchasePaymentCompleted(status)
    val rejected: Boolean get() = redirect == null && notice == null && purchasePaymentRejected(status)
}
