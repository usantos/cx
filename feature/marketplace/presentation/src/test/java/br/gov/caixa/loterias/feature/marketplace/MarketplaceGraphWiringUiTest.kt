// Literais numéricos descrevem conteúdos sintéticos, níveis da API da plataforma e limites esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetMarketplaceFilterParametersUseCase
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.destination.AppDestination
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.CartPoolShare
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilterParameters
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePage
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolOrder
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceDetailUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceSpecialContestsUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.SearchMarketplaceUseCase
import br.gov.caixa.loterias.feature.marketplace.navigation.MarketplaceNavigationDependenciesAdapter
import br.gov.caixa.loterias.feature.marketplace.navigation.marketplaceGraph
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetFavoriteRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SearchRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Todas as fronteiras de backend e reserva usam resultados sintéticos locais; nenhum pedido sai do processo. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w480dp-h900dp")
class MarketplaceGraphWiringUiTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var nav: NavHostController
    private lateinit var navigation: AppNavigationState
    private var loggedIn = true
    private val owner = AuthenticatedSessionIdentity("a".repeat(64))
    private val searches = mutableListOf<MarketplaceFilter>()
    private val detailCodes = mutableListOf<String>()
    private val reservations = mutableListOf<MarketplacePoolOrder>()
    private val favorites = mutableListOf<Pair<Long, Boolean>>()
    private val retailer = Retailer(10, "0001", "SYNTHETIC LOTTERY", "Synthetic Ltd", "Address", "District", "Brasília", "DF", true)
    private val pool = MarketplacePool(
        "B/1", "Mega-Sena", 3000, "Synthetic Lottery", "22/10/2026", "10", 3, 2, 6, false,
        lotteryId = 10,
        cityAndState = "Brasília - DF",
        totalShares = 10,
        regularSharePriceWithFee = BigDecimal.TEN,
        lastSharePriceWithFeeValue = BigDecimal.TEN
    )
    private val session = object : SessionNavigationActions {
        override val state = AppSessionState()
        override val identity get() = owner.takeIf { loggedIn }
        override val privateEntryKey = "synthetic-owner"
        override val isPrivateSessionCurrent = { loggedIn }
        override val onLogout = {}
        override val requestVisitorLogin: (String) -> Unit = { error("Graph handles direct login routing") }
        override val recheckAccountCompletion = {}
        override val onAuthenticated: (String) -> Unit = {}
        override val onTryNow = {}
    }
    private fun detail(code: String) = MarketplacePoolDetail(
        code, "Mega-Sena", 3000, 10, "Synthetic Lottery", "0001", "Brasília - DF", "22/10 às 20:00", "1000000",
        3,
        10,
        2,
        6,
        "9",
        "1",
        "10",
        emptyList(),
        drawDate = "22/10/2026",
        lastSharePriceWithFeeValue = BigDecimal.TEN,
        regularSharePriceWithFee = BigDecimal.TEN
    )
    private fun dependencies() = MarketplaceNavigationDependenciesAdapter(
        AddMarketplacePoolToCartUseCase { order ->
            reservations += order
            AppResult.Success(
                ShoppingCart(
                    1,
                    BigDecimal.TEN,
                    listOf(
                        CartItem(
                            8,
                            CartItemKind.POOL_SHARE,
                            "Synthetic reservation",
                            "",
                            BigDecimal.TEN,
                            poolShare = CartPoolShare(order.code, 1, 10, "2030-01-01T10:00:00Z")
                        )
                    )
                )
            )
        },
        GetBettingParametersUseCase { AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 100, emptyList())) },
        GetCurrentCartUseCase { AppResult.Success(ShoppingCart(1, BigDecimal.TEN, emptyList(), totalBetCount = 1)) },
        FixedCoroutineDispatchers(Dispatchers.Main.immediate), GetFavoriteRetailersUseCase { AppResult.Success(listOf(retailer)) },
        GetMarketplaceDetailUseCase { code ->
            detailCodes += code
            AppResult.Success(detail(code))
        },
        GetMarketplaceFilterParametersUseCase { AppResult.Success(MarketplaceFilterParameters(1, 5)) },
        SearchMarketplaceUseCase { filter, page ->
            searches += filter
            AppResult.Success(MarketplacePage(listOf(pool), page, page, 1))
        },
        SearchRetailersUseCase { AppResult.Success(listOf(retailer)) },
        SetRetailerFavoriteUseCase { id, favorite ->
            favorites +=
                id to favorite
            AppResult.Success(Unit)
        },
        GetMarketplaceSpecialContestsUseCase { AppResult.Success(MarketplaceSpecialContests()) }
    )
    private fun show() {
        val dependencies = dependencies()
        compose.setContent {
            CaixaDSTheme {
                nav = rememberNavController()
                navigation = rememberAppNavigationState(owner.ownerKey)
                NavHost(nav, "home") {
                    composable("home") { CaixaDSText("HOME FIXTURE") }
                    composable(br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation.pattern) { CaixaDSText("LOGIN FIXTURE") }
                    composable("cart/current") { CaixaDSText("CART FIXTURE") }
                    composable("retail/search") { CaixaDSText("RETAIL SEARCH FIXTURE") }
                    composable("support/terms?tab={tab}") { CaixaDSText("HELP FIXTURE") }
                    composable(
                        "cart/pool-included?expiresAt={expiresAt}&modality={modality}&contest={contest}&drawDate={drawDate}&amount={amount}"
                    ) {
                        CaixaDSText("RESERVATION FIXTURE")
                    }
                    marketplaceGraph(dependencies, nav, navigation, session)
                }
            }
        }
    }
    private fun flush() {
        compose.runOnIdle { Snapshot.sendApplyNotifications() }
        compose.waitForIdle()
    }
    private fun open(route: String) {
        compose.runOnIdle { nav.navigate(route) { popUpTo("home") } }
        flush()
    }

    @Test fun `favorite retailer entry scopes search and back clears retailer context while helper destinations preserve owner`() {
        show()
        open("betting/pools")
        compose.onNodeWithText("Lotéricas Favoritas").performClick()
        flush()
        compose.onNodeWithContentDescription("Pesquisar lotérica").performClick()
        flush()
        compose.onNodeWithText("RETAIL SEARCH FIXTURE").assertExists()
        compose.runOnIdle { nav.popBackStack() }
        flush()
        compose.onNodeWithText("Ver Bolões").performClick()
        flush()
        compose.onNodeWithText("Lotérica favorita").assertExists()
        compose.runOnIdle {
            assertEquals(retailer, navigation.details.selectedMarketplaceRetailer)
            assertEquals("10", searches.last().lottery)
        }
        compose.onNodeWithContentDescription("Ajuda").performClick()
        flush()
        compose.onNodeWithText("HELP FIXTURE").assertExists()
        compose.runOnIdle { nav.popBackStack() }
        flush()
        compose.onNodeWithContentDescription("Voltar").performClick()
        flush()
        compose.runOnIdle {
            assertNull(navigation.details.selectedMarketplaceRetailer)
            assertEquals("betting/pools", nav.currentDestination!!.route)
        }
    }

    @Test fun `typed detail decodes pool code and routes only confirmed synthetic reservation with original metadata`() {
        show()
        open("betting/pool-detail/B%2F1?favoriteVisible=true&retailerFavorite=false")
        compose.runOnIdle { assertEquals(listOf("B/1"), detailCodes) }
        compose.onNodeWithTag("pool-detail-add-to-cart").performClick()
        flush()
        compose.onNodeWithText("RESERVATION FIXTURE").assertExists()
        compose.runOnIdle {
            assertEquals(1, reservations.size)
            assertEquals("B/1", reservations.single().code)
            assertEquals(1, navigation.marketplaceRevision)
            assertEquals("Mega-Sena", nav.currentBackStackEntry!!.arguments!!.getString("modality"))
            assertEquals("3000", nav.currentBackStackEntry!!.arguments!!.getString("contest"))
        }
    }

    @Test fun `anonymous restored detail redirects before fetching data and retains exact return destination`() {
        loggedIn = false
        show()
        open("betting/pool-detail/B%2F1?favoriteVisible=true&retailerFavorite=true")
        compose.onNodeWithText("LOGIN FIXTURE").assertExists()
        compose.runOnIdle {
            assertEquals("betting/pool-detail/B%2F1?favoriteVisible=true&retailerFavorite=true", session.state.visitorReturnRoute)
            assertNull(session.state.visitorReturnOwnerKey)
            assertTrue(detailCodes.isEmpty())
            assertTrue(searches.isEmpty())
            assertTrue(reservations.isEmpty())
        }
    }

    @Test fun `pool favorite requires confirmation and detail favorite revision changes only after successful callback`() {
        show()
        open("betting/pools")
        compose.onNodeWithContentDescription("Favoritar lotérica").performClick()
        flush()
        compose.onNodeWithText("Deseja incluir Synthetic Lottery na sua lista de favoritas?").assertExists()
        compose.onNodeWithText("Cancelar").performClick()
        flush()
        compose.runOnIdle { assertTrue(favorites.isEmpty()) }
        compose.onNodeWithContentDescription("Favoritar lotérica").performClick()
        flush()
        compose.onNodeWithText("Confirmar").performClick()
        flush()
        // Favoritar com sucesso na lista abre a aba de favoritos por contrato.
        compose.onNodeWithText("Ver Bolões").assertExists()
        compose.onNodeWithText("Bolões").performClick()
        flush()
        compose.onNodeWithContentDescription("Excluir lotérica favorita").assertExists()
        compose.runOnIdle { assertEquals(listOf(10L to true), favorites) }
        // Mudanças na aba de favoritos mantêm a expansão do bolão; abra-a apenas quando necessário.
        // Selecione o texto do resumo em vez da seta decorativa aninhada em sua semântica mesclada.
        if (compose.onAllNodesWithText("Detalhes").fetchSemanticsNodes().isEmpty()) {
            compose.onNodeWithText("Synthetic Lottery").performClick()
            flush()
        }
        compose.onNodeWithText("Detalhes").performScrollTo().performClick()
        flush()
        compose.runOnIdle { assertEquals(listOf("B/1"), detailCodes) }
        compose.onNodeWithContentDescription("Excluir lotérica favorita").performScrollTo().performClick()
        flush()
        compose.onNodeWithText("Confirmar").performClick()
        flush()
        compose.runOnIdle {
            assertEquals(listOf(10L to true, 10L to false), favorites)
            assertEquals(1, navigation.marketplaceRevision)
            assertTrue(reservations.isEmpty())
        }
    }

    @Test fun `filter back resets the correct list owner and preserves retailer scoping`() {
        show()
        open("betting/pools")
        compose.onNodeWithContentDescription("Filtrar bolões").performClick()
        flush()
        compose.onNodeWithContentDescription("Voltar").performClick()
        flush()
        compose.runOnIdle {
            assertFalse(navigation.details.marketplaceFilterApplied)
            assertNull(navigation.details.lastAppliedFilterRetailer)
            assertEquals(1, navigation.marketplaceRevision)
        }
        compose.onNodeWithText("Lotéricas Favoritas").performClick()
        flush()
        compose.onNodeWithText("Ver Bolões").performClick()
        flush()
        compose.runOnIdle { nav.navigate(AppDestination.MARKETPLACE_FILTER.route) }
        flush()
        compose.onNodeWithContentDescription("Voltar").performClick()
        flush()
        compose.runOnIdle {
            assertFalse(navigation.details.retailerMarketplaceFilterApplied)
            assertEquals("10", navigation.details.retailerMarketplaceFilter.lottery)
            assertEquals(2, navigation.marketplaceRevision)
        }
    }
}
