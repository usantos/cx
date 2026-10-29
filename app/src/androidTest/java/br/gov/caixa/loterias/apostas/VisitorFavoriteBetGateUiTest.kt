package br.gov.caixa.loterias.apostas

import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.core.navigation.state.rememberAppSessionState
import br.gov.caixa.loterias.core.navigation.workflow.VisitorLoginCallbacks
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetShakeEnabledUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.navigation.BettingNavigationDependencies
import br.gov.caixa.loterias.feature.betting.navigation.bettingGraph
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetBettingTutorialDismissedUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissedUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import java.lang.reflect.Proxy
import java.math.BigDecimal
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Executa a integração real do grafo com a sessão. Cada dependência fornecida é uma simulação local. */
class VisitorFavoriteBetGateUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun visitorFavoriteRequestsLoginBeforeOpeningOrValidatingFavoriteName() {
        val requests = CopyOnWriteArrayList<String>()
        val parameters = BettingParameters(
            BigDecimal("500"),
            BigDecimal("30"),
            50,
            listOf(
                LotteryGameParameters(
                    LotteryModality(3, "Quina", "QUINA"),
                    contestNumber = 7000, drawDate = "21/10/2026", maximumNumber = 80,
                    minimumSelection = 5, maximumSelection = 5, maximumSurprises = 3,
                    recurringOptions = emptyList(), prices = listOf(BetPrice(5, 0, BigDecimal("3.00")))
                )
            )
        )
        showVisitorFavorite(parameters, requests)
        compose.waitUntil { compose.onAllNodesWithText("Favoritar aposta").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Favoritar aposta").performClick()
        compose.waitForIdle()
        assertEquals(1, requests.size)
        compose.onNodeWithTag("favorite-name-input").assertDoesNotExist()
        compose.onNodeWithText("Não").performClick()
        compose.onNodeWithText("Favoritar aposta").assertExists()
        (1..5).forEach { compose.onNodeWithTag("betting-number-${it.toString().padStart(2, '0')}").assertIsSelected() }
        compose.onNodeWithText("Favoritar aposta").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Login simulado").assertExists()
        assertEquals(2, requests.size)
    }

    private fun showVisitorFavorite(parameters: BettingParameters, requests: MutableList<String>) {
        val dependencies = visitorDependencies(parameters)
        compose.setContent {
            val nav = rememberNavController()
            val navigation = rememberAppNavigationState(null)
            val state = rememberAppSessionState()
            remember(navigation.details) {
                navigation.details.betToEdit = ConfirmedBet(41, "Quina", 7000, "", "", listOf(1, 2, 3, 4, 5), modalityId = 3)
                true
            }
            val session = remember {
                proxy<SessionNavigationActions> { method ->
                    when (method) {
                        "getState" -> state

                        "getIdentity" -> null

                        "getPrivateEntryKey" -> "visitor"

                        "getRequestVisitorLogin" -> (
                            { route: String ->
                                requests += route
                                state.visitorLoginRequest = route
                            }
                            )

                        else -> error("Unexpected session action: $method")
                    }
                }
            }
            CaixaDSTheme {
                NavHost(nav, startDestination = "betting/simulation/3") {
                    bettingGraph(dependencies, nav, navigation, session)
                    composable(br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation.pattern) { Text("Login simulado") }
                }
                state.visitorLoginRequest?.let { route ->
                    val callbacks = VisitorLoginCallbacks(
                        route,
                        nav,
                        pendingRequest = { state.visitorLoginRequest },
                        clearRequest = { state.visitorLoginRequest = null },
                        continueAfterLogin = { state.visitorReturnRoute = it }
                    )
                    CaixaDSDialog(
                        "Atenção",
                        "Deseja efetuar o login?",
                        "Sim",
                        callbacks::onPositive,
                        negativeLabel = "Não",
                        onNegative = callbacks::onNegative,
                        visitorPrompt = true
                    )
                }
            }
        }
    }

    private fun visitorDependencies(parameters: BettingParameters) = proxy<BettingNavigationDependencies> { method ->
        when (method) {
            "getServerTime" -> br.gov.caixa.loterias.core.common.time.ServerTimeSource { java.time.Instant.EPOCH }

            "getRecoveryMessages" -> br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingRecoveryMessages.Identifiers

            "getBettingCheckpoints" -> br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpointSessionFactory {
                object : br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpointSession {
                    override fun isCurrent() = true
                    override suspend fun read(): br.gov.caixa.loterias.feature.betting.domain.recovery.BettingRecoveryRecord? = null
                    override suspend fun save(checkpoint: br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpoint) = Unit
                    override suspend fun beginOperation(
                        id: String,
                        kind: String,
                        checkpoint: br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpoint,
                    ) = error("Unexpected mutation")
                    override suspend fun completeOperation(id: String) = error("Unexpected mutation")
                }
            }

            "getBettingSpecialContests" -> br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingSpecialContestsUseCase {
                AppResult.Success(br.gov.caixa.loterias.core.domain.configuration.model.HomeSpecialContests())
            }

            "getBettingParameters" -> GetBettingParametersUseCase { AppResult.Success(parameters) }

            "getCurrentCart" -> GetCurrentCartUseCase { AppResult.Success(ShoppingCart(0, BigDecimal.ZERO, emptyList())) }

            "getAddBetToCart" -> AddBetToCartUseCase { error("Unexpected cart mutation") }

            "getValidateFavoriteBet" -> ValidateFavoriteBetUseCase { error("Visitor must not validate a favorite") }

            "getSaveFavoriteBet" -> SaveFavoriteBetUseCase { error("Visitor must not save a favorite") }

            "getDispatchers" -> FixedCoroutineDispatchers(Dispatchers.Main.immediate)

            "getShakeEnabled", "getBettingTutorialDismissed", "getSetBettingTutorialDismissed" -> visitorPreferenceDependency(method)

            else -> error("Unexpected dependency: $method")
        }
    }

    private fun visitorPreferenceDependency(method: String): Any = when (method) {
        "getShakeEnabled" -> GetShakeEnabledUseCase { false }
        "getBettingTutorialDismissed" -> GetBettingTutorialDismissedUseCase { error("Simulation must not read the rapid tutorial") }
        "getSetBettingTutorialDismissed" -> SetBettingTutorialDismissedUseCase { _, _ -> error("Unexpected tutorial write") }
        else -> error("Unexpected preference dependency: $method")
    }

    private inline fun <reified T> proxy(crossinline call: (String) -> Any?): T =
        Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, _ ->
            call(method.name)
        } as T
}
