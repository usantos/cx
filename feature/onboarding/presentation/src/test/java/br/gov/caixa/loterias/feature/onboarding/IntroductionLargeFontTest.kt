package br.gov.caixa.loterias.feature.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.CompleteIntroductionUseCase
import br.gov.caixa.loterias.feature.onboarding.presentation.components.IntroductionScreen
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.OnboardingViewModel
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
class IntroductionLargeFontTest {
    @get:Rule val compose = createComposeRule()

    @Test fun finalPageActionKeepsItsCompleteLabelAtDoubleFont() = doubleFont {
        show(LAST_PAGE)
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText("Acessar o aplicativo", ignoreCase = true).assertIsDisplayed()
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertFalse("Complete final introduction action must fit at 200%", layouts.single().hasVisualOverflow)
    }

    @Test fun introductionBodyCanScrollToItsCompleteMessageInAShortWindow() = doubleFont {
        show(0)
        val body = compose.onNodeWithText("Todas as apostas na palma da sua mão, o dia inteiro, todos os dias. ;)", ignoreCase = true)
            .performScrollTo().assertIsDisplayed()
        val layouts = mutableListOf<TextLayoutResult>()
        body.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertFalse("Introduction message must keep every glyph at 200%", layouts.single().hasVisualOverflow)
    }

    @Test fun firstPageNamesThePhoneIllustrationInPortuguese() = verifyIllustration(0, "Celular")

    @Test fun secondPageNamesTheStarIllustrationInPortuguese() = verifyIllustration(1, "Estrela")

    @Test fun thirdPageNamesTheSearchIllustrationInPortuguese() = verifyIllustration(2, "Lista com lupa")

    private fun verifyIllustration(page: Int, description: String) = doubleFont {
        show(page)
        compose.onAllNodesWithContentDescription(description).assertCountEquals(1)
        compose.onNodeWithContentDescription(description).performScrollTo().assertIsDisplayed()
    }

    private fun show(page: Int) {
        val model = OnboardingViewModel("onboarding/introduction", CompleteIntroductionUseCase { })
        model.confirmAdult()
        model.pageChanged(page)
        compose.setContent {
            val state by model.state.collectAsState()
            CaixaDSTheme { Box(Modifier.size(360.dp, 640.dp)) { IntroductionScreen(state, model) } }
        }
    }

    private fun doubleFont(block: () -> Unit) {
        val original = RuntimeEnvironment.getFontScale()
        RuntimeEnvironment.setFontScale(2f)
        try {
            block()
        } finally {
            RuntimeEnvironment.setFontScale(original)
        }
    }
}

private const val LAST_PAGE = 2
