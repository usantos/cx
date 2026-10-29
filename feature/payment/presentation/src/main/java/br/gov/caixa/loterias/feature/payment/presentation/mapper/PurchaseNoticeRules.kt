package br.gov.caixa.loterias.feature.payment.presentation.mapper

import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseNotice
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseNoticeReason
import java.time.LocalDateTime
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_INVALID_IDENTIFICATION
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_OPERATION_FAILED
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_REQUEST_FAILED
import br.gov.caixa.loterias.core.presentation.formatting.legacyDiagnosticMessage

data class PurchaseNoticeUiState(val message: String, val offerMoreBets: Boolean, val offerRegistration: Boolean = false)

internal val PURCHASE_OPERATION_FAILED: String get() = LEGACY_OPERATION_FAILED
internal val PURCHASE_REQUEST_FAILED: String get() = LEGACY_REQUEST_FAILED
internal val PURCHASE_INVALID_IDENTIFICATION: String get() = LEGACY_INVALID_IDENTIFICATION

/** Captura uma única vez o horário local de diagnóstico quando o aviso chega, e não na recomposição. */
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
