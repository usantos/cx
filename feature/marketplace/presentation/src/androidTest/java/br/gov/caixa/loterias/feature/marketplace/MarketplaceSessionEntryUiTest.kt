package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetMarketplaceFilterParametersUseCase
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePage
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceDetailUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.SearchMarketplaceUseCase
import br.gov.caixa.loterias.feature.marketplace.navigation.MarketplaceNavigationDependencies
import br.gov.caixa.loterias.feature.marketplace.navigation.marketplaceGraph
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetFavoriteRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SearchRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** O grafo de produção deve autenticar antes de ler a localização da conta ou o carrinho. */
class MarketplaceSessionEntryUiTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var nav: NavHostController
    private var owner by mutableStateOf<AuthenticatedSessionIdentity?>(null)
    private val privateReads = AtomicInteger()
    private val session = object : SessionNavigationActions {
        override val state = AppSessionState()
        override val identity get() = owner
        override val privateEntryKey get() = owner?.ownerKey ?: "visitor"
        override val isPrivateSessionCurrent = { owner != null }
        override val onLogout = {}
        override val requestVisitorLogin: (String) -> Unit = { error("Unexpected prompt") }
        override val recheckAccountCompletion = {}
        override val onAuthenticated: (String) -> Unit = {}
        override val onTryNow = {}
    }
    private val dependencies = object : MarketplaceNavigationDependencies {
        override val dispatchers = DefaultCoroutineDispatchers
        override val currentCart = GetCurrentCartUseCase {
            privateReads.incrementAndGet()
            AppResult.Failure(AppError.Unexpected)
        }
        override val searchMarketplace = SearchMarketplaceUseCase { _, page ->
            privateReads.incrementAndGet()
            AppResult.Success(MarketplacePage(emptyList(), page, page, 0))
        }
        override val favoriteRetailers = GetFavoriteRetailersUseCase {
            privateReads.incrementAndGet()
            AppResult.Success(emptyList())
        }
        override val bettingParameters = GetBettingParametersUseCase { AppResult.Failure(AppError.Unexpected) }
        override val addMarketplacePoolToCart = AddMarketplacePoolToCartUseCase { error("Reservation forbidden") }
        override val setRetailerFavorite = SetRetailerFavoriteUseCase { _, _ -> error("Mutation forbidden") }
        override val marketplaceDetail = GetMarketplaceDetailUseCase {
            privateReads.incrementAndGet()
            AppResult.Failure(AppError.Network)
        }
        override val marketplaceFilterParameters: GetMarketplaceFilterParametersUseCase get() = error("Filter not requested")
        override val searchRetailers: SearchRetailersUseCase get() = error("Retailer search not requested")
    }
    private fun show(route: String) {
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                nav = rememberNavController()
                val navigation = rememberAppNavigationState(owner?.ownerKey)
                NavHost(nav, startDestination = route) {
                    marketplaceGraph(dependencies, nav, navigation, session)
                    composable(LoginNavigation.pattern) {
                        TextButton(onClick = {
                            owner = AuthenticatedSessionIdentity("a".repeat(64))
                            nav.navigate(session.state.visitorReturnRoute!!) {
                                popUpTo(LoginNavigation.pattern) { inclusive = true }
                            }
                        }) { Text("Autenticar para consultar bolões") }
                    }
                }
            }
        }
    }

    @Test fun directPoolEntryAuthenticatesBeforeAnyPrivateReadAndResumesAfterLogin() = checkEntry("betting/pools")

    @Test fun directRetailerPoolEntryAuthenticatesBeforeAnyPrivateReadAndResumesAfterLogin() = checkEntry("betting/pool-view")

    @Test fun directDetailEntryAuthenticatesBeforePrivateReadAndRetainsPoolCode() {
        show("betting/pool-detail/B1")
        compose.onNodeWithText("Autenticar para consultar bolões").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(0, privateReads.get())
            assertEquals("betting/pool-detail/B1", session.state.visitorReturnRoute)
        }
        compose.onNodeWithText("Autenticar para consultar bolões").performClick()
        compose.waitUntil(5_000) { privateReads.get() > 0 }
        compose.runOnIdle { assertEquals("B1", nav.currentBackStackEntry?.arguments?.getString("poolCode")) }
    }
    private fun checkEntry(route: String) {
        show(route)
        compose.onNodeWithText("Autenticar para consultar bolões").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(0, privateReads.get())
            assertEquals(route, session.state.visitorReturnRoute)
        }
        compose.onNodeWithText("Autenticar para consultar bolões").performClick()
        compose.waitUntil(5_000) { privateReads.get() > 0 }
        compose.runOnIdle { assertEquals(route, nav.currentDestination?.route) }
        compose.onNodeWithText("Não foi possível carregar os bolões. Tente novamente.").assertDoesNotExist()
    }
}
