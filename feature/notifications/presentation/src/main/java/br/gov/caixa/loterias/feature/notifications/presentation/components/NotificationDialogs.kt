package br.gov.caixa.loterias.feature.notifications.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationConfirmation
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsUiState
import br.gov.caixa.loterias.feature.notifications.presentation.state.resolve

@Composable
internal fun NotificationDialogs(state: NotificationsUiState, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    state.pendingConfirmation?.let { confirmation ->
        val message = when (confirmation) {
            NotificationConfirmation.DeleteAll -> stringResource(R.string.notifications_deseja_excluir_todas_a_mensagens)
            NotificationConfirmation.MarkAllRead -> stringResource(R.string.notifications_deseja_marcar_todas_a_mensagens_como_lidas)
            is NotificationConfirmation.DeleteOne -> stringResource(R.string.notifications_deseja_excluir_esta_mensagem)
            is NotificationConfirmation.MarkOneRead -> stringResource(R.string.notifications_deseja_marcar_esta_mensagem_como_lida)
        }
        CaixaDSDialog(
            title = stringResource(R.string.notifications_atencao),
            message = message,
            positiveLabel = stringResource(R.string.notifications_sim),
            onPositive = onConfirm,
            negativeLabel = stringResource(R.string.notifications_nao),
            onNegative = onDismiss
        )
    }
    state.categoryActionMessage?.let { message ->
        CaixaDSDialog(
            title = stringResource(R.string.notifications_atencao),
            message = message.resolve(),
            positiveLabel = stringResource(R.string.notifications_entendi),
            onPositive = onDismiss
        )
    }
    state.errorMessage?.let { message ->
        CaixaDSDialog(
            title = stringResource(R.string.notifications_atencao),
            message = message.resolve(),
            positiveLabel = stringResource(R.string.notifications_entendi),
            onPositive = onDismiss
        )
    }
}
