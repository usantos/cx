package br.gov.caixa.loterias.feature.payment

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.payment.presentation.screen.PixGuidanceScreenDelivered
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentTerminalUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@ResourcesMode(ResourcesMode.Mode.NATIVE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(sdk = [36], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
class PaymentAccessibilityTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()
    private fun role(value: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, value)

    @Test fun pixConsentIncludesStatementAndStateOnSameControl() {
        val spec = WorkflowSpec("pix", "Pix", "Orientações", primaryAction = "Pagar")
        val accepted = mutableStateOf(false)
        var calls = 0
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                PixGuidanceScreenDelivered(
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
