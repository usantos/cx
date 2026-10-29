package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft

fun interface AddBetToCartUseCase {
    suspend operator fun invoke(draft: BetDraft): AppResult<ShoppingCart>
}
