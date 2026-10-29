package br.gov.caixa.loterias.feature.results

import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsMatch
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsTeam
import br.gov.caixa.loterias.feature.results.presentation.components.confirmedBetFirstDrawHitCount
import br.gov.caixa.loterias.feature.results.presentation.components.drawModalityCode
import br.gov.caixa.loterias.feature.results.presentation.components.drawResultTitle
import br.gov.caixa.loterias.feature.results.presentation.components.sportsResultLabel
import br.gov.caixa.loterias.feature.results.presentation.components.sportsWinnerLabel
import org.junit.Assert.assertEquals
import org.junit.Test

class SportsDrawPresentationTest {
    private val home = DrawSportsTeam("Casa")
    private val away = DrawSportsTeam("Fora")

    @Test fun `unknown scores and winner do not invent a draw or zero goals`() {
        val match = DrawSportsMatch(1, home, away)
        assertEquals("1. Casa × Fora", sportsResultLabel(match, lotogol = false))
        assertEquals("", sportsWinnerLabel(match))
    }

    @Test fun `lotogol overflow score is displayed as four or more`() {
        val match = DrawSportsMatch(null, home, away, homeScoreIndex = 4, awayScoreIndex = 0)
        assertEquals("Casa 4 ou mais × 0 Fora", sportsResultLabel(match, lotogol = true))
    }

    @Test fun `loteca actual score and outcome come from result rather than wager`() {
        val match = DrawSportsMatch(2, home.copy(score = "3", won = true), away.copy(score = "1", won = false), draw = false)
        assertEquals("2. Casa 3 × 1 Fora", sportsResultLabel(match, lotogol = false))
        assertEquals("Vencedor: Casa", sportsWinnerLabel(match))
        assertEquals("Empate", sportsWinnerLabel(match.copy(draw = true)))
    }

    @Test fun `special contest prefers service title and preserves installed fallback`() {
        val draw = DrawResult(
            "MEGA_SENA",
            1,
            "",
            "",
            emptyList(),
            emptyList(),
            emptyList(),
            false,
            "",
            "",
            "",
            emptyList(),
            specialContest = true
        )
        assertEquals("Mega da Virada", drawResultTitle(draw, "Mega-Sena"))
        assertEquals("Especial do serviço", drawResultTitle(draw.copy(specialName = "Especial do serviço"), "Mega-Sena"))
        assertEquals("Mega-Sena", drawResultTitle(draw.copy(specialContest = false), "Mega-Sena"))
    }

    @Test fun `result modality codes preserve backend payout name mappings`() {
        assertEquals("megasena", drawModalityCode("MEGA_SENA"))
        assertEquals("supersete", drawModalityCode("SUPER_7"))
        assertEquals("lotofacil", drawModalityCode("Lotofácil"))
    }

    @Test fun `Super Sete repeated correct digit scores all seven columns`() {
        val bet = br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet(
            1,
            "Super Sete",
            1,
            "",
            "",
            List(7) { 1 },
            modalityId = 7,
            numberColumns = List(7) { listOf(1) }
        )
        val draw = numericDraw(List(7) { listOf(1) }).copy(modality = "SUPER_7")
        assertEquals(7, confirmedBetFirstDrawHitCount(bet, draw))
    }

    @Test fun `Super Sete wrong column does not count even when digit appears elsewhere`() {
        val bet = br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet(
            1,
            "Super Sete",
            1,
            "",
            "",
            listOf(1, 2),
            numberColumns = listOf(listOf(1), listOf(2))
        )
        assertEquals(0, confirmedBetFirstDrawHitCount(bet, numericDraw(listOf(listOf(2), listOf(1)))))
    }

    @Test fun `ordinary numeric games retain number matching across draw order`() {
        val bet = br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet(
            1,
            "Mega-Sena",
            1,
            "",
            "",
            listOf(1, 2, 3),
            modalityId = 2
        )
        assertEquals(2, confirmedBetFirstDrawHitCount(bet, numericDraw(listOf(listOf(3, 1, 9)))))
    }
    private fun numericDraw(columns: List<List<Int>>) = br.gov.caixa.loterias.feature.results.domain.model.DrawResult(
        "MEGA_SENA", 1, "", "", columns.flatten(), emptyList(), emptyList(), false, "", "", "", emptyList(),
        firstDrawNumberColumns = columns
    )
}
