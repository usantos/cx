package br.gov.caixa.loterias.feature.betting.domain.repository

import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.betting.domain.model.validate
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetCartOptions

interface FavoriteBetRepository {
    suspend fun favorites(modalityId: Int? = null, offset: Int = 0): List<FavoriteBet>
    suspend fun delete(favoriteBetId: Long)
    suspend fun addToCart(favoriteBetId: Long, options: FavoriteBetCartOptions): ShoppingCart
    suspend fun validate(draft: FavoriteBetDraft)
    suspend fun save(draft: FavoriteBetDraft)
}
