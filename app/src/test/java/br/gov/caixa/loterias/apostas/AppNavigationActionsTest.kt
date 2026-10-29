package br.gov.caixa.loterias.apostas

import android.app.Application
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.gov.caixa.loterias.apostas.navigation.action.appNavigationActions
import br.gov.caixa.loterias.apostas.navigation.session.AppSessionCoordinator
import br.gov.caixa.loterias.apostas.navigation.session.AppSessionDependencies
import br.gov.caixa.loterias.apostas.navigation.session.rememberAppSessionCoordinator
import br.gov.caixa.loterias.core.auth.foreground.ForegroundSessionBackgroundState
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.navigation.action.AppNavigationActions
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
@Config(sdk = [35], application = Application::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AppNavigationActionsTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private lateinit var nav: NavHostController
    private lateinit var navigation: AppNavigationState
    private lateinit var session: AppSessionCoordinator
    private lateinit var actions: AppNavigationActions

    @Test fun prizeBetStartsFreshSimulationAndReturnsToTheRequestedHomeModality() {
        show()
        val game = game(contestType = "ESPECIAL")
        val parameters = BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 1, listOf(game))
        compose.runOnIdle {
            nav.navigate("cart/current")
            navigation.details.betToEdit = bet(1)
            actions.onPrizeBetEntry(BetEntryDecision.Ready(game, parameters))

            assertNull(navigation.details.betToEdit)
            assertSame(parameters, navigation.preparedBettingParameters)
            assertEquals(2, navigation.requestedHomeModalityId)
            assertEquals(2, nav.currentBackStackEntry?.arguments?.getInt("modalityId"))
            assertEquals(2, nav.currentBackStackEntry?.arguments?.getInt("contestTypeId"))
            assertEquals(1, nav.currentBackStackEntry?.arguments?.getInt("contestNumber"))
            assertTrue(nav.popBackStack())
            assertEquals("home", nav.currentDestination?.route)
            assertNull(nav.previousBackStackEntry)
        }
    }

    @Test fun ordinaryBetPreservesItsOriginAndReplacesPreviouslyPreparedParameters() {
        show()
        compose.runOnIdle {
            nav.navigate("cart/current")
            navigation.preparedBettingParameters = BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 1, listOf(game()))
            actions.handleBetEntry(BetEntryDecision.Ready(game()), false)

            assertNull(navigation.preparedBettingParameters)
            assertNull(navigation.requestedHomeModalityId)
            assertEquals(1, nav.currentBackStackEntry?.arguments?.getInt("contestTypeId"))
            assertTrue(nav.popBackStack())
            assertEquals("cart/current", nav.currentDestination?.route)
        }
    }

    @Test fun incompleteAccountOpensItsRequiredStepWithoutPreparingABet() {
        show()
        val destinations = listOf(
            PostLoginDestination.HOME to "home",
            PostLoginDestination.REGISTRATION to "account/register",
            PostLoginDestination.TERMS to "support/terms-acceptance",
            PostLoginDestination.SELF_ASSESSMENT to "responsible/start/required"
        )
        compose.runOnIdle {
            for ((destination, route) in destinations) {
                session.state.accountCompletionRequired = false
                actions.handleBetEntry(BetEntryDecision.CompleteAccount(destination), false)
                assertTrue(session.state.accountCompletionRequired)
                assertEquals(route, nav.currentDestination?.route)
                assertNull(navigation.preparedBettingParameters)
                actions.onPaymentMoreBets()
            }
        }
    }

    @Test fun prizeContinuationUsesTheOpenedBetAndCapturesTheAuthenticatedOwner() {
        val fixture = show()
        compose.runOnIdle {
            navigation.details.selectedBet = bet(2)
            nav.navigate("retail/prize/1")
            actions.onPrizeRedirect(ServiceRedirect.REGISTER_ACCOUNT)
            assertEquals("account/register", nav.currentDestination?.route)
            assertEquals("retail/prize/1", session.state.visitorReturnRoute)
            assertEquals(fixture.lease?.ownerKey, session.state.visitorReturnOwnerKey)

            session.onAuthenticated("home")
            assertEquals(1L, nav.currentBackStackEntry?.arguments?.getLong("betId"))
            assertEquals("home", nav.previousBackStackEntry?.destination?.route)
            assertNull(session.state.visitorReturnRoute)
            assertNull(session.state.visitorReturnOwnerKey)

            nav.navigate("cart/current")
            actions.onPrizeRedirect(ServiceRedirect.ACCEPT_TERMS)
            assertEquals("support/terms-acceptance", nav.currentDestination?.route)
            assertEquals("retail/prize/2", session.state.visitorReturnRoute)
            actions.onPrizeRedirect(ServiceRedirect.HOME)
            assertEquals("home", nav.currentDestination?.route)
            assertNull(nav.previousBackStackEntry)
            assertEquals("retail/prize/2", session.state.visitorReturnRoute)
        }
    }

    @Test fun ticketAccountRedirectReturnsToAFreshQueryAndKeepsItsOwner() {
        val fixture = show()
        compose.runOnIdle {
            navigation.details.selectedBet = bet(2)
            actions.onTicketServiceRedirect(ServiceRedirect.LOGIN)
            assertEquals("account/login", nav.currentDestination?.route?.substringBefore('?'))
            assertTrue(requireNotNull(nav.currentBackStackEntry?.arguments).getBoolean("authorize"))
            assertEquals("tickets/my-bets", session.state.visitorReturnRoute)
            assertEquals(fixture.lease?.ownerKey, session.state.visitorReturnOwnerKey)

            actions.onTicketServiceRedirect(ServiceRedirect.CART)
            assertEquals("cart/current", nav.currentDestination?.route)
            assertEquals("tickets/my-bets", session.state.visitorReturnRoute)
        }
    }

    @Test fun paymentAuthenticationReturnsToCartWhileOtherRedirectsPreserveTheContinuation() {
        show()
        val destinations = listOf(
            Triple(PurchaseRedirect.LOGIN, "account/login", true),
            Triple(PurchaseRedirect.REGISTER_ACCOUNT, "account/register", true),
            Triple(PurchaseRedirect.ACCEPT_TERMS, "support/terms-acceptance", true),
            Triple(PurchaseRedirect.UNAVAILABLE, "onboarding/unavailable", false),
            Triple(PurchaseRedirect.CART, "cart/current", false),
            Triple(PurchaseRedirect.FAVORITE_CARTS, "cart/favorite-carts", false)
        )
        compose.runOnIdle {
            for ((redirect, route, requiresAuthentication) in destinations) {
                session.state.visitorReturnRoute = "tickets/my-bets"
                actions.onPaymentRedirect(redirect)
                assertEquals(route, nav.currentDestination?.route?.substringBefore('?'))
                assertEquals(if (requiresAuthentication) "cart/current" else "tickets/my-bets", session.state.visitorReturnRoute)
                actions.onPaymentMoreBets()
                assertEquals("home", nav.currentDestination?.route)
                assertNull(nav.previousBackStackEntry)
            }
            assertFalse(session.state.accountCompletionRequired)
        }
    }

    private fun show(): AppSessionCoordinatorJvmTest.Fixture {
        val fixture = AppSessionCoordinatorJvmTest.Fixture()
        val dependencies = AppSessionDependencies(
            fixture,
            ForegroundSessionBackgroundState(),
            Clock.fixed(Instant.EPOCH, ZoneOffset.UTC)
        )
        compose.setContent {
            nav = rememberNavController()
            navigation = rememberAppNavigationState(fixture.lease?.ownerKey)
            session = rememberAppSessionCoordinator(dependencies, nav, navigation, fixture.lease, {})
            actions = appNavigationActions(dependencies, nav, navigation, session)
            NavHost(nav, startDestination = "home") {
                listOf(
                    "home",
                    "cart/current",
                    "account/register",
                    "support/terms-acceptance",
                    "responsible/start/required",
                    "onboarding/unavailable",
                    "cart/favorite-carts",
                    "tickets/my-bets"
                ).forEach { route -> composable(route) {} }
                composable(
                    "account/login?authorize={authorize}&silent={silent}",
                    arguments = listOf(
                        navArgument("authorize") {
                            type = NavType.BoolType
                            defaultValue = false
                        },
                        navArgument("silent") {
                            type = NavType.BoolType
                            defaultValue = false
                        }
                    )
                ) {}
                composable("retail/prize/{betId}", arguments = listOf(navArgument("betId") { type = NavType.LongType })) {}
                composable(
                    "betting/simulation/{modalityId}?contestTypeId={contestTypeId}&contestNumber={contestNumber}",
                    arguments = listOf("modalityId", "contestTypeId", "contestNumber").map { name ->
                        navArgument(name) { type = NavType.IntType }
                    }
                ) {}
            }
        }
        return fixture
    }

    private fun game(contestType: String = "NORMAL") = LotteryGameParameters(
        LotteryModality(2, "Mega-Sena", "megasena"), 1, "", 1, 1, 1, 1, emptyList(), emptyList(), contestType = contestType
    )

    private fun bet(id: Long) = ConfirmedBet(id, "Mega-Sena", 1, "", "Pendente", listOf(1))
}
