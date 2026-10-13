package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.domain.retail.PrizeRedirect
import org.junit.Assert.assertEquals
import org.junit.Test

class PrizeRedirectNavigationTest {
    @Test fun `every typed destination has the existing application route`() {
        val expected = mapOf(
            PrizeRedirect.LOGIN to LoginNavigation.authorize,
            PrizeRedirect.ACCEPT_TERMS to "support/terms-acceptance",
            PrizeRedirect.REGISTER_ACCOUNT to "account/register",
            PrizeRedirect.UNAVAILABLE to "onboarding/unavailable",
            PrizeRedirect.CART to "cart/current",
            PrizeRedirect.FAVORITE_CARTS to "cart/favorite-carts",
            PrizeRedirect.HOME to "home",
        )
        assertEquals(PrizeRedirect.entries.toSet(), expected.keys)
        expected.forEach { (destination, route) -> assertEquals(route, prizeRedirectRoute(destination)) }
    }
}
