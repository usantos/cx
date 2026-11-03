package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.BetDetailViewModel
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TicketsQueuedRepeatAvailabilityTest {
    private val bet = ConfirmedBet(1, "Mega-Sena", 1, "today", "Ativa", listOf(1), modalityId = 2, contestTypeId = 1)
    private val cart = ShoppingCart(1, BigDecimal.TEN, emptyList())

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `queued repeat cannot add after off and retains recovered bet`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var additions = 0
        val vm = model(dispatcher, { enabled }, add = {
            additions++
            AppResult.Success(cart)
        })
        runCurrent()
        vm.addCurrentBetToCart()
        enabled = false
        runCurrent()
        assertEquals(0, additions)
        assertEquals(bet, vm.state.value.bet)
        assertFalse(vm.state.value.loading)
    }

    @Test fun `repeat parameters returning after off cannot begin addition`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var additions = 0
        val read = CompletableDeferred<AppResult<BettingParameters>>()
        val vm = model(dispatcher, { enabled }, parameters = { read.await() }, add = {
            additions++
            AppResult.Success(cart)
        })
        runCurrent()
        vm.addCurrentBetToCart()
        runCurrent()
        enabled = false
        read.complete(AppResult.Success(parameters(listOf(game("NORMAL")))))
        runCurrent()
        assertEquals(0, additions)
        assertEquals(bet, vm.state.value.bet)
        assertFalse(vm.state.value.loading)
    }

    @Test fun `queued contest confirmation preserves choice when capacity is revoked`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var additions = 0
        val vm = model(
            dispatcher,
            { enabled },
            parameters = { AppResult.Success(parameters(listOf(game("NORMAL"), game("ESPECIAL")))) },
            add = {
                additions++
                AppResult.Success(cart)
            }
        )
        runCurrent()
        vm.addCurrentBetToCart()
        runCurrent()
        vm.selectRepeatContest(1)
        val options = vm.state.value.repeatContestOptions
        vm.confirmRepeatContest()
        enabled = false
        runCurrent()
        assertEquals(0, additions)
        assertEquals(options, vm.state.value.repeatContestOptions)
        assertEquals(1, vm.state.value.selectedRepeatContestIndex)
        assertFalse(vm.state.value.loading)
    }

    @Test fun `queued conversion confirmation preserves its option after off`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var additions = 0
        val vm = model(
            dispatcher,
            { enabled },
            parameters = { AppResult.Success(parameters(listOf(game("ESPECIAL")))) },
            add = {
                additions++
                AppResult.Success(cart)
            }
        )
        runCurrent()
        vm.addCurrentBetToCart()
        runCurrent()
        val option = vm.state.value.repeatConversion
        vm.confirmRepeatConversion()
        enabled = false
        runCurrent()
        assertEquals(0, additions)
        assertEquals(option, vm.state.value.repeatConversion)
        assertFalse(vm.state.value.loading)
    }

    @Test fun `a sent repeat completes after off and does not repeat the request`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var additions = 0
        val response = CompletableDeferred<AppResult<ShoppingCart>>()
        val vm = model(dispatcher, { enabled }, add = {
            additions++
            response.await()
        })
        runCurrent()
        vm.addCurrentBetToCart()
        runCurrent()
        assertEquals(1, additions)
        enabled = false
        response.complete(AppResult.Success(cart))
        runCurrent()
        vm.addCurrentBetToCart()
        runCurrent()
        assertEquals(1, additions)
        assertEquals(true, vm.state.value.cartAdditionConfirmed)
        assertFalse(vm.state.value.loading)
    }

    private fun model(
        dispatcher: CoroutineDispatcher,
        allowed: () -> Boolean,
        parameters: GetBettingParametersUseCase? = null,
        add: AddBetToCartUseCase,
    ) = BetDetailViewModel(
        bet = bet, recoverBet = { AppResult.Success(it) }, downloadBetReceipt = { AppResult.Success(byteArrayOf()) },
        validateFavoriteBet = { AppResult.Success(Unit) }, saveFavoriteBet = { AppResult.Success(Unit) },
        addBetToCart = add, getBettingParameters = parameters,
        canStartOperation = allowed, dispatchers = FixedCoroutineDispatchers(dispatcher)
    )

    private fun parameters(games: List<LotteryGameParameters>) = BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 1, games)

    private fun game(type: String) = LotteryGameParameters(
        LotteryModality(2, "Mega-Sena", "MEGA_SENA"), 1, "today", MAXIMUM_NUMBER, 1, 1, 1, emptyList(), emptyList(), contestType = type
    )
}

private const val MAXIMUM_NUMBER = 60
