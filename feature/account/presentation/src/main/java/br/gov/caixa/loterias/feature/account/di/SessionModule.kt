package br.gov.caixa.loterias.feature.account.di

import android.content.Context
import br.gov.caixa.loterias.core.auth.authorization.AuthorizationRequestPolicy
import br.gov.caixa.loterias.core.auth.gateway.AppAuthAuthenticationGateway
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.auth.gateway.OidcConfiguration
import br.gov.caixa.loterias.core.auth.session.JwtCurrentUserIdentityProvider
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.domain.contract.account.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.core.domain.contract.account.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.domain.contract.account.repository.AuthenticatedSessionContentRepository
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import br.gov.caixa.loterias.core.security.authentication.AndroidWeakBiometricAvailability
import br.gov.caixa.loterias.core.security.authentication.TopazAuthorizationDeviceInfoProvider
import br.gov.caixa.loterias.core.security.authentication.loginAuthorizationRequestPolicy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SessionModule {
    @Provides @Singleton
    fun authorizationRequestPolicy(
        @ApplicationContext context: Context,
        hostConfiguration: HostFeatureConfiguration,
    ): AuthorizationRequestPolicy = loginAuthorizationRequestPolicy(
        hostConfiguration.mockBackendEnabled,
        TopazAuthorizationDeviceInfoProvider(context),
        AndroidWeakBiometricAvailability(context),
    )

    @Provides @Singleton
    fun appAuthGateway(
        @ApplicationContext context: Context,
        tokenStore: SessionTokenStore,
        requestPolicy: AuthorizationRequestPolicy,
        hostConfiguration: HostFeatureConfiguration,
    ): AppAuthAuthenticationGateway = AppAuthAuthenticationGateway(
        context = context,
        configuration = OidcConfiguration(
            authorizationEndpoint = hostConfiguration.ssoAuthUrl,
            tokenEndpoint = hostConfiguration.ssoTokenUrl,
            clientId = hostConfiguration.ssoClientId,
            redirectUri = hostConfiguration.ssoRedirectUri,
            endSessionEndpoint = hostConfiguration.ssoLogoutUrl,
            postLogoutRedirectUri = hostConfiguration.ssoRedirectUri,
            sessionNamespace = hostConfiguration.backendSessionNamespace,
        ),
        tokenStore = tokenStore,
        requestPolicy = requestPolicy,
    )

    @Provides @Singleton
    fun tokenStore() = SessionTokenStore()

    @Provides
    fun accountVerificationSessionBinding(tokenStore: SessionTokenStore): br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding =
        br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding { identity ->
            br.gov.caixa.loterias.core.network.api.AccountVerificationSession(
                boundToken = { tokenStore.tokenFor(identity) },
                current = { tokenStore.isCurrent(identity) },
            )
        }

    @Provides @Singleton
    fun currentUserIdentity(tokenStore: SessionTokenStore): CurrentUserIdentityProvider =
        JwtCurrentUserIdentityProvider(tokenStore)

    @Provides
    fun regularSessionRefresh(gateway: AppAuthAuthenticationGateway): br.gov.caixa.loterias.core.auth.refresh.RegularSessionRefreshExecutor = gateway

    @Provides
    fun authentication(gateway: AppAuthAuthenticationGateway): AuthenticationGateway = gateway

    @Provides
    fun sessionIdentity(tokenStore: SessionTokenStore): AuthenticatedSessionIdentityProvider = tokenStore

    @Provides
    fun sessionContent(tokenStore: SessionTokenStore): AuthenticatedSessionContentRepository = tokenStore

    @Provides
    fun observeSessionContent(repository: AuthenticatedSessionContentRepository):
        br.gov.caixa.loterias.feature.account.domain.usecase.ObserveAuthenticatedSessionContentUseCase =
        br.gov.caixa.loterias.feature.account.domain.usecase.ObserveAuthenticatedSessionContent(repository)

    @Provides
    fun endSession(gateway: AppAuthAuthenticationGateway): br.gov.caixa.loterias.core.auth.gateway.EndSessionGateway = gateway

    @Provides @Singleton
    fun legacyLocalSessionData(@ApplicationContext context: Context): br.gov.caixa.loterias.feature.account.data.repository.LocalSessionDataRepositoryImpl =
        br.gov.caixa.loterias.feature.account.data.repository.LocalSessionDataRepositoryImpl.fromContext(context)

    @Provides
    fun localSessionData(repository: br.gov.caixa.loterias.feature.account.data.repository.LocalSessionDataRepositoryImpl):
        br.gov.caixa.loterias.feature.account.domain.repository.LocalSessionDataRepository = repository

    @Provides
    fun localSessionCleanupRecovery(repository: br.gov.caixa.loterias.feature.account.data.repository.LocalSessionDataRepositoryImpl):
        br.gov.caixa.loterias.feature.account.domain.repository.LocalSessionCleanupRecoveryRepository = repository

    @Provides
    fun resumeLocalSessionCleanup(repository: br.gov.caixa.loterias.feature.account.domain.repository.LocalSessionCleanupRecoveryRepository):
        br.gov.caixa.loterias.feature.account.domain.usecase.ResumeLocalSessionCleanupUseCase =
        br.gov.caixa.loterias.feature.account.domain.usecase.ResumeLocalSessionCleanup(repository)

    @Provides
    fun clearLocalSessionData(repository: br.gov.caixa.loterias.feature.account.domain.repository.LocalSessionDataRepository):
        br.gov.caixa.loterias.feature.account.domain.usecase.ClearLocalSessionDataUseCase =
        br.gov.caixa.loterias.feature.account.domain.usecase.ClearLocalSessionData(repository)
}
