package br.gov.caixa.loterias.feature.results

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
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
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.results.presentation.components.DrawPublicSuperSevenResult
import br.gov.caixa.loterias.feature.results.presentation.components.ResultContestSearchField
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
class ResultsAccessibilityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun superSevenDigitsRetainColumnAssociation() {
        compose.setContent { CaixaDSTheme { DrawPublicSuperSevenResult(List(RESULT_COLUMN_COUNT) { listOf(DRAWN_DIGIT) }, Color.Black) } }
        compose.onNodeWithContentDescription("Coluna 1: 3").assertExists()
        compose.onNodeWithContentDescription("Coluna 7: 3").assertExists()
    }

    @Test fun filledContestInputKeepsItsNameAndEditingAction() {
        compose.setContent { CaixaDSTheme { ResultContestSearchField("123", remember { FocusRequester() }, {}, {}) } }
        compose.onNode(hasSetTextAction() and hasContentDescription("Número do concurso")).assertExists()
    }

    @Test fun superSevenDigitsRemainReadableAtDoubleFontScale() {
        compose.setContent {
            val density = LocalDensity.current.density
            CompositionLocalProvider(LocalDensity provides Density(density, 2f)) {
                CaixaDSTheme { DrawPublicSuperSevenResult(List(RESULT_COLUMN_COUNT) { listOf(DRAWN_DIGIT) }, Color.Black) }
            }
        }
        val digits = compose.onAllNodes(hasText("3"), useUnmergedTree = true).fetchSemanticsNodes()
        assertTrue("Result digits must remain in the text layout tree", digits.isNotEmpty())
        digits.forEach { node ->
            val layouts = mutableListOf<TextLayoutResult>()
            node.config[SemanticsActions.GetTextLayoutResult].action!!.invoke(layouts)
            assertTrue(
                "Expanded result digit is clipped: " +
                    layouts.joinToString { "size=${it.size}; paragraph=${it.multiParagraph.width}x${it.multiParagraph.height}" },
                layouts.all { !it.hasVisualOverflow }
            )
        }
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun nativeGraphicsSuperSevenDigitsRemainReadableAtDoubleFontScale() {
        superSevenDigitsRemainReadableAtDoubleFontScale()
    }

    private companion object {
        const val RESULT_COLUMN_COUNT = 7
        const val DRAWN_DIGIT = 3
    }
}
