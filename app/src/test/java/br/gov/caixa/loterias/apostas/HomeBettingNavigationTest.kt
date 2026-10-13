package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.domain.betting.HomeBettingAction
import br.gov.caixa.loterias.core.domain.betting.HomeBettingTarget
import br.gov.caixa.loterias.core.domain.configuration.HomeFeatureConfiguration
import org.junit.Assert.*
import org.junit.Test

class HomeBettingNavigationTest {
    @Test fun `only betting shortcuts are intercepted and rapid respects its flag`() {
        listOf("betting/rapid", "betting/rapid-list").forEach {
            assertEquals(HomeBettingAction.RAPID, homeBettingAction(it))
            assertFalse(homeBettingRouteVisible(it, HomeFeatureConfiguration()))
            assertTrue(homeBettingRouteVisible(it, HomeFeatureConfiguration(rapidBettingEnabled = true)))
        }
        assertEquals(HomeBettingAction.COMBO, homeBettingAction("betting/combos"))
        assertNull(homeBettingAction("tickets/my-bets"))
        assertTrue(homeBettingRouteVisible("tickets/my-bets", HomeFeatureConfiguration()))
    }

    @Test fun `decisions map to legacy destinations`() {
        assertEquals("betting/combos", homeBettingDestination(HomeBettingTarget.COMBOS))
        assertEquals("https://instantanea.caixa.gov.br", homeBettingDestination(HomeBettingTarget.INSTANT))
        assertEquals("betting/rapid", homeBettingDestination(HomeBettingTarget.RAPID_CONFIGURATION))
        assertEquals("betting/rapid-list", homeBettingDestination(HomeBettingTarget.RAPID_LIST))
    }
}
