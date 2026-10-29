package br.gov.caixa.loterias.feature.home

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.home.presentation.components.ShakeMenuRow
import br.gov.caixa.loterias.feature.home.presentation.state.HomePreferencesUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [SHAKE_TEST_SDK], qualifiers = "w480dp-h900dp")
class ShakeConfirmationBehaviorTest {
    @get:Rule val compose = createComposeRule()

    @Test fun activationRequiresConsentAndCancellingDeactivationKeepsTheSwitchOn() {
        val settings = mutableStateOf(HomePreferencesUiState(loaded = true))
        val changes = mutableListOf<Boolean?>()
        compose.setContent {
            CaixaDSTheme {
                ShakeMenuRow(
                    settings.value,
                    onToggle = { hideWarning ->
                        changes.add(hideWarning)
                        settings.value = settings.value.copy(shakeEnabled = !settings.value.shakeEnabled)
                    },
                    onConsumeTooltip = {},
                    active = true
                )
            }
        }

        compose.onNodeWithText("Aposta Shake").performClick()
        compose.onNodeWithText("Ativar Aposta Shake").assertIsDisplayed()
        compose.onNodeWithText("Deseja ativar a Aposta Shake nas modalidades de loterias?").assertIsDisplayed()
        compose.runOnIdle { assertEquals(emptyList<Boolean?>(), changes) }
        compose.onNodeWithText("Não exibir o aviso novamente").performClick()
        compose.onNode(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox)).assertIsOn()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithContentDescription("Aposta Shake").assertIsOn().performClick()
        compose.onNodeWithText("Desativar Aposta Shake").assertIsDisplayed()
        compose.onNode(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox)).assertIsOff()
        compose.onNodeWithText("Não").performClick()
        compose.onNodeWithContentDescription("Aposta Shake").assertIsOn()
        compose.runOnIdle { assertEquals(listOf(true), changes) }

        compose.onNodeWithContentDescription("Aposta Shake").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithContentDescription("Aposta Shake").assertIsOff()
        compose.runOnIdle { assertEquals(listOf(true, false), changes) }
    }

    @Test fun savedWarningPreferenceAllowsDirectToggleWithoutAConfirmation() {
        val settings = mutableStateOf(HomePreferencesUiState(loaded = true, shakeEnabled = true, shakeWarningHidden = true))
        val changes = mutableListOf<Boolean?>()
        compose.setContent {
            CaixaDSTheme {
                ShakeMenuRow(
                    settings.value,
                    onToggle = { hideWarning ->
                        changes.add(hideWarning)
                        settings.value = settings.value.copy(shakeEnabled = false)
                    },
                    onConsumeTooltip = {},
                    active = true
                )
            }
        }
        compose.onNodeWithContentDescription("Aposta Shake").assertIsOn().performClick()
        compose.onNodeWithContentDescription("Aposta Shake").assertIsOff()
        compose.onNodeWithText("Desativar Aposta Shake").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf<Boolean?>(null), changes) }
    }
}

private const val SHAKE_TEST_SDK = 35
