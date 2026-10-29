package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import java.math.BigDecimal
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceScreen

class MarketplaceCartSummaryUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun realCartAmountAndFullCountAreDisplayedAndOpenCart() {
        var opened = false
        compose.setContent { CaixaDSTheme { Screen(BigDecimal("39.00"), 9) { opened = true } } }
        compose.onNodeWithText("R$ 39,00", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("9", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("Valor atual do carrinho").performClick()
        compose.runOnIdle { assertTrue(opened) }
    }

    @Test fun zeroCartDoesNotReserveOrShowFooter() {
        compose.setContent { CaixaDSTheme { Screen(BigDecimal.ZERO, 9) {} } }
        compose.onNodeWithText("Valor atual do carrinho", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("R$ 00,00", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("Bolão CAIXA").assertIsDisplayed()
    }

    @Composable private fun Screen(total: BigDecimal, count: Int, onCart: () -> Unit) {
        MarketplaceScreen(MarketplaceUiState(), onBack = {}, onFilter = {}, onHelp = {},
            onRetailerSelected = {}, onRetry = {}, onLoadMore = {}, onPoolSelected = {},
            onToggleExpanded = {}, onDecrease = {}, onIncrease = {}, onFavorite = {},
            onAddToCart = {}, onSelectTab = {}, onRemoveRetailer = {}, retailerView = false,
            cartTotal = total, cartBetCount = count, onCart = onCart)
    }
}
