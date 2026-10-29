package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem

fun interface RemoveCartItemUseCase {
    suspend operator fun invoke(item: CartItem): AppResult<ShoppingCart>
}
