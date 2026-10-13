package br.gov.caixa.loterias.core.network

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.annotations.SerializedName
import retrofit2.HttpException

/** Business metadata only; never retains a request, raw error body or exception cause. */
data class BackendErrorMetadata(
    @SerializedName("codigo") val code: String? = null,
    @SerializedName("redirect") val redirect: String? = null,
    @SerializedName("mensagem") val message: String? = null,
    @SerializedName("tipo") val type: String? = null,
) {
    override fun toString(): String = "BackendErrorMetadata(redacted)"
}

class BackendHttpFailure(val statusCode: Int, val metadata: BackendErrorMetadata?) :
    Exception("Falha HTTP ($statusCode)")

/** Bounds parsing and closes the body. I/O failures and cancellation are not swallowed. */
fun HttpException.toBackendHttpFailure(): BackendHttpFailure {
    val metadata = response()?.errorBody()?.use { body ->
        val source = body.source()
        source.request(65_537)
        if (source.buffer.size > 65_536) null
        else try {
            Gson().fromJson(source.buffer.readUtf8(), BackendErrorMetadata::class.java)
        } catch (_: JsonParseException) {
            null
        }
    }
    return BackendHttpFailure(code(), metadata)
}
