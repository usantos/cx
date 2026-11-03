package br.gov.caixa.loterias.core.presentation.delivery

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.delivery.ModuleDeliveryState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [MODULE_STATE_SCREEN_TEST_SDK], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
class FeatureModuleStateScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test fun preparingAndDisabledAllowBackAndOnlyFailureOffersAnExplicitRetry() {
        val state = mutableStateOf<ModuleDeliveryState>(ModuleDeliveryState.Checking)
        var backs = 0
        var retries = 0
        compose.setContent {
            LoteriasCaixaDSTheme {
                FeatureModuleStateScreen(AppModule.CART, state.value, { backs++ }, { retries++ })
            }
        }
        listOf(
            ModuleDeliveryState.Checking,
            ModuleDeliveryState.Pending,
            ModuleDeliveryState.Downloading(DOWNLOADED_BYTES, TOTAL_BYTES)
        ).forEach { next ->
            compose.runOnIdle { state.value = next }
            compose.onNodeWithTag("simlo.modules.0001").assertIsDisplayed()
            compose.onNodeWithText("Preparando o conteúdo. Aguarde.").assertIsDisplayed()
            compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
            compose.onNodeWithText("Voltar").performClick()
        }
        compose.runOnIdle {
            assertEquals(PREPARING_STATES, backs)
            assertEquals(0, retries)
            state.value = ModuleDeliveryState.Failed
        }
        compose.onNodeWithText("Não foi possível carregar o conteúdo. Tente novamente.").assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle {
            assertEquals(1, retries)
            assertEquals(PREPARING_STATES, backs)
            state.value = ModuleDeliveryState.Disabled
        }
        compose.onNodeWithText("Este conteúdo não está disponível no momento.").assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
        compose.onNodeWithText("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(PREPARING_STATES + 1, backs)
            assertEquals(1, retries)
        }
    }

    @Test fun everyKnownModuleShowsItsUserFacingTitleWhileUnavailable() {
        val module = mutableStateOf(AppModule.ACCOUNT)
        compose.setContent {
            LoteriasCaixaDSTheme {
                FeatureModuleStateScreen(module.value, ModuleDeliveryState.Disabled, {}, {})
            }
        }
        listOf(
            AppModule.ACCOUNT to "Minha conta",
            AppModule.HOME to "Início",
            AppModule.ONBOARDING to "Apresentação",
            AppModule.BETTING to "Apostas",
            AppModule.MARKETPLACE to "Bolões",
            AppModule.MARKETPLACE_FILTER to "Filtros de bolões",
            AppModule.CART to "Carrinho",
            AppModule.PAYMENT to "Pagamento",
            AppModule.TICKETS to "Apostas e compras",
            AppModule.RESULTS to "Resultados",
            AppModule.NOTIFICATIONS to "Mensagens",
            AppModule.RESPONSIBLE_GAMING to "Jogo responsável",
            AppModule.RETAIL to "Lotéricas e resgates",
            AppModule.SUPPORT_HELP to "Ajuda",
            AppModule.SUPPORT_FAQ to "Dúvidas"
        ).forEach { (next, title) ->
            compose.runOnIdle { module.value = next }
            compose.onAllNodesWithText(title).onFirst().assertIsDisplayed()
            compose.onNodeWithText("Este conteúdo não está disponível no momento.").assertIsDisplayed()
        }
    }
}

private const val MODULE_STATE_SCREEN_TEST_SDK = 35
private const val DOWNLOADED_BYTES = 10L
private const val TOTAL_BYTES = 100L
private const val PREPARING_STATES = 3
