package br.gov.caixa.loterias.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.CompleteIntroductionUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissedUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.ShouldShowIntroductionUseCase
import br.gov.caixa.loterias.feature.onboarding.navigation.OnboardingNavigationDependencies
import br.gov.caixa.loterias.feature.onboarding.navigation.onboardingGraph
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@RunWith(DynamicFeatureRobolectricTestRunner::class)
@ResourcesMode(ResourcesMode.Mode.NATIVE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], qualifiers = "w480dp-h900dp", application = android.app.Application::class)
class StartupRecoveryNavigationUiTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun recoveredUpdateRetriesStorageFailureAndNavigatesToLoginOnlyOnce() {
        val reads = AtomicInteger()
        val recoveryCallbacks = AtomicInteger()
        val loginVisits = AtomicInteger()
        val recomposeSignal = mutableIntStateOf(0)
        lateinit var nav: NavHostController
        val dependencies = object : OnboardingNavigationDependencies {
            override val completeIntroduction = CompleteIntroductionUseCase { error("Unexpected introduction write") }
            override val setBettingTutorialDismissed = SetBettingTutorialDismissedUseCase { _, _ -> error("Unexpected tutorial write") }
            override val shouldShowIntroduction = ShouldShowIntroductionUseCase {
                if (reads.incrementAndGet() == 1) throw SecureStorageException.ReadFailure()
                false
            }

            @Composable
            override fun StartupStatusDestination(route: String, onRecovered: () -> Unit) {
                LaunchedEffect(Unit) {
                    recoveryCallbacks.incrementAndGet()
                    onRecovered()
                }
            }
        }
        compose.setFeatureContent {
            nav = rememberNavController()
            DisposableEffect(nav) {
                val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
                    if (destination.route == "account/login") loginVisits.incrementAndGet()
                }
                nav.addOnDestinationChangedListener(listener)
                onDispose { nav.removeOnDestinationChangedListener(listener) }
            }
            LoteriasCaixaDSTheme {
                NavHost(nav, startDestination = "onboarding/update") {
                    onboardingGraph(dependencies, nav)
                    composable("account/login") { LoteriasCaixaDSText("Login ${recomposeSignal.intValue}") }
                }
            }
        }

        val failure = "Não foi possível salvar suas preferências. Tente novamente."
        compose.waitUntil(RECOVERY_TIMEOUT_MS) { compose.onAllNodesWithText(failure).fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText(failure).assertIsDisplayed()
        compose.runOnIdle {
            assertEquals("onboarding/update", nav.currentDestination?.route)
            assertEquals(0, loginVisits.get())
        }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.waitUntil(RECOVERY_TIMEOUT_MS) { compose.onAllNodesWithText("Login 0").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Login 0").assertIsDisplayed()
        compose.onNodeWithText(failure).assertDoesNotExist()

        compose.runOnIdle { recomposeSignal.intValue++ }
        compose.onNodeWithText("Login 1").assertIsDisplayed()
        compose.mainClock.advanceTimeBy(RECOMPOSITION_WINDOW_MS)
        compose.runOnIdle {
            assertEquals(2, reads.get())
            assertEquals(1, recoveryCallbacks.get())
            assertEquals(1, loginVisits.get())
            assertNull(nav.previousBackStackEntry)
        }
    }
}

private const val RECOVERY_TIMEOUT_MS = 5_000L
private const val RECOMPOSITION_WINDOW_MS = 1_000L
