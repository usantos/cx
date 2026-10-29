package br.gov.caixa.loterias.feature.marketplace.navigation

import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.gov.caixa.loterias.core.navigation.destination.AppDestination
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.activeCartSummary
import br.gov.caixa.loterias.core.navigation.state.activeResumingCartSummary
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.marketplace.presentation.route.MarketplaceDetailRoute
import br.gov.caixa.loterias.feature.marketplace.presentation.route.MarketplaceRoute
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplacePoolConfirmation
import br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel.MarketplaceDetailViewModel
import br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel.MarketplaceViewModel
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.route.MarketplaceFilterRoute
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.viewmodel.MarketplaceFilterViewModel

fun NavGraphBuilder.marketplaceGraph(
    appContainer: MarketplaceNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
    session: br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions,
) {
    marketplaceFilterGraph(appContainer, navController, navigation)
    marketplaceListsGraph(appContainer, navController, navigation, session)
    marketplaceRetailerGraph(appContainer, navController, navigation, session)
    marketplaceDetailGraph(appContainer, navController, navigation, session)
}

private fun MarketplacePoolConfirmation.poolAdditionRoute(): String = "cart/pool-included?expiresAt=${Uri.encode(expiresAt)}" +
    "&modality=${Uri.encode(modality)}" +
    "&contest=$contest" +
    "&drawDate=${Uri.encode(drawDate)}" +
    "&amount=${Uri.encode(amount.toPlainString())}"

/** Uma entrada direta ou restaurada na lista deve obter uma conta antes de ler sua localização. */
@androidx.compose.runtime.Composable
private fun marketplaceSessionReady(
    session: br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions,
    navController: NavHostController,
    returnRoute: String,
): Boolean {
    if (session.identity != null) return true
    LaunchedEffect(returnRoute) {
        session.state.visitorReturnRoute = returnRoute
        session.state.visitorReturnOwnerKey = null
        navController.navigate(br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation.authorize) {
            popUpTo(returnRoute) { inclusive = true }
            launchSingleTop = true
        }
    }
    return false
}

private fun NavGraphBuilder.marketplaceFilterGraph(
    appContainer: MarketplaceNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
) {
    composable(AppDestination.MARKETPLACE_FILTER.route) {
        val filterRetailer = navigation.details.selectedMarketplaceRetailer.takeIf {
            navController.previousBackStackEntry?.destination?.route == "betting/pool-view"
        }
        val filterEntry = marketplaceFilterEntry(
            if (filterRetailer != null) navigation.details.retailerMarketplaceFilter else navigation.details.marketplaceFilter,
            if (filterRetailer != null) MarketplaceFilter.empty() else navigation.details.marketplaceClearFilter,
            navigation.details.lastAppliedFilterRetailer,
            filterRetailer
        )
        val filterViewModel = viewModel {
            MarketplaceFilterViewModel(
                appContainer.marketplaceFilterParameters,
                appContainer.bettingParameters,
                appContainer.searchRetailers,
                appContainer.dispatchers,
                initialFilter = filterEntry.initialFilter,
                clearFilter = filterEntry.clearFilter,
                lotteryEnabled = filterEntry.retailer == null,
                initialRetailer = filterEntry.initialRetailer,
                getSpecialContests = appContainer.getSpecialContests
            )
        }
        val cartSummary = activeCartSummary(appContainer.currentCart)
        MarketplaceFilterRoute(
            viewModel = filterViewModel,
            cartTotal = cartSummary?.total ?: java.math.BigDecimal.ZERO,
            cartBetCount = cartSummary?.betCount ?: 0,
            onCart = { navController.navigate("cart/current") },
            onBack = navController::popBackStack,
            onBackReset = { filter ->
                navigation.marketplaceRevision++
                if (filterEntry.retailer != null) {
                    navigation.details.retailerMarketplaceFilter = MarketplaceFilter.empty().withLottery(filterEntry.retailer.id.toString())
                    navigation.details.retailerMarketplaceFilterApplied = false
                } else {
                    navigation.details.marketplaceFilter = filter
                    navigation.details.marketplaceFilterApplied = false
                    navigation.details.lastAppliedFilterRetailer = null
                }
            },
            onApply = { filter ->
                if (filterEntry.retailer != null) {
                    navigation.details.retailerMarketplaceFilter = filter
                    navigation.details.retailerMarketplaceFilterApplied = true
                } else {
                    navigation.details.marketplaceFilter = filter
                    navigation.details.marketplaceFilterApplied = true
                    navigation.details.lastAppliedFilterRetailer = filterViewModel.state.value.selectedRetailer
                }
                navController.navigate(filterEntry.resultDestination) {
                    popUpTo(filterEntry.resultDestination) { inclusive = true }
                }
            }
        )
    }
}

