package br.gov.caixa.loterias.feature.responsiblegaming

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.AssessmentStartScreen
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class AssessmentStartLayoutTest {
    @get:Rule val compose = createComposeRule()

    @Test fun introductionButtonsRemainReadableAndKeepTheirActions() {
        checkIntroduction(fontScale = 1f, required = false)
    }

    @Test fun largeFontsDoNotClipButtonsOrHideTheIntroduction() {
        checkIntroduction(fontScale = 1.3f, required = false)
    }

    @Test fun mandatoryAssessmentDoesNotOfferClose() {
        checkIntroduction(fontScale = 1.3f, required = true)
    }

    private fun checkIntroduction(fontScale: Float, required: Boolean) {
        val actions = mutableListOf<ResponsibleGamingAction>()
        compose.setContent {
            val density = LocalDensity.current.density
            CompositionLocalProvider(LocalDensity provides Density(density, fontScale)) {
                CaixaDSTheme {
                    Box(Modifier.requiredSize(360.dp, 736.dp)) {
                        AssessmentStartScreen(ResponsibleGamingUiState(assessmentRequired = required), actions::add)
                    }
                }
            }
        }
        val startLabel = "Seguir para autoavaliação"
        val results = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(startLabel, useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        assertFalse(results.single().hasVisualOverflow)
        compose.onNodeWithText(startLabel).performClick()
        assertEquals(listOf(ResponsibleGamingAction.Start), actions)
        if (required) {
            compose.onNodeWithText("Fechar").assertDoesNotExist()
        } else {
            compose.onNodeWithText("Fechar").performClick()
            assertEquals(ResponsibleGamingAction.Back, actions.last())
        }
        compose.onNodeWithText("A partir de agora", substring = true).performScrollTo()
    }
}
