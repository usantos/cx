package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceTab
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceScreen

class MarketplaceCoordinationUiTest {
    @get:Rule val compose = createComposeRule()
    private val pools = (1..12).map {
        MarketplacePool("B$it", "MEGA_SENA", 3070, "Lotérica $it", "18/10/2026", "10", 10, 3, 6, false)
    }

    @Test fun paginationUsesTheReplacementCallbackWithoutChangingEffectKeys() {
        val revision = mutableIntStateOf(0)
        val requests = intArrayOf(0, 0)
        compose.setContent {
            val current = revision.intValue
            CaixaDSTheme {
                Screen(MarketplaceUiState(pools = pools, canLoadMore = true), onLoadMore = { requests[current]++ })
            }
        }
        compose.runOnIdle { revision.intValue = 1 }
        compose.runOnIdle { assertEquals(0, requests.sum()) }
        compose.onNodeWithTag("marketplace-pools-list").performScrollToNode(hasTestTag("pool-list-B12"))
        compose.onNodeWithTag("marketplace-pools-list").performTouchInput { swipeUp() }
        compose.waitUntil { requests[1] == 1 }
        compose.runOnIdle {
            assertEquals(0, requests[0])
            assertEquals(1, requests[1])
        }
    }

    @Test fun appendFailureUsesNativeErrorCardWithoutRetryOrExhaustion() {
        var retries = 0
        compose.setContent {
            CaixaDSTheme {
                Screen(MarketplaceUiState(pools = pools, errorMessage = "Falha da próxima página", canLoadMore = false),
                    onRetry = { retries++ })
            }
        }
        compose.onNodeWithTag("marketplace-pools-list").assertDoesNotExist()
        compose.onNodeWithText("Falha da próxima página").assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
        compose.onNodeWithText("Fim da lista").assertDoesNotExist()
        compose.runOnIdle { assertEquals(0, retries) }
    }

    @Test fun favoriteRetailersKeepItemActionsAndRetailerViewForcesPools() {
        val retailer = Retailer(10, "001", "Lotérica Central", "Central", "Rua Um", "Centro", "Brasília", "DF", true)
        val state = mutableStateOf(MarketplaceUiState(pools = pools, favoriteRetailers = listOf(retailer)))
        val retailerView = mutableStateOf(false)
        val opened = mutableListOf<Retailer>()
        val removed = mutableListOf<Long>()
        var filters = 0
        compose.setContent {
            CaixaDSTheme {
                Screen(state.value, onFilter = { filters++ },
                    onSelectTab = { state.value = state.value.copy(selectedTab = it) },
                    onRetailerSelected = { opened += it }, onRemoveRetailer = { removed += it },
                    retailerView = retailerView.value)
            }
        }
        compose.onNodeWithContentDescription("Filtrar bolões").performClick()
        compose.onNodeWithText("Lotéricas Favoritas").performClick()
        compose.onNodeWithContentDescription("Pesquisar lotérica").assertIsEnabled()
        compose.onNodeWithText("Lotérica Central").assertIsDisplayed()
        compose.onNodeWithText("Ver Bolões").performClick()
        compose.onNodeWithContentDescription("Excluir Lotérica Central").performClick()
        compose.runOnIdle {
            assertEquals(1, filters)
            assertEquals(listOf(retailer), opened)
            assertEquals(listOf(10L), removed)
            retailerView.value = true
        }
        compose.onNodeWithText("Lotérica favorita").assertIsDisplayed()
        compose.onNodeWithText("Lotéricas Favoritas").assertDoesNotExist()
        compose.onNodeWithContentDescription("Pesquisar lotérica").assertDoesNotExist()
        compose.onNodeWithContentDescription("Filtrar bolões").assertDoesNotExist()
        compose.onNodeWithTag("marketplace-pools-list").assertExists()
        compose.onNodeWithTag("marketplace-carousel").assertExists()
    }

    @Composable
    private fun Screen(
        state: MarketplaceUiState,
        onLoadMore: () -> Unit = {},
        onRetry: () -> Unit = {},
        onFilter: () -> Unit = {},
        onSelectTab: (MarketplaceTab) -> Unit = {},
        onRetailerSelected: (Retailer) -> Unit = {},
        onRemoveRetailer: (Long) -> Unit = {},
        retailerView: Boolean = false,
    ) {
        MarketplaceScreen(state, onBack = {}, onFilter = onFilter, onHelp = {},
            onRetailerSelected = onRetailerSelected, onRetry = onRetry, onLoadMore = onLoadMore,
            onPoolSelected = {}, onToggleExpanded = {}, onDecrease = {}, onIncrease = {},
            onFavorite = {}, onAddToCart = {}, onSelectTab = onSelectTab,
            onRemoveRetailer = onRemoveRetailer, retailerView = retailerView)
    }
}
