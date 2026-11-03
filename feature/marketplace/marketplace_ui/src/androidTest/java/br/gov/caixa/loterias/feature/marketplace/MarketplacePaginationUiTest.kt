package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.createFeatureComposeRule
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceScreenActions
import br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceScreenNavigation
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceScreenDelivered
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MarketplacePaginationUiTest {
    @get:Rule val compose = createFeatureComposeRule()
    private val pools = (1..12).map {
        MarketplacePool(
            "B$it", "MEGA_SENA", 3070,
            "Lotérica $it", "14/10/2026", "10", 10, 3, 6, false
        )
    }

    @Test fun scrollingToBottomRequestsNextPageWithoutAnInventedLoadMoreButton() {
        var requests = 0
        compose.setFeatureContent { LoteriasCaixaDSTheme { Screen(MarketplaceUiState(pools = pools, canLoadMore = true)) { requests++ } } }
        scrollToBottom()
        compose.waitUntil { requests == 1 }
        compose.onNodeWithText("Carregar mais", useUnmergedTree = true).assertDoesNotExist()
        compose.runOnIdle { assertEquals(1, requests) }
    }

    @Test fun bottomOfLoadingPageDoesNotStartAnotherRequest() {
        var requests = 0
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                Screen(
                    MarketplaceUiState(
                        pools = pools,
                        canLoadMore = true,
                        isLoadingMore = true
                    )
                ) { requests++ }
            }
        }
        scrollToBottom()
        compose.runOnIdle { assertEquals(0, requests) }
    }

    private fun scrollToBottom() {
        val list = compose.onNodeWithTag("marketplace-pools-list")
        list.performScrollToNode(hasTestTag("pool-list-B12"))
        list.performTouchInput { swipeUp() }
        compose.waitForIdle()
    }

    @Composable private fun Screen(state: MarketplaceUiState, next: () -> Unit) {
        MarketplaceScreenDelivered(
            state = state,
            navigation = MarketplaceScreenNavigation(
                onBack = {},
                onFilter = {},
                onHelp = {},
                onRetailerSelected = {},
                onPoolSelected = {},
                onCart = null,
                onRetailerSearch = {}
            ),
            actions = MarketplaceScreenActions(
                onRetry = {},
                onLoadMore = next,
                onToggleExpanded = {},
                onDecrease = {},
                onIncrease = {},
                onFavorite = {},
                onAddToCart = {},
                onSelectTab = {},
                onRemoveRetailer = {}
            ),
            retailerView = false,
            cartTotal = java.math.BigDecimal.ZERO,
            cartBetCount = 0
        )
    }
}
