package br.gov.caixa.loterias.core.network

import com.google.gson.JsonElement
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Tag
import retrofit2.http.Path

interface LoteriasApi {
    @GET("app/valida-versao/{version}")
    suspend fun isVersionSupported(@Path("version") version: String): ApiEnvelope<Boolean>

    @GET("parametros/configuraveis-app")
    suspend fun appParameters(): ApiEnvelope<AppParametersMapDto>

    @GET("publico/modalidades")
    suspend fun lotteryModalities(): ApiEnvelope<List<LotteryModalityDto>>

    @GET("publico/parametros-simulacao")
    suspend fun bettingParameters(): ApiEnvelope<BettingParametersDto>

    @GET("logado/combos")
    suspend fun betCombos(): ApiEnvelope<List<BetComboDto>>

    @POST("logado/carrinhos/incluir-aposta")
    suspend fun addBetToCart(@Body request: AddBetRequestDto): ApiEnvelope<CartDto>

    @POST("logado/carrinhos/incluir-aposta")
    suspend fun addPoolShareToCart(@Body request: AddPoolShareRequestDto): ApiEnvelope<CartDto>

    @POST("logado/carrinhos/incluir-combo")
    suspend fun addComboToCart(@Body request: AddComboRequestDto): ApiEnvelope<CartDto>

    @GET("duvidas/secoes")
    suspend fun supportSections(): ApiEnvelope<List<SupportSectionDto>>

    @GET("duvidas")
    suspend fun supportQuestions(@Query("secao") sectionId: Long): ApiEnvelope<List<SupportSectionDto>>

    @GET("termos-de-uso/termo-vigente")
    suspend fun currentTerms(): ApiEnvelope<TermsDto>

    @POST("termos-de-uso/aceitar-termo-vigente")
    suspend fun acceptCurrentTerms(): ApiEnvelope<Unit>

    @GET("boloes/recuperar-boloes-disponiveis")
    suspend fun marketplacePools(@QueryMap filters: Map<String, String>): ApiEnvelope<MarketplacePageDto>

    @GET("boloes/detalhar-bolao")
    suspend fun marketplacePoolDetail(@Query("idBolao") poolCode: String): ApiEnvelope<MarketplacePoolDetailDto>

    @GET("resultados/{modalidade}")
    suspend fun latestResult(@Path("modalidade") modality: String): ApiEnvelope<DrawResultDto>

    @GET("resultados/{modalidade}/{concurso}")
    suspend fun resultByContest(
        @Path("modalidade") modality: String,
        @Path("concurso") contest: Int,
    ): ApiEnvelope<DrawResultDto>

    @GET("autoavaliacoes/recuperar-perguntas")
    suspend fun selfAssessmentQuestions(): ApiEnvelope<SelfAssessmentPayloadDto>

    @POST("autoavaliacoes/grava-resposta")
    suspend fun submitSelfAssessment(
        @Body answers: List<SelfAssessmentAnswerDto>,
    ): ApiEnvelope<SelfAssessmentPayloadDto>

    @GET("repassometro")
    suspend fun socialTransfers(): ApiEnvelope<SocialTransfersDto>

    @GET("usuarios/recuperar-dados")
    suspend fun currentUserProfile(): ApiEnvelope<UserProfileDto>

    @GET("municipios")
    suspend fun municipalities(@Query("idUF") stateId: Long): ApiEnvelope<List<MunicipalityDto>>

    @GET("bairros")
    suspend fun neighborhoodsByZipCode(@Query("cep") zipCode: String): ApiEnvelope<List<NeighborhoodDto>>

    @PUT("usuarios")
    suspend fun updatePersonalData(@Body request: PersonalDataUpdateDto): ApiEnvelope<Unit>

    @POST("usuarios")
    suspend fun registerAccount(@Body request: AccountRegistrationDto): ApiEnvelope<UserProfileDto>

    @POST("logado/usuarios/verificar-cadastro")
    suspend fun verifyAccount(
        @Body request: AccountVerificationRequestDto = AccountVerificationRequestDto(),
    ): ApiEnvelope<AccountSessionDto>

    @POST("usuarios/recalcular-limite-autorizado")
    suspend fun recalculateAuthorizedLimit(): ApiEnvelope<UserProfileDto>

    @GET("logado/carrinhos")
    suspend fun currentCart(): ApiEnvelope<CartDto>

    @DELETE("logado/carrinhos/apostas/{id}")
    suspend fun removeCartBet(@Path("id") id: Long): ApiEnvelope<CartDto>

    @DELETE("logado/carrinhos/apostas/{id}")
    suspend fun removeBoundCartBet(
        @Path("id") id: Long,
        @Tag session: AccountVerificationSession,
    ): ApiEnvelope<CartDto>

    @DELETE("logado/carrinhos/combos/{id}")
    suspend fun removeCartCombo(@Path("id") id: Long): ApiEnvelope<CartDto>

    @POST("logado/carrinhos/limpar")
    suspend fun clearCart(): ApiEnvelope<CartDto>

    @GET("logado/carrinhos/validar")
    suspend fun validateCart(): ApiEnvelope<JsonElement>

    @POST("logado/carrinhos-favoritos/validar")
    suspend fun validateFavoriteCartName(
        @Body request: SaveFavoriteCartRequestDto,
    ): ApiEnvelope<FavoriteCartDto>

    @POST("logado/carrinhos/salvar-carrinho-favorito")
    suspend fun saveCurrentCartFavorite(
        @Body request: SaveFavoriteCartRequestDto,
    ): ApiEnvelope<FavoriteCartDto>

    @GET("logado/carrinhos-favoritos")
    suspend fun favoriteCarts(): ApiEnvelope<List<FavoriteCartDto>>

    @GET("logado/carrinhos-favoritos/{cartId}/apostas")
    suspend fun favoriteCartBets(
        @Path("cartId") cartId: Long,
    ): ApiEnvelope<List<FavoriteCartBetDto>>

    @DELETE("logado/carrinhos-favoritos/{cartId}")
    suspend fun deleteFavoriteCart(
        @Path("cartId") cartId: Long,
    ): ApiEnvelope<List<FavoriteCartDto>>

    @DELETE("logado/carrinhos-favoritos/{cartId}/apostas/{betId}")
    suspend fun deleteFavoriteCartBet(
        @Path("cartId") cartId: Long,
        @Path("betId") betId: Long,
    ): ApiEnvelope<List<FavoriteCartBetDto>>

    @POST("logado/carrinhos/incluir-carrinho-favorito/{cartId}")
    suspend fun addFavoriteCartToCurrent(
        @Path("cartId") cartId: Long,
        @Body request: AddFavoriteCartRequestDto,
    ): ApiEnvelope<CartDto>

    @POST("usuarios/suspender-conta-apostador")
    suspend fun suspendAccount(
        @Body request: SuspendAccountRequestDto,
    ): ApiEnvelope<AccountSuspensionDto>
}
