package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.domain.retail.PrizeRedirect
import br.gov.caixa.loterias.core.domain.service.ServiceRedirect

internal fun prizeRedirectRoute(destination: PrizeRedirect): String = serviceRedirectRoute(destination)

internal fun serviceRedirectRoute(destination: ServiceRedirect): String = when (destination) {
    PrizeRedirect.LOGIN -> LoginNavigation.authorize
    PrizeRedirect.ACCEPT_TERMS -> "support/terms-acceptance"
    PrizeRedirect.REGISTER_ACCOUNT -> "account/register"
    PrizeRedirect.UNAVAILABLE -> "onboarding/unavailable"
    PrizeRedirect.CART -> "cart/current"
    PrizeRedirect.FAVORITE_CARTS -> "cart/favorite-carts"
    PrizeRedirect.HOME -> "home"
}
