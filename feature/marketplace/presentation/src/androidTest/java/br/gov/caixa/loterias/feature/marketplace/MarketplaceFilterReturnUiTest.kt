package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.runtime.*
import androidx.navigation.compose.composable
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel.MarketplaceViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetMarketplaceFilterParametersUseCase
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.PoolModalityAvailability
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilterParameters
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePage
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.*
import br.gov.caixa.loterias.feature.marketplace.navigation.MarketplaceNavigationDependencies
import br.gov.caixa.loterias.feature.marketplace.navigation.marketplaceGraph
import br.gov.caixa.loterias.feature.retail.domain.usecase.*
import java.math.BigDecimal
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Exercita o retorno do destino real de filtro ao destino de lista retido. */
class MarketplaceFilterReturnUiTest {
    @get:Rule val compose = createComposeRule()
    private val pages = CopyOnWriteArrayList<Int>()
    private val favoriteReads = java.util.concurrent.atomic.AtomicInteger()
    private val failSearch = AtomicBoolean(false)
    private val searchEvents = CopyOnWriteArrayList<String>()
    private lateinit var testNavigation: AppNavigationState
    private lateinit var testNavController: NavHostController
    private val session = object : SessionNavigationActions {
        override val state = AppSessionState()
        override val identity = AuthenticatedSessionIdentity("a".repeat(64))
        override val privateEntryKey get() = identity.ownerKey
        override val isPrivateSessionCurrent = { true }
        override val onLogout = {}
        override val requestVisitorLogin: (String) -> Unit = { error("Already authenticated") }
        override val recheckAccountCompletion = {}
        override val onAuthenticated: (String) -> Unit = {}
        override val onTryNow = {}
    }
    private val dependencies = object : MarketplaceNavigationDependencies {
        override val dispatchers = DefaultCoroutineDispatchers
        override val currentCart = GetCurrentCartUseCase { AppResult.Failure(AppError.Unexpected) }
        override val searchMarketplace = SearchMarketplaceUseCase { _, page ->
            pages += page
            searchEvents += "page=$page failure=${failSearch.get()} thread=${Thread.currentThread().name}"
            if (failSearch.get()) AppResult.Failure(AppError.Network)
            else AppResult.Success(MarketplacePage(listOf(pool), page, page, 1))
        }
        override val favoriteRetailers = GetFavoriteRetailersUseCase { favoriteReads.incrementAndGet(); AppResult.Success(emptyList()) }
        override val bettingParameters = GetBettingParametersUseCase { AppResult.Success(BettingParameters(
            BigDecimal.ZERO, BigDecimal.ZERO, 0, games = emptyList(),
            availablePoolModalities = listOf(PoolModalityAvailability(2, "MEGA_SENA", "NORMAL")))) }
        override val addMarketplacePoolToCart = AddMarketplacePoolToCartUseCase { error("No cart mutation") }
        override val setRetailerFavorite = SetRetailerFavoriteUseCase { _, _ -> error("No favorite mutation") }
        override val marketplaceDetail = GetMarketplaceDetailUseCase { error("Detail not requested") }
        override val marketplaceFilterParameters = GetMarketplaceFilterParametersUseCase {
            AppResult.Success(MarketplaceFilterParameters(1, 15))
        }
        override val searchRetailers = SearchRetailersUseCase { AppResult.Success(emptyList()) }
    }
    private val pool = MarketplacePool("B1", "MEGA_SENA", 2900, "Centro", "31/12/2026", "10.00",
        10, 2, 6, false, 101, "Brasília - DF", 20)

    private fun show(applied: Boolean = false) {
        compose.setContent { CaixaDSTheme {
            val nav = rememberNavController()
            val navigation = rememberAppNavigationState(session.identity.ownerKey)
            SideEffect { testNavigation = navigation; testNavController = nav }
            remember(navigation.details) {
                navigation.details.marketplaceFilter = MarketplaceFilter.forModality(2, 1)
                navigation.details.marketplaceClearFilter = MarketplaceFilter.forModality(2, 1)
                navigation.details.marketplaceFilterApplied = applied
                true
            }
            NavHost(nav, startDestination = "betting/pools") {
                marketplaceGraph(dependencies, nav, navigation, session)
                composable("retail/search") { Text("Busca de lotéricas aberta", Modifier.clickable { nav.popBackStack() }) }
            }
        } }
        compose.waitUntil(5_000) { pages.size == 1 }
        compose.waitUntil(5_000) { compose.onAllNodesWithTag("carousel-increase-B1").fetchSemanticsNodes().isNotEmpty() }
    }

