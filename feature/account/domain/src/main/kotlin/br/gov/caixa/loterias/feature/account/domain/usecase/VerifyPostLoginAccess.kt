package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.core.domain.contract.account.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginAccessResult
import br.gov.caixa.loterias.feature.account.domain.repository.BlockedAccountVerificationRepository
import br.gov.caixa.loterias.feature.account.domain.repository.PostLoginAccountRepository

/** Verificações ordenadas de TokenActivity; não é usado nas atualizações comuns de conta do carrinho ou das apostas. */
class VerifyPostLoginAccess(
    private val blockedAccount: BlockedAccountVerificationRepository,
    private val account: PostLoginAccountRepository,
    private val sessions: AuthenticatedSessionIdentityProvider,
) : VerifyPostLoginAccessUseCase {
    override suspend fun invoke(): PostLoginAccessResult {
        val identity = sessions.captureIdentity() ?: return sessionExpired()
        if (!sessions.isCurrent(identity)) return sessionExpired()
        val blockedResult = appResultOf { blockedAccount.verifyBlockedAccount() }
        // Uma requisição antiga malsucedida não deve pedir à interface que encerre uma sessão substituta.
        if (!sessions.isCurrent(identity)) return sessionExpired()
        when (blockedResult) {
            is AppResult.Failure -> return PostLoginAccessResult.BlockedAccountCheckFailedForSession(identity)
            is AppResult.Success -> Unit
        }
        val result = appResultOf { account.verifyAccess(identity) }
        if (!sessions.isCurrent(identity)) return sessionExpired()
        return when (result) {
            is AppResult.Success -> PostLoginAccessResult.Verified(result.value, identity)
            is AppResult.Failure -> PostLoginAccessResult.AccountCheckFailed(result.error, identity)
        }
    }

    private fun sessionExpired() = PostLoginAccessResult.SessionSuperseded
}
