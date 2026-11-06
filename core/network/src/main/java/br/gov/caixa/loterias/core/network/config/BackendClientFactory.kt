package br.gov.caixa.loterias.core.network.config

import br.gov.caixa.loterias.core.domain.environment.model.EffectiveEnvironment
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import br.gov.caixa.loterias.core.network.routing.CloudBackendRouting
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.client.AuthenticatedNetworkClients
import br.gov.caixa.loterias.core.network.client.NetworkClientOptions
import br.gov.caixa.loterias.core.network.client.PaymentProviderNetworkClients
import br.gov.caixa.loterias.core.network.client.SessionNetworkClients
import br.gov.caixa.loterias.core.network.client.TransactionNetworkClients
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
    fun updateParameters(parameters: br.gov.caixa.loterias.core.network.model.AppParametersDto) = cloudRouting.update(parameters)

    private fun backendClientBuilder() = OkHttpClient.Builder().apply {
        if (!mockOptions.enabled) addInterceptor(cloudRouting)
    }

    private fun backendOptions(
        keySet: PublicKeySet,
        baseUrl: String = environment.apiBaseUrl,
        clientBuilder: OkHttpClient.Builder = backendClientBuilder(),
    ) = NetworkClientOptions(
        baseUrl = baseUrl,
        clientBuilder = clientBuilder,
        gson = gson(),
        publicKeySet = keySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions,
        sessionRefreshHandler = sessionRefreshHandler,
    )

    private fun paymentOptions(baseUrl: String, keySet: PublicKeySet) = NetworkClientOptions(
        baseUrl = baseUrl,
        publicKeySet = keySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions,
    )

    fun createApi(keySet: PublicKeySet) =
        AuthenticatedNetworkClients.create(backendOptions(keySet), tokenProvider)

    fun createNotificationRead(keySet: PublicKeySet) =
        SessionNetworkClients.createNotificationRead(backendOptions(keySet))

    fun createNotificationInbox(keySet: PublicKeySet) =
        SessionNetworkClients.createNotificationInbox(backendOptions(keySet))

    fun createSessionLogout(keySet: PublicKeySet) =
        SessionNetworkClients.createSessionLogout(backendOptions(keySet))

    fun createAccountVerification(keySet: PublicKeySet) =
        SessionNetworkClients.createAccountVerification(backendOptions(keySet))

    fun createPrizeRedemption(keySet: PublicKeySet) =
        TransactionNetworkClients.createPrizeRedemption(backendOptions(keySet), tokenProvider)

    fun createPurchaseBackend(keySet: PublicKeySet) =
        TransactionNetworkClients.createPurchaseBackend(backendOptions(keySet), tokenProvider)

    fun createLegacy(keySet: PublicKeySet) =
        AuthenticatedNetworkClients.createLegacy(backendOptions(keySet), tokenProvider)

    fun createBetsHistory(keySet: PublicKeySet) =
        AuthenticatedNetworkClients.createBetsHistory(backendOptions(keySet), tokenProvider)

    fun createDynamicBetsHistory(baseUrl: String, keySet: PublicKeySet) =
        AuthenticatedNetworkClients.createBetsHistory(
            backendOptions(keySet, baseUrl, OkHttpClient.Builder()),
            tokenProvider,
        )

    fun createPaymentApis(keySet: PublicKeySet) = PaymentApis(
        mercadoPago = PaymentProviderNetworkClients.createMercadoPago(paymentOptions(environment.mercadoPagoBaseUrl, keySet)),
        recargaPay = PaymentProviderNetworkClients.createRecargaPay(paymentOptions(environment.recargaPayBaseUrl, keySet)),
    )
}
