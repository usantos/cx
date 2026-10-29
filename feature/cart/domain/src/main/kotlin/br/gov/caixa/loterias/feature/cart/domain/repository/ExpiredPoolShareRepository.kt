package br.gov.caixa.loterias.feature.cart.domain.repository

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem

/** Remove uma reserva usando o login que a exibiu originalmente. */
fun interface ExpiredPoolShareRepository {
    suspend fun remove(item: CartItem, identity: AuthenticatedSessionIdentity): ShoppingCart
}
