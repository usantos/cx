package br.gov.caixa.loterias.feature.cart.domain.model

data class CheckoutLocationAccess(
    val providerEnabled: Boolean,
    val fineGranted: Boolean,
    val coarseGranted: Boolean,
    val permissionRequestCount: Int,
)
