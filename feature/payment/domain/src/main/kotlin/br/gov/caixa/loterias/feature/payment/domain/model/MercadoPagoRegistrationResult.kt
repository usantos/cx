package br.gov.caixa.loterias.feature.payment.domain.model

import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure

sealed interface MercadoPagoRegistrationResult {
    data class Ready(val request: MercadoPagoRegistration) : MercadoPagoRegistrationResult
    data class Rejected(val failure: ServiceFailure) : MercadoPagoRegistrationResult
}
