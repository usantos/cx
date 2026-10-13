package br.gov.caixa.loterias.core.presentation

import br.gov.caixa.loterias.core.common.NetworkAvailability
import br.gov.caixa.loterias.core.domain.service.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.ServiceRedirect
import java.time.LocalDateTime

/** Identity, not enum equality, distinguishes repeated navigation requests. */
class ServiceRedirectEvent(val destination: ServiceRedirect)

data class ServiceFailurePresentation(
    val notice: ServiceNoticeUiState? = null,
    val redirect: ServiceRedirectEvent? = null,
) {
    val pending: Boolean get() = notice != null || redirect != null

    fun dismiss(expected: ServiceNoticeUiState): ServiceFailurePresentation =
        if (notice !== expected) this else copy(notice = null)

    fun accept(expected: ServiceNoticeUiState, network: NetworkAvailability): ServiceFailurePresentation {
        if (notice !== expected) return this
        val destination = expected.destination
        val offline = destination?.let { loginConnectivityNotice(it, network) }
        return copy(notice = offline, redirect = destination?.takeIf { offline == null }?.let(::ServiceRedirectEvent))
    }
}

fun serviceFailurePresentation(
    failure: ServiceFailure,
    network: NetworkAvailability,
    now: LocalDateTime,
): ServiceFailurePresentation {
    val notice = when (failure) {
        is ServiceFailure.ConnectionFailed -> connectionFailureNotice(failure.reason, network.current())
        is ServiceFailure.NoticeRequired -> serviceNoticeUi(failure.notice, now)
        is ServiceFailure.RegistrationRequired -> ServiceNoticeUiState(LEGACY_OPERATION_FAILED)
        is ServiceFailure.RedirectRequired -> when (failure.destination) {
            ServiceRedirect.ACCEPT_TERMS -> ServiceNoticeUiState(LEGACY_UPDATED_TERMS, failure.destination)
            else -> loginConnectivityNotice(failure.destination, network)
        }
        ServiceFailure.Unchanged -> null
    }
    val redirect = (failure as? ServiceFailure.RedirectRequired)?.takeIf { notice == null }
        ?.let { ServiceRedirectEvent(it.destination) }
    return ServiceFailurePresentation(notice, redirect)
}
