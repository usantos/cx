package br.gov.caixa.loterias.core.network

import com.google.gson.JsonElement
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * Retrofit contract for legacy operations that have not yet gained a feature-specific DTO.
 *
 * Keeping these operations explicit is intentional: the migration gate can compare this
 * interface with the immutable legacy inventory and fail when an old backend operation is
 * accidentally omitted. Feature repositories should migrate methods from this compatibility
 * contract to typed domain models as each screen is completed.
 */
interface LegacyBackendApi {
    @GET("publico/equipes-esportivas/listaEquipes")
    suspend fun sportsTeams(): ApiEnvelope<List<SportsTeamShieldDto>>

    @POST("logado/carrinhos/gerar-cobranca-pix")
    suspend fun generatePixCharge(@Body body: JsonElement): ApiEnvelope<JsonElement>

    @GET("logado/apostas-favoritas/lista-unica")
    suspend fun favoriteBets(@QueryMap filters: Map<String, String>): ApiEnvelope<FavoriteBetPageDto>

    @GET("logado/apostas-favoritas/modalidade")
    suspend fun favoriteBetsByModality(@QueryMap filters: Map<String, String>): ApiEnvelope<FavoriteBetPageDto>

    @GET("logado/apostas-favoritas/")
    suspend fun groupedFavoriteBets(): ApiEnvelope<GroupedFavoriteBetsDto>

    @DELETE("logado/apostas-favoritas/{id}")
    suspend fun deleteFavoriteBet(@Path("id") id: Long): ApiEnvelope<JsonElement>

    @POST("logado/carrinhos-favoritos/validar")
    suspend fun validateFavoriteCartName(@Body body: JsonElement): ApiEnvelope<JsonElement>

    @POST("logado/compras/{purchaseId}/salvar-carrinho-favorito")
    suspend fun savePurchaseAsFavoriteCart(
        @Path("purchaseId") purchaseId: Long,
        @Body body: JsonElement,
    ): ApiEnvelope<JsonElement>

    @POST("logado/carrinhos/salvar-carrinho-favorito")
    suspend fun saveCurrentCartAsFavorite(@Body body: JsonElement): ApiEnvelope<JsonElement>

    @POST("logado/carrinhos/incluir-surpresinha")
    suspend fun addSurpriseBets(@Body body: JsonElement): ApiEnvelope<CartDto>

    @POST("logado/carrinhos/registrar-apostas-async")
    suspend fun registerCartBetsAsync(@Body body: JsonElement): ApiEnvelope<JsonElement>

    @GET("logado/carrinhos/validar")
    suspend fun validateCart(): ApiEnvelope<JsonElement>

    @GET("logado/carrinhos/verifica-compra-processamento")
    suspend fun purchaseProcessing(@Query("idCompraCarrinho") cartPurchaseId: Long): ApiEnvelope<JsonElement>

    @POST("logado/carrinhos/incluir-aposta-favorita")
    suspend fun addFavoriteBetToCart(
        @Query("idApostaFavorita") favoriteBetId: Long,
        @Query("valorTipoConcurso") contestType: Int,
        @Query("qtdTeimosinhas") recurringDraws: Int,
        @Query("espelho") mirror: Boolean,
    ): ApiEnvelope<CartDto>

    @POST("logado/carrinhos/incluir-aposta-favorita/{favoriteBetId}/{contestType}/{recurringDraws}")
    suspend fun addFavoriteBetToCartLegacy(
        @Path("favoriteBetId") favoriteBetId: Long,
        @Path("contestType") contestType: Int,
        @Path("recurringDraws") recurringDraws: Int,
    ): ApiEnvelope<CartDto>

    @GET("apostas")
    suspend fun confirmedBets(@QueryMap filters: Map<String, String>): ApiEnvelope<JsonElement>

    @GET("historico-apostas")
    suspend fun betHistory(@QueryMap filters: Map<String, String>): ApiEnvelope<JsonElement>

    @POST("apostas/{id}/resgatar-premio")
    suspend fun redeemPrize(@Path("id") betId: Long, @Body body: JsonElement): PrizeRedemptionEnvelopeDto

    @GET("usuarios/gerar-token-cadastro-mercado-pago")
    suspend fun mercadoPagoRegistrationToken(@Query("email") email: String): MercadoPagoRegistrationEnvelopeDto

