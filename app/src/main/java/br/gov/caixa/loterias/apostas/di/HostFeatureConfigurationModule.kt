package br.gov.caixa.loterias.apostas.di

import br.gov.caixa.loterias.apostas.BuildConfig
import br.gov.caixa.loterias.apostas.environment.runtime.ProcessEnvironmentSettings
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import br.gov.caixa.loterias.feature.home.presentation.mapper.homeEnvironmentLabel
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.network.config.NetworkClientCoordinator
import br.gov.caixa.loterias.feature.account.navigation.AccountEnvironmentDependencies
import br.gov.caixa.loterias.feature.cart.navigation.CartRecoveryStatusPresentation
import br.gov.caixa.loterias.feature.onboarding.navigation.StartupStatusPresentation
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.StartupStatusKind
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.apostas.composition.startup.AppStartupStatusPanel
import br.gov.caixa.loterias.apostas.runtime.host.AppHostRuntime
import br.gov.caixa.loterias.feature.account.domain.environment.ChangeEnvironment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HostFeatureConfigurationModule {
    @Provides
    @Singleton
    fun hostFeatureConfiguration(settings: ProcessEnvironmentSettings): HostFeatureConfiguration =
        HostFeatureConfiguration(
            appVersionName = BuildConfig.VERSION_NAME,
            accountReferrerUri = BuildConfig.SSO_ACCOUNT_REFERRER_URI,
            ssoAccountUrl = settings.effective.ssoAccountUrl,
            ssoClientId = settings.effective.ssoClientId,
            environmentSelectorVisible = settings.selectorVisible,
            homeNavigationEnvironmentLabel = homeEnvironmentLabel(
                BuildConfig.FLAVOR,
                settings.effective.apiBaseUrl,
            ),
            mockBackendEnabled = BuildConfig.ENABLE_MOCK_BACKEND,
            firebaseAppId = BuildConfig.FIREBASE_APP_ID,
            firebaseApiKey = BuildConfig.FIREBASE_API_KEY,
            firebaseProjectId = BuildConfig.FIREBASE_PROJECT_ID,
            firebaseSenderId = BuildConfig.FIREBASE_SENDER_ID,
            backendApiBaseUrl = settings.effective.apiBaseUrl,
            backendSessionNamespace = settings.effective.sessionNamespace,
            ssoAuthUrl = settings.effective.ssoAuthUrl,
            ssoTokenUrl = settings.effective.ssoTokenUrl,
            ssoLogoutUrl = settings.effective.ssoLogoutUrl,
            ssoRedirectUri = settings.effective.ssoRedirectUri,
            productionBuild = BuildConfig.FLAVOR.equals("prd", ignoreCase = true),
        )

    @Provides
    @Singleton
    fun accountEnvironmentDependencies(
        settings: ProcessEnvironmentSettings,
        changeEnvironment: ChangeEnvironment,
    ): AccountEnvironmentDependencies {
        return AppAccountEnvironmentDependencies(settings, changeEnvironment)
    }

    @Provides
    @Singleton
    internal fun appHostRuntime(
        settings: ProcessEnvironmentSettings,
        authentication: AuthenticationGateway,
        tokens: SessionTokenStore,
        networkClients: dagger.Lazy<NetworkClientCoordinator>,
        dispatchers: CoroutineDispatchers,
    ): AppHostRuntime = AppHostRuntime(settings, authentication, tokens, networkClients, dispatchers).also { it.initialize() }

    @Provides
    internal fun changeEnvironment(runtime: AppHostRuntime): ChangeEnvironment = runtime.environmentChange

    @Provides
    @Singleton
    fun loginConnectionPresentation(network: NetworkAvailability): br.gov.caixa.loterias.feature.account.navigation.LoginConnectionPresentation =
        object : br.gov.caixa.loterias.feature.account.navigation.LoginConnectionPresentation {
            @androidx.compose.runtime.Composable
            override fun Offline(onRetry: () -> Unit, loading: Boolean) = AppStartupStatusPanel(
                StartupStatusKind.OFFLINE, onRetry, network, loading, connectionFailure = true)
        }

    @Provides
    @Singleton
    fun startupStatusPresentation(): StartupStatusPresentation = AppHostStartupStatusPresentation()

    @Provides
    @Singleton
    fun cartRecoveryStatusPresentation(): CartRecoveryStatusPresentation = AppCartRecoveryStatusPresentation()
}

private class AppAccountEnvironmentDependencies(
    settings: ProcessEnvironmentSettings,
    override val changeEnvironment: ChangeEnvironment,
) : AccountEnvironmentDependencies {
    override val effectiveEnvironment = settings.effective
    override val selectedEnvironment = settings.selected
    override val selectorVisible = settings.selectorVisible
}

private class AppHostStartupStatusPresentation : StartupStatusPresentation {
    @androidx.compose.runtime.Composable
    override fun Content(
        kind: StartupStatusKind,
        onRetry: () -> Unit,
        networkAvailability: NetworkAvailability,
        loading: Boolean,
    ) = AppStartupStatusPanel(kind, onRetry, networkAvailability, loading)
}

private class AppCartRecoveryStatusPresentation : CartRecoveryStatusPresentation {
    @androidx.compose.runtime.Composable
    override fun Content(
        loading: Boolean,
        onRetry: () -> Unit,
        networkAvailability: NetworkAvailability,
    ) = AppStartupStatusPanel(
        kind = StartupStatusKind.UNAVAILABLE,
        onRetry = onRetry,
        networkAvailability = networkAvailability,
        loading = loading,
    )
}
