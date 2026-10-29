package br.gov.caixa.loterias.core.designsystem

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import kotlin.math.roundToInt
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.components.cart.CaixaDSCartActions

class CaixaDSCartActionsTest {
    @get:Rule val compose = createComposeRule()

    @Test fun actionsRetainDimensionsSpacingTwoLinesAndMixedWeight() {
        var continueCalls = 0
        var paymentCalls = 0
        compose.setContent {
            CaixaDSTheme(darkTheme = false) { CaixaDSCartActions({ continueCalls++ }, { paymentCalls++ }) }
        }
        val continueButton = compose.onNodeWithText("Continuar apostando")
        val paymentButton = compose.onNodeWithText("Avançar\npara a forma de pagamento")
        continueButton.assertWidthIsEqualTo(240.dp).assertHeightIsEqualTo(56.dp).performClick()
        paymentButton.assertWidthIsEqualTo(240.dp).assertHeightIsEqualTo(70.dp).performClick()
        val continueBounds = continueButton.fetchSemanticsNode().boundsInRoot
        val paymentBounds = paymentButton.fetchSemanticsNode().boundsInRoot
        val density = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.density
        // O layout usa pixels inteiros: 14dp em densidade fracionária não voltam a exatamente 14dp na conversão inversa.
        assertEquals((14f * density).roundToInt().toFloat(), paymentBounds.top - continueBounds.bottom, 0f)
        val text = paymentButton.fetchSemanticsNode().config[SemanticsProperties.Text].single()
        assertEquals(FontWeight.Bold, text.spanStyles.single().item.fontWeight)
        assertEquals(0, text.spanStyles.single().start)
        assertEquals("Avançar".length, text.spanStyles.single().end)
        compose.runOnIdle { assertEquals(1, continueCalls); assertEquals(1, paymentCalls) }
    }

    @Test fun hostMayDisablePaymentWhileProcessingWithoutDisablingContinueBetting() {
        val enabled = mutableStateOf(false)
        var calls = 0
        compose.setContent {
            CaixaDSTheme(darkTheme = false) { CaixaDSCartActions({}, { calls++ }, paymentEnabled = enabled.value) }
        }
        compose.onNodeWithText("Continuar apostando").assertIsEnabled()
        val payment = compose.onNodeWithText("Avançar\npara a forma de pagamento")
        payment.assertIsNotEnabled().performClick()
        compose.runOnIdle { assertEquals(0, calls); enabled.value = true }
        payment.assertIsEnabled().performClick()
        compose.runOnIdle { assertEquals(1, calls) }
    }
}
