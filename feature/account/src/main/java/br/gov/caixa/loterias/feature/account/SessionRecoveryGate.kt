package br.gov.caixa.loterias.feature.account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog

@Composable
fun SessionRecoveryGate(
    viewModel: SessionRecoveryViewModel,
    loading: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SessionRecoveryContent(state, viewModel::retry, viewModel::acknowledge, loading, content)
}

@Composable
internal fun SessionRecoveryContent(
    state: SessionRecoveryState,
    onRetry: () -> Unit,
    onAcknowledge: () -> Unit,
    loading: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    if (state == SessionRecoveryState.READY) {
        content()
        return
    }
    loading()
    when (state) {
        SessionRecoveryState.FAILED -> CaixaDSLegacyDialog(
            title = "Atenção",
            message = "Não foi possível verificar ou concluir a limpeza dos dados da sessão anterior. Tente novamente para continuar com segurança.",
            positiveLabel = "Tentar novamente",
            onPositive = onRetry,
        )
        SessionRecoveryState.AWAITING_ACKNOWLEDGMENT -> CaixaDSLegacyDialog(
            title = "Atenção",
            message = "A limpeza dos dados locais da sessão anterior foi concluída. O encerramento da sessão no navegador não foi confirmado.",
            positiveLabel = "Entendi",
            onPositive = onAcknowledge,
        )
        else -> Unit
    }
}
