package br.gov.caixa.loterias.feature.cart.data.repository

import android.content.Context
import br.gov.caixa.loterias.feature.cart.domain.model.CartCleanupResult
import br.gov.caixa.loterias.feature.cart.domain.model.CartTransferPreparation
import br.gov.caixa.loterias.feature.cart.domain.repository.CartTransferRepository
import br.gov.caixa.loterias.feature.cart.domain.model.CartReview
import java.util.UUID
import br.gov.caixa.loterias.feature.cart.data.mapper.decodeLegacyCart
import br.gov.caixa.loterias.feature.cart.data.mapper.legacyCartFingerprint
import br.gov.caixa.loterias.feature.cart.data.datasource.local.CartReceiptPhase
import br.gov.caixa.loterias.feature.cart.data.datasource.local.CartReceipt
import br.gov.caixa.loterias.feature.cart.data.datasource.local.CartTransferLocalDataSource
import br.gov.caixa.loterias.feature.cart.data.datasource.local.CartTransferLocalDataSourceImpl

class CartTransferRepositoryImpl internal constructor(
    private val store: CartTransferLocalDataSource,
    private val newTransferId: () -> String = { UUID.randomUUID().toString() },
) : CartTransferRepository {
    internal constructor(
        document: br.gov.caixa.loterias.feature.cart.data.datasource.local.SecureCartDocument,
        newTransferId: () -> String = { UUID.randomUUID().toString() },
    ) : this(CartTransferLocalDataSourceImpl(document), newTransferId)

    override suspend fun prepare(ownerKey: String): CartTransferPreparation {
        validateOwner(ownerKey)
        if (store.requiresReview()) return CartTransferPreparation.RequiresReview
        return store.transaction { transaction ->
            val receipt = transaction.receipt()
            if (receipt != null) {
                return@transaction if (receipt.ownerKey == ownerKey && receipt.phase == CartReceiptPhase.CONFIRMED) {
                    CartTransferPreparation.CleanupPending(receipt.transferId)
                } else {
                    CartTransferPreparation.RequiresReview
                }
            }
            val tables = transaction.tables()
            val bets = decodeLegacyCart(tables)
            if (bets.isEmpty()) return@transaction CartTransferPreparation.Empty
            val transferId = newTransferId().also { require(it.isNotBlank()) }
            transaction.save(
                CartReceipt(
                    transferId,
                    ownerKey,
                    CartReceiptPhase.PENDING,
                    bets.associate { it.localId to checkNotNull(legacyCartFingerprint(tables, it.localId)) }
                )
            )
            CartTransferPreparation.Ready(transferId, bets)
        }
    }

    override suspend fun confirmReceived(ownerKey: String, transferId: String): Boolean {
        validateOwner(ownerKey)
        check(!store.requiresReview()) { "Legacy cart requires review" }
        return store.transaction { transaction ->
            val receipt = transaction.receipt() ?: return@transaction false
            if (receipt.ownerKey != ownerKey || receipt.transferId != transferId ||
                receipt.phase == CartReceiptPhase.CONFLICT
            ) {
                return@transaction false
            }
            if (receipt.phase == CartReceiptPhase.PENDING) transaction.save(receipt.copy(phase = CartReceiptPhase.CONFIRMED))
            true
        }
    }

    override suspend fun finishConfirmed(ownerKey: String, transferId: String): CartCleanupResult {
        validateOwner(ownerKey)
        check(!store.requiresReview()) { "Legacy cart requires review" }
        return store.transaction { transaction ->
            val receipt = checkNotNull(transaction.receipt()) { "Recibo da transferência não encontrado" }
            check(receipt.ownerKey == ownerKey && receipt.transferId == transferId && receipt.phase == CartReceiptPhase.CONFIRMED) {
                "Transferência não confirmada para esta sessão"
            }
            val tables = transaction.tables()
            val preserved = mutableSetOf<Long>()
            for ((id, expected) in receipt.fingerprints) {
                val current = legacyCartFingerprint(tables, id)
                when {
                    current == null -> Unit

                    // Já removido localmente; não toca em nenhuma outra linha.
                    current != expected -> preserved += id

                    else -> transaction.deleteBet(id)
                }
            }
            if (preserved.isEmpty()) {
                transaction.clearReceipt()
                CartCleanupResult.Complete
            } else {
                transaction.save(receipt.copy(phase = CartReceiptPhase.CONFLICT))
                CartCleanupResult.Conflict(preserved.toSet())
            }
        }
    }

    override suspend fun review(ownerKey: String): CartReview? {
        validateOwner(ownerKey)
        check(!store.requiresReview()) { "Legacy cart requires review" }
        return store.transaction { tx ->
            val receipt = tx.receipt() ?: return@transaction null
            if (receipt.ownerKey != ownerKey || receipt.phase == CartReceiptPhase.CONFIRMED) return@transaction null
            CartReview(receipt.transferId, decodeLegacyCart(tx.tables()).filter { it.localId in receipt.fingerprints })
        }
    }

    override suspend fun discardReviewed(ownerKey: String, review: CartReview): Boolean {
        validateOwner(ownerKey)
        check(!store.requiresReview()) { "Legacy cart requires review" }
        return store.transaction { tx ->
            val receipt = tx.receipt() ?: return@transaction false
            if (receipt.ownerKey != ownerKey || receipt.transferId != review.transferId ||
                receipt.phase == CartReceiptPhase.CONFIRMED
            ) {
                return@transaction false
            }
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
        fun fromContext(
            context: Context,
            scope: br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope,
        ): CartTransferRepositoryImpl =
            br.gov.caixa.loterias.feature.cart.data.datasource.local.AndroidCartPersistence(context, scope).let {
                CartTransferRepositoryImpl(CartTransferLocalDataSourceImpl(it.document, it::requiresLegacyReview))
            }
    }
}
