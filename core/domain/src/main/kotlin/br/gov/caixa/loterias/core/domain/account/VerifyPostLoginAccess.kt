package br.gov.caixa.loterias.core.domain.account

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf

fun interface BlockedAccountVerificationRepository {
    /** Completes only when the authenticated blocked-account check succeeds. */
    suspend fun verifyBlockedAccount()
}

sealed interface PostLoginAccessResult {
    data class Verified(val access: AccountAccess) : PostLoginAccessResult
    data object BlockedAccountCheckFailed : PostLoginAccessResult
    data class AccountCheckFailed(val error: AppError) : PostLoginAccessResult
}

fun interface VerifyPostLoginAccessUseCase {
    suspend operator fun invoke(): PostLoginAccessResult
}

/** Post-login transport must use the captured session, not a later generic access token. */
fun interface PostLoginAccountRepository {
    suspend fun verifyAccess(identity: AuthenticatedSessionIdentity): AccountAccess
}

/** TokenActivity's ordered checks; not used by ordinary cart/betting account refreshes. */
class VerifyPostLoginAccess(
    private val blockedAccount: BlockedAccountVerificationRepository,
    private val account: PostLoginAccountRepository,
    private val sessions: AuthenticatedSessionIdentityProvider,
) : VerifyPostLoginAccessUseCase {
    override suspend fun invoke(): PostLoginAccessResult {
        val identity = sessions.captureIdentity() ?: return sessionExpired()
        if (!sessions.isCurrent(identity)) return sessionExpired()
        val blockedResult = appResultOf { blockedAccount.verifyBlockedAccount() }
        // A failed old request must not ask the UI to log out a replacement session.
        if (!sessions.isCurrent(identity)) return sessionExpired()
        when (blockedResult) {
            is AppResult.Failure -> return PostLoginAccessResult.BlockedAccountCheckFailed
            is AppResult.Success -> Unit
        }
        val result = appResultOf { account.verifyAccess(identity) }
        if (!sessions.isCurrent(identity)) return sessionExpired()
        return when (result) {
            is AppResult.Success -> PostLoginAccessResult.Verified(result.value)
            is AppResult.Failure -> PostLoginAccessResult.AccountCheckFailed(result.error)
        }
    }

    private fun sessionExpired() = PostLoginAccessResult.AccountCheckFailed(
        AppError.Validation("A sessão foi encerrada ou substituída. Entre novamente."),
    )
}
