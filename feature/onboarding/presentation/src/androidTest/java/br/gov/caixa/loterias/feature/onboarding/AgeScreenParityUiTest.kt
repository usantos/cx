package br.gov.caixa.loterias.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.*
import br.gov.caixa.loterias.feature.onboarding.navigation.OnboardingNavigationDependencies
import br.gov.caixa.loterias.feature.onboarding.navigation.onboardingGraph
import br.gov.caixa.loterias.feature.onboarding.presentation.components.AgeScreen
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AgeScreenParityUiTest {
    @get:Rule val compose = createComposeRule()
    private val message = "O uso do aplicativo é restrito a maiores de 18 anos."
    private val link = "Saiba mais sobre o Jogo Responsável."

    @Test fun entendiRestartsIntroductionWithAgeQuestionAfterEveryRejection() {
        lateinit var nav: androidx.navigation.NavHostController
        var completions = 0
        val dependencies = object : OnboardingNavigationDependencies {
            override val completeIntroduction = CompleteIntroductionUseCase { completions++ }
            override val setBettingTutorialDismissed = SetBettingTutorialDismissedUseCase { _, _ -> }
            override val shouldShowIntroduction = ShouldShowIntroductionUseCase { true }
            @Composable override fun StartupStatusDestination(route: String, onRecovered: () -> Unit) {}
        }
        compose.setContent {
            CaixaDSTheme {
                nav = rememberNavController()
                NavHost(nav, startDestination = "onboarding/introduction") { onboardingGraph(dependencies, nav) }
            }
        }
        repeat(2) {
            compose.onNodeWithText("Você tem mais de 18 anos?").assertIsDisplayed()
            compose.onNodeWithText("Não").performClick()
            compose.onNodeWithText(message).assertIsDisplayed()
            compose.onNodeWithText("Entendi").performClick()
            compose.onNodeWithText("Você tem mais de 18 anos?").assertIsDisplayed()
            compose.onNodeWithText("Sim").assertIsDisplayed()
            compose.onNodeWithText("Não").assertIsDisplayed()
            compose.runOnIdle {
                assertEquals("onboarding/introduction", nav.currentDestination?.route)
                assertNull("Do not accumulate old introduction destinations", nav.previousBackStackEntry)
                assertEquals(0, completions)
            }
        }
    }

    @Test fun agePhraseIsBoldAndButtonUsesSemiBold() {
        compose.setContent { CaixaDSTheme { AgeScreen({}, {}) } }
        val layout = textLayout(message)
        val spans = layout.layoutInput.text.spanStyles
        assertTrue("Age must use the legacy bold face", spans.any {
            it.start == message.indexOf("18 anos.") && it.end == message.length && it.item.fontWeight == FontWeight.Bold
        })
        assertEquals(FontWeight.SemiBold, textLayout("Entendi").layoutInput.style.fontWeight)
    }

    @Test fun spacingFollowsLegacyGuidelineAndMargins() {
        compose.setContent { CaixaDSTheme { AgeScreen({}, {}) } }
        val icon = compose.onNodeWithContentDescription("Apostar é para maiores de 18 anos").getUnclippedBoundsInRoot()
        val text = compose.onNodeWithText(message).getUnclippedBoundsInRoot()
        val learnMore = compose.onNodeWithText(link).getUnclippedBoundsInRoot()
        assertEquals(100f, (icon.bottom - icon.top).value, 0.5f)
        assertEquals(49f, (text.top - icon.bottom).value, 0.5f)
        assertEquals(54f, (text.bottom - text.top).value, 0.5f)
        assertEquals(28f, (learnMore.top - text.bottom).value, 0.5f)
        compose.onNodeWithText("Entendi").assertIsDisplayed()
    }

    @Test fun responsibleGamingOpensCaixaWebsiteAndKeepsAgeDestination() {
        val urls = mutableListOf<String>()
        val handler = object : UriHandler { override fun openUri(uri: String) { urls += uri } }
        lateinit var nav: androidx.navigation.NavHostController
        val dependencies = object : OnboardingNavigationDependencies {
            override val completeIntroduction = CompleteIntroductionUseCase {}
            override val setBettingTutorialDismissed = SetBettingTutorialDismissedUseCase { _, _ -> }
            override val shouldShowIntroduction = ShouldShowIntroductionUseCase { true }
            @Composable override fun StartupStatusDestination(route: String, onRecovered: () -> Unit) {}
        }
        compose.setContent {
            CaixaDSTheme {
                CompositionLocalProvider(LocalUriHandler provides handler) {
                    nav = rememberNavController()
                    NavHost(nav, startDestination = "onboarding/age") { onboardingGraph(dependencies, nav) }
                }
            }
        }
        compose.onNodeWithText(link).performClick()
        compose.runOnIdle {
            assertEquals(listOf("https://www.caixa.gov.br/jogo-responsavel/Paginas/default.aspx"), urls)
            assertEquals("onboarding/age", nav.currentDestination?.route)
        }
    }

    private fun textLayout(text: String): TextLayoutResult {
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(text, useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        return layouts.single()
    }
}
