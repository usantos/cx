package br.gov.caixa.loterias.core.presentation.components

import br.gov.caixa.loterias.core.presentation.R as LocalizedR

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.state.ServiceRedirectEvent
import br.gov.caixa.loterias.core.presentation.state.ServiceFailurePresentation
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState

/** Exibe apenas decisões: confirmar nunca repete a requisição do serviço. */
@Composable
fun ServiceFailureOverlay(
    state: ServiceFailurePresentation,
    onAccept: (ServiceNoticeUiState) -> Unit,
    onDismiss: (ServiceNoticeUiState) -> Unit,
    consumeRedirect: (ServiceRedirectEvent) -> Boolean,
    onRedirect: (ServiceRedirect) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val lifecycle by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    LaunchedEffect(state.redirect, lifecycle) {
        if (lifecycle.isAtLeast(Lifecycle.State.RESUMED)) {
            state.redirect?.let { if (consumeRedirect(it)) onRedirect(it.destination) }
        }
    }
    state.notice?.let { notice ->
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = notice.message,
            positiveLabel = notice.positiveLabel,
            onPositive = { onAccept(notice) },
            negativeLabel = notice.negativeLabel,
            onNegative = { onDismiss(notice) },
            onDismiss = { onDismiss(notice) }
        )
    }
}