    @Test fun favoritesSearchOpensRetailSearchAndReturnsToFavorites() {
        show()
        compose.onNodeWithText("Lotéricas Favoritas").performClick()
        val readsBeforeSearch = favoriteReads.get()
        compose.onNodeWithContentDescription("Pesquisar lotérica").assertIsEnabled().performClick()
        compose.onNodeWithText("Busca de lotéricas aberta").assertIsDisplayed().performClick()
        compose.waitUntil(5_000) { favoriteReads.get() > readsBeforeSearch }
        compose.onNodeWithText("Você ainda não possui nenhuma lotérica favorita.").assertIsDisplayed()
        compose.onNodeWithContentDescription("Pesquisar lotérica").assertIsEnabled()
    }

    @Test fun backWithoutEditingReloadsPageZeroAndResetsSelectedSharesEvenWhenFilterHashIsUnchanged() {
        show()
        compose.onNodeWithTag("carousel-increase-B1").performClick()
        compose.onNode(hasText("2") and hasAnyAncestor(hasTestTag("marketplace-carousel"))).assertIsDisplayed()
        compose.onNodeWithContentDescription("Filtrar bolões").performClick()
        compose.onNodeWithTag("marketplace_filter_screen").assertExists()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.waitUntil(5_000) { pages.size >= 2 }
        compose.waitUntil(5_000) {
            compose.onAllNodes(hasText("1") and hasAnyAncestor(hasTestTag("marketplace-carousel"))).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNode(hasText("1") and hasAnyAncestor(hasTestTag("marketplace-carousel"))).assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf(0, 0), pages.toList()) }
    }

    @Test fun backClearsAppliedSearchModeForTheRetainedListErrorContract() {
        show(applied = true)
        compose.onNodeWithContentDescription("Filtrar bolões").performClick()
        compose.onNodeWithTag("marketplace_filter_screen").assertExists()
        failSearch.set(true)
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.waitUntil(5_000) { pages.size >= 2 }
        val unfilteredError = "Não foram encontrados bolões disponíveis para os filtros informados. Limpe os filtros e tente novamente."
        try {
            compose.waitUntil(5_000) { compose.onAllNodesWithText(unfilteredError).fetchSemanticsNodes().isNotEmpty() }
        } catch (timeout: androidx.compose.ui.test.ComposeTimeoutException) {
            var stateSnapshot = ""
            compose.runOnIdle {
                val filter = testNavigation.details.marketplaceFilter
                val applied = testNavigation.details.marketplaceFilterApplied
                val entry = testNavController.currentBackStackEntry
                val states = listOf(false, true).associateWith { flag ->
                    runCatching {
                        val key = "marketplace-${session.privateEntryKey}-${filter.hashCode()}-$flag"
                        requireNotNull(entry)
                        ViewModelProvider(entry)[key, MarketplaceViewModel::class.java].state.value.toString()
                    }.getOrElse { "No retained VM for flag=$flag: ${it.javaClass.simpleName}" }
                }
                stateSnapshot = "route=${entry?.destination?.route} applied=$applied revision=${testNavigation.marketplaceRevision} " +
                    "filterHash=${filter.hashCode()} retainedStates=$states pages=$pages events=$searchEvents"
            }
            val roots = compose.onAllNodes(isRoot(), useUnmergedTree = true)
            val trees = roots.fetchSemanticsNodes().indices.joinToString("\n") { index ->
                "Root $index:\n${roots[index].printToString()}"
            }
            throw AssertionError("Expected unfiltered error after back. $stateSnapshot\n$trees", timeout)
        }
        compose.onNodeWithText(unfilteredError).assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf(0, 0), pages.toList()) }
    }
}
