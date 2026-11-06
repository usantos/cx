package br.gov.caixa.loterias.core.domain.contract.cart.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart

fun interface GetCurrentCartUseCase {
    suspend operator fun invoke(): AppResult<ShoppingCart>
}
