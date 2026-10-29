package br.gov.caixa.loterias.feature.notifications.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.presentation.screen.CentralNotificationsScreen
import br.gov.caixa.loterias.feature.notifications.presentation.state.InboxConfirmation
import br.gov.caixa.loterias.feature.notifications.presentation.state.resolve
import br.gov.caixa.loterias.feature.notifications.presentation.viewmodel.CentralNotificationsViewModel

@Composable
fun CentralNotificationsRoute(viewModel: CentralNotificationsViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LifecycleResumeEffect(viewModel) {
        viewModel.onResume()
        onPauseOrDispose {}
    }
    CentralNotificationsScreen(
        state,
        onBack,
        viewModel::selectUnread,
        viewModel::requestMarkAll,
        viewModel::requestClear,
        viewModel::requestRemove,
        viewModel::load
    )
    state.pending?.let { pending ->
        val title = when (pending) {
            InboxConfirmation.MarkAll -> stringResource(R.string.notifications_marcar_todas_como_lidas)
            InboxConfirmation.Clear -> stringResource(R.string.notifications_excluir_notificacoes)
            is InboxConfirmation.Remove -> stringResource(R.string.notifications_excluir_notificacao)
        }
        val message = when (pending) {
            InboxConfirmation.MarkAll -> stringResource(R.string.notifications_deseja_marcar_todas_as_notificacoes_como_lidas)

            InboxConfirmation.Clear -> stringResource(R.string.notifications_deseja_excluir_todas_as_notificacoes)

            is InboxConfirmation.Remove -> stringResource(
                R.string.notifications_deseja_excluir_a_notificacao_1_s,
                pending.notification.title
            )
        }
        CaixaDSDialog(
            title,
            message,
            stringResource(R.string.notifications_sim),
            viewModel::confirm,
            stringResource(R.string.notifications_nao),
            viewModel::dismissDialog
        )
    }
    state.error?.let { message ->
        CaixaDSDialog(
            stringResource(R.string.notifications_atencao),
            message.resolve(),
            stringResource(R.string.notifications_entendi),
            viewModel::dismissDialog
        )
    }
}
