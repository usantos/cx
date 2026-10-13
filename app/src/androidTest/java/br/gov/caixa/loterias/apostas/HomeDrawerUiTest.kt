package br.gov.caixa.loterias.apostas

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.gov.caixa.loterias.core.domain.configuration.HomeFeatureConfiguration
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeDrawerUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test
    fun drawerExpansionNavigationAndConfirmationCancellationMatchLegacyBehavior() {
        val opened = mutableListOf<String>()
        var logoutCount = 0
        var externalFailureCount = 0
        composeRule.setContent {
            MaterialTheme {
                HomeDrawer(
                    features = HomeFeatureConfiguration(myBetsVisible = true, rapidBettingEnabled = true),
                    userName = "Maria da Silva Santos",
                    environmentLabel = "TQS",
                    onOpen = opened::add,
                    onLogout = { logoutCount++ },
                    onClose = {},
                    onExternalFailure = { externalFailureCount++ },
                )
            }
        }

        composeRule.onNodeWithText("Minha área").performClick()
        composeRule.onNodeWithText("Meus cartões").assertIsDisplayed()
        composeRule.onNodeWithText("Jogo responsável").performScrollTo().performClick()
        composeRule.onAllNodesWithText("Meus cartões").assertCountEquals(1)
        composeRule.onNodeWithText("Política de jogo responsável").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Política de jogo responsável").performScrollTo().performClick()
        composeRule.onNodeWithText("Você será redirecionado para o portal do Jogo Responsável").assertIsDisplayed()
        composeRule.onNodeWithText("Cancelar").performClick()
        composeRule.onAllNodesWithText("Você será redirecionado para o portal do Jogo Responsável").assertCountEquals(0)

        composeRule.onNodeWithText("Jogo responsável").performScrollTo().performClick()
        composeRule.onNodeWithText("Meus cartões").performScrollTo().performClick()
        composeRule.runOnIdle { assertEquals(listOf("payment/cards"), opened) }

        composeRule.onNodeWithText("Sair").performClick()
        composeRule.onNodeWithText("Tem certeza que deseja sair?").assertIsDisplayed()
        composeRule.onNodeWithText("Não").performClick()
        composeRule.onAllNodesWithText("Tem certeza que deseja sair?").assertCountEquals(0)
        composeRule.onNodeWithText("TQS").assertIsDisplayed()
        composeRule.runOnIdle {
            assertEquals(0, logoutCount)
            assertEquals(0, externalFailureCount)
        }
    }
}
