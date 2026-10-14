package br.gov.caixa.loterias.apostas

import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
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

    private fun show(openPersonal: Boolean) {
        compose.setContent {
            nav = rememberNavController()
            NavHost(nav, startDestination = "home") {
                composable("home") { Text("Home") }
                composable("account/personal-data") { Text("Dados pessoais") }
                composable("account/login?authorize={authorize}") { Text("Login") }
            }
        }
        compose.runOnIdle { if (openPersonal) nav.navigate("account/personal-data") }
        compose.waitForIdle()
    }

    private fun callbacks() = VisitorLoginCallbacks(
        route = "account/personal-data", navController = nav,
        pendingRequest = { request }, clearRequest = { request = null },
        continueAfterLogin = { continuation = it },
    )

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
            assertEquals("account/login?authorize={authorize}", nav.currentDestination?.route)
            assertEquals("account/personal-data", nav.previousBackStackEntry?.destination?.route)
            val login = nav.currentBackStackEntry
            callback.onPositive()
            callback.onNegative()
            assertSame(login, nav.currentBackStackEntry)
            assertNull(request)
        }
    }
}
