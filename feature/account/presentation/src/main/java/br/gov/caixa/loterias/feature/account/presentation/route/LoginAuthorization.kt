package br.gov.caixa.loterias.feature.account.presentation.route

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
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
    var offline by rememberSaveable { mutableStateOf(false) }
    var browserPending by rememberSaveable { mutableStateOf(false) }
    var retryPending by rememberSaveable { mutableStateOf(false) }
    var switchingUser by rememberSaveable { mutableStateOf(false) }
    var preferBrowserOnRetry by rememberSaveable { mutableStateOf(false) }
    var cancelActivityReturn by remember(activity) { mutableStateOf<(() -> Unit)?>(null) }
    DisposableEffect(activity) {
        onDispose { cancelActivityReturn?.invoke() }
    }
    fun returnBrowserToHost() {
        cancelActivityReturn?.invoke()
        cancelActivityReturn = if (returnFromBrowser != null) {
            returnFromBrowser()
            null
        } else {
            returnToLoginActivity(activity) {
                browserPending && !viewModel.state.value.authenticated && (offline || retryPending)
            }
        }
    }
    fun disconnected() = network?.current() == ConnectionAvailability.DISCONNECTED
    fun connectionLost() {
        if (offline || viewModel.state.value.authenticated) return
        offline = true
        retryPending = false
        // Invalida o tíquete desta requisição/PKCE; não encerra a sessão nem limpa o carrinho do usuário.
        viewModel.authorizationCancelled()
        if (browserPending) returnBrowserToHost()
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        cancelActivityReturn?.invoke()
        cancelActivityReturn = null
        browserPending = false
        // Um navegador cancelado pode retornar após a perda de rede. Não deve navegar para fora nem trocar tokens.
        if (!offline && viewModel.state.value.authorizationPending) {
            if (disconnected()) {
                connectionLost()
            } else if ((result.resultCode == Activity.RESULT_CANCELED && result.data == null) ||
                authentication.isAuthorizationCancelled(result.data)
            ) {
                viewModel.authorizationCancelled()
                onAuthorizationCancelled()
            } else {
                viewModel.complete { authentication.completeAuthorization(result.data) }
            }
        }
    }
    val biometric = rememberLoginBiometricAuthorization(viewModel, authentication, biometricPrompt)
    fun authorize(forceBrowser: Boolean = false, switchUser: Boolean = false) {
        switchingUser = switchUser
        if (disconnected()) {
            connectionLost()
            return
        }
        if (!viewModel.beginAuthorization()) return
        offline = false
        try {
            if (!forceBrowser && !switchUser && viewModel.canUseBiometricLogin()) {
                biometric.launch()
            } else {
                preferBrowserOnRetry = true
                browserPending = true
                launcher.launch(
                    if (switchUser) {
                        authentication.createAccountSwitchAuthorizationIntent()
                    } else {
                        authentication.createAuthorizationIntent()
                    }
                )
            }
        } catch (_: Exception) {
            browserPending = false
            if (disconnected()) {
                connectionLost()
            } else if (!forceBrowser && !switchUser && viewModel.canUseBiometricLogin()) {
                viewModel.biometricAuthorizationFailed()
            } else {
                viewModel.authorizationLaunchFailed()
            }
        }
    }
    ObserveLoginConnection(
        network,
        authorizationPending = { browserPending && viewModel.state.value.authorizationPending },
        onDisconnected = ::connectionLost
    )
    // A restauração do processo não preserva o tíquete de autorização em memória. Inicia uma nova tentativa ao repetir.
    LaunchedEffect(Unit) {
        if (offline && !viewModel.state.value.authorizationPending) browserPending = false
    }
    LaunchedEffect(browserFallbackPending) {
        if (viewModel.consumeBrowserFallback()) authorize(forceBrowser = true)
    }
    LaunchedEffect(retryPending, browserPending) {
        if (retryPending && !browserPending) {
            retryPending = false
            authorize(forceBrowser = preferBrowserOnRetry, switchUser = switchingUser)
        }
    }
    val retry: () -> Unit = {
        if (!disconnected()) {
            if (browserPending) {
                retryPending = true
                returnBrowserToHost()
            } else {
                authorize(forceBrowser = preferBrowserOnRetry, switchUser = switchingUser)
            }
        }
    }
    return LoginAuthorization(
        { authorize() },
        { authorize(forceBrowser = true, switchUser = true) },
        biometric.promptHost,
        offline,
        retry,
        retryPending && browserPending,
        ::connectionLost
    )
}
