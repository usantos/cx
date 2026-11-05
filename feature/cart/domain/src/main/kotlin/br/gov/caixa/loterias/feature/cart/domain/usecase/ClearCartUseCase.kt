package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart

fun interface ClearCartUseCase {
    suspend operator fun invoke(): AppResult<ShoppingCart>
}
