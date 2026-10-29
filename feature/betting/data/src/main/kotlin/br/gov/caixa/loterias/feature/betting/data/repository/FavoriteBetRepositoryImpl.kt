package br.gov.caixa.loterias.feature.betting.data.repository

import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetCartOptions
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetDraft
import br.gov.caixa.loterias.feature.betting.domain.repository.FavoriteBetRepository
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.data.repository.toDomainCart
import br.gov.caixa.loterias.feature.betting.data.mapper.toDomain
import br.gov.caixa.loterias.feature.betting.data.datasource.remote.FavoriteBetRemoteDataSource

class FavoriteBetRepositoryImpl(
    private val remote: FavoriteBetRemoteDataSource,
) : FavoriteBetRepository {
    override suspend fun favorites(modalityId: Int?, offset: Int): List<FavoriteBet> =
        if (modalityId == null) {
            remote.grouped()?.groups.orEmpty().values.flatMap { it.items.orEmpty() }
        } else {
            remote.byModality(modalityId, offset)?.items.orEmpty()
        }.map { it.toDomain() }

    override suspend fun delete(favoriteBetId: Long) = remote.delete(favoriteBetId)

    override suspend fun addToCart(favoriteBetId: Long, options: FavoriteBetCartOptions): ShoppingCart =
        remote.addToCart(favoriteBetId, options).toDomainCart()

    override suspend fun validate(draft: FavoriteBetDraft) = remote.validate(draft)

    override suspend fun save(draft: FavoriteBetDraft) = remote.save(draft)



}
