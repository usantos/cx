package br.gov.caixa.loterias.feature.betting.data.datasource.remote

import retrofit2.HttpException
import br.gov.caixa.loterias.core.network.error.toBackendHttpFailure
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetCartOptions
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetDraft
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import com.google.gson.JsonObject
import com.google.gson.JsonArray

class FavoriteBetRemoteDataSourceImpl(
    private val provider: BackendApiProvider,
) : FavoriteBetRemoteDataSource {
    override suspend fun grouped() = provider.current().featureBettingApi().groupedFavoriteBets().payload

    override suspend fun byModality(modalityId: Int, offset: Int) =
        provider.current().featureBettingApi().favoriteBetsByModality(
            mapOf("modalidade" to modalityId.toString(), "offset" to offset.toString(), "size" to "8"),
        ).payload

    override suspend fun delete(favoriteBetId: Long) {
        provider.current().featureBettingApi().deleteFavoriteBet(favoriteBetId)
    }

    override suspend fun addToCart(favoriteBetId: Long, options: FavoriteBetCartOptions) =
        provider.current().featureBettingApi().addFavoriteBetToCart(
            favoriteBetId = favoriteBetId,
            contestType = options.contestType,
            recurringDraws = options.recurringDraws,
            mirror = options.mirror,
        ).payload

    override suspend fun validate(draft: FavoriteBetDraft) {
        try {
            provider.current().featureBettingApi().validateFavoriteBet(draft.toJson())
        } catch (failure: HttpException) {
            val backend = failure.toBackendHttpFailure()
            val message = backend.metadata?.message?.takeIf(String::isNotBlank)
            if (backend.statusCode == 400 && message != null) throw IllegalArgumentException(message)
            throw backend
        }
    }

    override suspend fun save(draft: FavoriteBetDraft) {
        try {
            provider.current().featureBettingApi().saveFavoriteBet(draft.toJson())
        } catch (failure: HttpException) {
            val backend = failure.toBackendHttpFailure()
            val message = backend.metadata?.message?.takeIf(String::isNotBlank)
            if (backend.statusCode == 400 && message != null) throw IllegalArgumentException(message)
            throw backend
        }
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
