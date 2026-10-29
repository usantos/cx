package br.gov.caixa.loterias.core.network.client

import com.google.gson.Gson
import okhttp3.OkHttpClient
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.interceptor.SingleAttemptTransactionInterceptor
import br.gov.caixa.loterias.core.network.interceptor.NetworkDebugOptions
import br.gov.caixa.loterias.core.network.interceptor.SessionRefreshHandler
import br.gov.caixa.loterias.core.network.api.PurchaseBackendApi
import br.gov.caixa.loterias.core.network.api.PurchaseBackendClient

internal object TransactionNetworkClients {
    fun createPrizeRedemption(
        baseUrl: String,
        tokenProvider: AuthTokenProvider,
        clientBuilder: OkHttpClient.Builder,
        gson: Gson,
        publicKeySet: PublicKeySet?,
        debugOptions: NetworkDebugOptions,
        mockOptions: MockBackendOptions,
        sessionRefreshHandler: SessionRefreshHandler?,
    ): BackendApi = createConfiguredRetrofit(
        baseUrl, tokenProvider,
        clientBuilder.followRedirects(false).followSslRedirects(false).retryOnConnectionFailure(false)
            .addInterceptor(SingleAttemptTransactionInterceptor()),
        gson, publicKeySet, debugOptions, mockOptions, sessionRefreshHandler = sessionRefreshHandler,
    ).create(BackendApi::class.java)

    fun createPurchaseBackend(
        baseUrl: String,
        tokenProvider: AuthTokenProvider,
        clientBuilder: OkHttpClient.Builder,
        gson: Gson,
        publicKeySet: PublicKeySet?,
        debugOptions: NetworkDebugOptions,
        mockOptions: MockBackendOptions,
        sessionRefreshHandler: SessionRefreshHandler?,
    ): PurchaseBackendClient = PurchaseBackendClient(
        createConfiguredRetrofit(
            baseUrl, tokenProvider,
            clientBuilder.followRedirects(false).followSslRedirects(false).retryOnConnectionFailure(false)
                .addInterceptor(SingleAttemptTransactionInterceptor()),
            gson, publicKeySet, debugOptions, mockOptions, sessionRefreshHandler = sessionRefreshHandler,
    ).create(PurchaseBackendApi::class.java),
        gson,
    )
}
