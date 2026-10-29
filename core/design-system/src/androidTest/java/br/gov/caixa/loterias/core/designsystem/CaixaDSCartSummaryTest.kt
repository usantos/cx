package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.core.designsystem.components.cart.CaixaDSCartSummary
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme

class CaixaDSCartSummaryTest {
    @get:Rule val compose = createComposeRule()

    @Test fun summaryPresentsOriginalLabelAmountFullAccessibleCountAndOptionalNavigation() {
        var opens = 0
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                Box(Modifier.width(360.dp)) {
                    CaixaDSCartSummary(BigDecimal("25.50"), 1001, Modifier.testTag("summary"), onClick = { opens++ })
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
        val count = mutableStateOf(999)
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                CaixaDSCartSummary(total.value, count.value, Modifier.testTag("summary"))
            }
        }
        compose.onNodeWithTag("summary").assertHasNoClickAction()
        compose.onNodeWithText("999", useUnmergedTree = true).assertIsDisplayed()
        compose.runOnIdle { total.value = BigDecimal("7.00"); count.value = 2 }
        compose.onNodeWithText("R$ 7,00", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("2", useUnmergedTree = true).assertIsDisplayed()
        compose.runOnIdle { total.value = BigDecimal.ZERO }
        compose.onNodeWithTag("summary").assertDoesNotExist()
        compose.runOnIdle { total.value = BigDecimal("-1.00") }
        compose.onNodeWithTag("summary").assertDoesNotExist()
    }
}
