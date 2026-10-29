package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.core.navigation.state.DetailRestorationOutcome
import br.gov.caixa.loterias.core.navigation.state.LoggedDetailContext
import br.gov.caixa.loterias.core.navigation.state.LoggedDetailContextCodec
import br.gov.caixa.loterias.feature.home.presentation.mapper.prize
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import java.math.BigDecimal
import org.junit.Assert.*
import org.junit.Test

class AppSessionStateTest {
    private fun pendingDetails(): LoggedDetailContext {
        val original = LoggedDetailContext("owner").apply {
            selectedPurchase = Purchase(17, "17/10/2026", "Paga", "Pix", BigDecimal.TEN)
        }
        return requireNotNull(LoggedDetailContextCodec.defer(requireNotNull(LoggedDetailContextCodec.save(original))))
    }

    @Test fun sameOwnerRestoresPrivateDestinationAfterAuthentication() {
        val details = pendingDetails()
        val state = AppSessionState().apply { pendingRestoredRoute = "tickets/purchase-detail" }

        assertEquals("tickets/purchase-detail", state.authenticated("home", "owner", details))
        assertEquals(17L, details.selectedPurchase?.id)
        assertNull(state.pendingRestoredRoute)
        assertNull(state.visitorReturnRoute)
        assertNull(state.visitorReturnOwnerKey)
        assertFalse(state.accountCompletionRequired)
    }

    @Test fun anotherOwnerCannotResumeRestoredPrivateDetails() {
        val details = pendingDetails()
        val state = AppSessionState().apply { pendingRestoredRoute = "tickets/purchase-detail" }

        assertEquals("home", state.authenticated("home", "other", details))
        assertNull(details.selectedPurchase)
        assertEquals(DetailRestorationOutcome.Discarded, details.restorationOutcome)
        assertNull(state.pendingRestoredRoute)
        assertNull(state.visitorReturnRoute)
    }

    @Test fun accountCompletionKeepsPendingRestorationUntilHomeIsAllowed() {
        val details = pendingDetails()
        val state = AppSessionState().apply { pendingRestoredRoute = "tickets/purchase-detail" }

        assertEquals("account/register", state.authenticated("account/register", "owner", details))
        assertTrue(state.accountCompletionRequired)
        assertEquals("tickets/purchase-detail", state.pendingRestoredRoute)
        assertEquals("tickets/purchase-detail", state.authenticated("home", "owner", details))
    }

    @Test fun homeActionIsQueuedInsteadOfUsedAsADestination() {
        val state = AppSessionState().apply { visitorReturnRoute = "home/pool/2/NORMAL" }

        assertEquals("home", state.authenticated("home", "owner", LoggedDetailContext("owner")))
        assertEquals("home/pool/2/NORMAL", state.resumedHomeAction)
        assertNull(state.visitorReturnRoute)
        assertNull(state.visitorReturnOwnerKey)
    }

    @Test fun comboLoginReturnsHomeWithoutQueuingAnAutomaticCatalogEntry() {
        val state = AppSessionState().apply { visitorReturnRoute = "home/combo" }

        assertEquals("home", state.authenticated("home", "owner", LoggedDetailContext("owner")))
        assertNull(state.resumedHomeAction)
        assertNull(state.visitorReturnRoute)
    }

    @Test fun continuationBoundToAnotherOwnerIsDiscarded() {
        val state = AppSessionState().apply {
            visitorReturnRoute = "retail/prize/42"
            visitorReturnOwnerKey = "previous"
        }

        assertEquals("home", state.authenticated("home", "owner", LoggedDetailContext("owner")))
        assertNull(state.visitorReturnRoute)
        assertNull(state.resumedHomeAction)
    }

    @Test fun visitorEntryClearsRestorationAndAuthenticatedContinuation() {
        val details = pendingDetails()
        val state = AppSessionState().apply {
            pendingRestoredRoute = "tickets/purchase-detail"
            visitorReturnRoute = "cart/current"
            visitorReturnOwnerKey = "owner"
            resumedHomeAction = "home/rapid"
            accountCompletionRequired = true
        }

        state.clearForVisitor(details)

        assertEquals(DetailRestorationOutcome.None, details.restorationOutcome)
        assertNull(details.pendingSnapshot)
        assertNull(state.pendingRestoredRoute)
        assertNull(state.visitorReturnRoute)
        assertNull(state.visitorReturnOwnerKey)
        assertNull(state.resumedHomeAction)
        assertFalse(state.accountCompletionRequired)
    }
}
