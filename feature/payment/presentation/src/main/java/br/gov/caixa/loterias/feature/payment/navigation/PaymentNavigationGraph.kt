package br.gov.caixa.loterias.feature.payment.navigation

import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.navigation.action.AppNavigationActions
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.workflow.nextDestination
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleAdmission
import br.gov.caixa.loterias.feature.payment.R
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentCardFlowRoute
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentDataRoute
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentTerminalRoute
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentVisualAssets
import br.gov.caixa.loterias.feature.payment.presentation.screen.paymentScreens
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentMode
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentCardViewModel
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentTerminalViewModel
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentViewModel
import kotlinx.coroutines.flow.first

fun NavGraphBuilder.paymentGraph(
    appContainer: PaymentNavigationDependencies,
    navController: NavHostController,
    session: SessionNavigationActions,
    actions: AppNavigationActions,
) {
    paymentMethodsGraph(appContainer, navController, session, actions)
    savedPaymentCardsGraph(appContainer, navController, session, actions)
    paymentCardFlowGraph(appContainer, navController, session, actions)
    compatiblePaymentCardGraph(appContainer, navController, session, actions)
    paymentTerminalGraph(navController, session)
}

private fun NavGraphBuilder.paymentMethodsGraph(
    appContainer: PaymentNavigationDependencies,
    navController: NavHostController,
    session: SessionNavigationActions,
    actions: AppNavigationActions,
) {
    listOf(
        "payment/method" to PaymentMode.METHODS,
        "payment/pix-copy" to PaymentMode.PIX,
        "payment/pix-expired" to PaymentMode.PIX
    ).forEach { (route, mode) ->
        composable(route) {
            FeatureModuleAdmission(
                AppModule.PAYMENT,
                { navController.popBackStack() },
                access = if (mode ==
                    PaymentMode.PIX
                ) {
                    recoveryAccess(session.purchaseRecovery)
                } else {
                    FeatureEntryAccess.OPTIONAL
                }
            ) {
                if (route == "payment/pix-expired" && !appContainer.mockBackendEnabled) {
                    LaunchedEffect(route) {
                        navController.navigate("tickets/purchases") {
                            popUpTo(route) { inclusive = true }
                        }
                    }
                    return@FeatureModuleAdmission
                }
                val paymentViewModel = viewModel(key = "payment-data-$route-${session.privateEntryKey}") {
                    createPaymentDataViewModel(appContainer, mode, session, createSavedStateHandle())
                }
                ObservePixRecovery(paymentViewModel, session.purchaseRecovery)
                val registrationViewModel = paymentRegistrationViewModel(appContainer)
                PaymentDataRoute(
                    onRegistration = { registrationViewModel.start() },
                    onRedirect = actions.onPaymentRedirect,
                    mode = mode,
                    viewModel = paymentViewModel,
                    visualAssets = PaymentVisualAssets(recargaPayLogoResourceId = R.drawable.recargapay),
                    onBack = navController::popBackStack,
                    onHelp = { navController.navigate("support/terms?tab=FAQ") },
                    onPix = { navController.navigate("payment/pix-guidance") },
                    onAddCard = { methodId -> navController.navigate("payment/card-flow/$methodId") },
                    onOpenCard = { card ->
                        navController.navigate("payment/card-flow/${card.paymentMethodId}?cardId=${Uri.encode(card.id)}")
                    },
                    onApproved = {
                        paymentViewModel.recoveryEvidence()?.let { session.purchaseRecovery?.grant(it) }
                        navController.navigate("payment/success")
                    },
                    onHome = actions.onPaymentMoreBets,
                    onPurchases = {
                        paymentViewModel.recoveryEvidence()?.let { session.purchaseRecovery?.grant(it) }
                        navController.navigate("tickets/purchases")
                    }
                )
                PaymentRegistrationHost(
                    registrationViewModel,
                    paymentViewModel::registrationFinished,
                    actions.onPrizeRedirect,
                    simulateBrowser = appContainer.mockBackendEnabled
                )
            }
        }
    }
}

