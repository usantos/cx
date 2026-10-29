package br.gov.caixa.loterias.feature.account.domain.model

import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure

sealed interface NotificationReadResult {
    data object Confirmed : NotificationReadResult
    data class Rejected(val failure: ServiceFailure) : NotificationReadResult
}
