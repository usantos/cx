package br.gov.caixa.loterias.core.network

import okhttp3.Interceptor
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

/** Runs after authenticated renewal; bodyless bound DELETE must not trigger HTTP follow-ups. */
internal class BoundDeletionSingleAttemptInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.method != "DELETE" || request.tag(AccountVerificationSession::class.java) == null) {
            return chain.proceed(request)
        }
        val withBody = if (request.body == null) {
            request.newBuilder().method("DELETE", ByteArray(0).toRequestBody()).build()
        } else request
        return chain.proceed(withBody.singleAttempt())
    }
}
