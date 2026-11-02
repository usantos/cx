package br.gov.caixa.loterias.feature.account.presentation.route

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LoginViewModel

@Composable
internal fun rememberLoginAuthorizationActivityResult(
    state: LoginAuthorizationState,
    viewModel: LoginViewModel,
    authentication: AuthenticationGateway,
    connection: LoginAuthorizationConnection,
    onAuthorizationCancelled: () -> Unit,
) = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    state.cancelActivityReturn?.invoke()
    state.cancelActivityReturn = null
    state.browserPending = false
    // Um navegador cancelado pode retornar após a perda de rede. Não deve navegar para fora nem trocar tokens.
    if (!state.offline && viewModel.state.value.authorizationPending) {
        if (connection.disconnected()) {
            connection.connectionLost()
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
