package br.gov.caixa.loterias.apostas.navigation.session

import br.gov.caixa.loterias.apostas.R as LocalizedR

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import br.gov.caixa.loterias.apostas.BuildConfig
import br.gov.caixa.loterias.core.auth.foreground.ForegroundSessionLifecycleObserver
import br.gov.caixa.loterias.core.auth.foreground.ForegroundSessionNoticeController
import br.gov.caixa.loterias.feature.account.presentation.components.AccountCompletionDialogs
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.AccountCompletionViewModel
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.core.navigation.state.DetailRestorationOutcome
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.workflow.VisitorLoginCallbacks
import br.gov.caixa.loterias.core.navigation.state.authenticatedContinuation
import br.gov.caixa.loterias.core.navigation.state.postLoginDestinationRoute
import br.gov.caixa.loterias.core.navigation.state.rememberAppSessionState
import br.gov.caixa.loterias.core.navigation.workflow.rememberPrivateEntryKey
import br.gov.caixa.loterias.core.navigation.state.restoredLoggedRoute
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination

/** Funções de retorno e concessão da sessão capturadas por esta composição; os ViewModels das telas pertencem aos destinos. */
internal class AppSessionCoordinator(
    override val state: AppSessionState,
    override val identity: br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity?,
    override val privateEntryKey: String,
    override val isPrivateSessionCurrent: () -> Boolean,
    val logoutViewModel: br.gov.caixa.loterias.feature.account.presentation.viewmodel.LogoutViewModel,
    override val onLogout: () -> Unit,
    override val requestVisitorLogin: (String) -> Unit,
    override val recheckAccountCompletion: () -> Unit,
    override val onAuthenticated: (String) -> Unit,
    override val onTryNow: () -> Unit,
) : br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions

