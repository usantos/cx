package br.gov.caixa.loterias.feature.account.navigation

import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.auth.gateway.EndSessionGateway
import br.gov.caixa.loterias.core.auth.session.ForegroundSessionAccess
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.feature.account.domain.repository.AuthenticatedSessionContentRepository
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.ClearLocalSessionDataUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResumeLocalSessionCleanupUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.SignOutSessionUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import kotlinx.coroutines.flow.Flow

/** Operações restritas de sessão pertencentes à conta, consumidas pela coordenação global do hospedeiro. */
interface AccountSessionDependencies {
    val authentication: AuthenticationGateway
    val endSession: EndSessionGateway
    val clearLocalSessionData: ClearLocalSessionDataUseCase
    val resumeLocalSessionCleanup: ResumeLocalSessionCleanupUseCase
    val signOutSession: SignOutSessionUseCase
    val verifyPostLoginAccess: VerifyPostLoginAccessUseCase
    val authenticatedSessionIdentity: AuthenticatedSessionIdentityProvider
    val authenticatedSessionContent: AuthenticatedSessionContentRepository
    val currentUserIdentity: CurrentUserIdentityProvider
    val dispatchers: CoroutineDispatchers
    val sessionIdentityChanges: Flow<AuthenticatedSessionIdentity?>
    val foregroundSession: ForegroundSessionAccess
}
