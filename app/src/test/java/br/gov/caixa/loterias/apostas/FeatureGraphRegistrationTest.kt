// Literais numéricos descrevem entradas explícitas e valores esperados das fixtures.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.apostas

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import br.gov.caixa.loterias.core.navigation.action.AppNavigationActions
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.feature.account.navigation.AccountNavigationDependencies
import br.gov.caixa.loterias.feature.account.navigation.accountGraph
import br.gov.caixa.loterias.feature.betting.navigation.BettingNavigationDependencies
import br.gov.caixa.loterias.feature.betting.navigation.bettingGraph
import br.gov.caixa.loterias.feature.cart.navigation.CartNavigationDependencies
import br.gov.caixa.loterias.feature.cart.navigation.cartGraph
import br.gov.caixa.loterias.feature.home.navigation.HomeNavigationDependencies
import br.gov.caixa.loterias.feature.home.navigation.homeGraph
import br.gov.caixa.loterias.feature.marketplace.navigation.MarketplaceNavigationDependencies
import br.gov.caixa.loterias.feature.marketplace.navigation.marketplaceGraph
import br.gov.caixa.loterias.feature.notifications.navigation.NotificationsNavigationDependencies
import br.gov.caixa.loterias.feature.notifications.navigation.notificationsGraph
import br.gov.caixa.loterias.feature.onboarding.navigation.OnboardingNavigationDependencies
import br.gov.caixa.loterias.feature.onboarding.navigation.onboardingGraph
import br.gov.caixa.loterias.feature.payment.navigation.PaymentNavigationDependencies
import br.gov.caixa.loterias.feature.payment.navigation.paymentGraph
import br.gov.caixa.loterias.feature.responsiblegaming.navigation.ResponsibleGamingNavigationDependencies
import br.gov.caixa.loterias.feature.responsiblegaming.navigation.responsibleGamingGraph
import br.gov.caixa.loterias.feature.results.navigation.ResultsNavigationDependencies
import br.gov.caixa.loterias.feature.results.navigation.resultsGraph
import br.gov.caixa.loterias.feature.retail.navigation.RetailNavigationDependencies
import br.gov.caixa.loterias.feature.retail.navigation.retailGraph
import br.gov.caixa.loterias.feature.support.navigation.SupportNavigationDependencies
import br.gov.caixa.loterias.feature.support.navigation.supportGraph
import br.gov.caixa.loterias.feature.tickets.navigation.TicketsNavigationDependencies
import br.gov.caixa.loterias.feature.tickets.navigation.ticketsGraph
import java.lang.reflect.Proxy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Registrar o grafo não pode ler credenciais nem chamar um backend; os callbacks são adiados. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = android.app.Application::class)
class FeatureGraphRegistrationTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun everyFeatureRegistersUniqueDestinationsWithoutReadingSessionOrRepositories() {
        lateinit var graph: NavGraph
        compose.setContent {
            val nav = rememberNavController()
            val state = rememberAppNavigationState(null)
            graph = registerFeatures(nav, state)
        }
        compose.runOnIdle {
            val routes = graph.mapNotNull { it.route }
            assertEquals(routes.size, routes.toSet().size)
            for (route in listOf(
                "home",
                "cart/current",
                "cart/favorite-carts",
                "betting/favorite-retailers",
                "tickets/my-bets",
                "tickets/purchases",
                "notifications/messages",
                "results/confirmed-bet"
            )) {
                assertTrue("Missing registered destination: $route", route in routes)
            }
            assertTrue("All feature graphs must be included", routes.size > 60)
            assertEquals("fixture", graph.findNode(graph.startDestinationId)?.route)
        }
    }

    private fun registerFeatures(nav: NavHostController, state: AppNavigationState): NavGraph {
        val session = unused<SessionNavigationActions>()
        val actions = AppNavigationActions(
            { _, _ -> error("Unexpected entry") },
            { error("Unexpected prize") },
            { error("Unexpected redirect") },
            { error("Unexpected ticket") },
            { error("Unexpected payment") },
            { error("Unexpected bets") }
        )
        return nav.createGraph(startDestination = "fixture") {
            composable("fixture") { }
            accountGraph(unused<AccountNavigationDependencies>(), nav, session)
            bettingGraph(unused<BettingNavigationDependencies>(), nav, state, session)
            cartGraph(unused<CartNavigationDependencies>(), nav, state, session)
            homeGraph(unused<HomeNavigationDependencies>(), nav, state, session, actions)
            marketplaceGraph(unused<MarketplaceNavigationDependencies>(), nav, state, session)
            notificationsGraph(unused<NotificationsNavigationDependencies>(), nav)
            onboardingGraph(unused<OnboardingNavigationDependencies>(), nav)
            paymentGraph(unused<PaymentNavigationDependencies>(), nav, session, actions)
            responsibleGamingGraph(unused<ResponsibleGamingNavigationDependencies>(), nav, session)
            resultsGraph(unused<ResultsNavigationDependencies>(), nav, state, actions)
            retailGraph(unused<RetailNavigationDependencies>(), nav, session, actions)
            supportGraph(unused<SupportNavigationDependencies>(), nav, session)
            ticketsGraph(unused<TicketsNavigationDependencies>(), nav, state, session, actions)
        }
    }

    private inline fun <reified T> unused(): T = Proxy.newProxyInstance(
        T::class.java.classLoader,
        arrayOf(T::class.java)
    ) { _, method, _ -> error("Graph registration touched ${T::class.java.simpleName}.${method.name}") } as T
}
