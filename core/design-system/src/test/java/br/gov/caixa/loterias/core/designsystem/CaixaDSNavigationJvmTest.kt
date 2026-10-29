package br.gov.caixa.loterias.core.designsystem

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.view.WindowCompat
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSHomeTopBar
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSModalityTopBar
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSStatusBar
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSStatusBarColor
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSNavigationJvmTest {
    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun homeActionsReflectUnreadAndExpansionStateAndCanBeHidden() {
        val unread = mutableStateOf(2)
        val expanded = mutableStateOf(false)
        val enabled = mutableStateOf(true)
        val visible = mutableStateOf(true)
        val calls = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSHomeTopBar(
                    unread.value, false, expanded.value, 0f, { calls.add("menu") }, { calls.add("notifications") }, {},
                    {
                        calls.add("quick")
                        expanded.value = !expanded.value
                    }, quickAccessToggleEnabled = enabled.value, actionsVisible = visible.value
                )
            }
        }
        compose.onNodeWithContentDescription("Notificações não lidas", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithContentDescription("Abrir menu").performClick()
        compose.onNodeWithContentDescription("Notificações").performClick()
        compose.onNodeWithContentDescription("Apresentar atalhos").performClick()
        compose.onNodeWithContentDescription("Ocultar atalhos").assertIsDisplayed()
        compose.runOnIdle {
            unread.value = 0
            enabled.value = false
        }
        compose.onNodeWithContentDescription("Notificações não lidas", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithContentDescription("Ocultar atalhos").assertIsNotEnabled()
        compose.runOnIdle {
            assertEquals(listOf("menu", "notifications", "quick"), calls)
            visible.value = false
        }
        compose.onNodeWithContentDescription("Abrir menu").assertDoesNotExist()
        compose.onNodeWithContentDescription("Notificações").assertDoesNotExist()
    }

    @Test
    fun modalityHeaderExposesContestAndDrawMetadataAndNavigationActions() {
        val artwork = mutableStateOf<Int?>(null)
        val calls = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSModalityTopBar(
                    "Mega-Sena", Color.Green, Color.White, "1234", "Sorteio", "22/10", { calls.add("back") },
                    { calls.add("help") }, backgroundArtwork = artwork.value, titleScale = .9f
                )
            }
        }
        val title = compose.onNodeWithText("Mega-Sena").assertIsDisplayed().fetchSemanticsNode()
        assertTrue(title.config.contains(SemanticsProperties.Heading))
        compose.onNodeWithContentDescription("Concurso 1234").assertIsDisplayed()
        compose.onNodeWithContentDescription("Sorteio 22/10").assertIsDisplayed()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.onNodeWithContentDescription("Dúvidas").performClick()
        compose.runOnIdle {
            assertEquals(listOf("back", "help"), calls)
            artwork.value = R.drawable.legacy_marketplace_filter_bet
        }
        compose.onNodeWithText("Mega-Sena").assertIsDisplayed()
    }

    @Test
    fun statusBarUsesLastActiveRequestAndRestoresPreviousScreenWhenOverlayLeaves() {
        val overlay = mutableStateOf(false)
        val underlying = mutableStateOf(Color.White)
        compose.setContent {
            CaixaDSStatusBar {
                CaixaDSStatusBarColor(underlying.value)
                if (overlay.value) CaixaDSStatusBarColor(Color.Black)
            }
        }
        val activity = compose.activity
        val controller = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
        compose.runOnIdle {
            assertTrue(controller.isAppearanceLightStatusBars)
            assertTrue(controller.isAppearanceLightNavigationBars)
            overlay.value =
                true
        }
        compose.runOnIdle {
            assertFalse(controller.isAppearanceLightStatusBars)
            overlay.value = false
        }
        compose.runOnIdle {
            assertTrue(controller.isAppearanceLightStatusBars)
            underlying.value = Color.Black
        }
        compose.runOnIdle {
            assertFalse(controller.isAppearanceLightStatusBars)
            assertFalse(activity.window.isNavigationBarContrastEnforced)
        }
    }

    @Test
    fun standaloneStatusRequestRestoresTheHostsIconPreferenceOnDisposal() {
        val present = mutableStateOf(true)
        val activity = compose.activity
        val controller = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
        controller.isAppearanceLightStatusBars = false
        compose.setContent { if (present.value) CaixaDSStatusBarColor(Color.Black, darkIcons = true) }
        compose.runOnIdle {
            assertTrue(controller.isAppearanceLightStatusBars)
            present.value = false
        }
        compose.runOnIdle { assertFalse(controller.isAppearanceLightStatusBars) }
    }
}
