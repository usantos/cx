package br.gov.caixa.loterias.core.navigation.state

import androidx.compose.runtime.saveable.Saver

/** Snapshot mínimo: detalhes da compra, códigos Pix e a concessão privada permanecem fora do Bundle. */
internal data class PendingPurchaseRecovery(val owner: String, val evidence: PurchaseRecoveryEvidence)

internal val PurchaseRecoverySaver = Saver<LoggedDetailContext, List<String>>(
    save = { context ->
        val owned = context.owner?.let { owner ->
            context.purchaseRecovery?.let { PendingPurchaseRecovery(owner, it.evidence) }
        } ?: context.pendingPurchaseRecovery
        owned?.let {
            listOf(
                it.owner,
                if (it.evidence is PurchaseRecoveryEvidence.Confirmed) CONFIRMED else REVIEW,
                it.evidence.purchaseId?.toString().orEmpty()
            )
        }
    },
    restore = { saved ->
        decodePurchaseRecovery(saved)?.let { recovery ->
            LoggedDetailContext(null).apply { deferPurchaseRecovery(recovery) }
        }
    }
)

private fun decodePurchaseRecovery(saved: List<String>): PendingPurchaseRecovery? {
    if (saved.size != FIELD_COUNT || saved[0].isBlank()) return null
    val id = saved[2].takeIf { it.isNotBlank() }?.toLongOrNull()
    if (saved[2].isNotBlank() && (id == null || id <= 0)) return null
    val evidence = when (saved[1]) {
        CONFIRMED -> id?.let { PurchaseRecoveryEvidence.Confirmed(it) }
        REVIEW -> PurchaseRecoveryEvidence.Review(id)
        else -> null
    } ?: return null
    return PendingPurchaseRecovery(saved[0], evidence)
}

private const val FIELD_COUNT = 3
private const val CONFIRMED = "confirmed"
private const val REVIEW = "review"
