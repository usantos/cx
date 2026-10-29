package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.navigation.workflow.opensVisitorDestinationBeforeLogin
import br.gov.caixa.loterias.core.navigation.workflow.requiresVisitorLogin
import br.gov.caixa.loterias.core.navigation.workflow.requiresVisitorLoginOnSubmit
import org.junit.Assert.*
import org.junit.Test

class VisitorNavigationTest {
    @Test fun `private drawer destinations require login and public tools remain available`() {
        listOf("account/personal-data", "tickets/my-bets", "tickets/purchases", "betting/favorites", "cart/favorite-carts", "payment/cards", "responsible/form").forEach { assertTrue(it, requiresVisitorLogin(it)) }
        listOf("results/modalities", "tickets/read", "responsible/start", "responsible/suspension", "responsible/social", "support/terms", "support/terms-view", "support", "cart/current", "betting/simulation/2").forEach { assertFalse(it, requiresVisitorLogin(it)) }
        assertTrue(requiresVisitorLogin("tickets/my-bets?filter=all"))
    }

    @Test fun `rapid destinations show their screen underneath visitor prompt`() {
        listOf("betting/rapid", "betting/rapid-list", "betting/rapid-config").forEach {
            assertTrue(it, requiresVisitorLogin(it))
            assertTrue(it, opensVisitorDestinationBeforeLogin(it))
        }
    }

    @Test fun `suspension asks for login only when submitting`() {
        assertTrue(requiresVisitorLoginOnSubmit("responsible/suspension"))
        assertTrue(requiresVisitorLoginOnSubmit("responsible/suspension?source=drawer"))
        assertFalse(requiresVisitorLoginOnSubmit("responsible/start"))
    }

    @Test fun `personal data keeps its destination visible behind the visitor login prompt`() {
        assertTrue(opensVisitorDestinationBeforeLogin("account/personal-data"))
        assertTrue(opensVisitorDestinationBeforeLogin("account/personal-data?source=drawer"))
        listOf("tickets/my-bets", "tickets/purchases", "betting/favorites", "cart/favorite-carts", "payment/cards?methodId=1", "responsible/form").forEach { assertTrue(it, opensVisitorDestinationBeforeLogin(it)) }
    }
}
