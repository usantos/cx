package br.gov.caixa.loterias.feature.payment

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.pressKey
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.payment.presentation.screen.PixGuidanceScreen
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentTerminalUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
class PaymentAccessibilityTest {
    @get:Rule val compose = createComposeRule()
    private fun role(value: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, value)

    @Test fun pixConsentIncludesStatementAndStateOnSameControl() {
        val spec = WorkflowSpec("pix", "Pix", "Orientações", primaryAction = "Pagar")
        val accepted = mutableStateOf(false)
        var calls = 0
        compose.setContent {
            CaixaDSTheme {
                PixGuidanceScreen(
                    spec,
                    PaymentTerminalUiState(spec, accepted.value),
                    {},
                    {
                        calls++
                        accepted.value = !accepted.value
                    },
                    {}
                )
            }
        }
        val checkbox = compose.onNode(role(Role.Checkbox) and hasText("Estou ciente", substring = true))
        checkbox.performScrollTo().assertIsOff().performClick().assertIsOn()
        compose.runOnIdle { assertEquals(1, calls) }
    }
}
