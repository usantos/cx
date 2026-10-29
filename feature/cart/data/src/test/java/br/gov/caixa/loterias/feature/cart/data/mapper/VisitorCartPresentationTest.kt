// Literais numéricos descrevem conteúdos sintéticos, níveis da API da plataforma e limites esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.cart.data.mapper

import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotogolMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemBadge
import br.gov.caixa.loterias.feature.cart.domain.model.CartModality
import br.gov.caixa.loterias.feature.cart.domain.model.CartNumbers
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VisitorCartPresentationTest {
    private val game = LotteryGameParameters(LotteryModality(2, "Mega-Sena", ""), 3000, "", 60, 6, 20, 10, emptyList(), emptyList())
    private fun bet(draft: BetDraft) = decodeLegacyCart(guestCartRows(draft, game, BigDecimal("6.00"), 1, 1)).single()

    @Test fun `visitor projection preserves localized modality titles number padding and total count`() {
        val source = bet(BetDraft(2, numbers = (1..6).toList()))
        val labels = mapOf(
            CartModality.MEGA_SENA to "Mega-Sena",
            CartModality.MAIS_MILIONARIA to "+Milionária",
            CartModality.LOTOFACIL to "Lotofácil",
            CartModality.SUPER_7 to "Super Sete",
            CartModality.DIA_DE_SORTE to "Dia de Sorte",
            CartModality.DUPLA_SENA to "Dupla Sena",
            CartModality.QUINA to "Quina"
        )
        labels.forEach { (modality, expected) ->
            val cart = legacyVisitorCart(listOf(source.copy(modality = modality)))
            assertEquals(expected, cart.items.single().title)
            assertEquals(1, cart.betCount)
            assertEquals("01  02  03  04  05  06", cart.items.single().description)
            assertEquals(BigDecimal("6.00"), cart.total)
        }
        val columns = legacyVisitorCart(listOf(source.copy(numbers = CartNumbers.Columns(listOf(listOf(1, 2), listOf(3)))))).items.single()
        assertEquals(listOf(listOf(1, 2), listOf(3)), columns.selectedNumbers)
    }

    @Test fun `sports selections complementary month and flags render from persisted graph`() {
        val home = SportsTeam(1, "Casa")
        val away = SportsTeam(2, "Fora")
        val match = SportsMatch(1, home, away)
        val source = bet(
            BetDraft(
                19,
                numbers = emptyList(),
                favoriteTeam = home,
                luckyMonth = LuckyMonth(12, "Dezembro"),
                lotecaSelections = listOf(LotecaMatchSelection(match, true, true, false)),
                lotogolSelections = listOf(LotogolMatchSelection(match, 2, 1)),
                recurringDraws = 3
            )
        )
        val item = legacyVisitorCart(listOf(source.copy(mirrored = true))).items.single()
        assertEquals("Casa", item.complementaryLabel)
        assertEquals(listOf("Casa 1 X  Fora", "Casa 2 × 1 Fora"), item.gameRows)
        val selections = item.lotecaSelections.single()
        assertEquals("Casa", selections.homeLabel)
        assertTrue(selections.homeSelected)
        assertTrue(selections.drawSelected)
        assertEquals(false, selections.awaySelected)
        val original = source.loteca.single()
        val awayPick = original.copy(
            first = original.first.copy(winner = false),
            second = original.second.copy(winner = true),
            draw = false
        )
        val triple = original.copy(second = original.second.copy(winner = true))
        val multiple = legacyVisitorCart(listOf(source.copy(loteca = listOf(awayPick, triple))))
            .items.single().lotecaSelections
        assertEquals(listOf(false, true), multiple.map { it.homeSelected })
        assertEquals(listOf(false, true), multiple.map { it.drawSelected })
        assertEquals(listOf(true, true), multiple.map { it.awaySelected })
        assertEquals(setOf(CartItemBadge.RECURRING, CartItemBadge.MIRROR), item.badges)
        val month = legacyVisitorCart(listOf(source.copy(favoriteTeam = null, numbers = null))).items.single()
        assertEquals("Dezembro", month.complementaryLabel)
        assertTrue(month.selectedNumbers.isEmpty())
        assertEquals("", month.description)
    }
}