    @GET("usuarios/gerar-token-apostador-recarga-pay")
    suspend fun recargaPayCustomerToken(): ApiEnvelope<String>

    @GET("apostas/config-consulta")
    suspend fun confirmedBetsConfiguration(): ApiEnvelope<JsonElement>

    @GET("historico-apostas/{id}/comprovante-premio")
    suspend fun historicalPrizeReceipt(@Path("id") betId: Long): ApiEnvelope<JsonElement>

    @GET("apostas/{id}/app/gerar-codigo-resgate-loterica")
    suspend fun retailerRedemptionCode(@Path("id") betId: Long): ApiEnvelope<JsonElement>

    @GET("apostas/{id}/detalhes-premio")
    suspend fun prizeDetails(@Path("id") betId: Long): ApiEnvelope<JsonElement>

    @GET("apostas/{id}/comprovante-aposta-app-pdf")
    suspend fun betReceiptPdf(@Path("id") betId: Long): ResponseBody

    @GET("apostas/{id}/comprovante-premio-app-pdf")
    suspend fun prizeReceiptPdf(@Path("id") betId: Long): ResponseBody

    @GET("apostas/{id}/comprovante")
    suspend fun betReceipt(@Path("id") betId: Long): ApiEnvelope<JsonElement>

    @GET("apostas/{id}/comprovante-premio")
    suspend fun prizeReceipt(@Path("id") betId: Long): ApiEnvelope<JsonElement>

    @GET("apostas/{id}/conferir")
    suspend fun checkBet(@Path("id") betId: Long): ApiEnvelope<JsonElement>

    @POST("apostas-favoritas/")
    suspend fun saveFavoriteBetOnSilce(@Body body: JsonElement): ApiEnvelope<JsonElement>

    @POST("logado/apostas-favoritas/validar")
    suspend fun validateFavoriteBet(@Body body: JsonElement): ApiEnvelope<JsonElement>

    @POST("logado/apostas-favoritas")
    suspend fun saveFavoriteBet(@Body body: JsonElement): ApiEnvelope<JsonElement>

    @POST("carrinhos/incluir-apostas")
    suspend fun addMultipleBetsToCart(@Body body: JsonElement): ApiEnvelope<CartDto>

    @GET("logado/rapidoes")
    suspend fun rapidConfiguration(): ApiEnvelope<RapidConfigurationDto>

    @POST("logado/rapidoes")
    suspend fun saveRapidConfiguration(@Body body: RapidConfigurationDto): ApiEnvelope<RapidConfigurationDto>

    @POST("logado/rapidoes/gerar-apostas")
    suspend fun generateRapidBets(@Query("quantidade") quantity: Int): ApiEnvelope<CartDto>

    @GET("combos")
    suspend fun silceCombos(): ApiEnvelope<JsonElement>

    @GET("compras")
    suspend fun purchases(@QueryMap filters: Map<String, String>): ApiEnvelope<JsonElement>

    @GET("compras/{id}/apostas")
    suspend fun purchaseBets(@Path("id") purchaseId: Long): ApiEnvelope<JsonElement>

    @GET("compras/meses")
    suspend fun purchaseMonths(): ApiEnvelope<JsonElement>

    @GET("compras/situacoes-compra")
    suspend fun purchaseStatuses(@Query("meioPagamento") paymentMethodId: Long?): ApiEnvelope<JsonElement>

    @GET("resultados")
    suspend fun resultsRange(@QueryMap filters: Map<String, String>): ApiEnvelope<JsonElement>

    @POST("bilhetes/conferir-bilhete")
    suspend fun checkTicket(@Body body: JsonElement): ApiEnvelope<JsonElement>

    @POST("bilhetes/conferir-bilhete-qrcode")
    suspend fun checkTicketQrCode(@Body body: JsonElement): ApiEnvelope<JsonElement>

    @GET("boloes/consultar-loterica-favorita")
    suspend fun favoriteRetailers(): ApiEnvelope<JsonElement>

    @POST("apostas/{id}/consulta-dados-chave")
    suspend fun consultPixKey(@Path("id") betId: Long, @Body body: JsonElement): ApiEnvelope<JsonElement>

    @GET("apostas/{id}/cancelar-resgate-pix")
    suspend fun cancelPixRedemption(@Path("id") betId: Long): ApiEnvelope<JsonElement>

