// Literais numéricos descrevem entradas explícitas de teste e valores esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.home

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.core.navigation.action.AppNavigationActions
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.core.navigation.state.rememberAppSessionState
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedUser
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.repository.UnreadNotificationCountRepository
import br.gov.caixa.loterias.feature.account.domain.usecase.GetUnreadNotificationCountUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ObserveAuthenticatedSessionContentUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ReadLatestNotificationUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingDecision
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingTarget
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.repository.ShakeSettingsRepository
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBetCombosUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntryUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareHomeBettingActionUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PreparePoolEntryUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.home.domain.repository.HomeLayoutRepository
import br.gov.caixa.loterias.feature.home.domain.usecase.HomePreferencesUseCases
import br.gov.caixa.loterias.feature.home.domain.usecase.SelectHomeResaleOffers
import br.gov.caixa.loterias.feature.home.navigation.HomeNavigationDependenciesAdapter
import br.gov.caixa.loterias.feature.home.navigation.HomeRuntimeConfiguration
import br.gov.caixa.loterias.feature.home.navigation.LocalHomeRuntimeConfiguration
import br.gov.caixa.loterias.feature.home.navigation.homeGraph
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import br.gov.caixa.loterias.feature.results.domain.usecase.GetLatestResultUseCase
import br.gov.caixa.loterias.feature.results.domain.usecase.GetResultByContestUseCase
import java.lang.reflect.Proxy
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class HomeGraphFlowUiTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var nav: NavHostController
    private val requests = mutableListOf<String>()
    private val entries = mutableListOf<BetEntryDecision>()
    private val game = LotteryGameParameters(
        LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
        3073, "22/10/2099", 60, 6, 20, 10, emptyList(), emptyList()
    )
    private val parameters = BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 30, listOf(game))

    @Test fun loadedHomeBetUsesPreparedGameWithoutSubmittingAnOrder() {
        show()
        compose.onNode(
            androidx.compose.ui.test.hasText("Mega-Sena") and
                androidx.compose.ui.test.hasClickAction() and !androidx.compose.ui.test.hasText("Ver resultado ›")
        ).performScrollTo().assertIsDisplayed().performSemanticsAction(SemanticsActions.OnClick) { it() }
        compose.waitUntil(5_000) {
            compose.runOnIdle { androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications() }
            compose.waitForIdle()
            entries.isNotEmpty()
        }
        compose.runOnIdle { assertEquals(listOf(BetEntryDecision.Ready(game, parameters)), entries) }
    }

    @Test fun visitorFavoritesNavigateToPrivateDestinationAndRequestLogin() {
        show()
        compose.onNodeWithText("Apostas\nFavoritas").performClick()
        compose.runOnIdle { assertEquals(listOf("betting/favorites"), requests) }
    }

    @Test fun returningHomeResumesRequestedRapidActionExactlyOnce() {
        show(resume = "home/rapid", logged = true)
        compose.onNodeWithText("RAPID FIXTURE").assertIsDisplayed()
        compose.runOnIdle { assertEquals("betting/rapid", nav.currentDestination?.route) }
    }

    @Test fun homeRestoresPrivateContinuationOnlyForCurrentOwner() {
        show(continuation = "tickets/my-bets", logged = true)
        compose.onNodeWithText("HISTORY FIXTURE").assertIsDisplayed()
    }

    @Test fun leavingShakeTutorialWaitsForClassicHomeBeforeOpeningDrawer() {
        val parametersReady = CompletableDeferred<Unit>()
        show(tutorialCompleted = false, parametersReady = parametersReady, classicHome = true)
        compose.onNodeWithText("Pular tutorial").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Aposta Shake").assertIsNotDisplayed()
        compose.runOnIdle { parametersReady.complete(Unit) }
        compose.waitForIdle()
        compose.onNodeWithText("Aposta Shake").assertIsDisplayed()
    }

    private fun show(
        resume: String? = null,
        continuation: String? = null,
        logged: Boolean = false,
        tutorialCompleted: Boolean = true,
        parametersReady: CompletableDeferred<Unit>? = null,
        classicHome: Boolean = false,
    ) {
        val identity = if (logged) AuthenticatedSessionIdentity("a".repeat(64)) else null
        val sessions = object : AuthenticatedSessionIdentityProvider {
            override fun captureIdentity() = identity
            override fun isCurrent(candidate: AuthenticatedSessionIdentity) = candidate === identity
        }
        val now = ServerTimeSource { Instant.parse("2026-10-22T15:00:00Z") }
        val dependencies = homeDependencies(sessions, now, logged, tutorialCompleted, parametersReady)
        compose.setContent {
            nav = rememberNavController()
            val navigation = rememberAppNavigationState(identity?.ownerKey)
            val state = rememberAppSessionState()
            remember {
                state.resumedHomeAction = resume
                state.visitorReturnRoute = continuation
                state.visitorReturnOwnerKey =
                    identity?.ownerKey
                true
            }
            val session = remember {
                fixtureSession(state, identity)
            }
            val actions = remember { AppNavigationActions({ decision, _ -> entries.add(decision) }, {}, {}, {}, {}, {}) }
            CompositionLocalProvider(LocalHomeRuntimeConfiguration provides HomeRuntimeConfiguration(!classicHome, "test", now)) {
                CaixaDSTheme {
                    NavHost(nav, startDestination = "home") {
                        homeGraph(dependencies, nav, navigation, session, actions)
                        composable("betting/favorites") { CaixaDSText("FAVORITES FIXTURE") }
                        composable("betting/rapid") { CaixaDSText("RAPID FIXTURE") }
                        composable("tickets/my-bets") { CaixaDSText("HISTORY FIXTURE") }
                    }
                }
            }
        }
        compose.waitForIdle()
    }

    private fun homeDependencies(
        sessions: AuthenticatedSessionIdentityProvider,
        now: ServerTimeSource,
        logged: Boolean,
        tutorialCompleted: Boolean,
        parametersReady: CompletableDeferred<Unit>?,
    ): HomeNavigationDependenciesAdapter {
        val dependencies = HomeNavigationDependenciesAdapter(
            selectHomeResaleOffers = SelectHomeResaleOffers(now),
            homeFeatureConfiguration = GetHomeFeatureConfigurationUseCase { AppResult.Success(HomeFeatureConfiguration()) },
            homePreferences = HomePreferencesUseCases(
                object : HomeLayoutRepository {
                    override suspend fun loadOrder(): String? = null
                    override suspend fun saveOrder(order: String) = Unit
                },
                Settings(tutorialCompleted)
            ),
            latestResult = GetLatestResultUseCase { AppResult.Failure(AppError.Network) },
            resultByContest = GetResultByContestUseCase { _, _ -> AppResult.Failure(AppError.Network) },
            socialTransfers = GetSocialTransfersUseCase { AppResult.Failure(AppError.Network) },
            hostConfiguration = HostFeatureConfiguration(
                "test",
                "",
                "",
                "",
                false,
                null,
                false,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                false
            ),
            homeBettingDependencies = br.gov.caixa.loterias.feature.home.navigation.HomeBettingDependencies(
                betCombos = GetBetCombosUseCase { AppResult.Success(emptyList()) },
                bettingParameters = GetBettingParametersUseCase {
                    parametersReady?.await()
                    AppResult.Success(parameters)
                },
                currentCart = GetCurrentCartUseCase { AppResult.Success(ShoppingCart(0, BigDecimal.ZERO, emptyList())) },
                prepareBetEntry = PrepareBetEntryUseCase { AppResult.Success(BetEntryDecision.Ready(game, parameters)) },
                prepareHomeBettingAction = PrepareHomeBettingActionUseCase {
                    AppResult.Success(HomeBettingDecision.Ready(HomeBettingTarget.RAPID_CONFIGURATION))
                },
                preparePoolEntry = PreparePoolEntryUseCase { error("No reservation may be requested") }
            ),
            homeSessionDependencies = br.gov.caixa.loterias.feature.home.navigation.HomeSessionDependencies(
                serverTime = now,
                authenticatedSessionIdentity = sessions,
                currentUserIdentity = CurrentUserIdentityProvider { if (logged) AuthenticatedUser("Pessoa", "", "", "") else null },
                observeSessionContent = ObserveAuthenticatedSessionContentUseCase { flowOf(null) },
                readLatestNotification = ReadLatestNotificationUseCase { error("No private notification may be queried") },
                unreadNotificationCount = GetUnreadNotificationCountUseCase(UnreadNotificationCountRepository { 0 }, sessions)
            ),
            runtime = br.gov.caixa.loterias.feature.home.navigation.HomeRuntimeDependencies(
                dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
                localClock = Clock.systemUTC(),
                networkAvailability = NetworkAvailability { ConnectionAvailability.CONNECTED }
            )
        )
        return dependencies
    }

    private fun fixtureSession(
        state: br.gov.caixa.loterias.core.navigation.state.AppSessionState,
        identity: AuthenticatedSessionIdentity?,
    ): SessionNavigationActions {
        val onLogout: () -> Unit = { error("Unexpected logout") }
        val requestVisitorLogin: (String) -> Unit = { route ->
            requests.add(route)
            Unit
        }
        return Proxy.newProxyInstance(
            SessionNavigationActions::class.java.classLoader,
            arrayOf(SessionNavigationActions::class.java)
        ) { proxy, method, arguments ->
            when (method.name) {
                "equals" -> proxy === arguments?.firstOrNull()
                "hashCode" -> System.identityHashCode(proxy)
                "toString" -> "SessionNavigationActions fixture"
                "getState" -> state
                "getIdentity" -> identity
                "getOnLogout" -> onLogout
                "getRequestVisitorLogin" -> requestVisitorLogin
                else -> error("Unexpected session callback ${method.name}")
            }
        } as SessionNavigationActions
    }

    private class Settings(private var completed: Boolean = true) : ShakeSettingsRepository {
        private var pending = false
        override suspend fun enabled() = false
        override suspend fun setEnabled(enabled: Boolean) = Unit
        override suspend fun warningHidden() = true
        override suspend fun setWarningHidden(hidden: Boolean) = Unit
        override suspend fun tutorialCompleted(ownerKey: String?) = completed
        override suspend fun completeTutorial(ownerKey: String?) {
            completed = true
            pending = true
        }
        override suspend fun tooltipPending(ownerKey: String?) = pending
        override suspend fun consumeTooltip(ownerKey: String?) {
            pending = false
        }
    }
}
