package br.gov.caixa.loterias.feature.cart

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItemKind
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision
import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult
import br.gov.caixa.loterias.feature.cart.domain.model.CheckoutLocationStatus
import br.gov.caixa.loterias.feature.cart.domain.usecase.ClearCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.SaveCurrentCartFavoriteUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.CartViewModel
import java.math.BigDecimal
import java.time.Instant
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
class CartQueuedWriteAvailabilityTest {
    private val item = CartItem(1, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "01 02 03 04 05 06", BigDecimal.TEN)
    private val cart = ShoppingCart(1, BigDecimal.TEN, listOf(item))

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `queued removal cannot delete after capacity is revoked`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var removals = 0
        val vm = model(dispatcher, { enabled }, remove = {
            removals++
            AppResult.Success(cart)
        })
        runCurrent()
        vm.requestRemoval(item)
        vm.confirmRemoval()
        enabled = false
        runCurrent()
        assertEquals(0, removals)
        assertEquals(item, vm.state.value.pendingRemoval)
        assertEquals(cart, vm.state.value.cart)
        assertFalse(vm.state.value.loading)
    }

    @Test fun `queued clear retains confirmation when capacity is revoked`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var clears = 0
        val vm = model(dispatcher, { enabled }, clear = {
            clears++
            AppResult.Success(cart)
        })
        runCurrent()
        vm.requestClear()
        vm.confirmClear()
        enabled = false
        runCurrent()
        assertEquals(0, clears)
        assertEquals(true, vm.state.value.clearConfirmation)
        assertEquals(cart, vm.state.value.cart)
        assertFalse(vm.state.value.loading)
    }

    @Test fun `queued favorite preserves entered name without validating or saving after off`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var validations = 0
        var saves = 0
        val vm = model(
            dispatcher,
            { enabled },
            validate = {
                validations++
                AppResult.Success(Unit)
            },
            save = {
                saves++
                AppResult.Success(Unit)
            }
        )
        runCurrent()
        vm.requestFavorite()
        vm.updateFavoriteName("Meu carrinho")
        val draft = vm.state.value
        vm.submitFavoriteName()
        enabled = false
        runCurrent()
        assertEquals(0, validations)
        assertEquals(0, saves)
        assertEquals(draft, vm.state.value)
    }

    @Test fun `favorite validation returning after off does not start a save`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var saves = 0
        val validation = CompletableDeferred<AppResult<Unit>>()
        val vm = model(dispatcher, { enabled }, validate = { validation.await() }, save = {
            saves++
            AppResult.Success(Unit)
        })
        runCurrent()
        vm.requestFavorite()
        vm.updateFavoriteName("Meu carrinho")
        val draft = vm.state.value
        vm.submitFavoriteName()
        runCurrent()
        enabled = false
        validation.complete(AppResult.Success(Unit))
        runCurrent()
        assertEquals(0, saves)
        assertEquals(draft, vm.state.value)
    }

    @Test fun `a favorite save already sent completes after off without repeating`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var saves = 0
        val response = CompletableDeferred<AppResult<Unit>>()
        val vm = model(dispatcher, { enabled }, save = {
            saves++
            response.await()
        })
        runCurrent()
        vm.requestFavorite()
        vm.updateFavoriteName("Meu carrinho")
        vm.submitFavoriteName()
        runCurrent()
        assertEquals(1, saves)
        enabled = false
        response.complete(AppResult.Success(Unit))
        runCurrent()
        vm.submitFavoriteName()
        runCurrent()
        assertEquals(1, saves)
        assertEquals(true, vm.state.value.favoriteSaved)
        assertFalse(vm.state.value.loading)
    }

    private fun model(
        dispatcher: CoroutineDispatcher,
        allowed: () -> Boolean,
        remove: RemoveCartItemUseCase = RemoveCartItemUseCase { error("No removal expected") },
        clear: ClearCartUseCase = ClearCartUseCase { error("No clear expected") },
        validate: ValidateFavoriteCartNameUseCase = ValidateFavoriteCartNameUseCase { AppResult.Success(Unit) },
        save: SaveCurrentCartFavoriteUseCase = SaveCurrentCartFavoriteUseCase { error("No favorite expected") },
    ) = CartViewModel(
        getCurrentCart = { AppResult.Success(cart) }, removeCartItem = remove, clearCart = clear,
        serverTime = { Instant.parse("2026-09-07T15:00:00Z") },
        verifyPaymentAccount = { AppResult.Success(CartPaymentAccountDecision.Allowed) },
        validateCart = { AppResult.Success(CartValidationResult.Accepted) }, hasRecentPurchase = { AppResult.Success(false) },
        checkCheckoutLocation = {
            AppResult.Success(CheckoutLocationStatus.IN_BRAZIL)
        }, recordLocationRequest = { AppResult.Success(Unit) },
        authenticated = true, validateFavoriteCartName = validate, saveCurrentCartFavorite = save,
        canStartOperation = allowed, dispatchers = FixedCoroutineDispatchers(dispatcher)
    )
}
