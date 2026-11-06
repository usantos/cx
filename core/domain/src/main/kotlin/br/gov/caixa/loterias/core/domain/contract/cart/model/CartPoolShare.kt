package br.gov.caixa.loterias.core.domain.contract.cart.model

data class CartPoolShare(
    val code: String,
    val reservedShare: Int,
    val totalShares: Int,
    val expiresAt: String,
)
