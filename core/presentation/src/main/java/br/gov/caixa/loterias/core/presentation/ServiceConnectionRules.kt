package br.gov.caixa.loterias.core.presentation

import br.gov.caixa.loterias.core.common.ConnectionAvailability
import br.gov.caixa.loterias.core.common.NetworkAvailability
import br.gov.caixa.loterias.core.domain.service.ServiceConnectionFailure
import br.gov.caixa.loterias.core.domain.service.ServiceRedirect

fun connectionFailureNotice(reason: ServiceConnectionFailure, availability: ConnectionAvailability): ServiceNoticeUiState =
    ServiceNoticeUiState(when {
        availability == ConnectionAvailability.DISCONNECTED -> LEGACY_OFFLINE
        reason == ServiceConnectionFailure.TIMEOUT -> LEGACY_RESPONSE_TIMEOUT
        reason == ServiceConnectionFailure.INVALID_RESPONSE -> LEGACY_INVALID_RESPONSE
        else -> LEGACY_CONNECTION_FAILED
    })

/** Query only at the login action; an unknown device hint is not evidence of being offline. */
fun loginConnectivityNotice(destination: ServiceRedirect, network: NetworkAvailability): ServiceNoticeUiState? =
    if (destination == ServiceRedirect.LOGIN && network.current() == ConnectionAvailability.DISCONNECTED) {
        ServiceNoticeUiState(LEGACY_OFFLINE)
    } else null
