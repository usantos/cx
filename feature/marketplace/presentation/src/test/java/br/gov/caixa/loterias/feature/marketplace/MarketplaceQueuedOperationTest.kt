package br.gov.caixa.loterias.feature.marketplace

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItemKind
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartPoolShare
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart
import br.gov.caixa.loterias.core.domain.contract.marketplace.model.MarketplaceFilter
import br.gov.caixa.loterias.core.domain.contract.retail.model.Retailer
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePage
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail
import br.gov.caixa.loterias.feature.marketplace.domain.repository.MarketplaceRepository
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceDetailUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.SearchMarketplace
import br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel.MarketplaceDetailViewModel
import br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel.MarketplaceViewModel
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetFavoriteRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(Parameterized::class)
class MarketplaceQueuedOperationTest(private val operation: Operation) {
    @get:Rule val dispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    @Test fun `disabling while queued prevents the write and releases the claim for retry`() = runTest(dispatcherRule.dispatcher) {
        val fixture = fixture()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        fixture.submit()
        fixture.enabled = false
        dispatcherRule.dispatcher.scheduler.runCurrent()
        assertEquals(0, fixture.writes)
        fixture.assertCompleted(false)
        fixture.enabled = true
        fixture.response.complete(Unit)
        fixture.submit()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, fixture.writes)
        fixture.assertCompleted(true)
    }

    @Test fun `disabling after the write starts preserves its single completion`() = runTest(dispatcherRule.dispatcher) {
        val fixture = fixture()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        fixture.submit()
        dispatcherRule.dispatcher.scheduler.runCurrent()
        assertEquals(1, fixture.writes)
        fixture.enabled = false
        fixture.response.complete(Unit)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        fixture.assertCompleted(true)
        fixture.submit()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, fixture.writes)
    }

    private fun fixture(): Fixture {
        val fixture = Fixture()
        val add = AddMarketplacePoolToCartUseCase {
            fixture.write()
            AppResult.Success(cart())
        }
        val favorite = SetRetailerFavoriteUseCase { _, _ ->
            fixture.write()
            AppResult.Success(Unit)
        }
        val dispatchers = FixedCoroutineDispatchers(dispatcherRule.dispatcher)
        when (operation) {
            Operation.DETAIL_CART, Operation.DETAIL_FAVORITE -> {
                val model = MarketplaceDetailViewModel(
                    "B1",
                    GetMarketplaceDetailUseCase { AppResult.Success(detail()) },
                    add,
                    dispatchers,
                    favorite,
                    false,
                    canStartOperation = { fixture.enabled }
                )
                fixture.submit = { submitDetail(model) }
                fixture.assertCompleted = { completed -> assertDetail(model, completed) }
            }

            else -> {
                val model = MarketplaceViewModel(
                    SearchMarketplace(MarketplaceRepository { _, _ -> MarketplacePage(listOf(pool()), 0, 0, 1) }),
                    MarketplaceFilter.empty(),
                    add,
                    GetFavoriteRetailersUseCase { AppResult.Success(listOf(retailer())) },
                    favorite,
                    dispatchers,
                    canStartOperation = { fixture.enabled }
                )
                fixture.submit = { submitList(model) }
                fixture.assertCompleted = { completed -> assertList(model, completed) }
            }
        }
        return fixture
    }

    private fun submitDetail(model: MarketplaceDetailViewModel) {
        if (operation == Operation.DETAIL_CART) {
            model.addToCart()
        } else {
            model.requestFavorite()
            model.confirmFavorite()
        }
    }

    private fun submitList(model: MarketplaceViewModel) = when (operation) {
        Operation.LIST_CART -> model.addToCart("B1")

        Operation.LIST_FAVORITE -> {
            model.requestFavorite("B1")
            model.confirmFavorite()
        }

        Operation.LIST_RETAILER_REMOVAL -> {
            model.requestFavoriteRetailerRemoval(RETAILER_ID)
            model.confirmFavoriteRetailerRemoval()
        }

        else -> error("Detail operations have their own ViewModel")
    }

    private fun assertDetail(model: MarketplaceDetailViewModel, completed: Boolean) {
        val state = model.state.value
        assertFalse(state.isAddingToCart)
        assertFalse(state.isUpdatingFavorite)
        assertNull(state.addToCartError)
        assertNull(state.favoriteError)
        if (operation == Operation.DETAIL_CART) {
            assertEquals(completed, state.confirmation != null)
        } else {
            assertEquals(completed, state.favoriteLottery)
        }
    }

    private fun assertList(model: MarketplaceViewModel, completed: Boolean) {
        val state = model.state.value
        assertTrue(state.addingPoolCodes.isEmpty())
        assertTrue(state.updatingFavoriteLotteryIds.isEmpty())
        assertNull(state.actionError)
        when (operation) {
            Operation.LIST_CART -> assertEquals(completed, state.confirmation != null)
            Operation.LIST_FAVORITE -> assertEquals(completed, state.pools.single().favoriteLottery)
            Operation.LIST_RETAILER_REMOVAL -> assertEquals(if (completed) 0 else 1, state.favoriteRetailers.size)
            else -> error("Detail operations have their own ViewModel")
        }
    }

    private class Fixture {
        var enabled = true
        var writes = 0
        val response = CompletableDeferred<Unit>()
        lateinit var submit: () -> Unit
        lateinit var assertCompleted: (Boolean) -> Unit
        suspend fun write() {
            writes++
            response.await()
        }
    }

    enum class Operation { DETAIL_CART, DETAIL_FAVORITE, LIST_CART, LIST_FAVORITE, LIST_RETAILER_REMOVAL }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun operations(): List<Array<Operation>> = Operation.entries.map { arrayOf(it) }
    }

    private fun pool() = MarketplacePool(
        code = "B1", modality = "MEGA_SENA", contest = CONTEST_NUMBER, lotteryName = "Lotérica", drawDate = "14/10/2026",
        sharePrice = "10.50", availableShares = AVAILABLE_SHARES, betQuantity = BET_QUANTITY, numberQuantity = NUMBER_QUANTITY,
        favoriteLottery = false,
        lotteryId = RETAILER_ID, cityAndState = "Cidade - SP", totalShares = TOTAL_SHARES, contestType = 1, estimatedPrize = "150000000.00",
        regularSharePriceWithFee = BigDecimal("8.00"), serviceFeePerShare = BigDecimal("0.50"),
        lastSharePriceWithFeeValue = BigDecimal("10.50")
    )

    private fun detail() = MarketplacePoolDetail(
        "B1", "MEGA_SENA", CONTEST_NUMBER, RETAILER_ID, "Lotérica", "1", "Cidade - SP", "14/10/2026", "150000000.00",
        AVAILABLE_SHARES, TOTAL_SHARES, BET_QUANTITY, NUMBER_QUANTITY, "10.00", "0.50", "10.50", emptyList(),
        lastSharePriceWithFeeValue = BigDecimal("10.50"), regularSharePriceWithFee = BigDecimal("8.00")
    )

    private fun retailer() = Retailer(RETAILER_ID, "0010", "Lotérica", "Lotérica LTDA", "Rua", "Centro", "Cidade", "SP", true)

    private fun cart() = ShoppingCart(
        id = 1,
        total = BigDecimal("10.50"),
        items = listOf(
            CartItem(
                id = 2,
                kind = CartItemKind.POOL_SHARE,
                title = "Mega-Sena",
                description = "Cota",
                amount = BigDecimal("10.50"),
                poolShare = CartPoolShare("B1", 1, RESERVED_POOL_TOTAL_SHARES, "2026-11-03T03:01:00")
            )
        )
    )
}

private const val RETAILER_ID = 10L
private const val CONTEST_NUMBER = 3070
private const val AVAILABLE_SHARES = 3
private const val TOTAL_SHARES = 100
private const val BET_QUANTITY = 8
private const val NUMBER_QUANTITY = 9

private const val RESERVED_POOL_TOTAL_SHARES = 10
