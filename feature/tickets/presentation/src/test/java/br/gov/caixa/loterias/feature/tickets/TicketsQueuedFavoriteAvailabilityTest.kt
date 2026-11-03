package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.SavePurchaseAsFavoriteUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.BetDetailViewModel
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel
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
class TicketsQueuedFavoriteAvailabilityTest {
    private val bet = ConfirmedBet(1, "Mega-Sena", 1, "today", "Ativa", listOf(1), modalityId = 2)
    private val purchase = Purchase(1, "today", "Efetivada", "Pix", BigDecimal.TEN)

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `bet favorite queued before off preserves draft without validating or saving`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var validations = 0
        var saves = 0
        val vm =
            betModel(dispatcher, { enabled }, validate = {
                validations++
                AppResult.Success(Unit)
            }, save = {
                saves++
                AppResult.Success(Unit)
            })
        runCurrent()
        vm.toggleFavoriteEditor()
        vm.changeFavoriteName("Minha aposta")
        val draft = vm.state.value
        vm.submitFavorite()
        enabled = false
        runCurrent()
        assertEquals(0, validations)
        assertEquals(0, saves)
        assertEquals(draft, vm.state.value)
    }

    @Test fun `bet favorite validation finishing after off cannot save or discard name`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var saves = 0
        val validation = CompletableDeferred<AppResult<Unit>>()
        val vm = betModel(dispatcher, { enabled }, validate = { validation.await() }, save = {
            saves++
            AppResult.Success(Unit)
        })
        runCurrent()
        vm.toggleFavoriteEditor()
        vm.changeFavoriteName("Minha aposta")
        val draft = vm.state.value
        vm.submitFavorite()
        runCurrent()
        enabled = false
        validation.complete(AppResult.Success(Unit))
        runCurrent()
        assertEquals(0, saves)
        assertEquals(draft, vm.state.value)
    }

    @Test fun `bet favorite already sent completes after off without repeating`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var saves = 0
        val response = CompletableDeferred<AppResult<Unit>>()
        val vm = betModel(dispatcher, { enabled }, save = {
            saves++
            response.await()
        })
        runCurrent()
        vm.toggleFavoriteEditor()
        vm.changeFavoriteName("Minha aposta")
        vm.submitFavorite()
        runCurrent()
        assertEquals(1, saves)
        enabled = false
        response.complete(AppResult.Success(Unit))
        runCurrent()
        vm.submitFavorite()
        runCurrent()
        assertEquals(1, saves)
        assertEquals(true, vm.state.value.favoriteSaved)
        assertFalse(vm.state.value.loading)
    }

    @Test fun `purchase favorite queued before off preserves name and sends no write`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var saves = 0
        val vm = purchaseModel(dispatcher, { enabled }, save = { _, _, _ ->
            saves++
            AppResult.Success(Unit)
        })
        vm.loadPurchase(purchase)
        runCurrent()
        vm.openFavoriteDialog()
        vm.changeFavoriteName("Minha compra")
        val draft = vm.state.value
        vm.validateAndSaveFavorite()
        enabled = false
        runCurrent()
        assertEquals(0, saves)
        assertEquals(draft, vm.state.value)
    }

    @Test fun `purchase favorite validation finishing after off cannot send save`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var saves = 0
        val validation = CompletableDeferred<AppResult<Unit>>()
        val vm =
            purchaseModel(dispatcher, { enabled }, validate = { validation.await() }, save = { _, _, _ ->
                saves++
                AppResult.Success(Unit)
            })
        vm.loadPurchase(purchase)
        runCurrent()
        vm.openFavoriteDialog()
        vm.changeFavoriteName("Minha compra")
        val draft = vm.state.value
        vm.validateAndSaveFavorite()
        runCurrent()
        enabled = false
        validation.complete(AppResult.Success(Unit))
        runCurrent()
        assertEquals(0, saves)
        assertEquals(draft, vm.state.value)
    }

    @Test fun `purchase favorite already sent completes after off once`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var saves = 0
        val response = CompletableDeferred<AppResult<Unit>>()
        val vm = purchaseModel(dispatcher, { enabled }, save = { _, _, _ ->
            saves++
            response.await()
        })
        vm.loadPurchase(purchase)
        runCurrent()
        vm.openFavoriteDialog()
        vm.changeFavoriteName("Minha compra")
        vm.validateAndSaveFavorite()
        runCurrent()
        assertEquals(1, saves)
        enabled = false
        response.complete(AppResult.Success(Unit))
        runCurrent()
        vm.validateAndSaveFavorite()
        runCurrent()
        assertEquals(1, saves)
        assertEquals(true, vm.state.value.favoriteSaved)
    }

    private fun betModel(
        dispatcher: CoroutineDispatcher,
        allowed: () -> Boolean,
        validate: ValidateFavoriteBetUseCase = ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
        save: SaveFavoriteBetUseCase,
    ) = BetDetailViewModel(
        bet = bet,
        recoverBet = { AppResult.Success(it) },
        downloadBetReceipt = { AppResult.Success(byteArrayOf()) },
        validateFavoriteBet = validate,
        saveFavoriteBet = save,
        addBetToCart = { AppResult.Success(ShoppingCart(1, BigDecimal.TEN, emptyList())) },
        canStartOperation = allowed,
        dispatchers = FixedCoroutineDispatchers(dispatcher)
    )

    private fun purchaseModel(
        dispatcher: CoroutineDispatcher,
        allowed: () -> Boolean,
        validate: ValidateFavoriteCartNameUseCase = ValidateFavoriteCartNameUseCase { AppResult.Success(Unit) },
        save: SavePurchaseAsFavoriteUseCase,
    ) = TicketsViewModel(
        getConfirmedBets = { AppResult.Success(emptyList()) },
        getPurchases = { AppResult.Success(emptyList()) },
        getPurchaseBets = { AppResult.Success(listOf(bet)) },
        checkTicket = { AppResult.Success(emptyList()) },
        validateFavoriteName = validate,
        savePurchaseAsFavorite = save,
        elapsedMillis = { 0L },
        canStartOperation = allowed,
        dispatchers = FixedCoroutineDispatchers(dispatcher)
    )
}
