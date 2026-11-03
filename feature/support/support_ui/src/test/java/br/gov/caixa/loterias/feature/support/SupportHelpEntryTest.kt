package br.gov.caixa.loterias.feature.support

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.support.presentation.state.SupportMode
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import br.gov.caixa.loterias.feature.support.ui.SupportUiEntryImpl
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@RunWith(DynamicFeatureRobolectricTestRunner::class)
@ResourcesMode(ResourcesMode.Mode.NATIVE)
@Config(sdk = [36], qualifiers = "w411dp-h891dp-xhdpi", application = android.app.Application::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SupportHelpEntryTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun gameHelpEntryRendersLoadedMetadataRulesAndCurrentBackAction() {
        val state = mutableStateOf(
            SupportUiState(
                mode = SupportMode.GAME_HELP,
                gameHelpModalityId = 16,
                gameHelpModalityName = "Lotomania",
                gameHelpContestNumber = 123,
                gameHelpDrawDate = "2026-10-18",
                gameHelpClosingDateTime = "2026-10-18T23:30:00Z",
                htmlContent = "<b>Regra sintética</b>",
                isLoading = true
            )
        )
        var backs = 0
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                SupportUiEntryImpl().GameHelp(state.value, { backs++ }, Modifier)
            }
        }
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        compose.runOnIdle { state.value = state.value.copy(isLoading = false) }
        compose.onNodeWithTag("support_game_help").assertExists()
        compose.onNodeWithTag("support_screen").assertDoesNotExist()
        compose.onNodeWithText("Nº 123").assertIsDisplayed()
        compose.onNodeWithText("18/10").assertIsDisplayed()
        compose.onNodeWithText("18/10 | 20h30").assertIsDisplayed()
        compose.onNodeWithText("Regra sintética").assertExists()
        compose.onNodeWithContentDescription("Fechar").performClick()
        compose.runOnIdle { assertEquals(1, backs) }
    }
}
