// Literais numéricos descrevem entradas explícitas de teste e valores esperados dos dados de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.apostas

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.core.navigation.state.rememberAppSessionState
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.navigation.CartNavigationDependencies
import br.gov.caixa.loterias.feature.cart.navigation.cartGraph
import java.lang.reflect.Proxy
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = android.app.Application::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CartGraphFlowUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private lateinit var nav: NavHostController
    private var reads = 0

    @Test fun emptyCartNewBetReturnsToModalitiesWithoutMutation() {
        show("cart/current")
        compose.onNodeWithText("Seu carrinho está vazio").assertIsDisplayed()
        compose.onNodeWithText("Adicionar nova aposta").performClick()
        compose.onNodeWithText("MODALIDADES FIXTURE").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals("home", nav.currentDestination?.route)
            assertEquals(1, reads)
        }
    }

    @Test fun includedBetDecodesModalityAndReturnsToHome() {
        show("cart/included?modality=Quina&contest=7000&drawDate=22%2F10&amount=3.00&minimum=2.50")
        compose.onNodeWithText("Voltar ao início").performClick()
        compose.onNodeWithText("MODALIDADES FIXTURE").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, reads) }
    }

    @Test fun includedPoolReturnsToHomeWithoutCreatingAnotherReservation() {
        show("cart/pool-included?expiresAt=2026-10-22T16%3A00%3A00Z&modality=Mega-Sena&contest=3073&drawDate=22%2F10&amount=10")
        compose.onNodeWithText("Voltar ao início").performClick()
        compose.onNodeWithText("MODALIDADES FIXTURE").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, reads) }
    }

    @Test fun guestFavoriteCartsNeverReadPrivateDataAndCanReturn() {
        show("cart/favorite-carts")
        compose.onNodeWithText("Carrinhos Favoritos").assertIsDisplayed()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.onNodeWithText("ORIGIN FIXTURE").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, reads) }
    }

    private fun show(route: String) {
        val dependencies = dependencies()
        compose.setContent {
            nav = rememberNavController()
            val navigation = rememberAppNavigationState(null)
            val sessionState = rememberAppSessionState()
            val session = proxy<SessionNavigationActions> { name, _ ->
                when (name) {
                    "getState" -> sessionState
                    "getIdentity" -> null
                    "getPrivateEntryKey" -> "visitor"
                    "isPrivateSessionCurrent", "getIsPrivateSessionCurrent" -> ({ true })
                    else -> error("Unexpected session action $name")
                }
            }
            CaixaDSTheme {
                NavHost(nav, startDestination = "origin") {
                    composable("origin") { CaixaDSText("ORIGIN FIXTURE") }
                    composable("home") { CaixaDSText("MODALIDADES FIXTURE") }
                    cartGraph(dependencies, nav, navigation, session)
                }
            }
        }
        compose.runOnIdle { nav.navigate(route) }
        compose.waitForIdle()
    }

    private fun dependencies() = proxy<CartNavigationDependencies> { name, type ->
        when (name) {
            "getCurrentCart" -> GetCurrentCartUseCase {
                reads++
                AppResult.Success(ShoppingCart(0, BigDecimal.ZERO, emptyList()))
            }

            "getBettingParameters" -> GetBettingParametersUseCase {
                AppResult.Success(
                    BettingParameters(
                        BigDecimal.TEN,
                        BigDecimal.ONE,
                        30,
                        listOf(
                            br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters(
                                br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
                                3073, "22/10/2026", 60, 6, 20, 10, emptyList(), emptyList()
                            )
                        )
                    )
                )
            }

            "getDispatchers" -> FixedCoroutineDispatchers(Dispatchers.Main.immediate)

            "getNetworkAvailability" -> NetworkAvailability { ConnectionAvailability.CONNECTED }

            "getLocalClock" -> Clock.fixed(Instant.parse("2026-10-22T15:00:00Z"), ZoneOffset.UTC)

            "getServerTime" -> ServerTimeSource { Instant.parse("2026-10-22T15:00:00Z") }

            "getSessionIdentityChanges" -> flowOf(null)

            "getCurrentUserIdentity" -> CurrentUserIdentityProvider { null }

            "getAuthenticatedSessionIdentity" -> object : AuthenticatedSessionIdentityProvider {
                override fun captureIdentity() = null
                override fun isCurrent(identity: br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity) = false
            }

            else -> {
                check(type.isInterface) { "Unconfigured dependency $name" }
                Proxy.newProxyInstance(type.classLoader, arrayOf(type)) { _, method, _ ->
                    error("Unexpected mutation/private read $name.${method.name}")
                }
            }
        }
    }
    private inline fun <reified T> proxy(crossinline answer: (String, Class<*>) -> Any?): T =
        Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, _ ->
            answer(method.name, method.returnType)
        } as T
}
