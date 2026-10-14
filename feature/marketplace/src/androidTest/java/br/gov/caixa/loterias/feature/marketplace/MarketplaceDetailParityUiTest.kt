package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.marketplace.*
import br.gov.caixa.loterias.core.domain.cart.*
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class MarketplaceDetailParityUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun nativeHeaderPrizeAndDetailAmountsNeverExposeBackendEnumOrDecimalStrings() {
        compose.setContent { CaixaDSTheme { MarketplaceDetailScreen(MarketplaceDetailUiState(detail = detail(), isLoading = false), {}, {}, {}, {}, {}) } }
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
        compose.setContent { CaixaDSTheme { MarketplaceDetailScreen(MarketplaceDetailUiState(detail = detail(), isLoading = false), {}, {}, {}, {}, {}) } }
        val panelBefore = compose.onNodeWithTag("pool-detail-purchase-panel").fetchSemanticsNode().boundsInRoot
        compose.onNodeWithText("Jogo 1").performScrollTo().assertIsDisplayed()
        val first = compose.onNodeWithTag("pool-detail-ball-0-0").fetchSemanticsNode().boundsInRoot
        val sixth = compose.onNodeWithTag("pool-detail-ball-0-5").fetchSemanticsNode().boundsInRoot
        val seventh = compose.onNodeWithTag("pool-detail-ball-0-6").fetchSemanticsNode().boundsInRoot
        assertEquals(with(compose.density) { 35.dp.toPx() }, first.width, 1f)
        assertEquals(first.top, sixth.top, 1f)
        assertTrue("CX GridLayoutManager has six columns", seventh.top > first.bottom)
        compose.onNodeWithText("Adicionar ao carrinho").assertIsDisplayed()
        assertEquals(panelBefore, compose.onNodeWithTag("pool-detail-purchase-panel").fetchSemanticsNode().boundsInRoot)
    }

    @Test fun nativeFixedQuantityActionsSendOriginalOrderAndReservationThroughExistingViewModel() {
        var order: MarketplacePoolOrder? = null
        var confirmation: MarketplacePoolConfirmation? = null
        val model = MarketplaceDetailViewModel("B1", GetMarketplaceDetailUseCase { AppResult.Success(detail()) },
            AddMarketplacePoolToCartUseCase {
                order = it
                AppResult.Success(ShoppingCart(1, it.totalAmount, listOf(CartItem(id = 1, title = "Cota", description = "", amount = it.totalAmount,
                    kind = CartItemKind.POOL_SHARE, poolShare = CartPoolShare(code = "B1", reservedShare = 2, totalShares = 100, expiresAt = "2026-10-14T23:59:00Z")))))
            }, FixedCoroutineDispatchers(Dispatchers.Main.immediate))
        compose.setContent { CaixaDSTheme { MarketplaceDetailRoute(model, {}, { confirmation = it }) } }
        compose.onNodeWithContentDescription("Aumentar cotas").performClick()
        compose.onNodeWithContentDescription("Diminuir cotas").performClick()
        compose.onNodeWithContentDescription("Aumentar cotas").performClick()
        compose.onNodeWithText("Adicionar ao carrinho").performClick()
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
        val model = MarketplaceDetailViewModel("B1", GetMarketplaceDetailUseCase { AppResult.Success(detail()) },
            AddMarketplacePoolToCartUseCase { error("Unused") }, FixedCoroutineDispatchers(Dispatchers.Main.immediate),
            setRetailerFavorite = br.gov.caixa.loterias.core.domain.retail.SetRetailerFavoriteUseCase { id, favorite ->
                assertEquals(10L, id); assertTrue(favorite); favoriteWrites++; AppResult.Success(Unit)
            }, initialFavorite = false)
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

    private fun detail() = MarketplacePoolDetail("B1", "MEGA_SENA", 3070, 10, "Lotérica Nazaré", "01.234567-8", "Belém - PA",
        "14/10/2026 às 20:00", "150000000.00", 81, 100, 8, 9, "10.00", "0.50", "10.50",
        listOf(MarketplaceBet((1..9).toList(), emptyList(), false)), drawDate = "14/10/2026",
        serviceFeePerShare = BigDecimal("0.50"), lastSharePriceWithFeeValue = BigDecimal("10.50"))
}
