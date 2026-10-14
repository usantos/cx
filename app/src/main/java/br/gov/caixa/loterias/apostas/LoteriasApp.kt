package br.gov.caixa.loterias.apostas

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.currentBackStackEntryAsState
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import android.net.Uri
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import br.gov.caixa.loterias.feature.tickets.BetRepeatActionsOverlay
import br.gov.caixa.loterias.core.presentation.WorkflowSpec
import br.gov.caixa.loterias.feature.account.accountScreens
import br.gov.caixa.loterias.feature.account.AccountRoute
import br.gov.caixa.loterias.feature.account.AccountStatusViewModel
import br.gov.caixa.loterias.feature.account.LoginRoute
import br.gov.caixa.loterias.feature.account.LoginViewModel
import br.gov.caixa.loterias.feature.account.PersonalDataRoute
import br.gov.caixa.loterias.feature.account.PersonalDataViewModel
import br.gov.caixa.loterias.feature.account.RegistrationRoute
import br.gov.caixa.loterias.feature.account.RegistrationViewModel
import br.gov.caixa.loterias.feature.betting.bettingScreens
import br.gov.caixa.loterias.feature.betting.BettingRoute
import br.gov.caixa.loterias.feature.betting.BettingFlowViewModel
import br.gov.caixa.loterias.feature.betting.BetComboDetailRoute
import br.gov.caixa.loterias.feature.betting.BetCombosRoute
import br.gov.caixa.loterias.feature.betting.BettingCatalogMode
import br.gov.caixa.loterias.feature.betting.BettingCatalogViewModel
import br.gov.caixa.loterias.feature.betting.BettingGameRoute
import br.gov.caixa.loterias.feature.betting.BettingGameViewModel
import br.gov.caixa.loterias.feature.betting.LotteryModalitiesRoute
import br.gov.caixa.loterias.feature.betting.RapidConfigurationRoute
import br.gov.caixa.loterias.feature.betting.RapidConfigurationViewModel
import br.gov.caixa.loterias.feature.betting.RapidListRoute
import br.gov.caixa.loterias.feature.betting.RapidListViewModel
import br.gov.caixa.loterias.feature.betting.FavoriteBetsRoute
import br.gov.caixa.loterias.feature.betting.FavoriteBetsViewModel
import br.gov.caixa.loterias.feature.cart.cartScreens
import br.gov.caixa.loterias.feature.cart.CartAdditionKind
import br.gov.caixa.loterias.feature.cart.CartAdditionRoute
import br.gov.caixa.loterias.feature.cart.CartAdditionViewModel
import br.gov.caixa.loterias.feature.cart.CartViewModel
import br.gov.caixa.loterias.feature.cart.CurrentCartRoute
import br.gov.caixa.loterias.feature.cart.FavoriteCartDetailRoute
import br.gov.caixa.loterias.feature.cart.FavoriteCartsRoute
import br.gov.caixa.loterias.feature.cart.FavoriteCartsViewModel
import br.gov.caixa.loterias.core.navigation.AppDestination
import br.gov.caixa.loterias.feature.marketplacefilter.MarketplaceFilterRoute
import br.gov.caixa.loterias.feature.marketplacefilter.MarketplaceFilterViewModel
import br.gov.caixa.loterias.feature.marketplace.MarketplaceRoute
import br.gov.caixa.loterias.feature.marketplace.MarketplaceViewModel
import br.gov.caixa.loterias.feature.marketplace.MarketplaceDetailRoute
import br.gov.caixa.loterias.feature.marketplace.MarketplaceDetailViewModel
import br.gov.caixa.loterias.feature.marketplace.MarketplacePoolConfirmation
import br.gov.caixa.loterias.feature.home.HomeViewModel
import br.gov.caixa.loterias.feature.notifications.notificationScreens
import br.gov.caixa.loterias.feature.notifications.NotificationsRoute
import br.gov.caixa.loterias.feature.onboarding.onboardingScreens
import br.gov.caixa.loterias.feature.onboarding.OnboardingRoute
import br.gov.caixa.loterias.feature.onboarding.OnboardingViewModel
import br.gov.caixa.loterias.feature.payment.paymentScreens
import br.gov.caixa.loterias.feature.payment.PaymentTerminalRoute
import br.gov.caixa.loterias.feature.payment.PaymentTerminalViewModel
import br.gov.caixa.loterias.feature.payment.PaymentDataRoute
import br.gov.caixa.loterias.feature.payment.PaymentMode
import br.gov.caixa.loterias.feature.payment.PaymentVisualAssets
import br.gov.caixa.loterias.feature.payment.PaymentViewModel
import br.gov.caixa.loterias.feature.payment.PaymentCardFlowRoute
import br.gov.caixa.loterias.feature.payment.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.PaymentCardViewModel
import br.gov.caixa.loterias.feature.responsiblegaming.responsibleGamingScreens
import br.gov.caixa.loterias.feature.responsiblegaming.ResponsibleGamingRoute
import br.gov.caixa.loterias.feature.responsiblegaming.ResponsibleGamingViewModel
import br.gov.caixa.loterias.feature.responsiblegaming.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.results.resultScreens
import br.gov.caixa.loterias.feature.results.ResultsRoute
import br.gov.caixa.loterias.feature.results.ResultsViewModel
import br.gov.caixa.loterias.feature.retail.retailScreens
import br.gov.caixa.loterias.feature.retail.LocationGateRoute
import br.gov.caixa.loterias.feature.retail.LocationGateViewModel
import br.gov.caixa.loterias.feature.retail.RetailSearchRoute
import br.gov.caixa.loterias.feature.retail.RetailSearchViewModel
import br.gov.caixa.loterias.feature.retail.PrizeRedemptionRoute
import br.gov.caixa.loterias.feature.retail.PrizeRedemptionViewModel
import br.gov.caixa.loterias.feature.support.SupportRoute
import br.gov.caixa.loterias.feature.support.SupportMode
import br.gov.caixa.loterias.feature.support.SupportTab
import br.gov.caixa.loterias.feature.support.SupportUiState
import br.gov.caixa.loterias.feature.support.SupportViewModel
import br.gov.caixa.loterias.feature.support.supportScreens
import br.gov.caixa.loterias.feature.tickets.BetDetailDataRoute
import br.gov.caixa.loterias.feature.tickets.BetDetailViewModel
import br.gov.caixa.loterias.feature.tickets.ticketScreens
import br.gov.caixa.loterias.feature.tickets.TicketsViewModel
import br.gov.caixa.loterias.feature.tickets.ConfirmedBetsDataRoute
import br.gov.caixa.loterias.feature.tickets.PurchasesDataRoute
import br.gov.caixa.loterias.feature.tickets.PurchaseDetailDataRoute
import br.gov.caixa.loterias.feature.tickets.ManualTicketDataRoute
import br.gov.caixa.loterias.feature.tickets.TicketScannerRoute
import br.gov.caixa.loterias.feature.tickets.TicketGameChoiceRoute
import br.gov.caixa.loterias.feature.tickets.TicketResultDataRoute
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet
import br.gov.caixa.loterias.core.domain.tickets.Purchase
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilter
import br.gov.caixa.loterias.core.domain.retail.PrizeRedemptionChannel
import br.gov.caixa.loterias.core.domain.retail.Retailer
import br.gov.caixa.loterias.core.domain.betting.BetEntryDecision
import br.gov.caixa.loterias.core.domain.account.PostLoginDestination

