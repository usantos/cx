package br.gov.caixa.loterias.core.network

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Tag

data class NotificationReadEnvelopeDto(
    @SerializedName("codigo") val code: String? = null,
    @SerializedName("redirect") val redirect: JsonElement? = null,
    @SerializedName("errosValidacao") val errors: List<JsonElement>? = null,
) {
    override fun toString() = "NotificationReadEnvelopeDto(redacted)"
}

data class UnreadNotificationsEnvelopeDto(
    @SerializedName("payload") val payload: List<JsonElement>? = null,
    @SerializedName("codigo") val code: String? = null,
    @SerializedName("redirect") val redirect: JsonElement? = null,
    @SerializedName("errosValidacao") val errors: List<JsonElement>? = null,
) {
    override fun toString() = "UnreadNotificationsEnvelopeDto(redacted)"
}

interface NotificationReadApi {
    @GET("historico-notificacoes/nao-visualizadas")
    suspend fun unread(@Tag session: AccountVerificationSession): Response<UnreadNotificationsEnvelopeDto>

    @PUT("historico-notificacoes/{notificationId}/visualiza")
    suspend fun markRead(@Path("notificationId") notificationId: Long,
        @Tag session: AccountVerificationSession): Response<NotificationReadEnvelopeDto>
}

class NotificationReadApiProvider {
    @Volatile private var api: NotificationReadApi? = null
    fun current(): NotificationReadApi = checkNotNull(api) { "API de leitura de notificação ainda não configurada" }
    fun replace(value: NotificationReadApi) { api = value }
}