private fun NavGraphBuilder.savedPaymentCardsGraph(
    appContainer: PaymentNavigationDependencies,
    navController: NavHostController,
    session: SessionNavigationActions,
    actions: AppNavigationActions,
) {
    composable(
        route = "payment/cards?methodId={methodId}",
        arguments = listOf(
            navArgument("methodId") {
                type = NavType.LongType
                defaultValue = 0L
            }
        )
    ) { entry ->
        FeatureModuleAdmission(AppModule.PAYMENT, { navController.popBackStack() }) {
            androidx.compose.runtime.key(session.privateEntryKey) {
                val methodId = requireNotNull(entry.arguments).getLong("methodId").takeIf { it > 0 }
                val paymentViewModel = viewModel(key = "payment-cards-${methodId ?: 0}-${session.privateEntryKey}") {
                    PaymentViewModel(
                        canStartOperation = { appContainer.appModulesConfiguration.isEnabled(AppModule.PAYMENT) },
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
                        autoLoad = session.identity != null,
                        isSessionCurrent = session.isPrivateSessionCurrent
                    )
                }
                ObservePixRecovery(paymentViewModel, session.purchaseRecovery)
                val registrationViewModel = paymentRegistrationViewModel(appContainer)
                PaymentDataRoute(
                    privateDataEnabled = session.identity != null,
                    onRegistration = { registrationViewModel.start() },
                    onRedirect = actions.onPaymentRedirect,
                    mode = PaymentMode.CARDS,
                    viewModel = paymentViewModel,
                    visualAssets = PaymentVisualAssets(recargaPayLogoResourceId = R.drawable.recargapay),
                    onBack = navController::popBackStack,
                    onHelp = { navController.navigate("support/terms?tab=FAQ") },
                    onPix = { navController.navigate("payment/pix-guidance") },
                    onAddCard = { selected -> navController.navigate("payment/card-flow/$selected") },
                    onOpenCard = { card ->
                        navController.navigate("payment/card-flow/${card.paymentMethodId}?cardId=${Uri.encode(card.id)}")
                    },
                    onApproved = {
                        paymentViewModel.recoveryEvidence()?.let { session.purchaseRecovery?.grant(it) }
                        navController.navigate("payment/success")
                    },
                    onHome = actions.onPaymentMoreBets,
                    onPurchases = {
                        paymentViewModel.recoveryEvidence()?.let { session.purchaseRecovery?.grant(it) }
                        navController.navigate("tickets/purchases")
                    }
                )
                PaymentRegistrationHost(
                    registrationViewModel,
                    paymentViewModel::registrationFinished,
                    actions.onPrizeRedirect,
                    simulateBrowser = appContainer.mockBackendEnabled
                )
            }
        }
    }
}

