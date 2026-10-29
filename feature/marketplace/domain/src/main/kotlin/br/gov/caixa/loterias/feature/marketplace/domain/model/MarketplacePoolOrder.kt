package br.gov.caixa.loterias.feature.marketplace.domain.model

import java.math.BigDecimal

enum class MarketplacePoolOrderOrigin { LIST, DETAIL }

data class MarketplacePoolOrder(
    val code: String,
    val modality: String,
    val contest: Int,
    val contestType: Int,
    val drawDate: String,
    val availableShares: Int,
    val requestedShares: Int,
    val serviceFeePerShare: BigDecimal,
    val firstSharePriceWithFee: BigDecimal,
    val regularSharePriceWithFee: BigDecimal = firstSharePriceWithFee,
    val origin: MarketplacePoolOrderOrigin = MarketplacePoolOrderOrigin.LIST,
) {
    val totalAmount: BigDecimal
        get() = firstSharePriceWithFee + regularSharePriceWithFee.multiply((requestedShares - 1).coerceAtLeast(0).toBigDecimal())

    // A requisição nativa de detalhes usa um valor diferente do total exibido.
    val requestAmount: BigDecimal
        get() = when (origin) {
            MarketplacePoolOrderOrigin.LIST -> totalAmount
            MarketplacePoolOrderOrigin.DETAIL -> firstSharePriceWithFee.multiply(requestedShares.toBigDecimal())
        }
}
