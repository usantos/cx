package br.gov.caixa.loterias.core.navigation.destination

import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect

fun serviceRedirectRoute(destination: ServiceRedirect): String = when (destination) {
    ServiceRedirect.LOGIN -> "account/login?authorize=true"
    ServiceRedirect.ACCEPT_TERMS -> "support/terms-acceptance"
    ServiceRedirect.REGISTER_ACCOUNT -> "account/register"
    ServiceRedirect.UNAVAILABLE -> "onboarding/unavailable"
    ServiceRedirect.CART -> "cart/current"
    ServiceRedirect.FAVORITE_CARTS -> "cart/favorite-carts"
    ServiceRedirect.HOME -> "home"
}
