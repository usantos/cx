package br.gov.caixa.loterias.feature.cart.domain.repository

import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart

/** Remove uma reserva usando o login que a exibiu originalmente. */
fun interface ExpiredPoolShareRepository {
    suspend fun remove(item: CartItem, identity: AuthenticatedSessionIdentity): ShoppingCart
}