private fun NavGraphBuilder.marketplaceListsGraph(
    appContainer: MarketplaceNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
    session: br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions,
) {
    listOf("betting/pools", "betting/favorite-retailers").forEach { route ->
        composable(route) {
            if (!marketplaceSessionReady(session, navController, route)) return@composable
            val marketplaceCartSummary = activeResumingCartSummary(appContainer.currentCart)
            val marketplaceViewModel =
                viewModel(
                    key = "marketplace-$route-${session.privateEntryKey}-" +
                        "${navigation.details.marketplaceFilter.hashCode()}-${navigation.details.marketplaceFilterApplied}"
                ) {
                    MarketplaceViewModel(
                        appContainer.searchMarketplace,
                        navigation.details.marketplaceFilter,
                        appContainer.addMarketplacePoolToCart,
                        appContainer.favoriteRetailers,
                        appContainer.setRetailerFavorite,
                        appContainer.dispatchers,
                        getBettingParameters = appContainer.bettingParameters,
                        getSpecialContests = appContainer.getSpecialContests,
                        filteredSearch = navigation.details.marketplaceFilterApplied,
                        initialTab = if (route == "betting/favorite-retailers") {
                            br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceTab.FAVORITE_RETAILERS
                        } else {
                            br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceTab.POOLS
                        }
                    )
                }
            MarketplaceRoute(
                viewModel = marketplaceViewModel,
                refreshRevision = navigation.marketplaceRevision,
                cartTotal = marketplaceCartSummary?.total ?: java.math.BigDecimal.ZERO,
                cartBetCount = marketplaceCartSummary?.betCount ?: 0,
                onCart = { navController.navigate("cart/current") },
                onBack = navController::popBackStack,
                onFilter = { navController.navigate(AppDestination.MARKETPLACE_FILTER.route) },
                onRetailerSearch = { navController.navigate("retail/search") },
                onHelp = { navController.navigate("support/terms?tab=FAQ") },
                onRetailerSelected = { retailer ->
                    navigation.details.selectedMarketplaceRetailer = retailer
                    navigation.details.retailerMarketplaceFilterApplied = false
                    navigation.details.retailerMarketplaceFilter = MarketplaceFilter.empty().withLottery(retailer.id.toString())
                    navController.navigate("betting/pool-view")
                },
                onPoolSelected = { code ->
                    val pool = marketplaceViewModel.state.value.pools.firstOrNull { it.code == code }
                    navController.navigate(
                        "betting/pool-detail/${Uri.encode(code)}?favoriteVisible=true&retailerFavorite=${pool?.favoriteLottery == true}"
                    )
                },
                onPoolReserved = { confirmation ->
                    navigation.marketplaceRevision++
                    navController.navigate(confirmation.poolAdditionRoute())
                }
            )
        }
    }
}

private fun NavGraphBuilder.marketplaceRetailerGraph(
    appContainer: MarketplaceNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
    session: br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions,
) {
    composable("betting/pool-view") {
        if (!marketplaceSessionReady(session, navController, "betting/pool-view")) return@composable
        val marketplaceCartSummary = activeResumingCartSummary(appContainer.currentCart)
        val retailerFilter = navigation.details.selectedMarketplaceRetailer
            ?.let { navigation.details.retailerMarketplaceFilter.withLottery(it.id.toString()) }
            ?: navigation.details.retailerMarketplaceFilter
        val marketplaceViewModel =
            viewModel(
                key = "marketplace-pool-view-${session.privateEntryKey}-${retailerFilter.hashCode()}-" +
                    "${navigation.details.retailerMarketplaceFilterApplied}"
            ) {
                MarketplaceViewModel(
                    appContainer.searchMarketplace,
                    retailerFilter,
                    appContainer.addMarketplacePoolToCart,
                    appContainer.favoriteRetailers,
                    appContainer.setRetailerFavorite,
                    appContainer.dispatchers,
                    getBettingParameters = appContainer.bettingParameters,
                    getSpecialContests = appContainer.getSpecialContests,
                    filteredSearch = navigation.details.retailerMarketplaceFilterApplied
                )
            }
        MarketplaceRoute(
            viewModel = marketplaceViewModel,
            refreshRevision = navigation.marketplaceRevision,
            cartTotal = marketplaceCartSummary?.total ?: java.math.BigDecimal.ZERO,
            cartBetCount = marketplaceCartSummary?.betCount ?: 0,
            onCart = { navController.navigate("cart/current") },
            onBack = {
                navigation.details.selectedMarketplaceRetailer = null
                navController.popBackStack()
            },
            onFilter = { navController.navigate(AppDestination.MARKETPLACE_FILTER.route) },
            onRetailerSearch = { navController.navigate("retail/search") },
            onHelp = { navController.navigate("support/terms?tab=FAQ") },
            onRetailerSelected = {},
            onPoolSelected = { code ->
                val pool = marketplaceViewModel.state.value.pools.firstOrNull { it.code == code }
                navController.navigate(
                    "betting/pool-detail/${Uri.encode(code)}?favoriteVisible=true&retailerFavorite=${pool?.favoriteLottery == true}"
                )
            },
            onPoolReserved = { confirmation ->
                navigation.marketplaceRevision++
                navController.navigate(confirmation.poolAdditionRoute())
            },
            retailerView = true
        )
    }
}

