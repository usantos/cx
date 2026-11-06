package br.gov.caixa.loterias.core.navigation.state

/** Evidência emitida apenas por callbacks locais de uma compra aceita ou de sua recuperação. */
sealed interface PurchaseRecoveryEvidence {
    val purchaseId: Long?

    data class Confirmed(override val purchaseId: Long) : PurchaseRecoveryEvidence {
        init {
            require(purchaseId > 0)
        }
    }

    data class Review(override val purchaseId: Long?) : PurchaseRecoveryEvidence {
        init {
            require(purchaseId == null || purchaseId > 0)
        }
    }
}

/** Evidência mantida com os demais detalhes privados; a chave da concessão não entra no snapshot. */
internal data class StoredPurchaseRecovery(val evidence: PurchaseRecoveryEvidence, val privateEntryKey: String?)

/** Recuperação de uma operação aceita, vinculada ao proprietário e à sessão que a iniciou. */
class PurchaseRecoverySession(
    private val details: LoggedDetailContext,
    private val ownerKey: String?,
    private val privateEntryKey: String,
    private val isSessionCurrent: () -> Boolean,
) {
    fun grant(evidence: PurchaseRecoveryEvidence): Boolean {
        if (!ownsCurrentSession()) return false
        details.purchaseRecovery = StoredPurchaseRecovery(evidence, privateEntryKey)
        return true
    }

    fun current(): PurchaseRecoveryEvidence? {
        if (!ownsCurrentSession()) return null
        val recovery = details.purchaseRecovery ?: return null
        if (recovery.privateEntryKey == null) {
            details.purchaseRecovery = recovery.copy(privateEntryKey = privateEntryKey)
        } else if (recovery.privateEntryKey != privateEntryKey) {
            return null
        }
        return recovery.evidence
    }

    fun permitsSelectedPurchase(purchaseId: Long): Boolean =
        purchaseId > 0 && ownsCurrentSession() && details.selectedPurchase?.id == purchaseId

    fun permitsSelectedBet(betId: Long): Boolean = betId > 0 && ownsCurrentSession() && details.selectedBet?.id == betId

    fun finish() {
        if (current() != null) details.purchaseRecovery = null
    }

    private fun ownsCurrentSession(): Boolean = !ownerKey.isNullOrBlank() && ownerKey == details.owner && isSessionCurrent()
}
