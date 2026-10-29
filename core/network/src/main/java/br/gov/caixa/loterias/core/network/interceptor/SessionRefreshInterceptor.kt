package br.gov.caixa.loterias.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/** Captura uma sessão autenticada antes de enviar qualquer requisição. */
fun interface SessionRefreshHandler {
    fun bind(authorization: String): SessionRefreshLease?
}

/** Implementações nunca podem ler o bearer de um novo login. Headers permanecem privados. */
interface SessionRefreshLease {
    fun prepare(): String?
    fun refreshAfterUnauthorized(): String?
}

class SessionRefreshInterceptor(private val handler: SessionRefreshHandler) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val authorization = original.header("Authorization") ?: return chain.proceed(original)
        val lease = handler.bind(authorization) ?: throw java.io.IOException("Sessão autenticada encerrada")
        val prepared = lease.prepare() ?: throw java.io.IOException("Não foi possível renovar a sessão")
        val response = chain.proceed(original.newBuilder().header("Authorization", prepared).build())
        if (response.code != 401 || original.body?.isOneShot() == true || original.body?.isDuplex() == true) return response
        val renewed = lease.refreshAfterUnauthorized() ?: return response
        response.close()
        // Repete somente a requisição original após renovar a autenticação, nunca uma continuação do transporte.
        return chain.proceed(original.newBuilder().header("Authorization", renewed).build())
    }
}
