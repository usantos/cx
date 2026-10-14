package br.gov.caixa.loterias.core.network

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.Strictness
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import java.io.IOException
import java.io.StringReader
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import okhttp3.Interceptor
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming
import retrofit2.http.Tag

/** Both callbacks refer to one captured login, never to the currently available generic token. */
class AccountVerificationSession(
    private val boundToken: () -> String?,
    private val current: () -> Boolean,
) {
    internal fun token(): String = boundToken()?.takeIf { it.isNotBlank() }
        ?: throw AccountVerificationSessionExpiredException()

    internal fun requireCurrent() {
        if (!current()) throw AccountVerificationSessionExpiredException()
    }

    override fun toString() = "AccountVerificationSession(redacted)"
}

class AccountVerificationSessionExpiredException : IOException("Sessão da verificação de cadastro encerrada")

internal class AccountVerificationSessionInterceptor(private val required: Boolean = true) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        val session = request.tag(AccountVerificationSession::class.java)
        if (session == null) {
            if (required) throw AccountVerificationSessionExpiredException()
            return chain.proceed(request)
        }
        session.requireCurrent()
        // token() must read token and lease atomically (SessionTokenStore.tokenFor in the host).
        // Overwrite any caller header; never fall back to BearerTokenInterceptor's current login.
        val authenticated = request.newBuilder().header("Authorization", "Bearer ${session.token()}").build()
        session.requireCurrent()
        val response = chain.proceed(authenticated)
        try {
            session.requireCurrent()
        } catch (failure: AccountVerificationSessionExpiredException) {
            response.close()
            throw failure
        }
        return response
    }
}

data class AccountVerificationEnvelopeDto(
    @SerializedName("codigo") val code: String? = null,
    @SerializedName("mensagem") val message: String? = null,
    @SerializedName("tipo") val type: Int? = null,
    @SerializedName("redirect") val redirect: String? = null,
    @SerializedName("errosValidacao") val validationErrors: List<String>? = null,
    @SerializedName("payload") val payload: AccountSessionDto? = null,
) {
    override fun toString() = "AccountVerificationEnvelopeDto(redacted)"
}

class AccountVerificationHttpException(val statusCode: Int, val envelope: AccountVerificationEnvelopeDto?) :
    IOException("Falha HTTP na verificação de cadastro ($statusCode)")

interface AccountVerificationBackendApi {
    @Streaming
    @POST("logado/usuarios/verificar-cadastro")
    suspend fun verify(
        @Body request: AccountVerificationRequestDto,
        @Tag session: AccountVerificationSession,
    ): Response<ResponseBody>
}

/** A response is not a cart acknowledgement: the post-login coordinator must validate its meaning. */
class AccountVerificationBackendClient(private val api: AccountVerificationBackendApi, private val gson: Gson) {
    suspend fun verify(request: AccountVerificationRequestDto, session: AccountVerificationSession): AccountVerificationEnvelopeDto {
        currentCoroutineContext().ensureActive()
        session.requireCurrent()
        val response = api.verify(request, session)
        val body = if (response.isSuccessful) response.body() else response.errorBody()
        return body.use {
            currentCoroutineContext().ensureActive()
            session.requireCurrent()
            val envelope = body?.let(::decode)
            currentCoroutineContext().ensureActive()
            session.requireCurrent()
            if (response.code() != 200) throw AccountVerificationHttpException(response.code(), envelope)
            envelope ?: throw IOException("Resposta de verificação de cadastro inválida")
        }
    }

    private fun decode(body: ResponseBody): AccountVerificationEnvelopeDto? {
        val source = body.source()
        source.request(MAX_ENVELOPE_BYTES + 1)
        if (source.buffer.size > MAX_ENVELOPE_BYTES) return null
        return try {
            JsonReader(StringReader(source.buffer.readUtf8())).use { reader ->
                reader.strictness = Strictness.STRICT
                val envelope = gson.fromJson<AccountVerificationEnvelopeDto>(reader, AccountVerificationEnvelopeDto::class.java)
                if (reader.peek() != JsonToken.END_DOCUMENT) null else envelope
            }
        } catch (_: JsonParseException) {
            null
        } catch (_: IOException) {
            null
        }
    }

    private companion object { const val MAX_ENVELOPE_BYTES = 1_048_576L }
}

/** Configured and rotated together with the pinned backend clients; no general-client fallback. */
class AccountVerificationBackendProvider {
    @Volatile private var client: AccountVerificationBackendClient? = null
    fun current(): AccountVerificationBackendClient = checkNotNull(client) { "API de verificação de cadastro ainda não foi configurada" }
    fun replace(value: AccountVerificationBackendClient) { client = value }
}
