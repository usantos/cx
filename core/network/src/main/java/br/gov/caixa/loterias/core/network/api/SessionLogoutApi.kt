package br.gov.caixa.loterias.core.network.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Streaming
import retrofit2.http.Tag

/** O legado DadosUsuarioBO.sair não envia corpo nem consulta e consome a confirmação HTTP. */
interface SessionLogoutApi {
    @POST("usuarios/sair")
    @Streaming
    suspend fun signOut(@Tag session: AccountVerificationSession): Response<ResponseBody>
}

class SessionLogoutApiProvider {
    @Volatile private var api: SessionLogoutApi? = null
    fun current(): SessionLogoutApi = checkNotNull(api) { "API de logout ainda não configurada" }
    fun replace(value: SessionLogoutApi) { api = value }
}
