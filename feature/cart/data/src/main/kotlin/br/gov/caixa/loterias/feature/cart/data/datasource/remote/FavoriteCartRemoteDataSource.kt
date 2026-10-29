package br.gov.caixa.loterias.feature.cart.data.datasource.remote

import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.feature.cart.data.dto.FavoriteCartDto
import br.gov.caixa.loterias.feature.cart.data.dto.FavoriteCartBetDto

interface FavoriteCartRemoteDataSource {
    suspend fun favoriteCarts(): List<FavoriteCartDto>?
    suspend fun favoriteCartBets(cartId: Long): List<FavoriteCartBetDto>?
    suspend fun deleteFavoriteCart(cartId: Long): List<FavoriteCartDto>?
    suspend fun deleteFavoriteCartBet(cartId: Long, betId: Long): List<FavoriteCartBetDto>?
    suspend fun addFavoriteCartToCurrent(cartId: Long, specialSelection: Int): CartDto?
}
