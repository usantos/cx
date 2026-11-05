package br.gov.caixa.loterias.feature.cart.domain.repository

import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult

interface CartRepository {
    suspend fun current(): ShoppingCart
    suspend fun remove(item: CartItem): ShoppingCart
    suspend fun clear(): ShoppingCart
    suspend fun validate(): CartValidationResult
    suspend fun validateFavoriteName(name: String)
    suspend fun saveFavorite(name: String)
}
