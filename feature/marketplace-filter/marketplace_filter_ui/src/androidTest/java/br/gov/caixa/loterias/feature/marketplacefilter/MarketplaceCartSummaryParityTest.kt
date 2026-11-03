package br.gov.caixa.loterias.feature.marketplacefilter

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.createFeatureComposeRule
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.screen.MarketplaceFilterScreenDelivered
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState
import java.math.BigDecimal
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Apenas retratos sintéticos; abrir o carrinho invoca um callback sem realizar compras. */
class MarketplaceCartSummaryParityTest {
    @get:Rule val compose = createFeatureComposeRule()

    @Test fun positiveCartUsesActualAmountAndFullBetCountAndOpensCart() {
        var opened = false
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                MarketplaceFilterScreenDelivered(
                    MarketplaceFilterUiState(),
                    {},
                    cartTotal = BigDecimal("39.00"),
                    cartBetCount = 9,
                    onCart = { opened = true }
                )
            }
        }
        compose.onNodeWithText("R$ 39,00", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("9", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("Valor atual do carrinho").performClick()
        compose.runOnIdle { assertTrue(opened) }
    }

    @Test fun emptyCartHidesTheSummaryEvenWhenCountIsNonzero() {
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                MarketplaceFilterScreenDelivered(
                    MarketplaceFilterUiState(),
                    {},
                    cartTotal = BigDecimal.ZERO,
                    cartBetCount = 9,
                    onCart = {}
                )
            }
        }
        compose.onNodeWithText("Valor atual do carrinho", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("R$ 00,00", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("Filtros").assertIsDisplayed()
    }
}
