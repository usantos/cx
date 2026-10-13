package br.gov.caixa.loterias.core.domain.cart

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.domain.account.PostLoginDestination
import br.gov.caixa.loterias.core.domain.account.VerifyAccountAccessUseCase

sealed interface CartPaymentAccountDecision {
    data object Allowed : CartPaymentAccountDecision
    data object LoginRequired : CartPaymentAccountDecision
    data object Suspended : CartPaymentAccountDecision
    data class CompleteAccount(val destination: PostLoginDestination) : CartPaymentAccountDecision
}

fun interface VerifyCartPaymentAccountUseCase {
    suspend operator fun invoke(): AppResult<CartPaymentAccountDecision>
}

class VerifyCartPaymentAccount(
    private val isAuthenticated: () -> Boolean,
    private val verifyAccess: VerifyAccountAccessUseCase,
) : VerifyCartPaymentAccountUseCase {
    override suspend fun invoke(): AppResult<CartPaymentAccountDecision> {
        if (!isAuthenticated()) return AppResult.Success(CartPaymentAccountDecision.LoginRequired)
        val access = when (val result = verifyAccess()) {
            is AppResult.Success -> result.value
            is AppResult.Failure -> return result
        }
        // The legacy cart handles an unfinished registration before checking suspension.
        return AppResult.Success(when {
            access.destination == PostLoginDestination.REGISTRATION -> CartPaymentAccountDecision.CompleteAccount(access.destination)
            access.temporarySuspension -> CartPaymentAccountDecision.Suspended
            access.destination != PostLoginDestination.HOME -> CartPaymentAccountDecision.CompleteAccount(access.destination)
            else -> CartPaymentAccountDecision.Allowed
        })
    }
}
