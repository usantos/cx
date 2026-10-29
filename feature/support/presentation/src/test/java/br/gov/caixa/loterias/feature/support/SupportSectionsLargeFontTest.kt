package br.gov.caixa.loterias.feature.support

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
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
import br.gov.caixa.loterias.feature.support.domain.model.SupportSection
import br.gov.caixa.loterias.feature.support.presentation.screen.FaqContent
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
class SupportSectionsLargeFontTest {
    @get:Rule val compose = createComposeRule()

    @Test fun longSectionTitleIsReadableAndOpensItsOwnSectionOnce() {
        val original = RuntimeEnvironment.getFontScale()
        RuntimeEnvironment.setFontScale(2f)
        try {
            val title = "RESGATE DE PRÊMIOS NAS AGÊNCIAS DA CAIXA"
            val actions = mutableListOf<SupportAction>()
            compose.setContent {
                CaixaDSTheme {
                    Box(Modifier.size(360.dp, 640.dp)) {
                        FaqContent(SupportUiState(sections = listOf(SupportSection(SECTION_ID, title, emptyList()))), { actions += it })
                    }
                }
            }
            val node = compose.onNodeWithText(title).assertIsDisplayed()
            val layouts = mutableListOf<TextLayoutResult>()
            node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            assertEquals(2f, layouts.single().layoutInput.density.fontScale, 0f)
            assertFalse("Complete FAQ section title must fit at 200%", layouts.single().hasVisualOverflow)
            node.performClick()
            compose.runOnIdle { assertEquals(listOf(SupportAction.OpenSection(SECTION_ID, title)), actions) }
        } finally {
            RuntimeEnvironment.setFontScale(original)
        }
    }
}

private const val SECTION_ID = 5L
