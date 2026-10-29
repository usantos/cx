package br.gov.caixa.loterias.feature.account.navigation

import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.RecalculateAuthorizedLimitUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.RegisterAccountUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResolveZipCodeUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.UpdatePersonalDataUseCase

/** Dependências deste grafo de destinos, fornecidas pela raiz de composição do aplicativo. */
interface AccountNavigationDependencies {
    val loginNetworkAvailability: br.gov.caixa.loterias.core.common.network.NetworkAvailability
    val loginConnectionPresentation: LoginConnectionPresentation
    val accountProfile: GetAccountProfileUseCase
    val accountReferrerUri: String
    val appVersionName: String
    val authentication: AuthenticationGateway
    val currentUserIdentity: CurrentUserIdentityProvider
    val dispatchers: CoroutineDispatchers
    val environmentSelectorVisible: Boolean
    val recalculateAuthorizedLimit: RecalculateAuthorizedLimitUseCase
    val registerAccount: RegisterAccountUseCase
    val resolveZipCode: ResolveZipCodeUseCase
    val ssoAccountUrl: String
    val ssoClientId: String
    val updatePersonalData: UpdatePersonalDataUseCase
    val verifyPostLoginAccess: br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase

    @androidx.compose.runtime.Composable
    fun loginNotificationPermissionReady(): Boolean

    @androidx.compose.runtime.Composable
    fun LoginEnvironmentSelector(enabled: Boolean)
}
