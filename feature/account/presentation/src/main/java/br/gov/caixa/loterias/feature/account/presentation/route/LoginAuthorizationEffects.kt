package br.gov.caixa.loterias.feature.account.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LoginViewModel

@Composable
internal fun LoginAuthorizationEffects(
    state: LoginAuthorizationState,
    viewModel: LoginViewModel,
    network: NetworkAvailability?,
    browserFallbackPending: Boolean,
    connection: LoginAuthorizationConnection,
    launch: LoginAuthorizationLaunch,
) {
    ObserveLoginConnection(
        network,
        authorizationPending = { state.browserPending && viewModel.state.value.authorizationPending },
        onDisconnected = connection::connectionLost
    )
    // A restauração do processo não preserva o tíquete de autorização em memória. Inicia uma nova tentativa ao repetir.
    LaunchedEffect(Unit) {
        if (state.offline && !viewModel.state.value.authorizationPending) state.browserPending = false
    }
    LaunchedEffect(browserFallbackPending) {
        if (viewModel.consumeBrowserFallback()) launch.authorize(forceBrowser = true)
    }
    LaunchedEffect(state.retryPending, state.browserPending) {
        if (state.retryPending && !state.browserPending) {
            state.retryPending = false
            launch.authorize(forceBrowser = state.preferBrowserOnRetry, switchUser = state.switchingUser)
        }
    }
}
