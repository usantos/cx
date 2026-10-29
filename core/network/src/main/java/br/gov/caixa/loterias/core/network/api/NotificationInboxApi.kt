package br.gov.caixa.loterias.core.network.api

import br.gov.caixa.loterias.feature.notifications.data.dto.NotificationInboxEnvelopeDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Tag

/** Cada operação usa a sessão de login capturada pelo chamador. */
interface NotificationInboxApi {
    @GET("historico-notificacoes")
    suspend fun all(@Tag session: AccountVerificationSession): Response<NotificationInboxEnvelopeDto>

    @GET("historico-notificacoes/nao-visualizadas")
    suspend fun unread(@Tag session: AccountVerificationSession): Response<NotificationInboxEnvelopeDto>

    @PUT("historico-notificacoes/{id}/visualiza")
    suspend fun markRead(@Path("id") id: Long, @Tag session: AccountVerificationSession): Response<ResponseBody>

    @PUT("historico-notificacoes/{id}/remove")
    suspend fun remove(@Path("id") id: Long, @Tag session: AccountVerificationSession): Response<ResponseBody>

    @PUT("historico-notificacoes/limpa")
    suspend fun clear(@Tag session: AccountVerificationSession): Response<ResponseBody>
}

class NotificationInboxApiProvider {
    @Volatile private var api: NotificationInboxApi? = null
    fun current(): NotificationInboxApi = checkNotNull(api) { "API de histórico de notificações ainda não configurada" }
    fun replace(value: NotificationInboxApi) {
        api = value
    }
}
