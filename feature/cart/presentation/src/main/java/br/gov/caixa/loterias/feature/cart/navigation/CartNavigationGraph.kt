package br.gov.caixa.loterias.feature.cart.navigation

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
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.action.returnToModalitiesHome
import br.gov.caixa.loterias.core.navigation.destination.serviceRedirectRoute
import br.gov.caixa.loterias.core.navigation.policy.shouldClosePoolDetailAfterChild
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.accountCompletionRedirects
import br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.cart.presentation.route.CartAdditionRoute
import br.gov.caixa.loterias.feature.cart.presentation.route.CurrentCartRoute
import br.gov.caixa.loterias.feature.cart.presentation.route.FavoriteCartDetailRoute
import br.gov.caixa.loterias.feature.cart.presentation.route.FavoriteCartsRoute
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAdditionKind
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.CartAdditionViewModel
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.CartViewModel
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.FavoriteCartsViewModel

fun NavGraphBuilder.cartGraph(
    appContainer: CartNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
    session: SessionNavigationActions,
) {
    cartLocalReviewGraph(appContainer, navController)
    currentCartGraph(appContainer, navController, navigation, session)
    includedCartGraph(appContainer, navController)
    includedPoolCartGraph(appContainer, navController)
    favoriteCartsGraph(appContainer, navController, session)
    favoriteCartDetailGraph(appContainer, navController)
}

private fun NavGraphBuilder.cartLocalReviewGraph(appContainer: CartNavigationDependencies, navController: NavHostController) {
    composable("cart/local-review") {
        val reviewModel = viewModel {
            br.gov.caixa.loterias.feature.cart.presentation.viewmodel.LocalCartReviewViewModel(
                appContainer.authenticatedSessionIdentity,
                br.gov.caixa.loterias.feature.cart.domain.usecase.GetLocalCartReviewUseCase(
                    appContainer.authenticatedSessionIdentity,
                    appContainer.legacyCartTransfer,
                    appContainer.visitorCart,
                    appContainer.currentCart
                ),
                br.gov.caixa.loterias.feature.cart.domain.usecase.DiscardLocalCartReviewUseCase(
                    appContainer.authenticatedSessionIdentity,
                    appContainer.legacyCartTransfer,
                    appContainer.authenticatedSessionContent
                ),
                appContainer.dispatchers
            )
        }
        br.gov.caixa.loterias.feature.cart.presentation.route.LocalCartReviewRoute(
            reviewModel,
            onBack = navController::popBackStack,
            onRemoteCart = { navController.navigate("cart/current") }
        )
    }
}

private fun NavGraphBuilder.currentCartGraph(
    appContainer: CartNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
    session: SessionNavigationActions,
) {
    composable("cart/current") {
        val cartViewModel = currentCartViewModel(appContainer)
        val cartState by cartViewModel.state.collectAsStateWithLifecycle()
        var lastObservedCart by remember(cartViewModel) {
            mutableStateOf<br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart?>(null)
        }
        LaunchedEffect(cartState.cart) {
            cartState.cart?.let { current ->
                if (lastObservedCart != null && lastObservedCart != current) {
                    navigation.marketplaceRevision++
                }
                lastObservedCart = current
            }
        }
        if (cartState.parametersUnavailable) {
            appContainer.SimulationRecoveryPanel(
                loading = cartState.loadingParameters,
                message = cartState.parameterRetryMessage,
                onRetry = cartViewModel::retryParameters,
                onBack = cartViewModel::dismissParametersUnavailable,
                onDismissMessage = cartViewModel::dismissParameterRetryMessage,
                networkAvailability = appContainer.networkAvailability
            )
            return@composable
        }
        CurrentCartRoute(
            onLocationBlocked = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                }
            },
            onLogin = {
                session.state.visitorReturnRoute = "cart/current"
                navController.navigate(LoginNavigation.authorize)
            },
            onAccountRequired = { destination ->
                session.state.visitorReturnRoute = "cart/current"
                session.state.accountCompletionRequired = true
                val route = when (destination) {
                    PostLoginDestination.REGISTRATION -> "account/register"
                    PostLoginDestination.TERMS -> "support/terms-acceptance"
                    PostLoginDestination.SELF_ASSESSMENT -> "responsible/start/required"
                    PostLoginDestination.HOME -> "home"
                }
                navController.navigate(route) {
                    if (destination == PostLoginDestination.REGISTRATION) popUpTo("cart/current") { inclusive = true }
                    launchSingleTop = true
                }
            },
            viewModel = cartViewModel,
            onValidationRedirect = {
                if (it in accountCompletionRedirects) session.state.visitorReturnRoute = "cart/current"
                navController.navigate(serviceRedirectRoute(it))
            },
            onBack = navController::popBackStack,
            onContinueBetting = {
                navigation.marketplaceRevision++
                navController.returnToModalitiesHome()
            },
            onPayment = { navController.navigate("payment/method") },
            onPoolDetail = { code -> navController.navigate("betting/pool-detail/${Uri.encode(code)}") },
            onComboDetail = { id -> navController.navigate("betting/combo-detail/${id.toInt()}") }
        )
    }
}

