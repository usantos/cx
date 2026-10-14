package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import java.math.BigDecimal
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MarketplaceCartSummaryUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun realCartAmountAndFullCountAreDisplayedAndOpenCart() {
        var opened = false
        compose.setContent { CaixaDSTheme { screen(BigDecimal("39.00"), 9) { opened = true } } }
        compose.onNodeWithText("R$ 39,00", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("9", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("Valor atual do carrinho").performClick()
        compose.runOnIdle { assertTrue(opened) }
    }

    @Test fun zeroCartDoesNotReserveOrShowFooter() {
        compose.setContent { CaixaDSTheme { screen(BigDecimal.ZERO, 9) {} } }
        compose.onNodeWithText("Valor atual do carrinho", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("R$ 00,00", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("Bolão CAIXA").assertIsDisplayed()
    }

    @Composable private fun screen(total: BigDecimal, count: Int, onCart: () -> Unit) {
        MarketplaceScreen(MarketplaceUiState(), onBack = {}, onFilter = {}, onHelp = {},
            onRetailerSelected = {}, onRetry = {}, onLoadMore = {}, onPoolSelected = {},
            onToggleExpanded = {}, onDecrease = {}, onIncrease = {}, onFavorite = {},
            onAddToCart = {}, onSelectTab = {}, onRemoveRetailer = {}, retailerView = false,
            cartTotal = total, cartBetCount = count, onCart = onCart)
    }
}
