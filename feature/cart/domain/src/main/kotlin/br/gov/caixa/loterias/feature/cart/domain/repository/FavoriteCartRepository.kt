package br.gov.caixa.loterias.feature.cart.domain.repository

import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCart
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet

interface FavoriteCartRepository {
    suspend fun favorites(): List<FavoriteCart>
    suspend fun bets(cartId: Long): List<FavoriteCartBet>
    suspend fun deleteCart(cartId: Long): List<FavoriteCart>
    suspend fun deleteBet(cartId: Long, betId: Long): List<FavoriteCartBet>
    suspend fun addToCurrentCart(cartId: Long, specialSelection: Int): ShoppingCart
}
