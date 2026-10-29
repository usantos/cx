package br.gov.caixa.loterias.feature.betting

import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.core.navigation.state.rememberAppSessionState
import br.gov.caixa.loterias.feature.betting.domain.model.RapidBetConfiguration
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
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test

class RapidReopenedConfigurationUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun reopeningConfigurationAfterTutorialStartsAtSummaryWithSaveVisible() {
        val dependencies = proxy<BettingNavigationDependencies> { method ->
            when (method) {
                "getBettingTutorialDismissed" -> GetBettingTutorialDismissedUseCase { true }

                "getSetBettingTutorialDismissed" -> SetBettingTutorialDismissedUseCase { _, _ -> error("Unexpected tutorial write") }

                "getShakeEnabled" -> GetShakeEnabledUseCase { error("Rapid configuration must not read shake settings") }

                "getPrepareHomeBettingAction" -> PrepareHomeBettingActionUseCase { error("Visitor entry has no account check") }

                "getRapidBetConfiguration" -> GetRapidBetConfigurationUseCase { error("Visitor configuration has no backend load") }

                "getLotteryModalities" -> GetLotteryModalitiesUseCase { AppResult.Success(emptyList()) }

                "getSaveRapidBetConfiguration" -> SaveRapidBetConfigurationUseCase { _: RapidBetConfiguration -> error("Unexpected save") }

                "getFavoriteBets" -> GetFavoriteBetsUseCase { AppResult.Success(emptyList()) }

                "getBettingParameters" -> GetBettingParametersUseCase { error("Unexpected parameters") }

                "getVerifyAccountAccess" -> br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase {
                    error("Unexpected account check")
                }

                "getDispatchers" -> FixedCoroutineDispatchers(Dispatchers.Main.immediate)

                "getLocalClock" -> Clock.systemUTC()

                "getNetworkAvailability" -> br.gov.caixa.loterias.core.common.network.NetworkAvailability {
                    br.gov.caixa.loterias.core.common.network.ConnectionAvailability.CONNECTED
                }

                else -> error("Unexpected dependency: $method")
            }
        }
        compose.setContent {
            val nav = rememberNavController()
            val navigation = rememberAppNavigationState(null)
            val state = rememberAppSessionState()
            val session = remember {
                proxy<SessionNavigationActions> { method ->
                    when (method) {
                        "getState" -> state
                        "getIdentity" -> null
                        "getPrivateEntryKey" -> "visitor"
                        "isPrivateSessionCurrent", "getIsPrivateSessionCurrent" -> ({ true })
                        else -> error("Unexpected session action: $method")
                    }
                }
            }
            CaixaDSTheme {
                NavHost(nav, startDestination = "betting/rapid-config") {
                    bettingGraph(dependencies, nav, navigation, session)
                }
            }
        }
        compose.onNodeWithText("Resumo").assertIsDisplayed()
        compose.onNodeWithText("Salvar").assertIsDisplayed()
    }

    private inline fun <reified T> proxy(crossinline call: (String) -> Any?): T =
        Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, _ -> call(method.name) } as T
}
