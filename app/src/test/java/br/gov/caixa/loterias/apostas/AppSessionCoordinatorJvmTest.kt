// Literais numéricos descrevem geometria fixa de teste, IDs dos dados de teste, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.apostas

import android.content.Intent
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.apostas.navigation.session.AppSessionCoordinator
import br.gov.caixa.loterias.apostas.navigation.session.AppSessionDependencies
import br.gov.caixa.loterias.apostas.navigation.session.rememberAppSessionCoordinator
import br.gov.caixa.loterias.core.auth.foreground.ForegroundSessionBackgroundState
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.auth.gateway.EndSessionGateway
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.DetailRestorationOutcome
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.EndSessionLaunch
import br.gov.caixa.loterias.feature.account.domain.model.EndSessionOutcome
import br.gov.caixa.loterias.feature.account.domain.model.LocalSessionRecovery
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginAccessResult
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.model.SessionSignOutResult
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.ClearLocalSessionDataUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResumeLocalSessionCleanupUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.SignOutSessionUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import br.gov.caixa.loterias.feature.account.navigation.AccountSessionDependencies
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AppSessionCoordinatorJvmTest {
    @get:Rule
    val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private lateinit var coordinator: AppSessionCoordinator
    private lateinit var navigation: AppNavigationState
    private lateinit var nav: NavHostController
    private var restarts = 0
    private var homeCompositions = 0

    @Test
    fun appHostStartsPublicResultsWithoutTouchingOtherFeatureRepositoriesOrCredentials() {
        val fixture = Fixture(authenticated = false)
        val requests = mutableListOf<String>()
        val result = br.gov.caixa.loterias.feature.results.domain.model.DrawResult(
            "Quina", 10, "22/10/2026", "",
            listOf(1, 2, 3, 4, 5), emptyList(), emptyList(), false, "", "0", "", emptyList()
        )
        val results = object : br.gov.caixa.loterias.feature.results.navigation.ResultsNavigationDependencies {
            override val dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate)
            override val mockBackendEnabled = false
            override val latestResult = br.gov.caixa.loterias.feature.results.domain.usecase.GetLatestResultUseCase { modality ->
                requests.add(modality)
                kotlinx.coroutines.yield()
                br.gov.caixa.loterias.core.common.result.AppResult.Success(result)
            }
            override val resultByContest = br.gov.caixa.loterias.feature.results.domain.usecase.GetResultByContestUseCase { _, _ ->
                error("No contest search was requested")
            }
            override val confirmedBetResults = br.gov.caixa.loterias.feature.results.domain.usecase.GetConfirmedBetResultsUseCase {
                error("Public results cannot read private confirmed bets")
            }
            override fun visualConfirmedBet(variant: String, originalRecurringBet: Boolean, redeemedPrize: Boolean): ConfirmedBet =
                error("Real backend cannot fabricate a bet")
        }
        val home = object : br.gov.caixa.loterias.feature.home.navigation.HomeNavigationDependencies by inactiveFeature() {
            override val serverTime = br.gov.caixa.loterias.core.common.time.ServerTimeSource { Instant.ofEpochSecond(100) }
        }
        val features = br.gov.caixa.loterias.apostas.composition.navigation.AppFeatureGraphDependencies(
            inactiveFeature(), inactiveFeature(), inactiveFeature(), home, inactiveFeature(), inactiveFeature(),
            inactiveFeature(), inactiveFeature(), inactiveFeature(), results, inactiveFeature(), inactiveFeature(), inactiveFeature()
        )
        val session =
            AppSessionDependencies(fixture, ForegroundSessionBackgroundState(), Clock.fixed(Instant.ofEpochSecond(100), ZoneOffset.UTC))
        compose.setContent { CaixaDSTheme { br.gov.caixa.loterias.apostas.navigation.AppNavHost(features, session, "results/modalities") } }
        compose.onNodeWithText("Modalidades").assertIsDisplayed()
        compose.onNodeWithText("quina").performClick()
        compose.onNodeWithText("Concurso nº 10").assertIsDisplayed()
        compose.onNodeWithText("Voltar").performClick()
        compose.onNodeWithText("Modalidades").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(listOf("quina"), requests)
            assertEquals(0, fixture.localLogouts)
            assertEquals(0, fixture.verifications)
            assertNull(fixture.lease)
        }
    }

    @Test
    fun authenticatedHomeDoesNotOpenAnUnrequestedSavedBettingScreen() {
        val fixture = Fixture()
        var recoveryOpens = 0
        val betting = object : br.gov.caixa.loterias.feature.betting.navigation.BettingNavigationDependencies by inactiveFeature() {
            override val bettingCheckpoints = br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpointSessionFactory {
                recoveryOpens++
                error("Home cannot choose a destination from an old betting draft")
            }
        }
        val preferences = br.gov.caixa.loterias.feature.home.domain.usecase.HomePreferencesUseCases(
            object : br.gov.caixa.loterias.feature.home.domain.repository.HomeLayoutRepository {
                override suspend fun loadOrder(): String? = null
                override suspend fun saveOrder(order: String) = Unit
            },
            object : br.gov.caixa.loterias.feature.betting.domain.repository.ShakeSettingsRepository {
                override suspend fun enabled() = false
                override suspend fun setEnabled(enabled: Boolean) = Unit
                override suspend fun warningHidden() = false
                override suspend fun setWarningHidden(hidden: Boolean) = Unit
                override suspend fun tutorialCompleted(ownerKey: String?) = false
                override suspend fun completeTutorial(ownerKey: String?) = Unit
                override suspend fun tooltipPending(ownerKey: String?) = false
                override suspend fun consumeTooltip(ownerKey: String?) = Unit
            }
        )
        val home = object : br.gov.caixa.loterias.feature.home.navigation.HomeNavigationDependencies by inactiveFeature() {
            override val serverTime = br.gov.caixa.loterias.core.common.time.ServerTimeSource { Instant.ofEpochSecond(100) }
            override val dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate)
            override val homePreferences = preferences
        }
        val features = br.gov.caixa.loterias.apostas.composition.navigation.AppFeatureGraphDependencies(
            inactiveFeature(), betting, inactiveFeature(), home, inactiveFeature(), inactiveFeature(),
            inactiveFeature(), inactiveFeature(), inactiveFeature(), inactiveFeature(),
            inactiveFeature(), inactiveFeature(), inactiveFeature()
        )
        val dependencies = AppSessionDependencies(
            fixture,
            ForegroundSessionBackgroundState(),
            Clock.fixed(Instant.ofEpochSecond(100), ZoneOffset.UTC)
        )
        compose.setContent {
            CaixaDSTheme { br.gov.caixa.loterias.apostas.navigation.AppNavHost(features, dependencies, "home") }
        }
        compose.runOnIdle { assertEquals(0, recoveryOpens) }
        compose.onNodeWithText("Pular tutorial").assertExists()
    }

    @Test
    fun visitorCanDeclineThenAcceptLoginAndResumeRequestedFeatureAfterAuthentication() {
        val fixture = Fixture(authenticated = false)
        show(fixture)
        compose.runOnIdle { coordinator.requestVisitorLogin("cart/current") }
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle {
            assertNull(coordinator.state.visitorLoginRequest)
            assertEquals("home", nav.currentDestination?.route)
        }
        compose.runOnIdle { coordinator.requestVisitorLogin("cart/current") }
        compose.onNodeWithText("Sim").performClick()
        compose.runOnIdle {
            assertEquals(br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation.pattern, nav.currentDestination?.route)
            assertEquals("cart/current", coordinator.state.visitorReturnRoute)
            fixture.lease = AuthenticatedSessionIdentity("a".repeat(64))
        }
        compose.runOnIdle { coordinator.onAuthenticated("home") }
        compose.runOnIdle {
            assertEquals("cart/current", nav.currentDestination?.route)
            assertNull(coordinator.state.visitorReturnRoute)
            assertFalse(coordinator.state.accountCompletionRequired)
            assertTrue(coordinator.isPrivateSessionCurrent())
        }
    }

    @Test
    fun tryNowClearsOwnerBoundContinuationsAndPrivateDetailsBeforeShowingPublicHome() {
        val fixture = Fixture()
        show(fixture, "account/login")
        compose.runOnIdle {
            navigation.details.reconcile(fixture.lease!!.ownerKey)
            navigation.details.selectedBet = privateBet()
            coordinator.state.pendingRestoredRoute = "results/confirmed-bet"
            coordinator.state.visitorReturnRoute = "retail/prize/10"
            coordinator.state.visitorReturnOwnerKey = fixture.lease!!.ownerKey
            coordinator.state.accountCompletionRequired = true
            coordinator.onTryNow()
        }
        compose.runOnIdle {
            assertEquals("home", nav.currentDestination?.route)
            assertNull(navigation.details.selectedBet)
            assertNull(navigation.details.owner)
            assertNull(coordinator.state.pendingRestoredRoute)
            assertNull(coordinator.state.visitorReturnRoute)
            assertNull(coordinator.state.visitorReturnOwnerKey)
            assertFalse(coordinator.state.accountCompletionRequired)
        }
    }

    @Test
    fun anonymousExitReturnsToLoginWithoutEndingAuthenticatedSessions() {
        val fixture = Fixture(authenticated = false)
        show(fixture, "home")
        compose.runOnIdle { coordinator.onLogout() }
        compose.runOnIdle {
            assertEquals("account/login", nav.currentDestination?.route)
            assertEquals(0, fixture.serverLogouts)
            assertEquals(0, fixture.localLogouts)
            assertEquals(0, fixture.cleanups)
            assertEquals(0, fixture.browserLogouts)
            assertNull(coordinator.logoutViewModel.state.value.notice)
            assertNull(coordinator.logoutViewModel.state.value.launch)
        }
    }

    @Test
    fun logoutClearsNavigationOnlyAfterLocalSessionEnds() {
        val fixture = Fixture()
        show(fixture)
        compose.runOnIdle {
            navigation.requestedHomeModalityId = 3
            navigation.details.selectedBet = privateBet()
            coordinator.state.visitorReturnRoute = "cart/current"
            coordinator.state.resumedHomeAction = "home/pool"
            coordinator.state.accountCompletionRequired = true
            coordinator.onLogout()
        }
        compose.runOnIdle {
            assertEquals("account/login", nav.currentDestination?.route)
            assertEquals(1, fixture.localLogouts)
            assertEquals(1, fixture.cleanups)
            assertEquals(1, fixture.serverLogouts)
            assertEquals(1, fixture.browserLogouts)
            assertNull(navigation.requestedHomeModalityId)
            assertNull(navigation.details.selectedBet)
            assertNull(coordinator.state.visitorReturnRoute)
            assertNull(coordinator.state.resumedHomeAction)
            assertFalse(coordinator.state.accountCompletionRequired)
            assertNull(fixture.lease)
        }
    }

    @Test
    fun completionRecheckNavigatesToRequiredDestinationAndClearsErrorOnRetry() {
        val fixture = Fixture()
        fixture.verification = PostLoginAccessResult.BlockedAccountCheckFailed
        show(fixture)
        compose.runOnIdle { coordinator.recheckAccountCompletion() }
        compose.onNodeWithText("Não foi possível validar o cadastro. Tente novamente.").assertIsDisplayed()
        compose.runOnIdle { fixture.verification = verified(PostLoginDestination.TERMS) }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle {
            assertEquals("support/terms-acceptance", nav.currentDestination?.route)
            assertTrue(coordinator.state.accountCompletionRequired)
            assertEquals(2, fixture.verifications)
        }
    }

    @Test
    fun completionRestoresSameOwnersLockedDetailsAndRetainsItsContinuation() {
        val fixture = Fixture()
        show(fixture)
        compose.runOnIdle {
            navigation.details.reconcile(fixture.lease!!.ownerKey)
            navigation.details.selectedBet = privateBet()
            navigation.details.reconcile(null)
            coordinator.state.pendingRestoredRoute = "results/confirmed-bet"
            coordinator.recheckAccountCompletion()
        }
        compose.runOnIdle {
            assertEquals(DetailRestorationOutcome.Restored, navigation.details.restorationOutcome)
            assertEquals(10L, navigation.details.selectedBet?.id)
            assertNull(coordinator.state.pendingRestoredRoute)
            assertEquals("results/confirmed-bet", coordinator.state.visitorReturnRoute)
            assertEquals(fixture.lease!!.ownerKey, coordinator.state.visitorReturnOwnerKey)
            assertEquals("home", nav.currentDestination?.route)
        }
    }

    @Test
    fun authenticatingAsAnotherOwnerCannotResumeAStoredPrizeContinuation() {
        val fixture = Fixture()
        show(fixture)
        val previous = fixture.lease!!
        lateinit var previousCheck: () -> Boolean
        lateinit var previousKey: String
        compose.runOnIdle {
            previousCheck = coordinator.isPrivateSessionCurrent
            previousKey = coordinator.privateEntryKey
            coordinator.state.visitorReturnRoute = "retail/prize/10"
            coordinator.state.visitorReturnOwnerKey = previous.ownerKey
            fixture.lease = AuthenticatedSessionIdentity("b".repeat(64))
        }
        compose.runOnIdle {
            assertFalse(previousCheck())
            assertTrue(coordinator.isPrivateSessionCurrent())
            assertNotEquals(previousKey, coordinator.privateEntryKey)
            coordinator.onAuthenticated("home")
        }
        compose.runOnIdle {
            assertEquals("home", nav.currentDestination?.route)
            assertNull(coordinator.state.visitorReturnRoute)
        }
    }

    @Test
    fun currentExpiryAcknowledgementRestartsSessionAndClearsSavedMarker() {
        val fixture = Fixture()
        show(fixture)
        compose.runOnIdle {
            fixture.tokens.replace(token(99))
            coordinator.state.foregroundExpiryMarker = fixture.tokens.rememberForegroundNotice(fixture.tokens.foregroundCheck(100))
        }
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            assertEquals(1, restarts)
            assertEquals("account/login", nav.currentDestination?.route)
            assertNull(coordinator.state.foregroundExpiryMarker)
            assertNull(fixture.tokens.token())
        }
    }

    @Test
    fun loginNavigationDismissesAnOutstandingExpiryNoticeWithoutRestartingAgain() {
        val fixture = Fixture()
        show(fixture)
        compose.runOnIdle {
            fixture.tokens.replace(token(99))
            coordinator.state.foregroundExpiryMarker = fixture.tokens.rememberForegroundNotice(fixture.tokens.foregroundCheck(100))
        }
        compose.onNodeWithText("Entendi").assertIsDisplayed()
        compose.runOnIdle { nav.navigate("account/login") }
        compose.onNodeWithText("Entendi").assertDoesNotExist()
        compose.runOnIdle {
            assertNull(coordinator.state.foregroundExpiryMarker)
            assertEquals(0, restarts)
        }
    }

    @Test
    fun processRestoredDetailsStayLockedUntilTheSameOwnerAuthenticates() {
        val fixture = Fixture(authenticated = false)
        show(fixture, "results/confirmed-bet")
        compose.runOnIdle {
            navigation.details.reconcile("a".repeat(64))
            navigation.details.selectedBet = privateBet()
            navigation.details.reconcile(null)
        }
        compose.runOnIdle {
            assertEquals("account/login", nav.currentDestination?.route)
            assertEquals("results/confirmed-bet", coordinator.state.pendingRestoredRoute)
            assertNull(navigation.details.selectedBet)
            fixture.lease = AuthenticatedSessionIdentity("a".repeat(64))
        }
        compose.runOnIdle { coordinator.onAuthenticated("home") }
        compose.runOnIdle {
            assertEquals("results/confirmed-bet", nav.currentDestination?.route)
            assertEquals(10L, navigation.details.selectedBet?.id)
            assertEquals(DetailRestorationOutcome.Restored, navigation.details.restorationOutcome)
            assertNull(coordinator.state.pendingRestoredRoute)
        }
    }

    @Test
    fun authenticatedVisitorOpensFeatureWithoutRenderingHomeAndHeaderReturnsHome() {
        val fixture = Fixture()
        show(fixture, "account/login")
        compose.runOnIdle {
            coordinator.state.visitorReturnRoute = "cart/current"
            coordinator.onAuthenticated("home")
        }
        compose.runOnIdle {
            assertEquals("cart/current", nav.currentDestination?.route)
            assertEquals(0, homeCompositions)
            assertEquals("home", nav.previousBackStackEntry?.destination?.route)
        }
        compose.onNodeWithText("Voltar pelo header").performClick()
        compose.runOnIdle {
            assertEquals("home", nav.currentDestination?.route)
            assertTrue(homeCompositions > 0)
            assertNull(nav.previousBackStackEntry)
        }
    }

    @Test
    fun personalDataContinuationKeepsInputsCheckboxAndHeaderWorking() {
        val fixture = Fixture()
        val model = br.gov.caixa.loterias.feature.account.presentation.viewmodel.PersonalDataViewModel(
            br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase {
                br.gov.caixa.loterias.core.common.result.AppResult.Success(
                    br.gov.caixa.loterias.feature.account.domain.model.AccountProfile(
                        "12345678901", "Synthetic Person", "fixture@example.test", "70000000",
                        br.gov.caixa.loterias.feature.account.domain.model.Gender.MALE,
                        br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId(7, 1, 0),
                        "DF", "Brasília", BigDecimal("100"), BigDecimal("10"), BigDecimal("200"), false
                    )
                )
            },
            br.gov.caixa.loterias.feature.account.domain.usecase.ResolveZipCodeUseCase { error("No CEP lookup requested") },
            br.gov.caixa.loterias.feature.account.domain.usecase.UpdatePersonalDataUseCase { _, _ ->
                error("Never submit profile in this test")
            },
            FixedCoroutineDispatchers(Dispatchers.Main.immediate)
        )
        show(fixture, "account/login", model)
        compose.runOnIdle {
            coordinator.state.visitorReturnRoute = "account/personal-data"
            coordinator.onAuthenticated("home")
        }
        compose.onNodeWithText("70000-000").performScrollTo().performTextReplacement("700001")
        compose.onNodeWithText("70000-1").assertExists()
        compose.onNodeWithText("R$ 100,00").performScrollTo().performTextReplacement("25000")
        compose.onNodeWithText("R$ 250,00").assertExists()
        compose.onNodeWithText("Aceito receber notificações de Loterias.").performScrollTo().performClick()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            assertEquals("700001", model.state.value.zipCode)
            assertEquals("R$ 250,00", model.state.value.dailyLimit)
            assertTrue(model.state.value.acceptsNotifications)
        }
        compose.onNodeWithText("Aceito receber notificações de Loterias.").performClick()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertFalse(model.state.value.acceptsNotifications) }
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle { assertEquals("home", nav.currentDestination?.route) }
    }

    @Test
    fun requiredRegistrationRetainsContinuationAndHomeActionsResumeWithinHome() {
        val fixture = Fixture()
        show(fixture)
        compose.runOnIdle {
            coordinator.state.visitorReturnRoute = "cart/current"
            coordinator.onAuthenticated("account/register")
        }
        compose.runOnIdle {
            assertEquals("account/register", nav.currentDestination?.route)
            assertTrue(coordinator.state.accountCompletionRequired)
            assertEquals("cart/current", coordinator.state.visitorReturnRoute)
            coordinator.state.visitorReturnRoute = "home/pool"
            coordinator.onAuthenticated("home")
        }
        compose.runOnIdle {
            assertEquals("home", nav.currentDestination?.route)
            assertEquals("home/pool", coordinator.state.resumedHomeAction)
            assertFalse(coordinator.state.accountCompletionRequired)
            assertNull(coordinator.state.visitorReturnRoute)
        }
    }

    private fun show(
        fixture: Fixture,
        start: String = "home",
        personalData: br.gov.caixa.loterias.feature.account.presentation.viewmodel.PersonalDataViewModel? = null,
    ) {
        val dependencies =
            AppSessionDependencies(fixture, ForegroundSessionBackgroundState(), Clock.fixed(Instant.ofEpochSecond(100), ZoneOffset.UTC))
        compose.setContent {
            nav = rememberNavController()
            navigation = rememberAppNavigationState("coordinator-fixture")
            CaixaDSTheme {
                coordinator = rememberAppSessionCoordinator(dependencies, nav, navigation, fixture.lease, { restarts++ })
                NavHost(nav, startDestination = start) {
                    listOf(
                        "home",
                        "account/login",
                        "cart/current",
                        "account/personal-data",
                        "account/register",
                        "support/terms-acceptance",
                        "responsible/start/required",
                        "results/confirmed-bet",
                        "retail/prize/10"
                    ).forEach { route ->
                        composable(route) {
                            if (route == "home") androidx.compose.runtime.SideEffect { homeCompositions++ }
                            if (route == "account/personal-data" && personalData != null) {
                                br.gov.caixa.loterias.feature.account.presentation.route.PersonalDataRoute(
                                    personalData,
                                    { nav.popBackStack() },
                                    {}
                                )
                            } else {
                                Text(route)
                            }
                            if (route == "cart/current") {
                                androidx.compose.material3.TextButton(onClick = { nav.popBackStack() }) {
                                    Text("Voltar pelo header")
                                }
                            }
                        }
                    }
                    composable(br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation.pattern) { Text("Authorization requested") }
                }
            }
        }
    }

    private fun privateBet() = ConfirmedBet(10, "Mega-Sena", 1, "", "Pendente", listOf(1))

    internal class Fixture(authenticated: Boolean = true) : AccountSessionDependencies {
        var lease by mutableStateOf(if (authenticated) AuthenticatedSessionIdentity("a".repeat(64)) else null)
        val tokens = SessionTokenStore().apply { if (authenticated) replace(token(1000)) }
        var localLogouts = 0
        var cleanups = 0
        var serverLogouts = 0
        var browserLogouts = 0
        var verifications = 0
        var verification: PostLoginAccessResult = verified(PostLoginDestination.HOME)
        override val authenticatedSessionIdentity = object : AuthenticatedSessionIdentityProvider {
            override fun captureIdentity() = lease
            override fun isCurrent(identity: AuthenticatedSessionIdentity) = lease === identity
        }
        override val authentication = object : AuthenticationGateway {
            override fun createAuthorizationIntent(): Intent = error("Coordinator must not initiate browser authorization")
            override suspend fun completeAuthorization(result: Intent?) = error("Coordinator must not exchange credentials")
            override fun cancelAuthorization() = Unit
            override fun logout() {
                localLogouts++
                lease = null
                tokens.clear()
            }
        }
        override val endSession = object : EndSessionGateway {
            override fun prepare(): EndSessionLaunch {
                browserLogouts++
                return EndSessionLaunch()
            }
            override fun intentFor(launch: EndSessionLaunch): Intent = error("Browser launch belongs to LogoutOverlay")
            override fun complete(launch: EndSessionLaunch, result: Intent?) = EndSessionOutcome.UNCONFIRMED
            override fun discard(launch: EndSessionLaunch) = Unit
        }
        override val clearLocalSessionData = ClearLocalSessionDataUseCase { cleanups++ }
        override val resumeLocalSessionCleanup = ResumeLocalSessionCleanupUseCase { LocalSessionRecovery.NONE_PENDING }
        override val signOutSession = SignOutSessionUseCase {
            serverLogouts++
            SessionSignOutResult.Confirmed(requireNotNull(lease))
        }
        override val verifyPostLoginAccess = VerifyPostLoginAccessUseCase {
            verifications++
            verification
        }
        override val authenticatedSessionContent = tokens
        override val currentUserIdentity = CurrentUserIdentityProvider { null }
        override val dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate)
        override val sessionIdentityChanges = MutableStateFlow(lease)
        override val foregroundSession = tokens
    }

    companion object {
        /** Fronteira estrita de funcionalidade inativa; chamar qualquer dependência é uma falha de isolamento do hospedeiro. */
        private inline fun <reified T> inactiveFeature(): T = java.lang.reflect.Proxy.newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java)
        ) { _, method, _ -> error("Inactive feature dependency accessed: ${T::class.java.simpleName}.${method.name}") } as T

        private fun verified(destination: PostLoginDestination) =
            PostLoginAccessResult.Verified(AccountAccess(destination, false, BigDecimal.ZERO))
        private fun token(expiration: Long): String = "e30." + Base64.getUrlEncoder().withoutPadding()
            .encodeToString("{\"exp\":$expiration}".toByteArray()) + ".signature"
    }
}
