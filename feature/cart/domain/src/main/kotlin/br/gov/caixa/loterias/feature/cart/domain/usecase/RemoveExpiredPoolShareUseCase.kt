package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart

/** A expiração automática pertence ao login que exibiu a reserva. */
fun interface RemoveExpiredPoolShareUseCase {
    suspend operator fun invoke(item: CartItem, identity: AuthenticatedSessionIdentity): AppResult<ShoppingCart>
}
