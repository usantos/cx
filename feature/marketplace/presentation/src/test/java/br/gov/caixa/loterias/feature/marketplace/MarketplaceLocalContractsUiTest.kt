// Literais numéricos descrevem conteúdos sintéticos, níveis da API da plataforma e limites esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.marketplace

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSStatusBar
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceBet
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailBetGrid
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailLotecaMatch
import br.gov.caixa.loterias.feature.marketplace.presentation.components.DetailPurchasePanel
import br.gov.caixa.loterias.feature.marketplace.presentation.components.FavoriteRetailersContent
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceDetailScreen
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceScreen
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceTab
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Contratos locais de interface complementam testes de entrada em janela em primeiro plano e rasterização de recuos no aparelho. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w480dp-h900dp")
class MarketplaceLocalContractsUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun favoriteRetailerCardUsesNarrowNameWrappingAndKeepsOpenSelection() {
        val retailer = Retailer(1L, "000001", "LOTÉRICA ABCDEFGHIJKLMNOPQRS CASA", "Razão", "Rua", "Centro", "Cidade", "DF", true)
        var opened: Retailer? = null
        show {
            FavoriteRetailersContent(
                MarketplaceUiState(favoriteRetailers = listOf(retailer)),
                onOpen = { opened = it },
                onRemove = {}
            )
        }
        compose.onNodeWithText("Lotérica\nAbcdefghijklmnopqrs Casa", useUnmergedTree = true).assertExists()
        compose.onNodeWithText("Ver Bolões").performClick()
        compose.runOnIdle { assertEquals(retailer, opened) }
    }

    @Test fun detailLoadingOmitsPurchasePanelUntilPayloadIsReady() {
        val state = mutableStateOf(MarketplaceDetailUiState(detail = detail()))
        show { MarketplaceDetailScreen(state.value, {}, {}, {}, {}, {}) }
        compose.onNodeWithTag("pool-detail-purchase-panel").assertDoesNotExist()
        compose.runOnIdle { state.value = state.value.copy(isLoading = false) }
        compose.onNodeWithTag("pool-detail-purchase-panel").assertExists()
        compose.onNodeWithText("Adicionar ao carrinho").assertExists()
    }

    @Test fun detailErrorHidesPurchasePanelAndOffersRetry() {
        var retries = 0
        show {
            MarketplaceDetailScreen(
                MarketplaceDetailUiState(
                    detail = detail(),
                    isLoading = false,
                    errorMessage = "Consulta indisponível"
                ),
                {
                },
                { retries++ },
                {},
                {},
                {}
            )
        }
        compose.onNodeWithText("Consulta indisponível").assertExists()
        compose.onNodeWithTag("pool-detail-purchase-panel").assertDoesNotExist()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }

    @Test fun reservationInFlightDisablesQuantityAndAddControlsThenRestoresThem() {
        var decreases = 0
        var increases = 0
        var adds = 0
        val state =
            mutableStateOf(MarketplaceDetailUiState(detail = detail(), selectedShares = 2, isLoading = false, isAddingToCart = true))
        show { DetailPurchasePanel(state.value, { decreases++ }, { increases++ }, { adds++ }, BigDecimal.ZERO, 0, null) }
        compose.onNodeWithContentDescription("Diminuir cotas").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Aumentar cotas").assertIsNotEnabled()
        compose.onNodeWithTag("pool-detail-add-to-cart").assertIsNotEnabled()
        compose.onNodeWithText("Adicionando…").assertExists()
        compose.runOnIdle { state.value = state.value.copy(isAddingToCart = false) }
        compose.onNodeWithContentDescription("Diminuir cotas").assertIsEnabled().performClick()
        compose.onNodeWithContentDescription("Aumentar cotas").assertIsEnabled().performClick()
        compose.onNodeWithTag("pool-detail-add-to-cart").assertIsEnabled().performClick()
        compose.runOnIdle {
            assertEquals(1, decreases)
            assertEquals(1, increases)
            assertEquals(1, adds)
        }
    }

    @Test fun purchasePanelDisablesUnavailableCartAndBoundsQuantity() {
        val state = mutableStateOf(MarketplaceDetailUiState(detail = detail(), selectedShares = 1, isLoading = false))
        show { DetailPurchasePanel(state.value, {}, {}, {}, BigDecimal.ZERO, 0, null) }
        compose.onNodeWithContentDescription("Carrinho de apostas").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Diminuir cotas").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Aumentar cotas").assertIsEnabled()
        compose.runOnIdle { state.value = state.value.copy(selectedShares = detail().availableShares) }
        compose.onNodeWithContentDescription("Aumentar cotas").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Diminuir cotas").assertIsEnabled()
    }

    @Test fun purchasePanelShowsServerErrorAndCapsBadgeWithoutChangingCartCallback() {
        var carts = 0
        show {
            DetailPurchasePanel(MarketplaceDetailUiState(detail = detail(), isLoading = false, addToCartError = "Cotas insuficientes"), {
            }, {}, {}, BigDecimal.TEN, 1001, { carts++ })
        }
        compose.onNodeWithText("Cotas insuficientes").assertExists()
        compose.onNodeWithText("+999").assertExists()
        compose.onNodeWithContentDescription("Carrinho de apostas").performClick()
        compose.runOnIdle { assertEquals(1, carts) }
    }

    @Test fun nativeWindowFlagsFollowDetailRequestAndRestoreWhenDetailLeaves() {
        val detailVisible = mutableStateOf(true)
        compose.runOnUiThread { compose.activity.window.isNavigationBarContrastEnforced = true }
        show {
            CaixaDSStatusBar {
                if (detailVisible.value) {
                    MarketplaceDetailScreen(MarketplaceDetailUiState(), {}, {}, {}, {}, {})
                } else {
                    Box(Modifier.fillMaxSize().background(Color.White))
                }
            }
        }
        compose.runOnIdle {
            val controller = WindowCompat.getInsetsController(compose.activity.window, compose.activity.window.decorView)
            assertTrue(controller.isAppearanceLightStatusBars)
            assertTrue(controller.isAppearanceLightNavigationBars)
            assertFalse(compose.activity.window.isNavigationBarContrastEnforced)
            detailVisible.value = false
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertFalse(
                WindowCompat.getInsetsController(compose.activity.window, compose.activity.window.decorView).isAppearanceLightStatusBars
            )
        }
    }

    @Test fun numericGridNormalizesZeroAndHundredAndRetainsRingGeometryTags() {
        show { DetailBetGrid(2, MarketplaceBet(listOf(0, 100, 9), emptyList(), false), Color.Green, "MEGA_SENA") }
        compose.onNodeWithText("Jogo 3").assertExists()
        compose.onAllNodesWithText("00").assertCountEquals(2)
        compose.onNodeWithText("9").assertExists()
        compose.onNodeWithTag("pool-detail-ball-2-0").assertWidthIsEqualTo(35.dp).assertHeightIsEqualTo(35.dp)
        compose.onNodeWithTag("pool-detail-ball-2-2").assertExists()
        compose.onNodeWithTag("pool-detail-ball-2-3").assertDoesNotExist()
    }

    @Test fun superSevenGridExposesColumnNumberSemantics() {
        show {
            DetailBetGrid(
                0,
                MarketplaceBet(emptyList(), emptyList(), false, numberColumns = listOf(listOf(8, 9), listOf(0))),
                Color.Green,
                "SUPER_SETE"
            )
        }
        compose.onNodeWithText("Colunas").assertExists()
        compose.onNodeWithContentDescription("Coluna 1, número 8").assertExists()
        compose.onNodeWithContentDescription("Coluna 1, número 9").assertExists()
        compose.onNodeWithContentDescription("Coluna 2, número 0").assertExists()
        compose.onNodeWithTag("pool-detail-super-0-2-0").assertDoesNotExist()
    }

    @Test fun lotecaReadOnlySelectionExposesSelectedAndUnselectedOutcomes() {
        val match = SportsMatch(1, SportsTeam(1, "Flamengo", "RJ"), SportsTeam(2, "Vasco", "RJ"))
        show { DetailLotecaMatch(0, LotecaMatchSelection(match, true, false, false)) }
        compose.onNodeWithTag("pool-detail-loteca-0-home").assertIsSelected()
        compose.onNodeWithTag("pool-detail-loteca-0-draw").assertIsNotSelected()
        compose.onNodeWithTag("pool-detail-loteca-0-away").assertIsNotSelected()
        compose.onNodeWithText("Flamengo/RJ").assertExists()
        compose.onNodeWithText("Vasco/RJ").assertExists()
    }

    @Test fun favoriteRetailerSearchDispatchesCurrentSupportedSearchAction() {
        var searches = 0
        show {
            MarketplaceScreen(MarketplaceUiState(selectedTab = MarketplaceTab.FAVORITE_RETAILERS), {
            }, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, false, onRetailerSearch = { searches++ })
        }
        compose.onNodeWithContentDescription("Pesquisar lotérica").assertIsEnabled().performClick()
        compose.runOnIdle { assertEquals(1, searches) }
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setContent { CaixaDSTheme { content() } }
    }
    private fun detail() = MarketplacePoolDetail(
        "B1", "MEGA_SENA", 3070, 10, "Lotérica", "1", "Cidade - SP",
        "14/10/2026", "1000.00", 20, 50, 2, 8, "10.00", "0.50", "10.50", emptyList(),
        lastSharePriceWithFeeValue = BigDecimal(
            "10.50"
        )
    )
}
