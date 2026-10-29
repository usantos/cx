package br.gov.caixa.loterias.feature.account.presentation.route

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LoginViewModel

/** Uma sessão do provedor pode satisfazer prompt=none; caso contrário, mantém visível a tela normal de entrada. */
@Composable
internal fun SilentLoginOnEnter(
    enabled: Boolean,
    viewModel: LoginViewModel,
    authentication: AuthenticationGateway,
    network: NetworkAvailability?,
) {
    val activity = LocalActivity.current
    var consumed by rememberSaveable { mutableStateOf(false) }
    var pending by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val wasPending = pending
        pending = false
        if (wasPending && viewModel.state.value.authorizationPending) {
            if (silentAuthorizationCancelled(result, network, authentication)) {
                viewModel.authorizationCancelled()
            } else {
                // Usa os mesmos state/nonce/PKCE e a mesma verificação de conta do login interativo.
                viewModel.completeSilent { authentication.completeAuthorization(result.data) }
            }
        }
    }
    ObserveLoginConnection(network, authorizationPending = { pending && viewModel.state.value.authorizationPending }) {
        if (pending) {
            pending = false
            viewModel.authorizationCancelled()
            returnToLoginActivity(activity)
        }
    }
    LaunchedEffect(Unit) {
        if (!viewModel.state.value.authorizationPending) pending = false
    }
    LaunchedEffect(enabled) {
        if (enabled && !consumed) {
            consumed = true
            if (network?.current() != ConnectionAvailability.DISCONNECTED && viewModel.beginAuthorization()) {
                try {
                    val intent = authentication.createSilentAuthorizationIntent()
                    if (intent == null) {
                        viewModel.authorizationCancelled()
                    } else {
                        pending = true
                        launcher.launch(intent)
                    }
                } catch (cancelled: kotlinx.coroutines.CancellationException) {
                    pending = false
                    viewModel.authorizationCancelled()
                    throw cancelled
                } catch (_: Exception) {
                    pending = false
                    viewModel.authorizationCancelled()
                }
            }
        }
    }
}

private fun silentAuthorizationCancelled(
    result: ActivityResult,
    network: NetworkAvailability?,
    authentication: AuthenticationGateway,
): Boolean = result.resultCode == Activity.RESULT_CANCELED || result.data == null ||
    network?.current() == ConnectionAvailability.DISCONNECTED || authentication.isAuthorizationCancelled(result.data)
