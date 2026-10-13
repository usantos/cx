package br.gov.caixa.loterias.apostas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MockConfirmedBetFixturesTest {
    @Test fun `fixtures cover every confirmed bet presentation contract`() {
        val plus = mockConfirmedBet("plus")
        assertEquals(listOf(2, 5), plus.clovers)
        assertEquals(3, plus.recurringDraws)

        val superSeven = mockConfirmedBet("super-seven")
        assertEquals(7, superSeven.numberColumns.size)
        assertEquals(8, superSeven.numberColumns.flatten().size)

        assertEquals("Time Azul", mockConfirmedBet("timemania").favoriteTeam?.name)
        assertEquals("Setembro", mockConfirmedBet("dia-de-sorte").luckyMonth?.name)
        assertEquals(14, mockConfirmedBet("loteca").lotecaSelections.size)
        assertEquals(5, mockConfirmedBet("lotogol").lotogolSelections.size)
        assertTrue(mockConfirmedBet("pool").poolShare)
    }

    @Test fun `default fixture preserves teimosinha rescue scenario`() {
        val bet = mockConfirmedBet("", originalRecurringBet = true, redeemedPrize = true)
        assertTrue(bet.originalRecurringBet)
        assertTrue(bet.redeemedPrize)
        assertEquals(2L, bet.exchangeBetId)
        assertFalse(bet.poolShare)
    }
}
