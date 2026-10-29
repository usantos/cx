package br.gov.caixa.loterias.feature.cart

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.usecase.ClearCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.SaveCurrentCartFavoriteUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision
import br.gov.caixa.loterias.feature.cart.domain.usecase.VerifyCartPaymentAccountUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.CheckCheckoutLocationUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.CheckoutLocationStatus
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordCheckoutLocationRequestUseCase
import java.math.BigDecimal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.cart.presentation.state.CartLocationAction
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.CartViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()

    @Test
    fun `loads cart and removes the confirmed item`() = runTest(dispatcherRule.dispatcher) {
        var removed: CartItem? = null
        val viewModel = viewModel(remove = {
            removed = it
            AppResult.Success(emptyCart())
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(cart(), viewModel.state.value.cart)
        assertFalse(viewModel.state.value.loading)
        viewModel.requestRemoval(item())
        assertEquals(item(), viewModel.state.value.pendingRemoval)
        viewModel.confirmRemoval()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(item(), removed)
        assertEquals(emptyCart(), viewModel.state.value.cart)
        assertNull(viewModel.state.value.pendingRemoval)
    }

    @Test
    fun `dismisses removal and clear confirmations`() = runTest(dispatcherRule.dispatcher) {
        val viewModel = viewModel()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        viewModel.requestRemoval(item())
        viewModel.requestClear()
        assertTrue(viewModel.state.value.clearConfirmation)
        viewModel.dismissConfirmation()
        assertNull(viewModel.state.value.pendingRemoval)
        assertFalse(viewModel.state.value.clearConfirmation)
    }

    @Test
    fun `clears the cart after confirmation`() = runTest(dispatcherRule.dispatcher) {
        var clearCalls = 0
        val viewModel = viewModel(clear = {
            clearCalls++
            AppResult.Success(emptyCart())
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        viewModel.requestClear()
        viewModel.confirmClear()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, clearCalls)
        assertEquals(emptyCart().copy(totalBetCount = 0), viewModel.state.value.cart)
        assertFalse(viewModel.state.value.clearConfirmation)
    }

    @Test
    fun `successful confirmed clear empties a stale populated response without refetch`() = runTest(dispatcherRule.dispatcher) {
        var loads = 0
        val viewModel = viewModel(
            load = { loads++; AppResult.Success(cart()) },
            clear = { AppResult.Success(cart().copy(totalBetCount = 7)) },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.requestClear()
        viewModel.confirmClear()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        val cleared = viewModel.state.value.cart!!
        assertEquals(5L, cleared.id)
        assertTrue(cleared.items.isEmpty())
        assertEquals(BigDecimal.ZERO, cleared.total)
        assertEquals(0, cleared.betCount)
        assertNull(viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.loading)
        assertEquals(1, loads)
    }

    @Test
    fun `confirmation without pending item does not remove anything`() = runTest(dispatcherRule.dispatcher) {
        var removeCalls = 0
        val viewModel = viewModel(remove = {
            removeCalls++
            AppResult.Success(emptyCart())
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        viewModel.confirmRemoval()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, removeCalls)
        assertEquals(cart(), viewModel.state.value.cart)
    }

    @Test
    fun `maps every load failure and clears the previous error on retry`() = runTest(dispatcherRule.dispatcher) {
        val cases = listOf(
            AppError.Network to "Não foi possível carregar o carrinho. Verifique sua internet.",
            AppError.Validation("") to "Não foi possível atualizar o carrinho.",
            AppError.Validation("Carrinho expirado") to "Carrinho expirado",
            AppError.Unexpected to "Ocorreu um erro inesperado ao atualizar o carrinho.",
        )
        cases.forEach { (error, message) ->
            var fail = true
            val viewModel = viewModel(load = {
                if (fail) AppResult.Failure(error) else AppResult.Success(cart())
            })
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(message, viewModel.state.value.errorMessage)
            assertFalse(viewModel.state.value.loading)

            fail = false
            viewModel.load()
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertNull(viewModel.state.value.errorMessage)
            assertEquals(cart(), viewModel.state.value.cart)
        }
    }

    @Test
    fun `keeps loaded cart when removing one item fails`() = runTest(dispatcherRule.dispatcher) {
        val viewModel = viewModel(
            remove = { AppResult.Failure(AppError.Network) },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        viewModel.requestRemoval(item())
        viewModel.confirmRemoval()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(cart(), viewModel.state.value.cart)
        assertEquals("Não foi possível carregar o carrinho. Verifique sua internet.", viewModel.state.value.errorMessage)
    }

    @Test
    fun `failed confirmed clear empties displayed cart without refetch and preserves error notice`() = runTest(dispatcherRule.dispatcher) {
        var loadCalls = 0
        val viewModel = viewModel(
            load = { loadCalls++; AppResult.Success(cart().copy(totalBetCount = 7)) },
            clear = { AppResult.Failure(AppError.Validation("Não autorizado")) },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.requestClear()
        viewModel.confirmClear()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        val cleared = viewModel.state.value.cart!!
        assertEquals(5L, cleared.id)
        assertTrue(cleared.items.isEmpty())
        assertEquals(BigDecimal.ZERO, cleared.total)
        assertEquals(0, cleared.betCount)
        assertFalse(viewModel.state.value.loading)
        assertEquals("Não autorizado", viewModel.state.value.errorMessage)
        assertEquals(1, loadCalls)
    }

    @Test
    fun `loads cart limits and saves an authenticated favorite`() = runTest(dispatcherRule.dispatcher) {
        var savedName: String? = null
        val parameters = BettingParameters(
            dailyLimit = BigDecimal("500"),
            minimumCartAmount = BigDecimal("30"),
            maximumCartBets = 50,
            games = validParameters().games,
        )
        val viewModel = viewModel(
            authenticated = true,
            parameters = { AppResult.Success(parameters) },
            saveFavorite = { name -> savedName = name; AppResult.Success(Unit) },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(BigDecimal("30"), viewModel.state.value.minimumCartAmount)
        assertEquals(50, viewModel.state.value.maximumCartBets)
        viewModel.requestFavorite()
        assertTrue(viewModel.state.value.favoriteNameDialog)
        viewModel.updateFavoriteName("  ${"x".repeat(70)}  ")
        assertEquals(30, viewModel.state.value.favoriteName.length)
        viewModel.submitFavoriteName()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals("x".repeat(28), savedName)
        assertTrue(viewModel.state.value.favoriteSaved)
        assertEquals("", viewModel.state.value.favoriteName)
        viewModel.dismissConfirmation()
        assertFalse(viewModel.state.value.favoriteSaved)
    }

    @Test
    fun `cancel favorite clears draft and error before reopening`() = runTest(dispatcherRule.dispatcher) {
        val model = viewModel(authenticated = true)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        model.requestFavorite()
        model.updateFavoriteName("   ")
        model.submitFavoriteName()
        assertNotNull(model.state.value.errorMessage)
        model.dismissConfirmation()
        assertEquals("", model.state.value.favoriteName)
        assertNull(model.state.value.errorMessage)
        model.requestFavorite()
        assertTrue(model.state.value.favoriteNameDialog)
        assertEquals("", model.state.value.favoriteName)
    }

    @Test
    fun `repeated favorite submission while validating saves the captured name once`() = runTest(dispatcherRule.dispatcher) {
        val release = kotlinx.coroutines.CompletableDeferred<Unit>()
        val saved = mutableListOf<String>()
        var validations = 0
        val model = viewModel(authenticated = true,
            validateFavorite = { validations++; release.await(); AppResult.Success(Unit) },
            saveFavorite = { saved += it; AppResult.Success(Unit) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        model.requestFavorite()
        model.updateFavoriteName("Meu carrinho")
        model.submitFavoriteName()
        model.submitFavoriteName()
        dispatcherRule.dispatcher.scheduler.runCurrent()
        model.updateFavoriteName("Outro carrinho")
        model.submitFavoriteName()
        assertEquals("Meu carrinho", model.state.value.favoriteName)
        release.complete(Unit)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, validations)
        assertEquals(listOf("Meu carrinho"), saved)
    }

    @Test
    fun `favorite flow validates blank duplicate and transport errors`() = runTest(dispatcherRule.dispatcher) {
        var validation: AppResult<Unit> = AppResult.Success(Unit)
        var save: AppResult<Unit> = AppResult.Success(Unit)
        val viewModel = viewModel(
            authenticated = true,
            validateFavorite = { validation },
            saveFavorite = { save },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        viewModel.requestFavorite()
        viewModel.updateFavoriteName("   ")
        viewModel.submitFavoriteName()
        assertEquals("Informe o nome do carrinho favorito.", viewModel.state.value.errorMessage)

        viewModel.updateFavoriteName("Meu carrinho")
        validation = AppResult.Failure(AppError.Network)
        viewModel.submitFavoriteName()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(
            "Não foi possível carregar o carrinho. Verifique sua internet.",
            viewModel.state.value.errorMessage,
        )

        validation = AppResult.Failure(AppError.Validation("Já existe"))
        viewModel.submitFavoriteName()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.state.value.favoriteNameDialog)
        assertTrue(viewModel.state.value.favoriteOverwriteConfirmation)

        save = AppResult.Failure(AppError.Unexpected)
        viewModel.confirmFavoriteOverwrite()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Ocorreu um erro inesperado ao atualizar o carrinho.", viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.favoriteOverwriteConfirmation)
    }

    @Test
    fun `payment delegates empty and populated cart acceptance to validation and consumes navigation`() = runTest(dispatcherRule.dispatcher) {
        var emptyValidations = 0
        val empty = viewModel(load = { AppResult.Success(emptyCart()) }, validateCart = {
            emptyValidations++
            AppResult.Failure(AppError.Validation("Carrinho vazio"))
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        empty.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(empty.state.value.paymentReady)
        assertEquals(1, emptyValidations)
        assertEquals("Carrinho vazio", empty.state.value.errorMessage)

        var validation: AppResult<CartValidationResult> = AppResult.Success(CartValidationResult.Accepted)
        val populated = viewModel(validateCart = { validation })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        populated.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(populated.state.value.paymentReady)
        populated.consumePaymentReady()
        assertFalse(populated.state.value.paymentReady)

        validation = AppResult.Failure(AppError.Validation("Aposta expirada"))
        populated.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Aposta expirada", populated.state.value.errorMessage)
        assertFalse(populated.state.value.paymentReady)
    }

    @Test
    fun `unauthenticated or ineligible carts cannot be favorited`() = runTest(dispatcherRule.dispatcher) {
        val unauthenticated = viewModel(authenticated = false)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        unauthenticated.requestFavorite()
        assertFalse(unauthenticated.state.value.favoriteNameDialog)

        val empty = viewModel(load = { AppResult.Success(emptyCart()) }, authenticated = true)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        empty.requestFavorite()
        assertFalse(empty.state.value.favoriteNameDialog)
    }

    @Test fun `empty simulation parameters open recovery and retry preserves cart`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        var result: AppResult<BettingParameters> = AppResult.Success(validParameters().copy(games = emptyList()))
        val vm = viewModel(parameters = { calls++; result })
        vm.retryParameters()
        vm.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, calls)
        assertTrue(vm.state.value.parametersUnavailable)
        assertFalse(vm.state.value.paymentReady)
        assertEquals(cart(), vm.state.value.cart)
        assertNull(vm.state.value.parameterRetryMessage)

        vm.retryParameters()
        vm.retryParameters()
        vm.dismissParametersUnavailable()
        assertTrue(vm.state.value.parametersUnavailable)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, calls)
        assertEquals("Não foi possível concluir esta operação. Por favor, tente novamente.", vm.state.value.parameterRetryMessage)
        vm.dismissParameterRetryMessage()
        assertNull(vm.state.value.parameterRetryMessage)

        result = AppResult.Failure(AppError.Network)
        vm.retryParameters()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.state.value.parametersUnavailable)
        assertTrue(vm.state.value.parameterRetryMessage != null)
        vm.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(vm.state.value.paymentReady)

        result = AppResult.Success(validParameters())
        vm.retryParameters()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(vm.state.value.parametersUnavailable)
        assertFalse(vm.state.value.loadingParameters)
        assertNull(vm.state.value.parameterRetryMessage)
        assertEquals(BigDecimal("30"), vm.state.value.minimumCartAmount)
        assertEquals(cart(), vm.state.value.cart)
        result = AppResult.Success(validParameters().copy(games = emptyList()))
        vm.retryParameters()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.dismissParametersUnavailable()
        assertFalse(vm.state.value.parametersUnavailable)
    }

    @Test fun `initial cart parameter transport failure keeps cart rather than opening recovery`() = runTest(dispatcherRule.dispatcher) {
        val vm = viewModel(parameters = { AppResult.Failure(AppError.Network) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(vm.state.value.parametersUnavailable)
        assertFalse(vm.state.value.loadingParameters)
        assertNull(vm.state.value.parameterRetryMessage)
        assertEquals(cart(), vm.state.value.cart)
    }

    @Test fun `periodic refresh only fetches expired shares using injected server time`() = runTest(dispatcherRule.dispatcher) {
        var now = java.time.Instant.parse("2026-09-04T12:00:00Z")
        var calls = 0
        val vm = viewModel(load = { calls++; AppResult.Success(poolCart("2026-09-04T12:01:00Z")) },
            serverTime = br.gov.caixa.loterias.core.common.time.ServerTimeSource { now })
        vm.load()
        vm.refreshExpiredReservations()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, calls)
        assertEquals(now, vm.reservationNow())
        vm.refreshExpiredReservations()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, calls)
        now = now.plusSeconds(60)
        vm.refreshExpiredReservations()
        vm.refreshExpiredReservations()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, calls)
        assertEquals(now, vm.reservationNow())
    }

    @Test fun `expired share is refreshed before validation and repeated payment clicks do not duplicate`() = runTest(dispatcherRule.dispatcher) {
        val calls = mutableListOf<String>()
        var refreshed = false
        val vm = viewModel(load = {
            calls += "cart"
            AppResult.Success(if (refreshed) cart().copy(expiredPoolShareRemoved = true) else poolCart("2026-09-04T11:00:00Z"))
        }, validateCart = { calls += "validate"; AppResult.Success(CartValidationResult.Accepted) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        refreshed = true
        vm.requestPayment()
        vm.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("cart", "cart", "validate"), calls)
        assertTrue(vm.state.value.cart!!.expiredPoolShareRemoved)
        assertTrue(vm.state.value.paymentReady)
        assertFalse(vm.state.value.loading)
    }

    @Test fun `expired refresh that empties cart continues gates and displays server rejection`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        var validations = 0
        val vm = viewModel(load = { calls++; AppResult.Success(if (calls == 1) poolCart("2026-09-04T11:00:00Z") else emptyCart()) },
            validateCart = { validations++; AppResult.Failure(AppError.Validation("Carrinho vazio")) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, calls)
        assertEquals(1, validations)
        assertEquals("Carrinho vazio", vm.state.value.errorMessage)
        assertFalse(vm.state.value.paymentReady)
        assertFalse(vm.state.value.loading)
        assertEquals(emptyCart(), vm.state.value.cart)
    }

    @Test fun `refresh failure preserves cart and server validation can reject payment`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        var validations = 0
        val original = poolCart("2026-09-04T11:00:00Z")
        val vm = viewModel(load = { calls++; if (calls == 1) AppResult.Success(original) else AppResult.Failure(AppError.Network) },
            validateCart = { validations++; AppResult.Failure(AppError.Validation("Reserva expirada")) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, validations)
        assertEquals(original, vm.state.value.cart)
        assertFalse(vm.state.value.paymentReady)
        assertEquals("Reserva expirada", vm.state.value.errorMessage)
    }

    @Test fun `account requirement or suspension blocks validation and expired refresh`() = runTest(dispatcherRule.dispatcher) {
        for (decision in listOf(CartPaymentAccountDecision.Suspended,
            CartPaymentAccountDecision.CompleteAccount(br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination.REGISTRATION))) {
            var loads = 0
            var checks = 0
            var validations = 0
            val vm = viewModel(load = { loads++; AppResult.Success(poolCart("2026-09-04T11:00:00Z")) },
                verifyAccount = { checks++; AppResult.Success(decision) },
                validateCart = { validations++; AppResult.Success(CartValidationResult.Accepted) })
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.requestPayment()
            vm.requestPayment()
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.requestPayment()
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(1, checks)
            assertEquals(1, loads)
            assertEquals(0, validations)
            assertEquals(decision, vm.state.value.paymentAccountDecision)
            assertFalse(vm.state.value.paymentReady)
            assertFalse(vm.state.value.loading)
            vm.consumePaymentAccountDecision()
            assertNull(vm.state.value.paymentAccountDecision)
        }
    }

    @Test fun `guest receives login confirmation without validating cart and can retry after login`() = runTest(dispatcherRule.dispatcher) {
        var decision: CartPaymentAccountDecision = CartPaymentAccountDecision.LoginRequired
        var validations = 0
        val vm = viewModel(verifyAccount = { AppResult.Success(decision) },
            validateCart = { validations++; AppResult.Success(CartValidationResult.Accepted) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(CartPaymentAccountDecision.LoginRequired, vm.state.value.paymentAccountDecision)
        assertEquals(0, validations)
        assertEquals(cart(), vm.state.value.cart)
        vm.consumePaymentAccountDecision()
        decision = CartPaymentAccountDecision.Allowed
        vm.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.state.value.paymentReady)
        assertEquals(1, validations)
    }

    @Test fun `account verification failure preserves cart and permits a later retry`() = runTest(dispatcherRule.dispatcher) {
        var result: AppResult<CartPaymentAccountDecision> = AppResult.Failure(AppError.Validation("Sessão expirada"))
        var validations = 0
        val vm = viewModel(verifyAccount = { result }, validateCart = { validations++; AppResult.Success(CartValidationResult.Accepted) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Sessão expirada", vm.state.value.errorMessage)
        assertEquals(cart(), vm.state.value.cart)
        assertFalse(vm.state.value.loading)
        assertEquals(0, validations)
        result = AppResult.Success(CartPaymentAccountDecision.Allowed)
        vm.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.state.value.paymentReady)
        assertEquals(1, validations)
    }

    @Test fun `recent purchase asks review before validation and Sim keeps the cart`() = runTest(dispatcherRule.dispatcher) {
        var checks = 0
        var validations = 0
        val vm = viewModel(recentPurchase = { amount ->
            assertEquals(cart().total, amount)
            checks++
            AppResult.Success(true)
        }, validateCart = { validations++; AppResult.Success(CartValidationResult.Accepted) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.continueRecentPurchase()
        vm.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestPayment()
        assertEquals(1, checks)
        assertEquals(0, validations)
        assertTrue(vm.state.value.recentPurchaseConfirmation)
        vm.reviewRecentPurchase()
        assertFalse(vm.state.value.recentPurchaseConfirmation)
        assertEquals(cart(), vm.state.value.cart)
        vm.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, checks)
        vm.continueRecentPurchase()
        vm.continueRecentPurchase()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, validations)
        assertTrue(vm.state.value.paymentReady)
    }

    @Test fun `Nao on recent purchase keeps login gate and storage failure blocks payment`() = runTest(dispatcherRule.dispatcher) {
        var validations = 0
        var history: AppResult<Boolean> = AppResult.Failure(AppError.Unexpected)
        val vm = viewModel(
            recentPurchase = { history },
            verifyAccount = { AppResult.Success(CartPaymentAccountDecision.LoginRequired) },
            validateCart = { validations++; AppResult.Success(CartValidationResult.Accepted) },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(vm.state.value.loading)
        assertTrue(vm.state.value.errorMessage != null)
        assertNull(vm.state.value.paymentAccountDecision)
        history = AppResult.Success(true)
        vm.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertNull(vm.state.value.paymentAccountDecision)
        vm.continueRecentPurchase()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(CartPaymentAccountDecision.LoginRequired, vm.state.value.paymentAccountDecision)
        assertEquals(0, validations)
    }

    @Test fun `changed cart invalidates prior recent purchase consent`() = runTest(dispatcherRule.dispatcher) {
        var loaded = cart()
        val checkedAmounts = mutableListOf<BigDecimal>()
        val vm = viewModel(load = { AppResult.Success(loaded) }, recentPurchase = {
            checkedAmounts += it
            AppResult.Success(true)
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        loaded = loaded.copy(total = BigDecimal("999"))
        vm.load()
        vm.continueRecentPurchase() // Ignora enquanto a atualização está em andamento.
        assertTrue(vm.state.value.recentPurchaseConfirmation)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.continueRecentPurchase()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf(cart().total, BigDecimal("999")), checkedAmounts)
        assertTrue(vm.state.value.recentPurchaseConfirmation)
        assertFalse(vm.state.value.paymentReady)
    }

    @Test fun `checkout evaluates location between reservation refresh and recent purchases`() = runTest(dispatcherRule.dispatcher) {
        val events = mutableListOf<String>()
        val vm = viewModel(
            load = { events += "cart"; AppResult.Success(poolCart("2026-09-04T11:59:00Z")) },
            verifyAccount = { events += "account"; AppResult.Success(CartPaymentAccountDecision.Allowed) },
            location = { events += "location"; AppResult.Success(CheckoutLocationStatus.IN_BRAZIL) },
            recentPurchase = { events += "history"; AppResult.Success(false) },
            validateCart = { events += "validate"; AppResult.Success(CartValidationResult.Accepted) },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        events.clear()
        vm.requestPayment()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("account", "cart", "location", "history", "validate"), events)
        assertTrue(vm.state.value.paymentReady)
    }

    @Test fun `outside country or unknown location preserves cart and never checks history or validates`() = runTest(dispatcherRule.dispatcher) {
        for (result in listOf(
            AppResult.Success(CheckoutLocationStatus.OUTSIDE_BRAZIL),
            AppResult.Success(CheckoutLocationStatus.UNAVAILABLE),
            AppResult.Failure(AppError.Network),
        )) {
            var checks = 0
            val vm = viewModel(location = { checks++; result }, recentPurchase = { error("History must wait") })
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.requestPayment()
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.requestPayment()
            vm.acceptLocationConsent()
            assertEquals(1, checks)
            assertFalse(vm.state.value.loading)
            assertFalse(vm.state.value.paymentReady)
            assertEquals(cart(), vm.state.value.cart)
            assertTrue(vm.dismissLocationBlock())
            assertFalse(vm.dismissLocationBlock())
            assertNull(vm.state.value.locationPrompt)
        }
    }

    @Test fun `declining any location consent shows blocked alert without Android action`() = runTest(dispatcherRule.dispatcher) {
        for (status in listOf(CheckoutLocationStatus.PERMISSION_REQUIRED, CheckoutLocationStatus.PERMISSION_SETTINGS_REQUIRED, CheckoutLocationStatus.PROVIDER_DISABLED)) {
            val vm = viewModel(location = { AppResult.Success(status) })
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.declineLocationConsent()
            vm.acceptLocationConsent()
            vm.locationLaunchFailed()
            vm.locationPermissionResult(true, false)
            vm.locationSettingsReturned()
            assertFalse(vm.dismissLocationBlock())
            vm.requestPayment()
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(status, vm.state.value.locationPrompt)
            vm.declineLocationConsent()
            assertEquals(CheckoutLocationStatus.UNAVAILABLE, vm.state.value.locationPrompt)
            assertNull(vm.state.value.locationAction)
            assertNull(vm.state.value.awaitingLocationAction)
            assertEquals(cart(), vm.state.value.cart)
            assertTrue(vm.dismissLocationBlock())
        }
    }

    @Test fun `permission is launched once and granted result rechecks country before history`() = runTest(dispatcherRule.dispatcher) {
        var status = CheckoutLocationStatus.PERMISSION_REQUIRED
        var requests = 0
        val vm = viewModel(location = { AppResult.Success(status) },
            recordLocationRequest = { requests++; AppResult.Success(Unit) },
            recentPurchase = { AppResult.Success(true) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestPayment(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.acceptLocationConsent(); vm.acceptLocationConsent()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, requests)
        assertFalse(vm.takeLocationAction(CartLocationAction.PROVIDER_SETTINGS))
        assertTrue(vm.takeLocationAction(CartLocationAction.REQUEST_PERMISSION))
        assertFalse(vm.takeLocationAction(CartLocationAction.REQUEST_PERMISSION))
        vm.locationSettingsReturned()
        vm.requestPayment()
        assertEquals(CartLocationAction.REQUEST_PERMISSION, vm.state.value.awaitingLocationAction)
        status = CheckoutLocationStatus.IN_BRAZIL
        vm.locationPermissionResult(true, false)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.state.value.recentPurchaseConfirmation)
        assertFalse(vm.state.value.paymentReady)
        assertNull(vm.state.value.awaitingLocationAction)
        vm.locationPermissionResult(false, true)
        assertNull(vm.state.value.locationAction)
    }

    @Test fun `denied permission blocks or opens app settings and rechecks on return`() = runTest(dispatcherRule.dispatcher) {
        for (permanent in listOf(false, true)) {
            var status = CheckoutLocationStatus.PERMISSION_REQUIRED
            val vm = viewModel(location = { AppResult.Success(status) })
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.requestPayment(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.acceptLocationConsent(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertTrue(vm.takeLocationAction(CartLocationAction.REQUEST_PERMISSION))
            vm.locationPermissionResult(false, permanent)
            if (permanent) {
                assertEquals(CartLocationAction.PERMISSION_SETTINGS, vm.state.value.locationAction)
                assertTrue(vm.takeLocationAction(CartLocationAction.PERMISSION_SETTINGS))
                status = CheckoutLocationStatus.IN_BRAZIL
                vm.locationSettingsReturned(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
                assertTrue(vm.state.value.paymentReady)
            } else {
                assertEquals(CheckoutLocationStatus.UNAVAILABLE, vm.state.value.locationPrompt)
                assertFalse(vm.state.value.paymentReady)
            }
        }
    }

    @Test fun `settings consent never requests permissions automatically and launch failure blocks`() = runTest(dispatcherRule.dispatcher) {
        for ((status, action) in listOf(
            CheckoutLocationStatus.PROVIDER_DISABLED to CartLocationAction.PROVIDER_SETTINGS,
            CheckoutLocationStatus.PERMISSION_SETTINGS_REQUIRED to CartLocationAction.PERMISSION_SETTINGS,
        )) {
            val vm = viewModel(location = { AppResult.Success(status) }, recordLocationRequest = { error("No permission request") })
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.requestPayment(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.acceptLocationConsent(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertTrue(vm.takeLocationAction(action))
            vm.locationSettingsReturned(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(status, vm.state.value.locationPrompt)
            vm.acceptLocationConsent(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.locationLaunchFailed()
            assertEquals(CheckoutLocationStatus.UNAVAILABLE, vm.state.value.locationPrompt)
            assertNull(vm.state.value.locationAction)
            assertFalse(vm.state.value.loading)
        }
    }

    @Test fun `location errors and cancellation cannot leave checkout loading or authorize it`() = runTest(dispatcherRule.dispatcher) {
        val cancelled = viewModel(location = { throw kotlinx.coroutines.CancellationException("geocoder") })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        cancelled.requestPayment(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(cancelled.state.value.loading)
        assertEquals(CheckoutLocationStatus.UNAVAILABLE, cancelled.state.value.locationPrompt)
        for (cancel in listOf(false, true)) {
            val vm = viewModel(location = { AppResult.Success(CheckoutLocationStatus.PERMISSION_REQUIRED) }, recordLocationRequest = {
                if (cancel) throw kotlinx.coroutines.CancellationException("request")
                AppResult.Failure(AppError.Unexpected)
            })
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.requestPayment(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.acceptLocationConsent(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(CheckoutLocationStatus.UNAVAILABLE, vm.state.value.locationPrompt)
            assertFalse(vm.state.value.loading)
            assertNull(vm.state.value.locationAction)
        }
    }

    @Test fun `empty cart after settings return uses current total then authoritative validation`() = runTest(dispatcherRule.dispatcher) {
        var loaded = cart()
        var status = CheckoutLocationStatus.PROVIDER_DISABLED
        val calls = mutableListOf<String>()
        val vm = viewModel(load = { AppResult.Success(loaded) }, location = { AppResult.Success(status) },
            recentPurchase = { calls += "recent:$it"; AppResult.Success(false) },
            validateCart = { calls += "validate"; AppResult.Failure(AppError.Validation("Carrinho vazio")) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestPayment(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.acceptLocationConsent(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.takeLocationAction(CartLocationAction.PROVIDER_SETTINGS))
        loaded = emptyCart()
        vm.load(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        status = CheckoutLocationStatus.IN_BRAZIL
        vm.locationSettingsReturned(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(vm.state.value.loading)
        assertFalse(vm.state.value.paymentReady)
        assertEquals(listOf("recent:0", "validate"), calls)
        assertEquals("Carrinho vazio", vm.state.value.errorMessage)
    }

    @Test fun `empty minimum amount and maximum count do not bypass or replace legacy checkout gates`() = runTest(dispatcherRule.dispatcher) {
        for (snapshot in listOf(emptyCart(), cart().copy(total = BigDecimal("10")),
            cart().copy(total = BigDecimal("40"), totalBetCount = 51))) {
            val calls = mutableListOf<String>()
            val vm = viewModel(load = { AppResult.Success(snapshot) },
                parameters = { AppResult.Success(validParameters().copy(minimumCartAmount = BigDecimal("30"), maximumCartBets = 50)) },
                verifyAccount = { calls += "account"; AppResult.Success(CartPaymentAccountDecision.Allowed) },
                location = { calls += "location"; AppResult.Success(CheckoutLocationStatus.IN_BRAZIL) },
                recentPurchase = { calls += "recent:$it"; AppResult.Success(false) },
                validateCart = { calls += "validate"; AppResult.Failure(AppError.Validation("Limite do serviço")) })
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.requestPayment(); vm.requestPayment()
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(listOf("account", "location", "recent:${snapshot.total}", "validate"), calls)
            assertEquals("Limite do serviço", vm.state.value.errorMessage)
            assertFalse(vm.state.value.paymentReady)
            assertFalse(vm.state.value.loading)
            assertEquals(snapshot, vm.state.value.cart)
        }
    }

    @Test fun `empty cart still respects account decision before asking for location`() = runTest(dispatcherRule.dispatcher) {
        for (decision in listOf(CartPaymentAccountDecision.Suspended,
            CartPaymentAccountDecision.CompleteAccount(br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination.REGISTRATION))) {
            val vm = viewModel(load = { AppResult.Success(emptyCart()) }, verifyAccount = { AppResult.Success(decision) },
                location = { error("Account gate must come first") }, validateCart = { error("Account gate must come first") })
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.requestPayment(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(decision, vm.state.value.paymentAccountDecision)
            assertFalse(vm.state.value.paymentReady)
        }
    }

    @Test fun `empty cart asks for login only after location and recent purchase decisions`() = runTest(dispatcherRule.dispatcher) {
        val calls = mutableListOf<String>()
        val vm = viewModel(load = { AppResult.Success(emptyCart()) },
            verifyAccount = { calls += "account"; AppResult.Success(CartPaymentAccountDecision.LoginRequired) },
            location = { calls += "location"; AppResult.Success(CheckoutLocationStatus.IN_BRAZIL) },
            recentPurchase = { calls += "recent:$it"; AppResult.Success(true) },
            validateCart = { error("Must authenticate before validation") })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestPayment(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.state.value.recentPurchaseConfirmation)
        assertNull(vm.state.value.paymentAccountDecision)
        vm.continueRecentPurchase(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("account", "location", "recent:0"), calls)
        assertEquals(CartPaymentAccountDecision.LoginRequired, vm.state.value.paymentAccountDecision)
        assertFalse(vm.state.value.paymentReady)
    }

    @Test fun `missing snapshot does not start checkout while a successful validation is authoritative`() = runTest(dispatcherRule.dispatcher) {
        val missing = viewModel(load = { AppResult.Failure(AppError.Network) }, verifyAccount = { error("No snapshot") })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        missing.requestPayment(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertNull(missing.state.value.cart)
        assertFalse(missing.state.value.paymentReady)
        val loaded = viewModel(load = { AppResult.Success(emptyCart()) }, validateCart = { AppResult.Success(CartValidationResult.Accepted) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        loaded.requestPayment(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(loaded.state.value.paymentReady)
    }

    @Test fun `validation notice blocks duplicate calls and consent navigation is consumed once`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        var availability = br.gov.caixa.loterias.core.common.network.ConnectionAvailability.CONNECTED
        val vm = viewModel(validateCart = {
            calls++
            AppResult.Success(CartValidationResult.Rejected(br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.NoticeRequired(
                br.gov.caixa.loterias.core.domain.service.model.ServiceNotice(br.gov.caixa.loterias.core.domain.service.model.ServiceNoticeReason.LOGIN_REQUIRED))))
        }, network = br.gov.caixa.loterias.core.common.network.NetworkAvailability { availability })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestPayment(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        val notice = vm.state.value.validationFailure.notice!!
        assertFalse(vm.state.value.paymentReady)
        vm.requestPayment(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, calls)
        vm.dismissValidationNotice(notice.copy()); vm.acceptValidationNotice(notice.copy())
        assertTrue(vm.state.value.validationFailure.notice === notice)
        availability = br.gov.caixa.loterias.core.common.network.ConnectionAvailability.DISCONNECTED
        vm.acceptValidationNotice(notice)
        assertEquals(br.gov.caixa.loterias.core.presentation.formatting.LEGACY_OFFLINE, vm.state.value.validationFailure.notice!!.message)
        assertNull(vm.state.value.validationFailure.redirect)
        vm.dismissValidationNotice(vm.state.value.validationFailure.notice!!)
        availability = br.gov.caixa.loterias.core.common.network.ConnectionAvailability.CONNECTED
        vm.requestPayment(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.acceptValidationNotice(vm.state.value.validationFailure.notice!!)
        val event = vm.state.value.validationFailure.redirect!!
        assertFalse(vm.consumeValidationRedirect(br.gov.caixa.loterias.core.presentation.state.ServiceRedirectEvent(event.destination)))
        assertTrue(vm.consumeValidationRedirect(event)); assertFalse(vm.consumeValidationRedirect(event))
        assertEquals(2, calls)
        assertFalse(vm.state.value.paymentReady)
    }

    @Test fun `ignored validation directive keeps cart without payment or invented warning`() = runTest(dispatcherRule.dispatcher) {
        val vm = viewModel(validateCart = { AppResult.Success(CartValidationResult.Rejected(
            br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.Unchanged)) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestPayment(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(vm.state.value.paymentReady)
        assertFalse(vm.state.value.validationFailure.pending)
        assertEquals(cart(), vm.state.value.cart)
        assertFalse(vm.state.value.loading)
    }

    @Test fun `manual removal and clear reserve cart before a same turn expiration tick`() = runTest(dispatcherRule.dispatcher) {
        for (clear in listOf(false, true)) {
            val response = kotlinx.coroutines.CompletableDeferred<AppResult<ShoppingCart>>()
            var calls = 0
            val vm = viewModel(load = { AppResult.Success(poolCart("2026-09-04T11:59:00Z")) },
                remove = { calls++; response.await() }, clear = { calls++; response.await() })
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            if (clear) { vm.requestClear(); vm.confirmClear() }
            else { vm.requestRemoval(item()); vm.confirmRemoval() }
            assertTrue(vm.state.value.loading)
            vm.refreshExpiredReservations()
            dispatcherRule.dispatcher.scheduler.runCurrent()
            assertEquals(1, calls)
            response.complete(AppResult.Success(emptyCart())); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertFalse(vm.state.value.loading)
            assertEquals(if (clear) emptyCart().copy(totalBetCount = 0) else emptyCart(), vm.state.value.cart)
        }
    }

    @Test fun `expiry blocks payment clear manual removal and reload until callback completes`() = runTest(dispatcherRule.dispatcher) {
        val response = kotlinx.coroutines.CompletableDeferred<AppResult<ShoppingCart>>()
        var deleteCalls = 0
        var clearCalls = 0
        var accountCalls = 0
        var loadCalls = 0
        val vm = viewModel(load = { loadCalls++; AppResult.Success(poolCart("2026-09-04T11:59:00Z")) },
            remove = { deleteCalls++; response.await() },
            clear = { clearCalls++; AppResult.Success(emptyCart()) },
            verifyAccount = { accountCalls++; AppResult.Success(CartPaymentAccountDecision.Allowed) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestRemoval(item()); vm.requestClear()
        vm.refreshExpiredReservations()
        // Um clique no mesmo turno de despacho já deve estar bloqueado, antes do início do DELETE.
        assertTrue(vm.state.value.loading)
        vm.requestPayment(); vm.confirmRemoval(); vm.confirmClear(); vm.load()
        dispatcherRule.dispatcher.scheduler.runCurrent()
        assertEquals(1, deleteCalls)
        assertEquals(0, clearCalls)
        assertEquals(0, accountCalls)
        assertEquals(1, loadCalls)
        vm.requestRemoval(item()); vm.requestClear()
        assertNull(vm.state.value.pendingRemoval)
        assertFalse(vm.state.value.clearConfirmation)
        response.complete(AppResult.Failure(AppError.Network))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(vm.state.value.loading)
        vm.requestClear(); vm.confirmClear(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, clearCalls)
    }

    @Test fun `unavailable parameters do not suppress the expiration callback`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        var parameterCallbacks = 0
        val vm = viewModel(load = { AppResult.Success(poolCart("2026-09-04T11:59:00Z")) },
            parameters = { parameterCallbacks++; AppResult.Success(validParameters().copy(games = emptyList())) },
            remove = { calls++; AppResult.Failure(AppError.Network) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.state.value.parametersUnavailable)
        vm.refreshExpiredReservations(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, calls)
        assertEquals(2, parameterCallbacks)
    }

    @Test fun `late initial parameters cannot repopulate ended session`() = runTest(dispatcherRule.dispatcher) {
        val owner = br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity("a".repeat(64))
        val changes = kotlinx.coroutines.flow.MutableStateFlow<br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity?>(owner)
        val sessions = object : br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider {
            override fun captureIdentity() = changes.value
            override fun isCurrent(identity: br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity) = changes.value === identity
        }
        val response = kotlinx.coroutines.CompletableDeferred<AppResult<BettingParameters>>()
        val vm = viewModel(sessions = sessions, changes = changes, parameters = {
            kotlinx.coroutines.withContext(kotlinx.coroutines.NonCancellable) { response.await() }
        })
        dispatcherRule.dispatcher.scheduler.runCurrent()
        changes.value = null; dispatcherRule.dispatcher.scheduler.runCurrent()
        response.complete(AppResult.Success(validParameters())); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(BigDecimal.ZERO, vm.state.value.minimumCartAmount)
        assertNull(vm.state.value.cart)
    }

    @Test fun `expiry removes both shares of same pool only once even on network failure`() = runTest(dispatcherRule.dispatcher) {
        val expired = poolCart("2026-09-04T11:59:00Z")
        val second = expired.items.single().copy(id = 13)
        val snapshot = expired.copy(items = expired.items + second)
        val removed = mutableListOf<Long>()
        val vm = viewModel(load = { AppResult.Success(snapshot) }, remove = {
            removed += it.id
            AppResult.Failure(AppError.Network)
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.refreshExpiredReservations(); vm.refreshExpiredReservations()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.refreshExpiredReservations(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf(12L, 13L), removed)
        assertNull(vm.state.value.pendingRemoval)
        assertEquals(snapshot, vm.state.value.cart)
    }

    @Test fun `expiry allows a new reservation with same item and pool but changed expiration`() = runTest(dispatcherRule.dispatcher) {
        var snapshot = poolCart("2026-09-04T11:58:00Z")
        var calls = 0
        val vm = viewModel(load = { AppResult.Success(snapshot) }, remove = {
            calls++; AppResult.Failure(AppError.Network)
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.refreshExpiredReservations(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        snapshot = poolCart("2026-09-04T11:59:00Z")
        vm.load(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.refreshExpiredReservations(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, calls)
    }

    @Test fun `expiry binds original identity and drops late response after logout or login`() = runTest(dispatcherRule.dispatcher) {
        for (replacement in listOf(null, br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity("b".repeat(64)))) {
            val owner = br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity("a".repeat(64))
            val changes = kotlinx.coroutines.flow.MutableStateFlow<br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity?>(owner)
            val sessions = object : br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider {
                override fun captureIdentity() = changes.value
                override fun isCurrent(identity: br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity) = changes.value === identity
            }
            val response = kotlinx.coroutines.CompletableDeferred<AppResult<ShoppingCart>>()
            var captured: br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity? = null
            var cancelled = false
            val vm = viewModel(load = { AppResult.Success(poolCart("2026-09-04T11:59:00Z")) },
                sessions = sessions, changes = changes, bound = br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveExpiredPoolShareUseCase { _, identity ->
                    captured = identity
                    try { kotlinx.coroutines.withContext(kotlinx.coroutines.NonCancellable) { response.await() } }
                    finally { cancelled = !kotlin.coroutines.coroutineContext[kotlinx.coroutines.Job]!!.isActive }
                })
            dispatcherRule.dispatcher.scheduler.runCurrent()
            vm.refreshExpiredReservations(); dispatcherRule.dispatcher.scheduler.runCurrent()
            assertTrue(captured === owner)
            changes.value = replacement; dispatcherRule.dispatcher.scheduler.runCurrent()
            response.complete(AppResult.Success(emptyCart())); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertTrue(cancelled)
            assertNull(vm.state.value.cart)
            vm.refreshExpiredReservations(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertTrue(captured === owner)
        }
    }

    @Test fun `identity replaced before ticker prevents deletion`() = runTest(dispatcherRule.dispatcher) {
        val owner = br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity("a".repeat(64))
        var current: br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity? = owner
        val sessions = object : br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider {
            override fun captureIdentity() = current
            override fun isCurrent(identity: br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity) = current === identity
        }
        var calls = 0
        val vm = viewModel(load = { AppResult.Success(poolCart("2026-09-04T11:59:00Z")) }, sessions = sessions,
            bound = br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveExpiredPoolShareUseCase { _, _ -> calls++; AppResult.Success(emptyCart()) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        current = br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity("a".repeat(64))
        vm.refreshExpiredReservations(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, calls)
    }

    @Test fun `suspended permission recording publishes no action and prevents another checkout`() = runTest(dispatcherRule.dispatcher) {
        val recorded = kotlinx.coroutines.CompletableDeferred<Unit>()
        var status = CheckoutLocationStatus.PERMISSION_REQUIRED
        var requests = 0
        var countryChecks = 0
        val vm = viewModel(
            location = { countryChecks++; AppResult.Success(status) },
            recordLocationRequest = { requests++; recorded.await(); AppResult.Success(Unit) },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestPayment(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.acceptLocationConsent(); dispatcherRule.dispatcher.scheduler.runCurrent()
        assertTrue(vm.state.value.loading)
        assertNull(vm.state.value.locationAction)
        vm.acceptLocationConsent(); vm.requestPayment(); vm.locationSettingsReturned()
        dispatcherRule.dispatcher.scheduler.runCurrent()
        assertEquals(1, requests)
        assertEquals(1, countryChecks)
        assertFalse(vm.takeLocationAction(CartLocationAction.REQUEST_PERMISSION))
        recorded.complete(Unit)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(vm.state.value.loading)
        assertTrue(vm.takeLocationAction(CartLocationAction.REQUEST_PERMISSION))
        assertFalse(vm.takeLocationAction(CartLocationAction.REQUEST_PERMISSION))
        status = CheckoutLocationStatus.IN_BRAZIL
        vm.locationPermissionResult(true, false)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, countryChecks)
        assertTrue(vm.state.value.paymentReady)
    }

    @Test fun `downstream cancellation retains the location cleanup boundary and allows a new attempt`() = runTest(dispatcherRule.dispatcher) {
        for (cancelDuringHistory in listOf(true, false)) {
            val resume = kotlinx.coroutines.CompletableDeferred<Unit>()
            var cancelAttempt = true
            var cancelledJob: kotlinx.coroutines.Job? = null
            suspend fun cancelAfterSuspension() {
                cancelledJob = kotlin.coroutines.coroutineContext[kotlinx.coroutines.Job]
                resume.await()
                throw kotlinx.coroutines.CancellationException("checkout")
            }
            val vm = viewModel(
                recentPurchase = {
                    if (cancelAttempt && cancelDuringHistory) cancelAfterSuspension()
                    AppResult.Success(false)
                },
                validateCart = {
                    if (cancelAttempt && !cancelDuringHistory) cancelAfterSuspension()
                    AppResult.Success(CartValidationResult.Accepted)
                },
            )
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            vm.requestPayment(); dispatcherRule.dispatcher.scheduler.runCurrent()
            assertTrue(vm.state.value.loading)
            assertFalse(vm.state.value.paymentReady)
            resume.complete(Unit)
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertTrue(cancelledJob!!.isCancelled)
            assertFalse(vm.state.value.loading)
            assertEquals(CheckoutLocationStatus.UNAVAILABLE, vm.state.value.locationPrompt)
            assertNull(vm.state.value.locationAction)
            assertFalse(vm.state.value.paymentReady)
            assertEquals(cart(), vm.state.value.cart)
            cancelAttempt = false
            assertTrue(vm.dismissLocationBlock())
            vm.requestPayment(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertTrue(vm.state.value.paymentReady)
        }
    }

    private fun viewModel(
        load: suspend () -> AppResult<ShoppingCart> = { AppResult.Success(cart()) },
        remove: suspend (CartItem) -> AppResult<ShoppingCart> = { AppResult.Success(emptyCart()) },
        clear: suspend () -> AppResult<ShoppingCart> = { AppResult.Success(emptyCart()) },
        parameters: (suspend () -> AppResult<BettingParameters>)? = null,
        validateCart: (suspend () -> AppResult<CartValidationResult>)? = null,
        validateFavorite: (suspend (String) -> AppResult<Unit>)? = null,
        saveFavorite: (suspend (String) -> AppResult<Unit>)? = null,
        authenticated: Boolean = false,
        recentPurchase: suspend (BigDecimal) -> AppResult<Boolean> = { AppResult.Success(false) },
        location: suspend () -> AppResult<CheckoutLocationStatus> = { AppResult.Success(CheckoutLocationStatus.IN_BRAZIL) },
        recordLocationRequest: suspend () -> AppResult<Unit> = { AppResult.Success(Unit) },
        network: br.gov.caixa.loterias.core.common.network.NetworkAvailability = br.gov.caixa.loterias.core.common.network.NetworkAvailability {
            br.gov.caixa.loterias.core.common.network.ConnectionAvailability.UNKNOWN
        },
        verifyAccount: suspend () -> AppResult<CartPaymentAccountDecision> = { AppResult.Success(CartPaymentAccountDecision.Allowed) },
        serverTime: br.gov.caixa.loterias.core.common.time.ServerTimeSource = br.gov.caixa.loterias.core.common.time.ServerTimeSource {
            java.time.Instant.parse("2026-09-04T12:00:00Z")
        },
        sessions: br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider? = null,
        changes: kotlinx.coroutines.flow.Flow<br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity?>? = null,
        bound: br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveExpiredPoolShareUseCase? = null,
    ) = CartViewModel(
        checkCheckoutLocation = CheckCheckoutLocationUseCase { location() },
        recordLocationRequest = RecordCheckoutLocationRequestUseCase { recordLocationRequest() },
        hasRecentPurchase = br.gov.caixa.loterias.feature.cart.domain.usecase.HasRecentPurchaseUseCase { recentPurchase(it) },
        getCurrentCart = GetCurrentCartUseCase { load() },
        removeCartItem = RemoveCartItemUseCase { remove(it) },
        clearCart = ClearCartUseCase { clear() },
        serverTime = serverTime,
        verifyPaymentAccount = VerifyCartPaymentAccountUseCase { verifyAccount() },
        dispatchers = FixedCoroutineDispatchers(dispatcherRule.dispatcher),
        getBettingParameters = parameters?.let { callback -> GetBettingParametersUseCase { callback() } },
        validateCart = ValidateCartUseCase { validateCart?.invoke() ?: AppResult.Success(CartValidationResult.Accepted) },
        validateFavoriteCartName = validateFavorite?.let { callback -> ValidateFavoriteCartNameUseCase { callback(it) } },
        saveCurrentCartFavorite = saveFavorite?.let { callback -> SaveCurrentCartFavoriteUseCase { callback(it) } },
        authenticated = authenticated,
        networkAvailability = network,
        reservationSessions = sessions,
        reservationSessionChanges = changes,
        removeExpiredPoolShare = bound,
    )

    companion object {
        private fun poolCart(expiresAt: String) = cart().copy(items = listOf(item().copy(
            kind = CartItemKind.POOL_SHARE,
            poolShare = br.gov.caixa.loterias.feature.cart.domain.model.CartPoolShare("B123", 1, 10, expiresAt),
        )))

        private fun validParameters() = BettingParameters(
            BigDecimal("500"), BigDecimal("30"), 50,
            listOf(br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters(
                br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
                2900, "10/09/2026", 60, 6, 20, 15, emptyList(), emptyList(),
            )),
        )

        private fun item() = CartItem(
            id = 12,
            kind = CartItemKind.INDIVIDUAL_BET,
            title = "Mega Sena",
            description = "01  02  03",
            amount = BigDecimal("20.00"),
        )

        private fun cart() = ShoppingCart(
            id = 5,
            total = BigDecimal("20.00"),
            items = listOf(item()),
        )

        private fun emptyCart() = ShoppingCart(5, BigDecimal.ZERO, emptyList())
    }
}
