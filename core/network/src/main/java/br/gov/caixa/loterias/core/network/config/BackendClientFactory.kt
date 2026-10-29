package br.gov.caixa.loterias.core.network.config

import br.gov.caixa.loterias.core.domain.environment.model.EffectiveEnvironment
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import br.gov.caixa.loterias.core.network.routing.CloudBackendRouting
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.client.NetworkClientFactory
import br.gov.caixa.loterias.core.network.interceptor.NetworkDebugOptions
import br.gov.caixa.loterias.core.network.provider.PaymentApis
import okhttp3.OkHttpClient

/** Cria clientes com a configuração do processo; a instalação pertence ao coordenador. */
class BackendClientFactory(
    private val environment: EffectiveEnvironment,
    private val tokenProvider: br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider,
    private val gson: () -> com.google.gson.Gson,
    private val cloudRouting: CloudBackendRouting,
    private val debugOptions: NetworkDebugOptions,
    private val mockOptions: MockBackendOptions,
    private val sessionRefreshHandler: br.gov.caixa.loterias.core.network.interceptor.SessionRefreshHandler?,
) {
    private val backendGson get() = gson()

    fun updateParameters(parameters: br.gov.caixa.loterias.core.network.model.AppParametersDto) = cloudRouting.update(parameters)

    private fun backendClientBuilder() = OkHttpClient.Builder().apply {
        if (!mockOptions.enabled) addInterceptor(cloudRouting)
    }

    fun createApi(keySet: PublicKeySet) =
        NetworkClientFactory.create(
            baseUrl = environment.apiBaseUrl, clientBuilder = backendClientBuilder(),
            tokenProvider = tokenProvider,
            gson = backendGson,
            publicKeySet = keySet,
            debugOptions = debugOptions,
            mockOptions = mockOptions, sessionRefreshHandler = sessionRefreshHandler,
        )

    fun createNotificationRead(keySet: PublicKeySet) =
        NetworkClientFactory.createNotificationRead(
            baseUrl = environment.apiBaseUrl, clientBuilder = backendClientBuilder(), gson = backendGson, publicKeySet = keySet,
            debugOptions = debugOptions, mockOptions = mockOptions, sessionRefreshHandler = sessionRefreshHandler,
        )

    fun createNotificationInbox(keySet: PublicKeySet) = NetworkClientFactory.createNotificationInbox(
        baseUrl = environment.apiBaseUrl,
        clientBuilder = backendClientBuilder(),
        gson = backendGson,
        publicKeySet = keySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions,
        sessionRefreshHandler = sessionRefreshHandler
    )

    fun createSessionLogout(keySet: PublicKeySet) = NetworkClientFactory.createSessionLogout(
        baseUrl = environment.apiBaseUrl,
        clientBuilder = backendClientBuilder(),
        gson = backendGson,
        publicKeySet = keySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions,
        sessionRefreshHandler = sessionRefreshHandler
    )

    fun createAccountVerification(keySet: PublicKeySet) = NetworkClientFactory.createAccountVerification(
        baseUrl = environment.apiBaseUrl,
        clientBuilder = backendClientBuilder(),
        gson = backendGson,
        publicKeySet = keySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions,
        sessionRefreshHandler = sessionRefreshHandler
    )

    fun createPrizeRedemption(keySet: PublicKeySet) = NetworkClientFactory.createPrizeRedemption(
        baseUrl = environment.apiBaseUrl,
        clientBuilder = backendClientBuilder(),
        tokenProvider = tokenProvider,
        gson = backendGson,
        publicKeySet = keySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions,
        sessionRefreshHandler = sessionRefreshHandler
    )

    fun createPurchaseBackend(keySet: PublicKeySet) = NetworkClientFactory.createPurchaseBackend(
        baseUrl = environment.apiBaseUrl,
        clientBuilder = backendClientBuilder(),
        tokenProvider = tokenProvider,
        gson = backendGson,
        publicKeySet = keySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions,
        sessionRefreshHandler = sessionRefreshHandler
    )

    fun createLegacy(keySet: PublicKeySet) = NetworkClientFactory.createLegacy(
        baseUrl = environment.apiBaseUrl,
        clientBuilder = backendClientBuilder(),
        tokenProvider = tokenProvider,
        gson = backendGson,
        publicKeySet = keySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions,
        sessionRefreshHandler = sessionRefreshHandler
    )

    fun createBetsHistory(keySet: PublicKeySet) = NetworkClientFactory.createBetsHistory(
        baseUrl = environment.apiBaseUrl,
        clientBuilder = backendClientBuilder(),
        tokenProvider = tokenProvider,
        gson = backendGson,
        publicKeySet = keySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions,
        sessionRefreshHandler = sessionRefreshHandler
    )

    fun createDynamicBetsHistory(baseUrl: String, keySet: PublicKeySet) = NetworkClientFactory.createBetsHistory(
        baseUrl = baseUrl,
        tokenProvider = tokenProvider,
        gson = backendGson,
        publicKeySet = keySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions,
        sessionRefreshHandler = sessionRefreshHandler
    )

    fun createPaymentApis(keySet: PublicKeySet) = PaymentApis(
        mercadoPago = NetworkClientFactory.createMercadoPago(
            baseUrl = environment.mercadoPagoBaseUrl,
            publicKeySet = keySet,
            debugOptions = debugOptions,
            mockOptions = mockOptions
        ),
        recargaPay = NetworkClientFactory.createRecargaPay(
            baseUrl = environment.recargaPayBaseUrl,
            publicKeySet = keySet,
            debugOptions = debugOptions,
            mockOptions = mockOptions
        )
    )
}
