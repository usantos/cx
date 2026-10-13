package br.gov.caixa.loterias.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.domain.service.ServiceRedirect

/** Displays decisions only: acknowledging never repeats the service request. */
@Composable
fun ServiceFailureOverlay(
    state: ServiceFailurePresentation,
    onAccept: (ServiceNoticeUiState) -> Unit,
    onDismiss: (ServiceNoticeUiState) -> Unit,
    consumeRedirect: (ServiceRedirectEvent) -> Boolean,
    onRedirect: (ServiceRedirect) -> Unit,
) {
    val lifecycle by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    LaunchedEffect(state.redirect, lifecycle) {
        if (lifecycle.isAtLeast(Lifecycle.State.RESUMED)) {
            state.redirect?.let { if (consumeRedirect(it)) onRedirect(it.destination) }
        }
    }
    state.notice?.let { notice ->
        CaixaDSLegacyDialog(title = "Atenção", message = notice.message,
            positiveLabel = notice.positiveLabel, onPositive = { onAccept(notice) },
            negativeLabel = notice.negativeLabel, onNegative = { onDismiss(notice) },
            onDismiss = { onDismiss(notice) })
    }
}
