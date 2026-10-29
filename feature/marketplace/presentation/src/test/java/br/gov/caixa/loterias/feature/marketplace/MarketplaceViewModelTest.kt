package br.gov.caixa.loterias.feature.marketplace

import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.CartPoolShare
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePage
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolOrder
import br.gov.caixa.loterias.feature.marketplace.domain.repository.MarketplaceRepository
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.SearchMarketplace
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetFavoriteRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceAction
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceTab
import br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class MarketplaceViewModelTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()

    private val pool = MarketplacePool(
        code = "B1",
        modality = "Mega-Sena",
        contest = 2900,
        lotteryName = "Lotérica",
        drawDate = "01/01/2026",
        sharePrice = "12.50",
        availableShares = 3,
        betQuantity = 2,
        numberQuantity = 6,
        favoriteLottery = false,
        lotteryId = 10,
        cityAndState = "Brasília - DF",
        totalShares = 10,
        contestType = 1,
        estimatedPrize = "1000000",
        regularSharePriceWithFee = BigDecimal.TEN,
        serviceFeePerShare = BigDecimal("2.50"),
        lastSharePriceWithFeeValue = BigDecimal("12.50"),
    )

    @Test
    fun `exhausts local pages then starts state and national at page zero`() = runTest(dispatcherRule.dispatcher) {
        val calls = mutableListOf<Pair<br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea?, Int>>()
        val repository = MarketplaceRepository { filter, page ->
            calls += filter.searchArea to page
            val next = when (filter.searchArea) {
                br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea.MUNICIPALITY ->
                    if (page == 1) br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea.STATE else null
                br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea.STATE ->
                    br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea.NATIONAL
                else -> null
            }
            MarketplacePage(listOf(pool.copy(code = "${filter.searchArea}-$page")), page,
                if (filter.searchArea == br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea.MUNICIPALITY) 1 else 0, 1, next)
        }
        val viewModel = viewModel(repository, filter = MarketplaceFilter.empty().withSearchArea(
            br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea.MUNICIPALITY))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        repeat(3) {
            viewModel.loadMore()
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        }
        assertEquals(listOf(
            br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea.MUNICIPALITY to 0,
            br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea.MUNICIPALITY to 1,
            br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea.STATE to 0,
            br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea.NATIONAL to 0,
        ), calls)
        assertEquals(4, viewModel.state.value.pools.size)
        assertFalse(viewModel.state.value.canLoadMore)
    }

    @Test
    fun `empty municipality and state automatically continue until national results`() = runTest(dispatcherRule.dispatcher) {
        val calls = mutableListOf<Int>()
        val repository = MarketplaceRepository { filter, page ->
            calls += page
            val area = requireNotNull(filter.searchArea)
            MarketplacePage(if (area == br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea.NATIONAL) listOf(pool) else emptyList(),
                0, 0, 1, area.next())
        }
        val viewModel = viewModel(repository, filter = MarketplaceFilter.empty().withSearchArea(
            br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea.MUNICIPALITY))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf(0, 0, 0), calls)
        assertEquals(listOf(pool), viewModel.state.value.pools)
        assertFalse(viewModel.state.value.canLoadMore)
    }

    @Test
    fun `backend order supplies four carousel pools and remaining list without duplication`() = runTest(dispatcherRule.dispatcher) {
        val values = (1..7).map { pool.copy(code = "B$it") }
        val vm = viewModel(MarketplaceRepository { _, _ -> MarketplacePage(values, 0, 0, values.size) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("B1", "B2", "B3", "B4"), vm.state.value.carouselPools.map { it.code })
        assertEquals(listOf("B5", "B6", "B7"), vm.state.value.listPools.map { it.code })
    }

    @Test
    fun `contest header uses current simulation parameters instead of pool advertisement data`() = runTest(dispatcherRule.dispatcher) {
        val game = br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters(
            br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
            4100, "2026-12-31", 60, 6, 20, 1, emptyList(), emptyList(),
            estimatedPrize = BigDecimal("7654321"),
        )
        val vm = MarketplaceViewModel(
            SearchMarketplace(MarketplaceRepository { _, _ -> MarketplacePage(listOf(pool), 0, 0, 1) }),
            MarketplaceFilter.forModality(2, 1),
            AddMarketplacePoolToCartUseCase { AppResult.Success(cartWithReservation()) },
            GetFavoriteRetailersUseCase { AppResult.Success(emptyList()) },
            SetRetailerFavoriteUseCase { _, _ -> AppResult.Success(Unit) },
            FixedCoroutineDispatchers(dispatcherRule.dispatcher),
            getBettingParameters = br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase {
                AppResult.Success(br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters(
                    BigDecimal.ZERO, BigDecimal.ZERO, 0, listOf(game)))
            },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(4100, vm.state.value.contestHeader?.contestNumber)
        assertEquals(BigDecimal("7654321"), vm.state.value.contestHeader?.estimatedPrize)
        assertEquals("2026-12-31", vm.state.value.contestHeader?.drawDate)
        assertEquals(listOf(pool), vm.state.value.pools)
    }

    @Test
    fun `loads first page and next page`() = runTest(dispatcherRule.dispatcher) {
        val calls = mutableListOf<Int>()
        val repository = MarketplaceRepository { _, page ->
            calls += page
            MarketplacePage(listOf(pool.copy(code = "B$page")), page, 1, 2)
        }
        val viewModel = viewModel(repository)

        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("B0"), viewModel.state.value.pools.map { it.code })
        assertTrue(viewModel.state.value.canLoadMore)
        viewModel.onAction(MarketplaceAction.LoadMore)
        viewModel.loadMore()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf(0, 1), calls)
        assertEquals(listOf("B0", "B1"), viewModel.state.value.pools.map { it.code })
        assertFalse(viewModel.state.value.canLoadMore)
    }

    @Test
    fun `shows error and retries`() = runTest(dispatcherRule.dispatcher) {
        var fails = true
        val repository = MarketplaceRepository { _, _ ->
            if (fails) error("offline")
            MarketplacePage(listOf(pool), 0, 0, 1)
        }
        val viewModel = viewModel(repository)

        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Não foram encontrados bolões disponíveis para os filtros informados. Limpe os filtros e tente novamente.", viewModel.state.value.errorMessage)
        fails = false
        viewModel.onAction(MarketplaceAction.Retry)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf(pool), viewModel.state.value.pools)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `does not load more without another page`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        val viewModel = viewModel(MarketplaceRepository { _, _ ->
            calls++
            MarketplacePage(emptyList(), 0, 0, 0)
        })
        viewModel.loadMore()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.loadMore()
        assertEquals(1, calls)
    }

    @Test
    fun `expands card and constrains share quantity to availability`() = runTest(dispatcherRule.dispatcher) {
        val viewModel = loadedViewModel()

        viewModel.onAction(MarketplaceAction.ToggleExpanded("unknown"))
        viewModel.onAction(MarketplaceAction.ToggleExpanded("B1"))
        assertEquals(setOf("B1"), viewModel.state.value.expandedPoolCodes)
        repeat(5) { viewModel.increaseShares("B1") }
        assertEquals(3, viewModel.state.value.selectedShares["B1"])
        repeat(5) { viewModel.decreaseShares("B1") }
        assertEquals(1, viewModel.state.value.selectedShares["B1"])
        viewModel.onAction(MarketplaceAction.ToggleExpanded("B1"))
        assertTrue(viewModel.state.value.expandedPoolCodes.isEmpty())
    }

    @Test
    fun `favorite confirmation updates every pool from same retailer`() = runTest(dispatcherRule.dispatcher) {
        val calls = mutableListOf<Pair<Long, Boolean>>()
        val repository = MarketplaceRepository { _, _ ->
            MarketplacePage(listOf(pool, pool.copy(code = "B2")), 0, 0, 2)
        }
        val viewModel = viewModel(
            repository,
            favorite = SetRetailerFavoriteUseCase { id, favorite ->
                calls += id to favorite
                AppResult.Success(Unit)
            },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        viewModel.onAction(MarketplaceAction.RequestFavorite("B1"))
        assertEquals("B1", viewModel.state.value.pendingFavoritePoolCode)
        viewModel.onAction(MarketplaceAction.ConfirmFavorite)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf(10L to true), calls)
        assertTrue(viewModel.state.value.pools.all { it.favoriteLottery })
        assertTrue(viewModel.state.value.updatingFavoriteLotteryIds.isEmpty())
        viewModel.requestFavorite("B1")
        viewModel.cancelFavorite()
        assertNull(viewModel.state.value.pendingFavoritePoolCode)
    }

    @Test
    fun `favorite failure exposes legacy error and can be dismissed`() = runTest(dispatcherRule.dispatcher) {
        val viewModel = viewModel(
            MarketplaceRepository { _, _ -> MarketplacePage(listOf(pool), 0, 0, 1) },
            favorite = SetRetailerFavoriteUseCase { _, _ -> AppResult.Failure(AppError.Network) },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.requestFavorite("B1")
        viewModel.confirmFavorite()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.actionError!!.contains("concluir esta operação"))
        viewModel.onAction(MarketplaceAction.DismissActionError)
        assertNull(viewModel.state.value.actionError)
    }

    @Test
    fun `direct addition sends selected order and confirms backend reservation`() = runTest(dispatcherRule.dispatcher) {
        var received: MarketplacePoolOrder? = null
        val viewModel = viewModel(
            MarketplaceRepository { _, _ -> MarketplacePage(listOf(pool), 0, 0, 1) },
            add = AddMarketplacePoolToCartUseCase { order ->
                received = order
                AppResult.Success(cartWithReservation())
            },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.increaseShares("B1")
        viewModel.onAction(MarketplaceAction.AddToCart("B1"))
        viewModel.addToCart("B1")
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, received?.requestedShares)
        assertEquals(BigDecimal("22.50"), received?.totalAmount)
        assertEquals(1, viewModel.state.value.pools.single().availableShares)
        assertEquals("2026-09-03T03:01:00", viewModel.state.value.confirmation?.expiresAt)
        assertTrue(viewModel.state.value.addingPoolCodes.isEmpty())
        viewModel.consumeConfirmation()
        assertNull(viewModel.state.value.confirmation)
    }

    @Test
    fun `direct addition reports missing reservation and service failure`() = runTest(dispatcherRule.dispatcher) {
        var missingReservation = true
        val viewModel = viewModel(
            MarketplaceRepository { _, _ -> MarketplacePage(listOf(pool), 0, 0, 1) },
            add = AddMarketplacePoolToCartUseCase {
                if (missingReservation) AppResult.Success(ShoppingCart(1, BigDecimal.TEN, emptyList()))
                else AppResult.Failure(AppError.Network)
            },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.addToCart("B1")
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.actionError!!.contains("confirmar a reserva"))

        missingReservation = false
        viewModel.dismissActionError()
        viewModel.addToCart("B1")
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.actionError!!.contains("adicionar a cota"))
    }

    @Test
    fun `lottery filter is exposed and invalid retailer is ignored`() = runTest(dispatcherRule.dispatcher) {
        val viewModel = viewModel(
            MarketplaceRepository { _, _ -> MarketplacePage(listOf(pool.copy(lotteryId = 0)), 0, 0, 1) },
            filter = MarketplaceFilter("", "", null, null, null, null, "Mega-Sena", "", emptyList(), emptyList()),
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.requestFavorite("B1")
        viewModel.confirmFavorite()
        assertTrue(viewModel.state.value.lotteryFiltered)
        assertNull(viewModel.state.value.pendingFavoritePoolCode)
    }

    @Test
    fun `loads favorite retailers and changes the legacy tab`() = runTest(dispatcherRule.dispatcher) {
        val retailer = retailer()
        val viewModel = viewModel(
            MarketplaceRepository { _, _ -> MarketplacePage(emptyList(), 0, 0, 0) },
            favoriteRetailers = GetFavoriteRetailersUseCase { AppResult.Success(listOf(retailer)) },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf(retailer), viewModel.state.value.favoriteRetailers)
        assertFalse(viewModel.state.value.favoriteRetailersLoading)
        viewModel.onAction(MarketplaceAction.SelectTab(MarketplaceTab.FAVORITE_RETAILERS))
        assertEquals(MarketplaceTab.FAVORITE_RETAILERS, viewModel.state.value.selectedTab)
    }

    @Test
    fun `return from retailer search synchronizes hearts without resetting selected shares`() = runTest(dispatcherRule.dispatcher) {
        var favorites = emptyList<Retailer>()
        val model = viewModel(
            MarketplaceRepository { _, _ -> MarketplacePage(listOf(pool.copy(favoriteLottery = false)), 0, 0, 1) },
            favoriteRetailers = GetFavoriteRetailersUseCase { AppResult.Success(favorites) },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        model.onAction(MarketplaceAction.IncreaseShares("B1"))
        favorites = listOf(retailer())
        model.refreshFavoriteRetailers()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(model.state.value.pools.single().favoriteLottery)
        assertEquals(2, model.state.value.selectedShares["B1"])
        favorites = emptyList()
        model.refreshFavoriteRetailers()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(model.state.value.pools.single().favoriteLottery)
        assertEquals(2, model.state.value.selectedShares["B1"])
    }

    @Test
    fun `removes favorite retailer and updates pool cards`() = runTest(dispatcherRule.dispatcher) {
        val calls = mutableListOf<Pair<Long, Boolean>>()
        val viewModel = viewModel(
            MarketplaceRepository { _, _ -> MarketplacePage(listOf(pool.copy(favoriteLottery = true)), 0, 0, 1) },
            favorite = SetRetailerFavoriteUseCase { id, favorite ->
                calls += id to favorite
                AppResult.Success(Unit)
            },
            favoriteRetailers = GetFavoriteRetailersUseCase { AppResult.Success(listOf(retailer())) },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        viewModel.onAction(MarketplaceAction.RequestFavoriteRetailerRemoval(99))
        assertNull(viewModel.state.value.pendingFavoriteRetailerRemoval)
        viewModel.requestFavoriteRetailerRemoval(10)
        assertEquals(10L, viewModel.state.value.pendingFavoriteRetailerRemoval)
        viewModel.onAction(MarketplaceAction.ConfirmFavoriteRetailerRemoval)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf(10L to false), calls)
        assertTrue(viewModel.state.value.favoriteRetailers.isEmpty())
        assertFalse(viewModel.state.value.pools.single().favoriteLottery)
    }

    @Test
    fun `favorite retailer cancellation and failures preserve the item`() = runTest(dispatcherRule.dispatcher) {
        val viewModel = viewModel(
            MarketplaceRepository { _, _ -> MarketplacePage(emptyList(), 0, 0, 0) },
            favorite = SetRetailerFavoriteUseCase { _, _ -> AppResult.Failure(AppError.Network) },
            favoriteRetailers = GetFavoriteRetailersUseCase { AppResult.Success(listOf(retailer())) },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        viewModel.requestFavoriteRetailerRemoval(10)
        viewModel.onAction(MarketplaceAction.CancelFavoriteRetailerRemoval)
        assertNull(viewModel.state.value.pendingFavoriteRetailerRemoval)
        viewModel.requestFavoriteRetailerRemoval(10)
        viewModel.confirmFavoriteRetailerRemoval()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.state.value.favoriteRetailers.size)
        assertTrue(viewModel.state.value.actionError!!.contains("concluir esta operação"))
    }

    @Test
    fun `favorite retailer loading failure exposes its own state`() = runTest(dispatcherRule.dispatcher) {
        val viewModel = viewModel(
            MarketplaceRepository { _, _ -> MarketplacePage(emptyList(), 0, 0, 0) },
            favoriteRetailers = GetFavoriteRetailersUseCase { AppResult.Failure(AppError.Network) },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.state.value.favoriteRetailersLoading)
        assertTrue(viewModel.state.value.favoriteRetailersError!!.contains("lotéricas favoritas"))
    }

    @Test
    fun `confirmed child changes refresh queries once while read only returns keep the baseline`() = runTest(dispatcherRule.dispatcher) {
        var searches = 0
        var favorites = 0
        val viewModel = viewModel(
            MarketplaceRepository { _, _ ->
                searches++
                MarketplacePage(listOf(pool), 0, 0, 1)
            },
            favoriteRetailers = GetFavoriteRetailersUseCase {
                favorites++
                AppResult.Success(emptyList())
            },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.observeChildRevision(7)
        viewModel.observeChildRevision(7)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, searches)
        assertEquals(1, favorites)

        viewModel.observeChildRevision(8)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, searches)
        assertEquals(2, favorites)
        // Reentrar na rota com o mesmo ViewModel retido não deve emitir outro GET.
        viewModel.observeChildRevision(8)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, searches)
        assertEquals(2, favorites)

        viewModel.observeChildRevision(9)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(3, searches)
        assertEquals(3, favorites)
    }

    @Test
    fun `favorite tab deletion stays locked until confirmed backend success or failure`() = runTest(dispatcherRule.dispatcher) {
        for (succeeds in listOf(true, false)) {
            val result = CompletableDeferred<AppResult<Unit>>()
            var calls = 0
            val viewModel = viewModel(
                MarketplaceRepository { _, _ -> MarketplacePage(listOf(pool.copy(favoriteLottery = true)), 0, 0, 1) },
                favoriteRetailers = GetFavoriteRetailersUseCase { AppResult.Success(listOf(retailer())) },
                favorite = SetRetailerFavoriteUseCase { _, _ -> calls++; result.await() },
            )
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            viewModel.requestFavoriteRetailerRemoval(10)
            viewModel.confirmFavoriteRetailerRemoval()
            assertEquals(setOf(10L), viewModel.state.value.updatingFavoriteLotteryIds)
            dispatcherRule.dispatcher.scheduler.runCurrent()
            viewModel.requestFavoriteRetailerRemoval(10)
            assertNull(viewModel.state.value.pendingFavoriteRetailerRemoval)
            viewModel.confirmFavoriteRetailerRemoval()
            assertEquals(1, calls)
            result.complete(if (succeeds) AppResult.Success(Unit) else AppResult.Failure(AppError.Network))
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertTrue(viewModel.state.value.updatingFavoriteLotteryIds.isEmpty())
            assertEquals(if (succeeds) 0 else 1, viewModel.state.value.favoriteRetailers.size)
            assertEquals(!succeeds, viewModel.state.value.pools.single().favoriteLottery)
            assertEquals(!succeeds, viewModel.state.value.actionError != null)
        }
    }

    @Test
    fun `failed area append retains pools and retry resets the original search area`() = runTest(dispatcherRule.dispatcher) {
        val calls = mutableListOf<Pair<MarketplaceSearchArea?, Int>>()
        var failAppend = true
        val model = viewModel(MarketplaceRepository { filter, page ->
            calls += filter.searchArea to page
            if (filter.searchArea == MarketplaceSearchArea.STATE && failAppend) error("offline")
            MarketplacePage(listOf(pool), 0, 0, 2, MarketplaceSearchArea.STATE)
        }, filter = MarketplaceFilter.empty().withSearchArea(MarketplaceSearchArea.MUNICIPALITY))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        model.loadMore()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf(pool), model.state.value.pools)
        assertEquals(0, model.state.value.currentPage)
        assertFalse(model.state.value.isLoadingMore)
        assertTrue(model.state.value.canLoadMore)
        assertTrue(model.state.value.errorMessage!!.contains("Não foram encontrados bolões"))
        failAppend = false
        model.retry()
        assertTrue(model.state.value.isLoading)
        assertNull(model.state.value.errorMessage)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf(
            MarketplaceSearchArea.MUNICIPALITY to 0,
            MarketplaceSearchArea.STATE to 0,
            MarketplaceSearchArea.MUNICIPALITY to 0,
        ), calls)
        assertEquals(listOf(pool), model.state.value.pools)
    }

    @Test
    fun `initial requests keep their order and retry does not reload favorite retailers`() = runTest(dispatcherRule.dispatcher) {
        val calls = mutableListOf<String>()
        val model = viewModel(MarketplaceRepository { _, _ ->
            calls += "pools"; MarketplacePage(listOf(pool), 0, 0, 1)
        }, filter = MarketplaceFilter.forModality(2, 1),
            favoriteRetailers = GetFavoriteRetailersUseCase { calls += "retailers"; AppResult.Success(emptyList()) },
            parameters = GetBettingParametersUseCase { calls += "header"; AppResult.Failure(AppError.Network) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("pools", "retailers", "header"), calls)
        model.retry()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("pools", "retailers", "header", "pools", "header"), calls)
        assertEquals(listOf(pool), model.state.value.pools)
        assertNull(model.state.value.contestHeader)
        assertNull(model.state.value.errorMessage)
    }

    @Test
    fun `header selects the special contest and clears after parameter failure`() = runTest(dispatcherRule.dispatcher) {
        val regular = headerGame("NORMAL", 4000)
        val special = headerGame("eSpEcIaL", 5000)
        var response: AppResult<BettingParameters> = AppResult.Success(
            BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0,
                listOf(regular, special.copy(modality = LotteryModality(3, "Lotofácil", "LOTOFACIL")), special)))
        val model = viewModel(MarketplaceRepository { _, _ -> MarketplacePage(listOf(pool), 0, 0, 1) },
            filter = MarketplaceFilter.forModality(2, 2), parameters = GetBettingParametersUseCase { response })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(5000, model.state.value.contestHeader?.contestNumber)
        response = AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0,
            listOf(special.copy(contestType = "E", contestNumber = 5001))))
        model.retry(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(5001, model.state.value.contestHeader?.contestNumber)
        response = AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0,
            listOf(special.copy(contestNumber = 0))))
        model.retry(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertNull(model.state.value.contestHeader)
        response = AppResult.Failure(AppError.Network)
        model.retry(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertNull(model.state.value.contestHeader)
        assertNull(model.state.value.errorMessage)
    }

    @Test
    fun `retailer filter and missing modality load label metadata without a contest header`() = runTest(dispatcherRule.dispatcher) {
        var parameterCalls = 0
        for (filter in listOf(MarketplaceFilter.empty(), MarketplaceFilter.forModality(2, 1).withLottery("0010"))) {
            val model = viewModel(MarketplaceRepository { _, _ -> MarketplacePage(listOf(pool), 0, 0, 1) },
                filter = filter, parameters = GetBettingParametersUseCase {
                    parameterCalls++; AppResult.Failure(AppError.Network)
                })
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            model.retry(); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertNull(model.state.value.contestHeader)
            assertEquals(listOf(pool), model.state.value.pools)
        }
        assertEquals(4, parameterCalls)
    }

    @Test
    fun `pool favorite and retailer removal share the same pending retailer lock`() = runTest(dispatcherRule.dispatcher) {
        val pending = CompletableDeferred<AppResult<Unit>>()
        val calls = mutableListOf<Pair<Long, Boolean>>()
        val model = viewModel(MarketplaceRepository { _, _ -> MarketplacePage(listOf(pool), 0, 0, 1) },
            favoriteRetailers = GetFavoriteRetailersUseCase { AppResult.Success(listOf(retailer())) },
            favorite = SetRetailerFavoriteUseCase { id, favorite -> calls += id to favorite; pending.await() })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        model.requestFavorite("B1"); model.confirmFavorite()
        dispatcherRule.dispatcher.scheduler.runCurrent()
        model.requestFavorite("B1"); model.requestFavoriteRetailerRemoval(10)
        assertNull(model.state.value.pendingFavoritePoolCode)
        assertNull(model.state.value.pendingFavoriteRetailerRemoval)
        assertEquals(setOf(10L), model.state.value.updatingFavoriteLotteryIds)
        pending.complete(AppResult.Success(Unit))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(model.state.value.pools.single().favoriteLottery)
        assertEquals(listOf(retailer()), model.state.value.favoriteRetailers)
        model.requestFavoriteRetailerRemoval(10); model.confirmFavoriteRetailerRemoval()
        model.requestFavorite("B1")
        assertNull(model.state.value.pendingFavoritePoolCode)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf(10L to true, 10L to false), calls)
        assertTrue(model.state.value.favoriteRetailers.isEmpty())
        assertFalse(model.state.value.pools.single().favoriteLottery)
    }

    @Test
    fun `cart submission freezes shares until reservation and clears only the purchased selection`() = runTest(dispatcherRule.dispatcher) {
        val pending = CompletableDeferred<AppResult<ShoppingCart>>()
        val orders = mutableListOf<MarketplacePoolOrder>()
        val model = viewModel(MarketplaceRepository { _, _ -> MarketplacePage(listOf(pool, pool.copy(code = "B2")), 0, 0, 2) },
            add = AddMarketplacePoolToCartUseCase { orders += it; pending.await() })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        model.increaseShares("B1"); model.increaseShares("B2")
        model.addToCart("B1")
        model.increaseShares("B1"); model.decreaseShares("B1"); model.addToCart("B1")
        dispatcherRule.dispatcher.scheduler.runCurrent()
        assertEquals(2, model.state.value.selectedShares["B1"])
        assertEquals(setOf("B1"), model.state.value.addingPoolCodes)
        assertEquals(1, orders.size)
        assertEquals(2, orders.single().requestedShares)
        pending.complete(AppResult.Success(cartWithReservation()))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(mapOf("B2" to 2), model.state.value.selectedShares)
        assertEquals(listOf(1, 3), model.state.value.pools.map { it.availableShares })
        assertEquals(BigDecimal("22.50"), model.state.value.confirmation?.amount)
        assertTrue(model.state.value.addingPoolCodes.isEmpty())
        model.increaseShares("B1")
        assertNull(model.state.value.selectedShares["B1"])
    }

    @Test
    fun `clearing viewmodel cancels requests in every extracted coordinator`() = runTest(dispatcherRule.dispatcher) {
        val pending = CompletableDeferred<Unit>()
        val cancelled = mutableListOf<String>()
        var searches = 0
        suspend fun waitForResult(name: String) {
            try { pending.await() } finally { cancelled += name }
        }
        val model = viewModel(MarketplaceRepository { _, _ ->
            if (searches++ > 0) waitForResult("search")
            MarketplacePage(listOf(pool), 0, 0, 1)
        }, filter = MarketplaceFilter.forModality(2, 1),
            favoriteRetailers = GetFavoriteRetailersUseCase { waitForResult("retailers"); AppResult.Success(emptyList()) },
            parameters = GetBettingParametersUseCase { waitForResult("header"); AppResult.Failure(AppError.Network) },
            favorite = SetRetailerFavoriteUseCase { _, _ -> waitForResult("favorite"); AppResult.Success(Unit) },
            add = AddMarketplacePoolToCartUseCase { waitForResult("cart"); AppResult.Success(cartWithReservation()) })
        val store = ViewModelStore().apply { put("marketplace", model) }
        dispatcherRule.dispatcher.scheduler.runCurrent()
        model.requestFavorite("B1"); model.confirmFavorite(); model.addToCart("B1"); model.retry()
        dispatcherRule.dispatcher.scheduler.runCurrent()
        assertEquals(listOf("header"), cancelled)
        val snapshot = model.state.value
        store.clear(); dispatcherRule.dispatcher.scheduler.runCurrent()
        assertEquals(mapOf("search" to 1, "retailers" to 1, "header" to 2, "favorite" to 1, "cart" to 1),
            cancelled.groupingBy { it }.eachCount())
        pending.complete(Unit); dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(snapshot, model.state.value)
    }

    @Test fun `favorite retailers entry selects its tab before the first frame`() = runTest(dispatcherRule.dispatcher) {
        val model = viewModel(
            MarketplaceRepository { _, _ -> MarketplacePage(emptyList(), 0, 0, 0) },
            initialTab = MarketplaceTab.FAVORITE_RETAILERS
        )
        assertEquals(MarketplaceTab.FAVORITE_RETAILERS, model.state.value.selectedTab)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(MarketplaceTab.FAVORITE_RETAILERS, model.state.value.selectedTab)
        model.selectTab(MarketplaceTab.POOLS)
        assertEquals(MarketplaceTab.POOLS, model.state.value.selectedTab)
    }

    private fun headerGame(type: String, contest: Int) = LotteryGameParameters(
        LotteryModality(2, "Mega-Sena", "MEGA_SENA"), contest, "2026-12-31", 60, 6, 20, 1,
        emptyList(), emptyList(), contestType = type
    )

    private suspend fun loadedViewModel(): MarketplaceViewModel {
        val viewModel = viewModel(
            MarketplaceRepository { _, _ -> MarketplacePage(listOf(pool), 0, 0, 1) }
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        return viewModel
    }

    private fun viewModel(
        repository: MarketplaceRepository,
        add: AddMarketplacePoolToCartUseCase = AddMarketplacePoolToCartUseCase {
            AppResult.Failure(AppError.Unexpected)
        },
        favorite: SetRetailerFavoriteUseCase = SetRetailerFavoriteUseCase { _, _ ->
            AppResult.Failure(AppError.Unexpected)
        },
        filter: MarketplaceFilter = MarketplaceFilter.empty(),
        favoriteRetailers: GetFavoriteRetailersUseCase = GetFavoriteRetailersUseCase {
            AppResult.Success(emptyList())
        },
        parameters: GetBettingParametersUseCase? = null,
        initialTab: MarketplaceTab = MarketplaceTab.POOLS,
    ) = MarketplaceViewModel(
        SearchMarketplace(repository),
        filter,
        add,
        favoriteRetailers,
        favorite,
        FixedCoroutineDispatchers(dispatcherRule.dispatcher),
        parameters,
        initialTab = initialTab
    )

    private fun cartWithReservation() = ShoppingCart(
        id = 1,
        total = BigDecimal("22.50"),
        items = listOf(
            CartItem(
                id = 2,
                kind = CartItemKind.POOL_SHARE,
                title = "Mega-Sena",
                description = "Cota 1",
                amount = BigDecimal("22.50"),
                poolShare = CartPoolShare("B1", 1, 10, "2026-09-03T03:01:00")
            )
        )
    )

    private fun retailer() = Retailer(
        id = 10,
        code = "0010",
        fantasyName = "Lotérica Centro",
        legalName = "Centro LTDA",
        address = "Rua Um",
        district = "Centro",
        city = "Brasília",
        state = "DF",
        favorite = true
    )
}
