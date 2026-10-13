package br.gov.caixa.loterias.core.domain.cart

sealed interface LegacyCartTransferPreparation {
    data object Empty : LegacyCartTransferPreparation
    data class Ready(val transferId: String, val bets: List<LegacyLocalCartBet>) : LegacyCartTransferPreparation {
        override fun toString() = "LegacyCartTransferPreparation.Ready(redacted)"
    }
    /** No automatic retry is safe: a previous process may already have sent the request. */
    data object RequiresReview : LegacyCartTransferPreparation
    data class CleanupPending(val transferId: String) : LegacyCartTransferPreparation
}

sealed interface LegacyCartCleanupResult {
    data object Complete : LegacyCartCleanupResult
    /** Modified local bets remain stored and must be reconciled before another upload. */
    data class Conflict(val preservedBetIds: Set<Long>) : LegacyCartCleanupResult
}

data class LegacyCartReview(val transferId: String, val bets: List<LegacyLocalCartBet>) {
    override fun toString() = "LegacyCartReview(redacted)"
}

/** Durable local protocol. ownerKey is a hash of the verified issuer/subject, never a token/CPF. */
interface LegacyCartTransferRepository {
    /** Commits a pending receipt BEFORE returning Ready. A second call never returns that batch. */
    suspend fun prepare(ownerKey: String): LegacyCartTransferPreparation
    /** Call only after validating the remote response confirms this exact batch. */
    suspend fun confirmReceived(ownerKey: String, transferId: String): Boolean
    /** Deletes only unchanged rows from a confirmed batch, atomically with its local receipt. */
    suspend fun finishConfirmed(ownerKey: String, transferId: String): LegacyCartCleanupResult
    /** Only this owner can inspect and explicitly discard an unresolved batch. */
    suspend fun review(ownerKey: String): LegacyCartReview? = null
    /** Refuses a changed snapshot; never touches new bets outside this receipt. */
    suspend fun discardReviewed(ownerKey: String, review: LegacyCartReview): Boolean = false
}
