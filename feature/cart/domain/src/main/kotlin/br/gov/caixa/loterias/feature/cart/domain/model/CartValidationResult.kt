package br.gov.caixa.loterias.feature.cart.domain.model

import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure

sealed interface CartValidationResult {
    data object Accepted : CartValidationResult
    data class Rejected(val failure: ServiceFailure) : CartValidationResult
}
