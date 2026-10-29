package br.gov.caixa.loterias.feature.betting

import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.core.navigation.state.rememberAppSessionState
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetFavoriteBetsUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetShakeEnabledUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareHomeBettingActionUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.navigation.BettingNavigationDependencies
import br.gov.caixa.loterias.feature.betting.navigation.bettingGraph
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetBettingTutorialDismissedUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissedUseCase
import java.lang.reflect.Proxy
import java.time.Clock
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w480dp-h900dp")
class RapidPreferenceRecoveryNavigationUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun rapidTutorialReadFailureShowsPreferenceMessageAndRetryOpensSavedSummary() {
        val reads = AtomicInteger()
        lateinit var nav: NavHostController
        val dependencies = createDependencies(reads)
        compose.setContent {
            nav = rememberNavController()
            val navigation = rememberAppNavigationState(null)
            val state = rememberAppSessionState()
            val session = remember { visitorSession(state) }
            CaixaDSTheme {
                NavHost(nav, startDestination = "betting/rapid-config") {
                    bettingGraph(dependencies, nav, navigation, session)
                }
            }
        }

        val failure = "Não foi possível ler suas preferências. Tente novamente."
        compose.onNodeWithText(failure).assertIsDisplayed()
        compose.onNodeWithText("Salvar").assertDoesNotExist()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText(failure).assertDoesNotExist()
        compose.onNodeWithText("Resumo").assertIsDisplayed()
        compose.onNodeWithText("Salvar").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(2, reads.get())
            assertEquals("betting/rapid-config", nav.currentDestination?.route)
        }
    }

    private fun createDependencies(reads: AtomicInteger): BettingNavigationDependencies = proxy { method ->
        when (method) {
            "getBettingTutorialDismissed", "getSetBettingTutorialDismissed", "getShakeEnabled" ->
                preferenceDependency(method, reads)

            "getPrepareHomeBettingAction" -> PrepareHomeBettingActionUseCase { error("Visitor must not check account") }

            "getRapidBetConfiguration" -> GetRapidBetConfigurationUseCase { error("Visitor must not load private configuration") }

            "getLotteryModalities" -> GetLotteryModalitiesUseCase { AppResult.Success(emptyList()) }

            "getSaveRapidBetConfiguration" -> SaveRapidBetConfigurationUseCase { error("Unexpected configuration write") }

            "getFavoriteBets" -> GetFavoriteBetsUseCase { AppResult.Success(emptyList()) }

            "getBettingParameters" -> GetBettingParametersUseCase { error("Unexpected betting parameters") }

            "getVerifyAccountAccess" -> VerifyAccountAccessUseCase { error("Unexpected account check") }

            "getDispatchers" -> FixedCoroutineDispatchers(Dispatchers.Main.immediate)

            "getLocalClock" -> Clock.systemUTC()

            "getNetworkAvailability" -> NetworkAvailability { ConnectionAvailability.CONNECTED }

            else -> error("Unexpected dependency: $method")
        }
    }

    private fun preferenceDependency(method: String, reads: AtomicInteger): Any = when (method) {
        "getBettingTutorialDismissed" -> GetBettingTutorialDismissedUseCase {
            if (reads.incrementAndGet() == 1) throw SecureStorageException.ReadFailure()
            true
        }

        "getSetBettingTutorialDismissed" -> SetBettingTutorialDismissedUseCase { _, _ -> error("Unexpected tutorial write") }

        "getShakeEnabled" -> GetShakeEnabledUseCase { error("Rapid configuration must not read shake settings") }

        else -> error("Unexpected preference dependency: $method")
    }

    private fun visitorSession(state: AppSessionState): SessionNavigationActions = proxy { method ->
        when (method) {
            "getState" -> state
            "getIdentity" -> null
            "getPrivateEntryKey" -> "visitor"
            "getIsPrivateSessionCurrent", "isPrivateSessionCurrent" -> ({ true })
            else -> error("Unexpected session action: $method")
        }
    }

    private inline fun <reified T> proxy(crossinline call: (String) -> Any?): T =
        Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, _ -> call(method.name) } as T
}
