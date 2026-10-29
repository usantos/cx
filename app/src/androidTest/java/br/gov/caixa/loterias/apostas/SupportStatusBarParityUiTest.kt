package br.gov.caixa.loterias.apostas

import android.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.core.view.WindowCompat
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSStatusBar
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.support.presentation.screen.SupportScreen
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SupportStatusBarParityUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun questionsKeepNativeWhiteStatusInsetAndDarkSystemIcons() {
        compose.runOnUiThread {
            WindowCompat.setDecorFitsSystemWindows(compose.activity.window, false)
            WindowCompat.getInsetsController(compose.activity.window, compose.activity.window.decorView)
                .isAppearanceLightStatusBars = false
        }
        // Usa a mesma raiz da aplicação em uma janela real, com o estado de suporte controlado.
        // A atualização obrigatória remota tem suas próprias verificações e não pertence a este contrato visual.
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                CaixaDSStatusBar {
                    SupportScreen(SupportUiState(), {})
                }
            }
        }
        compose.onNodeWithTag("support_screen").assertIsDisplayed()
        compose.waitForIdle()
        val screenshot = requireNotNull(InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot())
        try {
            assertEquals("Native support status inset is white", Color.WHITE, screenshot.getPixel(screenshot.width / 2, 10))
            compose.runOnUiThread {
                assertTrue(
                    "Native white status bar uses dark icons",
                    WindowCompat.getInsetsController(compose.activity.window, compose.activity.window.decorView)
                        .isAppearanceLightStatusBars
                )
            }
        } finally {
            screenshot.recycle()
        }
    }
}
