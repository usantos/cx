package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart

fun interface GenerateRapidBetsUseCase {
    suspend operator fun invoke(quantity: Int): ServiceResult<ShoppingCart>
}
