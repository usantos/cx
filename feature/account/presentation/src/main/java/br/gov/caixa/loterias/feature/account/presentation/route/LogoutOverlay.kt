package br.gov.caixa.loterias.feature.account.presentation.route

import br.gov.caixa.loterias.feature.account.presentation.components.LogoutStatus
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.auth.gateway.EndSessionGateway
import br.gov.caixa.loterias.feature.account.presentation.state.LogoutNotice
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LogoutViewModel

/** Mantido fora de NavHost para que a limpeza da pilha autenticada não perca o callback. */
@Composable
fun LogoutOverlay(viewModel: LogoutViewModel, endSession: EndSessionGateway) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        viewModel.complete { launch -> endSession.complete(launch, it.data) }
    }
    LaunchedEffect(state.launch) {
        state.launch?.let { launch ->
            if (viewModel.takeLaunch(launch)) {
                try { launcher.launch(endSession.intentFor(launch)) }
                catch (_: Exception) { viewModel.launchFailed(launch) }
            }
        }
    }
    LogoutStatus(state) {
        if (state.notice == LogoutNotice.SERVER_FAILURE) viewModel.retryServerLogout()
        else if (state.notice == LogoutNotice.LOCAL_DATA_FAILURE) viewModel.retryLocalCleanup()
        else viewModel.dismissNotice()
    }
}

