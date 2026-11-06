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
import br.gov.caixa.loterias.core.network.interceptor.NetworkDebugOptions
import br.gov.caixa.loterias.core.network.interceptor.CurlLoggingInterceptor
import br.gov.caixa.loterias.core.network.interceptor.SessionRefreshHandler
import br.gov.caixa.loterias.core.network.interceptor.SessionRefreshInterceptor
import br.gov.caixa.loterias.core.network.interceptor.BoundDeletionSingleAttemptInterceptor
import br.gov.caixa.loterias.core.network.api.AccountVerificationSessionInterceptor
import br.gov.caixa.loterias.core.network.interceptor.MockBackendInterceptor
import br.gov.caixa.loterias.core.network.interceptor.PinnedHostInterceptor
import br.gov.caixa.loterias.core.network.interceptor.SilceTransportInterceptor

internal class NetworkClientOptions(
    val baseUrl: String,
    val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
    val gson: Gson = Gson(),
    val publicKeySet: PublicKeySet? = null,
    val debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
    val mockOptions: MockBackendOptions = MockBackendOptions(),
    val sessionRefreshHandler: SessionRefreshHandler? = null,
)

internal fun OkHttpClient.Builder.singleAttemptRequests(): OkHttpClient.Builder =
    followRedirects(false).followSslRedirects(false).retryOnConnectionFailure(false)
        .addInterceptor(SingleAttemptTransactionInterceptor())

internal fun createConfiguredRetrofit(
    options: NetworkClientOptions,
    tokenProvider: AuthTokenProvider? = null,
): Retrofit {
    val client = options.clientBuilder
        .apply {
            // A renovação lê o corpo bruto antes da proteção de tentativa única e da codificação SILCE.
            val singleAttempt = interceptors().filterIsInstance<SingleAttemptTransactionInterceptor>()
            interceptors().removeAll(singleAttempt.toSet())
            if (tokenProvider != null) {
                addInterceptor(AccountVerificationSessionInterceptor(required = false))
                addInterceptor(BearerTokenInterceptor(tokenProvider))
            }
            options.sessionRefreshHandler?.let { addInterceptor(SessionRefreshInterceptor(it)) }
            singleAttempt.forEach(::addInterceptor)
            if (tokenProvider != null) addInterceptor(BoundDeletionSingleAttemptInterceptor())
        }
        .apply {
            val debugOptions = options.debugOptions
            if (debugOptions.enabled) {
                debugOptions.inspector?.let { addInterceptor(it) }
                addInterceptor(CurlLoggingInterceptor(debugOptions))
            }
        }
        .addInterceptor(MockBackendInterceptor(options.mockOptions))
        .addInterceptor(SilceTransportInterceptor(NetworkClientFactory.normalizeBaseUrl(options.baseUrl).toHttpUrl()))
        .apply {
            options.publicKeySet?.let { keySet ->
                certificatePinner(NetworkClientFactory.certificatePinner(keySet))
                val pinnedHostGuard = PinnedHostInterceptor(keySet)
                // Respostas simuladas param antes desta proteção; nenhuma conexão real a ignora.
                addInterceptor(pinnedHostGuard)
                addNetworkInterceptor(pinnedHostGuard)
            }
        }
        .build()
    return Retrofit.Builder()
        .baseUrl(NetworkClientFactory.normalizeBaseUrl(options.baseUrl))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(options.gson))
        .build()
}
