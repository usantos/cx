package br.gov.caixa.loterias.feature.tickets.presentation.mapper

import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseNotice
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseNoticeReason
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_INVALID_IDENTIFICATION
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_OPERATION_FAILED
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_REQUEST_FAILED
import br.gov.caixa.loterias.core.presentation.formatting.legacyDiagnosticMessage
import java.time.LocalDateTime

/** Mesmo contrato de aviso no recebimento da resposta do pagamento; sem dependência entre funcionalidades. */
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
