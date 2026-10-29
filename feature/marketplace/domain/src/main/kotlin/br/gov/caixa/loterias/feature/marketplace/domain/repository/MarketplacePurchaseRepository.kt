package br.gov.caixa.loterias.feature.marketplace.domain.repository

import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolOrder

fun interface MarketplacePurchaseRepository {
    suspend fun addToCart(order: MarketplacePoolOrder): ShoppingCart
}
