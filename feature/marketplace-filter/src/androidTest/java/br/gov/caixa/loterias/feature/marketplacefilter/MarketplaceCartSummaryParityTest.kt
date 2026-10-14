package br.gov.caixa.loterias.feature.marketplacefilter

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import java.math.BigDecimal
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Synthetic snapshots only; opening the cart invokes a callback without making purchases. */
class MarketplaceCartSummaryParityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun positiveCartUsesActualAmountAndFullBetCountAndOpensCart() {
        var opened = false
        compose.setContent {
            CaixaDSTheme {
                MarketplaceFilterScreen(MarketplaceFilterUiState(), {}, cartTotal = BigDecimal("39.00"),
                    cartBetCount = 9, onCart = { opened = true })
            }
        }
        compose.onNodeWithText("R$ 39,00", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("9", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("Valor atual do carrinho").performClick()
        compose.runOnIdle { assertTrue(opened) }
    }

    @Test fun emptyCartHidesTheSummaryEvenWhenCountIsNonzero() {
        compose.setContent {
            CaixaDSTheme {
                MarketplaceFilterScreen(MarketplaceFilterUiState(), {}, cartTotal = BigDecimal.ZERO,
                    cartBetCount = 9, onCart = {})
            }
        }
        compose.onNodeWithText("Valor atual do carrinho", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("R$ 00,00", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("Filtros").assertIsDisplayed()
    }
}
