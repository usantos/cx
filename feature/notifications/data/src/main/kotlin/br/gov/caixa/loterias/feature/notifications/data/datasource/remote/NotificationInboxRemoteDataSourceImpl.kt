package br.gov.caixa.loterias.feature.notifications.data.datasource.remote

import br.gov.caixa.loterias.core.network.api.NotificationInboxApiProvider
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.notifications.data.dto.NotificationInboxEnvelopeDto
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.Strictness
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import java.io.IOException
import java.io.StringReader
import okhttp3.ResponseBody
import retrofit2.Response

class NotificationInboxRemoteDataSourceImpl(
    private val provider: NotificationInboxApiProvider,
    private val sessions: NotificationInboxSessionBinding,
) : NotificationInboxRemoteDataSource {
    override suspend fun load(identity: AuthenticatedSessionIdentity): Pair<NotificationInboxEnvelopeDto, NotificationInboxEnvelopeDto> {
        val session = sessions.bind(identity)
        val api = provider.current()
        return decode(api.all(session)) to decode(api.unread(session))
    }

    override suspend fun markRead(identity: AuthenticatedSessionIdentity, id: Long) {
        require(id > 0)
        mutate(provider.current().markRead(id, sessions.bind(identity)))
    }

    override suspend fun remove(identity: AuthenticatedSessionIdentity, id: Long) {
        require(id > 0)
        mutate(provider.current().remove(id, sessions.bind(identity)))
    }

    override suspend fun clear(identity: AuthenticatedSessionIdentity) {
        mutate(provider.current().clear(sessions.bind(identity)))
    }

    private fun decode(response: Response<NotificationInboxEnvelopeDto>): NotificationInboxEnvelopeDto {
        if (response.code() != HTTP_OK) {
            response.errorBody()?.close()
            invalid()
        }
        return (response.body() ?: invalid()).also(::validate)
    }

    private fun mutate(response: Response<ResponseBody>) {
        val body = if (response.isSuccessful) response.body() else response.errorBody()
        body.use {
            if (response.code() != HTTP_OK && response.code() != HTTP_NO_CONTENT) invalid()
            val raw = body?.string()?.takeIf { it.isNotBlank() } ?: return
            val envelope = try {
                JsonReader(StringReader(raw)).use { reader ->
                    reader.strictness = Strictness.STRICT
                    val decoded = Gson().fromJson<NotificationInboxEnvelopeDto>(
                        reader,
                        NotificationInboxEnvelopeDto::class.java
                    )
                    if (reader.peek() != JsonToken.END_DOCUMENT) invalid()
                    decoded ?: invalid()
                }
            } catch (_: JsonParseException) {
                invalid()
            }
            validate(envelope)
        }
    }

    private fun validate(envelope: NotificationInboxEnvelopeDto) {
        val errors = envelope.errors
        if (envelope.code.present() || envelope.redirect.present() ||
            (errors.present() && !(errors!!.isJsonArray && errors.asJsonArray.size() == 0))
        ) {
            invalid()
        }
    }

    private fun JsonElement?.present() = this != null && !isJsonNull
    private fun invalid(): Nothing = throw IOException("Resposta do histórico de notificações inválida")
    private companion object {
        const val HTTP_OK = 200
        const val HTTP_NO_CONTENT = 204
    }
}
