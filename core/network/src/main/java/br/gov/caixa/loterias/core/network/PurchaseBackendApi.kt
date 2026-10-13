package br.gov.caixa.loterias.core.network

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.annotations.SerializedName
import java.io.IOException
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class PurchaseProcessingDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("periodicidade") val intervalMillis: Long? = null,
    @SerializedName("repeticoes") val repetitions: Int? = null,
    @SerializedName("idSituacaoCompra") val status: Int? = null,
)

data class PurchaseEnvelopeDto(
    @SerializedName("payload") val payload: PurchaseProcessingDto? = null,
    @SerializedName("codigo") val code: String? = null,
    @SerializedName("redirect") val redirect: String? = null,
    @SerializedName("mensagem") val message: String? = null,
    @SerializedName("dataHoraServidor") val serverDateTime: String? = null,
)

/** Error payloads need not have the processing DTO shape. Only business metadata is read. */
data class PurchaseErrorEnvelopeDto(
    @SerializedName("codigo") val code: String? = null,
    @SerializedName("redirect") val redirect: String? = null,
    @SerializedName("mensagem") val message: String? = null,
    @SerializedName("tipo") val type: String? = null,
)

/** No request, raw body, credential or server message is included in the exception text. */
class PurchaseHttpException(val statusCode: Int, val error: PurchaseErrorEnvelopeDto?) :
    Exception("Falha HTTP de compra ($statusCode)")

/** Transport-owned metadata: the server JSON cannot impersonate an HTTP 303 response. */
data class PurchaseBackendResponse(val envelope: PurchaseEnvelopeDto, val processing: Boolean)

interface PurchaseBackendApi {
    @POST("logado/carrinhos/registrar-apostas-async")
    suspend fun registerCartBetsAsync(@Body body: JsonElement): Response<PurchaseEnvelopeDto>

    @GET("logado/carrinhos/verifica-compra-processamento")
    suspend fun purchaseProcessing(@Query("idCompraCarrinho") purchaseId: Long): Response<PurchaseEnvelopeDto>
}

/** Reads the 303 envelope without reissuing a request, following a URL or discarding directives. */
class PurchaseBackendClient(private val api: PurchaseBackendApi, private val gson: Gson) {
    suspend fun submit(body: JsonElement): PurchaseBackendResponse = decode(api.registerCartBetsAsync(body))
    suspend fun progress(purchaseId: Long): PurchaseBackendResponse = decode(api.purchaseProcessing(purchaseId))

    private fun decode(response: Response<PurchaseEnvelopeDto>): PurchaseBackendResponse {
        if (response.isSuccessful) return PurchaseBackendResponse(
            response.body() ?: throw IOException("Resposta de compra vazia"), processing = false,
        )
        if (response.code() != 303) {
            val error = response.errorBody()?.use {
                val source = it.source()
                source.request(MAX_ENVELOPE_BYTES + 1)
                if (source.buffer.size > MAX_ENVELOPE_BYTES) null
                else try {
                    gson.fromJson(source.buffer.readUtf8(), PurchaseErrorEnvelopeDto::class.java)
                } catch (_: JsonParseException) {
                    null
                }
            }
            throw PurchaseHttpException(response.code(), error)
        }
        val body = response.errorBody() ?: throw IOException("Resposta de processamento vazia")
        return body.use {
            val source = it.source()
            source.request(MAX_ENVELOPE_BYTES + 1)
            if (source.buffer.size > MAX_ENVELOPE_BYTES) throw IOException("Resposta de processamento inválida")
            PurchaseBackendResponse(
                gson.fromJson(source.buffer.readUtf8(), PurchaseEnvelopeDto::class.java)
                    ?: throw IOException("Resposta de processamento vazia"),
                processing = true,
            )
        }
    }

    private companion object { const val MAX_ENVELOPE_BYTES = 65_536L }
}

/** Replaced together with the remaining clients when the verified pin set rotates. */
class PurchaseBackendApiProvider {
    @Volatile private var client: PurchaseBackendClient? = null
    fun current(): PurchaseBackendClient = checkNotNull(client) { "API de compras ainda não foi configurada" }
    fun replace(value: PurchaseBackendClient) { client = value }
}
