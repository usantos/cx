package br.gov.caixa.loterias.feature.retail.data.datasource.remote

import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.feature.retail.data.dto.MercadoPagoRegistrationEnvelopeDto
import br.gov.caixa.loterias.feature.retail.data.dto.PrizeRedemptionEnvelopeDto
import com.google.gson.JsonElement
import okhttp3.ResponseBody
import retrofit2.http.*

/** Contrato HTTP pertencente a esta funcionalidade; anotações de transporte e valores padrão originais são preservados. */
interface RetailBackendApi {
    @POST("apostas/{id}/resgatar-premio")
    suspend fun redeemPrize(@Path("id") betId: Long, @Body body: JsonElement): PrizeRedemptionEnvelopeDto

    @GET("usuarios/gerar-token-cadastro-mercado-pago")
    suspend fun mercadoPagoRegistrationToken(@Query("email") email: String): MercadoPagoRegistrationEnvelopeDto

    @GET("apostas/{id}/app/gerar-codigo-resgate-loterica")
    suspend fun retailerRedemptionCode(@Path("id") betId: Long): ApiEnvelope<JsonElement>

    @GET("apostas/{id}/detalhes-premio")
    suspend fun prizeDetails(@Path("id") betId: Long): ApiEnvelope<JsonElement>

    @retrofit2.http.Headers("Disable-Crypto: true")
    @GET("apostas/{id}/comprovante-aposta-app-pdf")
    suspend fun betReceiptPdf(@Path("id") betId: Long): ResponseBody

    @retrofit2.http.Headers("Disable-Crypto: true")
    @GET("apostas/{id}/comprovante-premio-app-pdf")
    suspend fun prizeReceiptPdf(@Path("id") betId: Long): ResponseBody

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

    @GET("lotericas/nome-loterico")
    suspend fun retailersByNameOrCode(@Query("nomeCodigo") nameOrCode: String): ApiEnvelope<JsonElement>
}

/** O receptor é o retrato do cliente já selecionado para esta operação. */
internal fun BackendApi.featureRetailApi(): RetailBackendApi = RetailBackendApiProjection(this)

private class RetailBackendApiProjection(private val transport: BackendApi) : RetailBackendApi {
    override suspend fun redeemPrize(@Path("id") betId: Long, @Body body: JsonElement): PrizeRedemptionEnvelopeDto = transport.redeemPrize(betId = betId, body = body)

    override suspend fun mercadoPagoRegistrationToken(@Query("email") email: String): MercadoPagoRegistrationEnvelopeDto = transport.mercadoPagoRegistrationToken(email = email)

    override suspend fun retailerRedemptionCode(@Path("id") betId: Long): ApiEnvelope<JsonElement> = transport.retailerRedemptionCode(betId = betId)

    override suspend fun prizeDetails(@Path("id") betId: Long): ApiEnvelope<JsonElement> = transport.prizeDetails(betId = betId)

    override suspend fun betReceiptPdf(@Path("id") betId: Long): ResponseBody = transport.betReceiptPdf(betId = betId)

    override suspend fun prizeReceiptPdf(@Path("id") betId: Long): ResponseBody = transport.prizeReceiptPdf(betId = betId)

    override suspend fun favoriteRetailers(): ApiEnvelope<JsonElement> = transport.favoriteRetailers()

    override suspend fun consultPixKey(@Path("id") betId: Long, @Body body: JsonElement): ApiEnvelope<JsonElement> = transport.consultPixKey(betId = betId, body = body)

    override suspend fun cancelPixRedemption(@Path("id") betId: Long): ApiEnvelope<JsonElement> = transport.cancelPixRedemption(betId = betId)

    override suspend fun addFavoriteRetailer(@Query("nuCd") retailerId: Long): ApiEnvelope<JsonElement> = transport.addFavoriteRetailer(retailerId = retailerId)

    override suspend fun deleteFavoriteRetailer(@Path("id") retailerId: Long): ApiEnvelope<JsonElement> = transport.deleteFavoriteRetailer(retailerId = retailerId)

    override suspend fun retailersByNameOrCode(@Query("nomeCodigo") nameOrCode: String): ApiEnvelope<JsonElement> = transport.retailersByNameOrCode(nameOrCode = nameOrCode)
}
