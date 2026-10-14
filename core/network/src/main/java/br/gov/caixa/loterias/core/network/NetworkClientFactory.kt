package br.gov.caixa.loterias.core.network

import com.google.gson.Gson
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.CertificatePinner
import br.gov.caixa.loterias.core.keymanagement.PublicKeySet
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClientFactory {
    /** Logout stays bound to one login; only explicit authenticated renewal may repeat it. */
    fun createSessionLogout(
        baseUrl: String,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
        sessionRefreshHandler: SessionRefreshHandler? = null,
    ): SessionLogoutApi = retrofit(
        baseUrl, AuthTokenProvider { null },
        clientBuilder.followRedirects(false).followSslRedirects(false).retryOnConnectionFailure(false)
            .addInterceptor(AccountVerificationSessionInterceptor())
            .addInterceptor(SingleAttemptTransactionInterceptor()),
        gson, publicKeySet, debugOptions, mockOptions, attachBearerToken = false, sessionRefreshHandler = sessionRefreshHandler,
    ).create(SessionLogoutApi::class.java)

    fun createNotificationRead(
        baseUrl: String,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
        sessionRefreshHandler: SessionRefreshHandler? = null,
    ): NotificationReadApi = retrofit(
        baseUrl, AuthTokenProvider { null },
        clientBuilder.followRedirects(false).followSslRedirects(false).retryOnConnectionFailure(false)
            .addInterceptor(AccountVerificationSessionInterceptor())
            .addInterceptor(SingleAttemptTransactionInterceptor()),
        gson, publicKeySet, debugOptions, mockOptions, attachBearerToken = false, sessionRefreshHandler = sessionRefreshHandler,
    ).create(NotificationReadApi::class.java)

    fun createAccountVerification(
        baseUrl: String,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
        sessionRefreshHandler: SessionRefreshHandler? = null,
    ): AccountVerificationBackendClient = AccountVerificationBackendClient(
        retrofit(
            baseUrl, AuthTokenProvider { null },
            clientBuilder.followRedirects(false).followSslRedirects(false).retryOnConnectionFailure(false)
                .addInterceptor(AccountVerificationSessionInterceptor())
                .addInterceptor(SingleAttemptTransactionInterceptor()),
            gson, publicKeySet, debugOptions, mockOptions, attachBearerToken = false, sessionRefreshHandler = sessionRefreshHandler,
    ).create(AccountVerificationBackendApi::class.java), gson,
    )

    /** Prize redemption forbids transport follow-ups; authenticated renewal is explicit. */
    fun createPrizeRedemption(
        baseUrl: String,
        tokenProvider: AuthTokenProvider,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
        sessionRefreshHandler: SessionRefreshHandler? = null,
    ): LegacyBackendApi = retrofit(
        baseUrl, tokenProvider,
        clientBuilder.followRedirects(false).followSslRedirects(false).retryOnConnectionFailure(false)
            .addInterceptor(SingleAttemptTransactionInterceptor()),
        gson, publicKeySet, debugOptions, mockOptions, sessionRefreshHandler = sessionRefreshHandler,
    ).create(LegacyBackendApi::class.java)

    fun createPurchaseBackend(
        baseUrl: String,
        tokenProvider: AuthTokenProvider,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
        sessionRefreshHandler: SessionRefreshHandler? = null,
    ): PurchaseBackendClient = PurchaseBackendClient(
        retrofit(
            baseUrl, tokenProvider,
            clientBuilder.followRedirects(false).followSslRedirects(false).retryOnConnectionFailure(false)
                .addInterceptor(SingleAttemptTransactionInterceptor()),
            gson, publicKeySet, debugOptions, mockOptions, sessionRefreshHandler = sessionRefreshHandler,
    ).create(PurchaseBackendApi::class.java),
        gson,
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
    ): LoteriasApi = retrofit(
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
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
        sessionRefreshHandler: SessionRefreshHandler? = null,
    ): LegacyBackendApi = retrofit(
        baseUrl = baseUrl,
        tokenProvider = tokenProvider,
        clientBuilder = clientBuilder,
        gson = gson,
        publicKeySet = publicKeySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions, sessionRefreshHandler = sessionRefreshHandler,
    ).create(LegacyBackendApi::class.java)

    fun createBetsHistory(
        baseUrl: String,
        tokenProvider: AuthTokenProvider,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
        sessionRefreshHandler: SessionRefreshHandler? = null,
    ): BetsHistoryApi = retrofit(
        baseUrl = baseUrl,
        tokenProvider = tokenProvider,
        clientBuilder = clientBuilder,
        gson = gson,
        publicKeySet = publicKeySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions, sessionRefreshHandler = sessionRefreshHandler,
    ).create(BetsHistoryApi::class.java)

    fun createMercadoPago(
        baseUrl: String,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
    ): MercadoPagoApi = retrofit(
        baseUrl = baseUrl,
        tokenProvider = AuthTokenProvider { null },
        clientBuilder = clientBuilder,
        gson = gson,
        publicKeySet = publicKeySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions,
        attachBearerToken = false,
    ).create(MercadoPagoApi::class.java)

    fun createRecargaPay(
        baseUrl: String,
        clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
        gson: Gson = Gson(),
        publicKeySet: PublicKeySet? = null,
        debugOptions: NetworkDebugOptions = NetworkDebugOptions(),
        mockOptions: MockBackendOptions = MockBackendOptions(),
    ): RecargaPayApi = retrofit(
        baseUrl = baseUrl,
        tokenProvider = AuthTokenProvider { null },
        clientBuilder = clientBuilder,
        gson = gson,
        publicKeySet = publicKeySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions,
        attachBearerToken = false,
    ).create(RecargaPayApi::class.java)

    private fun retrofit(
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
                // Renewal sees the raw body, before the one-shot transport guard and SILCE encoding.
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
            .addInterceptor(CurlLoggingInterceptor(debugOptions))
            .addInterceptor(MockBackendInterceptor(mockOptions))
            .addInterceptor(SilceTransportInterceptor(normalizeBaseUrl(baseUrl).toHttpUrl()))
            .apply {
                publicKeySet?.let { keySet ->
                    certificatePinner(certificatePinner(keySet))
                    val pinnedHostGuard = PinnedHostInterceptor(keySet)
                    // Mock responses stop before this guard; no real connection bypasses it.
                    addInterceptor(pinnedHostGuard)
                    addNetworkInterceptor(pinnedHostGuard)
                }
            }
            .build()
        return Retrofit.Builder()
            .baseUrl(normalizeBaseUrl(baseUrl))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    internal fun certificatePinner(keySet: PublicKeySet): CertificatePinner =
        CertificatePinner.Builder().apply {
            keySet.hosts.forEach { host -> add(host.hostname, *host.pins.toTypedArray()) }
        }.build()
}
