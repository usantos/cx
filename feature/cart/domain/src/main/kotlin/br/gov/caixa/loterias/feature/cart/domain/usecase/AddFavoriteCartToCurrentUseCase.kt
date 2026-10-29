package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart

fun interface AddFavoriteCartToCurrentUseCase {
    suspend operator fun invoke(cartId: Long, specialSelection: Int): AppResult<ShoppingCart>
}
