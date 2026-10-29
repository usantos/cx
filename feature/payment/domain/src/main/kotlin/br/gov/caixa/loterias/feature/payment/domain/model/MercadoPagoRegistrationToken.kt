package br.gov.caixa.loterias.feature.payment.domain.model

import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure

sealed interface MercadoPagoRegistrationToken {
    class Available(val value: String) : MercadoPagoRegistrationToken {
        override fun toString() = "MercadoPagoRegistrationToken(redacted)"
    }
    data class Rejected(val failure: ServiceFailure) : MercadoPagoRegistrationToken
}
