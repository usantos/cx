package br.gov.caixa.loterias.feature.cart.data.datasource.remote

import br.gov.caixa.loterias.feature.cart.data.dto.CartDto

interface CartRemoteDataSource {
    suspend fun currentCart(): CartDto?
    suspend fun removeCartBet(id: Long): CartDto?
    suspend fun removeCartCombo(id: Long): CartDto?
    suspend fun clearCart(): CartDto?
    suspend fun validateCart()
    suspend fun validateFavoriteCartName(name: String)
    suspend fun saveCurrentCartFavorite(name: String)
}
