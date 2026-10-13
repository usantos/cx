package br.gov.caixa.loterias.core.designsystem

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CaixaDSCartActionsTest {
    @get:Rule val compose = createComposeRule()

    @Test fun actionsRetainLegacyDimensionsSpacingTwoLinesAndMixedWeight() {
        var continueCalls = 0
        var paymentCalls = 0
        compose.setContent {
            CaixaDSTheme(darkTheme = false) { CaixaDSCartActions({ continueCalls++ }, { paymentCalls++ }) }
        }
        val continueButton = compose.onNodeWithText("Continuar apostando")
        val paymentButton = compose.onNodeWithText("Avançar\npara a forma de pagamento")
        continueButton.assertWidthIsEqualTo(240.dp).assertHeightIsEqualTo(56.dp).performClick()
        paymentButton.assertWidthIsEqualTo(240.dp).assertHeightIsEqualTo(70.dp).performClick()
        val continueBounds = continueButton.getUnclippedBoundsInRoot()
        val paymentBounds = paymentButton.getUnclippedBoundsInRoot()
        assertEquals(14.dp, paymentBounds.top - continueBounds.bottom)
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
