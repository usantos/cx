package br.gov.caixa.loterias.feature.marketplace.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolOrder

fun interface AddMarketplacePoolToCartUseCase {
    suspend operator fun invoke(order: MarketplacePoolOrder): AppResult<ShoppingCart>
}
