package br.gov.caixa.loterias.feature.payment.domain.model

/** Estado puro de continuação que reproduz a comparação posição < repetições de CompraUtil. */
data class CardPurchasePolling(
    val purchaseId: Long,
    val intervalMillis: Long,
    val attemptLimit: Int,
    val completedAttempts: Int = 0,
) {
    val hasNext: Boolean get() = completedAttempts < attemptLimit

    fun after(progress: PurchaseProgress): CardPurchasePolling? {
        require(progress.redirect == null) { "Handle the purchase redirect before evaluating completion" }
        require(progress.notice == null) { "Handle the purchase notice before evaluating completion" }
        return progress.nextPoll?.let {
            CardPurchasePolling(
                purchaseId = progress.purchaseId,
                intervalMillis = it.intervalMillis.coerceAtLeast(0),
                attemptLimit = it.attemptLimit.coerceAtLeast(0),
                completedAttempts = completedAttempts + 1,
            )
        }
    }
}
