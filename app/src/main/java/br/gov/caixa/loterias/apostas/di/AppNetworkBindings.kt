package br.gov.caixa.loterias.apostas.di

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import br.gov.caixa.loterias.apostas.BuildConfig
import br.gov.caixa.loterias.apostas.environment.runtime.ProcessEnvironmentSettings
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.keymanagement.model.HostPublicKeyPins
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import br.gov.caixa.loterias.core.keymanagement.model.TrustedConfigurationSigners
import br.gov.caixa.loterias.core.keymanagement.repository.PublicKeySetRepository
import br.gov.caixa.loterias.core.keymanagement.verification.SignedKeySetVerifier
import br.gov.caixa.loterias.core.network.config.BackendClientFactory
import br.gov.caixa.loterias.core.network.config.NetworkApiProviders
import br.gov.caixa.loterias.core.network.config.NetworkClientCoordinator
import br.gov.caixa.loterias.core.network.di.NetworkApiProvidersModule
import br.gov.caixa.loterias.core.network.interceptor.NetworkDebugOptions
import br.gov.caixa.loterias.core.network.interceptor.NetworkLogSink
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.mock.MockBackendScenario
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.provider.BetsHistoryApiProvider
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.provider.PaymentApiProvider
import br.gov.caixa.loterias.core.network.routing.CloudBackendRouting
import br.gov.caixa.loterias.core.security.inspection.createNetworkInspector
import br.gov.caixa.loterias.core.security.keyset.FirebaseKeySetOptions
import br.gov.caixa.loterias.core.security.keyset.FirebaseRemotePublicKeySetSource
import br.gov.caixa.loterias.core.security.keyset.PreferencesSignedKeySetStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.HttpUrl.Companion.toHttpUrl

@Module(includes = [NetworkApiProvidersModule::class])
@InstallIn(SingletonComponent::class)
object AppNetworkBindings {
    @Provides @Singleton
    fun networkSecurity(
        networkClients: NetworkClientCoordinator,
    ): br.gov.caixa.loterias.core.domain.configuration.usecase.PrepareNetworkSecurityUseCase = networkClients.networkSecurity

    @Provides @Singleton
    fun serverClock() = br.gov.caixa.loterias.core.common.time.MonotonicServerClock(
        elapsedMillis = { android.os.SystemClock.elapsedRealtime() }
    )

    @Provides @Singleton
    fun serverTimeSource(
        clock: br.gov.caixa.loterias.core.common.time.MonotonicServerClock,
    ): br.gov.caixa.loterias.core.common.time.ServerTimeSource = clock

    @Provides @Singleton
    fun networkApiProviders(
        apiProvider: LoteriasApiProvider,
        legacyApiProvider: BackendApiProvider,
        betsHistoryApiProvider: BetsHistoryApiProvider,
        paymentApiProvider: PaymentApiProvider,
        purchaseApiProvider: br.gov.caixa.loterias.core.network.api.PurchaseBackendApiProvider,
        prizeApiProvider: br.gov.caixa.loterias.core.network.provider.PrizeBackendApiProvider,
        accountVerificationProvider: br.gov.caixa.loterias.core.network.api.AccountVerificationBackendProvider,
        notificationReadProvider: br.gov.caixa.loterias.core.network.api.NotificationReadApiProvider,
        notificationInboxProvider: br.gov.caixa.loterias.core.network.api.NotificationInboxApiProvider,
        sessionLogoutProvider: br.gov.caixa.loterias.core.network.api.SessionLogoutApiProvider,
    ) = NetworkApiProviders(
        apiProvider = apiProvider,
        legacyApiProvider = legacyApiProvider,
        betsHistoryApiProvider = betsHistoryApiProvider,
        paymentApiProvider = paymentApiProvider,
        purchaseApiProvider = purchaseApiProvider,
        prizeApiProvider = prizeApiProvider,
        accountVerificationProvider = accountVerificationProvider,
        notificationReadProvider = notificationReadProvider,
        sessionLogoutProvider = sessionLogoutProvider,
        notificationInboxProvider = notificationInboxProvider
    )

