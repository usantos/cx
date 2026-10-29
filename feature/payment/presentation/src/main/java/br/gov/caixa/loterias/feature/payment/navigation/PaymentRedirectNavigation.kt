package br.gov.caixa.loterias.feature.payment.navigation

import br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect

fun paymentRedirectRoute(destination: PurchaseRedirect): String = when (destination) {
    PurchaseRedirect.LOGIN -> LoginNavigation.authorize
    PurchaseRedirect.ACCEPT_TERMS -> "support/terms-acceptance"
    PurchaseRedirect.REGISTER_ACCOUNT -> "account/register"
    PurchaseRedirect.UNAVAILABLE -> "onboarding/unavailable"
    PurchaseRedirect.CART -> "cart/current"
    PurchaseRedirect.FAVORITE_CARTS -> "cart/favorite-carts"
}
