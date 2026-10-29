package br.gov.caixa.loterias.feature.responsiblegaming

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentQuestion
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.AssessmentFormScreen
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.AssessmentStartScreen
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState
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
class ResponsibleGamingLargeFontTest {
    @get:Rule val compose = createComposeRule()

    @Test fun introductionRespectsSystemScaleAndRetainsReadableText() = atDoubleFont {
        compose.setContent { CaixaDSTheme { Box(Modifier.size(360.dp, 640.dp)) { AssessmentStartScreen(ResponsibleGamingUiState(), {}) } } }
        val welcome = compose.onNode(hasText("Bem-vindo", substring = true)).performScrollTo().assertIsDisplayed()
        val layouts = mutableListOf<TextLayoutResult>()
        welcome.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(
            "Welcome must retain its sp size instead of cancelling the OS scale",
            TITLE_SIZE,
            layouts.single().layoutInput.style.fontSize.value,
            0f
        )
        assertFalse(layouts.single().hasVisualOverflow)
        val body = compose.onNode(hasText("A partir de agora", substring = true)).performScrollTo()
        val bodyLayouts = mutableListOf<TextLayoutResult>()
        body.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(bodyLayouts) }
        assertEquals(BODY_SIZE, bodyLayouts.single().layoutInput.style.fontSize.value, 0f)
        assertFalse(bodyLayouts.single().hasVisualOverflow)
    }

    @Test fun closeActionAndNavigationLabelsAreReadableAtDoubleFont() = atDoubleFont {
        val actions = mutableListOf<ResponsibleGamingAction>()
        val state =
            ResponsibleGamingUiState(
                questions = listOf(SelfAssessmentQuestion(1, "Pergunta de teste", 1), SelfAssessmentQuestion(2, "Segunda pergunta", 2))
            )
        compose.setContent { CaixaDSTheme { Box(Modifier.size(360.dp, 640.dp)) { AssessmentFormScreen(state, { actions += it }) } } }
        val close = compose.onNodeWithText("Fechar").performScrollTo().assertIsDisplayed()
        listOf("Fechar", "Anterior", "Próximo").forEach { label ->
            val layouts = mutableListOf<TextLayoutResult>()
            compose.onNodeWithText(label).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            assertFalse("Complete label must fit: $label", layouts.single().hasVisualOverflow)
        }
        close.performClick()
        compose.runOnIdle { assertEquals(listOf(ResponsibleGamingAction.Back), actions) }
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

private const val TITLE_SIZE = 18f
private const val BODY_SIZE = 15f