private fun NavGraphBuilder.includedCartGraph(appContainer: CartNavigationDependencies, navController: NavHostController) {
    composable(
        route = "cart/included?modality={modality}&contest={contest}&drawDate={drawDate}&amount={amount}&minimum={minimum}",
        arguments = listOf(
            navArgument("modality") {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument("contest") {
                type = NavType.IntType
                defaultValue = 0
            },
            navArgument("drawDate") {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument("amount") {
                type = NavType.StringType
                defaultValue = "0"
            },
            navArgument("minimum") {
                type = NavType.StringType
                defaultValue = "0"
            }
        )
    ) { entry ->
        val additionViewModel = viewModel<CartAdditionViewModel> {
            CartAdditionViewModel(
                kind = CartAdditionKind.BET,
                serverTime = appContainer.serverTime,
                modality = entry.arguments?.getString("modality").orEmpty(),
                contest = entry.arguments?.getInt("contest") ?: 0,
                drawDate = entry.arguments?.getString("drawDate").orEmpty(),
                amount = entry.arguments?.getString("amount")?.toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO,
                minimumCartAmount = entry.arguments?.getString("minimum")?.toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO
            )
        }
        CartAdditionRoute(
            onBack = navController::popBackStack,
            viewModel = additionViewModel,
            onHome = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                }
            },
            onOpenCart = { navController.navigate("cart/current") },
            onHelp = { id, name -> navController.navigate("support/game-help?modalityId=$id&modalityName=${Uri.encode(name)}") }
        )
    }
}