    @POST("boloes/incluir-loterica-favorita")
    suspend fun addFavoriteRetailer(@Query("nuCd") retailerId: Long): ApiEnvelope<JsonElement>

    @DELETE("boloes/excluir-loterica-favorita/{id}")
    suspend fun deleteFavoriteRetailer(@Path("id") retailerId: Long): ApiEnvelope<JsonElement>

    @GET("ufs")
    suspend fun federativeUnits(): ApiEnvelope<JsonElement>

    @GET("lotericas/nome-loterico")
    suspend fun retailersByNameOrCode(@Query("nomeCodigo") nameOrCode: String): ApiEnvelope<JsonElement>

    @GET("lotericas")
    suspend fun retailers(@QueryMap filters: Map<String, String>): ApiEnvelope<JsonElement>

    @GET("lotericas/buscas-habilitadas")
    suspend fun enabledRetailerSearches(): ApiEnvelope<JsonElement>

    @GET("lotericas/codigo-completo")
    suspend fun retailerByFullCode(@Query("codigoCompleto") fullCode: String): ApiEnvelope<JsonElement>

    @GET("lotericas/nome-fantasia")
    suspend fun retailersByFantasyName(@Query("nomeFantasia") fantasyName: String): ApiEnvelope<JsonElement>

    @GET("termos-de-uso/valida-termo-aceito")
    suspend fun validateAcceptedTerms(): ApiEnvelope<JsonElement>

    @GET("meios-pagamento/{id}")
    suspend fun paymentMethod(@Path("id") id: Long): ApiEnvelope<JsonElement>

    @GET("meios-pagamento")
    suspend fun paymentMethods(): ApiEnvelope<JsonElement>

    @GET("parametros/valida-represa")
    suspend fun validateDam(): ApiEnvelope<JsonElement>

    @GET("usuarios/cartoes")
    suspend fun cards(@Query("meioPagamento") paymentMethod: String): ApiEnvelope<JsonElement>

    @POST("usuarios/sair")
    suspend fun signOut(): ResponseBody

    @PUT("historico-notificacoes/{notificationId}/visualiza")
    suspend fun markNotificationRead(@Path("notificationId") notificationId: Long): ApiEnvelope<JsonElement>

    @DELETE("usuarios/cartoes/{cardId}")
    suspend fun deleteCard(
        @Path("cardId") cardId: String,
        @Query("meioPagamento") paymentMethod: String,
    ): ApiEnvelope<JsonElement>

    @GET("usuarios/verificar-apostador-bloqueado")
    suspend fun blockedUserStatus(): BlockedAccountVerificationEnvelopeDto

    @POST("push/registros")
    suspend fun registerPushDevice(@Body body: JsonElement): ApiEnvelope<JsonElement>
}

/** Runtime-selected service used by the legacy rollout for microservice/Nova API bet history. */
interface BetsHistoryApi {
    @GET("apostas")
    suspend fun confirmedBets(@QueryMap filters: Map<String, String>): ApiEnvelope<JsonElement>

    @GET("consultar")
    suspend fun novaApiConfirmedBets(@QueryMap filters: Map<String, String>): ApiEnvelope<JsonElement>

    @GET("{id}/recuperar")
    suspend fun recoverBet(@Path("id") betId: Long): ApiEnvelope<JsonElement>
}

interface MercadoPagoApi {
    @GET("v1/payment_methods/installments")
    suspend fun installments(
        @Query("bin") bin: String,
        @Query("public_key") publicKey: String,
    ): JsonElement

    @POST("v1/card_tokens")
    suspend fun createCardToken(
        @Query("public_key") publicKey: String,
        @Body body: JsonElement,
    ): JsonElement
}

interface RecargaPayApi {
    @PUT("cards")
    suspend fun createCard(
        @Header("Authorization") authorization: String,
        @Header("x-api-version") apiVersion: String = "20220405",
        @Body body: JsonElement,
    ): JsonElement

    @PATCH("cards/{customerCardToken}")
    suspend fun authorizeSavedCard(
        @Path("customerCardToken") customerCardToken: String,
        @Header("Authorization") authorization: String,
        @Header("x-api-version") apiVersion: String = "20220405",
        @Body body: JsonElement,
    ): JsonElement
}
