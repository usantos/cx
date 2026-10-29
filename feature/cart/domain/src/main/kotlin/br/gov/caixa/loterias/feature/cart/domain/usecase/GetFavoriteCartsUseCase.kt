package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCart

fun interface GetFavoriteCartsUseCase {
    suspend operator fun invoke(): AppResult<List<FavoriteCart>>
}
