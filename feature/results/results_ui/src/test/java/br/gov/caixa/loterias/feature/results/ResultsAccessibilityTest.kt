package br.gov.caixa.loterias.feature.results

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.results.presentation.components.DrawPublicSuperSevenResult
import br.gov.caixa.loterias.feature.results.presentation.components.ResultContestSearchField
import org.junit.Assert.assertTrue
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
class ResultsAccessibilityTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun superSevenDigitsRetainColumnAssociation() {
        compose.setFeatureContent {
            LoteriasCaixaDSTheme { DrawPublicSuperSevenResult(List(RESULT_COLUMN_COUNT) { listOf(DRAWN_DIGIT) }, Color.Black) }
        }
        compose.onNodeWithContentDescription("Coluna 1: 3").assertExists()
        compose.onNodeWithContentDescription("Coluna 7: 3").assertExists()
    }

    @Test fun filledContestInputKeepsItsNameAndEditingAction() {
        compose.setFeatureContent { LoteriasCaixaDSTheme { ResultContestSearchField("123", remember { FocusRequester() }, {}, {}) } }
        compose.onNode(hasSetTextAction() and hasContentDescription("Número do concurso")).assertExists()
    }

    @Test fun superSevenDigitsRemainReadableAtDoubleFontScale() {
        compose.setFeatureContent {
            val density = LocalDensity.current.density
            CompositionLocalProvider(LocalDensity provides Density(density, 2f)) {
                LoteriasCaixaDSTheme { DrawPublicSuperSevenResult(List(RESULT_COLUMN_COUNT) { listOf(DRAWN_DIGIT) }, Color.Black) }
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
