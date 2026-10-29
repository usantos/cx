package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.ui.test.*
import org.junit.Assert.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.CartPoolShare
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceDetailUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceBet
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolOrder
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplacePoolConfirmation
import br.gov.caixa.loterias.feature.marketplace.presentation.route.MarketplaceDetailRoute
import br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel.MarketplaceDetailViewModel

class MarketplaceDetailCoordinationUiTest {
    @get:Rule val compose = createComposeRule()
    private val models = mutableListOf<MarketplaceDetailViewModel>()
    @After fun cleanup() { models.forEach { it.viewModelScope.cancel() } }

    @Test fun soldOutNoticeReturnsToPoolsOnlyAfterAcknowledgementAndConsumesNoticeFirst() {
        val fixture = Fixture("B1")
        fixture.load = { AppResult.Success(detail("B1").copy(availableShares = 0)) }
        var returnsToPools = 0
        compose.setContent {
            val model = remember { fixture.create() }
            CaixaDSTheme(darkTheme = false) {
                MarketplaceDetailRoute(model, {}, {}, onSoldOut = {
                    assertNull(model.state.value.soldOutNotice)
                    returnsToPools++
                })
            }
        }
        compose.onNodeWithText("Apostas sem quantidade minina necessaria, vamos voltar para a área principal do aposta bolão").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, returnsToPools); assertTrue(fixture.orders.isEmpty()) }
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertEquals(1, returnsToPools); assertTrue(fixture.orders.isEmpty()) }
        compose.onNodeWithText("Apostas sem quantidade minina necessaria, vamos voltar para a área principal do aposta bolão").assertDoesNotExist()
    }

    @Test fun replacementViewModelReceivesQuantityAndOrderAndConsumesConfirmationBeforeNavigation() {
        val first = Fixture("B1")
        val second = Fixture("B2")
        val selected = mutableIntStateOf(0)
        val cartCount = mutableIntStateOf(1)
        val cartTotal = mutableStateOf(BigDecimal("39.00"))
        val confirmations = mutableListOf<MarketplacePoolConfirmation>()
        val cartCalls = mutableListOf<String>()
        compose.setContent {
            val viewModels = remember { listOf(first.create(), second.create()) }
            val index = selected.intValue
            val model = viewModels[index]
            CaixaDSTheme(darkTheme = false) {
                MarketplaceDetailRoute(model, {}, {
                    assertNull(model.state.value.confirmation)
                    confirmations += it
                }, cartTotal.value, cartCount.intValue, { cartCalls += if (index == 0) "B1" else "B2" })
            }
        }
        compose.onNodeWithContentDescription("Diminuir cotas").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Aumentar cotas").performClick().assertIsNotEnabled()
        compose.runOnIdle { selected.intValue = 1 }
        compose.onNodeWithContentDescription("Diminuir cotas").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Aumentar cotas").performClick().assertIsNotEnabled()
        compose.onNodeWithTag("pool-detail-add-to-cart").performClick()
        compose.runOnIdle {
            assertTrue(first.orders.isEmpty())
            assertEquals("B2", second.orders.single().code)
            assertEquals(2, second.orders.single().requestedShares)
            assertEquals(BigDecimal("18.50"), second.orders.single().totalAmount)
            assertEquals("B2", confirmations.single().poolCode)
            assertEquals("2026-10-18T17:00:00Z", confirmations.single().expiresAt)
            cartCount.intValue = 1000
            cartTotal.value = BigDecimal("42.00")
        }
        compose.onNodeWithText("+999").assertIsDisplayed()
        compose.onNodeWithText("R$ 42,00").assertIsDisplayed()
        compose.onNodeWithContentDescription("Carrinho de apostas").performClick()
        compose.runOnIdle { assertEquals(1, confirmations.size); assertEquals(listOf("B2"), cartCalls) }
    }

    @Test fun removingFavoriteRequiresConsentAndFailureKeepsFavoriteUntilSuccessfulRetry() {
        val fixture = Fixture("B1", initialFavorite = true).apply { failFavorite = true }
        compose.setContent {
            val model = remember { fixture.create() }
            CaixaDSTheme(darkTheme = false) { MarketplaceDetailRoute(model, {}, {}) }
        }
        compose.onNodeWithContentDescription("Excluir lotérica favorita").performClick()
        compose.onNodeWithText("Deseja excluir Lotérica B1 da sua lista de favoritas?").assertIsDisplayed()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle { assertTrue(fixture.favoriteWrites.isEmpty()) }
        compose.onNodeWithContentDescription("Excluir lotérica favorita").performClick()
        compose.onNodeWithText("Confirmar").performClick()
        compose.onNodeWithText("Não foi possível concluir esta operação. Por favor, tente novamente.").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithContentDescription("Excluir lotérica favorita").assertIsDisplayed()
        compose.runOnIdle { fixture.failFavorite = false }
        compose.onNodeWithContentDescription("Excluir lotérica favorita").performClick()
        compose.onNodeWithText("Confirmar").performClick()
        compose.onNodeWithContentDescription("Favoritar lotérica").assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf(10L to false, 10L to false), fixture.favoriteWrites) }
    }

    @Test fun laterResumeReloadsAvailabilityAndResetsQuantityWhileKeepingFavoriteAcrossFailureAndRetry() {
        val fixture = Fixture("B1")
        lateinit var owner: Owner
        fixture.load = {
            if (fixture.failLoad) AppResult.Failure(AppError.Network)
            else AppResult.Success(detail("B1").copy(availableShares = if (fixture.gets == 1) 4 else 1))
        }
        compose.setContent {
            val lifecycleOwner = remember { Owner().also { owner = it } }
            val model = remember { fixture.create() }
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                CaixaDSTheme(darkTheme = false) { MarketplaceDetailRoute(model, {}, {}) }
            }
        }
        compose.runOnIdle { assertEquals(1, fixture.gets) }
        compose.onNodeWithContentDescription("Aumentar cotas").performClick()
        compose.onNodeWithContentDescription("Favoritar lotérica").performClick()
        compose.onNodeWithText("Confirmar").performClick()
        compose.runOnIdle { owner.registry.currentState = Lifecycle.State.CREATED }
        compose.runOnIdle { owner.registry.currentState = Lifecycle.State.RESUMED }
        compose.onNodeWithContentDescription("Aumentar cotas").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Diminuir cotas").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Excluir lotérica favorita").assertIsDisplayed()
        compose.runOnIdle { assertEquals(2, fixture.gets); owner.registry.currentState = Lifecycle.State.CREATED; fixture.failLoad = true }
        compose.runOnIdle { owner.registry.currentState = Lifecycle.State.RESUMED }
        compose.onNodeWithText("Não foi possível carregar o bolão").assertDoesNotExist()
        compose.onNodeWithTag("pool-detail-purchase-panel").assertIsDisplayed()
        compose.onNodeWithContentDescription("Aumentar cotas").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Excluir lotérica favorita").assertIsDisplayed()
        compose.runOnIdle { assertEquals(3, fixture.gets); fixture.failLoad = false }
        compose.runOnIdle { owner.registry.currentState = Lifecycle.State.CREATED }
        compose.runOnIdle { owner.registry.currentState = Lifecycle.State.RESUMED }
        compose.onNodeWithContentDescription("Excluir lotérica favorita").assertIsDisplayed()
        compose.onNodeWithTag("pool-detail-purchase-panel").assertIsDisplayed()
        compose.runOnIdle { assertEquals(4, fixture.gets) }
    }

    private inner class Fixture(private val code: String, private val initialFavorite: Boolean = false) {
        var gets = 0
        var failLoad = false
        var failFavorite = false
        var load: () -> AppResult<MarketplacePoolDetail> = { AppResult.Success(detail(code)) }
        val orders = mutableListOf<MarketplacePoolOrder>()
        val favoriteWrites = mutableListOf<Pair<Long, Boolean>>()
        fun create() = MarketplaceDetailViewModel(code, GetMarketplaceDetailUseCase { gets++; load() },
            AddMarketplacePoolToCartUseCase { order ->
                orders += order
                AppResult.Success(ShoppingCart(1, order.totalAmount, listOf(CartItem(1, CartItemKind.POOL_SHARE,
                    "Cota", "", order.totalAmount, poolShare = CartPoolShare(code, 2, 50, "2026-10-18T17:00:00Z")))))
            }, FixedCoroutineDispatchers(Dispatchers.Main.immediate), SetRetailerFavoriteUseCase { id, favorite ->
                favoriteWrites += id to favorite
                if (failFavorite) AppResult.Failure(AppError.Network) else AppResult.Success(Unit)
            }, initialFavorite).also { models += it }
    }

    private fun detail(code: String) = MarketplacePoolDetail(code, "MEGA_SENA", 3070, 10, "Lotérica $code", "1", "Cidade - SP",
        "14/10/2026", "150000000.00", 2, 50, 2, 6, "10.00", "0.50", "10.50",
        listOf(MarketplaceBet((1..6).toList(), emptyList(), false)), lastSharePriceWithFeeValue = BigDecimal("10.50"),
        regularSharePriceWithFee = BigDecimal("8.00"))

    private class Owner : LifecycleOwner {
        val registry = LifecycleRegistry(this).apply { currentState = Lifecycle.State.RESUMED }
        override val lifecycle: Lifecycle get() = registry
    }
}
