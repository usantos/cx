package br.gov.caixa.loterias.core.network.client

import com.google.gson.Gson
import okhttp3.OkHttpClient
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.api.BetsHistoryApi
import br.gov.caixa.loterias.core.network.api.LoteriasApi
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.interceptor.NetworkDebugOptions
import br.gov.caixa.loterias.core.network.interceptor.SessionRefreshHandler

internal object AuthenticatedNetworkClients {
    fun create(
        baseUrl: String,
        tokenProvider: AuthTokenProvider,
        clientBuilder: OkHttpClient.Builder,
        gson: Gson,
        publicKeySet: PublicKeySet?,
        debugOptions: NetworkDebugOptions,
        mockOptions: MockBackendOptions,
        sessionRefreshHandler: SessionRefreshHandler?,
    ): LoteriasApi = createConfiguredRetrofit(
        baseUrl = baseUrl,
        tokenProvider = tokenProvider,
        clientBuilder = clientBuilder,
        gson = gson,
        publicKeySet = publicKeySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions, sessionRefreshHandler = sessionRefreshHandler,
    ).create(LoteriasApi::class.java)

    fun createLegacy(
        baseUrl: String,
        tokenProvider: AuthTokenProvider,
        clientBuilder: OkHttpClient.Builder,
        gson: Gson,
        publicKeySet: PublicKeySet?,
        debugOptions: NetworkDebugOptions,
        mockOptions: MockBackendOptions,
        sessionRefreshHandler: SessionRefreshHandler?,
    ): BackendApi = createConfiguredRetrofit(
        baseUrl = baseUrl,
        tokenProvider = tokenProvider,
        clientBuilder = clientBuilder,
        gson = gson,
        publicKeySet = publicKeySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions, sessionRefreshHandler = sessionRefreshHandler,
    ).create(BackendApi::class.java)

    fun createBetsHistory(
        baseUrl: String,
        tokenProvider: AuthTokenProvider,
        clientBuilder: OkHttpClient.Builder,
        gson: Gson,
        publicKeySet: PublicKeySet?,
        debugOptions: NetworkDebugOptions,
        mockOptions: MockBackendOptions,
        sessionRefreshHandler: SessionRefreshHandler?,
    ): BetsHistoryApi = createConfiguredRetrofit(
        baseUrl = baseUrl,
        tokenProvider = tokenProvider,
        clientBuilder = clientBuilder,
        gson = gson,
        publicKeySet = publicKeySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions, sessionRefreshHandler = sessionRefreshHandler,
    ).create(BetsHistoryApi::class.java)
}
