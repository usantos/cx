package br.gov.caixa.loterias.feature.marketplace

import br.gov.caixa.loterias.core.common.*
import br.gov.caixa.loterias.core.domain.marketplace.*
import br.gov.caixa.loterias.core.domain.retail.SetRetailerFavoriteUseCase
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class MarketplaceDetailFavoriteTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()

    @Test fun `favorite asks confirmation and writes only once after acceptance`() = runTest(dispatcherRule.dispatcher) {
        val calls = mutableListOf<Pair<Long, Boolean>>()
        val vm = model(false, SetRetailerFavoriteUseCase { id, favorite -> calls += id to favorite; AppResult.Success(Unit) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestFavorite()
        assertEquals(true, vm.state.value.pendingFavorite)
        assertEquals(false, vm.state.value.favoriteLottery)
        assertTrue(calls.isEmpty())
        vm.confirmFavorite(); vm.confirmFavorite()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf(10L to true), calls)
        assertEquals(true, vm.state.value.favoriteLottery)
        assertNull(vm.state.value.pendingFavorite)
        assertFalse(vm.state.value.isUpdatingFavorite)
    }

    @Test fun `cancel and hidden legacy favorite context never invoke service`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        val service = SetRetailerFavoriteUseCase { _, _ -> calls++; AppResult.Success(Unit) }
        val vm = model(true, service)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestFavorite(); assertEquals(false, vm.state.value.pendingFavorite)
        vm.cancelFavorite(); vm.confirmFavorite()
        val hidden = model(null, service)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        hidden.requestFavorite(); hidden.confirmFavorite()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertNull(hidden.state.value.favoriteLottery)
        assertEquals(0, calls)
    }

    @Test fun `failed favorite retains old state and error is dismissible`() = runTest(dispatcherRule.dispatcher) {
        val vm = model(true, SetRetailerFavoriteUseCase { _, _ -> AppResult.Failure(AppError.Network) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestFavorite(); vm.confirmFavorite()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(true, vm.state.value.favoriteLottery)
        assertNotNull(vm.state.value.favoriteError)
        vm.dismissFavoriteError()
        assertNull(vm.state.value.favoriteError)
    }

    @Test fun `footer and service order use last share plus regular shares without rounding monetary inputs`() = runTest(dispatcherRule.dispatcher) {
        var order: MarketplacePoolOrder? = null
        val vm = MarketplaceDetailViewModel("B1", GetMarketplaceDetailUseCase { AppResult.Success(detail()) },
            AddMarketplacePoolToCartUseCase { order = it; AppResult.Failure(AppError.Network) },
            FixedCoroutineDispatchers(dispatcherRule.dispatcher))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.increaseShares()
        assertEquals(BigDecimal("18.50"), vm.state.value.purchaseAmount)
        vm.addToCart()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(BigDecimal("18.50"), order?.totalAmount)
        assertEquals("MEGA_SENA", order?.modality)
    }

    @Test fun `first resume reuses initial GET and later resume reloads resets quantity and preserves changed favorite`() = runTest(dispatcherRule.dispatcher) {
        var loads = 0
        val vm = MarketplaceDetailViewModel("B1", GetMarketplaceDetailUseCase {
            loads++
            AppResult.Success(detail().copy(availableShares = if (loads == 1) 81 else 79))
        }, AddMarketplacePoolToCartUseCase { AppResult.Failure(AppError.Network) },
            FixedCoroutineDispatchers(dispatcherRule.dispatcher),
            SetRetailerFavoriteUseCase { _, _ -> AppResult.Success(Unit) }, false)
        vm.onResume()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, loads)
        vm.increaseShares()
        vm.requestFavorite(); vm.confirmFavorite()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, vm.state.value.selectedShares)
        assertEquals(true, vm.state.value.favoriteLottery)
        vm.onResume()
        assertTrue(vm.state.value.isLoading)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, loads)
        assertEquals(79, vm.state.value.detail?.availableShares)
        assertEquals(1, vm.state.value.selectedShares)
        assertEquals(true, vm.state.value.favoriteLottery)
        vm.onResume()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(3, loads)
    }

    private fun model(initial: Boolean?, service: SetRetailerFavoriteUseCase) = MarketplaceDetailViewModel("B1",
        GetMarketplaceDetailUseCase { AppResult.Success(detail()) },
        AddMarketplacePoolToCartUseCase { AppResult.Failure(AppError.Network) },
        FixedCoroutineDispatchers(dispatcherRule.dispatcher), service, initial)

    private fun detail() = MarketplacePoolDetail("B1", "MEGA_SENA", 3070, 10, "Lotérica", "1", "Cidade - SP",
        "14/10/2026", "150000000.00", 81, 100, 8, 9, "10.00", "0.50", "10.50", emptyList(),
        lastSharePriceWithFeeValue = BigDecimal("10.50"), regularSharePriceWithFee = BigDecimal("8.00"))
}
