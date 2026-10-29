package br.gov.caixa.loterias.feature.account.presentation.route

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.auth.biometric.showBiometricPrompt
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.feature.account.presentation.components.LoginDialogs
import br.gov.caixa.loterias.feature.account.presentation.screen.LoginScreen
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LoginViewModel

@Composable
fun LoginRoute(
    viewModel: LoginViewModel,
    authentication: AuthenticationGateway,
    versionName: String,
    onAuthenticated: (String) -> Unit,
    onTryNow: () -> Unit,
    onSessionInvalidated: () -> Unit,
    authorizeOnEnter: Boolean = false,
    onAuthorizationCancelled: () -> Unit = {},
    networkAvailability: br.gov.caixa.loterias.core.common.network.NetworkAvailability? = null,
    offlineScreen: @Composable (() -> Unit, Boolean) -> Unit = { _, _ -> },
    returnFromBrowser: (() -> Unit)? = null,
    environmentSelectorVisible: Boolean = false,
    environmentSelector: @Composable (Boolean) -> Unit = {},
    biometricPrompt: (Activity, android.content.Intent, (android.content.Intent?) -> Unit) -> (() -> Unit) =
        ::showBiometricPrompt,
    biometricEnrollment: (Activity) -> Unit = { activity ->
        br.gov.caixa.loterias.core.auth.gateway.launchBiometricEnrollment(activity::startActivity)
    },
    silentAuthorizeOnEnter: Boolean = false,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    androidx.lifecycle.compose.LifecycleResumeEffect(viewModel) {
        viewModel.refreshBiometricAvailability()
        onPauseOrDispose {}
    }
    var automaticAuthorizationConsumed by rememberSaveable { mutableStateOf(false) }
    val authorization = rememberLoginAuthorization(
        viewModel,
        authentication,
        state.browserFallbackPending,
        onAuthorizationCancelled,
        biometricPrompt,
        networkAvailability,
        returnFromBrowser
    )
    val authorize = authorization.authorize
    val promptHost = authorization.promptHost
    SilentLoginOnEnter(silentAuthorizeOnEnter, viewModel, authentication, networkAvailability)
    LaunchedEffect(authorizeOnEnter, silentAuthorizeOnEnter) {
        if (authorizeOnEnter && !silentAuthorizeOnEnter && !automaticAuthorizationConsumed) {
            automaticAuthorizationConsumed = true
            authorize()
        }
    }
    LaunchedEffect(state.authenticated) {
        if (state.authenticated) onAuthenticated(state.destination)
    }
    LaunchedEffect(state.isLoading, state.errorMessage) {
        if (!state.isLoading && state.errorMessage != null && !state.authenticated &&
            networkAvailability?.current() == br.gov.caixa.loterias.core.common.network.ConnectionAvailability.DISCONNECTED
        ) {
            authorization.connectionLost()
        }
    }
    if (authorization.offline) {
        offlineScreen(authorization.retry, authorization.waitingForBrowser)
        return
    }
    LoginScreen(
        state = state,
        versionName = versionName,
        onTryNow = { if (viewModel.enterVisitor()) onTryNow() },
        onLogin = authorize,
        onSwitchUser = authorization.switchUser,
        onBiometricChanged = viewModel::setBiometricEnabled,
        environmentSelector = environmentSelector,
        environmentSelectorVisible = environmentSelectorVisible, modifier = Modifier.uiId("account.0018")
    )
    LoginDialogs(state, viewModel, promptHost, biometricEnrollment, onSessionInvalidated)
}
