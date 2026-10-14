package br.gov.caixa.loterias.apostas

import androidx.compose.ui.unit.dp
import androidx.compose.runtime.DisposableEffect
import kotlinx.coroutines.awaitCancellation
import org.junit.Assert.assertEquals
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.LoginScreen
import br.gov.caixa.loterias.feature.account.LoginUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginEnvironmentPickerUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun environmentTouchTargetDoesNotOverlapBiometricSwitch() {
        var biometricChanged = false
        compose.setContent {
            CaixaDSTheme {
                LoginScreen(LoginUiState(), "test", {}, {}, { biometricChanged = it }, environmentSelectorVisible = true, environmentSelector = { enabled ->
                    LoginEnvironmentPicker(LegacyTestEnvironment.EXTERNO_ESTEIRA, enabled, { true }, {})
                })
            }
        }
        compose.onNodeWithTag("environment-picker").assertHeightIsAtLeast(48.dp)
        val environment = compose.onNodeWithTag("environment-picker").fetchSemanticsNode().boundsInRoot
        val biometric = compose.onNode(hasClickAction() and hasText("Acessar com biometria")).fetchSemanticsNode().boundsInRoot
        assertTrue("Environment touch target $environment overlaps biometric switch $biometric", environment.top >= biometric.bottom)
        val version = compose.onNodeWithText("v. test").fetchSemanticsNode().boundsInRoot
        assertTrue("Picker must also remain above the version text", environment.bottom <= version.top)
        compose.onNode(hasClickAction() and hasText("Acessar com biometria")).performClick()
        compose.runOnIdle { assertTrue(biometricChanged) }
        compose.onNodeWithText("PILOTO").assertDoesNotExist()
        compose.onNodeWithTag("environment-picker").performClick()
        compose.onNodeWithText("PILOTO").assertIsDisplayed()
    }
    @Test fun restoredCommittedChoiceDoesNotLeaveSelectorDisabled() {
        val active = mutableStateOf(LegacyTestEnvironment.EXTERNO_ESTEIRA)
        val restoration = StateRestorationTester(compose)
        var changes = 0
        var restoreCommittedSelection = false
        restoration.setContent {
            DisposableEffect(Unit) {
                onDispose {
                    if (restoreCommittedSelection) active.value = LegacyTestEnvironment.PILOTO
                }
            }
            CaixaDSTheme {
                LoginEnvironmentPicker(active.value, true, { requested ->
                    changes++
                    // Durable commit happened, but process died before its completion flag was saved.
                    if (requested == active.value) false else awaitCancellation()
                }, {})
            }
        }
        compose.onNodeWithTag("environment-picker").performClick()
        compose.onNodeWithText("PILOTO").performClick()
        compose.runOnIdle { restoreCommittedSelection = true; assertEquals(1, changes) }
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithTag("environment-picker").assertIsEnabled().performClick()
        compose.onNodeWithText("INTERNO").assertIsDisplayed()
        compose.onNodeWithText("Mudança de Ambiente").assertDoesNotExist()
        compose.runOnIdle { assertEquals("Do not reapply an already active environment", 1, changes) }
    }

}
