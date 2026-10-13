package br.gov.caixa.loterias.feature.marketplace

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.cart.CartItem
import br.gov.caixa.loterias.core.domain.cart.CartItemKind
import br.gov.caixa.loterias.core.domain.cart.CartPoolShare
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import br.gov.caixa.loterias.core.domain.marketplace.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilter
import br.gov.caixa.loterias.core.domain.marketplace.MarketplacePage
import br.gov.caixa.loterias.core.domain.marketplace.MarketplacePool
import br.gov.caixa.loterias.core.domain.marketplace.MarketplacePoolOrder
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceRepository
import br.gov.caixa.loterias.core.domain.marketplace.SearchMarketplace
import br.gov.caixa.loterias.core.domain.retail.SetRetailerFavoriteUseCase
import br.gov.caixa.loterias.core.domain.retail.GetFavoriteRetailersUseCase
import br.gov.caixa.loterias.core.domain.retail.Retailer
import java.math.BigDecimal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

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
        assertEquals("Não foi possível carregar os bolões. Tente novamente.", viewModel.state.value.errorMessage)
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

    private suspend fun loadedViewModel(): MarketplaceViewModel {
        val viewModel = viewModel(
            MarketplaceRepository { _, _ -> MarketplacePage(listOf(pool), 0, 0, 1) },
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
    ) = MarketplaceViewModel(
        SearchMarketplace(repository),
        filter,
        add,
        favoriteRetailers,
        favorite,
        FixedCoroutineDispatchers(dispatcherRule.dispatcher),
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
                poolShare = CartPoolShare("B1", 1, 10, "2026-09-03T03:01:00"),
            ),
        ),
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
        favorite = true,
    )
}
