package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceScreenActions
import br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceScreenNavigation
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceScreenDelivered
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import java.math.BigDecimal
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MarketplaceCartSummaryUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun realCartAmountAndFullCountAreDisplayedAndOpenCart() {
        var opened = false
        compose.setFeatureContent { LoteriasCaixaDSTheme { Screen(BigDecimal("39.00"), 9) { opened = true } } }
        compose.onNodeWithText("R$ 39,00", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("9", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("Valor atual do carrinho").performClick()
        compose.runOnIdle { assertTrue(opened) }
    }

    @Test fun zeroCartDoesNotReserveOrShowFooter() {
        compose.setFeatureContent { LoteriasCaixaDSTheme { Screen(BigDecimal.ZERO, 9) {} } }
        compose.onNodeWithText("Valor atual do carrinho", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("R$ 00,00", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("Bolão CAIXA").assertIsDisplayed()
    }

    @Composable private fun Screen(total: BigDecimal, count: Int, onCart: () -> Unit) {
        MarketplaceScreenDelivered(
            state = MarketplaceUiState(),
            navigation = MarketplaceScreenNavigation(
                onBack = {},
                onFilter = {},
                onHelp = {},
                onRetailerSelected = {},
                onPoolSelected = {},
                onCart = onCart,
                onRetailerSearch = {}
            ),
            actions = MarketplaceScreenActions(
                onRetry = {},
                onLoadMore = {},
                onToggleExpanded = {},
                onDecrease = {},
                onIncrease = {},
                onFavorite = {},
                onAddToCart = {},
                onSelectTab = {},
                onRemoveRetailer = {}
            ),
            retailerView = false,
            cartTotal = total,
            cartBetCount = count
        )
    }
}
