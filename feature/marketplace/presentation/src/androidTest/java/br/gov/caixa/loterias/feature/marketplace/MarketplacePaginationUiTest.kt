package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceScreen

class MarketplacePaginationUiTest {
    @get:Rule val compose = createComposeRule()
    private val pools = (1..12).map { MarketplacePool("B$it", "MEGA_SENA", 3070,
        "Lotérica $it", "14/10/2026", "10", 10, 3, 6, false) }

    @Test fun scrollingToBottomRequestsNextPageWithoutAnInventedLoadMoreButton() {
        var requests = 0
        compose.setContent { CaixaDSTheme { Screen(MarketplaceUiState(pools = pools, canLoadMore = true)) { requests++ } } }
        scrollToBottom()
        compose.waitUntil { requests == 1 }
        compose.onNodeWithText("Carregar mais", useUnmergedTree = true).assertDoesNotExist()
        compose.runOnIdle { assertEquals(1, requests) }
    }

    @Test fun bottomOfLoadingPageDoesNotStartAnotherRequest() {
        var requests = 0
        compose.setContent { CaixaDSTheme { Screen(MarketplaceUiState(pools = pools,
            canLoadMore = true, isLoadingMore = true)) { requests++ } } }
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
        MarketplaceScreen(state, onBack = {}, onFilter = {}, onHelp = {}, onRetailerSelected = {},
            onRetry = {}, onLoadMore = next, onPoolSelected = {}, onToggleExpanded = {},
            onDecrease = {}, onIncrease = {}, onFavorite = {}, onAddToCart = {},
            onSelectTab = {}, onRemoveRetailer = {}, retailerView = false)
    }
}
