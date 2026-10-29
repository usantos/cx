package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart

fun interface AddComboToCartUseCase {
    suspend operator fun invoke(comboId: Int, quantity: Int, surprise: Boolean): AppResult<ShoppingCart>
}
