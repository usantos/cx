package br.gov.caixa.loterias.feature.tickets.data.datasource.remote

import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.core.network.api.BackendApi
import com.google.gson.JsonElement
import retrofit2.http.*

/** Contrato HTTP pertencente a esta funcionalidade; anotações de transporte e valores padrão originais são preservados. */
interface TicketsBackendApi {
    @POST("logado/compras/{purchaseId}/salvar-carrinho-favorito")
    suspend fun savePurchaseAsFavoriteCart(
        @Path("purchaseId") purchaseId: Long,
        @Body body: JsonElement,
    ): ApiEnvelope<JsonElement>

    @GET("apostas")
    suspend fun confirmedBets(@QueryMap filters: Map<String, String>): ApiEnvelope<JsonElement>

    @GET("apostas/config-consulta")
    suspend fun confirmedBetsConfiguration(): ApiEnvelope<JsonElement>

    @GET("historico-apostas/{id}/comprovante-premio")
    suspend fun historicalPrizeReceipt(@Path("id") betId: Long): ApiEnvelope<JsonElement>

    @GET("apostas/{id}/detalhes-premio")
    suspend fun prizeDetails(@Path("id") betId: Long): ApiEnvelope<JsonElement>

    @GET("apostas/{id}/comprovante")
    suspend fun betReceipt(@Path("id") betId: Long): ApiEnvelope<JsonElement>

    @GET("apostas/{id}/comprovante-premio")
    suspend fun prizeReceipt(@Path("id") betId: Long): ApiEnvelope<JsonElement>

    @GET("apostas/{id}/conferir")
    suspend fun checkBet(@Path("id") betId: Long): ApiEnvelope<JsonElement>

    @GET("compras")
    suspend fun purchases(@QueryMap filters: Map<String, String>): ApiEnvelope<JsonElement>

    @GET("compras/{id}/apostas")
    suspend fun purchaseBets(@Path("id") purchaseId: Long): ApiEnvelope<JsonElement>

    @GET("compras/meses")
    suspend fun purchaseMonths(): ApiEnvelope<JsonElement>

    @GET("compras/situacoes-compra")
    suspend fun purchaseStatuses(@Query("meioPagamento") paymentMethodId: Long?): ApiEnvelope<JsonElement>

    @POST("bilhetes/conferir-bilhete")
    suspend fun checkTicket(@Body body: JsonElement): ApiEnvelope<JsonElement>

    @POST("bilhetes/conferir-bilhete-qrcode")
    suspend fun checkTicketQrCode(@Body body: JsonElement): ApiEnvelope<JsonElement>

    @GET("meios-pagamento")
    suspend fun paymentMethods(): ApiEnvelope<JsonElement>
}

/** O receptor é o retrato do cliente já selecionado para esta operação. */
internal fun BackendApi.featureTicketsApi(): TicketsBackendApi = TicketsBackendApiProjection(this)

private class TicketsBackendApiProjection(private val transport: BackendApi) : TicketsBackendApi {
    override suspend fun savePurchaseAsFavoriteCart(
        @Path("purchaseId") purchaseId: Long,
        @Body body: JsonElement,
    ): ApiEnvelope<JsonElement> = transport.savePurchaseAsFavoriteCart(purchaseId = purchaseId, body = body)

    override suspend fun confirmedBets(@QueryMap filters: Map<String, String>): ApiEnvelope<JsonElement> = transport.confirmedBets(filters = filters)

    override suspend fun confirmedBetsConfiguration(): ApiEnvelope<JsonElement> = transport.confirmedBetsConfiguration()

    override suspend fun historicalPrizeReceipt(@Path("id") betId: Long): ApiEnvelope<JsonElement> = transport.historicalPrizeReceipt(betId = betId)

    override suspend fun prizeDetails(@Path("id") betId: Long): ApiEnvelope<JsonElement> = transport.prizeDetails(betId = betId)

    override suspend fun betReceipt(@Path("id") betId: Long): ApiEnvelope<JsonElement> = transport.betReceipt(betId = betId)

    override suspend fun prizeReceipt(@Path("id") betId: Long): ApiEnvelope<JsonElement> = transport.prizeReceipt(betId = betId)

    override suspend fun checkBet(@Path("id") betId: Long): ApiEnvelope<JsonElement> = transport.checkBet(betId = betId)

    override suspend fun purchases(@QueryMap filters: Map<String, String>): ApiEnvelope<JsonElement> = transport.purchases(filters = filters)

    override suspend fun purchaseBets(@Path("id") purchaseId: Long): ApiEnvelope<JsonElement> = transport.purchaseBets(purchaseId = purchaseId)

    override suspend fun purchaseMonths(): ApiEnvelope<JsonElement> = transport.purchaseMonths()

    override suspend fun purchaseStatuses(@Query("meioPagamento") paymentMethodId: Long?): ApiEnvelope<JsonElement> = transport.purchaseStatuses(paymentMethodId = paymentMethodId)

    override suspend fun checkTicket(@Body body: JsonElement): ApiEnvelope<JsonElement> = transport.checkTicket(body = body)

    override suspend fun checkTicketQrCode(@Body body: JsonElement): ApiEnvelope<JsonElement> = transport.checkTicketQrCode(body = body)

    override suspend fun paymentMethods(): ApiEnvelope<JsonElement> = transport.paymentMethods()
}
