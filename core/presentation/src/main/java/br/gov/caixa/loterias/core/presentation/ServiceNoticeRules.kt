package br.gov.caixa.loterias.core.presentation

import br.gov.caixa.loterias.core.domain.service.ServiceNotice
import br.gov.caixa.loterias.core.domain.service.ServiceNoticeReason
import br.gov.caixa.loterias.core.domain.service.ServiceRedirect
import java.time.LocalDateTime

data class ServiceNoticeUiState(val message: String, val destination: ServiceRedirect? = null) {
    val positiveLabel: String get() = when (destination) {
        ServiceRedirect.HOME -> "Apostar mais"
        else -> "Entendi"
    }
    val negativeLabel: String? get() = when (destination) {
        ServiceRedirect.HOME -> "Entendi"
        else -> null
    }
}

fun serviceNoticeUi(notice: ServiceNotice, now: LocalDateTime): ServiceNoticeUiState {
    val message = when (notice.reason) {
        ServiceNoticeReason.SERVER_MESSAGE -> notice.message ?: LEGACY_REQUEST_FAILED
        ServiceNoticeReason.SERVER_FAILURE -> LEGACY_REQUEST_FAILED
        ServiceNoticeReason.INVALID_IDENTIFICATION -> LEGACY_INVALID_IDENTIFICATION
        ServiceNoticeReason.LOGIN_REQUIRED -> LEGACY_LOGIN_REQUIRED
        ServiceNoticeReason.OPERATION_FAILED -> LEGACY_OPERATION_FAILED
        else -> notice.message?.takeIf { it.isNotEmpty() } ?: LEGACY_OPERATION_FAILED
    }
    val destination = when (notice.reason) {
        ServiceNoticeReason.MORE_BETS_REQUIRED -> ServiceRedirect.HOME
        ServiceNoticeReason.LOGIN_REQUIRED -> ServiceRedirect.LOGIN
        else -> null
    }
    val diagnostics = notice.reason in setOf(ServiceNoticeReason.BUSINESS_RULE, ServiceNoticeReason.SERVER_MESSAGE, ServiceNoticeReason.SERVER_FAILURE)
    return ServiceNoticeUiState(if (diagnostics) legacyDiagnosticMessage(message, notice.code, now) else message, destination)
}
