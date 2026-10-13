package br.gov.caixa.loterias.core.preferences

import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.cart.*
import java.math.BigDecimal
import org.junit.Assert.*
import org.junit.Test

class GuestCartRowsTest {
    private fun game(id: Int, name: String) = LotteryGameParameters(LotteryModality(id, name, ""), 3000, "", 60, 6, 20, 10, emptyList(), listOf(BetPrice(6, 2, BigDecimal("6.00"))))
    @Test fun `numeric simulator ignores its empty super seven state columns`() {
        val draft = BetDraft(2, numbers = (1..6).toList(), superSevenColumns = List(7) { emptyList() })
        val bet = decodeLegacyCart(guestCartRows(draft, game(2, "Mega-Sena"), BigDecimal.TEN, 1, 1)).single()
        assertEquals(LegacyCartNumbers.Flat((1..6).toList()), bet.numbers)
    }
    @Test fun `persisted graph keeps columns clovers contest and repeats for upload`() {
        val draft = BetDraft(7, numbers = emptyList(), superSevenColumns = List(7) { listOf(it) }, clovers = listOf(1, 2), recurringDraws = 3)
        val graphs = guestCartRows(draft, game(7, "Super Sete"), BigDecimal("18.00"), 1, 1)
        val bet = decodeLegacyCart(graphs).single()
        assertEquals(LegacyCartNumbers.Columns(draft.superSevenColumns), bet.numbers)
        assertEquals(draft.clovers, bet.clovers)
        assertEquals(3000, bet.targetContest)
        assertEquals(3, bet.repeatCount)
        assertEquals(BigDecimal("18.00"), bet.amount)
    }
    @Test fun `sports and complementary selections survive legacy decoding`() {
        val team = SportsTeam(10, "Time", countryNumber = 55, selectionIndicator = true)
        val match = SportsMatch(1, team, team.copy(number = 11, name = "Outro"))
        val draft = BetDraft(19, numbers = emptyList(), lotecaSelections = listOf(LotecaMatchSelection(match, true, true, false)), favoriteTeam = team, luckyMonth = LuckyMonth(12, "Dezembro", "Dez"))
        val bet = decodeLegacyCart(guestCartRows(draft, game(19, "Loteca"), BigDecimal.TEN, 1, 1)).single()
        assertTrue(bet.loteca.single().draw)
        assertTrue(bet.loteca.single().first.winner)
        assertFalse(bet.loteca.single().second.winner)
        assertEquals(10, bet.favoriteTeam?.number)
        assertEquals(12, bet.luckyMonth?.number)
    }
}
