package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.core.domain.retail.PrizeNotice
import br.gov.caixa.loterias.core.presentation.serviceNoticeUi
import java.time.LocalDateTime

typealias PrizeNoticeUiState = br.gov.caixa.loterias.core.presentation.ServiceNoticeUiState

internal fun prizeNoticeUi(notice: PrizeNotice, now: LocalDateTime): PrizeNoticeUiState = serviceNoticeUi(notice, now)
