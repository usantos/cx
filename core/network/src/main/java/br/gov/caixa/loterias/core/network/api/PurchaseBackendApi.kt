package br.gov.caixa.loterias.core.network.api

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.io.IOException
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import br.gov.caixa.loterias.feature.payment.data.dto.PurchaseErrorEnvelopeDto
import br.gov.caixa.loterias.feature.payment.data.dto.PurchaseEnvelopeDto

/** O texto da exceção não inclui requisição, corpo bruto, credenciais ou mensagem do servidor. */
class PurchaseHttpException(val statusCode: Int, val error: PurchaseErrorEnvelopeDto?) :
    Exception("Falha HTTP de compra ($statusCode)")

/** Metadados do transporte: o JSON do servidor não pode se passar por uma resposta HTTP 303. */
data class PurchaseBackendResponse(val envelope: PurchaseEnvelopeDto, val processing: Boolean)

interface PurchaseBackendApi {
    @POST("logado/carrinhos/registrar-apostas-async")
    suspend fun registerCartBetsAsync(@Body body: JsonElement): Response<PurchaseEnvelopeDto>

    @GET("logado/carrinhos/verifica-compra-processamento")
    suspend fun purchaseProcessing(@Query("idCompraCarrinho") purchaseId: Long): Response<PurchaseEnvelopeDto>
}

/** Lê o envelope 303 sem reenviar requisições, seguir URLs ou descartar diretivas. */
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

/** Substituído junto aos demais clientes quando o conjunto verificado de pins é rotacionado. */
class PurchaseBackendApiProvider {
    @Volatile private var client: PurchaseBackendClient? = null
    fun current(): PurchaseBackendClient = checkNotNull(client) { "API de compras ainda não foi configurada" }
    fun replace(value: PurchaseBackendClient) { client = value }
}
