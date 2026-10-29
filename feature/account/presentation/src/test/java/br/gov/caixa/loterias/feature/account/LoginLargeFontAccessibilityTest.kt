package br.gov.caixa.loterias.feature.account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.presentation.screen.LoginScreen
import br.gov.caixa.loterias.feature.account.presentation.state.LoginUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
class LoginLargeFontAccessibilityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun loginLabelIsFullyReadableAndInvokesOneActionAtDoubleFont() = atDoubleFont {
        var calls = 0
        showLogin(onLogin = { calls++ })
        assertReadable("Faça login/cadastre-se")
        compose.onNodeWithText("Faça login/cadastre-se").performClick()
        compose.runOnIdle { assertEquals(1, calls) }
    }

    @Test fun biometricLabelAndVersionAreReachableWithoutOverlappingPrimaryActions() = atDoubleFont {
        var calls = 0
        showLogin(onBiometricChanged = { calls++ })
        assertReadable("Acessar com biometria")
        compose.onNodeWithText("Acessar com biometria").performClick()
        compose.runOnIdle { assertEquals(1, calls) }
        reachable("v. 1.25.0").assertIsDisplayed()
        val version = compose.onNodeWithText("v. 1.25.0").getUnclippedBoundsInRoot()
        val biometric = compose.onNodeWithText("Acessar com biometria").getUnclippedBoundsInRoot()
        assertTrue("Version follows biometric choice", version.top >= biometric.bottom)
    }

    @Test fun rememberedLoginOffersIndependentAccountSwitchAtDoubleFont() = atDoubleFont {
        var logins = 0
        var switches = 0
        showLogin(
            onLogin = { logins++ },
            state = LoginUiState(rememberedFirstName = "João", rememberedLoginAvailable = true),
            onSwitchUser = { switches++ }
        )
        assertReadable("Entrar como João")
        compose.onNodeWithText("Entrar como João").performClick()
        assertReadable("Trocar usuário")
        compose.onNodeWithText("Trocar usuário").performClick()
        compose.runOnIdle {
            assertEquals(1, logins)
            assertEquals(1, switches)
        }
        compose.onNodeWithText("Faça login/cadastre-se").assertDoesNotExist()
    }

    private fun showLogin(
        onLogin: () -> Unit = {},
        onBiometricChanged: (Boolean) -> Unit = {},
        state: LoginUiState = LoginUiState(biometricRowVisible = true),
        onSwitchUser: () -> Unit = {},
    ) {
        compose.setContent {
            CaixaDSTheme {
                Box(Modifier.size(width = 360.dp, height = 640.dp)) {
                    LoginScreen(
                        state = state,
                        versionName = "1.25.0",
                        onTryNow = {},
                        onLogin = onLogin,
                        onBiometricChanged = onBiometricChanged,
                        onSwitchUser = onSwitchUser
                    )
                }
            }
        }
    }

    private fun assertReadable(text: String) {
        val node = reachable(text).assertIsDisplayed()
        val layouts = mutableListOf<TextLayoutResult>()
        node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val layout = layouts.single()
        assertEquals(2f, layout.layoutInput.density.fontScale, 0f)
        assertFalse("Full text must fit at system font 200%: $text", layout.hasVisualOverflow)
        assertTrue(
            "Text lines must remain inside visible bounds: $text",
            layout.getLineBottom(layout.lineCount - 1) <= node.fetchSemanticsNode().boundsInRoot.height
        )
    }

    private fun reachable(text: String): androidx.compose.ui.test.SemanticsNodeInteraction {
        val node = compose.onNodeWithText(text)
        val ancestors = generateSequence(node.fetchSemanticsNode().parent) { it.parent }
        if (ancestors.any { it.config.contains(SemanticsActions.ScrollBy) }) node.performScrollTo()
        return node
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
