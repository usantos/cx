package br.gov.caixa.loterias.feature.betting.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.destination.BettingSimulationRoute
import br.gov.caixa.loterias.core.navigation.destination.serviceRedirectRoute
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.accountCompletionRedirects
import br.gov.caixa.loterias.core.navigation.state.activeCartSummary
import br.gov.caixa.loterias.core.navigation.state.activeResumingCartSummary
import br.gov.caixa.loterias.core.navigation.state.postLoginDestinationRoute
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingTarget
import br.gov.caixa.loterias.feature.betting.presentation.coordinator.RapidEntryCoordinator
import br.gov.caixa.loterias.feature.betting.presentation.route.BetComboDetailRoute
import br.gov.caixa.loterias.feature.betting.presentation.route.BetCombosRoute
import br.gov.caixa.loterias.feature.betting.presentation.route.BettingGameRoute
import br.gov.caixa.loterias.feature.betting.presentation.route.FavoriteBetsRoute
import br.gov.caixa.loterias.feature.betting.presentation.route.LotteryModalitiesRoute
import br.gov.caixa.loterias.feature.betting.presentation.route.RapidConfigurationRoute
import br.gov.caixa.loterias.feature.betting.presentation.route.RapidEntryRoute
import br.gov.caixa.loterias.feature.betting.presentation.route.RapidListRoute
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingCatalogMode
import br.gov.caixa.loterias.feature.betting.presentation.state.NavigationPreferenceMode
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidCompletion
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingCatalogViewModel
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.FavoriteBetsViewModel
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.RapidConfigurationViewModel
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.RapidListViewModel
// O registro declarativo de rotas mantém a fronteira compartilhada de navegação e sessão visível em um único local.
@Suppress("LongMethod", "CyclomaticComplexMethod")
fun NavGraphBuilder.bettingGraph(
    appContainer: BettingNavigationDependencies,
    navController: NavHostController,
    navigation: AppNavigationState,
    session: SessionNavigationActions,
) {
    listOf("betting/home", "betting/modalities").forEach { route ->
        composable(route) {
            val catalogViewModel = viewModel(key = "modalities-$route") {
                BettingCatalogViewModel(
                    checkpointSession = appContainer.bettingCheckpoints.open(),
                    recoveryMessages = appContainer.recoveryMessages,
                    mode = BettingCatalogMode.MODALITIES,
                    getModalities = appContainer.lotteryModalities,
                    getParameters = appContainer.bettingParameters,
                    getCombos = appContainer.betCombos,
                    addCombo = appContainer.addComboToCart,
                    dispatchers = appContainer.dispatchers
                )
            }
            LotteryModalitiesRoute(
                viewModel = catalogViewModel,
                onBack = navController::popBackStack,
                onSelect = { modalityId ->
                    navigation.details.betToEdit = null
                    navigation.preparedBettingParameters = catalogViewModel.state.value.parameters
                    navController.navigate("betting/simulation/$modalityId")
                }
            )
        }
    }
    composable("betting/simulation") {
        val bettingGameViewModel = viewModel(key = "betting-game-default") {
            BettingGameViewModel(
                modalityId = null,
                serverTime = appContainer.serverTime,
                checkpointSession = appContainer.bettingCheckpoints.open(),
                recoveryMessages = appContainer.recoveryMessages,
                getParameters = appContainer.bettingParameters,
                getSpecialContests = appContainer.bettingSpecialContests,
                addBet = appContainer.addBetToCart,
                validateFavoriteBet = appContainer.validateFavoriteBet,
                saveFavoriteBet = appContainer.saveFavoriteBet,
                dispatchers = appContainer.dispatchers
            )
        }
        var gameCartRevision by remember { mutableIntStateOf(0) }
        val gameCartSummary = activeResumingCartSummary(appContainer.currentCart, gameCartRevision)
        BettingGameRoute(
            isAuthenticated = session.identity != null,
            onFavoriteLoginRequired = if (session.identity == null) {
                { session.requestVisitorLogin("betting/simulation") }
            } else {
                null
            },
            onHelp = { navController.navigate("support/terms?tab=FAQ") },
            cartTotal = gameCartSummary?.total ?: java.math.BigDecimal.ZERO,
            cartBetCount = gameCartSummary?.betCount ?: 0,
            onCart = { navController.navigate("cart/current") },
            shakeEnabled = navigationShakeEnabled(appContainer, session, navController::popBackStack),
            viewModel = bettingGameViewModel,
            onBack = navController::popBackStack,
            onAdded = { gameCartRevision++ }
        )
    }
    listOf("betting/rapid", "betting/rapid-config", "betting/rapid-list").forEach { route ->
        composable(route) {
            val preferences = rememberNavigationPreferences(appContainer, session, NavigationPreferenceMode.RAPID)
            val preferenceState by preferences.state.collectAsStateWithLifecycle()
            NavigationPreferenceFailureDialog(preferenceState, preferences::retry, navController::popBackStack)
            LaunchedEffect(preferenceState.rapidCompletion) {
                val completion = preferenceState.rapidCompletion ?: return@LaunchedEffect
                preferences.acknowledgeCompletion()
                if (completion == RapidCompletion.POP_BACK) {
                    navController.popBackStack()
                } else {
                    navController.navigate("betting/rapid-list") { popUpTo(route) { inclusive = true } }
                }
            }
            val tutorialDismissed = preferenceState.tutorialDismissed
            if (tutorialDismissed == null) return@composable
            val initialShowList = remember(route, tutorialDismissed) {
                route == "betting/rapid-list" || (route == "betting/rapid" && tutorialDismissed == true)
            }
            key(session.privateEntryKey) {
                val entryCoordinator = remember {
                    RapidEntryCoordinator(
                        prepareEntry = appContainer.prepareHomeBettingAction,
                        isAuthenticated = session.identity != null,
                        showList = initialShowList,
                        isSessionCurrent = session.isPrivateSessionCurrent
                    )
                }
                RapidEntryRoute(
                    coordinator = entryCoordinator,
                    onBack = navController::popBackStack,
                    onCompleteAccount = { destination ->
                        session.state.visitorReturnRoute = route
                        navController.navigate(postLoginDestinationRoute(destination)) {
                            popUpTo(route) { inclusive = true }
                        }
                    }
                ) { target ->
                    val showList = if (route ==
                        "betting/rapid"
                    ) {
                        target == HomeBettingTarget.RAPID_LIST
                    } else {
                        initialShowList
                    }
                    val onServiceRedirect: (ServiceRedirect) -> Unit = { redirect ->
                        if (redirect ==
                            ServiceRedirect.LOGIN
                        ) {
                            session.requestVisitorLogin(route)
                        } else {
                            navController.navigate(serviceRedirectRoute(redirect))
                        }
                    }
                    if (showList) {
                        val rapidListViewModel = viewModel(key = "rapid-list-${session.privateEntryKey}") {
                            RapidListViewModel(
                                checkpointSession = appContainer.bettingCheckpoints.open(),
                                recoveryMessages = appContainer.recoveryMessages,
                                getCurrentCart = appContainer.currentCart,
                                generateRapidBets = appContainer.generateRapidBets,
                                removeCartItem = appContainer.removeCartItem,
                                validateCart = appContainer.validateCart,
                                dispatchers = appContainer.dispatchers,
                                networkAvailability = appContainer.networkAvailability,
                                localClock = appContainer.localClock,
                                autoLoad = session.identity != null,
                                isSessionCurrent = session.isPrivateSessionCurrent
                            )
                        }
                        RapidListRoute(
                            viewModel = rapidListViewModel,
                            onBack = navController::popBackStack,
                            onPayment = { navController.navigate("payment/method") },
                            onConfiguration = { navController.navigate("betting/rapid-config") },
                            onServiceRedirect = onServiceRedirect,
                            onValidationRedirect = {
                                if (it in accountCompletionRedirects) session.state.visitorReturnRoute = "cart/current"
                                onServiceRedirect(it)
                            }
                        )
                    } else {
                        val rapidConfigurationViewModel = viewModel(key = "rapid-configuration-${session.privateEntryKey}") {
                            RapidConfigurationViewModel(
                                getConfiguration = appContainer.rapidBetConfiguration,
                                getModalities = appContainer.lotteryModalities,
                                saveConfiguration = appContainer.saveRapidBetConfiguration,
                                getFavorites = appContainer.favoriteBets,
                                getParameters = appContainer.bettingParameters,
                                verifyAccess = appContainer.verifyAccountAccess,
                                dispatchers = appContainer.dispatchers,
                                networkAvailability = appContainer.networkAvailability,
                                localClock = appContainer.localClock,
                                autoLoad = session.identity != null,
                                isSessionCurrent = session.isPrivateSessionCurrent,
                                initialPage = if (tutorialDismissed == true) 6 else 0
                            )
                        }
                        RapidConfigurationRoute(
                            viewModel = rapidConfigurationViewModel,
                            onBack = navController::popBackStack,
                            onServiceRedirect = onServiceRedirect,
                            onSaved = { preferences.completeRapidConfiguration(returnToPrevious = route == "betting/rapid-config") }
                        )
                    }
                }
            }
        }
    }
    composable("betting/favorites") {
        key(session.privateEntryKey) {
            val favoriteBetsViewModel = viewModel(key = "favorite-bets-${session.privateEntryKey}") {
                FavoriteBetsViewModel(
                    checkpointSession = appContainer.bettingCheckpoints.open(),
                    recoveryMessages = appContainer.recoveryMessages,
                    getFavorites = appContainer.favoriteBets,
                    getParameters = appContainer.bettingParameters,
                    deleteFavorite = appContainer.deleteFavoriteBet,
                    addFavoriteToCart = appContainer.addFavoriteBetToCart,
                    dispatchers = appContainer.dispatchers,
                    getModalities = appContainer.lotteryModalities,
                    getCurrentCart = appContainer.currentCart,
                    verifyAccountAccess = appContainer.verifyAccountAccess,
                    autoLoad = session.identity != null,
                    isSessionCurrent = session.isPrivateSessionCurrent
                )
            }
            FavoriteBetsRoute(
                viewModel = favoriteBetsViewModel,
                privateDataEnabled = session.identity != null,
                onBack = navController::popBackStack,
                onTerms = { navController.navigate("support/terms") },
                onCreateFavorite = {
                    navController.navigate("betting/simulation/$it") {
                        popUpTo("betting/favorites") { inclusive = true }
                    }
                }
            )
        }
    }
    composable(
        route = BettingSimulationRoute.pattern,
        arguments = listOf(
            navArgument("modalityId") { type = NavType.IntType },
            navArgument("contestTypeId") {
                type = NavType.LongType
                defaultValue = 0L
            },
            navArgument("contestNumber") {
                type = NavType.IntType
                defaultValue = 0
            }
        )
    ) { entry ->
        val modalityId = requireNotNull(entry.arguments).getInt("modalityId")
        val requestedContestTypeId = requireNotNull(entry.arguments).getLong("contestTypeId")
        val requestedContestNumber = requireNotNull(entry.arguments).getInt("contestNumber").takeUnless { it == 0 }
        LaunchedEffect(entry) { navigation.preparedBettingParameters = null }
        BettingSimulationDestination(
            isAuthenticated = session.identity != null,
            onFavoriteLoginRequired = if (session.identity == null) {
                {
                    session.requestVisitorLogin(
                        "betting/simulation/$modalityId?contestTypeId=$requestedContestTypeId&contestNumber=${requestedContestNumber ?: 0}"
                    )
                }
            } else {
                null
            },
            destinationIdentity = entry,
            modalityId = modalityId,
            viewModelKey = "betting-game-$modalityId-$requestedContestTypeId-$requestedContestNumber",
            pendingEdit = navigation.details.betToEdit,
            createViewModel = { editSeed ->
                val preparedParameters = navigation.preparedBettingParameters.takeIf { editSeed == null }
                BettingGameViewModel(
                    modalityId = modalityId,
                    serverTime = appContainer.serverTime,
                    checkpointSession = appContainer.bettingCheckpoints.open(),
                    recoveryMessages = appContainer.recoveryMessages,
                    restoreCheckpoint = editSeed == null,
                    getParameters = appContainer.bettingParameters,
                    initialParameters = preparedParameters,
                    getSpecialContests = appContainer.bettingSpecialContests,
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
                    dispatchers = appContainer.dispatchers
                )
            },
            getCurrentCart = appContainer.currentCart,
            clearPendingEdit = { navigation.details.betToEdit = null },
            onBack = { navController.popBackStack() },
            onHelp = { navController.navigate("support/terms?tab=FAQ") },
            onCart = { navController.navigate("cart/current") },
            shakeEnabled = navigationShakeEnabled(appContainer, session, navController::popBackStack)
        )
    }
    composable("betting/combos") {
        val catalogViewModel = viewModel {
            BettingCatalogViewModel(
                checkpointSession = appContainer.bettingCheckpoints.open(),
                recoveryMessages = appContainer.recoveryMessages,
                mode = BettingCatalogMode.COMBOS,
                getModalities = appContainer.lotteryModalities,
                getParameters = appContainer.bettingParameters,
                getCombos = appContainer.betCombos,
                addCombo = appContainer.addComboToCart,
                dispatchers = appContainer.dispatchers
            )
        }
        val cartSummary = activeCartSummary(appContainer.currentCart)
        BetCombosRoute(
            viewModel = catalogViewModel,
            cartTotal = cartSummary?.total ?: java.math.BigDecimal.ZERO,
            cartBetCount = cartSummary?.betCount ?: 0,
            onCart = { navController.navigate("cart/current") },
            onBack = navController::popBackStack,
            onHelp = { navController.navigate("support/terms") },
            onSelect = { navController.navigate("betting/combo-detail/$it") }
        )
    }
    composable(
        route = "betting/combo-detail/{comboId}",
        arguments = listOf(navArgument("comboId") { type = NavType.IntType })
    ) { entry ->
        val comboId = requireNotNull(entry.arguments).getInt("comboId")
        val catalogViewModel = viewModel(key = "betting-combo-$comboId") {
            BettingCatalogViewModel(
                checkpointSession = appContainer.bettingCheckpoints.open(),
                recoveryMessages = appContainer.recoveryMessages,
                mode = BettingCatalogMode.COMBO_DETAIL,
                comboId = comboId,
                getModalities = appContainer.lotteryModalities,
                getParameters = appContainer.bettingParameters,
                getCombos = appContainer.betCombos,
                addCombo = appContainer.addComboToCart,
                dispatchers = appContainer.dispatchers
            )
        }
        val cartSummary = activeCartSummary(appContainer.currentCart)
        BetComboDetailRoute(
            viewModel = catalogViewModel,
            cartTotal = cartSummary?.total ?: java.math.BigDecimal.ZERO,
            cartBetCount = cartSummary?.betCount ?: 0,
            onCart = { navController.navigate("cart/current") },
            onBack = navController::popBackStack,
            onHelp = { navController.navigate("support/terms") },
            onAdded = {
                val state = catalogViewModel.state.value
                val amount = state.selectedCombo?.amount?.multiply(state.comboQuantity.toBigDecimal()) ?: java.math.BigDecimal.ZERO
                navController.navigate(
                    "cart/included?modality=COMBO&amount=${amount.toPlainString()}" +
                        "&minimum=${state.parameters?.minimumCartAmount ?: java.math.BigDecimal.ZERO}"
                )
            }
        )
    }
}
