package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult

fun interface ValidateCartUseCase {
    suspend operator fun invoke(): AppResult<CartValidationResult>
}
