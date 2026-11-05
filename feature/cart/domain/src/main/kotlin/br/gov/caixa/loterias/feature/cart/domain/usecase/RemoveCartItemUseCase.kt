package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart

fun interface RemoveCartItemUseCase {
    suspend operator fun invoke(item: CartItem): AppResult<ShoppingCart>
}
