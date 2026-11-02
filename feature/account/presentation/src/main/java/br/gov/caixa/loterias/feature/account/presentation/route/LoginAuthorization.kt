package br.gov.caixa.loterias.feature.account.presentation.route

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LoginViewModel

internal class LoginAuthorization(
    val authorize: () -> Unit,
    val switchUser: () -> Unit,
    val promptHost: Activity?,
    val offline: Boolean,
    val retry: () -> Unit,
    val waitingForBrowser: Boolean,
    val connectionLost: () -> Unit,
)

@Composable
internal fun rememberLoginAuthorization(
    viewModel: LoginViewModel,
    authentication: AuthenticationGateway,
    browserFallbackPending: Boolean,
    onAuthorizationCancelled: () -> Unit,
    biometricPrompt: (Activity, Intent, (Intent?) -> Unit) -> (() -> Unit),
    network: NetworkAvailability?,
    returnFromBrowser: (() -> Unit)?,
): LoginAuthorization {
    val activity = LocalActivity.current
    val state = rememberLoginAuthorizationState(activity)
    val connection = LoginAuthorizationConnection(state, viewModel, network, activity, returnFromBrowser)
    val launcher = rememberLoginAuthorizationActivityResult(state, viewModel, authentication, connection, onAuthorizationCancelled)
    val biometric = rememberLoginBiometricAuthorization(viewModel, authentication, biometricPrompt)
    val launch = LoginAuthorizationLaunch(state, viewModel, authentication, connection, biometric) { launcher.launch(it) }
    LoginAuthorizationEffects(state, viewModel, network, browserFallbackPending, connection, launch)
    return LoginAuthorization(
        { launch.authorize() },
        { launch.authorize(forceBrowser = true, switchUser = true) },
        biometric.promptHost,
        state.offline,
        launch::retry,
        state.retryPending && state.browserPending,
        connection::connectionLost
    )
}
