package br.gov.caixa.loterias.feature.cart

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCart
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet
import br.gov.caixa.loterias.feature.cart.domain.usecase.AddFavoriteCartToCurrentUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartBetUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartBetsUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartsUseCase
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.FavoriteCartsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private const val SPECIAL_BET_ID = 3L
private const val SPECIAL_FIRST_NUMBER = 3
private const val SPECIAL_SECOND_NUMBER = 4

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteCartsExpansionOwnershipTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()

    @Test fun `latest expansion survives an older response arriving last`() = runTest(dispatcherRule.dispatcher) {
        val first = kotlinx.coroutines.CompletableDeferred<AppResult<List<FavoriteCartBet>>>()
        val second = kotlinx.coroutines.CompletableDeferred<AppResult<List<FavoriteCartBet>>>()
        val vm = viewModel(loadBets = { if (it == 1L) first.await() else second.await() })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.toggleCart(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.toggleCart(2)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        second.complete(AppResult.Success(listOf(specialBet())))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2L, vm.state.value.expandedCartId)
        first.complete(AppResult.Success(listOf(normalBet())))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2L, vm.state.value.expandedCartId)
        assertEquals(listOf(specialBet()), vm.state.value.expandedBets)
        assertNull(vm.state.value.loadingCartId)
    }

    @Test fun `collapse invalidates an older pending expansion`() = runTest(dispatcherRule.dispatcher) {
        val pending = kotlinx.coroutines.CompletableDeferred<AppResult<List<FavoriteCartBet>>>()
        val vm = viewModel(loadBets = {
            if (it == 1L) pending.await() else AppResult.Success(listOf(specialBet()))
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.toggleCart(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.toggleCart(2)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.toggleCart(2)
        pending.complete(AppResult.Success(listOf(normalBet())))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertNull(vm.state.value.expandedCartId)
        assertTrue(vm.state.value.expandedBets.isEmpty())
        assertNull(vm.state.value.loadingCartId)
    }

    @Test fun `cache selection supersedes a pending expansion`() = runTest(dispatcherRule.dispatcher) {
        val pending = kotlinx.coroutines.CompletableDeferred<AppResult<List<FavoriteCartBet>>>()
        val vm = viewModel(loadBets = {
            if (it == 1L) pending.await() else AppResult.Success(listOf(specialBet()))
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.toggleCart(2)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.toggleCart(2)
        vm.toggleCart(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.toggleCart(2)
        pending.complete(AppResult.Success(listOf(normalBet())))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2L, vm.state.value.expandedCartId)
        assertEquals(listOf(specialBet()), vm.state.value.expandedBets)
        assertNull(vm.state.value.loadingCartId)
    }

    @Test fun `refresh rejects pending expansion and does not repopulate its invalidated cache`() = runTest(dispatcherRule.dispatcher) {
        val pending = kotlinx.coroutines.CompletableDeferred<AppResult<List<FavoriteCartBet>>>()
        var reads = 0
        val vm = viewModel(loadBets = {
            reads++
            if (reads == 1) pending.await() else AppResult.Success(listOf(specialBet()))
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.toggleCart(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.refreshAfterDetail()
        pending.complete(AppResult.Success(listOf(normalBet())))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertNull(vm.state.value.expandedCartId)
        assertNull(vm.state.value.loadingCartId)
        vm.toggleCart(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, reads)
        assertEquals(listOf(specialBet()), vm.state.value.expandedBets)
    }

    @Test fun `obsolete expansion failure cannot clear the latest loading or show an error`() = runTest(dispatcherRule.dispatcher) {
        val first = kotlinx.coroutines.CompletableDeferred<AppResult<List<FavoriteCartBet>>>()
        val second = kotlinx.coroutines.CompletableDeferred<AppResult<List<FavoriteCartBet>>>()
        val vm = viewModel(loadBets = { if (it == 1L) first.await() else second.await() })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.toggleCart(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.toggleCart(2)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        first.complete(AppResult.Failure(AppError.Network))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2L, vm.state.value.loadingCartId)
        assertNull(vm.state.value.errorMessage)
        second.complete(AppResult.Success(listOf(specialBet())))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2L, vm.state.value.expandedCartId)
        assertEquals(listOf(specialBet()), vm.state.value.expandedBets)
    }

    @Test fun `refresh distinguishes two requests for the same favorite`() = runTest(dispatcherRule.dispatcher) {
        val stale = kotlinx.coroutines.CompletableDeferred<AppResult<List<FavoriteCartBet>>>()
        val fresh = kotlinx.coroutines.CompletableDeferred<AppResult<List<FavoriteCartBet>>>()
        var reads = 0
        val vm = viewModel(loadBets = { if (++reads == 1) stale.await() else fresh.await() })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.toggleCart(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.refreshAfterDetail()
        vm.toggleCart(1)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        fresh.complete(AppResult.Success(listOf(specialBet())))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        stale.complete(AppResult.Success(listOf(normalBet())))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1L, vm.state.value.expandedCartId)
        assertEquals(listOf(specialBet()), vm.state.value.expandedBets)
        vm.toggleCart(1)
        vm.toggleCart(1)
        assertEquals(2, reads)
        assertEquals(listOf(specialBet()), vm.state.value.expandedBets)
    }

    private fun viewModel(loadBets: suspend (Long) -> AppResult<List<FavoriteCartBet>>) = FavoriteCartsViewModel(
        cartId = null,
        getFavoriteCarts = GetFavoriteCartsUseCase { AppResult.Success(listOf(FavoriteCart(1, "Favorito"))) },
        getFavoriteCartBets = GetFavoriteCartBetsUseCase { loadBets(it) },
        deleteFavoriteCart = DeleteFavoriteCartUseCase { error("Expansion cannot delete favorites") },
        deleteFavoriteCartBet = DeleteFavoriteCartBetUseCase { _, _ -> error("Expansion cannot delete bets") },
        addFavoriteCartToCurrent = AddFavoriteCartToCurrentUseCase { _, _ -> error("Expansion cannot import favorites") },
        dispatchers = FixedCoroutineDispatchers(dispatcherRule.dispatcher)
    )

    companion object {
        private fun normalBet() = FavoriteCartBet(2, "Mega-Sena", listOf(1, 2))
        private fun specialBet() = FavoriteCartBet(
            SPECIAL_BET_ID,
            "Mega da Virada",
            listOf(SPECIAL_FIRST_NUMBER, SPECIAL_SECOND_NUMBER),
            special = true
        )
    }
}
