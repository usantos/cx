package br.gov.caixa.loterias.core.designsystem

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.input.KeyboardType
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterInput
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSMarketplaceInputAccessibilityJvmTest {
    @get:Rule val compose = createComposeRule()

    @Test fun filledNumericFilterKeepsSpecificNameAndErrorWhileEditingAndClearingError() {
        val value = mutableStateOf("2")
        val message = mutableStateOf<String?>("Informe pelo menos duas cotas")
        compose.setContent {
            CaixaDSTheme {
                CaixaDSMarketplaceFilterInput(
                    value.value,
                    { value.value = it },
                    "2",
                    KeyboardType.Number,
                    accessibilityLabel = "Quantidade mínima de cotas",
                    errorMessage = message.value
                )
            }
        }
        val input = compose.onNode(hasSetTextAction())
        input.assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Quantidade mínima de cotas")))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Informe pelo menos duas cotas"))
            .performTextReplacement("12")
        compose.runOnIdle {
            assertEquals("12", value.value)
            message.value = null
        }
        input.assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Error))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Quantidade mínima de cotas")))
    }

    @Test fun filledRetailerSearchRetainsStableSearchNameAfterReplacingText() {
        val value = mutableStateOf("Centro")
        compose.setContent {
            CaixaDSTheme {
                CaixaDSMarketplaceFilterInput(
                    value.value,
                    { value.value = it },
                    "Nome ou endereço",
                    KeyboardType.Text,
                    accessibilityLabel = "Buscar lotérica"
                )
            }
        }
        compose.onNode(hasSetTextAction())
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Buscar lotérica")))
            .performTextReplacement("Paulista")
        compose.runOnIdle { assertEquals("Paulista", value.value) }
        compose.onNode(hasSetTextAction())
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Buscar lotérica")))
    }
}
