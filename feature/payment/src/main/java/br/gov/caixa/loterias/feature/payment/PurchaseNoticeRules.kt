package br.gov.caixa.loterias.feature.payment

import br.gov.caixa.loterias.core.domain.payment.PurchaseNotice
import br.gov.caixa.loterias.core.domain.payment.PurchaseNoticeReason
import java.time.LocalDateTime
import br.gov.caixa.loterias.core.presentation.*

data class PurchaseNoticeUiState(val message: String, val offerMoreBets: Boolean, val offerRegistration: Boolean = false)

internal const val PURCHASE_OPERATION_FAILED = LEGACY_OPERATION_FAILED
internal const val PURCHASE_REQUEST_FAILED = LEGACY_REQUEST_FAILED
internal const val PURCHASE_INVALID_IDENTIFICATION = LEGACY_INVALID_IDENTIFICATION

/** Snapshot the local diagnostic time once, when the notice arrives, not at recomposition. */
internal fun purchaseNoticeUi(notice: PurchaseNotice, now: LocalDateTime): PurchaseNoticeUiState {
    val message = when (notice.reason) {
        PurchaseNoticeReason.MORE_BETS_REQUIRED, PurchaseNoticeReason.BUSINESS_RULE, PurchaseNoticeReason.MERCADO_PAGO_REGISTRATION ->
            notice.message?.takeIf { it.isNotEmpty() } ?: PURCHASE_OPERATION_FAILED
        PurchaseNoticeReason.SERVER_MESSAGE -> notice.message ?: PURCHASE_REQUEST_FAILED
        PurchaseNoticeReason.SERVER_FAILURE -> PURCHASE_REQUEST_FAILED
        PurchaseNoticeReason.INVALID_IDENTIFICATION -> PURCHASE_INVALID_IDENTIFICATION
    }
    val moreBets = notice.reason == PurchaseNoticeReason.MORE_BETS_REQUIRED
    val registration = notice.reason == PurchaseNoticeReason.MERCADO_PAGO_REGISTRATION
    val diagnostics = !moreBets && !registration && notice.reason != PurchaseNoticeReason.INVALID_IDENTIFICATION
    val text = if (diagnostics) legacyDiagnosticMessage(message, notice.code, now) else message
    return PurchaseNoticeUiState(text, moreBets, registration)
}
