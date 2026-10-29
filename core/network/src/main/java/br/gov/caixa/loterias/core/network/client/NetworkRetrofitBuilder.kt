package br.gov.caixa.loterias.core.network.client

import com.google.gson.Gson
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import br.gov.caixa.loterias.core.network.interceptor.BearerTokenInterceptor
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.interceptor.SingleAttemptTransactionInterceptor
import br.gov.caixa.loterias.core.network.interceptor.singleAttempt
import br.gov.caixa.loterias.core.network.interceptor.NetworkDebugOptions
import br.gov.caixa.loterias.core.network.interceptor.CurlLoggingInterceptor
import br.gov.caixa.loterias.core.network.interceptor.SessionRefreshHandler
import br.gov.caixa.loterias.core.network.interceptor.SessionRefreshInterceptor
import br.gov.caixa.loterias.core.network.interceptor.BoundDeletionSingleAttemptInterceptor
import br.gov.caixa.loterias.core.network.api.AccountVerificationSessionInterceptor
import br.gov.caixa.loterias.core.network.interceptor.MockBackendInterceptor
import br.gov.caixa.loterias.core.network.interceptor.PinnedHostInterceptor
import br.gov.caixa.loterias.core.network.interceptor.SilceTransportInterceptor

internal fun createConfiguredRetrofit(
    baseUrl: String,
    tokenProvider: AuthTokenProvider,
    clientBuilder: OkHttpClient.Builder,
    gson: Gson,
    publicKeySet: PublicKeySet?,
    debugOptions: NetworkDebugOptions,
    mockOptions: MockBackendOptions,
    attachBearerToken: Boolean = true,
    sessionRefreshHandler: SessionRefreshHandler? = null,
): Retrofit {
    val client = clientBuilder
        .apply {
            // A renovação lê o corpo bruto antes da proteção de tentativa única e da codificação SILCE.
            val singleAttempt = interceptors().filterIsInstance<SingleAttemptTransactionInterceptor>()
            interceptors().removeAll(singleAttempt.toSet())
            if (attachBearerToken) {
                addInterceptor(AccountVerificationSessionInterceptor(required = false))
                addInterceptor(BearerTokenInterceptor(tokenProvider))
            }
            sessionRefreshHandler?.let { addInterceptor(SessionRefreshInterceptor(it)) }
            singleAttempt.forEach(::addInterceptor)
            if (attachBearerToken) addInterceptor(BoundDeletionSingleAttemptInterceptor())
        }
        .apply {
            if (debugOptions.enabled) {
                debugOptions.inspector?.let { addInterceptor(it) }
                addInterceptor(CurlLoggingInterceptor(debugOptions))
            }
        }
        .addInterceptor(MockBackendInterceptor(mockOptions))
        .addInterceptor(SilceTransportInterceptor(NetworkClientFactory.normalizeBaseUrl(baseUrl).toHttpUrl()))
        .apply {
            publicKeySet?.let { keySet ->
                certificatePinner(NetworkClientFactory.certificatePinner(keySet))
                val pinnedHostGuard = PinnedHostInterceptor(keySet)
                // Respostas simuladas param antes desta proteção; nenhuma conexão real a ignora.
                addInterceptor(pinnedHostGuard)
                addNetworkInterceptor(pinnedHostGuard)
            }
        }
        .build()
    return Retrofit.Builder()
        .baseUrl(NetworkClientFactory.normalizeBaseUrl(baseUrl))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}
