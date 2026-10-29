package br.gov.caixa.loterias.feature.betting.data.datasource.remote

import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.feature.betting.data.dto.FavoriteBetPageDto
import br.gov.caixa.loterias.feature.betting.data.dto.GroupedFavoriteBetsDto
import br.gov.caixa.loterias.feature.betting.data.dto.RapidConfigurationDto
import br.gov.caixa.loterias.feature.betting.data.dto.SportsTeamShieldDto
import com.google.gson.JsonElement
import retrofit2.http.*

/** Contrato HTTP pertencente a esta funcionalidade; anotações de transporte e valores padrão originais são preservados. */
interface BettingBackendApi {
    @GET("publico/equipes-esportivas/listaEquipes")
    suspend fun sportsTeams(): ApiEnvelope<List<SportsTeamShieldDto>>

    @GET("logado/apostas-favoritas/modalidade")
    suspend fun favoriteBetsByModality(@QueryMap filters: Map<String, String>): ApiEnvelope<FavoriteBetPageDto>

    @GET("logado/apostas-favoritas/")
    suspend fun groupedFavoriteBets(): ApiEnvelope<GroupedFavoriteBetsDto>

    @DELETE("logado/apostas-favoritas/{id}")
    suspend fun deleteFavoriteBet(@Path("id") id: Long): ApiEnvelope<JsonElement>

    @POST("logado/carrinhos/incluir-aposta-favorita")
    suspend fun addFavoriteBetToCart(
        @Query("idApostaFavorita") favoriteBetId: Long,
        @Query("valorTipoConcurso") contestType: Int,
        @Query("qtdTeimosinhas") recurringDraws: Int,
        @Query("espelho") mirror: Boolean?,
    ): ApiEnvelope<CartDto>

    @POST("logado/apostas-favoritas/validar")
    suspend fun validateFavoriteBet(@Body body: JsonElement): Unit

    @POST("logado/apostas-favoritas")
    suspend fun saveFavoriteBet(@Body body: JsonElement): Unit

    @POST("logado/carrinhos/incluir-surpresinha")
    suspend fun addSurpriseBets(@Body body: JsonElement): ApiEnvelope<CartDto>

    @GET("logado/rapidoes")
    suspend fun rapidConfiguration(): ApiEnvelope<RapidConfigurationDto>

    @POST("logado/rapidoes")
    suspend fun saveRapidConfiguration(@Body body: RapidConfigurationDto): ApiEnvelope<RapidConfigurationDto>

    @POST("logado/rapidoes/gerar-apostas")
    suspend fun generateRapidBets(@Query("quantidade") quantity: Int): ApiEnvelope<CartDto>
}

/** O receptor é o retrato do cliente já selecionado para esta operação. */
internal fun BackendApi.featureBettingApi(): BettingBackendApi = BettingBackendApiProjection(this)

private class BettingBackendApiProjection(private val transport: BackendApi) : BettingBackendApi {
    override suspend fun addSurpriseBets(@Body body: JsonElement): ApiEnvelope<CartDto> = transport.addSurpriseBets(body)

    override suspend fun sportsTeams(): ApiEnvelope<List<SportsTeamShieldDto>> = transport.sportsTeams()

    override suspend fun favoriteBetsByModality(@QueryMap filters: Map<String, String>): ApiEnvelope<FavoriteBetPageDto> = transport.favoriteBetsByModality(filters = filters)

    override suspend fun groupedFavoriteBets(): ApiEnvelope<GroupedFavoriteBetsDto> = transport.groupedFavoriteBets()

    override suspend fun deleteFavoriteBet(@Path("id") id: Long): ApiEnvelope<JsonElement> = transport.deleteFavoriteBet(id = id)

    override suspend fun addFavoriteBetToCart(
        @Query("idApostaFavorita") favoriteBetId: Long,
        @Query("valorTipoConcurso") contestType: Int,
        @Query("qtdTeimosinhas") recurringDraws: Int,
        @Query("espelho") mirror: Boolean?,
    ): ApiEnvelope<CartDto> = transport.addFavoriteBetToCart(favoriteBetId = favoriteBetId, contestType = contestType, recurringDraws = recurringDraws, mirror = mirror)

    override suspend fun validateFavoriteBet(@Body body: JsonElement): Unit = transport.validateFavoriteBet(body = body)

    override suspend fun saveFavoriteBet(@Body body: JsonElement): Unit = transport.saveFavoriteBet(body = body)

    override suspend fun rapidConfiguration(): ApiEnvelope<RapidConfigurationDto> = transport.rapidConfiguration()

    override suspend fun saveRapidConfiguration(@Body body: RapidConfigurationDto): ApiEnvelope<RapidConfigurationDto> = transport.saveRapidConfiguration(body = body)

    override suspend fun generateRapidBets(@Query("quantidade") quantity: Int): ApiEnvelope<CartDto> = transport.generateRapidBets(quantity = quantity)
}
