package br.gov.caixa.loterias.feature.responsiblegaming

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SocialTransfers
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.SocialTransfersScreen
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SocialTransfersScreenJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun transfersShowAllSocialCategoriesAndKeepTermsAndBettingNavigationSeparate() {
        val state = mutableStateOf(ResponsibleGamingUiState())
        val actions = mutableListOf<ResponsibleGamingAction>()
        var terms = 0
        compose.setContent { CaixaDSTheme { SocialTransfersScreen(state.value, actions::add, { terms++ }) } }
        compose.onNodeWithText("Repasses sociais").assertIsDisplayed()
        compose.onNodeWithText("R$1,00").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("R$0,48*").assertIsDisplayed()
        listOf("Educação", "Cultura", "Esporte", "Segurança", "Seguridade social", "Outros")
            .forEach { compose.onNodeWithText(it).performScrollTo().assertIsDisplayed() }
        compose.onNodeWithContentDescription("Dúvidas").performClick()
        compose.runOnIdle {
            state.value = state.value.copy(
                socialTransfers = SocialTransfers(
                    BigDecimal("1000000"),
                    BigDecimal.ZERO,
                    "2026-10-22",
                    BigDecimal("48"),
                    BigDecimal("10"),
                    BigDecimal("5"),
                    BigDecimal("10"),
                    BigDecimal("5"),
                    BigDecimal("10"),
                    BigDecimal("8")
                )
            )
        }
        compose.onNodeWithText("Educação").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Aposte e ajude ainda mais").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(1, terms)
            assertEquals(listOf(ResponsibleGamingAction.Back, ResponsibleGamingAction.Back), actions)
        }
    }

    @Test
    fun socialTransferLoadingAndErrorStatesPreserveRetry() {
        val state = mutableStateOf(ResponsibleGamingUiState(loading = true))
        val actions = mutableListOf<ResponsibleGamingAction>()
        compose.setContent { CaixaDSTheme { SocialTransfersScreen(state.value, actions::add, {}) } }
        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)).assertIsDisplayed()
        compose.runOnIdle { state.value = ResponsibleGamingUiState(errorMessage = "Repasses indisponíveis") }
        compose.onNodeWithText("Repasses indisponíveis").assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle { assertEquals(listOf(ResponsibleGamingAction.Retry), actions) }
    }
}
