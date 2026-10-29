package br.gov.caixa.loterias.feature.retail.presentation.mapper

import br.gov.caixa.loterias.core.domain.service.model.ServiceNotice
import br.gov.caixa.loterias.core.presentation.rule.serviceNoticeUi
import java.time.LocalDateTime
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState

internal fun prizeNoticeUi(notice: ServiceNotice, now: LocalDateTime): ServiceNoticeUiState = serviceNoticeUi(notice, now)