@Composable
internal fun rememberAppSessionCoordinator(
    sessionDependencies: AppSessionDependencies,
    navController: androidx.navigation.NavHostController,
    navigation: AppNavigationState,
    identity: br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity?,
    onForegroundSessionRestart: () -> Unit,
): AppSessionCoordinator {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state = rememberAppSessionState()
    val completion = viewModel(key = "account-completion") {
        AccountCompletionViewModel(
            sessionDependencies.account.authenticatedSessionIdentity,
            sessionDependencies.account.verifyPostLoginAccess
        )
    }
    DisposableEffect(completion) { onDispose { completion.reset() } }
    val completionState by completion.state.collectAsStateWithLifecycle()
    val privateEntryKey = rememberPrivateEntryKey(identity)
    val isPrivateSessionCurrent = remember(identity) {
        val lease = identity
        { lease != null && sessionDependencies.account.authenticatedSessionIdentity.isCurrent(lease) }
    }
    val logoutViewModel = viewModel(key = "session-logout") {
        br.gov.caixa.loterias.feature.account.presentation.viewmodel.LogoutViewModel(
            sessionDependencies.account.authentication,
            sessionDependencies.account.endSession,
            sessionDependencies.account.clearLocalSessionData,
            signOutSession = sessionDependencies.account.signOutSession,
            identities = sessionDependencies.account.authenticatedSessionIdentity,
            dispatchers = sessionDependencies.account.dispatchers
        )
    }
    val resetLoggedNavigation: () -> Unit = {
        completion.reset()
        navigation.requestedHomeModalityId = null
        navigation.details.clear()
        state.pendingRestoredRoute = null
        state.visitorLoginRequest = null
        state.visitorReturnRoute = null
        state.visitorReturnOwnerKey = null
        state.resumedHomeAction = null
        state.accountCompletionRequired = false
        state.foregroundExpiryMarker = null
        navController.navigate("account/login") {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }
    val logoutState by logoutViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(logoutState.localSessionEnded) {
        if (logoutState.localSessionEnded) resetLoggedNavigation()
    }
    val onLogout: () -> Unit = {
        if (sessionDependencies.account.authenticatedSessionIdentity.captureIdentity() == null) {
            resetLoggedNavigation()
        } else {
            logoutViewModel.start(simulated = BuildConfig.ENABLE_MOCK_BACKEND)
        }
    }
    val resetForegroundNavigation: () -> Unit = {
        resetLoggedNavigation()
        onForegroundSessionRestart()
    }
    val latestForegroundReset by androidx.compose.runtime.rememberUpdatedState(resetForegroundNavigation)
    val foregroundNoticeController = remember(sessionDependencies, navController) {
        ForegroundSessionNoticeController(
            sessionDependencies.account.foregroundSession,
            { sessionDependencies.localClock.instant().epochSecond },
            { latestForegroundReset() }
        )
    }
    val lifecycleOwner = androidx.lifecycle.ProcessLifecycleOwner.get()
    val foregroundObserver = remember(sessionDependencies, navController) {
        ForegroundSessionLifecycleObserver(
            sessionDependencies.account.foregroundSession,
            currentRoute = { navController.currentBackStackEntry?.destination?.route },
            nowSeconds = { sessionDependencies.localClock.instant().epochSecond },
            onLogin = { latestForegroundReset() },
            onExpired = { state.foregroundExpiryMarker = foregroundNoticeController.show(it) },
            backgroundState = sessionDependencies.foregroundBackground,
            prizeChannelHost = { state.prizeChannelHost },
            routeEligible = br.gov.caixa.loterias.apostas.navigation.session.ForegroundSessionRoutePolicy::eligible
        )
    }
    LaunchedEffect(state.prizeChannelHost) { foregroundObserver.onRouteEntered() }
    DisposableEffect(lifecycleOwner, foregroundObserver, navController) {
        val observer = foregroundObserver
        val destinationListener = androidx.navigation.NavController.OnDestinationChangedListener { _, _, _ ->
            observer.onRouteEntered()
        }
        navController.addOnDestinationChangedListener(destinationListener)
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            navController.removeOnDestinationChangedListener(destinationListener)
        }
    }
    state.foregroundExpiryMarker?.let { marker ->
        foregroundNoticeController.restore(marker)?.let { accept ->
            CaixaDSDialog(
                title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
                message = br.gov.caixa.loterias.core.presentation.formatting.LEGACY_LOGIN_REQUIRED,
                positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
                onPositive = {
                    state.foregroundExpiryMarker = null
                    accept()
                }
            )
        }
    }
    val recheckAccountCompletion: () -> Unit = completion::recheck
    LaunchedEffect(completionState.outcome) {
        val outcome = completionState.outcome ?: return@LaunchedEffect
        try {
            val lease = outcome.identity
            if (!sessionDependencies.account.authenticatedSessionIdentity.isCurrent(lease)) return@LaunchedEffect
            state.accountCompletionRequired = outcome.destination != PostLoginDestination.HOME
            if (!state.accountCompletionRequired && state.pendingRestoredRoute != null) {
                navigation.details.reconcile(lease.ownerKey)
                state.visitorReturnRoute =
                    if (navigation.details.restorationOutcome == DetailRestorationOutcome.Restored) state.pendingRestoredRoute else null
                state.visitorReturnOwnerKey = lease.ownerKey
                state.pendingRestoredRoute = null
            }
            state.visitorReturnRoute = authenticatedContinuation(state.visitorReturnRoute, state.visitorReturnOwnerKey, lease.ownerKey)
            navController.navigate(postLoginDestinationRoute(outcome.destination)) { launchSingleTop = true }
        } finally {
            completion.acknowledge(outcome)
        }
    }
    AccountCompletionDialogs(
        completionState,
        onRetry = {
            completion.dismissError()
            completion.recheck()
        },
        onDismiss = completion::dismissError
    )
    val currentNavigationEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(identity, currentNavigationEntry) {
        state.foregroundExpiryMarker?.let { marker ->
            val route = currentNavigationEntry?.destination?.route?.substringBefore('?')
            if (route == "account/login" || route == "account/token" || route?.startsWith("onboarding/") == true ||
                foregroundNoticeController.restore(marker) == null
            ) {
                foregroundNoticeController.dismiss(marker)
                state.foregroundExpiryMarker = null
            }
        }
    }
    LaunchedEffect(identity, currentNavigationEntry, navigation.details.restorationOutcome) {
        val entry = currentNavigationEntry
        if (identity == null && navigation.details.restorationOutcome == DetailRestorationOutcome.Pending && entry != null &&
            entry.destination.route?.startsWith("account/login") != true
        ) {
            state.pendingRestoredRoute =
                restoredLoggedRoute(entry.destination.route.orEmpty()) { key -> entry.arguments?.get(key)?.toString() }
            navController.navigate("account/login") {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    val requestVisitorLogin: (String) -> Unit = { route -> state.visitorLoginRequest = route }
    state.visitorLoginRequest?.let { route ->
        val callbacks = VisitorLoginCallbacks(
            route = route,
            navController = navController,
            pendingRequest = { state.visitorLoginRequest },
            clearRequest = { state.visitorLoginRequest = null },
            continueAfterLogin = { state.visitorReturnRoute = it }
        )
        br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_para_continuar_utilizando_a_funciona_a5583f),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            visitorPrompt = true,
            onPositive = callbacks::onPositive,
            onNegative = callbacks::onNegative
        )
    }

    val onAuthenticated: (String) -> Unit = { destination ->
        val owner = sessionDependencies.account.authenticatedSessionIdentity.captureIdentity()?.ownerKey
        val target = state.authenticated(destination, owner, navigation.details)
        val resumeFeature = destination == "home" && target != "home"
        navController.navigate(if (resumeFeature) "home" else target) {
            // Destinos privados devem carregar novamente com a conta validada.
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
        if (resumeFeature) {
            // Constrói a pilha de retorno de forma síncrona; somente a feature solicitada é renderizada.
            navController.navigate(target) { launchSingleTop = true }
        }
    }
    val onTryNow: () -> Unit = {
        state.clearForVisitor(navigation.details)
        navController.navigate("home") { popUpTo(navController.graph.id) { inclusive = true } }
    }
    return AppSessionCoordinator(
        state, identity, privateEntryKey, isPrivateSessionCurrent,
        logoutViewModel, onLogout, requestVisitorLogin, recheckAccountCompletion, onAuthenticated, onTryNow
    )
}
