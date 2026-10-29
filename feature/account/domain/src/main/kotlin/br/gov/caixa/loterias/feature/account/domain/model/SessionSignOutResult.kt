package br.gov.caixa.loterias.feature.account.domain.model

import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure

sealed interface SessionSignOutResult {
    data class Confirmed(val identity: AuthenticatedSessionIdentity) : SessionSignOutResult
    data class Rejected(val failure: ServiceFailure) : SessionSignOutResult
}
