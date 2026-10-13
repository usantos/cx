package br.gov.caixa.loterias.feature.payment

import androidx.lifecycle.SavedStateHandle
import br.gov.caixa.loterias.core.domain.payment.PixCharge

/** Nav-entry state only. Never saves QR images, account details or credentials. */
internal class SavedPixRestoration(
    private val handle: SavedStateHandle,
    private val ownerKey: String?,
) {
    fun restore(): PaymentUiState? {
        val savedOwner = handle.get<String>(OWNER)
        if (ownerKey.isNullOrBlank() || (savedOwner != null && savedOwner != ownerKey)) {
            return PaymentUiState(loading = false, pixSessionMismatch = true)
        }
        val attempted = handle.get<Boolean>(ATTEMPTED) == true
        val complete = handle.get<Boolean>(COMPLETE) == true
        if (!attempted && !complete) return null
        // A saved attempt without its captured owner cannot authorize another POST.
        if (savedOwner == null) return PaymentUiState(loading = false, pixSessionMismatch = true)
        val charge = if (complete) restoreCharge() else null
        return if (charge != null) PaymentUiState(loading = false, pix = charge)
        else PaymentUiState(loading = false, pixRecoveryRequired = true)
    }

    fun recordAttempt() {
        check(!ownerKey.isNullOrBlank())
        handle[OWNER] = ownerKey
        handle[COMPLETE] = false
        // Written before GeneratePixChargeUseCase can send the purchase POST.
        handle[ATTEMPTED] = true
    }

    fun recordCharge(charge: PixCharge) {
        handle[CODE] = charge.copyPaste
        handle[TRANSACTION] = charge.transactionId
        handle[CREATED] = charge.createdAt
        handle[EXPIRES] = charge.expiresAt
        handle[PURCHASE] = charge.purchaseId
        handle[AMOUNT] = charge.amount.toPlainString()
        // A partial saved result remains an interrupted attempt until every field is stored.
        handle[COMPLETE] = true
    }

    private fun restoreCharge(): PixCharge? {
        val code = handle.get<String>(CODE) ?: return null
        val transaction = handle.get<String>(TRANSACTION) ?: return null
        val created = handle.get<String>(CREATED) ?: return null
        val expires = handle.get<String>(EXPIRES) ?: return null
        val purchase = handle.get<Long>(PURCHASE) ?: return null
        val amount = handle.get<String>(AMOUNT)?.toBigDecimalOrNull() ?: return null
        if (purchase <= 0 || amount.signum() <= 0 || code.isBlank() || transaction.isBlank() ||
            created.isBlank() || expires.isBlank()) return null
        return PixCharge(code, transaction, "", created, expires, purchase, amount)
    }

    private companion object {
        const val OWNER = "pix.owner"
        const val ATTEMPTED = "pix.attempted"
        const val COMPLETE = "pix.complete"
        const val CODE = "pix.code"
        const val TRANSACTION = "pix.transaction"
        const val CREATED = "pix.created"
        const val EXPIRES = "pix.expires"
        const val PURCHASE = "pix.purchase"
        const val AMOUNT = "pix.amount"
    }
}
