package br.gov.caixa.loterias.feature.payment.data.datasource.remote

import br.gov.caixa.loterias.core.network.api.RecargaPayApi
import com.google.gson.JsonElement
import retrofit2.http.*

/** Contrato HTTP pertencente a esta funcionalidade; anotações de transporte e valores padrão originais são preservados. */
interface PaymentRecargaPayApi {
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

/** O receptor é o retrato do cliente já selecionado para esta operação. */
internal fun RecargaPayApi.featurePaymentApi(): PaymentRecargaPayApi = PaymentRecargaPayApiProjection(this)

private class PaymentRecargaPayApiProjection(private val transport: RecargaPayApi) : PaymentRecargaPayApi {
    override suspend fun createCard(
        @Header("Authorization") authorization: String,
        @Header("x-api-version") apiVersion: String,
        @Body body: JsonElement,
    ): JsonElement = transport.createCard(authorization = authorization, apiVersion = apiVersion, body = body)

    override suspend fun authorizeSavedCard(
        @Path("customerCardToken") customerCardToken: String,
        @Header("Authorization") authorization: String,
        @Header("x-api-version") apiVersion: String,
        @Body body: JsonElement,
    ): JsonElement = transport.authorizeSavedCard(customerCardToken = customerCardToken, authorization = authorization, apiVersion = apiVersion, body = body)
}
