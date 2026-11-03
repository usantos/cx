package br.gov.caixa.loterias.feature.support

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.configuration.model.AppModulesConfiguration
import br.gov.caixa.loterias.core.domain.configuration.repository.AppModulesConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.support.domain.delivery.SupportDeliveryState
import br.gov.caixa.loterias.feature.support.domain.delivery.SupportModuleDelivery
import br.gov.caixa.loterias.feature.support.navigation.SupportModuleNavigationDependencies
import br.gov.caixa.loterias.feature.support.presentation.dynamic.SupportFaqEntry
import br.gov.caixa.loterias.feature.support.presentation.dynamic.SupportFaqEntryLoader
import br.gov.caixa.loterias.feature.support.presentation.dynamic.SupportModuleGate
import br.gov.caixa.loterias.feature.support.presentation.dynamic.SupportModuleStateScreen
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@RunWith(DynamicFeatureRobolectricTestRunner::class)
@ResourcesMode(ResourcesMode.Mode.NATIVE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
class SupportModuleStateScreenTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun disabledShowsEmptyStateAndOnlyBackAction() {
        var backs = 0
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                SupportModuleStateScreen(SupportDeliveryState.Disabled, onBack = {
                    backs++
                }, onRetry = { error("Módulo desabilitado não tenta instalar") })
            }
        }
        compose.onNodeWithTag("simlo.help.0050").assertIsDisplayed()
        compose.onNodeWithText("Este módulo não está disponível no momento.").assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
        compose.onNodeWithText("Voltar").performClick()
        compose.runOnIdle { assertEquals(1, backs) }
    }

    @Test fun failureOffersExplicitRetryAndReadyWithoutEntryKeepsTheFailureAction() {
        val state = mutableStateOf<SupportDeliveryState>(SupportDeliveryState.Failed)
        var retries = 0
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                SupportModuleStateScreen(state.value, onBack = {}, onRetry = { retries++ })
            }
        }
        compose.onNodeWithText("Não foi possível abrir este módulo. Tente novamente.").assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle { state.value = SupportDeliveryState.Ready }
        compose.onNodeWithText("Não foi possível abrir este módulo. Tente novamente.").assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle { assertEquals(2, retries) }
    }

    @Test fun installationStatesAllowLeavingWithoutStartingAnotherRequest() {
        val state = mutableStateOf<SupportDeliveryState>(SupportDeliveryState.Checking)
        var backs = 0
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                SupportModuleStateScreen(state.value, onBack = {
                    backs++
                }, onRetry = { error("Instalação em andamento não repete solicitação") })
            }
        }
        listOf(SupportDeliveryState.Checking, SupportDeliveryState.Pending, SupportDeliveryState.Downloading(1, 2)).forEach { current ->
            compose.runOnIdle { state.value = current }
            compose.onNodeWithText("Preparando o módulo. Aguarde a instalação.").assertIsDisplayed()
            compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
            compose.onNodeWithText("Voltar").performClick()
        }
        compose.runOnIdle { assertEquals(INSTALLATION_STATES_COUNT, backs) }
    }

    @Test fun gateDoesNotInstallOrResolveEntryUntilEnabledAndReportsMissingInstalledEntry() {
        val moduleConfiguration = MutableStateFlow(AppModulesConfiguration())
        var installs = 0
        var resolutions = 0
        val dependencies = object : SupportModuleNavigationDependencies {
            override val modules = AppModulesConfigurationUseCase(object : AppModulesConfigurationRepository {
                override val configuration = moduleConfiguration
                override suspend fun refresh() = Unit
            })
            override val delivery = object : SupportModuleDelivery {
                override fun install() = flow {
                    installs++
                    emit(SupportDeliveryState.Ready)
                }
            }
            override val entryLoader = SupportFaqEntryLoader {
                resolutions++
                if (resolutions == 1) {
                    null
                } else {
                    object : SupportFaqEntry {
                        @Composable
                        override fun Content(state: SupportUiState, onAction: (SupportAction) -> Unit) {
                            Text("Conteúdo FAQ")
                        }
                    }
                }
            }
        }
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                SupportModuleGate(dependencies, onBack = {}) { entry -> entry.Content(SupportUiState(), {}) }
            }
        }
        compose.onNodeWithText("Este módulo não está disponível no momento.").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(0, installs)
            assertEquals(0, resolutions)
            moduleConfiguration.value = AppModulesConfiguration(setOf(AppModule.SUPPORT_FAQ))
        }
        compose.onNodeWithText("Não foi possível abrir este módulo. Tente novamente.").assertIsDisplayed()
        compose.onNodeWithText("Conteúdo FAQ").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(1, installs)
            assertEquals(1, resolutions)
        }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("Conteúdo FAQ").assertIsDisplayed()
        compose.onNodeWithText("Não foi possível abrir este módulo. Tente novamente.").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(2, installs)
            assertEquals(2, resolutions)
        }
    }
}

private const val INSTALLATION_STATES_COUNT = 3
