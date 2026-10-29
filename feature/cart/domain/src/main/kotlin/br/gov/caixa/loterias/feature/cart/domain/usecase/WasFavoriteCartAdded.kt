package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.feature.cart.domain.repository.FavoriteCartUsageRepository

class WasFavoriteCartAdded(private val repository: FavoriteCartUsageRepository) : WasFavoriteCartAddedUseCase {
    override suspend fun invoke(cartId: Long): Boolean = cartId > 0 && repository.wasAddedToCurrentCart(cartId)
}
