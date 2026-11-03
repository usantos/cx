package br.gov.caixa.loterias.feature.notifications.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.presentation.state.CentralNotificationsState
import br.gov.caixa.loterias.feature.notifications.presentation.state.InboxConfirmation
import br.gov.caixa.loterias.feature.notifications.presentation.state.resolve
import br.gov.caixa.loterias.feature.notifications.ui.R as DeliveryR

@Composable
internal fun DeliveredCentralNotificationsDialogs(state: CentralNotificationsState, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    state.pending?.let { pending ->
        val title = when (pending) {
            InboxConfirmation.MarkAll -> stringResource(R.string.notifications_marcar_todas_como_lidas)
            InboxConfirmation.Clear -> stringResource(DeliveryR.string.notifications_excluir_notificacoes)
            is InboxConfirmation.Remove -> stringResource(DeliveryR.string.notifications_excluir_notificacao)
        }
        val message = when (pending) {
            InboxConfirmation.MarkAll -> stringResource(DeliveryR.string.notifications_deseja_marcar_todas_as_notificacoes_como_lidas)

            InboxConfirmation.Clear -> stringResource(DeliveryR.string.notifications_deseja_excluir_todas_as_notificacoes)

            is InboxConfirmation.Remove -> stringResource(
                DeliveryR.string.notifications_deseja_excluir_a_notificacao_1_s,
                pending.notification.title
            )
        }
        LoteriasCaixaDSDialog(
            title,
            message,
            stringResource(DeliveryR.string.notifications_sim),
            onConfirm,
            stringResource(DeliveryR.string.notifications_nao),
            onDismiss
        )
    }
    state.error?.let { message ->
        LoteriasCaixaDSDialog(
            stringResource(DeliveryR.string.notifications_atencao),
            message.resolve(),
            stringResource(DeliveryR.string.notifications_entendi),
            onDismiss
        )
    }
}