private fun NavGraphBuilder.includedPoolCartGraph(appContainer: CartNavigationDependencies, navController: NavHostController) {
    composable(
        route = "cart/pool-included?expiresAt={expiresAt}&modality={modality}" +
            "&contest={contest}&drawDate={drawDate}&amount={amount}",
        arguments = listOf(
            navArgument("expiresAt") {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument("modality") {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument("contest") {
                type = NavType.IntType
                defaultValue = 0
            },
            navArgument("drawDate") {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument("amount") {
                type = NavType.StringType
                defaultValue = "0"
            }
        )
    ) { entry ->
        val expiresAt = entry.arguments?.getString("expiresAt").orEmpty()
        val modality = entry.arguments?.getString("modality").orEmpty()
        val contest = entry.arguments?.getInt("contest") ?: 0
        val drawDate = entry.arguments?.getString("drawDate").orEmpty()
        val amount = entry.arguments?.getString("amount").orEmpty().toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO
        val additionViewModel = viewModel(key = "cart-addition-pool-$expiresAt") {
            CartAdditionViewModel(
                serverTime = appContainer.serverTime,
                kind = CartAdditionKind.POOL_SHARE,
                expiresAt = expiresAt,
                modality = modality,
                contest = contest,
                drawDate = drawDate,
                amount = amount
            )
        }
        CartAdditionRoute(
            onBack = {
                val parent = navController.previousBackStackEntry?.destination?.route
                if (shouldClosePoolDetailAfterChild(parent, contentChanged = true)) {
                    navController.popBackStack(requireNotNull(parent), inclusive = true)
                } else {
                    navController.popBackStack()
                }
            },
            viewModel = additionViewModel,
            onHome = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                }
            },
            onOpenCart = { navController.navigate("cart/current") },
            onHelp = { id, name -> navController.navigate("support/game-help?modalityId=$id&modalityName=${Uri.encode(name)}") }
        )
    }
}

private fun NavGraphBuilder.favoriteCartsGraph(
    appContainer: CartNavigationDependencies,
    navController: NavHostController,
    session: SessionNavigationActions,
) {
    composable("cart/favorite-carts") { entry ->
        androidx.compose.runtime.key(session.privateEntryKey) {
            val favoriteCartsViewModel = viewModel(key = "favorite-carts-${session.privateEntryKey}") {
                FavoriteCartsViewModel(
                    cartId = null,
                    getFavoriteCarts = appContainer.favoriteCarts,
                    getFavoriteCartBets = appContainer.favoriteCartBets,
                    deleteFavoriteCart = appContainer.deleteFavoriteCart,
                    deleteFavoriteCartBet = appContainer.deleteFavoriteCartBet,
                    addFavoriteCartToCurrent = appContainer.addFavoriteCartToCurrent,
                    wasFavoriteCartAdded = appContainer.wasFavoriteCartAdded,
                    markFavoriteCartAdded = appContainer.markFavoriteCartAdded,
                    dispatchers = appContainer.dispatchers,
                    autoLoad = session.identity != null,
                    isSessionCurrent = session.isPrivateSessionCurrent
                )
            }
            val detailReturned by entry.savedStateHandle.getStateFlow("favoriteDetailReturned", false)
                .collectAsStateWithLifecycle()
            LaunchedEffect(detailReturned) {
                if (detailReturned) {
                    entry.savedStateHandle["favoriteDetailReturned"] = false
                    favoriteCartsViewModel.refreshAfterDetail()
                }
            }
            FavoriteCartsRoute(
                viewModel = favoriteCartsViewModel,
                privateDataEnabled = session.identity != null,
                onHelp = { navController.navigate("support/terms?tab=FAQ") },
                onBack = navController::popBackStack,
                onOpen = { navController.navigate("cart/favorite-detail/$it") },
                onCartReady = { navController.navigate("cart/current") }
            )
        }
    }
}

private fun NavGraphBuilder.favoriteCartDetailGraph(appContainer: CartNavigationDependencies, navController: NavHostController) {
    composable(
        route = "cart/favorite-detail/{cartId}",
        arguments = listOf(navArgument("cartId") { type = NavType.LongType })
    ) { entry ->
        val cartId = requireNotNull(entry.arguments).getLong("cartId")
        val favoriteCartsViewModel = viewModel(key = "favorite-cart-$cartId") {
            FavoriteCartsViewModel(
                cartId = cartId,
                getFavoriteCarts = appContainer.favoriteCarts,
                getFavoriteCartBets = appContainer.favoriteCartBets,
                deleteFavoriteCart = appContainer.deleteFavoriteCart,
                deleteFavoriteCartBet = appContainer.deleteFavoriteCartBet,
                addFavoriteCartToCurrent = appContainer.addFavoriteCartToCurrent,
                wasFavoriteCartAdded = appContainer.wasFavoriteCartAdded,
                markFavoriteCartAdded = appContainer.markFavoriteCartAdded,
                dispatchers = appContainer.dispatchers
            )
        }
        FavoriteCartDetailRoute(
            viewModel = favoriteCartsViewModel,
            onHelp = { navController.navigate("support/terms?tab=FAQ") },
            onBack = {
                navController.previousBackStackEntry?.savedStateHandle?.set("favoriteDetailReturned", true)
                navController.popBackStack()
            }
        )
    }
}

@androidx.compose.runtime.Composable
private fun currentCartViewModel(appContainer: CartNavigationDependencies): CartViewModel = viewModel {
    CartViewModel(
        checkCheckoutLocation = appContainer.checkCheckoutLocation,
        recordLocationRequest = appContainer.recordCheckoutLocationRequest,
        hasRecentPurchase = appContainer.hasRecentPurchase,
        verifyPaymentAccount = appContainer.verifyCartPaymentAccount,
        serverTime = appContainer.serverTime,
        getCurrentCart = appContainer.currentCart,
        removeCartItem = appContainer.removeCartItem,
        removeExpiredPoolShare = appContainer.removeExpiredPoolShare,
        reservationSessions = appContainer.authenticatedSessionIdentity,
        reservationSessionChanges = appContainer.sessionIdentityChanges,
        clearCart = appContainer.clearCart,
        dispatchers = appContainer.dispatchers,
        getBettingParameters = appContainer.bettingParameters,
        validateCart = appContainer.validateCart,
        validateFavoriteCartName = appContainer.validateFavoriteCartName,
        saveCurrentCartFavorite = appContainer.saveCurrentCartFavorite,
        authenticated = appContainer.currentUserIdentity.currentUser() != null,
        networkAvailability = appContainer.networkAvailability,
        localClock = appContainer.localClock
    )
}
