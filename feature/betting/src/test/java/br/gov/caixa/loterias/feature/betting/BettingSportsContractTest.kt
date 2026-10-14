package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class BettingSportsContractTest {
    @Before fun setup() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @After fun teardown() = Dispatchers.resetMain()

    @Test fun `sparse footer completion increases quantity locally with one session warning and no submit`() = runTest {
        var sends = 0
        val model = model(prices=listOf(BetPrice(18,0,BigDecimal("32"),4,0)), addBet=AddBetToCartUseCase {
            sends++; AppResult.Success(ShoppingCart(1,BigDecimal.ZERO,emptyList()))
        })
        advanceUntilIdle()
        model.fillShake(false)
        assertEquals(18,model.state.value.numberQuantity)
        assertEquals(18,model.state.value.lotecaOutcomes.values.sumOf { it.size })
        assertEquals(BigDecimal("32"),model.state.value.unitPrice)
        assertTrue(model.state.value.canAddToCart)
        assertEquals("Você aumentou a quantidade de números, o valor será alterado. Fique atento ao valor final da sua aposta.",model.state.value.warningMessage)
        val completed = model.state.value.lotecaOutcomes
        model.fillShake(false)
        assertEquals(completed,model.state.value.lotecaOutcomes)
        model.dismissMessage()
        model.clearCurrentStep()
        model.chooseNumberQuantity(16)
        model.toggleLotecaOutcome(2,LotecaOutcome.AWAY)
        model.fillShake(false)
        assertEquals(18,model.state.value.numberQuantity)
        assertTrue(model.state.value.canAddToCart)
        assertEquals(setOf(LotecaOutcome.AWAY),model.state.value.lotecaOutcomes[2])
        assertNull(model.state.value.warningMessage)
        assertEquals(0,sends)
    }

    @Test fun `Loteca quantity is fifteen through twenty six independent of sparse prices`() = runTest {
        val model = model()
        advanceUntilIdle()
        assertEquals((15..26).toList(), model.state.value.numberQuantityOptions)
        assertEquals(15, model.state.value.numberQuantity)
        model.chooseNumberQuantity(26)
        assertEquals(26, model.state.value.numberQuantity)
        model.chooseNumberQuantity(14)
        assertEquals(26, model.state.value.numberQuantity)
        model.chooseNumberQuantity(27)
        assertEquals(26, model.state.value.numberQuantity)
    }

    @Test fun `reducing eighteen to seventeen clears outcomes retains target and never warns`() = runTest {
        val model = model(initialQuantity = 18)
        advanceUntilIdle()
        model.chooseNumberQuantity(17)
        assertEquals(17, model.state.value.numberQuantity)
        assertTrue(model.state.value.lotecaOutcomes.isEmpty())
        assertNull(model.state.value.warningMessage)
    }

    @Test fun `increasing target preserves outcomes and warns once per session`() = runTest {
        val model = model()
        advanceUntilIdle()
        model.toggleLotecaOutcome(1, LotecaOutcome.HOME)
        val outcomes = model.state.value.lotecaOutcomes
        model.chooseNumberQuantity(16)
        assertEquals(outcomes, model.state.value.lotecaOutcomes)
        assertEquals("Você aumentou a quantidade de números, o valor será alterado. Fique atento ao valor final da sua aposta.", model.state.value.warningMessage)
        model.dismissMessage()
        model.chooseNumberQuantity(18)
        assertNull(model.state.value.warningMessage)
        model.chooseNumberQuantity(17)
        assertNull(model.state.value.warningMessage)
    }

    @Test fun `footer clear discards Loteca outcomes and preserves target contest and continuation`() = runTest {
        val model = model()
        advanceUntilIdle()
        model.chooseNumberQuantity(17)
        model.dismissMessage()
        model.toggleLotecaOutcome(1, LotecaOutcome.HOME)
        model.clearCurrentStep()
        assertTrue(model.state.value.lotecaOutcomes.isEmpty())
        assertEquals(17, model.state.value.numberQuantity)
        assertEquals(1275, model.state.value.game?.contestNumber)
        model.toggleLotecaOutcome(2, LotecaOutcome.AWAY)
        assertEquals(setOf(LotecaOutcome.AWAY), model.state.value.lotecaOutcomes[2])
    }

    @Test fun `footer clear discards Lotogol scores and preserves quantity contest and continuation`() = runTest {
        val model = model(lotogol = true)
        advanceUntilIdle()
        val quantity = model.state.value.numberQuantity
        model.selectLotogolScore(1, true, 2)
        model.selectLotogolScore(1, false, 3)
        model.clearCurrentStep()
        assertTrue(model.state.value.lotogolScores.isEmpty())
        assertEquals(quantity, model.state.value.numberQuantity)
        assertEquals(1275, model.state.value.game?.contestNumber)
        model.selectLotogolScore(2, true, 5)
        assertEquals(5 to null, model.state.value.lotogolScores[2])
    }

    @Test fun `valid sparse priced Loteca submits unchanged payload and failed inclusion retains draft`() = runTest {
        var submitted: BetDraft? = null
        var fails = true
        val model = model(addBet = AddBetToCartUseCase { draft ->
            submitted = draft
            if (fails) AppResult.Failure(br.gov.caixa.loterias.core.common.AppError.Network)
            else AppResult.Success(ShoppingCart(1, BigDecimal("4"), emptyList()))
        })
        advanceUntilIdle()
        (1..14).forEach { model.toggleLotecaOutcome(it, LotecaOutcome.HOME) }
        model.toggleLotecaOutcome(1, LotecaOutcome.DRAW)
        assertTrue(model.state.value.canAddToCart)
        assertEquals(BigDecimal("4"), model.state.value.unitPrice)
        val outcomes = model.state.value.lotecaOutcomes
        model.addToCart()
        advanceUntilIdle()
        assertEquals(outcomes, model.state.value.lotecaOutcomes)
        assertEquals(15, model.state.value.numberQuantity)
        assertFalse(model.state.value.addedToCart)
        assertEquals(15, submitted?.numberQuantity)
        assertEquals(14, submitted?.lotecaSelections?.size)
        assertEquals(LotecaMatchSelection(requireNotNull(model.state.value.game).matches[0], true, true, false), submitted?.lotecaSelections?.first())
        fails = false
        model.dismissMessage()
        model.addToCart()
        advanceUntilIdle()
        assertTrue(model.state.value.addedToCart)
        assertEquals(15, submitted?.numberQuantity)
    }

    private fun model(initialQuantity: Int = 0, lotogol: Boolean = false,
        prices: List<BetPrice> = listOf(BetPrice(15,0,BigDecimal("4"),1,0), BetPrice(18,0,BigDecimal("32"),4,0)),
        addBet: AddBetToCartUseCase = AddBetToCartUseCase { AppResult.Success(ShoppingCart(1,BigDecimal.ZERO,emptyList())) },
    ): BettingGameViewModel {
        val id = if (lotogol) LOTOGOL_ID else LOTECA_ID
        val team = SportsTeam(1, "BAHIA", "BA", countryCode = "BRA")
        val matches = (1..14).map { SportsMatch(it, team, team.copy(number=2, name="FLAMENGO", state="RJ")) }
        val game = LotteryGameParameters(LotteryModality(id, "Loteca", "LOTECA"), contestNumber=1275, drawDate="18/10/2026", maximumSurprises=1, recurringOptions=listOf(0),
            maximumNumber=0, minimumSelection=15, maximumSelection=26, matches=matches,
            prices=prices)
        val seed = if (initialQuantity == 18) matches.mapIndexed { index, match ->
            LotecaMatchSelection(match, true, index < 4, false)
        } else emptyList()
        return BettingGameViewModel(id, GetBettingParametersUseCase {
            AppResult.Success(BettingParameters(BigDecimal.ZERO,BigDecimal.ZERO,50,listOf(game)))
        }, addBet,
            FixedCoroutineDispatchers(UnconfinedTestDispatcher()), initialLotecaSelections=seed)
    }
}
