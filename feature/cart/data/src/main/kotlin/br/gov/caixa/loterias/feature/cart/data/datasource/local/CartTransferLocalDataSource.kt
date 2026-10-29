package br.gov.caixa.loterias.feature.cart.data.datasource.local

internal enum class CartReceiptPhase { PENDING, CONFIRMED, CONFLICT }
internal data class CartReceipt(
    val transferId: String,
    val ownerKey: String,
    val phase: CartReceiptPhase,
    val fingerprints: Map<Long, String>,
) {
    override fun toString() = "CartReceipt(redacted)"
}

internal interface CartTransferTransaction {
    fun receipt(): CartReceipt?
    fun tables(): Map<String, List<CartSnapshotRow>>
    fun save(receipt: CartReceipt)
    fun deleteBet(id: Long)
    fun clearReceipt()
}

/** Deve serializar entre instâncias e processos, reverter em caso de falha e confirmar antes de retornar. */
internal interface CartTransferLocalDataSource {
    suspend fun requiresReview(): Boolean = false
    suspend fun <T> transaction(action: (CartTransferTransaction) -> T): T
}
