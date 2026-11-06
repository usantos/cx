package br.gov.caixa.loterias.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

fun interface AuthTokenProvider {
    fun token(): String?
}

class BearerTokenInterceptor(
    private val tokenProvider: AuthTokenProvider,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = tokenProvider.token()?.trim().orEmpty()
        if (token.isEmpty() || request.header(AUTHORIZATION) != null) {
            return chain.proceed(request)
        }
        return chain.proceed(
            request.newBuilder().header(AUTHORIZATION, "Bearer $token").build(),
        )
    }

    private companion object {
        const val AUTHORIZATION = "Authorization"
    }
}
