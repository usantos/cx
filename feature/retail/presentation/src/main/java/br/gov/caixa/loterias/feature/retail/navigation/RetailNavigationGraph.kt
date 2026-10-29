package br.gov.caixa.loterias.feature.retail.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.gov.caixa.loterias.core.navigation.action.AppNavigationActions
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision
import br.gov.caixa.loterias.feature.payment.navigation.PaymentRegistrationHost
import br.gov.caixa.loterias.feature.payment.navigation.paymentRegistrationViewModel
import br.gov.caixa.loterias.feature.retail.R
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.presentation.route.LocationGateRoute
import br.gov.caixa.loterias.feature.retail.presentation.route.PrizeRedemptionRoute
import br.gov.caixa.loterias.feature.retail.presentation.route.RetailSearchRoute
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.LocationGateViewModel
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeReceiptUseCases
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionInput
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionViewModel
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.RetailSearchViewModel
import kotlinx.coroutines.flow.first

fun NavGraphBuilder.retailGraph(
    appContainer: RetailNavigationDependencies,
    navController: NavHostController,
    session: SessionNavigationActions,
    actions: AppNavigationActions,
) {
    composable(
        route = "retail/prize/{betId}",
        arguments = listOf(navArgument("betId") { type = NavType.LongType })
    ) { entry ->
        val betId = requireNotNull(entry.arguments).getLong("betId")
        PrizeRedemptionDestination(
            appContainer = appContainer,
            betId = betId,
            keySuffix = betId.toString(),
            onForegroundHostChanged = { session.state.prizeChannelHost = it },
            onRedirect = actions.onPrizeRedirect,
            onBetEntry = actions.onPrizeBetEntry,
            onBack = navController::popBackStack,
            onHelp = { navController.navigate("support/terms?tab=FAQ") },
            onHome = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                }
            },
            onMyBets = {
                navController.navigate("tickets/my-bets") {
                    popUpTo("tickets/my-bets") { inclusive = false }
                    launchSingleTop = true
                }
            }
        )
    }
    composable("retail/location") {
        val locationViewModel = viewModel { LocationGateViewModel(appContainer.checkRetailLocation) }
        LocationGateRoute(
            viewModel = locationViewModel,
            versionName = appContainer.appVersionName,
            lotteryIconResourceId = R.drawable.icon_loteria,
            mockInsideBrazil = appContainer.mockBackendEnabled,
            onAllowed = {
                navController.navigate("retail/search") {
                    popUpTo("retail/location") { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }
    registerMockPrizeRoutes(appContainer, navController, actions)
    composable("retail/search") {
        val retailSearchViewModel = viewModel {
            RetailSearchViewModel(
                searchRetailers = appContainer.searchRetailers,
                setFavorite = appContainer.setRetailerFavorite,
                dispatchers = appContainer.dispatchers
            )
        }
        RetailSearchRoute(retailSearchViewModel, navController::popBackStack, onHelp = { navController.navigate("support/terms?tab=FAQ") })
    }
}

@Composable
private fun PrizeRedemptionDestination(
    appContainer: RetailNavigationDependencies,
    betId: Long,
    keySuffix: String,
    onRedirect: (br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect) -> Unit,
    onBetEntry: (BetEntryDecision) -> Unit,
    initialChannel: PrizeRedemptionChannel? = null,
    previewSuccessChannel: PrizeRedemptionChannel? = null,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onHome: () -> Unit,
    onMyBets: () -> Unit,
    onForegroundHostChanged: (Boolean) -> Unit = {},
) {
    val authenticatedUser = appContainer.currentUserIdentity.currentUser()
    val prizeViewModel = viewModel(key = "prize-redemption-$keySuffix") {
        PrizeRedemptionViewModel(
            input = PrizeRedemptionInput(
                betId,
                authenticatedUser?.email
                    ?: if (appContainer.mockBackendEnabled) "mock@caixa.gov.br" else "",
                authenticatedUser?.cpf
                    ?: if (appContainer.mockBackendEnabled) "12345678901" else ""
            ),
            getDetails = appContainer.prizeDetails,
            generateLotteryCode = appContainer.lotteryRedemptionCode,
            consultPixKey = appContainer.consultPixRedemptionKey,
            redeemPrize = appContainer.redeemPrize,
            cancelPix = appContainer.cancelPixRedemption,
            receiptUseCases = PrizeReceiptUseCases(appContainer.downloadBetReceipt, appContainer.downloadPrizeReceipt),
            getBettingParameters = appContainer.bettingParameters,
            prepareBetEntry = appContainer.prepareBetEntry,
            localClock = appContainer.localClock,
            networkAvailability = appContainer.networkAvailability,
            dispatchers = appContainer.dispatchers
        )
    }
    val prizeState by prizeViewModel.state.collectAsStateWithLifecycle()
    val channelHost = legacyPrizeForegroundHost(prizeState.stage)
    DisposableEffect(channelHost) {
        onForegroundHostChanged(channelHost)
        onDispose { onForegroundHostChanged(false) }
    }
    val registrationViewModel = paymentRegistrationViewModel(appContainer)
    PrizeRedemptionRoute(
        viewModel = prizeViewModel,
        onRedirect = onRedirect,
        onBetEntry = onBetEntry,
        initialChannel = initialChannel,
        previewSuccessChannel = previewSuccessChannel,
        allowControlledSuccessPreview = appContainer.mockBackendEnabled,
        onBack = onBack,
        onHelp = onHelp,
        onHome = onHome,
        onMyBets = onMyBets,
        onRegistration = { email -> registrationViewModel.start(email) }
    )
    PaymentRegistrationHost(
        registrationViewModel,
        prizeViewModel::registrationFinished,
        onRedirect,
        simulateBrowser = appContainer.mockBackendEnabled
    )
}

private fun NavGraphBuilder.registerMockPrizeRoutes(
    appContainer: RetailNavigationDependencies,
    navController: NavHostController,
    actions: AppNavigationActions,
) {
    mapOf(
        "retail/prize" to Triple<PrizeRedemptionChannel?, PrizeRedemptionChannel?, String>(null, null, "options"),
        "retail/prize-guidance" to Triple(PrizeRedemptionChannel.LOTTERY, null, "guidance"),
        "retail/prize-lottery" to Triple(PrizeRedemptionChannel.LOTTERY, null, "lottery"),
        "retail/prize-agency" to Triple(PrizeRedemptionChannel.AGENCY, null, "agency"),
        "retail/prize-pix" to Triple(PrizeRedemptionChannel.PIX, null, "pix"),
        "retail/prize-confirmation" to Triple(null, PrizeRedemptionChannel.PIX, "pix-success"),
        "retail/prize-mercado-pago" to Triple(PrizeRedemptionChannel.MERCADO_PAGO, null, "mp"),
        "retail/prize-mercado-pago-success" to Triple(null, PrizeRedemptionChannel.MERCADO_PAGO, "mp-success")
    ).forEach { (route, setup) ->
        composable(route) {
            if (!appContainer.mockBackendEnabled) {
                LaunchedEffect(Unit) {
                    navController.navigate("tickets/my-bets") {
                        popUpTo(route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                return@composable
            }
            PrizeRedemptionDestination(
                appContainer = appContainer,
                betId = 100L,
                keySuffix = "alias-${setup.third}",
                onRedirect = actions.onPrizeRedirect,
                onBetEntry = actions.onPrizeBetEntry,
                initialChannel = setup.first,
                previewSuccessChannel = setup.second,
                onBack = navController::popBackStack,
                onHelp = { navController.navigate("support/terms?tab=FAQ") },
                onHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onMyBets = {
                    navController.navigate("tickets/my-bets") { launchSingleTop = true }
                }
            )
        }
    }
}
