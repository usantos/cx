package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.CartPoolShare
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceBet
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolOrder
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceDetailUseCase
import br.gov.caixa.loterias.feature.marketplace.presentation.route.MarketplaceDetailRoute
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceDetailScreen
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplacePoolConfirmation
import br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel.MarketplaceDetailViewModel
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MarketplaceDetailParityUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun nativeHeaderPrizeAndDetailAmountsNeverExposeBackendEnumOrDecimalStrings() {
        compose.setContent {
            CaixaDSTheme { MarketplaceDetailScreen(MarketplaceDetailUiState(detail = detail(), isLoading = false), {}, {}, {}, {}, {}) }
        }
        compose.onNodeWithText("mega-sena").assertIsDisplayed()
        compose.onNodeWithText("3070").assertIsDisplayed()
        compose.onNodeWithText("14/10").assertIsDisplayed()
        compose.onNodeWithText("R$ 150.000.000,00").assertIsDisplayed()
        compose.onNodeWithText("(R$ 150 Milhões)").assertIsDisplayed()
        compose.onNodeWithText("Valor da Cota: R$ 10,00").assertIsDisplayed()
        compose.onNodeWithText("Tarifa de Serviço: R$ 0,50").assertIsDisplayed()
        compose.onNodeWithText("Total: R$ 10,50").assertIsDisplayed()
        compose.onNodeWithText("MEGA_SENA").assertDoesNotExist()
        compose.onNodeWithText("150000000.00", substring = true).assertDoesNotExist()
    }

    @Test fun nativeNumberGridHasSixColumnsAndPurchasePanelStaysVisibleAfterScrolling() {
        compose.setContent {
            CaixaDSTheme { MarketplaceDetailScreen(MarketplaceDetailUiState(detail = detail(), isLoading = false), {}, {}, {}, {}, {}) }
        }
        val panelBefore = compose.onNodeWithTag("pool-detail-purchase-panel").fetchSemanticsNode().boundsInRoot
        compose.onNodeWithText("Jogo 1").performScrollTo().assertIsDisplayed()
        val first = compose.onNodeWithTag("pool-detail-ball-0-0").getUnclippedBoundsInRoot()
        val sixth = compose.onNodeWithTag("pool-detail-ball-0-5").getUnclippedBoundsInRoot()
        val seventh = compose.onNodeWithTag("pool-detail-ball-0-6").getUnclippedBoundsInRoot()
        val tolerance = 1f / compose.density.density
        assertEquals(35.dp.value, (first.right - first.left).value, tolerance)
        assertEquals(first.top.value, sixth.top.value, tolerance)
        assertTrue("CX GridLayoutManager has six columns", seventh.top > first.bottom)
        compose.onNodeWithTag("pool-detail-ball-0-6").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("pool-detail-add-to-cart").assertIsDisplayed()
        assertEquals(panelBefore, compose.onNodeWithTag("pool-detail-purchase-panel").fetchSemanticsNode().boundsInRoot)
    }

    @Test fun nativeFixedQuantityActionsSendOriginalOrderAndReservationThroughExistingViewModel() {
        var order: MarketplacePoolOrder? = null
        var confirmation: MarketplacePoolConfirmation? = null
        val model = MarketplaceDetailViewModel(
            "B1",
            GetMarketplaceDetailUseCase { AppResult.Success(detail()) },
            AddMarketplacePoolToCartUseCase {
                order = it
                AppResult.Success(
                    ShoppingCart(
                        1,
                        it.totalAmount,
                        listOf(
                            CartItem(
                                id = 1,
                                title = "Cota",
                                description = "",
                                amount = it.totalAmount,
                                kind = CartItemKind.POOL_SHARE,
                                poolShare = CartPoolShare(
                                    code = "B1",
                                    reservedShare = 2,
                                    totalShares = 100,
                                    expiresAt = "2026-10-14T23:59:00Z"
                                )
                            )
                        )
                    )
                )
            },
            FixedCoroutineDispatchers(Dispatchers.Main.immediate)
        )
        compose.setContent { CaixaDSTheme { MarketplaceDetailRoute(model, {}, { confirmation = it }) } }
        compose.onNodeWithContentDescription("Aumentar cotas").performClick()
        compose.onNodeWithContentDescription("Diminuir cotas").performClick()
        compose.onNodeWithContentDescription("Aumentar cotas").performClick()
        compose.onNodeWithTag("pool-detail-add-to-cart").performClick()
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals("B1", order?.code)
            assertEquals("MEGA_SENA", order?.modality)
            assertEquals(2, order?.requestedShares)
            assertEquals(BigDecimal("21.00"), order?.totalAmount)
            assertEquals("B1", confirmation?.poolCode)
            assertEquals("2026-10-14T23:59:00Z", confirmation?.expiresAt)
        }
    }

    @Test fun favoriteUsesNativeConfirmationBeforeInvokingExistingService() {
        var favoriteWrites = 0
        val model = MarketplaceDetailViewModel(
            "B1",
            GetMarketplaceDetailUseCase { AppResult.Success(detail()) },
            AddMarketplacePoolToCartUseCase { error("Unused") },
            FixedCoroutineDispatchers(Dispatchers.Main.immediate),
            setRetailerFavorite = br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase { id, favorite ->
                assertEquals(10L, id)
                assertTrue(favorite)
                favoriteWrites++
                AppResult.Success(Unit)
            },
            initialFavorite = false
        )
        compose.setContent { CaixaDSTheme { MarketplaceDetailRoute(model, {}, {}) } }
        compose.onNodeWithContentDescription("Favoritar lotérica").performClick()
        compose.onNodeWithText("Deseja incluir Lotérica Nazaré na sua lista de favoritas?").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, favoriteWrites) }
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithContentDescription("Favoritar lotérica").performClick()
        compose.onNodeWithText("Confirmar").performClick()
        compose.waitForIdle()
        compose.onNodeWithContentDescription("Excluir lotérica favorita").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, favoriteWrites) }
    }

    private fun detail() = MarketplacePoolDetail(
        "B1", "MEGA_SENA", 3070, 10, "Lotérica Nazaré", "01.234567-8", "Belém - PA",
        "14/10/2026 às 20:00", "150000000.00", 81, 100, 8, 9, "10.00", "0.50", "10.50",
        listOf(MarketplaceBet((1..9).toList(), emptyList(), false)), drawDate = "14/10/2026",
        serviceFeePerShare = BigDecimal("0.50"), lastSharePriceWithFeeValue = BigDecimal("10.50")
    )
}
