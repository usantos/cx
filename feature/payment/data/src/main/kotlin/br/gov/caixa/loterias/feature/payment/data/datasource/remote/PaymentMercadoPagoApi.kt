package br.gov.caixa.loterias.feature.payment.data.datasource.remote

import br.gov.caixa.loterias.core.network.api.MercadoPagoApi
import com.google.gson.JsonElement
import retrofit2.http.*

/** Contrato HTTP pertencente a esta funcionalidade; anotações de transporte e valores padrão originais são preservados. */
interface PaymentMercadoPagoApi {
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

/** O receptor é o retrato do cliente já selecionado para esta operação. */
internal fun MercadoPagoApi.featurePaymentApi(): PaymentMercadoPagoApi = PaymentMercadoPagoApiProjection(this)

private class PaymentMercadoPagoApiProjection(private val transport: MercadoPagoApi) : PaymentMercadoPagoApi {
    override suspend fun installments(
        @Query("bin") bin: String,
        @Query("public_key") publicKey: String,
    ): JsonElement = transport.installments(bin = bin, publicKey = publicKey)

    override suspend fun createCardToken(
        @Query("public_key") publicKey: String,
        @Body body: JsonElement,
    ): JsonElement = transport.createCardToken(publicKey = publicKey, body = body)
}
