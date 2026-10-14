package br.gov.caixa.loterias.core.domain.account

import br.gov.caixa.loterias.core.domain.service.ServiceConnectionFailure
import br.gov.caixa.loterias.core.domain.service.ServiceFailure

sealed interface SessionSignOutResult {
    data class Confirmed(val identity: AuthenticatedSessionIdentity) : SessionSignOutResult
    data class Rejected(val failure: ServiceFailure) : SessionSignOutResult
}

fun interface SessionSignOutRepository {
    suspend fun signOut(identity: AuthenticatedSessionIdentity): SessionSignOutResult
}

fun interface SignOutSessionUseCase {
    suspend operator fun invoke(): SessionSignOutResult
}

/** SILCE confirmation must precede local credential deletion and SSO end-session. */
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
