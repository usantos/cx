package br.gov.caixa.loterias.feature.cart.data.repository

import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart
import br.gov.caixa.loterias.core.network.dto.cart.FavoriteCartBetDto
import br.gov.caixa.loterias.core.network.dto.cart.FavoriteCartDto
import br.gov.caixa.loterias.core.network.mapper.flattenNumbers
import br.gov.caixa.loterias.feature.cart.data.datasource.remote.FavoriteCartRemoteDataSource
import br.gov.caixa.loterias.feature.cart.data.localizedMessage as messageText
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCart
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet
import br.gov.caixa.loterias.feature.cart.domain.repository.FavoriteCartRepository

class FavoriteCartRepositoryImpl(private val remote: FavoriteCartRemoteDataSource) : FavoriteCartRepository {
    override suspend fun favorites() = remote.favoriteCarts().toFavorites()

    override suspend fun bets(cartId: Long) = remote.favoriteCartBets(cartId).orEmpty().map { it.toDomain() }

    override suspend fun deleteCart(cartId: Long) = remote.deleteFavoriteCart(cartId).toFavorites()

    override suspend fun deleteBet(cartId: Long, betId: Long) = remote.deleteFavoriteCartBet(cartId, betId).orEmpty().map { it.toDomain() }

    override suspend fun addToCurrentCart(cartId: Long, specialSelection: Int): ShoppingCart =
        remote.addFavoriteCartToCurrent(cartId, specialSelection).toDomainCart()

    /** O contrato não fornece data de criação; a lista usa o ID numérico decrescente. */
    private fun List<FavoriteCartDto>?.toFavorites(): List<FavoriteCart> = orEmpty().map { dto ->
        FavoriteCart(dto.id ?: 0, dto.name.orEmpty().ifBlank { messageText("carrinho_favorito_e0c191") })
    }.sortedByDescending { it.id }

    private fun FavoriteCartBetDto.toDomain(): FavoriteCartBet {
        val isSpecial = mirror == true
        val normalName = modality?.description.orEmpty()
        val specialName = modality?.specialDescription.orEmpty()
        return FavoriteCartBet(
            id = id ?: 0,
            modality = if (isSpecial) specialName.ifBlank { normalName } else normalName,
            numbers = selectedNumbers.flattenNumbers(),
            clovers = clovers?.selected.flattenNumbers(),
            recurringDraws = recurringDraws ?: 0,
            surprise = surpriseIndicator.orEmpty() != "NAO_SURPRESINHA",
            special = isSpecial,
            modalityId = modality?.value,
            numberColumns = if (modality?.value == 7 && selectedNumbers?.isJsonArray == true) {
                selectedNumbers?.asJsonArray?.map { it.flattenNumbers() }.orEmpty()
            } else {
                emptyList()
            },
            numberCount = numberCount?.coerceAtLeast(0) ?: selectedNumbers.flattenNumbers().size,
            cloverCount = cloverCount?.coerceAtLeast(0) ?: clovers?.selected.flattenNumbers().size,
            favoriteTeam = favoriteTeam?.let { team ->
                listOfNotNull(team.name?.takeIf(String::isNotBlank), team.state?.takeIf(String::isNotBlank)).joinToString("/")
            },
            luckyMonth = luckyMonth?.name
        )
    }
}
