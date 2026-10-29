package br.gov.caixa.loterias.feature.account.domain.model

import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart

data class AuthenticatedSessionContent(
    val cart: ShoppingCart?,
    val latestNotification: AccountNotification?,
    val unidentifiedPayment: Boolean,
    val localCartReviewRequired: Boolean = false,
) {
    override fun toString() = "AuthenticatedSessionContent(redacted)"
}
