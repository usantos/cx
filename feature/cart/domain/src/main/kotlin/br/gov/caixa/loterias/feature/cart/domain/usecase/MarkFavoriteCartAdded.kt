package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.feature.cart.domain.repository.FavoriteCartUsageRepository

class MarkFavoriteCartAdded(private val repository: FavoriteCartUsageRepository) : MarkFavoriteCartAddedUseCase {
    override suspend fun invoke(cartId: Long) {
        if (cartId > 0) repository.markAsAddedToCurrentCart(cartId)
    }
}
