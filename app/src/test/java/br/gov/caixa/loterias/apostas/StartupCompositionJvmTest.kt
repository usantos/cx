// Literais numéricos descrevem geometria fixa de teste, IDs dos dados de teste, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.apostas

import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.apostas.composition.navigation.AppFeatureGraphDependencies
import br.gov.caixa.loterias.apostas.composition.startup.StartupGateContent
import br.gov.caixa.loterias.apostas.initialization.AppInitializationDependencies
import br.gov.caixa.loterias.apostas.initialization.startup.FilaBraInitialization
import br.gov.caixa.loterias.apostas.initialization.startup.MockBackendAppGate
import br.gov.caixa.loterias.apostas.navigation.session.AppSessionDependencies
import br.gov.caixa.loterias.core.auth.foreground.ForegroundSessionBackgroundState
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetMarketplaceFilterParametersUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.IsVersionSupportedUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.PrepareNetworkSecurityUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilterParameters
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.StartupStatusKind
import br.gov.caixa.loterias.feature.onboarding.presentation.state.StartupState
import java.time.Clock
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class StartupCompositionJvmTest {
    @get:Rule
    val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private val network = NetworkAvailability { ConnectionAvailability.CONNECTED }

    @Test
    fun startupGateKeepsFailureVisibleDuringRetryAndReleasesReadyContentOnlyAfterRecovery() {
        val state = mutableStateOf(StartupState.WAITING_QUEUE)
        var retries = 0
        compose.setContent {
            CaixaDSTheme {
                StartupGateContent(state.value, {
                    retries++
                    state.value = StartupState.CHECKING
                }, network) { Text("Main content ready") }
            }
        }
        compose.onNodeWithContentDescription("Logo Loterias CAIXA").assertExists()
        compose.onNodeWithText("Main content ready").assertDoesNotExist()
        compose.runOnIdle { state.value = StartupState.UNAVAILABLE }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("Tentando novamente…").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, retries)
            state.value = StartupState.READY
        }
        compose.onNodeWithText("Main content ready").assertIsDisplayed()
        compose.onNodeWithText("Tentando novamente…").assertDoesNotExist()
    }

    @Test
    fun securityInitializationRetriesBeforeReleasingTheNextStartupStage() {
        val retry = CompletableDeferred<AppResult<Unit>>()
        var calls = 0
        val dependencies = initialization(
            PrepareNetworkSecurityUseCase {
                if (++calls == 1) AppResult.Failure(AppError.Network) else retry.await()
            }
        )
        compose.setContent {
            CaixaDSTheme { FilaBraInitialization(dependencies) { Text("Security prepared") } }
        }
        compose.onNodeWithText("Security prepared").assertDoesNotExist()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("Tentando novamente…").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(2, calls)
            retry.complete(AppResult.Success(Unit))
        }
        compose.onNodeWithText("Security prepared").assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
    }

    @Test
    fun mockGateStillRequiresVersionAndFilterValidationBeforeAnyFeatureCanStart() {
        var versions = 0
        var parameters = 0
        val supported = mutableStateOf(false)
        val dependencies = initialization(
            PrepareNetworkSecurityUseCase { AppResult.Success(Unit) },
            IsVersionSupportedUseCase {
                versions++
                AppResult.Success(supported.value)
            },
            GetMarketplaceFilterParametersUseCase {
                parameters++
                AppResult.Success(MarketplaceFilterParameters(2, 4))
            }
        )
        val features = AppFeatureGraphDependencies(
            inactive(), inactive(), inactive(), inactive(), inactive(), inactive(),
            inactive(), inactive(), inactive(), inactive(), inactive(), inactive(), inactive()
        )
        val session = AppSessionDependencies(inactive(), ForegroundSessionBackgroundState(), Clock.systemUTC())
        compose.setContent { CaixaDSTheme { MockBackendAppGate(dependencies, features, session, minimumSplashElapsed = false, {}) } }
        compose.onNodeWithText("Atualizar").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, versions)
            assertEquals(0, parameters)
        }
    }

    @Test
    fun startupGateCanStartFromAnExplicitUpdateRequirementWithoutRetryingBusinessServices() {
        var retries = 0
        compose.setContent {
            CaixaDSTheme {
                StartupGateContent(StartupState.WAITING_QUEUE, {
                    retries++
                }, network, StartupStatusKind.UPDATE_REQUIRED) { Text("Unexpected ready") }
            }
        }
        compose.onNodeWithText("Atualizar").assertIsDisplayed()
        compose.onNodeWithText("Unexpected ready").assertDoesNotExist()
        compose.runOnIdle { assertEquals(0, retries) }
    }

    @Test
    fun successfulMockValidationStillWaitsForTheMinimumSplashBeforeReadingSessionOrFeatures() {
        val operations = mutableListOf<String>()
        val dependencies = initialization(
            PrepareNetworkSecurityUseCase {
                operations.add("security")
                AppResult.Success(Unit)
            },
            IsVersionSupportedUseCase {
                operations.add("version")
                AppResult.Success(true)
            },
            GetMarketplaceFilterParametersUseCase {
                operations.add("filters")
                AppResult.Success(MarketplaceFilterParameters(2, 4))
            }
        )
        val features = AppFeatureGraphDependencies(
            inactive(), inactive(), inactive(), inactive(), inactive(), inactive(),
            inactive(), inactive(), inactive(), inactive(), inactive(), inactive(), inactive()
        )
        val session = AppSessionDependencies(inactive(), ForegroundSessionBackgroundState(), Clock.systemUTC())
        compose.setContent {
            CaixaDSTheme { MockBackendAppGate(dependencies, features, session, minimumSplashElapsed = false, {}) }
        }
        compose.onNodeWithContentDescription("Logo Loterias CAIXA").assertExists()
        compose.onNodeWithText("Atualizar").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf("security", "version", "filters"), operations) }
    }

    private fun initialization(
        security: PrepareNetworkSecurityUseCase,
        version: IsVersionSupportedUseCase = IsVersionSupportedUseCase { AppResult.Success(true) },
        parameters: GetMarketplaceFilterParametersUseCase =
            GetMarketplaceFilterParametersUseCase { AppResult.Success(MarketplaceFilterParameters(2, 4)) },
    ) = AppInitializationDependencies(network, security, version, parameters, FixedCoroutineDispatchers(Dispatchers.Main.immediate))

    /** A inicialização não deve acessar dependências de funcionalidades ou sessão enquanto o pré-requisito estiver bloqueado. */
    private inline fun <reified T> inactive(): T =
        java.lang.reflect.Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) {
                _,
                method,
                _,
            ->
            error("Startup crossed its blocked boundary: ${T::class.java.simpleName}.${method.name}")
        } as T
}
