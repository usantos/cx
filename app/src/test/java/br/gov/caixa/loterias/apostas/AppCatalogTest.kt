package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.feature.account.accountScreens
import br.gov.caixa.loterias.feature.betting.bettingScreens
import br.gov.caixa.loterias.feature.cart.cartScreens
import br.gov.caixa.loterias.feature.notifications.notificationScreens
import br.gov.caixa.loterias.feature.onboarding.onboardingScreens
import br.gov.caixa.loterias.feature.payment.paymentScreens
import br.gov.caixa.loterias.feature.responsiblegaming.responsibleGamingScreens
import br.gov.caixa.loterias.feature.results.resultScreens
import br.gov.caixa.loterias.feature.retail.retailScreens
import br.gov.caixa.loterias.feature.tickets.ticketScreens
import br.gov.caixa.loterias.feature.support.supportScreens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppCatalogTest {
    @Test fun `all migrated screen routes are unique and documented`() {
        val screens = listOf(
            onboardingScreens, accountScreens, bettingScreens, cartScreens, paymentScreens,
            ticketScreens, resultScreens, responsibleGamingScreens, notificationScreens,
            retailScreens, supportScreens,
        ).flatten()
        assertEquals(76, screens.size)
        assertEquals(screens.size, screens.map { it.route }.distinct().size)
        assertTrue(screens.all { it.route.isNotBlank() && it.title.isNotBlank() && it.description.isNotBlank() })
    }

    @Test fun `workflow destinations point to known screens`() {
        val routes = listOf(
            onboardingScreens, accountScreens, bettingScreens, cartScreens, paymentScreens,
            ticketScreens, resultScreens, responsibleGamingScreens, notificationScreens,
            retailScreens, supportScreens,
        ).flatten().map { it.route }.toSet() + "home"

        assertTrue(workflowNextDestinations.keys.all { it in routes })
        assertTrue(workflowNextDestinations.values.all { it in routes })
        assertEquals("onboarding/introduction", nextDestination("onboarding/splash"))
        assertEquals("account/login", nextDestination("onboarding/introduction"))
        assertEquals("payment/pix-copy", nextDestination("payment/pix-guidance"))
        assertEquals("payment/confirmation", nextDestination("payment/pix-copy"))
        assertEquals("tickets/purchases", nextDestination("payment/confirmation"))
        assertEquals("tickets/purchases", nextDestination("payment/success"))
        assertEquals(null, nextDestination("unknown"))
    }
}
