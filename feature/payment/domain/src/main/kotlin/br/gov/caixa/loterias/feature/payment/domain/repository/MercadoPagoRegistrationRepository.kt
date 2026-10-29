package br.gov.caixa.loterias.feature.payment.domain.repository

import br.gov.caixa.loterias.feature.payment.domain.model.MercadoPagoRegistrationToken

fun interface MercadoPagoRegistrationRepository {
    suspend fun mercadoPagoRegistrationToken(email: String): MercadoPagoRegistrationToken
}
