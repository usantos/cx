package br.gov.caixa.loterias.apostas

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.apostas.navigation.shortcuts.navigateToResultShortcut
import br.gov.caixa.loterias.apostas.runtime.shortcuts.ResultShortcutDestination
import br.gov.caixa.loterias.apostas.runtime.shortcuts.ResultShortcutRequest
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@ResourcesMode(ResourcesMode.Mode.NATIVE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(sdk = [36], application = android.app.Application::class)
class ResultShortcutNavigationTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private lateinit var nav: NavHostController

    private fun graph() {
        compose.setFeatureContent {
            nav = rememberNavController()
            NavHost(nav, startDestination = "account/login") {
                composable("account/login") {}
                composable("home") {}
                composable("cart/current") {}
                composable("results/home/{modality}") {}
                composable("results/modalities") {}
            }
        }
    }

    @Test fun coldShortcutBuildsPublicHomeBackStack() {
        graph()
        compose.runOnIdle {
            navigateToResultShortcut(nav, ResultShortcutRequest(ResultShortcutDestination.MEGA_SENA, coldStart = true))
            assertEquals("megasena", nav.currentBackStackEntry?.arguments?.getString("modality"))
            nav.popBackStack()
            assertEquals("home", nav.currentDestination?.route)
            assertEquals(null, nav.previousBackStackEntry)
        }
    }

    @Test fun warmShortcutPreservesCartUnderneath() {
        graph()
        compose.runOnIdle {
            nav.navigate("cart/current")
            navigateToResultShortcut(nav, ResultShortcutRequest(ResultShortcutDestination.LOTOFACIL, coldStart = false))
            assertEquals("lotofacil", nav.currentBackStackEntry?.arguments?.getString("modality"))
            nav.popBackStack()
            assertEquals("cart/current", nav.currentDestination?.route)
        }
    }

    @Test fun repeatedResultsReplaceSelectionAndRefreshWithoutStacking() {
        graph()
        compose.runOnIdle {
            nav.navigate("cart/current")
            navigateToResultShortcut(nav, ResultShortcutRequest(ResultShortcutDestination.LOTECA, coldStart = false))
            assertEquals("loteca", nav.currentBackStackEntry?.arguments?.getString("modality"))
            val first = nav.currentBackStackEntry
            navigateToResultShortcut(nav, ResultShortcutRequest(ResultShortcutDestination.LOTECA, coldStart = false))
            org.junit.Assert.assertNotSame(first, nav.currentBackStackEntry)
            navigateToResultShortcut(nav, ResultShortcutRequest(ResultShortcutDestination.ALL, coldStart = false))
            assertEquals("results/modalities", nav.currentDestination?.route)
            nav.popBackStack()
            assertEquals("cart/current", nav.currentDestination?.route)
        }
    }
}
