package br.gov.caixa.loterias.core.preferences

internal enum class LegacyCartReceiptPhase { PENDING, CONFIRMED, CONFLICT }
internal data class LegacyCartReceipt(
    val transferId: String,
    val ownerKey: String,
    val phase: LegacyCartReceiptPhase,
    val fingerprints: Map<Long, String>,
) {
    override fun toString() = "LegacyCartReceipt(redacted)"
}

internal interface LegacyCartTransferTransaction {
    fun receipt(): LegacyCartReceipt?
    fun tables(): Map<String, List<LegacyCartRow>>
    fun save(receipt: LegacyCartReceipt)
    fun deleteBet(id: Long)
    fun clearReceipt()
}

/** Must serialize across instances/processes, rollback on failure, and commit before returning. */
internal interface LegacyCartTransferStore {
    fun <T> transaction(action: (LegacyCartTransferTransaction) -> T): T
}