private fun NavGraphBuilder.paymentCardFlowGraph(
    appContainer: PaymentNavigationDependencies,
    navController: NavHostController,
    session: SessionNavigationActions,
    actions: AppNavigationActions,
) {
    composable(
        route = "payment/card-flow/{methodId}?cardId={cardId}",
        arguments = listOf(
            navArgument("methodId") { type = NavType.LongType },
            navArgument("cardId") {
                type = NavType.StringType
                defaultValue = ""
            }
        )
    ) { entry ->
        FeatureModuleAdmission(AppModule.PAYMENT, { navController.popBackStack() }) {
            val methodId = requireNotNull(entry.arguments).getLong("methodId")
            val cardId = requireNotNull(entry.arguments).getString("cardId").orEmpty().ifBlank { null }
            val cardViewModel = viewModel(key = "payment-card-$methodId-${cardId.orEmpty()}-${session.privateEntryKey}") {
                PaymentCardViewModel(
                    canStartOperation = { appContainer.appModulesConfiguration.isEnabled(AppModule.PAYMENT) },
                    clock = appContainer.localClock,
                    recordRecentPurchase = appContainer.recordRecentPurchase,
                    getCurrentCart = appContainer.currentCart,
                    paymentMethodId = methodId,
                    savedCardId = cardId,
                    getCards = appContainer.savedCards,
                    submitCardPayment = appContainer.submitCardPayment,
                    getPurchaseProgress = appContainer.purchaseProgress,
                    dispatchers = appContainer.dispatchers
                )
            }
            ObserveCardRecovery(cardViewModel, session.purchaseRecovery)
            val registrationViewModel = paymentRegistrationViewModel(appContainer)
            PaymentCardFlowRoute(
                onRegistration = { registrationViewModel.start() },
                onMoreBets = actions.onPaymentMoreBets,
                onRedirect = actions.onPaymentRedirect,
                viewModel = cardViewModel,
                onBack = navController::popBackStack,
                onSuccess = {
                    cardViewModel.recoveryEvidence()?.let { session.purchaseRecovery?.grant(it) }
                    navController.navigate("payment/success")
                },
                onReviewPurchases = {
                    cardViewModel.recoveryEvidence()?.let { session.purchaseRecovery?.grant(it) }
                    navController.navigate("tickets/purchases") {
                        popUpTo(entry.destination.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
            PaymentRegistrationHost(
                registrationViewModel,
                cardViewModel::registrationFinished,
                actions.onPrizeRedirect,
                simulateBrowser = appContainer.mockBackendEnabled
            )
        }
    }
}

private fun NavGraphBuilder.compatiblePaymentCardGraph(
    appContainer: PaymentNavigationDependencies,
    navController: NavHostController,
    session: SessionNavigationActions,
    actions: AppNavigationActions,
) {
    mapOf(
        "payment/add-card" to PaymentCardStage.FORM,
        "payment/card-preview" to PaymentCardStage.PREVIEW,
        "payment/cvv" to PaymentCardStage.CVV,
        "payment/confirm-card" to PaymentCardStage.CONFIRM
    ).forEach { (route, initialStage) ->
        composable(route) { entry ->
            FeatureModuleAdmission(AppModule.PAYMENT, { navController.popBackStack() }) {
                val cardViewModel = viewModel(key = "payment-card-compat-$route-${session.privateEntryKey}") {
                    PaymentCardViewModel(
                        canStartOperation = { appContainer.appModulesConfiguration.isEnabled(AppModule.PAYMENT) },
                        clock = appContainer.localClock,
                        recordRecentPurchase = appContainer.recordRecentPurchase,
                        getCurrentCart = appContainer.currentCart,
                        paymentMethodId = 1L,
                        savedCardId = null,
                        initialStage = initialStage,
                        getCards = appContainer.savedCards,
                        submitCardPayment = appContainer.submitCardPayment,
                        getPurchaseProgress = appContainer.purchaseProgress,
                        dispatchers = appContainer.dispatchers
                    )
                }
                ObserveCardRecovery(cardViewModel, session.purchaseRecovery)
                val registrationViewModel = paymentRegistrationViewModel(appContainer)
                PaymentCardFlowRoute(
                    onRegistration = { registrationViewModel.start() },
                    onMoreBets = actions.onPaymentMoreBets,
                    onRedirect = actions.onPaymentRedirect,
                    viewModel = cardViewModel,
                    onBack = navController::popBackStack,
                    onSuccess = {
                        cardViewModel.recoveryEvidence()?.let { session.purchaseRecovery?.grant(it) }
                        navController.navigate("payment/success")
                    },
                    onReviewPurchases = {
                        cardViewModel.recoveryEvidence()?.let { session.purchaseRecovery?.grant(it) }
                        navController.navigate("tickets/purchases") {
                            popUpTo(entry.destination.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
                PaymentRegistrationHost(
                    registrationViewModel,
                    cardViewModel::registrationFinished,
                    actions.onPrizeRedirect,
                    simulateBrowser = appContainer.mockBackendEnabled
                )
            }
        }
    }
}

private fun NavGraphBuilder.paymentTerminalGraph(navController: NavHostController, session: SessionNavigationActions) {
    listOf(
        "payment/pix-guidance",
        "payment/confirmation",
        "payment/failure",
        "payment/success"
    ).forEach { route ->
        composable(route) {
            FeatureModuleAdmission(
                AppModule.PAYMENT,
                { navController.popBackStack() },
                access = if (route == "payment/success" ||
                    route == "payment/confirmation"
                ) {
                    recoveryAccess(session.purchaseRecovery)
                } else {
                    FeatureEntryAccess.OPTIONAL
                }
            ) {
                val spec = paymentScreens(androidx.compose.ui.platform.LocalResources.current::getString).first { it.route == route }
                val paymentViewModel = viewModel(key = "payment-terminal-$route") {
                    PaymentTerminalViewModel(spec)
                }
                PaymentTerminalRoute(
                    viewModel = paymentViewModel,
                    onBack = navController::popBackStack,
                    onComplete = {
                        val destination = nextDestination(route)
                        if (destination == null) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(destination)
                        }
                    },
                    onNavigate = { destination ->
                        if (destination == "home") session.purchaseRecovery?.finish()
                        navController.navigate(destination)
                    },
                    purchaseRecovery = session.purchaseRecovery
                )
            }
        }
    }
}

private fun createPaymentDataViewModel(
    appContainer: PaymentNavigationDependencies,
    mode: PaymentMode,
    session: SessionNavigationActions,
    savedStateHandle: androidx.lifecycle.SavedStateHandle,
): PaymentViewModel = PaymentViewModel(
    canStartOperation = { appContainer.appModulesConfiguration.isEnabled(AppModule.PAYMENT) },
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
    ownerKey = session.identity?.ownerKey,
    isSessionCurrent = session.isPrivateSessionCurrent,
    savedStateHandle = savedStateHandle
)
