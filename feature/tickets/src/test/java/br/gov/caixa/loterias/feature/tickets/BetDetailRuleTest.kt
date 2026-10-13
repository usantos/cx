package br.gov.caixa.loterias.feature.tickets

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test

class BetDetailRuleTest {
    @Test fun `shows alert only for original teimosinha with redemption`() {
        assertTrue(shouldShowInvalidTeimosinhaAlert(true, true))
        assertFalse(shouldShowInvalidTeimosinhaAlert(true, false))
        assertFalse(shouldShowInvalidTeimosinhaAlert(false, true))
        assertFalse(shouldShowInvalidTeimosinhaAlert(false, false))
    }

    @Test fun `builds typed bet detail route`() {
        assertEquals("tickets/bet-detail?original=true&resgate=false", betDetailRoute(true, false))
    }
}
