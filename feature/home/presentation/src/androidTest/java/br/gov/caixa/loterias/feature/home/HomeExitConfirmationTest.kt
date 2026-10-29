package br.gov.caixa.loterias.feature.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.home.presentation.route.HomeExitConfirmation
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeExitConfirmationTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun deviceBackRequiresExplicitConfirmationToExit() {
        var exits = 0
        compose.setContent { CaixaDSTheme { HomeExitConfirmation { exits++ } } }
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("Deseja sair do aplicativo?").assertIsDisplayed()
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithText("Deseja sair do aplicativo?").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(0, exits)
            compose.activity.onBackPressedDispatcher.onBackPressed()
        }
        compose.onNodeWithText("Sair").performClick()
        compose.runOnIdle { assertEquals(1, exits) }
    }
}
