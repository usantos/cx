package br.gov.caixa.loterias.feature.cart.domain.repository

/** Persistência local da regra legada que alerta quando um favorito já virou carrinho. */
interface FavoriteCartUsageRepository {
    suspend fun wasAddedToCurrentCart(cartId: Long): Boolean
    suspend fun markAsAddedToCurrentCart(cartId: Long)
    suspend fun clear()
}
