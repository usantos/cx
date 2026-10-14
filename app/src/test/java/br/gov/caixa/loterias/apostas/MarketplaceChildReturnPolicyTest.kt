package br.gov.caixa.loterias.apostas

import org.junit.Assert.*
import org.junit.Test

class MarketplaceChildReturnPolicyTest {
    @Test fun successfulReservationClosesDetailInsteadOfReusingItsSnapshot() {
        assertTrue(shouldClosePoolDetailAfterChild("betting/pool-detail/{poolCode}?favoriteVisible={favoriteVisible}", true))
        assertTrue(shouldClosePoolDetailAfterChild("betting/pool-detail/B123", true))
    }
    @Test fun addingNewBetFinishesCartAndOnlyPurchaseDetailExplicitlyNavigatesHome() {
        assertEquals(CartNewBetReturn.Parent, cartNewBetReturn("betting/pools"))
        assertEquals(CartNewBetReturn.Parent, cartNewBetReturn("betting/pool-view"))
        assertEquals(CartNewBetReturn.Parent, cartNewBetReturn("betting/pool-detail/{poolCode}?favoriteVisible={favoriteVisible}"))
        assertEquals(CartNewBetReturn.Parent, cartNewBetReturn("home"))
        assertEquals(CartNewBetReturn.Home, cartNewBetReturn("tickets/purchase-detail"))
    }
    @Test fun unchangedCartAndOtherParentsReturnNormally() {
        assertFalse(shouldClosePoolDetailAfterChild("betting/pool-detail/B123", false))
        assertFalse(shouldClosePoolDetailAfterChild("betting/pools", true))
        assertFalse(shouldClosePoolDetailAfterChild("cart/pool-included", true))
        assertFalse(shouldClosePoolDetailAfterChild(null, true))
    }
}
