package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult

fun interface GenerateRapidBetsUseCase {
    suspend operator fun invoke(quantity: Int): ServiceResult<ShoppingCart>
}
