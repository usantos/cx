package br.gov.caixa.loterias.apostas

import org.junit.Assert.*
import org.junit.Test

class VisitorNavigationTest {
    @Test fun `private drawer destinations require login and public tools remain available`() {
        listOf("account/personal-data", "tickets/my-bets", "tickets/purchases", "betting/favorites", "cart/favorite-carts", "payment/cards", "responsible/form").forEach { assertTrue(it, requiresVisitorLogin(it)) }
        listOf("results/modalities", "tickets/read", "responsible/start", "responsible/suspension", "responsible/social", "support/terms", "support/terms-view", "support", "cart/current", "betting/simulation/2").forEach { assertFalse(it, requiresVisitorLogin(it)) }
        assertTrue(requiresVisitorLogin("tickets/my-bets?filter=all"))
    }

    @Test fun `suspension asks for login only when submitting`() {
        assertTrue(requiresVisitorLoginOnSubmit("responsible/suspension"))
        assertTrue(requiresVisitorLoginOnSubmit("responsible/suspension?source=drawer"))
        assertFalse(requiresVisitorLoginOnSubmit("responsible/start"))
    }

    @Test fun `personal data keeps its destination visible behind the visitor login prompt`() {
        assertTrue(opensVisitorDestinationBeforeLogin("account/personal-data"))
        assertTrue(opensVisitorDestinationBeforeLogin("account/personal-data?source=drawer"))
        assertFalse(opensVisitorDestinationBeforeLogin("tickets/my-bets"))
    }
}
