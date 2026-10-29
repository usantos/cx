package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision

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
        // O carrinho legado trata um cadastro inacabado antes de verificar a suspensão.
        return AppResult.Success(when {
            access.destination == PostLoginDestination.REGISTRATION -> CartPaymentAccountDecision.CompleteAccount(access.destination)
            access.temporarySuspension -> CartPaymentAccountDecision.Suspended
            access.destination != PostLoginDestination.HOME -> CartPaymentAccountDecision.CompleteAccount(access.destination)
            else -> CartPaymentAccountDecision.Allowed
        })
    }
}
