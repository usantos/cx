package br.gov.caixa.loterias.feature.cart.domain.repository

import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem

interface CartRepository {
    suspend fun current(): ShoppingCart
    suspend fun remove(item: CartItem): ShoppingCart
    suspend fun clear(): ShoppingCart
    suspend fun validate(): CartValidationResult
    suspend fun validateFavoriteName(name: String)
    suspend fun saveFavorite(name: String)
}
