package br.gov.caixa.loterias.apostas

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.feature.betting.domain.repository.ShakeSettingsRepository
import br.gov.caixa.loterias.feature.home.presentation.components.HomeDrawer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeDrawerUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test
    fun campaignsDoNotInsertAnExtraHeaderInTheLegacyDrawer() {
        var campaign by mutableStateOf<CommemorativeCampaign?>(null)
        composeRule.setContent {
            MaterialTheme {
                HomeDrawer(features = HomeFeatureConfiguration(), userName = null,
                    environmentLabel = null, campaign = campaign,
                    onOpen = {}, onLogout = {}, onClose = {}, onExternalFailure = {})
            }
        }
        composeRule.onAllNodesWithText("Outubro Rosa").assertCountEquals(0)
        composeRule.runOnIdle { campaign = CommemorativeCampaign.OUTUBRO_ROSA }
        composeRule.onAllNodesWithText("Outubro Rosa").assertCountEquals(0)
        composeRule.runOnIdle { campaign = CommemorativeCampaign.SETEMBRO_AZUL }
        composeRule.onAllNodesWithText("Setembro Azul").assertCountEquals(0)
        composeRule.onAllNodesWithText("Outubro Rosa").assertCountEquals(0)
        composeRule.runOnIdle { campaign = null }
        composeRule.onAllNodesWithText("Setembro Azul").assertCountEquals(0)
    }

    @Test
    fun shakeInformationCanBeToggledAndLabelStillChangesPreference() {
        val settings = DrawerShakeSettings()
        lateinit var preferences: br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomePreferencesViewModel
        composeRule.runOnUiThread {
            preferences = homePreferencesFixture(object : br.gov.caixa.loterias.feature.home.domain.repository.HomeLayoutRepository {
                override suspend fun loadOrder(): String? = null
                override suspend fun saveOrder(order: String) = Unit
            }, settings)
        }
        composeRule.setContent {
            val preferenceState by preferences.state.collectAsState()
            MaterialTheme {
                HomeDrawer(features = HomeFeatureConfiguration(), userName = null,
                    environmentLabel = null, preferenceState = preferenceState,
                    onToggleShake = { preferences.toggleShake(it) },
                    onConsumeShakeTooltip = { preferences.consumeShakeTooltip() },
                    onOpen = {}, onLogout = {}, onClose = {}, onExternalFailure = {})
            }
        }
        val info = composeRule.onNodeWithContentDescription("Informações sobre Aposta Shake")
        val message = "Agite o celular para preencher\nsua aposta com números aleatórios"
        info.performScrollTo().performClick()
        composeRule.onNodeWithText(message).assertIsDisplayed()
        // Somente a dica automática da primeira visita expira no menu original.
        composeRule.mainClock.advanceTimeBy(6000)
        composeRule.onNodeWithText(message).assertIsDisplayed()
        info.performClick()
        composeRule.onAllNodesWithText(message).assertCountEquals(0)
        info.performClick()
        composeRule.onNodeWithText(message).assertIsDisplayed()
        composeRule.onNodeWithText("Aposta Shake").performClick()
        composeRule.onAllNodesWithText(message).assertCountEquals(0)
        composeRule.runOnIdle { assertEquals(true, kotlinx.coroutines.runBlocking { settings.enabled() }) }
    }

    @Test
    fun drawerShowsLoginLogoAndCloseButtonClosesMenu() {
        var closeCount = 0
        composeRule.setContent {
            MaterialTheme {
                HomeDrawer(
                    features = HomeFeatureConfiguration(),
                    userName = null,
                    environmentLabel = null,
                    onOpen = {}, onLogout = {}, onClose = { closeCount++ }, onExternalFailure = {},
                )
            }
        }
        composeRule.onNodeWithContentDescription("Fechar menu").assertIsDisplayed()
        composeRule.onNodeWithText("Olá, seja bem-vindo ao App Loterias.").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Fechar menu").performClick()
        composeRule.runOnIdle { assertEquals(1, closeCount) }
    }

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

private class DrawerShakeSettings : ShakeSettingsRepository {
    private var value = false
    override suspend fun enabled() = value
    override suspend fun setEnabled(enabled: Boolean) {
        value = enabled
    }
    override suspend fun warningHidden() = true
    override suspend fun setWarningHidden(hidden: Boolean) = Unit
    override suspend fun tutorialCompleted(ownerKey: String?) = true
    override suspend fun completeTutorial(ownerKey: String?) = Unit
    override suspend fun tooltipPending(ownerKey: String?) = false
    override suspend fun consumeTooltip(ownerKey: String?) = Unit
}
