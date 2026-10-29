package br.gov.caixa.loterias.feature.cart.data.datasource.remote

import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.data.repository.toDomainCart

internal class CartRemoteOperations(private val remote: CartRemoteDataSource) {
    suspend fun current(): ShoppingCart = remote.currentCart().toDomainCart()

    suspend fun remove(item: CartItem): ShoppingCart {
        val dto = when (item.kind) {
            CartItemKind.COMBO -> remote.removeCartCombo(item.id)
            CartItemKind.INDIVIDUAL_BET, CartItemKind.POOL_SHARE -> remote.removeCartBet(item.id)
        }
        return dto.toDomainCart()
    }

    suspend fun clear(): ShoppingCart = remote.clearCart().toDomainCart()

    suspend fun validateFavoriteName(name: String) = remote.validateFavoriteCartName(name)

    suspend fun saveFavorite(name: String) = remote.saveCurrentCartFavorite(name)
}
