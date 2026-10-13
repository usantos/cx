package br.gov.caixa.loterias.feature.account

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.auth.EndSessionGateway
import br.gov.caixa.loterias.core.designsystem.CaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog

/** Kept outside NavHost so clearing the authenticated back stack cannot lose the callback. */
@Composable
fun LogoutOverlay(viewModel: LogoutViewModel, endSession: EndSessionGateway) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        viewModel.complete(it.data)
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
        if (state.notice == LogoutNotice.LOCAL_DATA_FAILURE) viewModel.retryLocalCleanup()
        else viewModel.dismissNotice()
    }
}

@Composable
internal fun LogoutStatus(state: LogoutUiState, onDismissNotice: () -> Unit) {
    if (state.clearingLocalData || state.launch != null || state.waitingForBrowser) CaixaDSAuthenticationLoadingDialog()
    state.notice?.let { notice ->
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = when (notice) {
                LogoutNotice.LOCAL_FAILURE -> "Não foi possível encerrar a sessão neste aplicativo. Tente novamente."
                LogoutNotice.LOCAL_DATA_FAILURE -> "O acesso foi encerrado, mas não foi possível limpar todos os dados locais da sessão. Tente novamente para continuar com segurança."
                LogoutNotice.CANCELLED -> "Você saiu deste aplicativo. O encerramento da sessão no navegador foi cancelado e não pôde ser confirmado."
                LogoutNotice.UNCONFIRMED -> "Você saiu deste aplicativo, mas não foi possível confirmar o encerramento da sessão no navegador."
                LogoutNotice.SIMULATED -> "Sessão local encerrada no modo de simulação. Nenhuma sessão real do navegador foi encerrada."
            },
            positiveLabel = if (notice == LogoutNotice.LOCAL_DATA_FAILURE) "Tentar novamente" else "Entendi",
            onPositive = onDismissNotice,
        )
    }
}
