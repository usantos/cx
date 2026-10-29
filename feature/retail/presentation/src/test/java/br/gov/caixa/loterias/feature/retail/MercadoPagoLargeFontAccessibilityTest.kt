package br.gov.caixa.loterias.feature.retail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeDetails
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizeMercadoPagoDataScreen
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import java.math.BigDecimal
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
class MercadoPagoLargeFontAccessibilityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun instructionsRespectSystemScaleAndRemainReadable() = atDoubleFont {
        show()
        val node = compose.onNode(hasText("Para receber seu prêmio", substring = true)).performScrollTo().assertIsDisplayed()
        val layouts = mutableListOf<TextLayoutResult>()
        node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(INSTRUCTION_SIZE, layouts.single().layoutInput.style.fontSize.value, FONT_TOLERANCE)
        assertFalse(layouts.single().hasVisualOverflow)
    }

    @Test fun filledEmailKeepsItsAccessibleNameAndEditingAction() = atDoubleFont {
        val edits = mutableListOf<String>()
        show { edits += it }
        val field = compose.onNode(hasSetTextAction()).performScrollTo().assertIsDisplayed()
        val config = field.fetchSemanticsNode().config
        val name = if (config.contains(
                SemanticsProperties.ContentDescription
            )
        ) {
            config[SemanticsProperties.ContentDescription]
        } else {
            emptyList()
        }
        assertTrue("Filled email needs a stable accessible name", name.any { it == "E-mail" })
        field.performTextReplacement("novo@exemplo.invalid")
        compose.runOnIdle { assertEquals(listOf("novo@exemplo.invalid"), edits) }
    }

    private fun show(onEmail: (String) -> Unit = {}) {
        val state = PrizeRedemptionUiState(
            details = PrizeDetails(PRIZE_ID, BigDecimal("125.50"), "synthetic", emptyList()),
            email = "teste@exemplo.invalid",
            loading = false
        )
        compose.setContent {
            CaixaDSTheme { Box(Modifier.size(360.dp, 640.dp)) { PrizeMercadoPagoDataScreen(state, {}, {}, {}, onEmail) } }
        }
    }

    private fun atDoubleFont(block: () -> Unit) {
        val original = RuntimeEnvironment.getFontScale()
        RuntimeEnvironment.setFontScale(2f)
        try {
            block()
        } finally {
            RuntimeEnvironment.setFontScale(original)
        }
    }
}

private const val FONT_TOLERANCE = .001f

private const val INSTRUCTION_SIZE = 18.199982f
private const val PRIZE_ID = 9L
