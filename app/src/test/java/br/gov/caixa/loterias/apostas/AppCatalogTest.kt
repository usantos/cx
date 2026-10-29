package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.navigation.workflow.nextDestination
import br.gov.caixa.loterias.core.navigation.workflow.workflowNextDestinations
import br.gov.caixa.loterias.feature.account.presentation.screen.accountScreens
import br.gov.caixa.loterias.feature.betting.presentation.screen.bettingScreens
import br.gov.caixa.loterias.feature.cart.presentation.screen.cartScreens
import br.gov.caixa.loterias.feature.notifications.presentation.screen.notificationScreens
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.onboardingScreens
import br.gov.caixa.loterias.feature.payment.presentation.screen.paymentScreens
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.responsibleGamingScreens
import br.gov.caixa.loterias.feature.results.presentation.screen.resultScreens
import br.gov.caixa.loterias.feature.retail.presentation.screen.retailScreens
import br.gov.caixa.loterias.feature.support.presentation.screen.supportScreens
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ticketScreens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppCatalogTest {
    @Test fun `all migrated screen routes are unique and documented`() {
        val screens = listOf(
            onboardingScreens {
                it.toString()
            },
            accountScreens {
                it.toString()
            },
            bettingScreens { it.toString() }, cartScreens { it.toString() }, paymentScreens { it.toString() },
            ticketScreens {
                it.toString()
            },
            resultScreens { it.toString() }, responsibleGamingScreens { it.toString() }, notificationScreens { it.toString() },
            retailScreens { it.toString() }, supportScreens { it.toString() }
        ).flatten()
        assertEquals(76, screens.size)
        assertEquals(screens.size, screens.map { it.route }.distinct().size)
        assertTrue(screens.all { it.route.isNotBlank() && it.title.isNotBlank() && it.description.isNotBlank() })
    }

    @Test fun `workflow destinations point to known screens`() {
        val routes = listOf(
            onboardingScreens {
                it.toString()
            },
            accountScreens {
                it.toString()
            },
            bettingScreens { it.toString() }, cartScreens { it.toString() }, paymentScreens { it.toString() },
            ticketScreens {
                it.toString()
            },
            resultScreens { it.toString() }, responsibleGamingScreens { it.toString() }, notificationScreens { it.toString() },
            retailScreens { it.toString() }, supportScreens { it.toString() }
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
