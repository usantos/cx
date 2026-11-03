package br.gov.caixa.loterias.apostas

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.core.view.WindowCompat
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSStatusBar
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSStatusBarColor
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class StatusBarTransitionParityUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun incomingWhiteScreenKeepsDarkIconsWhenOutgoingBlueScreenDisposes() {
        val outgoing = mutableStateOf(true)
        val incoming = mutableStateOf(false)
        compose.setFeatureContent {
            LoteriasCaixaDSStatusBar {
                if (outgoing.value) LoteriasCaixaDSStatusBarColor(LoteriasCaixaDSColorTokens.Blue700, darkIcons = false)
                if (incoming.value) LoteriasCaixaDSStatusBarColor(LoteriasCaixaDSColorTokens.White, darkIcons = true)
            }
        }
        compose.runOnIdle { incoming.value = true }
        compose.runOnIdle {
            assertTrue(controller().isAppearanceLightStatusBars)
            outgoing.value = false
        }
        compose.runOnIdle { assertTrue("Outgoing route must not overwrite the active screen", controller().isAppearanceLightStatusBars) }
    }

    private fun controller() = WindowCompat.getInsetsController(compose.activity.window, compose.activity.window.decorView)
}
