package br.gov.caixa.loterias.feature.cart.domain.port

import br.gov.caixa.loterias.feature.cart.domain.model.CheckoutLocationAccess

interface CheckoutLocationSource {
    suspend fun access(): CheckoutLocationAccess
    suspend fun countryCode(): String?
    suspend fun recordPermissionRequest()
}
