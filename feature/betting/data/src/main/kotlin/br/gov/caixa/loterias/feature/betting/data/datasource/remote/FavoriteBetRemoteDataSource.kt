package br.gov.caixa.loterias.feature.betting.data.datasource.remote

import br.gov.caixa.loterias.core.network.dto.betting.FavoriteBetPageDto
import br.gov.caixa.loterias.core.network.dto.betting.GroupedFavoriteBetsDto
import br.gov.caixa.loterias.core.network.dto.cart.CartDto
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetCartOptions
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetDraft

interface FavoriteBetRemoteDataSource {
    suspend fun grouped(): GroupedFavoriteBetsDto?
    suspend fun byModality(modalityId: Int, offset: Int): FavoriteBetPageDto?
    suspend fun delete(favoriteBetId: Long)
    suspend fun addToCart(favoriteBetId: Long, options: FavoriteBetCartOptions): CartDto?
    suspend fun validate(draft: FavoriteBetDraft)
    suspend fun save(draft: FavoriteBetDraft)
}
