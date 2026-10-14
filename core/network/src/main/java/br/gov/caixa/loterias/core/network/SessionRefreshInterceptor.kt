package br.gov.caixa.loterias.core.network

import okhttp3.Interceptor
import okhttp3.Response

/** Captures one authenticated session before any request is sent. */
fun interface SessionRefreshHandler {
    fun bind(authorization: String): SessionRefreshLease?
}

/** Implementations must never read a replacement login's bearer. Headers are kept private. */
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
        // Repeat only the original request after authenticated renewal, never a transport follow-up.
        return chain.proceed(original.newBuilder().header("Authorization", renewed).build())
    }
}
