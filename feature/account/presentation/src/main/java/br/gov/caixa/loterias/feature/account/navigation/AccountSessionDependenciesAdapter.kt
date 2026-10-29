package br.gov.caixa.loterias.feature.account.navigation

import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.auth.gateway.EndSessionGateway
import br.gov.caixa.loterias.core.auth.session.ForegroundSessionAccess
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.feature.account.domain.repository.AuthenticatedSessionContentRepository
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.ClearLocalSessionDataUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResumeLocalSessionCleanupUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.SignOutSessionUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AccountSessionDependenciesAdapter @Inject constructor(
    override val authentication: AuthenticationGateway,
    override val endSession: EndSessionGateway,
    override val clearLocalSessionData: ClearLocalSessionDataUseCase,
    override val resumeLocalSessionCleanup: ResumeLocalSessionCleanupUseCase,
    override val signOutSession: SignOutSessionUseCase,
    override val verifyPostLoginAccess: VerifyPostLoginAccessUseCase,
    override val authenticatedSessionIdentity: AuthenticatedSessionIdentityProvider,
    override val authenticatedSessionContent: AuthenticatedSessionContentRepository,
    override val currentUserIdentity: CurrentUserIdentityProvider,
    override val dispatchers: CoroutineDispatchers,
    private val sessionTokenStore: SessionTokenStore,
) : AccountSessionDependencies {
    override val foregroundSession: ForegroundSessionAccess get() = sessionTokenStore
    override val sessionIdentityChanges: Flow<AuthenticatedSessionIdentity?>
        get() = sessionTokenStore.observeIdentities()
}
