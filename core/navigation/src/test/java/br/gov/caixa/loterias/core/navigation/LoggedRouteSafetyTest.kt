// Literais numéricos descrevem payloads sintéticos, níveis de API e limites esperados das fixtures.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.navigation

import br.gov.caixa.loterias.core.navigation.state.authenticatedContinuation
import br.gov.caixa.loterias.core.navigation.state.loggedContinuation
import br.gov.caixa.loterias.core.navigation.state.postLoginDestinationRoute
import br.gov.caixa.loterias.core.navigation.state.restoredLoggedRoute
import br.gov.caixa.loterias.core.navigation.state.shouldUseLoggedVisualFixture
import br.gov.caixa.loterias.core.navigation.state.ticketQueryContinuation
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LoggedRouteSafetyTest {
    @Test fun `financial continuation reloads cart while prize details require a positive id`() {
        assertEquals("cart/current", loggedContinuation("payment/submit"))
        assertEquals("cart/current", loggedContinuation("cart/payment"))
        assertEquals("retail/prize/9", loggedContinuation("retail/prize/confirm", 9))
        assertEquals("tickets/my-bets", loggedContinuation("retail/prize", 0))
        assertEquals("home", loggedContinuation("account/private"))
        assertEquals("tickets/my-bets", ticketQueryContinuation())
    }

    @Test fun `route restoration URL encodes each argument and rejects financial screen restoration`() {
        assertEquals("item/a%20b%2Fc?value=x%26y", restoredLoggedRoute("item/{id}?value={value}") { if (it == "id") "a b/c" else "x&y" })
        assertEquals("item/", restoredLoggedRoute("item/{id}") { null })
        assertEquals("cart/current", restoredLoggedRoute("payment/{step}") { "submit" })
        assertEquals("account/register", postLoginDestinationRoute(PostLoginDestination.REGISTRATION))
        assertEquals("support/terms-acceptance", postLoginDestinationRoute(PostLoginDestination.TERMS))
        assertEquals("responsible/start/required", postLoginDestinationRoute(PostLoginDestination.SELF_ASSESSMENT))
        assertEquals("home", postLoginDestinationRoute(PostLoginDestination.HOME))
    }

    @Test fun `private prize continuation requires same authenticated owner and combo never resumes submission`() {
        assertNull(authenticatedContinuation("home/combo", "owner", "owner"))
        assertNull(authenticatedContinuation("retail/prize/1", null, "owner"))
        assertNull(authenticatedContinuation("retail/prize/1", "old", "new"))
        assertEquals("retail/prize/1", authenticatedContinuation("retail/prize/1", "owner", "owner"))
        assertEquals("tickets/my-bets", authenticatedContinuation("tickets/my-bets", null, "owner"))
        for (debug in listOf(false, true)) {
            for (mock in listOf(false, true)) {
                for (requested in listOf(false, true)) {
                    assertEquals(debug && mock && requested, shouldUseLoggedVisualFixture(debug, mock, requested))
                }
            }
        }
    }
}
