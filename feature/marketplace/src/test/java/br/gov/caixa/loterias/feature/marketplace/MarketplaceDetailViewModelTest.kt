package br.gov.caixa.loterias.feature.marketplace

import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceDetailRepository
import br.gov.caixa.loterias.core.domain.marketplace.MarketplacePoolDetail
import br.gov.caixa.loterias.core.domain.marketplace.GetMarketplaceDetail
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.domain.marketplace.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.core.domain.cart.CartItem
import br.gov.caixa.loterias.core.domain.cart.CartItemKind
import br.gov.caixa.loterias.core.domain.cart.CartPoolShare
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
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
class MarketplaceDetailViewModelTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()

    @Test
    fun `loads detail and constrains selected shares`() = runTest(dispatcherRule.dispatcher) {
        var requestedCode = ""
        val viewModel = MarketplaceDetailViewModel(
            poolCode = "B123",
            getMarketplaceDetail = GetMarketplaceDetail(MarketplaceDetailRepository { code ->
                requestedCode = code
                detail(availableShares = 2)
            }),
            addMarketplacePoolToCart = failingAddUseCase(),
            dispatchers = FixedCoroutineDispatchers(dispatcherRule.dispatcher),
        )

        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("B123", requestedCode)
        assertEquals(detail(2), viewModel.state.value.detail)
        assertEquals(1, viewModel.state.value.selectedShares)
        assertTrue(viewModel.state.value.canIncrease)
        assertFalse(viewModel.state.value.canDecrease)

        viewModel.decreaseShares()
        viewModel.increaseShares()
        viewModel.increaseShares()
        assertEquals(2, viewModel.state.value.selectedShares)
        assertFalse(viewModel.state.value.canIncrease)
        assertTrue(viewModel.state.value.canDecrease)
        viewModel.decreaseShares()
        assertEquals(1, viewModel.state.value.selectedShares)
    }

    @Test
    fun `empty availability disables purchase quantity`() = runTest(dispatcherRule.dispatcher) {
        val viewModel = MarketplaceDetailViewModel(
            "B0",
            GetMarketplaceDetail(MarketplaceDetailRepository { detail(availableShares = 0) }),
            failingAddUseCase(),
            FixedCoroutineDispatchers(dispatcherRule.dispatcher),
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, viewModel.state.value.selectedShares)
        assertFalse(viewModel.state.value.canIncrease)
        assertFalse(viewModel.state.value.canDecrease)
    }

    @Test
    fun `shows error then retries`() = runTest(dispatcherRule.dispatcher) {
        var fails = true
        val viewModel = MarketplaceDetailViewModel(
            "B1",
            GetMarketplaceDetail(MarketplaceDetailRepository {
                if (fails) error("offline")
                detail(availableShares = 1)
            }),
            failingAddUseCase(),
            FixedCoroutineDispatchers(dispatcherRule.dispatcher),
        )

        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertNull(viewModel.state.value.detail)
        assertEquals("Não foi possível carregar os detalhes do bolão. Tente novamente.", viewModel.state.value.errorMessage)

        fails = false
        viewModel.retry()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("B1", viewModel.state.value.detail?.code)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `adds selected shares and exposes confirmed backend reservation`() = runTest(dispatcherRule.dispatcher) {
        var requestedShares = 0
        val addUseCase = AddMarketplacePoolToCartUseCase { order ->
            requestedShares = order.requestedShares
            AppResult.Success(cartWithReservation(expiresAt = "2026-09-03T03:01:00"))
        }
        val viewModel = MarketplaceDetailViewModel(
            "B1",
            GetMarketplaceDetail(MarketplaceDetailRepository { detail(availableShares = 3) }),
            addUseCase,
            FixedCoroutineDispatchers(dispatcherRule.dispatcher),
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.increaseShares()
        viewModel.onAction(MarketplaceDetailAction.AddToCart)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, requestedShares)
        assertEquals("B1", viewModel.state.value.confirmation?.poolCode)
        assertEquals("2026-09-03T03:01:00", viewModel.state.value.confirmation?.expiresAt)
        assertFalse(viewModel.state.value.isAddingToCart)
        assertNull(viewModel.state.value.addToCartError)

        viewModel.consumeConfirmation()
        assertNull(viewModel.state.value.confirmation)
    }

    @Test
    fun `does not confirm cart addition without matching active reservation`() = runTest(dispatcherRule.dispatcher) {
        val viewModel = MarketplaceDetailViewModel(
            "B1",
            GetMarketplaceDetail(MarketplaceDetailRepository { detail(availableShares = 1) }),
            AddMarketplacePoolToCartUseCase { AppResult.Success(ShoppingCart(1, BigDecimal.TEN, emptyList())) },
            FixedCoroutineDispatchers(dispatcherRule.dispatcher),
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.addToCart()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.state.value.confirmation)
        assertTrue(viewModel.state.value.addToCartError!!.contains("confirmar a reserva"))
    }

    @Test
    fun `reports add failure and ignores invalid or concurrent additions`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        val viewModel = MarketplaceDetailViewModel(
            "B1",
            GetMarketplaceDetail(MarketplaceDetailRepository { detail(availableShares = 1) }),
            AddMarketplacePoolToCartUseCase {
                calls++
                AppResult.Failure(AppError.Network)
            },
            FixedCoroutineDispatchers(dispatcherRule.dispatcher),
        )
        viewModel.addToCart()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.addToCart()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, calls)
        assertTrue(viewModel.state.value.addToCartError!!.contains("adicionar a cota"))
    }

    private fun detail(availableShares: Int) = MarketplacePoolDetail(
        code = "B1",
        modality = "Mega-Sena",
        contest = 1,
        lotteryId = 1,
        lotteryName = "Lotérica",
        lotteryNumber = "0001",
        cityAndState = "Brasília - DF",
        drawDateAndTime = "01/01 às 20:00",
        estimatedPrize = "1000.00",
        availableShares = availableShares,
        totalShares = 10,
        betQuantity = 2,
        numberQuantity = 6,
        sharePriceWithoutFee = "9.00",
        lastShareFee = "1.00",
        lastSharePriceWithFee = "10.00",
        bets = emptyList(),
        contestType = 1,
        drawDate = "01/01/2026",
        serviceFeePerShare = BigDecimal.ONE,
        lastSharePriceWithFeeValue = BigDecimal.TEN,
    )

    private fun failingAddUseCase() = AddMarketplacePoolToCartUseCase {
        AppResult.Failure(AppError.Unexpected)
    }

    private fun cartWithReservation(expiresAt: String) = ShoppingCart(
        id = 1,
        total = BigDecimal.TEN,
        items = listOf(
            CartItem(
                id = 2,
                kind = CartItemKind.POOL_SHARE,
                title = "Mega-Sena",
                description = "Cota 1",
                amount = BigDecimal.TEN,
                poolShare = CartPoolShare("B1", 1, 10, expiresAt),
            ),
        ),
    )
}
