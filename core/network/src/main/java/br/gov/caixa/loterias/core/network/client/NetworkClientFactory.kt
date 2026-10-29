package br.gov.caixa.loterias.core.network.client

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.CertificatePinner
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.api.BetsHistoryApi
import br.gov.caixa.loterias.core.network.api.MercadoPagoApi
import br.gov.caixa.loterias.core.network.api.RecargaPayApi
import br.gov.caixa.loterias.core.network.api.LoteriasApi
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.interceptor.NetworkDebugOptions
import br.gov.caixa.loterias.core.network.interceptor.SessionRefreshHandler
import br.gov.caixa.loterias.core.network.api.NotificationInboxApi
import br.gov.caixa.loterias.core.network.api.NotificationReadApi
import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendClient
import br.gov.caixa.loterias.core.network.api.PurchaseBackendClient
import br.gov.caixa.loterias.core.network.api.SessionLogoutApi

object NetworkClientFactory {
    /** Logout permanece vinculado a um login; somente a renovação autenticada explícita pode repeti-lo. */
    fun createSessionLogout(
        baseUrl: String,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
        sessionRefreshHandler: SessionRefreshHandler? = null,
    ): SessionLogoutApi = SessionNetworkClients.createSessionLogout(
        baseUrl,
        clientBuilder,
        gson,
        publicKeySet,
        debugOptions,
        mockOptions,
        sessionRefreshHandler
    )

    fun createNotificationRead(
        baseUrl: String,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
        sessionRefreshHandler: SessionRefreshHandler? = null,
    ): NotificationReadApi = SessionNetworkClients.createNotificationRead(
        baseUrl,
        clientBuilder,
        gson,
        publicKeySet,
        debugOptions,
        mockOptions,
        sessionRefreshHandler
    )

    fun createNotificationInbox(
        baseUrl: String,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
        sessionRefreshHandler: SessionRefreshHandler? = null,
    ): NotificationInboxApi = SessionNetworkClients.createNotificationInbox(
        baseUrl,
        clientBuilder,
        gson,
        publicKeySet,
        debugOptions,
        mockOptions,
        sessionRefreshHandler
    )

    fun createAccountVerification(
        baseUrl: String,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
        sessionRefreshHandler: SessionRefreshHandler? = null,
    ): AccountVerificationBackendClient = SessionNetworkClients.createAccountVerification(
        baseUrl,
        clientBuilder,
        gson,
        publicKeySet,
        debugOptions,
        mockOptions,
        sessionRefreshHandler
    )

    /** Resgate de prêmio proíbe continuações do transporte; a renovação autenticada é explícita. */
    fun createPrizeRedemption(
        baseUrl: String,
        tokenProvider: AuthTokenProvider,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
        sessionRefreshHandler: SessionRefreshHandler? = null,
    ): BackendApi = TransactionNetworkClients.createPrizeRedemption(
        baseUrl,
        tokenProvider,
        clientBuilder,
        gson,
        publicKeySet,
        debugOptions,
        mockOptions,
        sessionRefreshHandler
    )

    fun createPurchaseBackend(
        baseUrl: String,
        tokenProvider: AuthTokenProvider,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
        sessionRefreshHandler: SessionRefreshHandler? = null,
    ): PurchaseBackendClient = TransactionNetworkClients.createPurchaseBackend(
        baseUrl,
        tokenProvider,
        clientBuilder,
        gson,
        publicKeySet,
        debugOptions,
        mockOptions,
        sessionRefreshHandler
    )

    fun normalizeBaseUrl(baseUrl: String): String {
        val normalized = baseUrl.trim()
        require(normalized.startsWith("https://")) { "A URL base deve usar HTTPS" }
        return if (normalized.endsWith('/')) normalized else "$normalized/"
    }

    fun create(
        baseUrl: String,
        tokenProvider: AuthTokenProvider,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
        sessionRefreshHandler: SessionRefreshHandler? = null,
    ): LoteriasApi = AuthenticatedNetworkClients.create(
        baseUrl,
        tokenProvider,
        clientBuilder,
        gson,
        publicKeySet,
        debugOptions,
        mockOptions,
        sessionRefreshHandler
    )

    fun createLegacy(
        baseUrl: String,
        tokenProvider: AuthTokenProvider,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
        sessionRefreshHandler: SessionRefreshHandler? = null,
    ): BackendApi = AuthenticatedNetworkClients.createLegacy(
        baseUrl,
        tokenProvider,
        clientBuilder,
        gson,
        publicKeySet,
        debugOptions,
        mockOptions,
        sessionRefreshHandler
    )

    fun createBetsHistory(
        baseUrl: String,
        tokenProvider: AuthTokenProvider,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
        sessionRefreshHandler: SessionRefreshHandler? = null,
    ): BetsHistoryApi = AuthenticatedNetworkClients.createBetsHistory(
        baseUrl,
        tokenProvider,
        clientBuilder,
        gson,
        publicKeySet,
        debugOptions,
        mockOptions,
        sessionRefreshHandler
    )

    fun createMercadoPago(
        baseUrl: String,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
    ): MercadoPagoApi = PaymentProviderNetworkClients.createMercadoPago(
        baseUrl,
        clientBuilder,
        gson,
        publicKeySet,
        debugOptions,
        mockOptions
    )

    fun createRecargaPay(
        baseUrl: String,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
    ): RecargaPayApi = PaymentProviderNetworkClients.createRecargaPay(
        baseUrl,
        clientBuilder,
        gson,
        publicKeySet,
        debugOptions,
        mockOptions
    )

    internal fun certificatePinner(keySet: PublicKeySet): CertificatePinner = CertificatePinner.Builder().apply {
        keySet.hosts.forEach { host -> add(host.hostname, *host.pins.toTypedArray()) }
    }.build()
}
