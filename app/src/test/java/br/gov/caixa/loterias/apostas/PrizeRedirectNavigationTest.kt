package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation
import br.gov.caixa.loterias.core.navigation.destination.serviceRedirectRoute
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import org.junit.Assert.assertEquals
import org.junit.Test

class PrizeRedirectNavigationTest {
    @Test fun `every typed destination has the existing application route`() {
        val expected = mapOf(
            ServiceRedirect.LOGIN to LoginNavigation.authorize,
            ServiceRedirect.ACCEPT_TERMS to "support/terms-acceptance",
            ServiceRedirect.REGISTER_ACCOUNT to "account/register",
            ServiceRedirect.UNAVAILABLE to "onboarding/unavailable",
            ServiceRedirect.CART to "cart/current",
            ServiceRedirect.FAVORITE_CARTS to "cart/favorite-carts",
            ServiceRedirect.HOME to "home",
        )
        assertEquals(ServiceRedirect.entries.toSet(), expected.keys)
        expected.forEach { (destination, route) -> assertEquals(route, serviceRedirectRoute(destination)) }
    }
}
