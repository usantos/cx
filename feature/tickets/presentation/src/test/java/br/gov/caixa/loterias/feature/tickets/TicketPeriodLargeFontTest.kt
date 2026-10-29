package br.gov.caixa.loterias.feature.tickets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyTicketPeriodSelector
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
class TicketPeriodLargeFontTest {
    @get:Rule val compose = createComposeRule()

    @Test fun periodNameIsReadableAndMenuKeepsItsSelectionCallbackAtDoubleFont() {
        val original = RuntimeEnvironment.getFontScale()
        RuntimeEnvironment.setFontScale(2f)
        try {
            val selected = mutableListOf<Int>()
            compose.setContent {
                CaixaDSTheme {
                    Box(Modifier.width(300.dp)) {
                        LegacyTicketPeriodSelector(
                            "Selecione o período",
                            "Últimos 7 dias",
                            listOf("Últimos 7 dias", "Últimos 15 dias")
                        ) { selected += it }
                    }
                }
            }
            val node = compose.onNodeWithText("Últimos 7 dias").assertIsDisplayed()
            val layouts = mutableListOf<TextLayoutResult>()
            node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            val layout = layouts.single()
            assertEquals(2f, layout.layoutInput.density.fontScale, 0f)
            assertFalse("Selected period must not clip at 200%", layout.hasVisualOverflow)
            assertTrue(layout.getLineBottom(layout.lineCount - 1) <= node.fetchSemanticsNode().boundsInRoot.height)
            node.performClick()
            compose.onNodeWithText("Últimos 15 dias").performClick()
            compose.runOnIdle { assertEquals(listOf(1), selected) }
        } finally {
            RuntimeEnvironment.setFontScale(original)
        }
    }
}
