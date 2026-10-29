package br.gov.caixa.loterias.core.domain.cart

import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import java.math.BigDecimal
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.feature.cart.domain.model.CartLotecaMatch
import br.gov.caixa.loterias.feature.cart.domain.model.CartLotogolMatch
import br.gov.caixa.loterias.feature.cart.domain.model.CartMatchTeam
import br.gov.caixa.loterias.feature.cart.domain.model.CartModality
import br.gov.caixa.loterias.feature.cart.domain.model.CartNumbers
import br.gov.caixa.loterias.feature.cart.domain.model.CartTeam
import br.gov.caixa.loterias.feature.cart.domain.model.VISITOR_DUPLICATE_CONFIRMATION
import br.gov.caixa.loterias.feature.cart.domain.model.VisitorCartEntry
import br.gov.caixa.loterias.feature.cart.domain.rule.VisitorCartPolicy
import br.gov.caixa.loterias.feature.cart.domain.model.VisitorCartSelection

class VisitorCartPolicyTest {
    private val draft = BetDraft(2, numbers = listOf(1, 2, 3, 4, 5, 6))
    private val game = LotteryGameParameters(LotteryModality(2, "Mega-Sena", "MEGA_SENA"), 3000, "", 60, 6, 20, 10,
        emptyList(), listOf(BetPrice(6, 0, BigDecimal("6.00"))))
    private val parameters = BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 10, listOf(game))
    private val selection = VisitorCartSelection(CartModality.MEGA_SENA, 3000, "N",
        CartNumbers.Flat(listOf(1, 2, 3, 4, 5, 6)), emptyList(), 0, null, null, emptyList(), emptyList())

    @Test fun quantityBoundsAreInclusiveAndInvalidQuantityPrecedesUnavailableGame() {
        for (quantity in listOf(1, 1000)) assertEquals(game, VisitorCartPolicy.gameFor(draft.copy(quantity = quantity), parameters))
        for (quantity in listOf(0, -1, 1001)) {
            assertEquals("Quantidade de apostas inválida.", failure { VisitorCartPolicy.gameFor(draft.copy(quantity = quantity), parameters.copy(games = emptyList())) })
        }
    }

    @Test fun contestSelectionMatchesModalityTypeAndOptionalNumberBeforeCheckingOpen() {
        val special = game.copy(contestNumber = 3001, contestType = "ESPECIAL")
        assertEquals(special, VisitorCartPolicy.gameFor(draft.copy(contestType = "especial", contestNumber = 3001), parameters.copy(games = listOf(game, special))))
        for (invalid in listOf(draft.copy(modalityId = 3), draft.copy(contestType = "ESPECIAL"), draft.copy(contestNumber = 2999))) {
            assertEquals("Modalidade indisponível para apostar no momento.", failure { VisitorCartPolicy.gameFor(invalid, parameters) })
        }
        assertEquals("O registro de apostas individuais para o concurso 3000 foi encerrado.",
            failure { VisitorCartPolicy.gameFor(draft, parameters.copy(games = listOf(game.copy(open = false)))) })
    }

    @Test fun capacityReservesMirrorSpaceEvenForModalitiesThatDoNotCreateMirrors() {
        VisitorCartPolicy.requireCapacity(draft.copy(quantity = 2), 10, 8)
        VisitorCartPolicy.requireCapacity(draft.copy(quantity = 2, generateMirror = true), 10, 6)
        assertNotNull(failure { VisitorCartPolicy.requireCapacity(draft.copy(quantity = 2, generateMirror = true), 10, 7) })
        VisitorCartPolicy.requireCapacity(draft.copy(quantity = 1000, generateMirror = true), 0, 9999)
        VisitorCartPolicy.requireCapacity(draft, -1, 9999)
        assertNull(VisitorCartPolicy.mirror(draft.copy(generateMirror = true)))
    }

    @Test fun exactCloverPricePrecedesNumberFallbackAndRecurrenceIsAtLeastOne() {
        val prices = listOf(BetPrice(6, 0, BigDecimal("6.00")), BetPrice(6, 2, BigDecimal("9.00")))
        assertEquals(BigDecimal("27.00"), VisitorCartPolicy.unitAmount(draft.copy(cloverQuantity = 2, recurringDraws = 3), game.copy(prices = prices)))
        assertEquals(BigDecimal("6.00"), VisitorCartPolicy.unitAmount(draft.copy(cloverQuantity = 3, recurringDraws = -1), game.copy(prices = prices)))
        assertEquals(BigDecimal("6.00"), VisitorCartPolicy.unitAmount(draft, game))
        assertEquals("Preço da aposta indisponível.", failure { VisitorCartPolicy.unitAmount(draft.copy(numberQuantity = 7), game) })
    }

    @Test fun lotecaUsesDoubleAndTripleCountsWithoutFallingBackToNumberPrice() {
        val match = SportsMatch(1, SportsTeam(1, "A"), SportsTeam(2, "B"))
        val loteca = draft.copy(modalityId = 19, lotecaSelections = listOf(
            LotecaMatchSelection(match, true, true, false), LotecaMatchSelection(match, true, true, true)))
        val prices = listOf(BetPrice(6, 0, BigDecimal("1.00")), BetPrice(0, 0, BigDecimal("12.00"), 1, 1))
        assertEquals(BigDecimal("24.00"), VisitorCartPolicy.unitAmount(loteca.copy(recurringDraws = 2), game.copy(prices = prices)))
        assertEquals("Preço da aposta indisponível.", failure { VisitorCartPolicy.unitAmount(loteca, game) })
    }

    @Test fun duplicateNeedsConfirmationButSurpriseAndExplicitConfirmationBypassIt() {
        assertEquals(VISITOR_DUPLICATE_CONFIRMATION, failure { VisitorCartPolicy.requireNoDuplicate(draft, selection, listOf(selection)) })
        VisitorCartPolicy.requireNoDuplicate(draft.copy(surprise = true), selection, listOf(selection))
        VisitorCartPolicy.requireNoDuplicate(draft.copy(allowLocalDuplicate = true), selection, listOf(selection))
    }

    @Test fun duplicateIdentityIncludesOrderedSelectionsContestAndComplements() {
        val distinct = listOf(
            selection.copy(modality = CartModality.QUINA), selection.copy(targetContest = 3001),
            selection.copy(contestType = "E"), selection.copy(numbers = CartNumbers.Flat(listOf(6, 5, 4, 3, 2, 1))),
            selection.copy(numbers = null), selection.copy(clovers = listOf(1)), selection.copy(repeatCount = 1),
            selection.copy(favoriteTeamNumber = 1), selection.copy(luckyMonthNumber = 1),
            selection.copy(numbers = CartNumbers.Columns(listOf(listOf(1, 2, 3, 4, 5, 6)))))
        distinct.forEach { VisitorCartPolicy.requireNoDuplicate(draft, it, listOf(selection)) }
    }

    @Test fun sportsDuplicateComparisonRetainsMetadataScoresOrderAndParameterTeam() {
        val team = CartTeam(1, "A", 55, "Brasil", "BR", "DF", "", true, "A", "Time A")
        val home = CartMatchTeam(team, team, null, true)
        val away = home.copy(team = team.copy(number = 2), parameter = team.copy(number = 2), winner = false)
        val match = CartLotecaMatch(home, away, true)
        val sports = selection.copy(loteca = listOf(match))
        assertEquals(VISITOR_DUPLICATE_CONFIRMATION, failure { VisitorCartPolicy.requireNoDuplicate(draft, sports, listOf(sports)) })
        listOf(match.copy(draw = false), match.copy(first = home.copy(parameter = null)),
            match.copy(first = home.copy(team = team.copy(name = "Renamed"))), match.copy(first = home.copy(winner = false)))
            .forEach { VisitorCartPolicy.requireNoDuplicate(draft, sports.copy(loteca = listOf(it)), listOf(sports)) }
        val goal = CartLotogolMatch(home.copy(score = "2"), away.copy(score = "1"), 2, 1)
        val goals = selection.copy(lotogol = listOf(goal))
        assertEquals(VISITOR_DUPLICATE_CONFIRMATION, failure { VisitorCartPolicy.requireNoDuplicate(draft, goals, listOf(goals)) })
        VisitorCartPolicy.requireNoDuplicate(draft, goals.copy(lotogol = listOf(goal.copy(firstScoreIndex = 3))), listOf(goals))
        VisitorCartPolicy.requireNoDuplicate(draft, goals.copy(lotogol = listOf(goal.copy(first = goal.first.copy(score = "3")))), listOf(goals))
    }

    @Test fun lotomaniaMirrorComplementsOneThroughOneHundredAndSurpriseRemainsOpaque() {
        val original = draft.copy(modalityId = 16, numbers = (1..50).toList(), generateMirror = true)
        val mirror = requireNotNull(VisitorCartPolicy.mirror(original))
        assertEquals((51..100).toList(), mirror.numbers)
        assertFalse(mirror.generateMirror)
        assertEquals(original.numberQuantity, mirror.numberQuantity)
        assertEquals(emptyList<Int>(), VisitorCartPolicy.mirror(original.copy(surprise = true))!!.numbers)
        assertEquals(3007L, VisitorCartPolicy.mirrorLink(7, 3000))
    }

    @Test fun removalIncludesOnlyTheSelectedMirrorGroupAndRejectsMissingItems() {
        val entries = listOf(VisitorCartEntry(1, 3001), VisitorCartEntry(2, 3001), VisitorCartEntry(3, 0), VisitorCartEntry(4, 3004))
        assertEquals(listOf(1L, 2L), VisitorCartPolicy.removalIds(2, entries))
        assertEquals(listOf(3L), VisitorCartPolicy.removalIds(3, entries))
        assertEquals("Aposta local não encontrada.", failure { VisitorCartPolicy.removalIds(99, entries) })
    }

    private fun failure(action: () -> Unit): String? {
        try { action(); fail("Expected rejection") } catch (failure: IllegalArgumentException) { return failure.message }
        return null
    }
}
