package br.gov.caixa.loterias.core.presentation.rule

import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_OFFLINE
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_CONNECTION_FAILED
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_RESPONSE_TIMEOUT
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_INVALID_RESPONSE

fun connectionFailureNotice(reason: ServiceConnectionFailure, availability: ConnectionAvailability): ServiceNoticeUiState =
    ServiceNoticeUiState(when {
        availability == ConnectionAvailability.DISCONNECTED -> LEGACY_OFFLINE
        reason == ServiceConnectionFailure.TIMEOUT -> LEGACY_RESPONSE_TIMEOUT
        reason == ServiceConnectionFailure.INVALID_RESPONSE -> LEGACY_INVALID_RESPONSE
        else -> LEGACY_CONNECTION_FAILED
    })

/** Consulta apenas na ação de login; uma indicação desconhecida do dispositivo não comprova ausência de conexão. */
fun loginConnectivityNotice(destination: ServiceRedirect, network: NetworkAvailability): ServiceNoticeUiState? =
    if (destination == ServiceRedirect.LOGIN && network.current() == ConnectionAvailability.DISCONNECTED) {
        ServiceNoticeUiState(LEGACY_OFFLINE)
    } else null
