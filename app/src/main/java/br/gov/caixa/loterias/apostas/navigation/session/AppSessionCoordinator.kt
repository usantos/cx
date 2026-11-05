package br.gov.caixa.loterias.apostas.navigation.session

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
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.state.AppNavigationState
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.core.navigation.state.rememberAppSessionState
import br.gov.caixa.loterias.core.navigation.workflow.rememberPrivateEntryKey
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.AccountCompletionViewModel

/** Funções de retorno e concessão da sessão capturadas por esta composição; os ViewModels das telas pertencem aos destinos. */
internal class AppSessionCoordinator(
    override val state: AppSessionState,
    override val identity: br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity?,
    override val privateEntryKey: String,
    override val isPrivateSessionCurrent: () -> Boolean,
    val logoutViewModel: br.gov.caixa.loterias.feature.account.presentation.viewmodel.LogoutViewModel,
    override val onLogout: () -> Unit,
    override val requestVisitorLogin: (String) -> Unit,
    override val recheckAccountCompletion: () -> Unit,
    override val onAuthenticated: (String) -> Unit,
    override val onTryNow: () -> Unit,
    override val purchaseRecovery: br.gov.caixa.loterias.core.navigation.state.PurchaseRecoverySession? = null,
) : br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions

@Composable
internal fun rememberAppSessionCoordinator(
    sessionDependencies: AppSessionDependencies,
    navController: androidx.navigation.NavHostController,
    navigation: AppNavigationState,
    identity: br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity?,
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
    val purchaseRecovery = remember(navigation.details, identity, privateEntryKey) {
        br.gov.caixa.loterias.core.navigation.state.PurchaseRecoverySession(
            navigation.details,
            identity?.ownerKey,
            privateEntryKey,
            isPrivateSessionCurrent
        )
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
        resetLoggedNavigationState(completion, navigation, state, navController)
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
    val foregroundNoticeController =
        rememberForegroundSessionController(sessionDependencies, navController, state, resetForegroundNavigation)
    val recheckAccountCompletion: () -> Unit = completion::recheck
    AccountCompletionNavigation(completionState, completion, sessionDependencies, navigation, state, navController)
    val currentNavigationEntry by navController.currentBackStackEntryAsState()
    AppSessionRestorationEffects(identity, currentNavigationEntry, foregroundNoticeController, navController, navigation, state)
    val requestVisitorLogin: (String) -> Unit = { route -> state.visitorLoginRequest = route }
    VisitorLoginPrompt(state, navController)
    val onAuthenticated: (String) -> Unit = { destination ->
        navigateAuthenticatedDestination(sessionDependencies, navController, navigation, state, destination)
    }
    val onTryNow: () -> Unit = {
        state.clearForVisitor(navigation.details)
        navController.navigate("home") { popUpTo(navController.graph.id) { inclusive = true } }
    }
    return AppSessionCoordinator(
        state, identity, privateEntryKey, isPrivateSessionCurrent,
        logoutViewModel, onLogout, requestVisitorLogin, recheckAccountCompletion, onAuthenticated, onTryNow, purchaseRecovery
    )
}
