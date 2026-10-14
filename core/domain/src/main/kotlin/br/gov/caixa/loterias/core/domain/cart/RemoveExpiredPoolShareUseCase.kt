package br.gov.caixa.loterias.core.domain.cart

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity

/** Automatic expiry belongs to the login that displayed the reservation. */
fun interface RemoveExpiredPoolShareUseCase {
    suspend operator fun invoke(item: CartItem, identity: AuthenticatedSessionIdentity): AppResult<ShoppingCart>
}
