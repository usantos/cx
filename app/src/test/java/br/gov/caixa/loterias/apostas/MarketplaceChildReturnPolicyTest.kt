package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.navigation.policy.shouldClosePoolDetailAfterChild
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MarketplaceChildReturnPolicyTest {
    @Test fun successfulReservationClosesDetailInsteadOfReusingItsSnapshot() {
        assertTrue(shouldClosePoolDetailAfterChild("betting/pool-detail/{poolCode}?favoriteVisible={favoriteVisible}", true))
        assertTrue(shouldClosePoolDetailAfterChild("betting/pool-detail/B123", true))
    }

    @Test fun unchangedCartAndOtherParentsReturnNormally() {
        assertFalse(shouldClosePoolDetailAfterChild("betting/pool-detail/B123", false))
        assertFalse(shouldClosePoolDetailAfterChild("betting/pools", true))
        assertFalse(shouldClosePoolDetailAfterChild("cart/pool-included", true))
        assertFalse(shouldClosePoolDetailAfterChild(null, true))
    }
}
