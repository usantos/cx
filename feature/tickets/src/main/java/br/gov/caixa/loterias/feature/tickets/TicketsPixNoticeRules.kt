package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.core.domain.payment.PurchaseNotice
import br.gov.caixa.loterias.core.domain.payment.PurchaseNoticeReason
import br.gov.caixa.loterias.core.presentation.*
import java.time.LocalDateTime

/** Same response-time notice contract as payment; no feature-to-feature dependency. */
data class TicketsPixNoticeUiState(val message: String, val offerMoreBets: Boolean, val offerRegistration: Boolean)

internal fun ticketsPixNoticeUi(notice: PurchaseNotice, now: LocalDateTime): TicketsPixNoticeUiState {
    val message = when (notice.reason) {
        PurchaseNoticeReason.MORE_BETS_REQUIRED, PurchaseNoticeReason.BUSINESS_RULE, PurchaseNoticeReason.MERCADO_PAGO_REGISTRATION ->
            notice.message?.takeIf { it.isNotEmpty() } ?: LEGACY_OPERATION_FAILED
        PurchaseNoticeReason.SERVER_MESSAGE -> notice.message ?: LEGACY_REQUEST_FAILED
        PurchaseNoticeReason.SERVER_FAILURE -> LEGACY_REQUEST_FAILED
        PurchaseNoticeReason.INVALID_IDENTIFICATION -> LEGACY_INVALID_IDENTIFICATION
    }
    val moreBets = notice.reason == PurchaseNoticeReason.MORE_BETS_REQUIRED
    val registration = notice.reason == PurchaseNoticeReason.MERCADO_PAGO_REGISTRATION
    val diagnostics = !moreBets && !registration && notice.reason != PurchaseNoticeReason.INVALID_IDENTIFICATION
    return TicketsPixNoticeUiState(if (diagnostics) legacyDiagnosticMessage(message, notice.code, now) else message, moreBets, registration)
}
