package br.gov.caixa.loterias.feature.account.navigation

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.RecalculateAuthorizedLimitUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.RegisterAccountUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResolveZipCodeUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.UpdatePersonalDataUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import br.gov.caixa.loterias.feature.account.presentation.permission.loginNotificationPermissionReady as permissionReady
import br.gov.caixa.loterias.feature.account.presentation.route.AccountLoginEnvironmentSelector
import javax.inject.Inject

class AccountNavigationDependenciesAdapter @Inject constructor(
    override val loginNetworkAvailability: NetworkAvailability,
    override val loginConnectionPresentation: LoginConnectionPresentation,
    override val accountProfile: GetAccountProfileUseCase,
    override val authentication: AuthenticationGateway,
    override val currentUserIdentity: CurrentUserIdentityProvider,
    override val dispatchers: CoroutineDispatchers,
    override val verifyPostLoginAccess: VerifyPostLoginAccessUseCase,
    private val accountEnvironment: AccountEnvironmentDependencies,
    private val hostConfiguration: HostFeatureConfiguration,
    private val accountRegistrationDependencies: AccountRegistrationDependencies,
) : AccountNavigationDependencies {
    override val recalculateAuthorizedLimit: RecalculateAuthorizedLimitUseCase get() =
        accountRegistrationDependencies.recalculateAuthorizedLimit
    override val registerAccount: RegisterAccountUseCase get() = accountRegistrationDependencies.registerAccount
    override val resolveZipCode: ResolveZipCodeUseCase get() = accountRegistrationDependencies.resolveZipCode
    override val updatePersonalData: UpdatePersonalDataUseCase get() = accountRegistrationDependencies.updatePersonalData

    override val accountReferrerUri: String get() = hostConfiguration.accountReferrerUri
    override val appVersionName: String get() = hostConfiguration.appVersionName
    override val environmentSelectorVisible: Boolean get() = hostConfiguration.environmentSelectorVisible
    override val ssoAccountUrl: String get() = hostConfiguration.ssoAccountUrl
    override val ssoClientId: String get() = hostConfiguration.ssoClientId

    @Composable
    override fun loginNotificationPermissionReady(): Boolean = permissionReady(hostConfiguration.mockBackendEnabled)

    @Composable
    override fun LoginEnvironmentSelector(enabled: Boolean) = AccountLoginEnvironmentSelector(enabled, accountEnvironment)
}

class AccountRegistrationDependencies @Inject constructor(
    val recalculateAuthorizedLimit: RecalculateAuthorizedLimitUseCase,
    val registerAccount: RegisterAccountUseCase,
    val resolveZipCode: ResolveZipCodeUseCase,
    val updatePersonalData: UpdatePersonalDataUseCase,
)
