package br.gov.caixa.loterias.core.designsystem

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.bottom
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.top
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.components.cart.LoteriasCaixaDSCartActions
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import kotlin.math.roundToInt
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LoteriasCaixaDSCartActionsTest {
    @get:Rule val compose = createComposeRule()

    @Test fun actionsRetainDimensionsSpacingTwoLinesAndMixedWeight() {
        var continueCalls = 0
        var paymentCalls = 0
        compose.setContent {
            LoteriasCaixaDSTheme(darkTheme = false) { LoteriasCaixaDSCartActions({ continueCalls++ }, { paymentCalls++ }) }
        }
        val continueButton = compose.onNodeWithText("Continuar apostando")
        val paymentButton = compose.onNodeWithText("Avançar\npara a forma de pagamento")
        continueButton.assertWidthIsEqualTo(240.dp).assertHeightIsEqualTo(56.dp).performClick()
        paymentButton.assertWidthIsEqualTo(240.dp).assertHeightIsEqualTo(70.dp).performClick()
        val continueBounds = continueButton.fetchSemanticsNode().boundsInRoot
        val paymentBounds = paymentButton.fetchSemanticsNode().boundsInRoot
        val density = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.density
        // O layout usa pixels inteiros: 14dp em densidade fracionária não voltam a exatamente 14dp na conversão inversa.
        assertEquals(
            (CartActionsTestExpectations.EXPECTED_ACTION_GAP_DP * density).roundToInt().toFloat(),
            paymentBounds.top - continueBounds.bottom,
            0f
        )
        val text = paymentButton.fetchSemanticsNode().config[SemanticsProperties.Text].single()
        assertEquals(FontWeight.Bold, text.spanStyles.single().item.fontWeight)
        assertEquals(0, text.spanStyles.single().start)
        assertEquals("Avançar".length, text.spanStyles.single().end)
        compose.runOnIdle {
            assertEquals(1, continueCalls)
            assertEquals(1, paymentCalls)
        }
    }

    @Test fun hostMayDisablePaymentWhileProcessingWithoutDisablingContinueBetting() {
        val enabled = mutableStateOf(false)
        var calls = 0
        compose.setContent {
            LoteriasCaixaDSTheme(darkTheme = false) { LoteriasCaixaDSCartActions({}, { calls++ }, paymentEnabled = enabled.value) }
        }
        compose.onNodeWithText("Continuar apostando").assertIsEnabled()
        val payment = compose.onNodeWithText("Avançar\npara a forma de pagamento")
        payment.assertIsNotEnabled().performClick()
        compose.runOnIdle {
            assertEquals(0, calls)
            enabled.value = true
        }
        payment.assertIsEnabled().performClick()
        compose.runOnIdle { assertEquals(1, calls) }
    }
}

private object CartActionsTestExpectations {
    const val EXPECTED_ACTION_GAP_DP = 14f
}
