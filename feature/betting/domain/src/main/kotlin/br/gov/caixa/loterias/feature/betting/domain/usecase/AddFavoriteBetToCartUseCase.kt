package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetCartOptions

fun interface AddFavoriteBetToCartUseCase {
    suspend operator fun invoke(favoriteBetId: Long, options: FavoriteBetCartOptions): AppResult<ShoppingCart>
}
