package br.gov.caixa.loterias.apostas

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.navigation.action.returnToModalitiesHome
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class CartHomeNavigationUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private lateinit var controller: NavHostController

    @Test fun newBetReturnsHomeFromEveryCartOriginAndClearsTheChildScreens() {
        showGraph("home")
        listOf("betting/game/2", "betting/pools", "betting/pool-detail/B123", "tickets/purchase-detail").forEach { origin ->
            compose.runOnIdle {
                controller.navigate(origin)
                controller.navigate("cart/current")
            }
            compose.runOnIdle { controller.returnToModalitiesHome() }
            compose.runOnIdle {
                assertEquals("home", controller.currentDestination?.route)
                assertNull(controller.previousBackStackEntry)
            }
        }
    }

    @Test fun cartOpenedDirectlyCreatesHomeWithoutLeavingCartBehind() {
        showGraph("cart/current")
        compose.runOnIdle { controller.returnToModalitiesHome() }
        compose.runOnIdle {
            assertEquals("home", controller.currentDestination?.route)
            assertNull(controller.previousBackStackEntry)
        }
    }

    private fun showGraph(start: String) {
        compose.setContent {
            val nav = rememberNavController()
            SideEffect { controller = nav }
            NavHost(nav, startDestination = start) {
                listOf("home", "cart/current", "betting/game/2", "betting/pools", "betting/pool-detail/B123", "tickets/purchase-detail")
                    .forEach { route -> composable(route) {} }
            }
        }
        compose.waitForIdle()
    }
}