@Composable
fun LoteriasApp(
    appContainer: AppContainer,
    startDestination: String = "account/login",
) {
    val navController = rememberNavController()
    val externalUriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    val sessionIdentity by appContainer.sessionIdentityChanges.collectAsStateWithLifecycle(initialValue = appContainer.authenticatedSessionIdentity.captureIdentity())
    val loggedDetailContext = rememberLoggedDetailContext(sessionIdentity?.ownerKey)
    var preparedBettingParameters by remember(sessionIdentity?.ownerKey) {
        mutableStateOf<br.gov.caixa.loterias.core.domain.betting.BettingParameters?>(null)
    }
    var marketplaceFilter by loggedDetailContext::marketplaceFilter
    var selectedPurchase by loggedDetailContext::selectedPurchase
    var selectedBet by loggedDetailContext::selectedBet
    var betToEdit by loggedDetailContext::betToEdit
    var selectedMarketplaceRetailer by loggedDetailContext::selectedMarketplaceRetailer
    var checkedTicketBets by loggedDetailContext::checkedTicketBets
    var pendingRestoredRoute by rememberSaveable { mutableStateOf<String?>(null) }
    var requestedHomeModalityId by remember { mutableStateOf<Int?>(null) }
    val logoutViewModel = viewModel(key = "session-logout") {
        br.gov.caixa.loterias.feature.account.LogoutViewModel(
            appContainer.authentication, appContainer.endSession, appContainer.clearLocalSessionData,
            signOutSession = appContainer.signOutSession,
            identities = appContainer.authenticatedSessionIdentity,
            dispatchers = appContainer.dispatchers,
        )
    }
    var visitorLoginRequest by rememberSaveable { mutableStateOf<String?>(null) }
    var visitorReturnRoute by rememberSaveable { mutableStateOf<String?>(null) }
    var visitorReturnOwnerKey by rememberSaveable { mutableStateOf<String?>(null) }
    var resumedHomeAction by rememberSaveable { mutableStateOf<String?>(null) }
    var checkingAccountCompletion by remember { mutableStateOf(false) }
    var accountCompletionError by remember { mutableStateOf<String?>(null) }
    var accountCompletionRequired by rememberSaveable { mutableStateOf(false) }
    var foregroundExpiryMarker by rememberSaveable { mutableStateOf<String?>(null) }
    val resetLoggedNavigation: () -> Unit = {
        marketplaceFilter = MarketplaceFilter.empty()
        selectedPurchase = null
        selectedBet = null
        betToEdit = null
        selectedMarketplaceRetailer = null
        checkedTicketBets = emptyList()
        requestedHomeModalityId = null
        loggedDetailContext.clear()
        pendingRestoredRoute = null
        visitorLoginRequest = null
        visitorReturnRoute = null
        visitorReturnOwnerKey = null
        resumedHomeAction = null
        accountCompletionRequired = false
        accountCompletionError = null
        foregroundExpiryMarker = null
        navController.navigate("account/login") {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }
    val logoutState by logoutViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(logoutState.localSessionEnded) {
        if (logoutState.localSessionEnded) resetLoggedNavigation()
    }
    val onLogout: () -> Unit = { logoutViewModel.start(simulated = BuildConfig.ENABLE_MOCK_BACKEND) }
    val foregroundNoticeController = remember(appContainer, navController) {
        ForegroundSessionNoticeController(appContainer.foregroundSession,
            { appContainer.localClock.instant().epochSecond }, resetLoggedNavigation)
    }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, appContainer, navController) {
        val observer = ForegroundSessionLifecycleObserver(
            appContainer.foregroundSession,
            currentRoute = { navController.currentBackStackEntry?.destination?.route },
            hasLoggedContext = { appContainer.authenticatedSessionIdentity.captureIdentity() != null },
            nowSeconds = { appContainer.localClock.instant().epochSecond },
            onLogin = resetLoggedNavigation,
            onExpired = { foregroundExpiryMarker = foregroundNoticeController.show(it) },
        )
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    foregroundExpiryMarker?.let { marker ->
        foregroundNoticeController.restore(marker)?.let { accept ->
            CaixaDSLegacyDialog(
                title = "Atenção",
                message = br.gov.caixa.loterias.core.presentation.LEGACY_LOGIN_REQUIRED,
                positiveLabel = "Entendi",
                onPositive = {
                    foregroundExpiryMarker = null
                    accept()
                },
            )
        }
    }
    val completionScope = androidx.compose.runtime.rememberCoroutineScope()
    val recheckAccountCompletion: () -> Unit = {
        if (!checkingAccountCompletion) {
            checkingAccountCompletion = true
            completionScope.launch {
                try {
                    val lease = appContainer.authenticatedSessionIdentity.captureIdentity() ?: return@launch
                    val checked = appContainer.verifyPostLoginAccess()
                    if (!appContainer.authenticatedSessionIdentity.isCurrent(lease)) return@launch
                    when (checked) {
                        is br.gov.caixa.loterias.core.domain.account.PostLoginAccessResult.Verified -> {
                            accountCompletionRequired = checked.access.destination != PostLoginDestination.HOME
                            if (!accountCompletionRequired && pendingRestoredRoute != null) {
                                loggedDetailContext.reconcile(lease.ownerKey)
                                visitorReturnRoute = if (loggedDetailContext.restorationOutcome == DetailRestorationOutcome.Restored) pendingRestoredRoute else null
                                visitorReturnOwnerKey = lease.ownerKey
                                pendingRestoredRoute = null
                            }
                            visitorReturnRoute = authenticatedContinuation(visitorReturnRoute, visitorReturnOwnerKey, lease.ownerKey)
                            navController.navigate(postLoginDestinationRoute(checked.access.destination)) { launchSingleTop = true }
                        }
                        else -> accountCompletionError = "Não foi possível validar o cadastro. Tente novamente."
                    }
                } finally { checkingAccountCompletion = false }
            }
        }
    }
    if (checkingAccountCompletion) br.gov.caixa.loterias.core.designsystem.CaixaDSAuthenticationLoadingDialog()
    accountCompletionError?.let { message -> CaixaDSLegacyDialog(title = "Atenção", message = message,
        positiveLabel = "Tentar novamente", negativeLabel = "Depois",
        onPositive = { accountCompletionError = null; recheckAccountCompletion() },
        onNegative = { accountCompletionError = null },
    ) }
    val currentNavigationEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(sessionIdentity, currentNavigationEntry) {
        foregroundExpiryMarker?.let { marker ->
            val route = currentNavigationEntry?.destination?.route?.substringBefore('?')
            if (route == "account/login" || route == "account/token" || route?.startsWith("onboarding/") == true ||
                foregroundNoticeController.restore(marker) == null) {
                foregroundNoticeController.dismiss(marker)
                foregroundExpiryMarker = null
            }
        }
    }
    LaunchedEffect(sessionIdentity, currentNavigationEntry, loggedDetailContext.restorationOutcome) {
        val entry = currentNavigationEntry
        if (sessionIdentity == null && loggedDetailContext.restorationOutcome == DetailRestorationOutcome.Pending && entry != null &&
            entry.destination.route?.startsWith("account/login") != true) {
            pendingRestoredRoute = restoredLoggedRoute(entry.destination.route.orEmpty()) { key -> entry.arguments?.get(key)?.toString() }
            navController.navigate("account/login") { popUpTo(navController.graph.id) { inclusive = true }; launchSingleTop = true }
        }
    }
    val requestVisitorLogin: (String) -> Unit = { route -> visitorLoginRequest = route }
    visitorLoginRequest?.let { route ->
        br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog(
            title = "Atenção",
            message = "Para continuar utilizando a funcionalidade, você precisa efetuar o login no app Loterias Caixa. Deseja efetuar o login?",
            positiveLabel = "Sim", negativeLabel = "Não",
            onPositive = {
                visitorLoginRequest = null
                visitorReturnRoute = route
                navController.navigate(LoginNavigation.authorize)
            },
            onNegative = { visitorLoginRequest = null },
        )
    }
    val handleBetEntry: (BetEntryDecision, Boolean) -> Unit = { decision, returnViaHome ->
        when (decision) {
            is BetEntryDecision.Ready -> {
                betToEdit = null
                preparedBettingParameters = decision.parameters
                if (returnViaHome) {
                    requestedHomeModalityId = decision.game.modality.id
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
                navController.navigate(BettingSimulationRoute.from(decision.game))
            }
            is BetEntryDecision.CompleteAccount -> {
                accountCompletionRequired = true
                navController.navigate(when (decision.destination) {
                PostLoginDestination.HOME -> "home"
                PostLoginDestination.REGISTRATION -> "account/register"
                PostLoginDestination.TERMS -> "support/terms-acceptance"
                PostLoginDestination.SELF_ASSESSMENT -> "responsible/start/required"
                }) { launchSingleTop = true }
            }
        }
    }
    val onPrizeBetEntry: (BetEntryDecision) -> Unit = { handleBetEntry(it, true) }
    val onPrizeRedirect: (br.gov.caixa.loterias.core.domain.retail.PrizeRedirect) -> Unit = { destination ->
        if (destination in accountCompletionRedirects) {
            val betId = navController.currentBackStackEntry?.arguments?.getLong("betId")?.takeIf { it > 0 } ?: selectedBet?.id
            visitorReturnRoute = loggedContinuation("retail/prize", betId)
            visitorReturnOwnerKey = appContainer.authenticatedSessionIdentity.captureIdentity()?.ownerKey
        }
        navController.navigate(prizeRedirectRoute(destination)) {
            if (destination == br.gov.caixa.loterias.core.domain.retail.PrizeRedirect.HOME) {
                popUpTo("home") { inclusive = false }
            }
            launchSingleTop = true
        }
    }
    val onTicketServiceRedirect: (br.gov.caixa.loterias.core.domain.service.ServiceRedirect) -> Unit = { destination ->
        if (destination in accountCompletionRedirects) {
            visitorReturnRoute = ticketQueryContinuation()
            visitorReturnOwnerKey = appContainer.authenticatedSessionIdentity.captureIdentity()?.ownerKey
        }
        navController.navigate(serviceRedirectRoute(destination)) { launchSingleTop = true }
    }
    val onPaymentRedirect: (br.gov.caixa.loterias.core.domain.payment.PurchaseRedirect) -> Unit = { destination ->
        if (destination.name in setOf("LOGIN", "REGISTER_ACCOUNT", "ACCEPT_TERMS")) visitorReturnRoute = "cart/current"
        navController.navigate(paymentRedirectRoute(destination)) { launchSingleTop = true }
    }
    val onPaymentMoreBets: () -> Unit = {
        navController.navigate("home") {
            popUpTo("home") { inclusive = false }
            launchSingleTop = true
        }
    }
    val sections = remember {
        listOf(
            HomeSection("Início", onboardingScreens),
            HomeSection("Conta", accountScreens),
            HomeSection("Apostas", bettingScreens),
            HomeSection("Carrinho", cartScreens),
            HomeSection("Pagamentos", paymentScreens),
            HomeSection("Bilhetes e compras", ticketScreens),
            HomeSection("Resultados", resultScreens),
            HomeSection("Jogo responsável", responsibleGamingScreens),
            HomeSection("Notificações", notificationScreens),
            HomeSection("Lotéricas e resgate", retailScreens),
            HomeSection("Ajuda", supportScreens),
        )
    }
    val allScreens = remember(sections) { sections.flatMap(HomeSection::screens) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable("home") {
            var showShakeTutorial by rememberSaveable { mutableStateOf(!appContainer.shakeSettings.tutorialCompleted()) }
            if (showShakeTutorial) {
                br.gov.caixa.loterias.feature.betting.ShakeTutorialRoute {
                    appContainer.shakeSettings.completeTutorial()
                    showShakeTutorial = false
                }
                return@composable
            }
            val homeViewModel = viewModel(key = "home") {
                HomeViewModel(
                    appContainer.bettingParameters,
                    appContainer.homeFeatureConfiguration,
                    appContainer.betCombos,
                    appContainer.socialTransfers,
                    appContainer.currentCart,
                    appContainer.prepareBetEntry,
                    appContainer.preparePoolEntry,
                    appContainer.prepareHomeBettingAction,
                    appContainer.observeSessionContent,
                    appContainer.readLatestNotification,
                    appContainer.dispatchers,
                    appContainer.networkAvailability,
                    appContainer.localClock,
                    getUnreadNotificationCount = appContainer.unreadNotificationCount,
                )
            }
            val homeResultsViewModel = viewModel(key = "home-results") {
                ResultsViewModel(
                    appContainer.latestResult,
                    appContainer.resultByContest,
                    appContainer.dispatchers,
                )
            }
            LaunchedEffect(visitorReturnRoute) {
                val requested = visitorReturnRoute ?: return@LaunchedEffect
                if (appContainer.currentUserIdentity.currentUser() != null) {
                    val route = authenticatedContinuation(requested, visitorReturnOwnerKey, appContainer.authenticatedSessionIdentity.captureIdentity()?.ownerKey)
                    visitorReturnRoute = null
                    visitorReturnOwnerKey = null
                    if (route == null) return@LaunchedEffect
                    if (route.startsWith("home/")) resumedHomeAction = route else navController.navigate(route) { launchSingleTop = true; popUpTo("home") { inclusive = false } }
                }
            }
            val resumeHomeState by homeViewModel.state.collectAsStateWithLifecycle()
            LaunchedEffect(resumedHomeAction, resumeHomeState.loading, resumeHomeState.parametersUnavailable) {
                val route = resumedHomeAction ?: return@LaunchedEffect
                if (resumeHomeState.loading || resumeHomeState.parametersUnavailable) return@LaunchedEffect
                resumedHomeAction = null
                when {
                    route == "home/rapid" -> homeViewModel.startOtherBetting(br.gov.caixa.loterias.core.domain.betting.HomeBettingAction.RAPID)
                    route == "home/combo" -> homeViewModel.startOtherBetting(br.gov.caixa.loterias.core.domain.betting.HomeBettingAction.COMBO)
                    route.startsWith("home/pool/") -> route.split('/').let { homeViewModel.startPools(it[2].toInt(), it[3]) }
                }
            }
            HomeScreen(
                shakeSettings = appContainer.shakeSettings,
                networkAvailability = appContainer.networkAvailability,
                userName = appContainer.currentUserIdentity.currentUser()?.name,
                environmentLabel = homeEnvironmentLabel(BuildConfig.FLAVOR, BuildConfig.API_BASE_URL),
                onOpen = { route ->
                    if (requiresVisitorLogin(route) && appContainer.currentUserIdentity.currentUser() == null) {
                        if (opensVisitorDestinationBeforeLogin(route)) {
                            navController.navigate(route) {
                                launchSingleTop = true
                                popUpTo("home") { inclusive = false }
                            }
                        }
                        requestVisitorLogin(route)
                    } else navController.navigate(route)
                },
                onProtectedAction = { route ->
                    if (appContainer.currentUserIdentity.currentUser() == null) requestVisitorLogin(route)
                    else when {
                        route == "home/rapid" -> homeViewModel.startOtherBetting(br.gov.caixa.loterias.core.domain.betting.HomeBettingAction.RAPID)
                        route == "home/combo" -> homeViewModel.startOtherBetting(br.gov.caixa.loterias.core.domain.betting.HomeBettingAction.COMBO)
                        route.startsWith("home/pool/") -> route.split('/').let { homeViewModel.startPools(it[2].toInt(), it[3]) }
                    }
                },
                onPoolEntry = { decision ->
                    when (decision) {
                        is br.gov.caixa.loterias.core.domain.betting.PoolEntryDecision.Ready -> {
                            marketplaceFilter = MarketplaceFilter.forModality(decision.request.modalityId,
                                if (decision.request.contestType == "ESPECIAL") 2 else 1)
                            navController.navigate(if (decision.showTutorial) PoolTutorialRoute.from(decision.request) else "betting/pools")
                        }
                        is br.gov.caixa.loterias.core.domain.betting.PoolEntryDecision.CompleteAccount ->
                            handleBetEntry(BetEntryDecision.CompleteAccount(decision.destination), false)
                    }
                },
                onLogout = onLogout,
                viewModel = homeViewModel,
                resultsViewModel = homeResultsViewModel,
                requestedModalityId = requestedHomeModalityId,
                onBetEntry = { handleBetEntry(it, false) },
                onOtherBetting = { decision ->
                    when (decision) {
                        is br.gov.caixa.loterias.core.domain.betting.HomeBettingDecision.CompleteAccount ->
                            handleBetEntry(BetEntryDecision.CompleteAccount(decision.destination), false)
                        is br.gov.caixa.loterias.core.domain.betting.HomeBettingDecision.Ready -> {
                            val destination = homeBettingDestination(decision.target)
                            if (decision.target == br.gov.caixa.loterias.core.domain.betting.HomeBettingTarget.INSTANT) {
                                runCatching { externalUriHandler.openUri(destination) }
                                    .onFailure { homeViewModel.externalBettingFailed() }
                            } else navController.navigate(destination)
                        }
                    }
                },
            )
        }
        allScreens.filterNot { it.route.startsWith("responsible/") || it.route.startsWith("onboarding/") ||
            it.route.startsWith("account/") || it.route in setOf(
            "account/login",
            "account/personal-data",
            "account/register",
            "cart/current",
            "cart/included",
            "cart/pool-included",
            "cart/favorite-carts",
            "cart/favorite-detail",
            "tickets/bet-detail",
            "tickets/my-bets",
            "tickets/purchases",
            "tickets/purchase-detail",
            "tickets/manual-code",
            "tickets/read",
            "tickets/read-game-choice",
            "tickets/read-result",
            "betting/pools",
            "betting/home",
            "betting/pool-view",
            "betting/pool-detail",
            "betting/modalities",
            "betting/simulation",
            "betting/rapid",
            "betting/rapid-list",
            "betting/favorites",
            "betting/combos",
            "betting/combo-detail",
            "payment/method",
            "payment/cards",
            "payment/add-card",
            "payment/card-preview",
            "payment/confirm-card",
            "payment/cvv",
            "payment/confirmation",
            "payment/failure",
            "payment/pix-copy",
            "payment/pix-expired",
            "payment/pix-guidance",
            "payment/success",
            "results/modalities",
            "results/detail",
            "results/confirmed-bet",
            "retail/search",
            "retail/location",
            "retail/prize",
            "retail/prize-guidance",
            "retail/prize-lottery",
            "retail/prize-agency",
            "retail/prize-pix",
            "retail/prize-confirmation",
            "retail/prize-mercado-pago",
            "retail/prize-mercado-pago-success",
            "support",
            "support/terms",
            "support/terms-view",
            "support/terms-acceptance",
            "support/game-help",
            "notifications/messages",
            "notifications/preferences",
        ) }.forEach { spec ->
            composable(
                route = if (spec.route == PoolTutorialRoute.base) PoolTutorialRoute.pattern else spec.route,
                arguments = if (spec.route == PoolTutorialRoute.base) listOf(
                    navArgument("modalityId") { type = NavType.IntType; defaultValue = 0 },
                    navArgument("contestTypeId") { type = NavType.IntType; defaultValue = 1 },
                ) else emptyList(),
            ) { entry ->
                val onComplete = {
                    if (spec.route == PoolTutorialRoute.base) {
                        val modality = entry.arguments?.getInt("modalityId") ?: 0
                        marketplaceFilter = if (modality > 0) MarketplaceFilter.forModality(
                            modality, entry.arguments?.getInt("contestTypeId") ?: 1,
                        ) else MarketplaceFilter.empty()
                        navController.navigate("betting/pools") {
                            popUpTo(PoolTutorialRoute.pattern) { inclusive = true }
                        }
                    } else nextDestination(spec.route)?.let { destination ->
                        navController.navigate(destination)
                    } ?: navController.popBackStack()
                    Unit
                }
                when {
                    spec.route.startsWith("betting/") -> {
                        val bettingViewModel = viewModel(key = "betting-flow-${spec.route}") {
                            BettingFlowViewModel(spec, appContainer.setBettingTutorialDismissed)
                        }
                        BettingRoute(bettingViewModel, navController::popBackStack, onComplete)
                    }
                    else -> error("Rota sem layout Compose específico: ${spec.route}")
                }
            }
        }
        onboardingScreens.forEach { spec ->
            composable(spec.route) {
                if (spec.route == "onboarding/update" || spec.route == "onboarding/unavailable") {
                    StartupStatusDestination(spec.route, appContainer) {
                        navController.navigate(startupDestination(appContainer.shouldShowIntroduction())) {
                            popUpTo(spec.route) { inclusive = true }
                        }
                    }
                    return@composable
                }
                val onboardingViewModel = viewModel(key = "onboarding-${spec.route}") {
                    OnboardingViewModel(
                        spec.route,
                        appContainer.completeIntroduction,
                        appContainer.setBettingTutorialDismissed,
                    )
                }
                OnboardingRoute(
                    viewModel = onboardingViewModel,
                    onBack = navController::popBackStack,
                    onComplete = {
                        nextDestination(spec.route)?.let { navController.navigate(it) }
                            ?: navController.popBackStack()
                    },
                    onUnderage = { navController.navigate("onboarding/age") },
                    onResponsibleGaming = { navController.navigate("responsible/start") },
                    onStartBettingTutorial = { navController.navigate("betting/tutorial-plus") },
                    onSkipBettingTutorial = { navController.navigate("betting/simulation") },
                )
            }
        }
        accountScreens.filter { it.route in accountStatusRoutes }.forEach { spec ->
            composable(spec.route) {
                val accountStatusViewModel = viewModel(key = "account-status-${spec.route}") {
                    AccountStatusViewModel(spec.route, appContainer.dispatchers)
                }
                AccountRoute(
                    viewModel = accountStatusViewModel,
                    onBack = navController::popBackStack,
                    onComplete = {
                        if (spec.route == "account/register-confirmation" && (accountCompletionRequired || visitorReturnRoute != null)) recheckAccountCompletion()
                        else nextDestination(spec.route)?.let { navController.navigate(it) } ?: navController.popBackStack()
                    },
                    ssoAccountUrl = BuildConfig.SSO_ACCOUNT_URL +
                        "?referrer=" + BuildConfig.SSO_CLIENT_ID +
                        "&referrer_uri=" + Uri.encode(BuildConfig.SSO_ACCOUNT_REFERRER_URI),
                )
            }
        }
        notificationScreens.forEach { spec ->
            composable(spec.route) {
                val notificationsViewModel = viewModel(key = "notifications-${spec.route}") {
                    appContainer.notificationViewModelFactory.create()
                }
                NotificationsRoute(
                    route = spec.route,
                    viewModel = notificationsViewModel,
                    onBack = navController::popBackStack,
                    onHelp = { navController.navigate("support/terms?tab=FAQ") },
                )
            }
        }
        responsibleGamingScreens.forEach { spec ->
            composable(spec.route) {
                val responsibleGamingViewModel = viewModel(key = "responsible-${spec.route}") {
                    ResponsibleGamingViewModel(
                        route = spec.route,
                        getQuestions = appContainer.selfAssessmentQuestions,
                        submitAssessment = appContainer.submitSelfAssessment,
                        getSocialTransfers = appContainer.socialTransfers,
                        suspendAccount = appContainer.suspendAccount,
                        dispatchers = appContainer.dispatchers,
                    )
                }
                ResponsibleGamingRoute(
                    route = spec.route,
                    viewModel = responsibleGamingViewModel,
                    onBack = navController::popBackStack,
                    onOpenTerms = { navController.navigate("support/terms") },
                    onSubmitSuspension = {
                        if (requiresVisitorLoginOnSubmit(spec.route) && appContainer.currentUserIdentity.currentUser() == null) {
                            requestVisitorLogin(spec.route)
                        } else {
                            responsibleGamingViewModel.onAction(ResponsibleGamingAction.SubmitSuspension)
                        }
                    },
                    onComplete = {
                        if (spec.route == "responsible/suspension") {
                            onLogout()
                        } else if (spec.route == "responsible/form" && (accountCompletionRequired || visitorReturnRoute != null)) {
                            recheckAccountCompletion()
                        } else {
                            nextDestination(spec.route)?.let { route ->
                                if (requiresVisitorLogin(route) && appContainer.currentUserIdentity.currentUser() == null) requestVisitorLogin(route)
                                else navController.navigate(route)
                            } ?: navController.popBackStack()
                        }
                    },
                )
            }
        }
        composable("responsible/start/required") {
            val responsibleGamingViewModel = viewModel(key = "responsible-start-required") {
                ResponsibleGamingViewModel(
                    route = "responsible/start",
                    getQuestions = appContainer.selfAssessmentQuestions,
                    submitAssessment = appContainer.submitSelfAssessment,
                    getSocialTransfers = appContainer.socialTransfers,
                    suspendAccount = appContainer.suspendAccount,
                    dispatchers = appContainer.dispatchers,
                    assessmentRequired = true,
                )
            }
            ResponsibleGamingRoute(
                route = "responsible/start",
                viewModel = responsibleGamingViewModel,
                onBack = navController::popBackStack,
                onOpenTerms = { navController.navigate("support/terms") },
                onComplete = { navController.navigate("responsible/form") },
            )
        }
        listOf("betting/home", "betting/modalities").forEach { route ->
            composable(route) {
            val catalogViewModel = viewModel(key = "modalities-$route") {
                BettingCatalogViewModel(
                    mode = BettingCatalogMode.MODALITIES,
                    getModalities = appContainer.lotteryModalities,
                    getParameters = appContainer.bettingParameters,
                    getCombos = appContainer.betCombos,
                    addCombo = appContainer.addComboToCart,
                    dispatchers = appContainer.dispatchers,
                )
            }
            LotteryModalitiesRoute(
                viewModel = catalogViewModel,
                onBack = navController::popBackStack,
                onSelect = { modalityId ->
                    betToEdit = null
                    preparedBettingParameters = catalogViewModel.state.value.parameters
                    navController.navigate("betting/simulation/$modalityId")
                },
            )
            }
        }
        composable("betting/simulation") {
            val bettingGameViewModel = viewModel(key = "betting-game-default") {
                BettingGameViewModel(
                    modalityId = null,
                    getParameters = appContainer.bettingParameters,
                    addBet = appContainer.addBetToCart,
                    validateFavoriteBet = appContainer.validateFavoriteBet,
                    saveFavoriteBet = appContainer.saveFavoriteBet,
                    dispatchers = appContainer.dispatchers,
                )
            }
            val gameCartSummary = activeCartSummary(appContainer.currentCart)
            BettingGameRoute(
                onHelp = { navController.navigate("support/terms?tab=FAQ") },
                cartTotal = gameCartSummary?.total ?: java.math.BigDecimal.ZERO,
                cartBetCount = gameCartSummary?.betCount ?: 0,
                onCart = { navController.navigate("cart/current") },
                shakeEnabled = appContainer.shakeSettings.enabled(),
                viewModel = bettingGameViewModel,
                onBack = navController::popBackStack,
                onAdded = { navController.navigate(betAdditionRoute(bettingGameViewModel.state.value)) },
            )
        }
        composable("betting/rapid") {
            val rapidConfigurationViewModel = viewModel(key = "rapid-configuration") {
                RapidConfigurationViewModel(
                    getConfiguration = appContainer.rapidBetConfiguration,
                    getModalities = appContainer.lotteryModalities,
                    saveConfiguration = appContainer.saveRapidBetConfiguration,
                    getFavorites = appContainer.favoriteBets,
                    getParameters = appContainer.bettingParameters,
                    verifyAccess = appContainer.verifyAccountAccess,
                    dispatchers = appContainer.dispatchers,
                )
            }
            RapidConfigurationRoute(
                viewModel = rapidConfigurationViewModel,
                onBack = navController::popBackStack,
                onSaved = { navController.navigate("betting/rapid-list") },
            )
        }
        composable("betting/rapid-list") {
            val rapidListViewModel = viewModel(key = "rapid-list") {
                RapidListViewModel(
                    generateRapidBets = appContainer.generateRapidBets,
                    removeCartItem = appContainer.removeCartItem,
                    validateCart = appContainer.validateCart,
                    dispatchers = appContainer.dispatchers,
                    networkAvailability = appContainer.networkAvailability,
                    localClock = appContainer.localClock,
                )
            }
            RapidListRoute(
                viewModel = rapidListViewModel,
                onBack = navController::popBackStack,
                onPayment = { navController.navigate("payment/method") },
                onValidationRedirect = {
                    if (it in accountCompletionRedirects) visitorReturnRoute = "cart/current"
                    navController.navigate(serviceRedirectRoute(it))
                },
            )
        }
        composable("betting/favorites") {
            val favoriteBetsViewModel = viewModel(key = "favorite-bets") {
                FavoriteBetsViewModel(
                    getFavorites = appContainer.favoriteBets,
                    getParameters = appContainer.bettingParameters,
                    deleteFavorite = appContainer.deleteFavoriteBet,
                    addFavoriteToCart = appContainer.addFavoriteBetToCart,
                    dispatchers = appContainer.dispatchers,
                )
            }
            FavoriteBetsRoute(
                viewModel = favoriteBetsViewModel,
                onBack = navController::popBackStack,
                onTerms = { navController.navigate("support/terms") },
                onCreateFavorite = { navController.navigate("betting/simulation/$it") },
            )
        }
        listOf(
            "payment/method" to PaymentMode.METHODS,
            "payment/pix-copy" to PaymentMode.PIX,
            "payment/pix-expired" to PaymentMode.PIX,
        ).forEach { (route, mode) ->
            composable(route) {
                if (route == "payment/pix-expired" && !BuildConfig.ENABLE_MOCK_BACKEND) {
                    LaunchedEffect(route) {
                        navController.navigate("tickets/purchases") {
                            popUpTo(route) { inclusive = true }
                        }
                    }
                    return@composable
                }
                val paymentViewModel = viewModel(key = "payment-data-$route") {
                    PaymentViewModel(
                        clock = appContainer.localClock,
                        recordRecentPurchase = appContainer.recordRecentPurchase,
                        mode = mode,
                        getMethods = appContainer.paymentMethods,
                        getCards = appContainer.savedCards,
                        deleteCard = appContainer.deleteSavedCard,
                        generatePixCharge = appContainer.generatePixCharge,
                        getPurchaseProgress = appContainer.purchaseProgress,
                        validateDam = appContainer.validateDam,
                        getCurrentCart = appContainer.currentCart,
                        dispatchers = appContainer.dispatchers,
                        ownerKey = appContainer.authenticatedSessionIdentity.captureIdentity()?.ownerKey,
                        savedStateHandle = createSavedStateHandle(),
                    )
                }
                val registrationViewModel = paymentRegistrationViewModel(appContainer)
                PaymentDataRoute(
                    onRegistration = { registrationViewModel.start() },
                    onRedirect = onPaymentRedirect,
                    mode = mode,
                    viewModel = paymentViewModel,
                    visualAssets = PaymentVisualAssets(recargaPayLogoResourceId = R.drawable.recargapay),
                    onBack = navController::popBackStack,
                    onHelp = { navController.navigate("support/terms?tab=FAQ") },
                    onPix = { navController.navigate("payment/pix-guidance") },
                    onAddCard = { methodId -> navController.navigate("payment/card-flow/$methodId") },
                    onOpenCard = { card -> navController.navigate("payment/card-flow/${card.paymentMethodId}?cardId=${Uri.encode(card.id)}") },
                    onApproved = { navController.navigate("payment/success") },
                    onHome = onPaymentMoreBets,
                    onPurchases = { navController.navigate("tickets/purchases") },
                )
                PaymentRegistrationHost(registrationViewModel, paymentViewModel::registrationFinished, onPrizeRedirect)
            }
        }
        composable(
            route = "payment/cards?methodId={methodId}",
            arguments = listOf(navArgument("methodId") { type = NavType.LongType; defaultValue = 0L }),
        ) { entry ->
            val methodId = requireNotNull(entry.arguments).getLong("methodId").takeIf { it > 0 }
            val paymentViewModel = viewModel(key = "payment-cards-${methodId ?: 0}") {
                PaymentViewModel(
                    clock = appContainer.localClock,
                    recordRecentPurchase = appContainer.recordRecentPurchase,
                    mode = PaymentMode.CARDS,
                    selectedPaymentMethodId = methodId,
                    getMethods = appContainer.paymentMethods,
                    getCards = appContainer.savedCards,
                    deleteCard = appContainer.deleteSavedCard,
                    generatePixCharge = appContainer.generatePixCharge,
                    getPurchaseProgress = appContainer.purchaseProgress,
                    validateDam = appContainer.validateDam,
                    getCurrentCart = appContainer.currentCart,
                    dispatchers = appContainer.dispatchers,
                )
            }
            val registrationViewModel = paymentRegistrationViewModel(appContainer)
            PaymentDataRoute(
                onRegistration = { registrationViewModel.start() },
                onRedirect = onPaymentRedirect,
                mode = PaymentMode.CARDS,
                viewModel = paymentViewModel,
                visualAssets = PaymentVisualAssets(recargaPayLogoResourceId = R.drawable.recargapay),
                onBack = navController::popBackStack,
                onHelp = { navController.navigate("support/terms?tab=FAQ") },
                onPix = { navController.navigate("payment/pix-guidance") },
                onAddCard = { selected -> navController.navigate("payment/card-flow/$selected") },
                onOpenCard = { card -> navController.navigate("payment/card-flow/${card.paymentMethodId}?cardId=${Uri.encode(card.id)}") },
                onApproved = { navController.navigate("payment/success") },
                onHome = onPaymentMoreBets,
                onPurchases = { navController.navigate("tickets/purchases") },
            )
            PaymentRegistrationHost(registrationViewModel, paymentViewModel::registrationFinished, onPrizeRedirect)
        }
        composable(
            route = "payment/card-flow/{methodId}?cardId={cardId}",
            arguments = listOf(
                navArgument("methodId") { type = NavType.LongType },
                navArgument("cardId") { type = NavType.StringType; defaultValue = "" },
            ),
        ) { entry ->
            val methodId = requireNotNull(entry.arguments).getLong("methodId")
            val cardId = requireNotNull(entry.arguments).getString("cardId").orEmpty().ifBlank { null }
            val cardViewModel = viewModel(key = "payment-card-$methodId-${cardId.orEmpty()}") {
                PaymentCardViewModel(
                    clock = appContainer.localClock,
                    recordRecentPurchase = appContainer.recordRecentPurchase,
                    getCurrentCart = appContainer.currentCart,
                    paymentMethodId = methodId,
                    savedCardId = cardId,
                    getCards = appContainer.savedCards,
                    submitCardPayment = appContainer.submitCardPayment,
                    getPurchaseProgress = appContainer.purchaseProgress,
                    dispatchers = appContainer.dispatchers,
                )
            }
            val registrationViewModel = paymentRegistrationViewModel(appContainer)
            PaymentCardFlowRoute(
                onRegistration = { registrationViewModel.start() },
                onMoreBets = onPaymentMoreBets,
                onRedirect = onPaymentRedirect,
                viewModel = cardViewModel,
                onBack = navController::popBackStack,
                onSuccess = { navController.navigate("payment/success") },
                onReviewPurchases = {
                    navController.navigate("tickets/purchases") {
                        popUpTo(entry.destination.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
            PaymentRegistrationHost(registrationViewModel, cardViewModel::registrationFinished, onPrizeRedirect)
        }
        mapOf(
            "payment/add-card" to PaymentCardStage.FORM,
            "payment/card-preview" to PaymentCardStage.PREVIEW,
            "payment/cvv" to PaymentCardStage.CVV,
            "payment/confirm-card" to PaymentCardStage.CONFIRM,
        ).forEach { (route, initialStage) ->
            composable(route) { entry ->
                val cardViewModel = viewModel(key = "payment-card-compat-$route") {
                    PaymentCardViewModel(
                        clock = appContainer.localClock,
                        recordRecentPurchase = appContainer.recordRecentPurchase,
                        getCurrentCart = appContainer.currentCart,
                        paymentMethodId = 1L,
                        savedCardId = null,
                        initialStage = initialStage,
                        getCards = appContainer.savedCards,
                        submitCardPayment = appContainer.submitCardPayment,
                        getPurchaseProgress = appContainer.purchaseProgress,
                        dispatchers = appContainer.dispatchers,
                    )
                }
                val registrationViewModel = paymentRegistrationViewModel(appContainer)
                PaymentCardFlowRoute(
                    onRegistration = { registrationViewModel.start() },
                    onMoreBets = onPaymentMoreBets,
                    onRedirect = onPaymentRedirect,
                    viewModel = cardViewModel,
                    onBack = navController::popBackStack,
                    onSuccess = { navController.navigate("payment/success") },
                    onReviewPurchases = {
                        navController.navigate("tickets/purchases") {
                            popUpTo(entry.destination.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
                PaymentRegistrationHost(registrationViewModel, cardViewModel::registrationFinished, onPrizeRedirect)
            }
        }
        listOf(
            "payment/pix-guidance",
            "payment/confirmation",
            "payment/failure",
            "payment/success",
        ).forEach { route ->
            composable(route) {
                val spec = paymentScreens.first { it.route == route }
                val paymentViewModel = viewModel(key = "payment-terminal-$route") {
                    PaymentTerminalViewModel(spec)
                }
                PaymentTerminalRoute(
                    viewModel = paymentViewModel,
                    onBack = navController::popBackStack,
                    onComplete = {
                        val destination = nextDestination(route)
                        if (destination == null) navController.popBackStack()
                        else navController.navigate(destination)
                    },
                    onNavigate = navController::navigate,
                )
            }
        }
        composable(
            route = BettingSimulationRoute.pattern,
            arguments = listOf(navArgument("modalityId") { type = NavType.IntType },
                navArgument("contestTypeId") { type = NavType.LongType; defaultValue = 0L },
                navArgument("contestNumber") { type = NavType.IntType; defaultValue = 0 }),
        ) { entry ->
            val modalityId = requireNotNull(entry.arguments).getInt("modalityId")
            val requestedContestTypeId = requireNotNull(entry.arguments).getLong("contestTypeId")
            val requestedContestNumber = requireNotNull(entry.arguments).getInt("contestNumber").takeUnless { it == 0 }
            val editSeed = betToEdit?.takeIf { it.modalityId == modalityId }
            val preparedParameters = preparedBettingParameters.takeIf { editSeed == null }
            LaunchedEffect(entry) { preparedBettingParameters = null }
            val bettingGameViewModel = viewModel(key = "betting-game-$modalityId-$requestedContestTypeId-$requestedContestNumber-${editSeed?.id ?: "new"}") {
                BettingGameViewModel(
                    modalityId = modalityId,
                    getParameters = appContainer.bettingParameters,
                    initialParameters = preparedParameters,
                    addBet = appContainer.addBetToCart,
                    validateFavoriteBet = appContainer.validateFavoriteBet,
                    saveFavoriteBet = appContainer.saveFavoriteBet,
                    initialNumbers = editSeed?.numbers.orEmpty(),
                    initialContestTypeId = editSeed?.contestTypeId ?: requestedContestTypeId,
                    expectedContestNumber = requestedContestNumber.takeIf { editSeed == null },
                    initialClovers = editSeed?.clovers.orEmpty(),
                    initialTeam = editSeed?.favoriteTeam,
                    initialLuckyMonth = editSeed?.luckyMonth,
                    initialLotecaSelections = editSeed?.lotecaSelections.orEmpty(),
                    initialLotogolSelections = editSeed?.lotogolSelections.orEmpty(),
                    initialSuperSevenColumns = editSeed?.numberColumns?.takeIf { it.size > 1 }.orEmpty(),
                    initialRecurringDraws = editSeed?.recurringDraws ?: 0,
                    initialMirror = editSeed?.mirror == true,
                    dispatchers = appContainer.dispatchers,
                )
            }
            val gameCartSummary = activeCartSummary(appContainer.currentCart)
            BettingGameRoute(
                onHelp = { navController.navigate("support/terms?tab=FAQ") },
                fallbackModalityId = modalityId,
                cartTotal = gameCartSummary?.total ?: java.math.BigDecimal.ZERO,
                cartBetCount = gameCartSummary?.betCount ?: 0,
                onCart = { navController.navigate("cart/current") },
                shakeEnabled = appContainer.shakeSettings.enabled(),
                viewModel = bettingGameViewModel,
                onBack = {
                    betToEdit = null
                    navController.popBackStack()
                },
                onAdded = {
                    betToEdit = null
                    navController.navigate(betAdditionRoute(bettingGameViewModel.state.value))
                },
            )
        }
        composable("betting/combos") {
            val catalogViewModel = viewModel {
                BettingCatalogViewModel(
                    mode = BettingCatalogMode.COMBOS,
                    getModalities = appContainer.lotteryModalities,
                    getParameters = appContainer.bettingParameters,
                    getCombos = appContainer.betCombos,
                    addCombo = appContainer.addComboToCart,
                    dispatchers = appContainer.dispatchers,
                )
            }
            val cartSummary = activeCartSummary(appContainer.currentCart)
            BetCombosRoute(
                viewModel = catalogViewModel,
                cartTotal = cartSummary?.total ?: java.math.BigDecimal.ZERO,
                cartBetCount = cartSummary?.betCount ?: 0,
                onCart = { navController.navigate("cart/current") },
                onBack = navController::popBackStack,
                onSelect = { navController.navigate("betting/combo-detail/$it") },
            )
        }
        composable(
            route = "betting/combo-detail/{comboId}",
            arguments = listOf(navArgument("comboId") { type = NavType.IntType }),
        ) { entry ->
            val comboId = requireNotNull(entry.arguments).getInt("comboId")
            val catalogViewModel = viewModel(key = "betting-combo-$comboId") {
                BettingCatalogViewModel(
                    mode = BettingCatalogMode.COMBO_DETAIL,
                    comboId = comboId,
                    getModalities = appContainer.lotteryModalities,
                    getParameters = appContainer.bettingParameters,
                    getCombos = appContainer.betCombos,
                    addCombo = appContainer.addComboToCart,
                    dispatchers = appContainer.dispatchers,
                )
            }
            val cartSummary = activeCartSummary(appContainer.currentCart)
            BetComboDetailRoute(
                viewModel = catalogViewModel,
                cartTotal = cartSummary?.total ?: java.math.BigDecimal.ZERO,
                cartBetCount = cartSummary?.betCount ?: 0,
                onCart = { navController.navigate("cart/current") },
                onBack = navController::popBackStack,
                onAdded = {
                    val state = catalogViewModel.state.value
                    val amount = state.selectedCombo?.amount?.multiply(state.comboQuantity.toBigDecimal()) ?: java.math.BigDecimal.ZERO
                    navController.navigate("cart/included?modality=COMBO&amount=${amount.toPlainString()}" +
                        "&minimum=${state.parameters?.minimumCartAmount ?: java.math.BigDecimal.ZERO}")
                },
            )
        }
        composable("account/personal-data") {
            val personalDataViewModel = viewModel {
                PersonalDataViewModel(
                    getProfile = appContainer.accountProfile,
                    resolveZipCode = appContainer.resolveZipCode,
                    updatePersonalData = appContainer.updatePersonalData,
                    dispatchers = appContainer.dispatchers,
                    recalculateAuthorizedLimit = appContainer.recalculateAuthorizedLimit,
                    deferInitialLoad = true,
                )
            }
            LaunchedEffect(sessionIdentity, personalDataViewModel) {
                if (sessionIdentity != null) personalDataViewModel.load()
            }
            PersonalDataRoute(
                viewModel = personalDataViewModel,
                onBack = navController::popBackStack,
                onComplete = { navController.navigate("account/personal-data-confirmation") },
            )
        }
        composable("account/register") {
            val registrationViewModel = viewModel {
                RegistrationViewModel(
                    identityProvider = appContainer.currentUserIdentity,
                    resolveZipCode = appContainer.resolveZipCode,
                    registerAccount = appContainer.registerAccount,
                    dispatchers = appContainer.dispatchers,
                )
            }
            RegistrationRoute(
                viewModel = registrationViewModel,
                onBack = navController::popBackStack,
                onComplete = { navController.navigate("account/register-confirmation") },
                onTerms = { navController.navigate("support/terms") },
            )
        }
        composable("cart/local-review") {
            val reviewModel = viewModel {
                br.gov.caixa.loterias.feature.cart.LocalCartReviewViewModel(
                    appContainer.authenticatedSessionIdentity, appContainer.authenticatedSessionContent,
                    appContainer.legacyCartTransfer, appContainer.visitorCart, appContainer.currentCart,
                    appContainer.dispatchers,
                )
            }
            br.gov.caixa.loterias.feature.cart.LocalCartReviewRoute(reviewModel,
                onBack = navController::popBackStack,
                onRemoteCart = { navController.navigate("cart/current") },
            )
        }
        composable("cart/current") {
            val cartViewModel = viewModel {
                CartViewModel(
                    checkCheckoutLocation = appContainer.checkCheckoutLocation,
                    recordLocationRequest = appContainer.recordCheckoutLocationRequest,
                    hasRecentPurchase = appContainer.hasRecentPurchase,
                    verifyPaymentAccount = appContainer.verifyCartPaymentAccount,
                    serverTime = appContainer.serverTime,
                    getCurrentCart = appContainer.currentCart,
                    removeCartItem = appContainer.removeCartItem,
                    clearCart = appContainer.clearCart,
                    dispatchers = appContainer.dispatchers,
                    getBettingParameters = appContainer.bettingParameters,
                    validateCart = appContainer.validateCart,
                    validateFavoriteCartName = appContainer.validateFavoriteCartName,
                    saveCurrentCartFavorite = appContainer.saveCurrentCartFavorite,
                    authenticated = appContainer.currentUserIdentity.currentUser() != null,
                    networkAvailability = appContainer.networkAvailability,
                    localClock = appContainer.localClock,
                )
            }
            val cartState by cartViewModel.state.collectAsStateWithLifecycle()
            if (cartState.parametersUnavailable) {
                SimulationRecoveryPanel(
                    loading = cartState.loadingParameters,
                    message = cartState.parameterRetryMessage,
                    onRetry = cartViewModel::retryParameters,
                    onBack = cartViewModel::dismissParametersUnavailable,
                    onDismissMessage = cartViewModel::dismissParameterRetryMessage,
                    networkAvailability = appContainer.networkAvailability,
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
                onLogin = { visitorReturnRoute = "cart/current"; navController.navigate(LoginNavigation.authorize) },
                onAccountRequired = { destination ->
                    visitorReturnRoute = "cart/current"
                    accountCompletionRequired = true
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
                    if (it in accountCompletionRedirects) visitorReturnRoute = "cart/current"
                    navController.navigate(serviceRedirectRoute(it))
                },
                onBack = navController::popBackStack,
                onContinueBetting = { navController.navigate("betting/home") },
                onPayment = { navController.navigate("payment/method") },
                onPoolDetail = { code -> navController.navigate("betting/pool-detail/${Uri.encode(code)}") },
                onComboDetail = { id -> navController.navigate("betting/combo-detail/${id.toInt()}") },
            )
        }
        composable(
            route = "cart/included?modality={modality}&contest={contest}&drawDate={drawDate}&amount={amount}&minimum={minimum}",
            arguments = listOf(
                navArgument("modality") { type = NavType.StringType; defaultValue = "" },
                navArgument("contest") { type = NavType.IntType; defaultValue = 0 },
                navArgument("drawDate") { type = NavType.StringType; defaultValue = "" },
                navArgument("amount") { type = NavType.StringType; defaultValue = "0" },
                navArgument("minimum") { type = NavType.StringType; defaultValue = "0" },
            ),
        ) { entry ->
            val additionViewModel = viewModel<CartAdditionViewModel> {
                CartAdditionViewModel(
                    kind = CartAdditionKind.BET,
                    serverTime = appContainer.serverTime,
                    modality = entry.arguments?.getString("modality").orEmpty(),
                    contest = entry.arguments?.getInt("contest") ?: 0,
                    drawDate = entry.arguments?.getString("drawDate").orEmpty(),
                    amount = entry.arguments?.getString("amount")?.toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO,
                    minimumCartAmount = entry.arguments?.getString("minimum")?.toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO,
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
                onHelp = { id, name -> navController.navigate("support/game-help?modalityId=$id&modalityName=${Uri.encode(name)}") },
            )
        }
        composable(
            route = "cart/pool-included?expiresAt={expiresAt}&modality={modality}" +
                "&contest={contest}&drawDate={drawDate}&amount={amount}",
            arguments = listOf(
                navArgument("expiresAt") { type = NavType.StringType; defaultValue = "" },
                navArgument("modality") { type = NavType.StringType; defaultValue = "" },
                navArgument("contest") { type = NavType.IntType; defaultValue = 0 },
                navArgument("drawDate") { type = NavType.StringType; defaultValue = "" },
                navArgument("amount") { type = NavType.StringType; defaultValue = "0" },
            ),
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
                    amount = amount,
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
                onHelp = { id, name -> navController.navigate("support/game-help?modalityId=$id&modalityName=${Uri.encode(name)}") },
            )
        }
        composable("cart/favorite-carts") { entry ->
            val favoriteCartsViewModel = viewModel {
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
                onHelp = { navController.navigate("support/terms?tab=FAQ") },
                onBack = navController::popBackStack,
                onOpen = { navController.navigate("cart/favorite-detail/$it") },
                onCartReady = { navController.navigate("cart/current") },
            )
        }
        composable(
            route = "cart/favorite-detail/{cartId}",
            arguments = listOf(navArgument("cartId") { type = NavType.LongType }),
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
                    dispatchers = appContainer.dispatchers,
                )
            }
            FavoriteCartDetailRoute(
                viewModel = favoriteCartsViewModel,
                onHelp = { navController.navigate("support/terms?tab=FAQ") },
                onBack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("favoriteDetailReturned", true)
                    navController.popBackStack()
                },
            )
        }
        composable(
            route = LoginNavigation.pattern,
            arguments = listOf(navArgument("authorize") { type = NavType.BoolType; defaultValue = false }),
        ) { entry ->
            val notificationPermissionReady = br.gov.caixa.loterias.apostas.push.loginNotificationPermissionReady()
            val loginViewModel = viewModel {
                LoginViewModel(
                    authentication = appContainer.authentication,
                    dispatchers = appContainer.dispatchers,
                    verifyAccess = appContainer.verifyPostLoginAccess,
                )
            }
            LoginRoute(
                onSessionInvalidated = onLogout,
                authorizeOnEnter = notificationPermissionReady && entry.arguments?.getBoolean("authorize") == true,
                onAuthorizationCancelled = {
                    if (entry.arguments?.getBoolean("authorize") == true) { visitorReturnRoute = null; navController.popBackStack() }
                },
                viewModel = loginViewModel,
                authentication = appContainer.authentication,
                versionName = BuildConfig.VERSION_NAME,
                onAuthenticated = { destination ->
                    val authenticatedOwner = appContainer.authenticatedSessionIdentity.captureIdentity()?.ownerKey
                    loggedDetailContext.reconcile(authenticatedOwner)
                    if (destination == "home" && pendingRestoredRoute != null) {
                        val restored = pendingRestoredRoute
                        pendingRestoredRoute = null
                        visitorReturnRoute = if (loggedDetailContext.restorationOutcome == DetailRestorationOutcome.Restored) restored else null
                        visitorReturnOwnerKey = authenticatedOwner
                    }
                    visitorReturnRoute = authenticatedContinuation(visitorReturnRoute, visitorReturnOwnerKey, authenticatedOwner)
                    accountCompletionRequired = destination != "home"
                    val requested = visitorReturnRoute
                    if (destination == "home") { visitorReturnRoute = null; visitorReturnOwnerKey = null }
                    val target = if (destination == "home" && requested != null && !requested.startsWith("home/")) requested else destination
                    if (destination == "home" && requested?.startsWith("home/") == true) resumedHomeAction = requested
                    navController.navigate(if (destination == "home") "home" else target) {
                        // Private destinations must load again under the validated account.
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                    if (destination == "home" && target != "home") {
                        navController.navigate(target) { launchSingleTop = true }
                    }
                },
                onTryNow = {
                    loggedDetailContext.clear()
                    pendingRestoredRoute = null
                    visitorReturnRoute = null
                    visitorReturnOwnerKey = null
                    resumedHomeAction = null
                    accountCompletionRequired = false
                    navController.navigate("home") { popUpTo(navController.graph.id) { inclusive = true } }
                },
            )
        }
        composable(
            route = "tickets/bet-detail?original={original}&resgate={resgate}&variant={variant}",
            arguments = listOf(
                navArgument("original") { type = NavType.BoolType; defaultValue = false },
                navArgument("resgate") { type = NavType.BoolType; defaultValue = false },
                navArgument("variant") { type = NavType.StringType; defaultValue = "" },
            ),
        ) { entry ->
            val original = entry.arguments?.getBoolean("original") == true
            val redeemed = entry.arguments?.getBoolean("resgate") == true
            val variant = entry.arguments?.getString("variant").orEmpty()
            val bet = selectedBet ?: if (BuildConfig.ENABLE_MOCK_BACKEND) {
                mockConfirmedBet(variant, original, redeemed)
            } else {
                null
            }
            if (bet == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
                return@composable
            }
            val detailViewModel = viewModel(key = "bet-detail-${bet.id}") {
                BetDetailViewModel(
                    bet,
                    appContainer.recoverConfirmedBet,
                    appContainer.downloadBetReceipt,
                    appContainer.validateFavoriteBet,
                    appContainer.saveFavoriteBet,
                    appContainer.addBetToCart,
                    appContainer.dispatchers,
                    getBettingParameters = appContainer.bettingParameters,
                    loadConfirmedBet = appContainer.loadConfirmedBet,
                    downloadPrizeReceipt = appContainer.downloadPrizeReceipt,
                )
            }
            BetDetailDataRoute(
                viewModel = detailViewModel,
                onBack = navController::popBackStack,
                onServiceRedirect = onTicketServiceRedirect,
                onViewResult = { recoveredBet ->
                    selectedBet = recoveredBet
                    navController.navigate("results/confirmed-bet")
                },
                onRedeemPrize = if (bet.id > 0) ({ recoveredBet ->
                    navController.navigate("retail/prize/${recoveredBet.id}")
                }) else null,
                onHelp = {
                    navController.navigate(
                        "support/game-help?modalityId=${bet.modalityId}&modalityName=${Uri.encode(bet.modality)}",
                    )
                },
                onEdit = { recoveredBet ->
                    betToEdit = recoveredBet
                    navController.navigate("betting/simulation/${recoveredBet.modalityId}")
                },
                onCart = { navController.navigate("cart/current") },
            )
        }
        composable(
            route = "retail/prize/{betId}",
            arguments = listOf(navArgument("betId") { type = NavType.LongType }),
        ) { entry ->
            val betId = requireNotNull(entry.arguments).getLong("betId")
            PrizeRedemptionDestination(
                appContainer = appContainer,
                betId = betId,
                keySuffix = betId.toString(),
                onRedirect = onPrizeRedirect,
                onBetEntry = onPrizeBetEntry,
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
                },
            )
        }
        composable("retail/location") {
            val locationViewModel = viewModel { LocationGateViewModel() }
            LocationGateRoute(
                viewModel = locationViewModel,
                versionName = BuildConfig.VERSION_NAME,
                lotteryIconResourceId = R.drawable.icon_loteria,
                mockInsideBrazil = BuildConfig.ENABLE_MOCK_BACKEND,
                onAllowed = {
                    navController.navigate("retail/search") {
                        popUpTo("retail/location") { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        mapOf(
            "retail/prize" to Triple<PrizeRedemptionChannel?, PrizeRedemptionChannel?, String>(null, null, "options"),
            "retail/prize-guidance" to Triple(PrizeRedemptionChannel.LOTTERY, null, "guidance"),
            "retail/prize-lottery" to Triple(PrizeRedemptionChannel.LOTTERY, null, "lottery"),
            "retail/prize-agency" to Triple(PrizeRedemptionChannel.AGENCY, null, "agency"),
            "retail/prize-pix" to Triple(PrizeRedemptionChannel.PIX, null, "pix"),
            "retail/prize-confirmation" to Triple(null, PrizeRedemptionChannel.PIX, "pix-success"),
            "retail/prize-mercado-pago" to Triple(PrizeRedemptionChannel.MERCADO_PAGO, null, "mp"),
            "retail/prize-mercado-pago-success" to Triple(null, PrizeRedemptionChannel.MERCADO_PAGO, "mp-success"),
        ).forEach { (route, setup) ->
            composable(route) {
                if (!BuildConfig.ENABLE_MOCK_BACKEND) {
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
                    onRedirect = onPrizeRedirect,
                    onBetEntry = onPrizeBetEntry,
                    initialChannel = setup.first,
                    previewSuccessChannel = setup.second,
                    onBack = navController::popBackStack,
                    onHelp = { navController.navigate("support/terms?tab=FAQ") },
                    onHome = {
                        navController.navigate("home") { popUpTo("home") { inclusive = false }; launchSingleTop = true }
                    },
                    onMyBets = {
                        navController.navigate("tickets/my-bets") { launchSingleTop = true }
                    },
                )
            }
        }
        composable("tickets/my-bets") {
            var repeatBet by remember { mutableStateOf<ConfirmedBet?>(null) }
            var repeatRequest by remember { mutableStateOf(0) }
            val ticketsViewModel = viewModel {
                TicketsViewModel(
                    appContainer.confirmedBets,
                    appContainer.purchases,
                    appContainer.purchaseBets,
                    appContainer.checkTicket,
                    appContainer.dispatchers,
                    appContainer.confirmedBetsConfiguration,
                    appContainer.lotteryModalities,
                    appContainer.purchaseMonths,
                    appContainer.purchasePaymentMethods,
                    appContainer.purchaseStatuses,
                )
            }
            val repeatViewModel = repeatBet?.let { bet ->
                val repeatViewModel = viewModel(key = "list-repeat-${bet.id}-$repeatRequest") {
                    BetDetailViewModel(
                        bet, appContainer.recoverConfirmedBet, appContainer.downloadBetReceipt,
                        appContainer.validateFavoriteBet, appContainer.saveFavoriteBet,
                        appContainer.addBetToCart, appContainer.dispatchers,
                        getBettingParameters = appContainer.bettingParameters,
                    loadConfirmedBet = appContainer.loadConfirmedBet,
                    downloadPrizeReceipt = appContainer.downloadPrizeReceipt,
                    )
                }
                LaunchedEffect(repeatViewModel) {
                    // A recuperação deve terminar antes da repetição para não apagar seus diálogos.
                    repeatViewModel.state.first { !it.loading }
                    if (repeatViewModel.state.value.detailsReady) repeatViewModel.addCurrentBetToCart().join()
                }
                repeatViewModel
            }
            androidx.compose.foundation.layout.Box {
            ConfirmedBetsDataRoute(
                viewModel = ticketsViewModel,
                onHelp = { navController.navigate("support/terms?tab=FAQ") },
                onAddToCart = { bet -> repeatRequest += 1; repeatBet = bet },
                onBetNow = { navController.navigate("home") { popUpTo("home"); launchSingleTop = true } },
                onBack = navController::popBackStack,
                onOpen = { bet ->
                    selectedBet = bet
                    navController.navigate(
                        "tickets/bet-detail?original=${bet.originalRecurringBet}&resgate=${bet.redeemedPrize}",
                    )
                },
            )
            repeatViewModel?.let { BetRepeatActionsOverlay(it, onServiceRedirect = onTicketServiceRedirect) }
            }
        }
        composable("results/confirmed-bet") {
            val bet = selectedBet ?: if (BuildConfig.ENABLE_MOCK_BACKEND) {
                mockConfirmedBet("").copy(id = 100L)
            } else null
            if (bet == null || bet.id <= 0) {
                LaunchedEffect(Unit) {
                    if (!navController.popBackStack()) {
                        navController.navigate("tickets/my-bets") {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            } else {
                val resultModel = viewModel(key = "confirmed-results-${bet.id}") {
                    br.gov.caixa.loterias.feature.results.ConfirmedBetResultsViewModel(
                        bet, appContainer.confirmedBetResults, appContainer.dispatchers,
                    )
                }
                br.gov.caixa.loterias.feature.results.ConfirmedBetResultRoute(
                    bet = bet, viewModel = resultModel, onBack = navController::popBackStack,
                    onComplete = { navController.navigate("tickets/my-bets") { launchSingleTop = true } },
                    onServiceRedirect = onTicketServiceRedirect,
                    onRedeemPrize = { navController.navigate("retail/prize/${bet.id}") },
                )
            }
        }
        composable("tickets/purchases") {
            val ticketsViewModel = viewModel {
                TicketsViewModel(
                    appContainer.confirmedBets,
                    appContainer.purchases,
                    appContainer.purchaseBets,
                    appContainer.checkTicket,
                    appContainer.dispatchers,
                    appContainer.confirmedBetsConfiguration,
                    appContainer.lotteryModalities,
                    appContainer.purchaseMonths,
                    appContainer.purchasePaymentMethods,
                    appContainer.purchaseStatuses,
                )
            }
            PurchasesDataRoute(
                viewModel = ticketsViewModel,
                onBack = navController::popBackStack,
                onOpen = { purchase ->
                    selectedPurchase = purchase
                    navController.navigate("tickets/purchase-detail")
                },
            )
        }
        composable("tickets/purchase-detail") {
            val purchase = selectedPurchase ?: visualTestPurchase(BuildConfig.ENABLE_MOCK_BACKEND)
            if (purchase == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
            } else {
                val ticketsViewModel = viewModel {
                    TicketsViewModel(
                        appContainer.confirmedBets,
                        appContainer.purchases,
                        appContainer.purchaseBets,
                        appContainer.checkTicket,
                        appContainer.dispatchers,
                        appContainer.confirmedBetsConfiguration,
                        appContainer.lotteryModalities,
                        appContainer.purchaseMonths,
                        appContainer.purchasePaymentMethods,
                        appContainer.purchaseStatuses,
                        appContainer.purchaseDetails,
                        appContainer.validateFavoriteCartName,
                        appContainer.savePurchaseAsFavorite,
                        appContainer.validateDam,
                        getPixPurchaseStatus = appContainer.purchaseProgress,
                        pixNoticeClock = appContainer.localClock,
                    )
                }
                val registrationViewModel = paymentRegistrationViewModel(appContainer)
                val onPurchaseDetailRedirect: (br.gov.caixa.loterias.core.domain.service.ServiceRedirect) -> Unit = { destination ->
                    if (destination.name in setOf("LOGIN", "REGISTER_ACCOUNT", "ACCEPT_TERMS")) {
                        visitorReturnRoute = "tickets/purchase-detail"
                        visitorReturnOwnerKey = appContainer.authenticatedSessionIdentity.captureIdentity()?.ownerKey
                    }
                    navController.navigate(serviceRedirectRoute(destination)) { launchSingleTop = true }
                }
                PurchaseDetailDataRoute(
                    ticketsViewModel,
                    purchase,
                    navController::popBackStack,
                    onOpen = { route ->
                        if (route == "home") {
                            navController.navigate("home") { popUpTo("home") { inclusive = true } }
                        } else navController.navigate(route)
                    },
                    onPixRedirect = { destination ->
                        onPurchaseDetailRedirect(br.gov.caixa.loterias.core.domain.service.ServiceRedirect.valueOf(destination.name))
                    },
                    onPixRegistration = { registrationViewModel.start() },
                )
                PaymentRegistrationHost(registrationViewModel, ticketsViewModel::resumePixPurchaseAfterRedirect, onPurchaseDetailRedirect)
            }
        }
        composable("tickets/read") {
                val ticketsViewModel = viewModel(key = "scanner-tickets-read") {
                    TicketsViewModel(
                        appContainer.confirmedBets,
                        appContainer.purchases,
                        appContainer.purchaseBets,
                        appContainer.checkTicket,
                        appContainer.dispatchers,
                        appContainer.confirmedBetsConfiguration,
                        appContainer.lotteryModalities,
                        appContainer.purchaseMonths,
                        appContainer.purchasePaymentMethods,
                        appContainer.purchaseStatuses,
                    )
                }
                TicketScannerRoute(
                    ticketsViewModel,
                    navController::popBackStack,
                    onManual = { navController.navigate("tickets/manual-code") },
                    onResult = {
                        checkedTicketBets = it
                        navController.navigate("tickets/read-result")
                    },
                )
        }
        composable("tickets/read-game-choice") {
            val ticketsViewModel = viewModel(key = "ticket-game-choice") {
                TicketsViewModel(
                    appContainer.confirmedBets,
                    appContainer.purchases,
                    appContainer.purchaseBets,
                    appContainer.checkTicket,
                    appContainer.dispatchers,
                    appContainer.confirmedBetsConfiguration,
                    appContainer.lotteryModalities,
                    appContainer.purchaseMonths,
                    appContainer.purchasePaymentMethods,
                    appContainer.purchaseStatuses,
                )
            }
            TicketGameChoiceRoute(
                viewModel = ticketsViewModel,
                onBack = navController::popBackStack,
                onHelp = { navController.navigate("support/terms?tab=FAQ") },
                onSave = { navController.navigate("tickets/read") },
            )
        }
        composable("tickets/manual-code") {
            val ticketsViewModel = viewModel {
                TicketsViewModel(
                    appContainer.confirmedBets,
                    appContainer.purchases,
                    appContainer.purchaseBets,
                    appContainer.checkTicket,
                    appContainer.dispatchers,
                    appContainer.confirmedBetsConfiguration,
                    appContainer.lotteryModalities,
                    appContainer.purchaseMonths,
                    appContainer.purchasePaymentMethods,
                    appContainer.purchaseStatuses,
                )
            }
            ManualTicketDataRoute(
                ticketsViewModel,
                navController::popBackStack,
                onResult = {
                    checkedTicketBets = it
                    navController.navigate("tickets/read-result")
                },
                onHelp = { navController.navigate("support/terms?tab=FAQ") },
            )
        }
        composable("tickets/read-result") {
            // Reuse the query route's existing VM; the query DTO is not a history of bets.
            val queryEntry = navController.previousBackStackEntry
            val queryViewModel: TicketsViewModel? = queryEntry?.let { entry ->
                viewModel<TicketsViewModel>(viewModelStoreOwner = entry,
                    key = if (entry.destination.route == "tickets/read") "scanner-tickets-read" else null)
            }
            val queryState = queryViewModel?.state?.collectAsStateWithLifecycle()?.value
            val ticketResult = queryState?.ticketResult
            var currentGame by remember(ticketResult) {
                mutableStateOf<br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters?>(null)
            }
            LaunchedEffect(ticketResult) {
                ticketResult?.modalityId?.let { modalityId ->
                    when (val parameters = appContainer.bettingParameters()) {
                        is br.gov.caixa.loterias.core.common.AppResult.Success ->
                            currentGame = parameters.value.games.firstOrNull { it.modality.id == modalityId }
                        is br.gov.caixa.loterias.core.common.AppResult.Failure -> Unit
                    }
                }
            }
            TicketResultDataRoute(
                bets = checkedTicketBets,
                visualData = ticketResult?.let {
                    br.gov.caixa.loterias.feature.tickets.ticketResultVisualData(it, currentGame)
                },
                onBetNow = { navController.navigate("home") { popUpTo("home"); launchSingleTop = true } },
                onBack = navController::popBackStack,
                onSave = { navController.navigate("tickets/my-bets") },
            )
        }
        composable(AppDestination.MARKETPLACE_FILTER.route) {
            val filterViewModel = viewModel {
                MarketplaceFilterViewModel(
                    appContainer.marketplaceFilterParameters,
                    appContainer.bettingParameters,
                    appContainer.searchRetailers,
                    appContainer.dispatchers,
                )
            }
            MarketplaceFilterRoute(
                viewModel = filterViewModel,
                onBack = navController::popBackStack,
                onApply = { filter ->
                    marketplaceFilter = filter
                    navController.navigate("betting/pools") {
                        popUpTo("betting/pools") { inclusive = true }
                    }
                },
            )
        }
        composable("betting/pools") {
            val marketplaceViewModel = viewModel(key = "marketplace-${marketplaceFilter.hashCode()}") {
                MarketplaceViewModel(
                    appContainer.searchMarketplace,
                    marketplaceFilter,
                    appContainer.addMarketplacePoolToCart,
                    appContainer.favoriteRetailers,
                    appContainer.setRetailerFavorite,
                    appContainer.dispatchers,
                )
            }
            MarketplaceRoute(
                viewModel = marketplaceViewModel,
                onBack = navController::popBackStack,
                onFilter = { navController.navigate(AppDestination.MARKETPLACE_FILTER.route) },
                onHelp = { navController.navigate("support/terms?tab=FAQ") },
                onRetailerSelected = { retailer ->
                    selectedMarketplaceRetailer = retailer
                    navController.navigate("betting/pool-view")
                },
                onPoolSelected = { code -> navController.navigate("betting/pool-detail/${Uri.encode(code)}") },
                onPoolReserved = { confirmation ->
                    navController.navigate(confirmation.poolAdditionRoute())
                },
            )
        }
        composable("betting/pool-view") {
            val retailerFilter = selectedMarketplaceRetailer
                ?.let { marketplaceFilter.withLottery(it.id.toString()) }
                ?: marketplaceFilter
            val marketplaceViewModel = viewModel(key = "marketplace-pool-view-${retailerFilter.hashCode()}") {
                MarketplaceViewModel(
                    appContainer.searchMarketplace,
                    retailerFilter,
                    appContainer.addMarketplacePoolToCart,
                    appContainer.favoriteRetailers,
                    appContainer.setRetailerFavorite,
                    appContainer.dispatchers,
                )
            }
            MarketplaceRoute(
                viewModel = marketplaceViewModel,
                onBack = {
                    selectedMarketplaceRetailer = null
                    navController.popBackStack()
                },
                onFilter = { navController.navigate(AppDestination.MARKETPLACE_FILTER.route) },
                onHelp = { navController.navigate("support/terms?tab=FAQ") },
                onRetailerSelected = {},
                onPoolSelected = { code -> navController.navigate("betting/pool-detail/${Uri.encode(code)}") },
                onPoolReserved = { confirmation ->
                    navController.navigate(confirmation.poolAdditionRoute())
                },
                retailerView = true,
            )
        }
        composable("retail/search") {
            val retailSearchViewModel = viewModel {
                RetailSearchViewModel(
                    searchRetailers = appContainer.searchRetailers,
                    setFavorite = appContainer.setRetailerFavorite,
                    dispatchers = appContainer.dispatchers,
                )
            }
            RetailSearchRoute(retailSearchViewModel, navController::popBackStack, onHelp = { navController.navigate("support/terms?tab=FAQ") })
        }
        composable(
            "results/home/{modality}",
            arguments = listOf(navArgument("modality") { type = NavType.StringType }),
        ) { entry ->
            val modality = entry.arguments?.getString("modality").orEmpty()
            val model = viewModel(key = "home-result-$modality") {
                ResultsViewModel(appContainer.latestResult, appContainer.resultByContest, appContainer.dispatchers)
            }
            LaunchedEffect(modality) { model.selectModality(modality) }
            ResultsRoute(model, navController::popBackStack, showDetail = true)
        }
        listOf("results/modalities", "results/detail").forEach { route ->
            composable(route) {
                val resultsViewModel = viewModel(key = "results-$route") {
                    ResultsViewModel(
                        appContainer.latestResult,
                        appContainer.resultByContest,
                        appContainer.dispatchers,
                    )
                }
                ResultsRoute(
                    resultsViewModel,
                    navController::popBackStack,
                    showDetail = route == "results/detail",
                )
            }
        }
        composable(
            route = "betting/pool-detail/{poolCode}",
            arguments = listOf(navArgument("poolCode") { type = NavType.StringType }),
        ) { entry ->
            val poolCode = requireNotNull(entry.arguments?.getString("poolCode"))
            val detailViewModel = viewModel(key = "pool-detail-$poolCode") {
                MarketplaceDetailViewModel(
                    poolCode,
                    appContainer.marketplaceDetail,
                    appContainer.addMarketplacePoolToCart,
                    appContainer.dispatchers,
                )
            }
            MarketplaceDetailRoute(
                viewModel = detailViewModel,
                onBack = navController::popBackStack,
                onPoolReserved = { confirmation ->
                    navController.navigate(confirmation.poolAdditionRoute())
                },
            )
        }
        composable(
            route = "support/game-help?modalityId={modalityId}&modalityName={modalityName}",
            arguments = listOf(
                navArgument("modalityId") { type = NavType.IntType; defaultValue = 2 },
                navArgument("modalityName") { type = NavType.StringType; defaultValue = "Mega-Sena" },
            ),
        ) { entry ->
            val modalityId = entry.arguments?.getInt("modalityId") ?: 2
            val modalityName = entry.arguments?.getString("modalityName").orEmpty().ifBlank { "Mega-Sena" }
            val supportViewModel = viewModel(key = "support-game-help-$modalityId-$modalityName") {
                SupportViewModel(
                    getSections = appContainer.supportSections,
                    getQuestions = appContainer.supportQuestions,
                    getTerms = appContainer.terms,
                    acceptTermsUseCase = appContainer.acceptTerms,
                    privacyHtml = "<h2>Política de privacidade</h2>",
                    dispatchers = appContainer.dispatchers,
                    initialState = SupportUiState(
                        mode = SupportMode.GAME_HELP,
                        gameHelpModalityId = modalityId,
                        gameHelpModalityName = modalityName,
                    ),
                )
            }
            SupportRoute(
                viewModel = supportViewModel,
                onBack = navController::popBackStack,
                onAcceptTerms = { navController.popBackStack() },
            )
        }
        supportScreens.filterNot { it.route == "support/game-help" }.forEach { spec ->
            val routePattern = if (spec.route == "support/terms") "support/terms?tab={tab}" else spec.route
            composable(routePattern, arguments = if (spec.route == "support/terms") listOf(
                navArgument("tab") { type = NavType.StringType; defaultValue = "TERMS" },
            ) else emptyList()) { supportEntry ->
                val initialState = when (spec.route) {
                    "support/terms-view" -> SupportUiState(
                        mode = SupportMode.TERMS_READ_ONLY,
                        selectedTab = SupportTab.TERMS,
                    )
                    "support/terms" -> SupportUiState(
                        selectedTab = if (supportEntry.arguments?.getString("tab") == "FAQ") SupportTab.FAQ else SupportTab.TERMS,
                        showTermsTabs = true,
                    )
                    "support/terms-acceptance" -> SupportUiState(
                        selectedTab = SupportTab.TERMS,
                        acceptanceRequired = true,
                    )
                    else -> SupportUiState(selectedTab = SupportTab.FAQ)
                }
                val supportViewModel = viewModel(key = "support-${spec.route}-${initialState.selectedTab}") {
                    SupportViewModel(
                        getSections = appContainer.supportSections,
                        getQuestions = appContainer.supportQuestions,
                        getTerms = appContainer.terms,
                        acceptTermsUseCase = appContainer.acceptTerms,
                        privacyHtml = "<h2>Política de privacidade</h2>",
                        dispatchers = appContainer.dispatchers,
                        initialState = initialState,
                    )
                }
                SupportRoute(
                    viewModel = supportViewModel,
                    onBack = navController::popBackStack,
                    onAcceptTerms = {
                        if (spec.route == "support/terms-acceptance") recheckAccountCompletion() else navController.popBackStack()
                    },
                )
            }
        }
    }
    br.gov.caixa.loterias.feature.account.LogoutOverlay(logoutViewModel, appContainer.endSession)
}

@Composable
private fun PrizeRedemptionDestination(
    appContainer: AppContainer,
    betId: Long,
    keySuffix: String,
    onRedirect: (br.gov.caixa.loterias.core.domain.retail.PrizeRedirect) -> Unit,
    onBetEntry: (BetEntryDecision) -> Unit,
    initialChannel: PrizeRedemptionChannel? = null,
    previewSuccessChannel: PrizeRedemptionChannel? = null,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onHome: () -> Unit,
    onMyBets: () -> Unit,
) {
    val authenticatedUser = appContainer.currentUserIdentity.currentUser()
    val prizeViewModel = viewModel(key = "prize-redemption-$keySuffix") {
        PrizeRedemptionViewModel(
            betId = betId,
            initialEmail = authenticatedUser?.email
                ?: if (BuildConfig.ENABLE_MOCK_BACKEND) "mock@caixa.gov.br" else "",
            initialCpf = authenticatedUser?.cpf
                ?: if (BuildConfig.ENABLE_MOCK_BACKEND) "12345678901" else "",
            getDetails = appContainer.prizeDetails,
            generateLotteryCode = appContainer.lotteryRedemptionCode,
            consultPixKey = appContainer.consultPixRedemptionKey,
            redeemPrize = appContainer.redeemPrize,
            cancelPix = appContainer.cancelPixRedemption,
            downloadBetReceiptUseCase = appContainer.downloadBetReceipt,
            downloadPrizeReceiptUseCase = appContainer.downloadPrizeReceipt,
            getBettingParameters = appContainer.bettingParameters,
            prepareBetEntry = appContainer.prepareBetEntry,
            localClock = appContainer.localClock,
            networkAvailability = appContainer.networkAvailability,
            dispatchers = appContainer.dispatchers,
        )
    }
    val registrationViewModel = paymentRegistrationViewModel(appContainer)
    PrizeRedemptionRoute(
        viewModel = prizeViewModel,
        onRedirect = onRedirect,
        onBetEntry = onBetEntry,
        initialChannel = initialChannel,
        previewSuccessChannel = previewSuccessChannel,
        allowControlledSuccessPreview = BuildConfig.ENABLE_MOCK_BACKEND,
        onBack = onBack,
        onHelp = onHelp,
        onHome = onHome,
        onMyBets = onMyBets,
        onRegistration = { email -> registrationViewModel.start(email) },
    )
    PaymentRegistrationHost(registrationViewModel, prizeViewModel::registrationFinished, onRedirect)
}

internal fun visualTestPurchase(mockBackendEnabled: Boolean): Purchase? =
    if (mockBackendEnabled) {
        Purchase(
            id = 99,
            date = "04/09/2026",
            status = "Aprovada",
            paymentMethod = "Pix",
            total = java.math.BigDecimal("35.00"),
        )
    } else {
        null
    }

private fun MarketplacePoolConfirmation.poolAdditionRoute(): String =
    "cart/pool-included?expiresAt=${Uri.encode(expiresAt)}" +
        "&modality=${Uri.encode(modality)}" +
        "&contest=$contest" +
        "&drawDate=${Uri.encode(drawDate)}" +
        "&amount=${Uri.encode(amount.toPlainString())}"

private fun betAdditionRoute(state: br.gov.caixa.loterias.feature.betting.BettingGameUiState): String {
    val game = state.game
    return "cart/included?modality=${Uri.encode(game?.modality?.name.orEmpty())}" +
        "&contest=${game?.contestNumber ?: 0}&drawDate=${Uri.encode(game?.drawDate.orEmpty())}" +
        "&amount=${state.totalPrice.toPlainString()}&minimum=${state.minimumCartAmount.toPlainString()}"
}
