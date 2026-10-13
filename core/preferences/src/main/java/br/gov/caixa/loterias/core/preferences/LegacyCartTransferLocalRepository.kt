package br.gov.caixa.loterias.core.preferences

import android.content.Context
import br.gov.caixa.loterias.core.domain.cart.LegacyCartCleanupResult
import br.gov.caixa.loterias.core.domain.cart.LegacyCartTransferPreparation
import br.gov.caixa.loterias.core.domain.cart.LegacyCartTransferRepository
import br.gov.caixa.loterias.core.domain.cart.LegacyCartReview
import java.util.UUID

class LegacyCartTransferLocalRepository internal constructor(
    private val store: LegacyCartTransferStore,
    private val newTransferId: () -> String = { UUID.randomUUID().toString() },
) : LegacyCartTransferRepository {
    override suspend fun prepare(ownerKey: String): LegacyCartTransferPreparation {
        validateOwner(ownerKey)
        return store.transaction { transaction ->
            val receipt = transaction.receipt()
            if (receipt != null) {
                return@transaction if (receipt.ownerKey == ownerKey && receipt.phase == LegacyCartReceiptPhase.CONFIRMED) {
                    LegacyCartTransferPreparation.CleanupPending(receipt.transferId)
                } else LegacyCartTransferPreparation.RequiresReview
            }
            val tables = transaction.tables()
            val bets = decodeLegacyCart(tables)
            if (bets.isEmpty()) return@transaction LegacyCartTransferPreparation.Empty
            val transferId = newTransferId().also { require(it.isNotBlank()) }
            transaction.save(LegacyCartReceipt(transferId, ownerKey, LegacyCartReceiptPhase.PENDING,
                bets.associate { it.localId to checkNotNull(legacyCartFingerprint(tables, it.localId)) }))
            LegacyCartTransferPreparation.Ready(transferId, bets)
        }
    }

    override suspend fun confirmReceived(ownerKey: String, transferId: String): Boolean {
        validateOwner(ownerKey)
        return store.transaction { transaction ->
            val receipt = transaction.receipt() ?: return@transaction false
            if (receipt.ownerKey != ownerKey || receipt.transferId != transferId || receipt.phase == LegacyCartReceiptPhase.CONFLICT) return@transaction false
            if (receipt.phase == LegacyCartReceiptPhase.PENDING) transaction.save(receipt.copy(phase = LegacyCartReceiptPhase.CONFIRMED))
            true
        }
    }

    override suspend fun finishConfirmed(ownerKey: String, transferId: String): LegacyCartCleanupResult {
        validateOwner(ownerKey)
        return store.transaction { transaction ->
            val receipt = checkNotNull(transaction.receipt()) { "Recibo da transferência não encontrado" }
            check(receipt.ownerKey == ownerKey && receipt.transferId == transferId && receipt.phase == LegacyCartReceiptPhase.CONFIRMED) {
                "Transferência não confirmada para esta sessão"
            }
            val tables = transaction.tables()
            val preserved = mutableSetOf<Long>()
            for ((id, expected) in receipt.fingerprints) {
                val current = legacyCartFingerprint(tables, id)
                when {
                    current == null -> Unit // Already removed locally; do not touch any other row.
                    current != expected -> preserved += id
                    else -> transaction.deleteBet(id)
                }
            }
            if (preserved.isEmpty()) {
                transaction.clearReceipt()
                LegacyCartCleanupResult.Complete
            } else {
                transaction.save(receipt.copy(phase = LegacyCartReceiptPhase.CONFLICT))
                LegacyCartCleanupResult.Conflict(preserved.toSet())
            }
        }
    }

    override suspend fun review(ownerKey: String): LegacyCartReview? {
        validateOwner(ownerKey)
        return store.transaction { tx ->
            val receipt = tx.receipt() ?: return@transaction null
            if (receipt.ownerKey != ownerKey || receipt.phase == LegacyCartReceiptPhase.CONFIRMED) return@transaction null
            LegacyCartReview(receipt.transferId, decodeLegacyCart(tx.tables()).filter { it.localId in receipt.fingerprints })
        }
    }

    override suspend fun discardReviewed(ownerKey: String, review: LegacyCartReview): Boolean {
        validateOwner(ownerKey)
        return store.transaction { tx ->
            val receipt = tx.receipt() ?: return@transaction false
            if (receipt.ownerKey != ownerKey || receipt.transferId != review.transferId || receipt.phase == LegacyCartReceiptPhase.CONFIRMED) return@transaction false
            val current = decodeLegacyCart(tx.tables()).filter { it.localId in receipt.fingerprints }
            if (current != review.bets) return@transaction false
            current.forEach { tx.deleteBet(it.localId) }
            tx.clearReceipt()
            true
        }
    }

    private fun validateOwner(ownerKey: String) {
        require(ownerKey.matches(Regex("[0-9a-f]{64}"))) { "Identificação de sessão inválida para transferência" }
    }

    companion object {
        fun fromContext(context: Context): LegacyCartTransferLocalRepository =
            LegacyCartTransferLocalRepository(AndroidLegacyCartTransferStore(context.applicationContext))
    }
}
