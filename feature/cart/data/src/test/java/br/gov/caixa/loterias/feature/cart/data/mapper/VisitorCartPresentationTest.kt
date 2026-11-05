// Literais numéricos descrevem conteúdos sintéticos, níveis da API da plataforma e limites esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.cart.data.mapper

import br.gov.caixa.loterias.core.domain.contract.betting.model.LotecaMatchSelection
import br.gov.caixa.loterias.core.domain.contract.betting.model.LotogolMatchSelection
import br.gov.caixa.loterias.core.domain.contract.betting.model.LotteryGameParameters
import br.gov.caixa.loterias.core.domain.contract.betting.model.LotteryModality
import br.gov.caixa.loterias.core.domain.contract.betting.model.LuckyMonth
import br.gov.caixa.loterias.core.domain.contract.betting.model.SportsMatch
import br.gov.caixa.loterias.core.domain.contract.betting.model.SportsTeam
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItemBadge
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.cart.domain.model.CartModality
import br.gov.caixa.loterias.feature.cart.domain.model.CartNumbers
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VisitorCartPresentationTest {
    private val game = LotteryGameParameters(LotteryModality(2, "Mega-Sena", ""), 3000, "", 60, 6, 20, 10, emptyList(), emptyList())
    private fun bet(draft: BetDraft) = decodeLegacyCart(guestCartRows(draft, game, BigDecimal("6.00"), 1, 1)).single()

    @Test fun `visitor upload uses the same contest and surprise codes as authenticated inclusion`() {
        val manual = BetDraft(2, numbers = (1..6).toList())
        val cases = listOf(
            Triple(manual, "1", 1),
            Triple(manual.copy(modalityId = 8), "1", 1),
            Triple(manual.copy(modalityId = 16), "1", 1),
            Triple(manual.copy(contestType = "ESPECIAL"), "2", 1),
            Triple(manual.copy(numbers = emptyList(), surprise = true), "1", 2),
            Triple(
                manual.copy(
                    modalityId = 20,
                    surprise = true,
                    surpriseTeamSelected = true,
                    favoriteTeam = SportsTeam(1, "Time")
                ),
                "1",
                3
            )
        )
        for ((draft, contest, indicator) in cases) {
            val persisted = bet(draft)
            assertEquals(contest, persisted.contestType.value)
            assertEquals(indicator, persisted.quickPickIndicator.value)
            val upload = persisted.toUploadDto()
            assertEquals(contest, upload.contestType.value)
            assertEquals(indicator, upload.quickPickIndicator.value)
            assertEquals(draft.surprise, upload.quickPick)
        }
    }

    @Test fun `upload repairs previously saved visitor codes without changing selections or persisted receipt data`() {
        val manual = bet(BetDraft(2, numbers = (1..6).toList()))
        for ((oldCode, expected) in listOf("N" to "1", "E" to "2", "1" to "1", "2" to "2")) {
            val old = manual.copy(
                contestType = manual.contestType.copy(value = oldCode),
                quickPickIndicator = manual.quickPickIndicator.copy(value = 0)
            )
            val upload = old.toUploadDto()
            assertEquals(expected, upload.contestType.value)
            assertEquals(1, upload.quickPickIndicator.value)
            assertEquals("[1,2,3,4,5,6]", upload.numbers.toString())
            assertEquals(oldCode, old.contestType.value)
            assertEquals(0, old.quickPickIndicator.value)
            assertEquals(
                manual.copy(contestType = manual.contestType.copy(value = expected)).visitorCartSelection(),
                old.visitorCartSelection()
            )
        }
        val oldSurprise = manual.copy(quickPick = true, quickPickIndicator = manual.quickPickIndicator.copy(value = 1))
        assertEquals(2, oldSurprise.toUploadDto().quickPickIndicator.value)
        val timemania = bet(
            BetDraft(
                20,
                numbers = emptyList(),
                surprise = true,
                favoriteTeam = SportsTeam(1, "Time")
            )
        )
        assertEquals(
            3,
            timemania.copy(quickPickIndicator = timemania.quickPickIndicator.copy(value = 1))
                .toUploadDto().quickPickIndicator.value
        )
        for (valid in listOf(2, 3)) {
            assertEquals(
                valid,
                oldSurprise.copy(quickPickIndicator = oldSurprise.quickPickIndicator.copy(value = valid))
                    .toUploadDto().quickPickIndicator.value
            )
        }
    }

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
