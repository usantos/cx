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
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.feature.account.presentation.components.LoginDialogActions
import br.gov.caixa.loterias.feature.account.presentation.components.LoginDialogs
import br.gov.caixa.loterias.feature.account.presentation.screen.LoginScreen
import br.gov.caixa.loterias.feature.account.presentation.state.LoginUiState
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
    requiredAuthentication: Boolean = false,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val canStartAuthentication = br.gov.caixa.loterias.core.presentation.delivery.rememberFeatureWritePermission(
        br.gov.caixa.loterias.core.domain.configuration.model.AppModule.ACCOUNT
    )
    androidx.lifecycle.compose.LifecycleResumeEffect(viewModel) {
        viewModel.refreshBiometricAvailability()
        onPauseOrDispose {}
    }
    val authorization = rememberLoginAuthorization(
        viewModel,
        authentication,
        state.browserFallbackPending,
        onAuthorizationCancelled,
        biometricPrompt,
        networkAvailability,
        returnFromBrowser
    )
    val authorize: () -> Unit = {
        if (requiredAuthentication || canStartAuthentication()) authorization.authorize()
    }
    val promptHost = authorization.promptHost
    SilentLoginOnEnter(
        silentAuthorizeOnEnter && (requiredAuthentication || canStartAuthentication()),
        viewModel,
        authentication,
        networkAvailability
    )
    LoginRouteEffects(state, authorization, authorizeOnEnter, silentAuthorizeOnEnter, networkAvailability, authorize, onAuthenticated)
    if (authorization.offline) {
        offlineScreen({ if (requiredAuthentication || canStartAuthentication()) authorization.retry() }, authorization.waitingForBrowser)
        return
    }
    LoginScreen(
        state = state,
        versionName = versionName,
        onTryNow = { if (viewModel.enterVisitor()) onTryNow() },
        onLogin = authorize,
        onSwitchUser = { if (requiredAuthentication || canStartAuthentication()) authorization.switchUser() },
        onBiometricChanged = viewModel::setBiometricEnabled,
        environmentSelector = environmentSelector,
        environmentSelectorVisible = environmentSelectorVisible, modifier = Modifier.uiId("account.0018"),
        access = loginEntryAccess(state, requiredAuthentication)
    )
    LoginRouteDialogs(state, viewModel, promptHost, biometricEnrollment, onSessionInvalidated)
}

@Composable
private fun LoginRouteEffects(
    state: LoginUiState,
    authorization: LoginAuthorization,
    authorizeOnEnter: Boolean,
    silentAuthorizeOnEnter: Boolean,
    networkAvailability: br.gov.caixa.loterias.core.common.network.NetworkAvailability?,
    authorize: () -> Unit,
    onAuthenticated: (String) -> Unit,
) {
    var automaticAuthorizationConsumed by rememberSaveable { mutableStateOf(false) }
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
}

private fun loginEntryAccess(state: LoginUiState, requiredAuthentication: Boolean): FeatureEntryAccess =
    if (requiredAuthentication || state.authorizationPending || state.browserFallbackPending || state.isLoading) {
        FeatureEntryAccess.CONTINUATION
    } else {
        FeatureEntryAccess.OPTIONAL
    }

@Composable
private fun LoginRouteDialogs(
    state: LoginUiState,
    viewModel: LoginViewModel,
    promptHost: Activity?,
    biometricEnrollment: (Activity) -> Unit,
    onSessionInvalidated: () -> Unit,
) {
    LoginDialogs(
        state,
        LoginDialogActions(
            enableBiometric = {
                viewModel.startBiometricEnrollment()
                promptHost?.let(biometricEnrollment)
            },
            dismissEnrollment = viewModel::dismissBiometricEnrollment,
            confirmDisable = viewModel::confirmBiometricDisable,
            acknowledgeBlocked = { if (viewModel.acknowledgeBlockedAccountFailure()) onSessionInvalidated() },
            acknowledgeConsultation = { viewModel.acknowledgeConsultationOnlyNotice() }
        )
    )
}
