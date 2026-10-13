package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.cart.FavoriteCart
import br.gov.caixa.loterias.core.domain.cart.FavoriteCartBet
import br.gov.caixa.loterias.core.domain.cart.FavoriteCartRepository
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import br.gov.caixa.loterias.core.network.FavoriteCartBetDto
import com.google.gson.JsonElement

class RemoteFavoriteCartRepository(
    private val remote: FavoriteCartRemoteDataSource,
) : FavoriteCartRepository {
    override suspend fun favorites() = remote.favoriteCarts().orEmpty().map { dto ->
        FavoriteCart(dto.id ?: 0, dto.name.orEmpty().ifBlank { "Carrinho favorito" })
    }

    override suspend fun bets(cartId: Long) = remote.favoriteCartBets(cartId).orEmpty().map { it.toDomain() }

    override suspend fun deleteCart(cartId: Long) = remote.deleteFavoriteCart(cartId).orEmpty().map { dto ->
        FavoriteCart(dto.id ?: 0, dto.name.orEmpty().ifBlank { "Carrinho favorito" })
    }

    override suspend fun deleteBet(cartId: Long, betId: Long) =
        remote.deleteFavoriteCartBet(cartId, betId).orEmpty().map { it.toDomain() }

    override suspend fun addToCurrentCart(cartId: Long, specialSelection: Int): ShoppingCart =
        remote.addFavoriteCartToCurrent(cartId, specialSelection).toDomainCart()

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
            numberColumns = if (modality?.value == 7 && selectedNumbers?.isJsonArray == true)
                selectedNumbers?.asJsonArray?.map { it.flattenNumbers() }.orEmpty() else emptyList(),
            numberCount = numberCount?.coerceAtLeast(0) ?: selectedNumbers.flattenNumbers().size,
            cloverCount = cloverCount?.coerceAtLeast(0) ?: clovers?.selected.flattenNumbers().size,
            favoriteTeam = favoriteTeam?.let { team ->
                listOfNotNull(team.name?.takeIf(String::isNotBlank), team.state?.takeIf(String::isNotBlank)).joinToString("/")
            },
            luckyMonth = luckyMonth?.name,
        )
    }

    private fun JsonElement?.flattenNumbers(): List<Int> = when {
        this == null || isJsonNull -> emptyList()
        isJsonArray -> asJsonArray.flatMap { it.flattenNumbers() }
        isJsonPrimitive && asJsonPrimitive.isNumber -> listOf(asInt)
        else -> emptyList()
    }
}
