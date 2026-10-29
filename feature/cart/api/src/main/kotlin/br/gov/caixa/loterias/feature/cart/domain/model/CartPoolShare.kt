package br.gov.caixa.loterias.feature.cart.domain.model

data class CartPoolShare(
    val code: String,
    val reservedShare: Int,
    val totalShares: Int,
    val expiresAt: String,
)
