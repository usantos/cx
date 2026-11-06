package br.gov.caixa.loterias.core.domain.contract.account.model

import br.gov.caixa.loterias.core.domain.contract.account.model.AccountNotification
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart

data class AuthenticatedSessionContent(
    val cart: ShoppingCart?,
    val latestNotification: AccountNotification?,
    val unidentifiedPayment: Boolean,
    val localCartReviewRequired: Boolean = false,
) {
    override fun toString() = "AuthenticatedSessionContent(redacted)"
}
