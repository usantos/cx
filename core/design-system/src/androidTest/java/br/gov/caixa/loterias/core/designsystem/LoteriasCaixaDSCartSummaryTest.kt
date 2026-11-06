package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.cart.LoteriasCaixaDSCartSummary
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LoteriasCaixaDSCartSummaryTest {
    @get:Rule val compose = createComposeRule()

    @Test fun summaryPresentsOriginalLabelAmountFullAccessibleCountAndOptionalNavigation() {
        var opens = 0
        compose.setContent {
            LoteriasCaixaDSTheme(darkTheme = false) {
                Box(Modifier.width(360.dp)) {
                    LoteriasCaixaDSCartSummary(
                        BigDecimal(
                            "25.50"
                        ),
                        CartSummaryTestExpectations.OVERFLOWING_BET_COUNT,
                        Modifier.testTag("summary"),
                        onClick = {
                            opens++
                        }
                    )
                }
            }
        }
        compose.onNodeWithTag("summary").assertHeightIsEqualTo(36.dp)
            .assert(SemanticsMatcher.expectValue(androidx.compose.ui.semantics.SemanticsProperties.StateDescription, "1001 apostas"))
            .assertHasClickAction().performClick()
        compose.onNodeWithText("Valor atual do carrinho", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("R$ 25,50", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("...", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("999+", useUnmergedTree = true).assertDoesNotExist()
        compose.runOnIdle { assertEquals(1, opens) }
    }

    @Test fun cartSummaryHasNoNavigationAndFollowsAmountAndCountUpdates() {
        val total = mutableStateOf(BigDecimal("3.50"))
        val count = mutableStateOf(CartSummaryTestExpectations.MAXIMUM_VISIBLE_BET_COUNT)
        compose.setContent {
            LoteriasCaixaDSTheme(darkTheme = false) {
                LoteriasCaixaDSCartSummary(total.value, count.value, Modifier.testTag("summary"))
            }
        }
        compose.onNodeWithTag("summary").assertHasNoClickAction()
        compose.onNodeWithText("999", useUnmergedTree = true).assertIsDisplayed()
        compose.runOnIdle {
            total.value = BigDecimal("7.00")
            count.value = 2
        }
        compose.onNodeWithText("R$ 7,00", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("2", useUnmergedTree = true).assertIsDisplayed()
        compose.runOnIdle { total.value = BigDecimal.ZERO }
        compose.onNodeWithTag("summary").assertDoesNotExist()
        compose.runOnIdle { total.value = BigDecimal("-1.00") }
        compose.onNodeWithTag("summary").assertDoesNotExist()
    }
}

private object CartSummaryTestExpectations {
    const val OVERFLOWING_BET_COUNT = 1001
    const val MAXIMUM_VISIBLE_BET_COUNT = 999
}
