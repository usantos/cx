package br.gov.caixa.loterias.core.domain.results

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.results.domain.model.DrawPayout
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

class ResultsModelsTest {
    @Test
    fun `draw result and payout expose immutable values`() {
        val payout = DrawPayout("Sena", 1, "100")
        val result = DrawResult(
            "Mega-Sena", 10, "01/01", "Cidade - UF", listOf(1), listOf(2), listOf(3), true,
            "200", "300", "Aviso", listOf(payout),
        )
        assertEquals("Mega-Sena", result.modality)
        assertEquals(10, result.contest)
        assertEquals("01/01", result.drawDate)
        assertEquals("Cidade - UF", result.drawLocation)
        assertEquals(listOf(1), result.firstDrawNumbers)
        assertEquals(listOf(2), result.secondDrawNumbers)
        assertEquals(listOf(3), result.clovers)
        assertTrue(result.accumulated)
        assertEquals("200", result.accumulatedNextContest)
        assertEquals("300", result.totalRevenue)
        assertEquals("Aviso", result.notice)
        assertEquals(listOf(payout), result.payouts)
        assertEquals("Sena", payout.description)
        assertEquals(1, payout.winners)
        assertEquals("100", payout.value)
        assertEquals(result, result.copy())
        assertNotEquals(result, result.copy(contest = 11))
        assertEquals(result.hashCode(), result.copy().hashCode())
        assertTrue(result.toString().contains("Mega-Sena"))
    }
}
