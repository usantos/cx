package br.gov.caixa.loterias.feature.cart

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet
import br.gov.caixa.loterias.feature.cart.domain.usecase.AddFavoriteCartToCurrentUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartBetUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartBetsUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.MarkFavoriteCartAddedUseCase
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.FavoriteCartsViewModel
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
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteCartsQueuedWriteAvailabilityTest {
    private val bets = listOf(FavoriteCartBet(1, "Mega-Sena", listOf(1)), FavoriteCartBet(2, "Lotofácil", listOf(2)))
    private val cart = ShoppingCart(1, BigDecimal.TEN, emptyList())

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `queued cart deletion preserves selected favorite after off`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var deletes = 0
        val vm = model(dispatcher, { enabled }, deleteCart = {
            deletes++
            AppResult.Success(emptyList())
        })
        vm.requestCartRemoval(1)
        vm.confirmCartRemoval()
        enabled = false
        runCurrent()
        assertEquals(0, deletes)
        assertEquals(1L, vm.state.value.pendingCartRemoval)
    }

    @Test fun `queued bet deletion preserves selected bet after off`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var deletes = 0
        val vm = model(dispatcher, { enabled }, deleteBet = { _, _ ->
            deletes++
            AppResult.Success(bets)
        })
        vm.load()
        runCurrent()
        vm.requestBetRemoval(1)
        vm.confirmBetRemoval()
        enabled = false
        runCurrent()
        assertEquals(0, deletes)
        assertEquals(1L, vm.state.value.pendingBetRemoval)
        assertEquals(bets, vm.state.value.bets)
    }

    @Test fun `an import queued before off cannot persist a marker or add a cart`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var markers = 0
        var additions = 0
        val vm = model(
            dispatcher,
            { enabled },
            cartId = null,
            mark = { markers++ },
            add = { _, _ ->
                additions++
                AppResult.Success(cart)
            }
        )
        vm.requestImport(1)
        enabled = false
        runCurrent()
        assertEquals(0, markers)
        assertEquals(0, additions)
    }

    @Test fun `off during favorite bets read blocks marker and remote addition`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var markers = 0
        var additions = 0
        val read = CompletableDeferred<AppResult<List<FavoriteCartBet>>>()
        val vm = model(
            dispatcher,
            { enabled },
            cartId = null,
            getBets = { read.await() },
            mark = { markers++ },
            add = { _, _ ->
                additions++
                AppResult.Success(cart)
            }
        )
        vm.requestImport(1)
        runCurrent()
        enabled = false
        read.complete(AppResult.Success(bets))
        runCurrent()
        assertEquals(0, markers)
        assertEquals(0, additions)
    }

    @Test fun `off during durable marker preserves the marker but sends no cart addition`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var markers = 0
        var additions = 0
        val marker = CompletableDeferred<Unit>()
        val vm = model(
            dispatcher,
            { enabled },
            cartId = null,
            mark = {
                markers++
                marker.await()
            },
            add = { _, _ ->
                additions++
                AppResult.Success(cart)
            }
        )
        vm.requestImport(1)
        runCurrent()
        assertEquals(1, markers)
        enabled = false
        marker.complete(Unit)
        runCurrent()
        assertEquals(1, markers)
        assertEquals(0, additions)
    }

    @Test fun `off while another import holds mutex preserves sent result and blocks waiting import`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        val markers = mutableListOf<Long>()
        val additions = mutableListOf<Long>()
        val first = CompletableDeferred<AppResult<ShoppingCart>>()
        val vm = model(
            dispatcher,
            { enabled },
            cartId = null,
            mark = { markers += it },
            add = { id, _ ->
                additions += id
                if (id == 1L) first.await() else AppResult.Success(cart)
            }
        )
        vm.requestImport(1)
        runCurrent()
        vm.requestImport(2)
        runCurrent()
        enabled = false
        first.complete(AppResult.Success(cart))
        runCurrent()
        assertEquals(listOf(1L), markers)
        assertEquals(listOf(1L), additions)
        assertEquals(true, vm.state.value.imported)
    }

    private fun model(
        dispatcher: CoroutineDispatcher,
        allowed: () -> Boolean,
        cartId: Long? = 1,
        getBets: GetFavoriteCartBetsUseCase = GetFavoriteCartBetsUseCase { AppResult.Success(bets) },
        deleteCart: DeleteFavoriteCartUseCase = DeleteFavoriteCartUseCase { error("No deletion expected") },
        deleteBet: DeleteFavoriteCartBetUseCase = DeleteFavoriteCartBetUseCase { _, _ -> error("No bet deletion expected") },
        mark: MarkFavoriteCartAddedUseCase = MarkFavoriteCartAddedUseCase { },
        add: AddFavoriteCartToCurrentUseCase = AddFavoriteCartToCurrentUseCase { _, _ -> error("No addition expected") },
    ) = FavoriteCartsViewModel(
        cartId = cartId, getFavoriteCarts = { AppResult.Success(emptyList()) }, getFavoriteCartBets = getBets,
        deleteFavoriteCart = deleteCart, deleteFavoriteCartBet = deleteBet, addFavoriteCartToCurrent = add,
        markFavoriteCartAdded = mark, autoLoad = false, canStartOperation = allowed, dispatchers = FixedCoroutineDispatchers(dispatcher)
    )
}
