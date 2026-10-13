package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.betting.FavoriteBet
import br.gov.caixa.loterias.core.domain.betting.FavoriteBetCartOptions
import br.gov.caixa.loterias.core.domain.betting.FavoriteBetDraft
import br.gov.caixa.loterias.core.domain.betting.FavoriteBetRepository
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import br.gov.caixa.loterias.core.network.CartDto
import br.gov.caixa.loterias.core.network.FavoriteBetDto
import br.gov.caixa.loterias.core.network.FavoriteBetPageDto
import br.gov.caixa.loterias.core.network.GroupedFavoriteBetsDto
import br.gov.caixa.loterias.core.network.LegacyBackendApiProvider
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonArray

interface FavoriteBetRemoteDataSource {
    suspend fun grouped(): GroupedFavoriteBetsDto?
    suspend fun byModality(modalityId: Int, offset: Int): FavoriteBetPageDto?
    suspend fun delete(favoriteBetId: Long)
    suspend fun addToCart(favoriteBetId: Long, options: FavoriteBetCartOptions): CartDto?
    suspend fun validate(draft: FavoriteBetDraft)
    suspend fun save(draft: FavoriteBetDraft)
}

class RetrofitFavoriteBetRemoteDataSource(
    private val provider: LegacyBackendApiProvider,
) : FavoriteBetRemoteDataSource {
    override suspend fun grouped() = provider.current().groupedFavoriteBets().payload

    override suspend fun byModality(modalityId: Int, offset: Int) =
        provider.current().favoriteBetsByModality(
            mapOf("modalidade" to modalityId.toString(), "offset" to offset.toString()),
        ).payload

    override suspend fun delete(favoriteBetId: Long) {
        provider.current().deleteFavoriteBet(favoriteBetId)
    }

    override suspend fun addToCart(favoriteBetId: Long, options: FavoriteBetCartOptions) =
        provider.current().addFavoriteBetToCart(
            favoriteBetId = favoriteBetId,
            contestType = options.contestType,
            recurringDraws = options.recurringDraws,
            mirror = options.mirror,
        ).payload

    override suspend fun validate(draft: FavoriteBetDraft) {
        provider.current().validateFavoriteBet(draft.toJson())
    }

    override suspend fun save(draft: FavoriteBetDraft) {
        provider.current().saveFavoriteBet(draft.toJson())
    }

    private fun FavoriteBetDraft.toJson() = JsonObject().apply {
        add("modalidade", JsonObject().apply {
            addProperty("valor", modalityId)
            addProperty("descricao", modalityName)
        })
        addProperty("nome", name.take(25))
        add("numerosSelecionados", if (numberColumns.size == 1) {
            JsonArray().apply { numberColumns.first().forEach(::add) }
        } else {
            JsonArray().apply {
                numberColumns.forEach { row -> add(JsonArray().apply { row.forEach(::add) }) }
            }
        })
        if (clovers.isNotEmpty()) {
            add("trevos", JsonObject().apply {
                add("trevosSelecionados", JsonArray().apply { clovers.forEach(::add) })
            })
        }
        favoriteTeam?.let { team ->
            add("timeDoCoracao", JsonObject().apply {
                addProperty("indicadorSelecao", team.selectionIndicator)
                addProperty("nome", team.name)
                addProperty("numero", team.number)
                addProperty("numeroPais", team.countryNumber)
                addProperty("descricaoCurta", team.shortDescription)
                addProperty("descricaoLonga", team.longDescription)
                addProperty("pais", team.country)
                addProperty("siglaPais", team.countryCode)
                addProperty("uf", team.state)
                addProperty("nomeClass", team.className)
            })
        }
        luckyMonth?.let { month ->
            add("mesDeSorte", JsonObject().apply {
                addProperty("numero", month.number)
                addProperty("nome", month.name)
                addProperty("abreviacao", month.abbreviation)
            })
        }
    }
}

class RemoteFavoriteBetRepository(
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

    private fun FavoriteBetDto.toDomain(): FavoriteBet {
        val rows = selectedNumbers.numberRows()
        return FavoriteBet(
            id = id ?: 0,
            modalityId = modality?.value ?: 0,
            modalityName = modality?.description.orEmpty(),
            name = name.orEmpty().ifBlank { "Aposta favorita" },
            numberColumns = rows,
            heartTeam = heartTeam?.name.orEmpty().let { name ->
                heartTeam?.state?.takeIf(String::isNotBlank)?.let { "$name/$it" } ?: name
            },
            luckyMonth = luckyMonth?.name.orEmpty(),
            clovers = clovers?.selected.flattenNumbers(),
        )
    }

    private fun JsonElement?.numberRows(): List<List<Int>> = when {
        this == null || isJsonNull -> emptyList()
        isJsonPrimitive && asJsonPrimitive.isNumber -> listOf(listOf(asInt))
        isJsonArray && asJsonArray.any { it.isJsonArray } -> asJsonArray.map { it.flattenNumbers() }
        isJsonArray -> listOf(flattenNumbers())
        else -> emptyList()
    }

    private fun JsonElement?.flattenNumbers(): List<Int> = when {
        this == null || isJsonNull -> emptyList()
        isJsonArray -> asJsonArray.flatMap { it.flattenNumbers() }
        isJsonPrimitive && asJsonPrimitive.isNumber -> listOf(asInt)
        else -> emptyList()
    }
}
