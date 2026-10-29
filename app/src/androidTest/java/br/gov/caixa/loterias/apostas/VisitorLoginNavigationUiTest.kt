package br.gov.caixa.loterias.apostas

import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.gov.caixa.loterias.core.navigation.workflow.VisitorLoginCallbacks
import br.gov.caixa.loterias.core.navigation.workflow.openResponsibleAssessment
import br.gov.caixa.loterias.core.navigation.workflow.openVisitorDrawerDestination
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VisitorLoginNavigationUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private lateinit var nav: NavHostController
    private var request: String? = "account/personal-data"
    private var continuation: String? = null
    private var protectedRoute = "account/personal-data"

    private fun show(openPersonal: Boolean) {
        compose.setContent {
            nav = rememberNavController()
            NavHost(nav, startDestination = "home") {
                composable("home") { Text("Home") }
                composable("responsible/start") { Text("Welcome") }
                listOf("account/personal-data", "tickets/my-bets", "tickets/purchases", "betting/favorites", "cart/favorite-carts", "payment/cards", "responsible/form", "betting/rapid", "betting/rapid-list", "betting/rapid-config").forEach { route -> composable(route) { Text(route) } }
                composable(br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation.pattern) { Text("Login") }
            }
        }
        compose.runOnIdle { if (openPersonal) nav.navigate(protectedRoute) }
        compose.waitForIdle()
    }

    private fun callbacks() = VisitorLoginCallbacks(
        route = protectedRoute, navController = nav,
        pendingRequest = { request }, clearRequest = { request = null },
        continueAfterLogin = { continuation = it },
    )

    @Test fun allPrivateDrawerEntriesOpenUnderPromptAndDeclineOnceToHome() {
        show(false)
        listOf("tickets/my-bets", "tickets/purchases", "betting/favorites", "cart/favorite-carts", "payment/cards", "responsible/form", "betting/rapid", "betting/rapid-list", "betting/rapid-config").forEach { route ->
            compose.runOnIdle {
                protectedRoute = route
                openVisitorDrawerDestination(route, nav) { request = it }
            }
            compose.waitForIdle()
            compose.runOnIdle {
                assertEquals(route, nav.currentDestination?.route)
                val callback = callbacks()
                callback.onNegative()
                assertEquals("home", nav.currentDestination?.route)
                callback.onNegative()
                assertEquals("home", nav.currentDestination?.route)
            }
        }
    }

    @Test fun allPrivateDrawerAcceptsRetainContinuationAndDuplicateCallbacksCannotNavigateTwice() {
        show(false)
        listOf("tickets/my-bets", "tickets/purchases", "betting/favorites", "cart/favorite-carts", "payment/cards", "responsible/form", "betting/rapid", "betting/rapid-list", "betting/rapid-config").forEach { route ->
            compose.runOnIdle {
                protectedRoute = route
                openVisitorDrawerDestination(route, nav) { request = it }
            }
            compose.waitForIdle()
            compose.runOnIdle {
                val callback = callbacks()
                callback.onPositive()
                assertEquals(route, continuation)
                assertEquals(route, nav.previousBackStackEntry?.destination?.route)
                val login = nav.currentBackStackEntry
                callback.onPositive(); callback.onNegative()
                assertSame(login, nav.currentBackStackEntry)
                nav.popBackStack("home", false)
            }
            compose.waitForIdle()
        }
    }

    @Test fun welcomeIsPublicAndSeguirFinishesWelcomeBeforeFormPromptThenDeclineReturnsHome() {
        show(false)
        compose.runOnIdle { nav.navigate("responsible/start") }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals("responsible/start", nav.currentDestination?.route)
            openResponsibleAssessment(nav)
            protectedRoute = "responsible/form"
            request = protectedRoute
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals("responsible/form", nav.currentDestination?.route)
            assertEquals("home", nav.previousBackStackEntry?.destination?.route)
            callbacks().onNegative()
            assertEquals("home", nav.currentDestination?.route)
        }
    }

    @Test fun declineClosesOpenPersonalDestinationAndCannotPopTwice() {
        show(true)
        compose.runOnIdle {
            val callback = callbacks()
            callback.onNegative()
            assertEquals("home", nav.currentDestination?.route)
            callback.onNegative()
            assertEquals("home", nav.currentDestination?.route)
            assertNull(request)
            assertNull(continuation)
        }
    }

    @Test fun declineFromHomeDoesNotNavigateOrCloseHome() {
        show(false)
        compose.runOnIdle {
            val entry = nav.currentBackStackEntry
            callbacks().onNegative()
            assertSame(entry, nav.currentBackStackEntry)
            assertNull(request)
        }
    }

    @Test fun acceptKeepsPersonalContinuationAndNavigatesToAuthorizationOnce() {
        show(true)
        compose.runOnIdle {
            val callback = callbacks()
            callback.onPositive()
            assertEquals("account/personal-data", continuation)
            assertEquals(br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation.pattern, nav.currentDestination?.route)
            assertEquals("account/personal-data", nav.previousBackStackEntry?.destination?.route)
            val login = nav.currentBackStackEntry
            callback.onPositive()
            callback.onNegative()
            assertSame(login, nav.currentBackStackEntry)
            assertNull(request)
        }
    }
}
