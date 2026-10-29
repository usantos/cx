package br.gov.caixa.loterias.feature.payment.data.datasource.remote

import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.core.network.api.BackendApi
import com.google.gson.JsonElement
import retrofit2.http.*

/** Contrato HTTP pertencente a esta funcionalidade; anotações de transporte e valores padrão originais são preservados. */
interface PaymentBackendApi {
    @POST("logado/carrinhos/gerar-cobranca-pix")
    suspend fun generatePixCharge(@Body body: JsonElement): ApiEnvelope<JsonElement>

    @GET("usuarios/gerar-token-apostador-recarga-pay")
    suspend fun recargaPayCustomerToken(): ApiEnvelope<String>

    @GET("meios-pagamento/{id}")
    suspend fun paymentMethod(@Path("id") id: Long): ApiEnvelope<JsonElement>

    @GET("meios-pagamento")
    suspend fun paymentMethods(): ApiEnvelope<JsonElement>

    @GET("parametros/valida-represa")
    suspend fun validateDam(): ApiEnvelope<JsonElement>

    @GET("usuarios/cartoes")
    suspend fun cards(@Query("meioPagamento") paymentMethod: String): ApiEnvelope<JsonElement>

    @DELETE("usuarios/cartoes/{cardId}")
    suspend fun deleteCard(
        @Path("cardId") cardId: String,
        @Query("meioPagamento") paymentMethod: String,
    ): ApiEnvelope<JsonElement>
}

/** O receptor é o retrato do cliente já selecionado para esta operação. */
internal fun BackendApi.featurePaymentApi(): PaymentBackendApi = PaymentBackendApiProjection(this)

private class PaymentBackendApiProjection(private val transport: BackendApi) : PaymentBackendApi {
    override suspend fun generatePixCharge(@Body body: JsonElement): ApiEnvelope<JsonElement> = transport.generatePixCharge(body = body)

    override suspend fun recargaPayCustomerToken(): ApiEnvelope<String> = transport.recargaPayCustomerToken()

    override suspend fun paymentMethod(@Path("id") id: Long): ApiEnvelope<JsonElement> = transport.paymentMethod(id = id)

    override suspend fun paymentMethods(): ApiEnvelope<JsonElement> = transport.paymentMethods()

    override suspend fun validateDam(): ApiEnvelope<JsonElement> = transport.validateDam()

    override suspend fun cards(@Query("meioPagamento") paymentMethod: String): ApiEnvelope<JsonElement> = transport.cards(paymentMethod = paymentMethod)

    override suspend fun deleteCard(
        @Path("cardId") cardId: String,
        @Query("meioPagamento") paymentMethod: String,
    ): ApiEnvelope<JsonElement> = transport.deleteCard(cardId = cardId, paymentMethod = paymentMethod)
}
