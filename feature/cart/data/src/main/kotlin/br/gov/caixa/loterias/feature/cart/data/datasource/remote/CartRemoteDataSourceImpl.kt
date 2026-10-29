package br.gov.caixa.loterias.feature.cart.data.datasource.remote

import br.gov.caixa.loterias.core.network.error.toBackendHttpFailure
import retrofit2.HttpException
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.feature.cart.data.dto.AddFavoriteCartRequestDto
import br.gov.caixa.loterias.feature.cart.data.dto.SaveFavoriteCartRequestDto

class CartRemoteDataSourceImpl(
    private val apiProvider: LoteriasApiProvider,
) : CartRemoteDataSource, BoundCartRemovalRemoteDataSource, FavoriteCartRemoteDataSource {
    override suspend fun currentCart() = apiProvider.current().featureCartApi().currentCart().payload

    override suspend fun removeCartBet(id: Long) = apiProvider.current().featureCartApi().removeCartBet(id).payload

    override suspend fun removeBoundCartBet(id: Long, session: br.gov.caixa.loterias.core.network.api.AccountVerificationSession) =
        apiProvider.current().featureCartApi().removeBoundCartBet(id, session).payload

    override suspend fun removeCartCombo(id: Long) = apiProvider.current().featureCartApi().removeCartCombo(id).payload

    override suspend fun clearCart() = apiProvider.current().featureCartApi().clearCart().payload

    override suspend fun validateCart() {
        try {
            apiProvider.current().featureCartApi().validateCart()
        } catch (failure: HttpException) {
            throw failure.toBackendHttpFailure()
        }
    }

    override suspend fun validateFavoriteCartName(name: String) {
        try {
            apiProvider.current().featureCartApi().validateFavoriteCartName(SaveFavoriteCartRequestDto(name))
        } catch (failure: HttpException) {
            val backend = failure.toBackendHttpFailure()
            val message = backend.metadata?.message?.takeIf(String::isNotBlank)
            if (backend.statusCode == 400 && message != null) throw IllegalArgumentException(message)
            throw backend
        }
    }

    override suspend fun saveCurrentCartFavorite(name: String) {
        try {
            apiProvider.current().featureCartApi().saveCurrentCartFavorite(SaveFavoriteCartRequestDto(name))
        } catch (failure: HttpException) {
            val backend = failure.toBackendHttpFailure()
            val message = backend.metadata?.message?.takeIf(String::isNotBlank)
            if (backend.statusCode == 400 && message != null) throw IllegalArgumentException(message)
            throw backend
        }
    }

    override suspend fun favoriteCarts() = apiProvider.current().featureCartApi().favoriteCarts().payload

    override suspend fun favoriteCartBets(cartId: Long) = apiProvider.current().featureCartApi().favoriteCartBets(cartId).payload

    override suspend fun deleteFavoriteCart(cartId: Long) = apiProvider.current().featureCartApi().deleteFavoriteCart(cartId).payload

    override suspend fun deleteFavoriteCartBet(cartId: Long, betId: Long) =
        apiProvider.current().featureCartApi().deleteFavoriteCartBet(cartId, betId).payload

    override suspend fun addFavoriteCartToCurrent(cartId: Long, specialSelection: Int) =
        apiProvider.current().featureCartApi().addFavoriteCartToCurrent(cartId, AddFavoriteCartRequestDto(specialSelection)).payload
}
