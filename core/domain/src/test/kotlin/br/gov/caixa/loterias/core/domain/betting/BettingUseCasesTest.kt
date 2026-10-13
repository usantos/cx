package br.gov.caixa.loterias.core.domain.betting

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BettingUseCasesTest {
    private val modality = LotteryModality(1, "Mega-Sena", "megasena", "Especial", "CAIXA")
    private val price = BetPrice(6, 0, BigDecimal("5.00"))
    private val game = LotteryGameParameters(
        modality = modality,
        contestNumber = 2800,
        drawDate = "10/09/2026",
        maximumNumber = 60,
        minimumSelection = 6,
        maximumSelection = 20,
        maximumSurprises = 10,
        recurringOptions = listOf(2, 4, 8),
        prices = listOf(price),
        minimumClovers = 0,
        maximumClovers = 0,
        maximumCloverNumber = 0,
        open = true,
    )
    private val parameters = BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 20, listOf(game))
    private val comboEntry = BetComboEntry(modality, "NORMAL", 2, 1, 6, 0, 2800)
    private val combo = BetCombo(3, "Combo", "Descrição", true, BigDecimal.TEN, listOf(comboEntry))
    private val cart = ShoppingCart(1, BigDecimal.TEN, emptyList())

    @Test fun `catalog and add use cases delegate valid values`() = runTest {
        val repository = FakeRepository(modality, parameters, combo, cart)
        val draft = BetDraft(1, "NORMAL", listOf(1, 2, 3, 4, 5, 6), listOf(1), 2, false, 3)

        assertEquals(listOf(modality), (GetLotteryModalities(repository)() as AppResult.Success).value)
        assertEquals(parameters, (GetBettingParameters(repository)() as AppResult.Success).value)
        assertEquals(listOf(combo), (GetBetCombos(repository)() as AppResult.Success).value)
        assertEquals(cart, (AddBetToCart(repository)(draft) as AppResult.Success).value)
        assertEquals(cart, (AddComboToCart(repository)(3, 2, true) as AppResult.Success).value)
        assertEquals(draft, repository.draft)
        assertEquals(Triple(3, 2, true), repository.comboRequest)
    }

    @Test fun `surprise bet can be submitted without chosen numbers`() = runTest {
        val repository = FakeRepository(modality, parameters, combo, cart)
        val result = AddBetToCart(repository)(BetDraft(1, numbers = emptyList(), surprise = true, numberQuantity = 6))
        assertTrue(result is AppResult.Success)
    }

    @Test fun `super seven matrix can be submitted without a flat number list`() = runTest {
        val repository = FakeRepository(modality, parameters, combo, cart)
        val draft = BetDraft(
            modalityId = 7,
            numbers = emptyList(),
            numberQuantity = 7,
            superSevenColumns = List(7) { listOf(it) },
        )
        assertTrue(AddBetToCart(repository)(draft) is AppResult.Success)
        assertEquals(draft, repository.draft)
    }

    @Test fun `sports and special modality models preserve every backend field`() {
        val home = SportsTeam(
            number = 101,
            name = "Azul",
            state = "DF",
            shortDescription = "AZU",
            longDescription = "Time Azul",
            countryNumber = 1,
            country = "Brasil",
            countryCode = "BR",
            className = "A",
            selectionIndicator = true,
            shieldBase64 = "shield",
        )
        val away = SportsTeam(102, "Verde")
        val match = SportsMatch(1, home, away, "Final", "F")
        val loteca = LotecaMatchSelection(match, homeWin = true, draw = false, awayWin = false)
        val lotogol = LotogolMatchSelection(match, homeScore = 5, awayScore = 0)
        val month = LuckyMonth(1, "Janeiro", "JAN")
        val specialGame = game.copy(luckyMonths = listOf(month), acceptsMirror = true, teams = listOf(home), matches = listOf(match))
        val draft = BetDraft(
            modalityId = 19,
            numbers = emptyList(),
            numberQuantity = 1,
            favoriteTeam = home,
            lotecaSelections = listOf(loteca),
            lotogolSelections = listOf(lotogol),
            superSevenColumns = listOf(listOf(1)),
            luckyMonth = month,
            generateMirror = true,
        )

        assertEquals(listOf(101, "Azul", "DF", "AZU", "Time Azul", 1, "Brasil", "BR", "A", true, "shield"), listOf(
            home.number, home.name, home.state, home.shortDescription, home.longDescription, home.countryNumber,
            home.country, home.countryCode, home.className, home.selectionIndicator, home.shieldBase64,
        ))
        assertEquals(listOf(1, home, away, "Final", "F"), listOf(match.number, match.home, match.away, match.legendDescription, match.legendLetter))
        assertEquals(listOf(match, true, false, false), listOf(loteca.match, loteca.homeWin, loteca.draw, loteca.awayWin))
        assertEquals(listOf(match, 5, 0), listOf(lotogol.match, lotogol.homeScore, lotogol.awayScore))
        assertEquals(listOf(1, "Janeiro", "JAN"), listOf(month.number, month.name, month.abbreviation))
        assertEquals(listOf(month), specialGame.luckyMonths)
        assertTrue(specialGame.acceptsMirror)
        assertEquals(home, draft.favoriteTeam)
        assertEquals(listOf(loteca), draft.lotecaSelections)
        assertEquals(listOf(lotogol), draft.lotogolSelections)
        assertEquals(listOf(listOf(1)), draft.superSevenColumns)
        assertEquals(month, draft.luckyMonth)
        assertTrue(draft.generateMirror)
    }

    @Test fun `bet validation rejects modality numbers and quantity`() = runTest {
        val repository = FakeRepository(modality, parameters, combo, cart)

        assertValidation(AddBetToCart(repository)(BetDraft(0, numbers = listOf(1))))
        assertValidation(AddBetToCart(repository)(BetDraft(1, numbers = emptyList())))
        assertValidation(AddBetToCart(repository)(BetDraft(1, numbers = listOf(1), quantity = 0)))
        assertValidation(AddBetToCart(repository)(BetDraft(1, numbers = listOf(1), numberQuantity = 0)))
    }

    @Test fun `combo validation rejects id and quantity`() = runTest {
        val repository = FakeRepository(modality, parameters, combo, cart)
        assertValidation(AddComboToCart(repository)(0, 1, false))
        assertValidation(AddComboToCart(repository)(1, 0, false))
    }

    @Test fun `repository failures are represented as app failures`() = runTest {
        val repository = FakeRepository(modality, parameters, combo, cart, fail = true)
        assertTrue(GetLotteryModalities(repository)() is AppResult.Failure)
        assertTrue(GetBettingParameters(repository)() is AppResult.Failure)
        assertTrue(GetBetCombos(repository)() is AppResult.Failure)
        assertTrue(AddBetToCart(repository)(BetDraft(1, numbers = listOf(1))) is AppResult.Failure)
        assertTrue(AddComboToCart(repository)(1, 1, false) is AppResult.Failure)
    }

    private fun assertValidation(result: AppResult<*>) = assertTrue(result is AppResult.Failure)

    private class FakeRepository(
        private val modality: LotteryModality,
        private val parameters: BettingParameters,
        private val combo: BetCombo,
        private val cart: ShoppingCart,
        private val fail: Boolean = false,
    ) : BettingRepository {
        var draft: BetDraft? = null
        var comboRequest: Triple<Int, Int, Boolean>? = null
        private fun verify() { if (fail) error("failure") }
        override suspend fun modalities() = listOf(modality).also { verify() }
        override suspend fun parameters() = parameters.also { verify() }
        override suspend fun combos() = listOf(combo).also { verify() }
        override suspend fun addBet(draft: BetDraft): ShoppingCart {
            verify()
            this.draft = draft
            return cart
        }
        override suspend fun addCombo(comboId: Int, quantity: Int, surprise: Boolean): ShoppingCart {
            verify()
            comboRequest = Triple(comboId, quantity, surprise)
            return cart
        }
    }
}
