package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.cart.domain.repository.FavoriteCartRepository

class GetFavoriteCarts(private val repository: FavoriteCartRepository) : GetFavoriteCartsUseCase {
    override suspend fun invoke() = appResultOf { repository.favorites() }
}
