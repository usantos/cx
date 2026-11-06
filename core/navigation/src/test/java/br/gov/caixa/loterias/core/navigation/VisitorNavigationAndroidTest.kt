package br.gov.caixa.loterias.core.navigation

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import br.gov.caixa.loterias.core.navigation.action.returnToModalitiesHome
import br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation
import br.gov.caixa.loterias.core.navigation.workflow.VisitorLoginCallbacks
import br.gov.caixa.loterias.core.navigation.workflow.openResponsibleAssessment
import br.gov.caixa.loterias.core.navigation.workflow.openVisitorDrawerDestination
import br.gov.caixa.loterias.core.navigation.workflow.opensVisitorDestinationBeforeLogin
import br.gov.caixa.loterias.core.navigation.workflow.requiresVisitorLogin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class VisitorNavigationAndroidTest {
    private class Owner : LifecycleOwner {
        override val lifecycle = LifecycleRegistry(this).apply { currentState = Lifecycle.State.RESUMED }
    }
    private fun controller(): NavHostController = NavHostController(RuntimeEnvironment.getApplication()).apply {
        setLifecycleOwner(Owner())
        setViewModelStore(ViewModelStore())
        navigatorProvider.addNavigator(ComposeNavigator())
        graph = createGraph(startDestination = "home") {
            for (route in listOf(
                "home",
                "account/personal-data",
                "responsible/start",
                "responsible/form",
                "cart",
                LoginNavigation.SILENT
            )) {
                composable(route) {
                }
            }
        }
    }

    @Test fun `favorite retailers prompts before opening and keeps the requested continuation`() {
        val navigation = controller()
        val route = "betting/favorite-retailers"
        assertTrue(requiresVisitorLogin(route))
        assertFalse(opensVisitorDestinationBeforeLogin(route))
        var pending: String? = null
        var continuation: String? = null
        openVisitorDrawerDestination(route, navigation) { pending = it }
        assertEquals("home", navigation.currentDestination!!.route)
        VisitorLoginCallbacks(route, navigation, { pending }, { pending = null }, { continuation = it }).onPositive()
        assertEquals(route, continuation)
        assertEquals(LoginNavigation.SILENT, navigation.currentDestination!!.route)
    }

    @Test fun `visitor private destination opens below login and negative action returns to home`() {
        val navigation = controller()
        var pending: String? = null
        openVisitorDrawerDestination("account/personal-data", navigation) { pending = it }
        assertEquals("account/personal-data", navigation.currentDestination!!.route)
        val callbacks =
            VisitorLoginCallbacks("account/personal-data", navigation, {
                pending
            }, { pending = null }, { error("negative must not authorize") })
        callbacks.onNegative()
        assertNull(pending)
        assertEquals("home", navigation.currentDestination!!.route)
        callbacks.onNegative()
        assertEquals("home", navigation.currentDestination!!.route)
    }

    @Test fun `positive login action clears its pending route and ignores obsolete callback`() {
        val navigation = controller()
        var pending: String? = "account/personal-data"
        var continuation: String? = null
        val stale = VisitorLoginCallbacks("payment/cards", navigation, { pending }, { pending = null }, { continuation = it })
        stale.onPositive()
        assertEquals("account/personal-data", pending)
        assertNull(continuation)
        val current = VisitorLoginCallbacks("account/personal-data", navigation, { pending }, { pending = null }, { continuation = it })
        current.onPositive()
        assertNull(pending)
        assertEquals("account/personal-data", continuation)
        assertEquals(LoginNavigation.SILENT, navigation.currentDestination!!.route)
    }

    @Test fun `public drawer only requests login and assessment removes introductory destination`() {
        val navigation = controller()
        var request: String? = null
        openVisitorDrawerDestination("public", navigation) { request = it }
        assertEquals("public", request)
        assertEquals("home", navigation.currentDestination!!.route)
        navigation.navigate("responsible/start")
        openResponsibleAssessment(navigation)
        assertEquals("responsible/form", navigation.currentDestination!!.route)
        navigation.popBackStack()
        assertEquals("home", navigation.currentDestination!!.route)
        navigation.navigate("cart")
        navigation.returnToModalitiesHome()
        assertEquals("home", navigation.currentDestination!!.route)
        navigation.returnToModalitiesHome()
        assertEquals("home", navigation.currentDestination!!.route)
    }
}
