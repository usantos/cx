package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.feature.account.domain.model.SessionSignOutResult
import br.gov.caixa.loterias.feature.account.domain.repository.SessionSignOutRepository
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider

/** A confirmação SILCE deve preceder a exclusão local de credenciais e o encerramento da sessão SSO. */
class SignOutSession(
    private val identities: AuthenticatedSessionIdentityProvider,
    private val repository: SessionSignOutRepository,
) : SignOutSessionUseCase {
    override suspend fun invoke(): SessionSignOutResult {
        val identity = identities.captureIdentity() ?: return staleSession()
        val result = repository.signOut(identity)
        return if (identities.isCurrent(identity)) result else staleSession()
    }

    private fun staleSession() = SessionSignOutResult.Rejected(
        ServiceFailure.ConnectionFailed(ServiceConnectionFailure.INVALID_RESPONSE),
    )
}
