package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.navigation.destination.BettingSimulationRoute
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import org.junit.Assert.assertEquals
import org.junit.Test

class BettingSimulationRouteTest {
    private val game = LotteryGameParameters(LotteryModality(2, "Mega-Sena", "MEGA_SENA"), 3000,
        "", 60, 6, 20, 10, emptyList(), emptyList())

    @Test fun `normal and special navigation carry the validated number`() {
        assertEquals("betting/simulation/2?contestTypeId=1&contestNumber=3000", BettingSimulationRoute.from(game))
        assertEquals("betting/simulation/2?contestTypeId=2&contestNumber=3001",
            BettingSimulationRoute.from(game.copy(contestType = "especial", contestNumber = 3001)))
        assertEquals("betting/simulation/{modalityId}?contestTypeId={contestTypeId}&contestNumber={contestNumber}",
            BettingSimulationRoute.pattern)
    }
}
