package br.gov.caixa.loterias.apostas

import org.junit.Assert.*
import org.junit.Test

class LoggedContinuationTest {
    @Test fun `logged fixtures are impossible outside an explicit debug mock request`() {
        for (debug in listOf(false, true)) for (mock in listOf(false, true)) for (request in listOf(false, true)) {
            assertEquals(debug && mock && request, shouldUseLoggedVisualFixture(debug, mock, request))
        }
    }
    @Test fun `restored route encodes values and sends payment to fresh cart`() {
        assertEquals("retail/prize/42", restoredLoggedRoute("retail/prize/{betId}") { "42" })
        assertEquals("tickets/bet-detail?variant=a%20b", restoredLoggedRoute("tickets/bet-detail?variant={variant}") { "a b" })
        assertEquals("cart/current", restoredLoggedRoute("payment/confirm-card") { null })
    }
    @Test fun `financial redirects return to a query rather than a submitting screen`() {
        assertEquals("cart/current", loggedContinuation("payment/confirm-card"))
        assertEquals("cart/current", loggedContinuation("cart/current"))
        assertEquals("retail/prize/41", loggedContinuation("retail/prize-pix", 41))
        assertEquals("tickets/my-bets", loggedContinuation("retail/prize-pix", null))
    }
    @Test fun `prize return survives only the originating authenticated account`() {
        assertEquals("retail/prize/100", authenticatedContinuation("retail/prize/100", "owner-a", "owner-a"))
        assertNull(authenticatedContinuation("retail/prize/100", "owner-a", "owner-b"))
        assertNull(authenticatedContinuation("retail/prize/100", null, "owner-b"))
        assertNull(authenticatedContinuation("retail/prize/100", "owner-a", null))
    }
    @Test fun `fresh cart and visitor entry are not bound to an earlier prize owner`() {
        assertEquals("cart/current", authenticatedContinuation("cart/current", "owner-a", "owner-b"))
        assertEquals("home/combo", authenticatedContinuation("home/combo", null, "owner-b"))
        assertNull(authenticatedContinuation(null, "owner-a", "owner-b"))
    }

}