    @Provides @Singleton
    fun networkClients(
        @ApplicationContext context: Context,
        environmentSettings: br.gov.caixa.loterias.apostas.environment.runtime.ProcessEnvironmentSettings,
        tokenStore: br.gov.caixa.loterias.core.auth.session.SessionTokenStore,
        regularSessionRefreshExecutor: br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshExecutor,
        serverClock: br.gov.caixa.loterias.core.common.time.MonotonicServerClock,
        providers: NetworkApiProviders,
    ): NetworkClientCoordinator {
        val environment = environmentSettings.effective
        val applicationContext = context.applicationContext
        // Apenas os clientes CAIXA observam o relógio do servidor, nunca os clientes de pagamento de terceiros.
        val backendGson by lazy {
            com.google.gson.GsonBuilder().registerTypeAdapterFactory(
                br.gov.caixa.loterias.core.network.adapter.ServerTimeAdapterFactory { serverClock.synchronize(it) }
            ).create()
        }
        val backendSessionRefresh = br.gov.caixa.loterias.core.auth.refresh.BackendSessionRefresh(
            tokenStore,
            br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshCoordinator(tokenStore, regularSessionRefreshExecutor)
        )

        val cloudRouting = CloudBackendRouting(environment.apiBaseUrl.toHttpUrl(), BuildConfig.FLAVOR == "prd")

        val localFallback = PublicKeySet(
            version = 0,
            issuedAtEpochSeconds = 0,
            expiresAtEpochSeconds = Long.MAX_VALUE,
            hosts = com.google.gson.Gson().fromJson(
                BuildConfig.TLS_DEFAULT_PINS,
                Array<HostPublicKeyPins>::class.java
            ).toList()
        )
        val logNetwork = BuildConfig.DEBUG && BuildConfig.ENABLE_NETWORK_LOGGING
        val debugOptions = NetworkDebugOptions(
            enabled = logNetwork,
            sink = NetworkLogSink { message -> if (logNetwork) message.chunked(3500).forEach { Log.d(NETWORK_LOG_TAG, it) } },
            includeBodies = logNetwork,
            inspector = if (logNetwork) createNetworkInspector(applicationContext) else null
        )
        val mockOptions = MockBackendOptions(
            enabled = BuildConfig.ENABLE_MOCK_BACKEND,
            scenario = MockBackendScenario.from(BuildConfig.MOCK_BACKEND_SCENARIO)
        )
        val keySetRepository = runCatching { createKeySetRepository(applicationContext, environment, localFallback) }
            .onFailure { failure ->
                if (BuildConfig.ENABLE_NETWORK_LOGGING) {
                    Log.d(NETWORK_LOG_TAG, "Configuração remota de pins indisponível: ${failure.javaClass.simpleName}")
                }
            }
            .getOrNull()
        val factory = BackendClientFactory(
            environment = environment,
            tokenProvider = tokenStore,
            gson = { backendGson },
            cloudRouting = cloudRouting,
            debugOptions = debugOptions,
            mockOptions = mockOptions,
            sessionRefreshHandler = backendSessionRefresh
        )
        return NetworkClientCoordinator(
            factory = factory,
            providers = providers,
            localFallback = localFallback,
            mockBackend = BuildConfig.ENABLE_MOCK_BACKEND,
            resolveKeySet = { keySetRepository?.refresh() },
            onKeySetResolved = { resolved ->
                if (BuildConfig.ENABLE_NETWORK_LOGGING) {
                    Log.d(
                        NETWORK_LOG_TAG,
                        "Pins TLS carregados de ${resolved?.source ?: "LOCAL_FALLBACK"}, versão ${resolved?.keySet?.version ?: 0}"
                    )
                }
            }
        )
    }

    private fun createKeySetRepository(
        applicationContext: Context,
        environment: br.gov.caixa.loterias.core.domain.environment.model.EffectiveEnvironment,
        localFallback: PublicKeySet,
    ): PublicKeySetRepository? {
        val signers = BuildConfig.KEYSET_TRUSTED_SIGNERS.split(',')
            .mapNotNull { item ->
                val parts = item.trim().split('=', limit = 2)
                if (parts.size == 2 && parts.all(String::isNotBlank)) parts[0] to parts[1] else null
            }.toMap()
        if (signers.size < BuildConfig.KEYSET_SIGNATURE_THRESHOLD) return null

        val verifier = SignedKeySetVerifier(
            trustedSigners = TrustedConfigurationSigners(signers, BuildConfig.KEYSET_SIGNATURE_THRESHOLD),
            allowedHosts = localFallback.hosts.map(HostPublicKeyPins::hostname).toSet() +
                setOf(
                    "*.caixa.gov.br",
                    requireNotNull(environment.apiBaseUrl.toUri().host),
                    requireNotNull(environment.mercadoPagoBaseUrl.toUri().host),
                    requireNotNull(environment.recargaPayBaseUrl.toUri().host)
                )
        )
        return PublicKeySetRepository(
            remote = FirebaseRemotePublicKeySetSource.create(
                applicationContext,
                FirebaseKeySetOptions(
                    BuildConfig.FIREBASE_APP_ID,
                    BuildConfig.FIREBASE_API_KEY,
                    BuildConfig.FIREBASE_PROJECT_ID,
                    BuildConfig.FIREBASE_SENDER_ID
                )
            ),
            store = PreferencesSignedKeySetStore(applicationContext),
            verifier = verifier,
            localFallback = localFallback
        )
    }

    private const val NETWORK_LOG_TAG = "LoteriasHTTP"
}
