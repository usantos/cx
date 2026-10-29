package br.gov.caixa.loterias.feature.payment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.presentation.components.PaymentProviderCard
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentVisualAssets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w360dp-h640dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PaymentProviderLargeFontTest {
    @get:Rule val compose = createComposeRule()

    @Test fun providerActionsKeepWholeWordsAndTheirOwnCallbacksAtDoubleFont() {
        val original = RuntimeEnvironment.getFontScale()
        RuntimeEnvironment.setFontScale(2f)
        try {
            var additions = 0
            var expansions = 0
            val method = PaymentMethod(PaymentMethod.MERCADO_PAGO_ID, "Mercado Pago")
            val cards = listOf("first", "second", "third").map {
                SavedPaymentCard(it, method.id, "000000", "1111", 1, EXPIRATION_YEAR, SECURITY_DIGITS, "visa", "visa")
            }
            compose.setContent {
                CaixaDSTheme {
                    Box(Modifier.size(360.dp, 640.dp)) {
                        Column(Modifier.verticalScroll(rememberScrollState())) {
                            PaymentProviderCard(
                                method, cards, cards.take(2), false, PaymentVisualAssets(), {},
                                { additions++ }, {}, { expansions++ }
                            )
                        }
                    }
                }
            }
            listOf("Adicionar Cartão", "Listar Mais").forEach { label ->
                val action = compose.onNodeWithText(label, ignoreCase = true).performScrollTo().assertIsDisplayed()
                val layouts = mutableListOf<TextLayoutResult>()
                action.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
                val layout = layouts.single()
                assertFalse("Complete provider action must fit", layout.hasVisualOverflow)
                assertTrue("Words must not break letter by letter: $label", layout.lineCount <= MAX_ACTION_LINES)
                action.performClick()
            }
            compose.runOnIdle {
                assertEquals(1, additions)
                assertEquals(1, expansions)
            }
        } finally {
            RuntimeEnvironment.setFontScale(original)
        }
    }
}

private const val EXPIRATION_YEAR = 2030
private const val SECURITY_DIGITS = 3
private const val MAX_ACTION_LINES = 2
