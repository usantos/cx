// Literais numéricos descrevem geometria fixa dos testes, IDs dos dados de teste, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.onboarding

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetMarketplaceFilterParametersUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.IsVersionSupportedUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.PrepareNetworkSecurityUseCase
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilterParameters
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.CompleteIntroductionUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissedUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.ShouldShowIntroductionUseCase
import br.gov.caixa.loterias.feature.onboarding.navigation.OnboardingNavigationDependenciesAdapter
import br.gov.caixa.loterias.feature.onboarding.navigation.StartupStatusPresentation
import br.gov.caixa.loterias.feature.onboarding.presentation.components.SimulationRecoveryPanel
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.StartupStatusKind
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.StartupStatusScreen
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class StartupDestinationJvmTest {
    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()
    private val network = NetworkAvailability { ConnectionAvailability.CONNECTED }

    @Test
    fun statusAdapterRetainsUnavailablePanelDuringRetryAndRecoversOnlyAfterValidation() {
        val version = CompletableDeferred<AppResult<Boolean>>()
        var recovered = 0
        val operations = mutableListOf<String>()
        val complete = CompleteIntroductionUseCase { operations.add("complete") }
        val dismiss = SetBettingTutorialDismissedUseCase { _, _ -> operations.add("dismiss") }
        val showIntro = ShouldShowIntroductionUseCase { true }
        val presentation = object : StartupStatusPresentation {
            @androidx.compose.runtime.Composable
            override fun Content(kind: StartupStatusKind, onRetry: () -> Unit, networkAvailability: NetworkAvailability, loading: Boolean) {
                assertSame(network, networkAvailability)
                Text("${kind.name}:$loading")
                Button(onClick = onRetry) { Text("Retry validation") }
            }
        }
        val adapter = OnboardingNavigationDependenciesAdapter(
            complete, dismiss, showIntro, configuration(),
            IsVersionSupportedUseCase { requested ->
                assertEquals("1.2.3", requested)
                operations.add("version")
                version.await()
            },
            GetMarketplaceFilterParametersUseCase {
                operations.add("parameters")
                AppResult.Success(MarketplaceFilterParameters(2, 4))
            },
            PrepareNetworkSecurityUseCase {
                operations.add("security")
                AppResult.Success(Unit)
            },
            network,
            FixedCoroutineDispatchers(Dispatchers.Main.immediate), presentation
        )
        assertSame(complete, adapter.completeIntroduction)
        assertSame(dismiss, adapter.setBettingTutorialDismissed)
        assertSame(showIntro, adapter.shouldShowIntroduction)
        compose.setContent { CaixaDSTheme { adapter.StartupStatusDestination("onboarding/unavailable", { recovered++ }) } }
        compose.onNodeWithText("UNAVAILABLE:false").assertIsDisplayed()
        compose.onNodeWithText("Retry validation").performClick()
        compose.onNodeWithText("UNAVAILABLE:true").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(0, recovered)
            version.complete(AppResult.Success(true))
        }
        compose.onNodeWithText("Retry validation").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(1, recovered)
            assertEquals(listOf("security", "version", "parameters"), operations)
        }
    }

    @Test
    fun updateScreenUsesProvidedBrandSlotsAndLaunchesItsOwnUpdateAction() {
        var updates = 0
        compose.setContent {
            CaixaDSTheme {
                StartupStatusScreen(
                    StartupStatusKind.UPDATE_REQUIRED,
                    "Loterias",
                    "1.2.3",
                    { updates++ },
                    toolbarIcon = { Text("Marca institucional") },
                    illustration = { Box(it.testTag("update-artwork")) }
                )
            }
        }
        compose.onNodeWithText("Loterias").assertIsDisplayed()
        compose.onNodeWithText("Marca institucional").assertIsDisplayed()
        compose.onNodeWithTag("update-artwork").assertIsDisplayed()
        compose.onNodeWithText("v1.2.3").assertIsDisplayed()
        compose.onNodeWithText("Atualizar").performClick()
        compose.runOnIdle { assertEquals(1, updates) }
    }

    @Test
    fun recoveryPanelForwardsStatusActionsAndConsumesBackWhileRetryIsLoading() {
        val loading = mutableStateOf(true)
        val message = mutableStateOf<String?>("Aposta indisponível")
        var retries = 0
        var backs = 0
        var dismissals = 0
        compose.setContent {
            CaixaDSTheme {
                SimulationRecoveryPanel(loading.value, message.value, { retries++ }, { backs++ }, {
                    dismissals++
                    message.value = null
                }, network) { busy, retry, suppliedNetwork ->
                    assertSame(network, suppliedNetwork)
                    Text("Loading:$busy")
                    Button(onClick = retry) { Text("Retry") }
                }
            }
        }
        compose.onNodeWithText("Aposta indisponível").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            compose.activity.onBackPressedDispatcher.onBackPressed()
            assertEquals(0, backs)
            loading.value = false
        }
        compose.onNodeWithText("Loading:false").assertIsDisplayed()
        compose.onNodeWithText("Retry").performClick()
        compose.runOnIdle {
            compose.activity.onBackPressedDispatcher.onBackPressed()
            assertEquals(1, backs)
            assertEquals(1, retries)
            assertEquals(1, dismissals)
        }
    }

    private fun configuration() =
        HostFeatureConfiguration("1.2.3", "", "", "", false, null, false, "", "", "", "", "", "", "", "", "", "", true)
}