private fun NavGraphBuilder.marketplaceDetailGraph(
    appContainer: MarketplaceNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
    session: br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions,
) {
    composable(
        route = "betting/pool-detail/{poolCode}?favoriteVisible={favoriteVisible}&retailerFavorite={retailerFavorite}",
        arguments = listOf(
            navArgument("poolCode") { type = NavType.StringType },
            navArgument("favoriteVisible") {
                type = NavType.BoolType
                defaultValue = false
            },
            navArgument("retailerFavorite") {
                type = NavType.BoolType
                defaultValue = false
            }
        )
    ) { entry ->
        val poolCode = requireNotNull(entry.arguments?.getString("poolCode"))
        val detailReturnRoute = "betting/pool-detail/${Uri.encode(poolCode)}" +
            if (entry.arguments?.getBoolean("favoriteVisible") == true) {
                "?favoriteVisible=true&retailerFavorite=${entry.arguments?.getBoolean("retailerFavorite") == true}"
            } else {
                ""
            }
        if (!marketplaceSessionReady(session, navController, detailReturnRoute)) return@composable
        val detailCartSummary = activeResumingCartSummary(appContainer.currentCart)
        val detailViewModel = viewModel(key = "pool-detail-${session.privateEntryKey}-$poolCode") {
            MarketplaceDetailViewModel(
                poolCode,
                appContainer.marketplaceDetail,
                appContainer.addMarketplacePoolToCart,
                appContainer.dispatchers,
                setRetailerFavorite = appContainer.setRetailerFavorite,
                getSpecialContests = appContainer.getSpecialContests,
                initialFavorite = if (entry.arguments?.getBoolean("favoriteVisible") == true) {
                    entry.arguments?.getBoolean("retailerFavorite") ?: false
                } else {
                    null
                }
            )
        }
        ObserveMarketplaceFavorite(detailViewModel, navigation)
        MarketplaceDetailRoute(
            viewModel = detailViewModel,
            onSoldOut = {
                navigation.marketplaceRevision++
                if (!navController.popBackStack("betting/pools", inclusive = false)) {
                    navController.navigate("betting/pools") {
                        popUpTo(detailReturnRoute) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            cartTotal = detailCartSummary?.total ?: java.math.BigDecimal.ZERO,
            cartBetCount = detailCartSummary?.betCount ?: 0,
            onCart = { navController.navigate("cart/current") },
            onBack = navController::popBackStack,
            onPoolReserved = { confirmation ->
                navigation.marketplaceRevision++
                navController.navigate(confirmation.poolAdditionRoute())
            }
        )
    }
}

@androidx.compose.runtime.Composable
private fun ObserveMarketplaceFavorite(detailViewModel: MarketplaceDetailViewModel, navigation: AppNavigationState) {
    val detailState by detailViewModel.state.collectAsStateWithLifecycle()
    var lastObservedFavorite by remember(detailViewModel) { mutableStateOf(detailState.favoriteLottery) }
    LaunchedEffect(detailState.favoriteLottery) {
        detailState.favoriteLottery?.let { current ->
            if (lastObservedFavorite != null && lastObservedFavorite != current) navigation.marketplaceRevision++
            lastObservedFavorite = current
        }
    }
}
