package br.gov.caixa.loterias.feature.marketplace

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.*
import br.gov.caixa.loterias.feature.marketplace.domain.model.*
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.*
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceTab
import br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel.*
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import br.gov.caixa.loterias.feature.retail.domain.usecase.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class MarketplaceRefreshParityTest {
    @get:Rule val rule = MainDispatcherRule()
    private val pool = MarketplacePool("B1", "MEGA_SENA", 3073, "Lotérica", "22/10/2026", "10", 10, 1, 6, false, lotteryId = 10)
    private val retailer = Retailer(10, "0010", "Lotérica", "Lotérica LTDA", "Rua", "Centro", "Brasília", "DF", true)

    @Test fun `late initial search cannot replace a newer retry or its next area`() = runTest(rule.dispatcher) {
        val old = CompletableDeferred<AppResult<MarketplacePage>>()
        var calls = 0
        val vm = model(SearchMarketplaceUseCase { _, _ ->
            if (++calls == 1) withContext(NonCancellable) { old.await() }
            else AppResult.Success(page(pool.copy(code = "new")))
        })
        rule.dispatcher.scheduler.runCurrent()
        vm.retry(); rule.dispatcher.scheduler.runCurrent()
        old.complete(AppResult.Success(page(pool.copy(code = "old"))))
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("new"), vm.state.value.pools.map { it.code })
    }

    @Test fun `old append cannot join a restarted search`() = runTest(rule.dispatcher) {
        val oldAppend = CompletableDeferred<AppResult<MarketplacePage>>()
        var calls = 0
        val vm = model(SearchMarketplaceUseCase { _, _ ->
            when (++calls) {
                1 -> AppResult.Success(page(pool).copy(lastPage = 1))
                2 -> withContext(NonCancellable) { oldAppend.await() }
                else -> AppResult.Success(page(pool.copy(code = "new")))
            }
        })
        rule.dispatcher.scheduler.runCurrent()
        vm.loadMore(); rule.dispatcher.scheduler.runCurrent()
        vm.retry(); rule.dispatcher.scheduler.runCurrent()
        oldAppend.complete(AppResult.Success(page(pool.copy(code = "old-page"))))
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("new"), vm.state.value.pools.map { it.code })
        assertFalse(vm.state.value.isLoadingMore)
    }

    @Test fun `favorite addition reloads retailers and opens native favorites tab`() = runTest(rule.dispatcher) {
        var reads = 0
        val vm = model(SearchMarketplaceUseCase { _, _ -> AppResult.Success(page(pool)) },
            retailers = GetFavoriteRetailersUseCase { AppResult.Success(if (++reads == 1) emptyList() else listOf(retailer)) })
        rule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestFavorite("B1"); vm.confirmFavorite()
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, reads)
        assertEquals(listOf(retailer), vm.state.value.favoriteRetailers)
        assertEquals(MarketplaceTab.FAVORITE_RETAILERS, vm.state.value.selectedTab)
    }

    @Test fun `unfavoriting a pool removes the same retailer from the favorites tab`() = runTest(rule.dispatcher) {
        val vm = model(SearchMarketplaceUseCase { _, _ -> AppResult.Success(page(pool.copy(favoriteLottery = true))) },
            retailers = GetFavoriteRetailersUseCase { AppResult.Success(listOf(retailer)) })
        rule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestFavorite("B1"); vm.confirmFavorite()
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.state.value.favoriteRetailers.isEmpty())
        assertFalse(vm.state.value.pools.single().favoriteLottery)
    }

    @Test fun `late favorite read cannot restore a successfully removed retailer`() = runTest(rule.dispatcher) {
        val pending = CompletableDeferred<AppResult<List<Retailer>>>()
        val vm = model(SearchMarketplaceUseCase { _, _ -> AppResult.Success(page(pool.copy(favoriteLottery = true))) },
            retailers = GetFavoriteRetailersUseCase { withContext(NonCancellable) { pending.await() } })
        rule.dispatcher.scheduler.runCurrent()
        vm.requestFavorite("B1"); vm.confirmFavorite()
        rule.dispatcher.scheduler.runCurrent()
        pending.complete(AppResult.Success(listOf(retailer)))
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.state.value.favoriteRetailers.isEmpty())
    }

    @Test fun `late detail request cannot replace fresh availability`() = runTest(rule.dispatcher) {
        val old = CompletableDeferred<AppResult<MarketplacePoolDetail>>()
        var reads = 0
        val vm = MarketplaceDetailViewModel("B1", GetMarketplaceDetailUseCase {
            if (++reads == 1) withContext(NonCancellable) { old.await() }
            else AppResult.Success(detail(2))
        }, AddMarketplacePoolToCartUseCase { AppResult.Failure(AppError.Network) }, FixedCoroutineDispatchers(rule.dispatcher))
        rule.dispatcher.scheduler.runCurrent(); vm.retry(); rule.dispatcher.scheduler.runCurrent()
        old.complete(AppResult.Success(detail(9)))
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, vm.state.value.detail?.availableShares)
    }

    @Test fun `detail retry during favorite mutation cannot drop its lock`() = runTest(rule.dispatcher) {
        val pending = CompletableDeferred<AppResult<Unit>>()
        var reads = 0
        val vm = MarketplaceDetailViewModel("B1", GetMarketplaceDetailUseCase { reads++; AppResult.Success(detail(9)) },
            AddMarketplacePoolToCartUseCase { AppResult.Failure(AppError.Network) }, FixedCoroutineDispatchers(rule.dispatcher),
            SetRetailerFavoriteUseCase { _, _ -> pending.await() }, initialFavorite = false)
        rule.dispatcher.scheduler.advanceUntilIdle()
        vm.requestFavorite(); vm.confirmFavorite(); rule.dispatcher.scheduler.runCurrent()
        vm.retry(); rule.dispatcher.scheduler.runCurrent()
        assertTrue(vm.state.value.isUpdatingFavorite)
        assertEquals(1, reads)
        pending.complete(AppResult.Success(Unit)); rule.dispatcher.scheduler.advanceUntilIdle()
    }

    @Test fun `unfiltered query failure exposes native empty copy`() = runTest(rule.dispatcher) {
        val vm = model(SearchMarketplaceUseCase { _, _ -> AppResult.Failure(AppError.Network) })
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Não foram encontrados bolões disponíveis para os filtros informados. Limpe os filtros e tente novamente.", vm.state.value.errorMessage)
    }

    @Test fun `applied filter failure exposes native filtered error copy`() = runTest(rule.dispatcher) {
        val vm = MarketplaceViewModel(SearchMarketplaceUseCase { _, _ -> AppResult.Failure(AppError.Network) },
            MarketplaceFilter.empty(), AddMarketplacePoolToCartUseCase { AppResult.Failure(AppError.Network) },
            GetFavoriteRetailersUseCase { AppResult.Success(emptyList()) }, SetRetailerFavoriteUseCase { _, _ -> AppResult.Success(Unit) },
            FixedCoroutineDispatchers(rule.dispatcher), filteredSearch = true)
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Ocorreu um erro.\nTente novamente", vm.state.value.errorMessage)
    }

    private fun page(value: MarketplacePool) = MarketplacePage(listOf(value), 0, 0, 1)
    private fun model(search: SearchMarketplaceUseCase, retailers: GetFavoriteRetailersUseCase = GetFavoriteRetailersUseCase { AppResult.Success(emptyList()) }) =
        MarketplaceViewModel(search, MarketplaceFilter.empty(), AddMarketplacePoolToCartUseCase { AppResult.Failure(AppError.Network) },
            retailers, SetRetailerFavoriteUseCase { _, _ -> AppResult.Success(Unit) }, FixedCoroutineDispatchers(rule.dispatcher))
    private fun detail(available: Int) = MarketplacePoolDetail(
        code = "B1", modality = "MEGA_SENA", contest = 3073, lotteryName = "Lotérica", lotteryId = 10,
        availableShares = available, totalShares = 10, betQuantity = 1, numberQuantity = 6,
        lotteryNumber = "0010", cityAndState = "Brasília - DF", drawDateAndTime = "22/10/2026",
        estimatedPrize = "200000000", sharePriceWithoutFee = "9", lastShareFee = "1", lastSharePriceWithFee = "10", bets = emptyList(),
        lastSharePriceWithFeeValue = BigDecimal.TEN, regularSharePriceWithFee = BigDecimal.TEN,
    )
}
