package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision

fun interface VerifyCartPaymentAccountUseCase {
    suspend operator fun invoke(): AppResult<CartPaymentAccountDecision>
}
