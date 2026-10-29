package br.gov.caixa.loterias.core.domain.betting

import java.time.Instant
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.PoolModalityAvailability
import br.gov.caixa.loterias.feature.betting.domain.model.isPoolOnlyWindow

class PoolResaleWindowTest {
    private val game = LotteryGameParameters(LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
        3000, "", 60, 6, 20, 10, emptyList(), emptyList(),
        closingDateTime = "06/09/2026 18:00:00")
    private val pool = PoolModalityAvailability(2, "MEGA_SENA", game.contestType, 3000, "06/09/2026", "19:00:00")
    private val start = Instant.parse("2026-09-06T21:00:00Z")

    @Test fun `both closing boundaries are inclusive`() {
        listOf(0L, 1L, 3599999L, 3600000L).forEach {
            assertTrue(isPoolOnlyWindow(game, listOf(pool), true, start.plusMillis(it)))
        }
        listOf(-1L, 3600001L).forEach {
            assertFalse(isPoolOnlyWindow(game, listOf(pool), true, start.plusMillis(it)))
        }
    }

    @Test fun `flag and complete contest identity must match`() {
        assertFalse(isPoolOnlyWindow(game, listOf(pool), false, start))
        assertFalse(isPoolOnlyWindow(game, emptyList(), true, start))
        listOf(pool.copy(modalityId = 1), pool.copy(contestType = "OUTRO"),
            pool.copy(contestNumber = 3001), pool.copy(contestNumber = null)).forEach {
            assertFalse(isPoolOnlyWindow(game, listOf(it), true, start))
            assertTrue(isPoolOnlyWindow(game, listOf(it, pool), true, start))
        }
        assertTrue(isPoolOnlyWindow(game, listOf(pool.copy(contestType = game.contestType.lowercase())), true, start))
    }

    @Test fun `missing invalid or inverted dates never manufacture a window`() {
        listOf("", "invalid", "31/02/2026 18:00:00").forEach {
            assertFalse(isPoolOnlyWindow(game.copy(closingDateTime = it), listOf(pool), true, start))
        }
        listOf(pool.copy(resaleClosingDate = ""), pool.copy(resaleClosingTime = ""),
            pool.copy(resaleClosingDate = "31/02/2026"), pool.copy(resaleClosingTime = "17:00:00")).forEach {
            assertFalse(isPoolOnlyWindow(game, listOf(it), true, start))
        }
    }
}
