package br.gov.caixa.loterias.feature.betting.presentation.mapper

import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceNoticeReason
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.state.ServiceFailurePresentation
import br.gov.caixa.loterias.core.presentation.state.ServiceRedirectEvent
import br.gov.caixa.loterias.core.presentation.state.serviceFailurePresentation
import java.time.LocalDateTime

/** As atividades de Rapidão usam a confirmação compartilhada de login Sim/Não logo após HTTP 401. */
internal fun rapidServiceFailurePresentation(
    failure: ServiceFailure,
    network: NetworkAvailability,
    now: LocalDateTime,
): ServiceFailurePresentation =
    if (failure is ServiceFailure.NoticeRequired && failure.notice.reason == ServiceNoticeReason.LOGIN_REQUIRED) {
        ServiceFailurePresentation(redirect = ServiceRedirectEvent(ServiceRedirect.LOGIN))
    } else serviceFailurePresentation(failure, network, now)
