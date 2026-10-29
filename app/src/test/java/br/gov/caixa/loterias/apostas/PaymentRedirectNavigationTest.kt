package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect
import br.gov.caixa.loterias.feature.payment.navigation.paymentRedirectRoute
import org.junit.Assert.assertEquals
import org.junit.Test

class PaymentRedirectNavigationTest {
    @Test fun `only known destinations map to internal purchase routes`() {
        assertEquals(
            listOf("account/login?authorize=true", "support/terms-acceptance", "account/register", "onboarding/unavailable", "cart/current", "cart/favorite-carts"),
            PurchaseRedirect.entries.map(::paymentRedirectRoute),
        )
        assertEquals(LoginNavigation.authorize, paymentRedirectRoute(PurchaseRedirect.LOGIN))
    }
}
