package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem

/** A expiração automática pertence ao login que exibiu a reserva. */
fun interface RemoveExpiredPoolShareUseCase {
    suspend operator fun invoke(item: CartItem, identity: AuthenticatedSessionIdentity): AppResult<ShoppingCart>
}
