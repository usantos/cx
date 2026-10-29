package br.gov.caixa.loterias.feature.account.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.feature.account.R as LocalizedR
import br.gov.caixa.loterias.feature.account.presentation.state.SessionRecoveryState
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.SessionRecoveryViewModel

@Composable
fun SessionRecoveryGate(viewModel: SessionRecoveryViewModel, loading: @Composable () -> Unit, content: @Composable () -> Unit) {
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
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (state == SessionRecoveryState.READY) {
        content()
        return
    }
    loading()
    when (state) {
        SessionRecoveryState.FAILED -> CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_verificar_ou_conclu_08ef9f),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            onPositive = onRetry
        )

        SessionRecoveryState.AWAITING_ACKNOWLEDGMENT -> CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_a_limpeza_dos_dados_locais_da_sessao_25a8d7),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = onAcknowledge
        )

        else -> Unit
    }
}
