package br.gov.caixa.loterias.feature.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.CompleteIntroductionUseCase
import br.gov.caixa.loterias.feature.onboarding.presentation.components.IntroductionScreen
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.OnboardingViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IntroductionFooterParityUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun finalActionWrapsWithoutClippingAtCompactWidthsAndCompletesOnce() {
        var completions = 0
        val width = mutableStateOf(360.dp)
        val model = OnboardingViewModel("onboarding/introduction", CompleteIntroductionUseCase { completions++ })
        model.confirmAdult()
        model.pageChanged(2)
        compose.setContent {
            CaixaDSTheme {
                val state = model.state.collectAsStateWithLifecycle()
                Box(Modifier.width(width.value).fillMaxHeight().testTag("intro-viewport")) {
                    IntroductionScreen(state.value, model)
                }
            }
        }
        for (size in listOf(360.dp, 320.dp)) {
            compose.runOnIdle { width.value = size }
            val layouts = mutableListOf<TextLayoutResult>()
            compose.onNodeWithText("Acessar o aplicativo", useUnmergedTree = true)
                .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            val layout = layouts.single()
            assertFalse("Final action must fit completely at $size", layout.hasVisualOverflow)
            assertEquals("Native 140dp button wraps the final action", 2, layout.lineCount)
            val indicators = compose.onNodeWithTag("introduction-page-indicators").getUnclippedBoundsInRoot()
            val viewport = compose.onNodeWithTag("intro-viewport").getUnclippedBoundsInRoot()
            assertEquals("Indicators stay centered independently of the action",
                (viewport.left + viewport.right).value / 2f,
                (indicators.left + indicators.right).value / 2f, 0.5f)
        }
        compose.onNodeWithText("Acessar o aplicativo").performClick()
        compose.runOnIdle { assertEquals(1, completions) }
    }
}
