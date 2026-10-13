package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.core.domain.retail.PrizeAward
import br.gov.caixa.loterias.core.domain.retail.PrizeDetails
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test

class PrizeResultRulesTest {
    private val details = PrizeDetails(7, BigDecimal("100.50"), "", emptyList())

    @Test fun `closing date keeps legacy day month and hour display without fabricating malformed dates`() {
        assertEquals("10/09 | 19h05", formatPrizeClosingDate("10/09/2026 19:05:00"))
        assertEquals("10/09 | 19h05", formatPrizeClosingDate("2026-09-10T19:05:00"))
        assertEquals("", formatPrizeClosingDate(""))
        assertEquals("", formatPrizeClosingDate("not a date"))
    }

    @Test fun `bet type strip is absent for ordinary bets and non positive recurrence`() {
        assertEquals(emptyList<PrizeBetType>(), prizeBetTypes(details))
        assertEquals(emptyList<PrizeBetType>(), prizeBetTypes(details.copy(recurringDraws = -1)))
    }

    @Test fun `bet type strip preserves legacy labels and insertion order`() {
        val bet = details.copy(exchange = true, isPool = true, combo = true, mirror = true, surprise = true, recurringDraws = 1)
        assertEquals(
            listOf("Aposta de Troca", "Bolão", "Combo", "Espelho", "Surpresinha", "Teimosinha"),
            prizeBetTypes(bet).map { it.label },
        )
        assertEquals(listOf(PrizeBetType.SURPRISE, PrizeBetType.RECURRING),
            prizeBetTypes(details.copy(surprise = true, recurringDraws = 2)))
    }

    @Test fun `individual prizes have contest status range quantity draw and amount columns`() {
        val award = PrizeAward(3100, "Quadra", BigDecimal("25"), "Premiada", 2, 1)
        assertEquals(
            listOf("3100", "Premiada", "Quadra", "2", "1", "R$ 25,00"),
            prizeAwardCells(details, award, 0),
        )
    }

    @Test fun `non winning contest has dashes rather than fabricated zero prize and quantity`() {
        val award = PrizeAward(3101, "", null, "Não premiada")
        assertEquals(
            listOf("3101", "Não premiada", "-", "-", "-", "-"),
            prizeAwardCells(details, award, 1),
        )
    }

    @Test fun `pool total appears only on first row and zero quota remains zero`() {
        val pool = details.copy(isPool = true)
        val award = PrizeAward(3100, "Quina", BigDecimal.ZERO)
        assertEquals(listOf("Quina", "R$ 0,00", "R$ 100,50"), prizeAwardCells(pool, award, 0))
        assertEquals(listOf("Quina", "R$ 0,00", ""), prizeAwardCells(pool, award, 1))
        assertEquals(listOf("-", "-", ""), prizeAwardCells(pool, PrizeAward(3101, "", null), 2))
    }
}
