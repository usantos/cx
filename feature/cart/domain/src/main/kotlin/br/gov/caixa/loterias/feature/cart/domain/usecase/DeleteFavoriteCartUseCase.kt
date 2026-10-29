package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCart

fun interface DeleteFavoriteCartUseCase {
    suspend operator fun invoke(cartId: Long): AppResult<List<FavoriteCart>>
}
