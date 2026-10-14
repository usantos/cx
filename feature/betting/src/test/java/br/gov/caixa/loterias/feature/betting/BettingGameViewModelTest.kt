package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.betting.AddBetToCartUseCase
import br.gov.caixa.loterias.core.domain.betting.BetDraft
import br.gov.caixa.loterias.core.domain.betting.BetPrice
import br.gov.caixa.loterias.core.domain.betting.BettingParameters
import br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase
import br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters
import br.gov.caixa.loterias.core.domain.betting.LotteryModality
import br.gov.caixa.loterias.core.domain.betting.LotecaMatchSelection
import br.gov.caixa.loterias.core.domain.betting.LotogolMatchSelection
import br.gov.caixa.loterias.core.domain.betting.LuckyMonth
import br.gov.caixa.loterias.core.domain.betting.SportsMatch
import br.gov.caixa.loterias.core.domain.betting.SportsTeam
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BettingGameViewModelTest {
    private val game = game()
    private var parameterResult: AppResult<BettingParameters> = successParameters(game)
    private var addResult: AppResult<ShoppingCart> = AppResult.Success(ShoppingCart(1, BigDecimal.TEN, emptyList()))
    private var submitted: BetDraft? = null

    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `successful inclusion resets surprise draft immediately before acknowledgement`() = runTest {
        val model = viewModel(2)
        advanceUntilIdle()
        model.selectMode(BetEntryMode.SURPRISE)
        model.chooseNumberQuantity(7)
        model.chooseRecurringDraws(2)
        model.increaseSurpriseQuantity()
        model.addToCart()
        advanceUntilIdle()
        assertTrue(model.state.value.addedToCart)
        assertEquals(BetEntryMode.MANUAL, model.state.value.mode)
        assertEquals(BetSelectionStep.NUMBERS, model.state.value.selectionStep)
        assertEquals(6, model.state.value.numberQuantity)
        assertEquals(0, model.state.value.recurringDraws)
        assertEquals(1, model.state.value.surpriseQuantity)
        assertTrue(model.state.value.selectedNumbers.isEmpty())
        assertEquals((addResult as AppResult.Success).value, model.state.value.confirmedCart)
        model.acknowledgeCartAddition()
        assertFalse(model.state.value.addedToCart)
        (1..6).forEach(model::toggleNumber)
        model.addToCart()
        advanceUntilIdle()
        assertTrue(model.state.value.addedToCart)
        assertEquals(false, submitted?.surprise)
        assertEquals(0, submitted?.recurringDraws)
        assertEquals(1, submitted?.quantity)
    }

    @Test fun `failed inclusion preserves extras and confirmed success resets all draft fields`() = runTest {
        val team = SportsTeam(1, "Time", "SP")
        val month = LuckyMonth(1, "Janeiro")
        val plus = game(id = MAIS_MILIONARIA_ID).copy(
            minimumClovers = 2, maximumClovers = 6, maximumCloverNumber = 6,
            teams = listOf(team), luckyMonths = listOf(month), acceptsMirror = true,
            prices = listOf(BetPrice(6, 2, BigDecimal("18.00"))),
        )
        parameterResult = successParameters(plus)
        val model = BettingGameViewModel(MAIS_MILIONARIA_ID,
            GetBettingParametersUseCase { parameterResult }, AddBetToCartUseCase { addResult },
            FixedCoroutineDispatchers(UnconfinedTestDispatcher()),
            initialNumbers = (1..6).toList(), initialClovers = listOf(1,2),
            initialTeam = team, initialLuckyMonth = month, initialRecurringDraws = 2,
            initialMirror = true,
        )
        advanceUntilIdle()
        model.primaryAction()
        val before = model.state.value
        addResult = AppResult.Failure(AppError.Network)
        model.addToCart(); advanceUntilIdle()
        assertEquals(before.selectedNumbers, model.state.value.selectedNumbers)
        assertEquals(before.selectedClovers, model.state.value.selectedClovers)
        assertEquals(team, model.state.value.selectedTeam)
        assertEquals(month, model.state.value.selectedLuckyMonth)
        assertEquals(2, model.state.value.recurringDraws)
        assertTrue(model.state.value.generateMirror)
        assertEquals(BetSelectionStep.COMPLEMENT, model.state.value.selectionStep)
        assertNull(model.state.value.confirmedCart)
        addResult = AppResult.Success(ShoppingCart(7, BigDecimal("72.00"), emptyList(), totalBetCount = 4))
        model.addToCart(); advanceUntilIdle()
        with(model.state.value) {
            assertTrue(addedToCart)
            assertTrue(selectedNumbers.isEmpty()); assertTrue(selectedClovers.isEmpty())
            assertNull(selectedTeam); assertNull(selectedLuckyMonth); assertEquals("", teamQuery)
            assertTrue(lotecaOutcomes.isEmpty()); assertTrue(lotogolScores.isEmpty())
            assertTrue(superSevenColumns.all(Set<Int>::isEmpty))
            assertFalse(generateMirror); assertEquals(0, recurringDraws)
            assertEquals(2, manualCloverQuantity); assertEquals(2, surpriseCloverQuantity)
            assertEquals(BigDecimal("18.00"), totalPrice)
            assertEquals(4, confirmedCart?.betCount)
        }
    }

    @Test fun `prepared parameters render immediately before queued IO runs`() = runTest {
        var requests = 0
        val model = BettingGameViewModel(
            modalityId = 2,
            getParameters = GetBettingParametersUseCase { requests++; parameterResult },
            addBet = AddBetToCartUseCase { error("This test must not submit a bet") },
            dispatchers = FixedCoroutineDispatchers(StandardTestDispatcher(testScheduler)),
            expectedContestNumber = game.contestNumber,
            initialParameters = successParameters(game).value,
        )
        assertEquals(game, model.state.value.game)
        assertFalse(model.state.value.loading)
        assertEquals(BigDecimal("30"), model.state.value.minimumCartAmount)
        assertEquals(0, requests)
        advanceUntilIdle()
        assertEquals(0, requests)
    }

    @Test fun `prepared public parameters avoid refetch and explicit retry refreshes backend`() = runTest {
        var requests = 0
        val prepared = successParameters(game).value
        parameterResult = AppResult.Failure(AppError.Validation("Atualização indisponível"))
        val model = BettingGameViewModel(
            modalityId = 2,
            getParameters = GetBettingParametersUseCase { requests++; parameterResult },
            addBet = AddBetToCartUseCase { error("This test must not submit a bet") },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher()),
            expectedContestNumber = game.contestNumber,
            initialParameters = prepared,
        )
        advanceUntilIdle()
        assertEquals(0, requests)
        assertEquals(game, model.state.value.game)
        assertEquals(prepared.minimumCartAmount, model.state.value.minimumCartAmount)
        assertFalse(model.state.value.loading)

        model.load()
        advanceUntilIdle()
        assertEquals(1, requests)
        assertNull(model.state.value.game)
        assertEquals("Atualização indisponível", model.state.value.errorMessage)

        val refreshed = game.copy(prices = listOf(BetPrice(6, 0, BigDecimal("6.00"))))
        parameterResult = successParameters(refreshed).copy(value = prepared.copy(
            minimumCartAmount = BigDecimal("40"), games = listOf(refreshed),
        ))
        model.load()
        advanceUntilIdle()
        assertEquals(2, requests)
        assertEquals(refreshed, model.state.value.game)
        assertEquals(BigDecimal("40"), model.state.value.minimumCartAmount)
        assertEquals(BigDecimal("6.00"), model.state.value.unitPrice)
        assertNull(model.state.value.errorMessage)
    }

    @Test fun `prepared parameters for a different route fall back to fresh parameters`() = runTest {
        val mismatches = listOf(
            game.copy(contestNumber = game.contestNumber - 1),
            game.copy(contestType = "ESPECIAL"),
            game(id = 3, name = "Quina"),
        )
        mismatches.forEach { mismatched ->
            var requests = 0
            val model = BettingGameViewModel(
                modalityId = 2,
                getParameters = GetBettingParametersUseCase { requests++; parameterResult },
                addBet = AddBetToCartUseCase { error("This test must not submit a bet") },
                dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher()),
                initialContestTypeId = 1L,
                expectedContestNumber = game.contestNumber,
                initialParameters = successParameters(mismatched).value,
            )
            advanceUntilIdle()
            assertEquals(1, requests)
            assertEquals(game, model.state.value.game)
        }
    }

    @Test fun `shake completes partial numbers without sending or replacing manual numbers`() = runTest {
        val model = viewModel(2)
        advanceUntilIdle()
        model.toggleNumber(1)
        model.fillShake(false)
        assertEquals(6, model.state.value.selectedNumbers.size)
        assertTrue(1 in model.state.value.selectedNumbers)
        assertNull(submitted)
        val original = model.state.value.selectedNumbers
        model.fillShake(true)
        assertEquals(6, model.state.value.selectedNumbers.size)
        assertTrue(original != model.state.value.selectedNumbers)
        assertNull(submitted)
        model.selectMode(BetEntryMode.SURPRISE)
        val state = model.state.value
        model.fillShake(false)
        assertEquals(state, model.state.value)
    }

    @Test fun `duplicate confirmation submits only after explicit acceptance`() = runTest {
        val model = viewModel(2)
        advanceUntilIdle()
        (1..6).forEach(model::toggleNumber)
        addResult = AppResult.Failure(AppError.Validation(br.gov.caixa.loterias.core.domain.cart.VISITOR_DUPLICATE_CONFIRMATION))
        model.addToCart()
        advanceUntilIdle()
        assertTrue(model.state.value.duplicateConfirmation)
        assertTrue(submitted?.allowLocalDuplicate == false)
        model.cancelLocalDuplicate()
        assertFalse(model.state.value.duplicateConfirmation)
        assertFalse(model.state.value.addedToCart)
        model.addToCart()
        advanceUntilIdle()
        addResult = AppResult.Success(ShoppingCart(0, BigDecimal.TEN, emptyList()))
        model.confirmLocalDuplicate()
        advanceUntilIdle()
        assertTrue(submitted?.allowLocalDuplicate == true)
        assertTrue(model.state.value.addedToCart)
    }

    @Test fun `loads requested game and derives all backend options`() = runTest {
        val other = game(id = 3, name = "Quina")
        parameterResult = successParameters(game, other)
        val viewModel = viewModel(3)
        advanceUntilIdle()

        with(viewModel.state.value) {
            assertFalse(loading)
            assertEquals(3, game?.modality?.id)
            assertEquals(listOf(6, 7), numberQuantityOptions)
            assertEquals(listOf(0, 2, 4), recurringOptions)
            assertEquals(0, recurringDraws)
            assertEquals(6, numberQuantity)
            assertTrue(canAddToCart.not())
            assertEquals(BigDecimal("5.00"), unitPrice)
            assertEquals(BigDecimal("5.00"), totalPrice)
        }
    }

    @Test fun `restores numbers and special contest when editing a confirmed bet`() = runTest {
        val normal = game()
        val special = game().copy(contestType = "ESPECIAL", contestNumber = 3000)
        parameterResult = successParameters(normal, special)
        val dispatcher = UnconfinedTestDispatcher()
        val viewModel = BettingGameViewModel(
            modalityId = 2,
            getParameters = GetBettingParametersUseCase { parameterResult },
            addBet = AddBetToCartUseCase { AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList())) },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher()),
            initialNumbers = listOf(1, 2, 3, 4, 5, 6),
            initialContestTypeId = 2,
        )
        advanceUntilIdle()
        assertEquals(3000, viewModel.state.value.game?.contestNumber)
        assertEquals(setOf(1, 2, 3, 4, 5, 6), viewModel.state.value.selectedNumbers)
        assertEquals(6, viewModel.state.value.numberQuantity)
    }

    @Test fun `generic simulator uses first open game then first game`() = runTest {
        parameterResult = successParameters(game.copy(open = false), game(id = 3, name = "Quina"))
        val openFallback = viewModel(null)
        advanceUntilIdle()
        assertEquals(3, openFallback.state.value.game?.modality?.id)

        parameterResult = successParameters(game.copy(open = false))
        val closedFallback = viewModel(null)
        advanceUntilIdle()
        assertEquals(2, closedFallback.state.value.game?.modality?.id)
    }

    @Test fun `requested modality or contest disappearing must not switch to another bet`() = runTest {
        val unknown = viewModel(999)
        advanceUntilIdle()
        assertNull(unknown.state.value.game)
        assertFalse(unknown.state.value.canAddToCart)
        val selected = viewModel(2)
        advanceUntilIdle()
        assertEquals(2, selected.state.value.game?.modality?.id)
        parameterResult = successParameters(game(id = 3, name = "Quina"))
        selected.load(); advanceUntilIdle()
        assertNull(selected.state.value.game)
        parameterResult = successParameters(game)
        val special = BettingGameViewModel(
            modalityId = 2, initialContestTypeId = 2,
            getParameters = GetBettingParametersUseCase { parameterResult },
            addBet = AddBetToCartUseCase { addResult },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher()),
        )
        advanceUntilIdle()
        assertNull(special.state.value.game)
        assertFalse(special.state.value.canAddToCart)
    }

    @Test fun `empty and failed parameters expose mapped errors and reload`() = runTest {
        parameterResult = AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0, emptyList()))
        val empty = viewModel()
        advanceUntilIdle()
        assertEquals("Nenhuma modalidade disponível para aposta.", empty.state.value.errorMessage)

        parameterResult = AppResult.Failure(AppError.Network)
        empty.load()
        advanceUntilIdle()
        assertEquals("Não foi possível carregar a aposta. Verifique sua internet.", empty.state.value.errorMessage)

        parameterResult = AppResult.Failure(AppError.Validation(""))
        empty.load()
        advanceUntilIdle()
        assertEquals("Não foi possível atualizar a aposta.", empty.state.value.errorMessage)

        parameterResult = AppResult.Failure(AppError.Unexpected)
        empty.load()
        advanceUntilIdle()
        assertEquals("Ocorreu um erro inesperado ao carregar a aposta.", empty.state.value.errorMessage)
    }

    @Test fun `validated contest survives second fetch and cannot roll forward on retry`() = runTest {
        val chosen = game.copy(contestType = "ESPECIAL", contestNumber = 3000)
        parameterResult = successParameters(game, chosen.copy(contestNumber = 3001), chosen)
        val vm = BettingGameViewModel(
            modalityId = 2, initialContestTypeId = 2, expectedContestNumber = 3000,
            getParameters = GetBettingParametersUseCase { parameterResult },
            addBet = AddBetToCartUseCase { submitted = it; addResult },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher()),
        )
        advanceUntilIdle()
        assertEquals(chosen, vm.state.value.game)
        parameterResult = successParameters(chosen.copy(contestNumber = 3001))
        vm.load(); advanceUntilIdle()
        assertNull(vm.state.value.game)
        assertFalse(vm.state.value.canAddToCart)
        vm.addToCart(); advanceUntilIdle()
        assertNull(submitted)
        parameterResult = successParameters(chosen)
        vm.load(); advanceUntilIdle()
        assertEquals(chosen, vm.state.value.game)
    }

    @Test fun `special contest type reaches manual and surprise drafts`() = runTest {
        parameterResult = successParameters(game.copy(contestType = "ESPECIAL"))
        listOf(BetEntryMode.MANUAL, BetEntryMode.SURPRISE).forEach { mode ->
            val vm = viewModel()
            advanceUntilIdle()
            vm.selectMode(mode)
            if (mode == BetEntryMode.MANUAL) (1..6).forEach(vm::toggleNumber)
            vm.addToCart(); advanceUntilIdle()
            assertEquals("ESPECIAL", submitted?.contestType)
            assertEquals(mode == BetEntryMode.SURPRISE, submitted?.surprise)
        }
    }

    @Test fun `closed contest and failed refresh never send a stale draft`() = runTest {
        val vm = viewModel(); advanceUntilIdle()
        (1..6).forEach(vm::toggleNumber)
        assertTrue(vm.state.value.canAddToCart)
        parameterResult = AppResult.Failure(AppError.Network)
        vm.load(); advanceUntilIdle()
        assertNull(vm.state.value.game)
        assertFalse(vm.state.value.canAddToCart)
        vm.addToCart(); advanceUntilIdle()
        assertNull(submitted)
        parameterResult = successParameters(game.copy(open = false))
        vm.load(); advanceUntilIdle()
        vm.addToCart(); advanceUntilIdle()
        assertEquals("O registro de apostas individuais para o concurso 2900 foi encerrado.", vm.state.value.errorMessage)
        assertNull(submitted)
    }

    @Test fun `add is single flight and success cannot be submitted twice`() = runTest {
        val response = CompletableDeferred<AppResult<ShoppingCart>>()
        var requests = 0
        val vm = BettingGameViewModel(
            modalityId = 2,
            getParameters = GetBettingParametersUseCase { parameterResult },
            addBet = AddBetToCartUseCase { requests++; response.await() },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher()),
        )
        advanceUntilIdle()
        (1..6).forEach(vm::toggleNumber)
        vm.addToCart(); vm.addToCart()
        assertTrue(vm.state.value.loading)
        assertFalse(vm.state.value.canAddToCart)
        assertFalse(vm.state.value.canPerformPrimaryAction)
        vm.primaryAction()
        assertEquals(1, requests)
        response.complete(addResult); advanceUntilIdle()
        assertTrue(vm.state.value.addedToCart)
        assertFalse(vm.state.value.canAddToCart)
        assertFalse(vm.state.value.canPerformPrimaryAction)
        vm.primaryAction()
        vm.addToCart(); advanceUntilIdle()
        assertEquals(1, requests)
    }

    @Test fun `manual selection enforces configured quantity and supports clear`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        (1..6).forEach(viewModel::toggleNumber)
        viewModel.toggleNumber(7)
        assertEquals((1..7).toSet(), viewModel.state.value.selectedNumbers)
        assertEquals(7, viewModel.state.value.numberQuantity)
        viewModel.dismissMessage()
        viewModel.toggleNumber(2)
        assertEquals(setOf(1, 3, 4, 5, 6, 7), viewModel.state.value.selectedNumbers)
        assertEquals(6, viewModel.state.value.numberQuantity)

        viewModel.chooseNumberQuantity(99)
        assertEquals(6, viewModel.state.value.numberQuantity)
        viewModel.chooseNumberQuantity(7)
        assertEquals(7, viewModel.state.value.numberQuantity)
        assertNull(viewModel.state.value.warningMessage)
        viewModel.dismissMessage()
        assertNull(viewModel.state.value.warningMessage)
        viewModel.chooseNumberQuantity(6)
        assertEquals(6, viewModel.state.value.selectedNumbers.size)

        viewModel.clearSelection()
        assertTrue(viewModel.state.value.selectedNumbers.isEmpty())
        assertTrue(viewModel.state.value.selectedClovers.isEmpty())
    }

    @Test fun `manual number taps grow and shrink quantity with the selected count`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        (1..6).forEach(viewModel::toggleNumber)

        viewModel.toggleNumber(7)

        assertEquals(7, viewModel.state.value.numberQuantity)
        assertEquals((1..7).toSet(), viewModel.state.value.selectedNumbers)
        assertEquals(
            "Você aumentou a quantidade de números, o valor será alterado. Fique atento ao valor final da sua aposta.",
            viewModel.state.value.warningMessage,
        )
        viewModel.dismissMessage()
        viewModel.toggleNumber(7)
        assertEquals(6, viewModel.state.value.numberQuantity)
        assertEquals((1..6).toSet(), viewModel.state.value.selectedNumbers)
    }

    @Test fun `lowering quantity below selected numbers clears the complete selection`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.chooseNumberQuantity(7)
        (1..7).forEach(viewModel::toggleNumber)
        assertEquals(7, viewModel.state.value.selectedNumbers.size)

        viewModel.chooseNumberQuantity(6)

        assertEquals(6, viewModel.state.value.numberQuantity)
        assertTrue(viewModel.state.value.selectedNumbers.isEmpty())
    }

    @Test fun `recurring and surprise controls are restricted to backend values`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.chooseRecurringDraws(9)
        assertEquals(0, viewModel.state.value.recurringDraws)
        viewModel.chooseRecurringDraws(4)
        assertEquals(4, viewModel.state.value.recurringDraws)
        viewModel.chooseRecurringDraws(0)
        assertEquals(0, viewModel.state.value.recurringDraws)

        viewModel.selectMode(BetEntryMode.SURPRISE)
        repeat(20) { viewModel.increaseSurpriseQuantity() }
        assertEquals(3, viewModel.state.value.surpriseQuantity)
        repeat(20) { viewModel.decreaseSurpriseQuantity() }
        assertEquals(1, viewModel.state.value.surpriseQuantity)
        assertTrue(viewModel.state.value.canAddToCart)
    }

    @Test fun `manual add validates missing numbers and submits sorted selection`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.addToCart()
        assertEquals("Selecione mais 6 números.", viewModel.state.value.errorMessage)
        (1..5).forEach(viewModel::toggleNumber)
        viewModel.addToCart()
        assertEquals("Selecione mais 1 número.", viewModel.state.value.errorMessage)
        viewModel.toggleNumber(6)
        assertTrue(viewModel.state.value.canAddToCart)
        viewModel.chooseRecurringDraws(2)
        viewModel.addToCart()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.addedToCart)
        assertEquals(listOf(1, 2, 3, 4, 5, 6), submitted?.numbers)
        assertFalse(requireNotNull(submitted).surprise)
        assertEquals(6, submitted?.numberQuantity)
        assertEquals(2, submitted?.recurringDraws)
    }

    @Test fun `surprise add sends quantity and configured number quantity`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.selectMode(BetEntryMode.SURPRISE)
        viewModel.increaseSurpriseQuantity()
        viewModel.addToCart()
        advanceUntilIdle()

        assertTrue(requireNotNull(submitted).surprise)
        assertTrue(requireNotNull(submitted).numbers.isEmpty())
        assertEquals(2, submitted?.quantity)
        assertEquals(6, submitted?.numberQuantity)
    }

    @Test fun `clovers obey bounds and participate in manual validation`() = runTest {
        parameterResult = successParameters(game.copy(
            minimumClovers = 1,
            maximumClovers = 2,
            maximumCloverNumber = 3,
            prices = listOf(BetPrice(6, 1, BigDecimal("6")), BetPrice(6, 2, BigDecimal("8"))),
        ))
        val viewModel = viewModel()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.selectedClovers.isEmpty())
        viewModel.toggleClover(2)
        viewModel.toggleClover(3)
        viewModel.toggleClover(4)
        assertEquals(setOf(2, 3), viewModel.state.value.selectedClovers)
        viewModel.toggleClover(2)
        assertEquals(setOf(3), viewModel.state.value.selectedClovers)
    }

    @Test fun `add failures map errors and can be dismissed`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.selectMode(BetEntryMode.SURPRISE)

        addResult = AppResult.Failure(AppError.Network)
        viewModel.addToCart()
        advanceUntilIdle()
        assertEquals("Não foi possível carregar a aposta. Verifique sua internet.", viewModel.state.value.errorMessage)
        viewModel.dismissMessage()
        assertNull(viewModel.state.value.errorMessage)

        addResult = AppResult.Failure(AppError.Validation("Bloqueada"))
        viewModel.addToCart()
        advanceUntilIdle()
        assertEquals("Bloqueada", viewModel.state.value.errorMessage)

        addResult = AppResult.Failure(AppError.Unexpected)
        viewModel.addToCart()
        advanceUntilIdle()
        assertEquals("Ocorreu um erro inesperado ao carregar a aposta.", viewModel.state.value.errorMessage)
    }

    @Test fun `timemania requires searchable team and submits team of the heart`() = runTest {
        val teams = sportsTeams()
        parameterResult = successParameters(
            game(id = TIMEMANIA_ID, name = "Timemania").copy(
                minimumSelection = 10,
                maximumSelection = 10,
                maximumNumber = 80,
                teams = teams,
                prices = listOf(BetPrice(10, 0, BigDecimal("3.50"))),
            ),
        )
        val viewModel = viewModel(TIMEMANIA_ID)
        advanceUntilIdle()
        (1..10).forEach(viewModel::toggleNumber)
        viewModel.addToCart()
        assertEquals("Selecione o time do coração.", viewModel.state.value.errorMessage)

        viewModel.updateTeamQuery("a".repeat(100))
        assertEquals(60, viewModel.state.value.teamQuery.length)
        viewModel.selectTeam(SportsTeam(999, "Inválido"))
        assertNull(viewModel.state.value.selectedTeam)
        viewModel.selectTeam(teams[1])
        assertEquals("Time Verde-SP", viewModel.state.value.teamQuery)
        assertTrue(viewModel.state.value.canAddToCart)
        viewModel.addToCart()
        advanceUntilIdle()
        assertEquals(102, submitted?.favoriteTeam?.number)

        viewModel.clearSelection()
        assertNull(viewModel.state.value.selectedTeam)
        assertTrue(viewModel.state.value.teamQuery.isBlank())
    }

    @Test fun `timemania surprise assigns random team when user did not choose one`() = runTest {
        val teams = sportsTeams()
        val sportsGame = game(id = TIMEMANIA_ID, name = "Timemania").copy(teams = teams)
        parameterResult = successParameters(sportsGame)
        val dispatcher = UnconfinedTestDispatcher()
        val viewModel = BettingGameViewModel(
            modalityId = TIMEMANIA_ID,
            getParameters = GetBettingParametersUseCase { parameterResult },
            addBet = AddBetToCartUseCase { draft -> submitted = draft; addResult },
            dispatchers = FixedCoroutineDispatchers(dispatcher),
            randomTeam = { it.lastOrNull() },
        )
        advanceUntilIdle()
        viewModel.selectMode(BetEntryMode.SURPRISE)
        viewModel.addToCart()
        advanceUntilIdle()
        assertEquals(teams.last(), submitted?.favoriteTeam)
    }

    @Test fun `loteca requires every match and sends simple double and triple outcomes`() = runTest {
        val matches = sportsMatches()
        parameterResult = successParameters(
            game(id = LOTECA_ID, name = "Loteca").copy(
                minimumSelection = 0,
                maximumSelection = 0,
                maximumNumber = 0,
                matches = matches,
                prices = listOf(
                    BetPrice(0, 0, BigDecimal("3.00"), 0, 0),
                    BetPrice(0, 0, BigDecimal("6.00"), 1, 0),
                ),
            ),
        )
        val viewModel = viewModel(LOTECA_ID)
        advanceUntilIdle()
        viewModel.toggleLotecaOutcome(999, LotecaOutcome.HOME)
        viewModel.addToCart()
        assertEquals("Selecione todos os jogos e ao menos um duplo ou triplo válido.", viewModel.state.value.errorMessage)

        matches.forEach { viewModel.toggleLotecaOutcome(it.number, LotecaOutcome.HOME) }
        assertFalse(viewModel.state.value.canAddToCart)
        assertEquals(BigDecimal("3.00"), viewModel.state.value.unitPrice)
        viewModel.toggleLotecaOutcome(matches.first().number, LotecaOutcome.DRAW)
        assertTrue(viewModel.state.value.canAddToCart)
        assertEquals(BigDecimal("6.00"), viewModel.state.value.unitPrice)
        viewModel.toggleLotecaOutcome(matches.first().number, LotecaOutcome.DRAW)
        assertEquals(3, viewModel.state.value.numberQuantity)
        assertEquals(2, viewModel.state.value.lotecaOutcomes.values.sumOf(Set<LotecaOutcome>::size))
        assertFalse(viewModel.state.value.canAddToCart)
        viewModel.toggleLotecaOutcome(matches.first().number, LotecaOutcome.DRAW)
        assertTrue(viewModel.state.value.canAddToCart)
        viewModel.toggleLotecaOutcome(matches.last().number, LotecaOutcome.DRAW)
        assertEquals(1, viewModel.state.value.lotecaOutcomes.values.count { it.size == 2 })
        assertEquals(
            "Essa combinação de simples, duplos e triplos não é permitida para a Loteca. Tente outra combinação.",
            viewModel.state.value.warningMessage,
        )
        viewModel.dismissMessage()
        viewModel.addToCart()
        advanceUntilIdle()
        assertEquals(2, submitted?.lotecaSelections?.size)
        assertTrue(requireNotNull(submitted).lotecaSelections.first().draw)
        assertTrue(requireNotNull(submitted).numbers.isEmpty())
    }

    @Test fun `loteca raises the priced prognosis count and caps selections at twenty six`() = runTest {
        val matches = (1..14).map { number ->
            val teams = sportsTeams()
            SportsMatch(number, teams[0], teams[1])
        }
        val prices = (0..12).map { doubles ->
            BetPrice(
                numberQuantity = 14 + doubles,
                cloverQuantity = 0,
                amount = BigDecimal.valueOf((doubles + 1L) * 3L),
                doubleQuantity = doubles,
                tripleQuantity = 0,
            )
        }
        parameterResult = successParameters(
            game(id = LOTECA_ID, name = "Loteca").copy(
                minimumSelection = 14,
                maximumSelection = 26,
                maximumNumber = 0,
                matches = matches,
                prices = prices,
            ),
        )
        val viewModel = viewModel(LOTECA_ID)
        advanceUntilIdle()

        matches.forEach { viewModel.toggleLotecaOutcome(it.number, LotecaOutcome.HOME) }
        repeat(12) { viewModel.toggleLotecaOutcome(matches[it].number, LotecaOutcome.DRAW) }
        assertEquals(26, viewModel.state.value.numberQuantity)
        assertEquals(BigDecimal("39"), viewModel.state.value.unitPrice)

        viewModel.toggleLotecaOutcome(matches[12].number, LotecaOutcome.DRAW)
        assertEquals(26, viewModel.state.value.lotecaOutcomes.values.sumOf(Set<LotecaOutcome>::size))
        assertEquals(
            "Não é possível selecionar quantidade de números superior ao limite para modalidade Lotérica.",
            viewModel.state.value.warningMessage,
        )
        viewModel.dismissMessage()
        viewModel.toggleLotecaOutcome(matches.first().number, LotecaOutcome.DRAW)
        assertEquals(26, viewModel.state.value.numberQuantity)
        assertEquals(25, viewModel.state.value.lotecaOutcomes.values.sumOf(Set<LotecaOutcome>::size))
        assertFalse(viewModel.state.value.canAddToCart)
        viewModel.toggleLotecaOutcome(matches.first().number, LotecaOutcome.DRAW)
        assertTrue(viewModel.state.value.canAddToCart)

        viewModel.addToCart()
        advanceUntilIdle()
        assertEquals(26, submitted?.numberQuantity)
        assertEquals(26, submitted?.lotecaSelections?.sumOf { selection ->
            listOf(selection.homeWin, selection.draw, selection.awayWin).count { it }
        })
    }

    @Test fun `loteca keeps a selection when deselecting would create an unpriced combination`() = runTest {
        val matches = sportsMatches().take(2)
        parameterResult = successParameters(
            game(id = LOTECA_ID, name = "Loteca").copy(
                minimumSelection = 2,
                maximumSelection = 26,
                maximumNumber = 0,
                matches = matches,
                prices = listOf(BetPrice(4, 0, BigDecimal("12.00"), 0, 1)),
            ),
        )
        // Load a valid triple combination whose resulting double is absent from
        // the price table; removal must be rolled back just like in the legacy adapter.
        val initialSelections = listOf(
            LotecaMatchSelection(matches.first(), homeWin = true, draw = true, awayWin = true),
            LotecaMatchSelection(matches.last(), homeWin = true, draw = false, awayWin = false),
        )
        val seededViewModel = BettingGameViewModel(
            modalityId = LOTECA_ID,
            getParameters = GetBettingParametersUseCase { parameterResult },
            addBet = AddBetToCartUseCase { draft -> submitted = draft; AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList())) },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher()),
            initialLotecaSelections = initialSelections,
        )
        advanceUntilIdle()
        assertEquals(3, seededViewModel.state.value.lotecaOutcomes[matches.first().number]?.size)

        seededViewModel.toggleLotecaOutcome(matches.first().number, LotecaOutcome.AWAY)

        assertEquals(3, seededViewModel.state.value.lotecaOutcomes[matches.first().number]?.size)
        assertEquals(
            "Essa combinação de simples, duplos e triplos não é permitida para a Loteca. Tente outra combinação.",
            seededViewModel.state.value.warningMessage,
        )
    }

    @Test fun `lotogol accepts scores zero through five plus and submits every match`() = runTest {
        val matches = sportsMatches()
        parameterResult = successParameters(
            game(id = LOTOGOL_ID, name = "Lotogol").copy(
                minimumSelection = 0,
                maximumSelection = 0,
                maximumNumber = 0,
                matches = matches,
                prices = listOf(BetPrice(0, 0, BigDecimal("2.00"))),
            ),
        )
        val viewModel = viewModel(LOTOGOL_ID)
        advanceUntilIdle()
        viewModel.selectLotogolScore(999, true, 1)
        viewModel.selectLotogolScore(1, true, 6)
        viewModel.addToCart()
        assertEquals("Informe o placar de todos os jogos.", viewModel.state.value.errorMessage)

        matches.forEachIndexed { index, match ->
            viewModel.selectLotogolScore(match.number, true, index)
            viewModel.selectLotogolScore(match.number, false, 5)
        }
        assertTrue(viewModel.state.value.canAddToCart)
        viewModel.addToCart()
        advanceUntilIdle()
        assertEquals(listOf(0, 1), submitted?.lotogolSelections?.map { it.homeScore })
        assertEquals(listOf(5, 5), submitted?.lotogolSelections?.map { it.awayScore })
    }

    @Test fun `super seven keeps one digit per column and grows the priced quantity`() = runTest {
        parameterResult = successParameters(
            game(id = SUPER_SEVEN_ID, name = "Super Sete").copy(
                maximumNumber = 9,
                minimumSelection = 7,
                maximumSelection = 21,
                prices = listOf(
                    BetPrice(7, 0, BigDecimal("2.50")),
                    BetPrice(8, 0, BigDecimal("5.00")),
                    BetPrice(15, 0, BigDecimal("1250.00")),
                    BetPrice(21, 0, BigDecimal("2500000.00")),
                ),
            ),
        )
        val viewModel = viewModel(SUPER_SEVEN_ID)
        advanceUntilIdle()
        viewModel.toggleSuperSevenDigit(-1, 0)
        viewModel.toggleSuperSevenDigit(0, 10)
        repeat(7) { viewModel.toggleSuperSevenDigit(it, it) }
        assertTrue(viewModel.state.value.canAddToCart)
        viewModel.toggleSuperSevenDigit(0, 0)
        assertTrue(viewModel.state.value.superSevenColumns.first().isEmpty())
        assertEquals(7, viewModel.state.value.numberQuantity)
        assertFalse(viewModel.state.value.canAddToCart)
        viewModel.toggleSuperSevenDigit(0, 0)
        assertEquals(setOf(0), viewModel.state.value.superSevenColumns.first())

        viewModel.toggleSuperSevenDigit(0, 1)
        assertEquals(8, viewModel.state.value.numberQuantity)
        assertEquals(BigDecimal("5.00"), viewModel.state.value.unitPrice)
        assertTrue(viewModel.state.value.canAddToCart)
        viewModel.addToCart()
        advanceUntilIdle()
        assertEquals(7, submitted?.superSevenColumns?.size)
        assertEquals(listOf(0, 1), submitted?.superSevenColumns?.first())
        assertTrue(requireNotNull(submitted).numbers.isEmpty())

        assertTrue(viewModel.state.value.superSevenColumns.all(Set<Int>::isEmpty))
        viewModel.acknowledgeCartAddition()
        repeat(7) { viewModel.toggleSuperSevenDigit(it, it) }
        viewModel.toggleSuperSevenDigit(0, 1)
        viewModel.toggleSuperSevenDigit(0, 2)
        assertEquals(15, viewModel.state.value.numberQuantity)
        viewModel.toggleSuperSevenDigit(0, 3)
        assertTrue(viewModel.state.value.warningMessage?.contains("no máximo 3") == true)
        viewModel.dismissMessage()
        viewModel.chooseNumberQuantity(7)
        assertTrue(viewModel.state.value.superSevenColumns.all(Set<Int>::isEmpty))
        viewModel.clearSelection()
        assertTrue(viewModel.state.value.superSevenColumns.all(Set<Int>::isEmpty))
    }

    @Test fun `super seven only removes from columns at the current limit`() = runTest {
        val columns = listOf(
            listOf(0, 1, 2), listOf(0, 1), listOf(0, 1), listOf(0, 1),
            listOf(0, 1), listOf(0, 1), listOf(0, 1),
        )
        parameterResult = successParameters(
            game(id = SUPER_SEVEN_ID, name = "Super Sete").copy(
                minimumSelection = 7,
                maximumSelection = 21,
                maximumNumber = 9,
                prices = listOf(
                    BetPrice(14, 0, BigDecimal("140.00")),
                    BetPrice(15, 0, BigDecimal("150.00")),
                    BetPrice(21, 0, BigDecimal("210.00")),
                ),
            ),
        )
        val viewModel = BettingGameViewModel(
            modalityId = SUPER_SEVEN_ID,
            getParameters = GetBettingParametersUseCase { parameterResult },
            addBet = AddBetToCartUseCase { AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList())) },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher()),
            initialSuperSevenColumns = columns,
        )
        advanceUntilIdle()

        viewModel.toggleSuperSevenDigit(1, 1)

        assertEquals(columns.map(List<Int>::toSet), viewModel.state.value.superSevenColumns)
        assertEquals(15, viewModel.state.value.numberQuantity)
        assertTrue(viewModel.state.value.canAddToCart)
        assertEquals(
            "Você não pode fazer isso, pois nessa coluna é necessário ter selecionado 2 número(s)",
            viewModel.state.value.warningMessage,
        )
    }

    @Test fun `lowering Super Sete quantity below selected count clears every column`() = runTest {
        val game = game(id = SUPER_SEVEN_ID, name = "Super Sete").copy(
            minimumSelection = 7,
            maximumSelection = 21,
            maximumNumber = 9,
            prices = listOf(
                BetPrice(14, 0, BigDecimal("140.00")),
                BetPrice(15, 0, BigDecimal("150.00")),
                BetPrice(21, 0, BigDecimal("210.00")),
            ),
        )
        parameterResult = successParameters(game)
        val columns = listOf(
            listOf(0, 1, 2), listOf(0, 1), listOf(0, 1), listOf(0, 1),
            listOf(0, 1), listOf(0, 1), listOf(0, 1),
        )
        val viewModel = BettingGameViewModel(
            modalityId = SUPER_SEVEN_ID,
            getParameters = GetBettingParametersUseCase { parameterResult },
            addBet = AddBetToCartUseCase { AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList())) },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher()),
            initialSuperSevenColumns = columns,
        )
        advanceUntilIdle()

        viewModel.chooseNumberQuantity(14)

        assertTrue(viewModel.state.value.superSevenColumns.all(Set<Int>::isEmpty))
    }

    @Test fun `dia de sorte requires backend month in manual and surprise bets`() = runTest {
        val months = listOf(LuckyMonth(1, "Janeiro", "JAN"), LuckyMonth(2, "Fevereiro", "FEV"))
        parameterResult = successParameters(
            game(id = DIA_DE_SORTE_ID, name = "Dia de Sorte").copy(
                minimumSelection = 7,
                maximumSelection = 7,
                maximumNumber = 31,
                prices = listOf(BetPrice(7, 0, BigDecimal("2.50"))),
                luckyMonths = months,
            ),
        )
        val viewModel = viewModel(DIA_DE_SORTE_ID)
        advanceUntilIdle()
        (1..7).forEach(viewModel::toggleNumber)
        assertEquals("Escolher Mês de Sorte", viewModel.state.value.primaryActionLabel)
        viewModel.primaryAction()
        assertEquals(BetSelectionStep.COMPLEMENT, viewModel.state.value.selectionStep)
        viewModel.addToCart()
        assertEquals("Selecione o mês da sorte.", viewModel.state.value.errorMessage)
        viewModel.selectLuckyMonth(LuckyMonth(99, "Inválido"))
        assertNull(viewModel.state.value.selectedLuckyMonth)
        viewModel.selectLuckyMonth(months.last())
        viewModel.clearCurrentStep()
        assertNull(viewModel.state.value.selectedLuckyMonth)
        viewModel.selectLuckyMonth(months.last())
        assertTrue(viewModel.state.value.canAddToCart)
        viewModel.addToCart()
        advanceUntilIdle()
        assertEquals(months.last(), submitted?.luckyMonth)

        // Exercise an independent Surpresinha draft after the acknowledged manual flow.
        val surprise = this@BettingGameViewModelTest.viewModel(DIA_DE_SORTE_ID)
        advanceUntilIdle()
        surprise.selectMode(BetEntryMode.SURPRISE)
        assertFalse(surprise.state.value.canAddToCart)
        surprise.addToCart()
        assertEquals("Informe uma quantidade válida de Surpresinhas.", surprise.state.value.errorMessage)
        surprise.selectLuckyMonth(months.first())
        assertTrue(surprise.state.value.canAddToCart)
    }

    @Test fun `lotomania mirror is offered only for a complete manual bet and doubles its value`() = runTest {
        parameterResult = successParameters(
            game(id = 16, name = "Lotomania").copy(
                maximumNumber = 4,
                minimumSelection = 2,
                maximumSelection = 2,
                acceptsMirror = true,
                prices = listOf(BetPrice(2, 0, BigDecimal("3.00"))),
            ),
        )
        val viewModel = viewModel(16)
        advanceUntilIdle()
        viewModel.toggleMirror()
        assertFalse(viewModel.state.value.generateMirror)
        viewModel.toggleNumber(1)
        viewModel.toggleNumber(2)
        assertEquals(BigDecimal("3.00"), viewModel.state.value.totalPrice)
        viewModel.toggleMirror()
        assertTrue(viewModel.state.value.generateMirror)
        assertEquals(BigDecimal("6.00"), viewModel.state.value.totalPrice)
        viewModel.addToCart()
        advanceUntilIdle()
        assertTrue(requireNotNull(submitted).generateMirror)

        viewModel.toggleNumber(2)
        assertFalse(viewModel.state.value.generateMirror)
        viewModel.selectMode(BetEntryMode.SURPRISE)
        viewModel.toggleMirror()
        assertTrue(viewModel.state.value.generateMirror)
        viewModel.addToCart()
        advanceUntilIdle()
        assertTrue(requireNotNull(submitted).generateMirror)
    }

    @Test fun `manual complementary modalities advance and consume back before leaving`() = runTest {
        val teams = sportsTeams()
        parameterResult = successParameters(
            game(id = TIMEMANIA_ID, name = "Timemania").copy(
                minimumSelection = 2,
                maximumSelection = 2,
                teams = teams,
                prices = listOf(BetPrice(2, 0, BigDecimal("3.50"))),
            ),
        )
        val viewModel = viewModel(TIMEMANIA_ID)
        advanceUntilIdle()
        assertEquals("Escolher Time do Coração", viewModel.state.value.primaryActionLabel)
        assertFalse(viewModel.state.value.canPerformPrimaryAction)
        viewModel.primaryAction()
        assertEquals("Selecione mais 2 números.", viewModel.state.value.errorMessage)
        viewModel.dismissMessage()
        viewModel.toggleNumber(1)
        viewModel.toggleNumber(2)
        assertTrue(viewModel.state.value.canPerformPrimaryAction)
        viewModel.primaryAction()
        assertEquals(BetSelectionStep.COMPLEMENT, viewModel.state.value.selectionStep)
        assertEquals("Adicionar ao carrinho", viewModel.state.value.primaryActionLabel)
        assertTrue(viewModel.returnToNumbers())
        assertEquals(BetSelectionStep.NUMBERS, viewModel.state.value.selectionStep)
        assertFalse(viewModel.returnToNumbers())
        viewModel.primaryAction()
        viewModel.selectTeam(teams.first())
        viewModel.clearCurrentStep()
        assertNull(viewModel.state.value.selectedTeam)
        viewModel.selectTeam(teams.first())
        viewModel.primaryAction()
        advanceUntilIdle()
        assertEquals(teams.first(), submitted?.favoriteTeam)
    }

    @Test fun `plus millionaire surprise preserves backend clover quantity`() = runTest {
        parameterResult = successParameters(
            game(id = MAIS_MILIONARIA_ID, name = "+Milionária").copy(
                minimumClovers = 2,
                maximumClovers = 4,
                maximumCloverNumber = 6,
                prices = listOf(
                    BetPrice(6, 2, BigDecimal("6.00")),
                    BetPrice(6, 3, BigDecimal("18.00")),
                ),
            ),
        )
        val viewModel = viewModel(MAIS_MILIONARIA_ID)
        advanceUntilIdle()
        (1..6).forEach(viewModel::toggleNumber)
        assertEquals("Escolher Trevos", viewModel.state.value.primaryActionLabel)
        viewModel.primaryAction()
        viewModel.toggleClover(1)
        viewModel.clearCurrentStep()
        assertTrue(viewModel.state.value.selectedClovers.isEmpty())
        viewModel.selectMode(BetEntryMode.SURPRISE)
        assertEquals(2, viewModel.state.value.surpriseCloverQuantity)
        repeat(10) { viewModel.increaseSurpriseCloverQuantity() }
        assertEquals(4, viewModel.state.value.surpriseCloverQuantity)
        repeat(10) { viewModel.decreaseSurpriseCloverQuantity() }
        assertEquals(2, viewModel.state.value.surpriseCloverQuantity)
        viewModel.increaseSurpriseCloverQuantity()
        viewModel.primaryAction()
        advanceUntilIdle()
        assertEquals(3, submitted?.cloverQuantity)
        assertTrue(requireNotNull(submitted).clovers.isEmpty())
    }

    @Test fun `edit seed restores clovers month team columns recurring and mirror`() = runTest {
        val teams = sportsTeams()
        val months = listOf(LuckyMonth(9, "Setembro", "SET"))
        val plus = game(id = MAIS_MILIONARIA_ID, name = "+Milionária").copy(
            maximumNumber = 50,
            minimumSelection = 6,
            maximumSelection = 6,
            minimumClovers = 2,
            maximumClovers = 6,
            maximumCloverNumber = 6,
            acceptsMirror = true,
        )
        parameterResult = successParameters(plus)
        val restored = BettingGameViewModel(
            modalityId = MAIS_MILIONARIA_ID,
            getParameters = GetBettingParametersUseCase { parameterResult },
            addBet = AddBetToCartUseCase { draft -> submitted = draft; AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList())) },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher()),
            initialNumbers = listOf(1, 2, 3, 4, 5, 6),
            initialClovers = listOf(2, 5),
            initialRecurringDraws = 4,
            initialMirror = true,
        )
        advanceUntilIdle()
        assertEquals(setOf(1, 2, 3, 4, 5, 6), restored.state.value.selectedNumbers)
        assertEquals(setOf(2, 5), restored.state.value.selectedClovers)
        assertEquals(4, restored.state.value.recurringDraws)
        assertTrue(restored.state.value.generateMirror)

        val superSeven = game(id = SUPER_SEVEN_ID, name = "Super Sete").copy(
            maximumNumber = 9,
            minimumSelection = 7,
            maximumSelection = 8,
            prices = listOf(BetPrice(7, 0, BigDecimal.ONE), BetPrice(8, 0, BigDecimal.TEN)),
        )
        parameterResult = successParameters(superSeven)
        val columns = listOf(listOf(1), listOf(2, 3), listOf(4), listOf(5), listOf(6), listOf(7), listOf(8))
        val restoredColumns = BettingGameViewModel(
            modalityId = SUPER_SEVEN_ID,
            getParameters = GetBettingParametersUseCase { parameterResult },
            addBet = AddBetToCartUseCase { AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList())) },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher()),
            initialSuperSevenColumns = columns,
        )
        advanceUntilIdle()
        assertEquals(columns.map(List<Int>::toSet), restoredColumns.state.value.superSevenColumns)
        assertEquals(8, restoredColumns.state.value.numberQuantity)

        val timemania = game(id = TIMEMANIA_ID, name = "Timemania").copy(teams = teams, luckyMonths = months)
        parameterResult = successParameters(timemania)
        val restoredTeam = BettingGameViewModel(
            modalityId = TIMEMANIA_ID,
            getParameters = GetBettingParametersUseCase { parameterResult },
            addBet = AddBetToCartUseCase { AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList())) },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher()),
            initialTeam = teams.first(),
            initialLuckyMonth = months.first(),
        )
        advanceUntilIdle()
        assertEquals(teams.first(), restoredTeam.state.value.selectedTeam)
        assertEquals(months.first(), restoredTeam.state.value.selectedLuckyMonth)
    }

    @Test fun `edit seed restores Loteca and Lotogol match selections by match number`() = runTest {
        val teams = sportsTeams()
        val matches = listOf(SportsMatch(1, teams[0], teams[1]), SportsMatch(2, teams[2], teams[0]))
        val lotecaGame = game(id = LOTECA_ID, name = "Loteca").copy(
            minimumSelection = 2,
            maximumSelection = 26,
            matches = matches,
            prices = listOf(BetPrice(3, 0, BigDecimal("12.00"), 1, 0)),
        )
        parameterResult = successParameters(lotecaGame)
        val lotecaSeed = listOf(
            LotecaMatchSelection(matches[0], homeWin = true, draw = true, awayWin = false),
            LotecaMatchSelection(matches[1], homeWin = false, draw = false, awayWin = true),
        )
        val loteca = BettingGameViewModel(
            modalityId = LOTECA_ID,
            getParameters = GetBettingParametersUseCase { parameterResult },
            addBet = AddBetToCartUseCase { draft -> submitted = draft; AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList())) },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher()),
            initialLotecaSelections = lotecaSeed,
        )
        advanceUntilIdle()
        assertEquals(setOf(LotecaOutcome.HOME, LotecaOutcome.DRAW), loteca.state.value.lotecaOutcomes[1])
        assertEquals(setOf(LotecaOutcome.AWAY), loteca.state.value.lotecaOutcomes[2])
        assertEquals(3, loteca.state.value.numberQuantity)
        assertTrue(loteca.state.value.canAddToCart)
        loteca.addToCart()
        advanceUntilIdle()
        assertEquals(3, submitted?.numberQuantity)
        assertEquals(3, submitted?.lotecaSelections?.sumOf { selection ->
            listOf(selection.homeWin, selection.draw, selection.awayWin).count { it }
        })

        val lotogolGame = game(id = LOTOGOL_ID, name = "Lotogol").copy(matches = matches)
        parameterResult = successParameters(lotogolGame)
        val lotogol = BettingGameViewModel(
            modalityId = LOTOGOL_ID,
            getParameters = GetBettingParametersUseCase { parameterResult },
            addBet = AddBetToCartUseCase { AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList())) },
            dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher()),
            initialLotogolSelections = listOf(
                LotogolMatchSelection(matches[0], 2, 1),
                LotogolMatchSelection(matches[1], 0, 5),
            ),
        )
        advanceUntilIdle()
        assertEquals(2 to 1, lotogol.state.value.lotogolScores[1])
        assertEquals(0 to 5, lotogol.state.value.lotogolScores[2])
    }

    @Test fun `changing manual clover quantity clears clovers and warns only once without losing numbers`() = runTest {
        parameterResult = successParameters(game(id = MAIS_MILIONARIA_ID).copy(
            minimumClovers = 2, maximumClovers = 4, maximumCloverNumber = 6,
            prices = listOf(BetPrice(6, 2, BigDecimal("6.00")), BetPrice(6, 3, BigDecimal("18.00")), BetPrice(6, 4, BigDecimal("36.00"))),
        ))
        val viewModel = viewModel(MAIS_MILIONARIA_ID)
        advanceUntilIdle()
        (1..6).forEach(viewModel::toggleNumber)
        viewModel.primaryAction()
        viewModel.toggleClover(1)
        viewModel.toggleClover(2)
        assertTrue(viewModel.state.value.canAddToCart)

        viewModel.chooseCloverQuantity(3)
        assertEquals(3, viewModel.state.value.manualCloverQuantity)
        assertEquals((1..6).toSet(), viewModel.state.value.selectedNumbers)
        assertTrue(viewModel.state.value.selectedClovers.isEmpty())
        assertFalse(viewModel.state.value.canAddToCart)
        assertEquals(BigDecimal("18.00"), viewModel.state.value.unitPrice)
        assertTrue(viewModel.state.value.warningMessage?.contains("quantidade de trevos") == true)
        viewModel.dismissMessage()
        (1..3).forEach(viewModel::toggleClover)
        viewModel.chooseCloverQuantity(3)
        viewModel.chooseCloverQuantity(5)
        assertEquals(setOf(1, 2, 3), viewModel.state.value.selectedClovers)
        viewModel.chooseCloverQuantity(4)
        assertNull(viewModel.state.value.warningMessage)
        assertTrue(viewModel.state.value.selectedClovers.isEmpty())
        (1..4).forEach(viewModel::toggleClover)
        viewModel.addToCart()
        advanceUntilIdle()
        assertEquals(listOf(1, 2, 3, 4), submitted?.clovers)
        assertEquals(4, submitted?.cloverQuantity)
    }

    @Test fun `clearing complete selection returns to numbers and keeps purchased quantity and recurrence`() = runTest {
        val teams = sportsTeams()
        parameterResult = successParameters(game(id = TIMEMANIA_ID).copy(teams = teams))
        val viewModel = viewModel(TIMEMANIA_ID)
        advanceUntilIdle()
        viewModel.chooseRecurringDraws(4)
        (1..6).forEach(viewModel::toggleNumber)
        viewModel.primaryAction()
        viewModel.selectTeam(teams.first())
        assertTrue(viewModel.state.value.hasPendingEdits)
        viewModel.clearSelection()
        with(viewModel.state.value) {
            assertEquals(BetSelectionStep.NUMBERS, selectionStep)
            assertTrue(selectedNumbers.isEmpty())
            assertNull(selectedTeam)
            assertEquals("", teamQuery)
            assertEquals(6, numberQuantity)
            assertEquals(4, recurringDraws)
            assertFalse(canAddToCart)
            assertFalse(hasPendingEdits)
        }
        assertNull(submitted)
    }

    private fun viewModel(modalityId: Int? = 2): BettingGameViewModel {
        val dispatcher = UnconfinedTestDispatcher()
        return BettingGameViewModel(
            modalityId = modalityId,
            getParameters = GetBettingParametersUseCase { parameterResult },
            addBet = AddBetToCartUseCase { draft -> submitted = draft; addResult },
            dispatchers = FixedCoroutineDispatchers(dispatcher),
        )
    }

    private fun game(id: Int = 2, name: String = "Mega-Sena") = LotteryGameParameters(
        modality = LotteryModality(id, name, name.uppercase()),
        contestNumber = 2900,
        drawDate = "10/09/2026",
        maximumNumber = 60,
        minimumSelection = 6,
        maximumSelection = 7,
        maximumSurprises = 3,
        recurringOptions = listOf(2, 4),
        prices = listOf(BetPrice(6, 0, BigDecimal("5.00")), BetPrice(7, 0, BigDecimal("35.00"))),
    )

    private fun successParameters(vararg games: LotteryGameParameters) = AppResult.Success(
        BettingParameters(BigDecimal("500"), BigDecimal("30"), 50, games.toList()),
    )

    private fun sportsTeams() = listOf(
        SportsTeam(101, "Time Azul", "DF"),
        SportsTeam(102, "Time Verde", "SP"),
        SportsTeam(103, "Time Branco", "RJ"),
    )

    private fun sportsMatches(): List<SportsMatch> {
        val teams = sportsTeams()
        return listOf(
            SportsMatch(1, teams[0], teams[1]),
            SportsMatch(2, teams[2], teams[0]),
        )
    }
}
