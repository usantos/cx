package br.gov.caixa.loterias.feature.account.presentation.components

import br.gov.caixa.loterias.feature.account.R as LocalizedR

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.account.presentation.state.AccountCompletionState
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog

@Composable
fun AccountCompletionDialogs(state: AccountCompletionState, onRetry: () -> Unit, onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (state.checking) CaixaDSAuthenticationLoadingDialog()
    state.error?.let { message ->
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = message,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_depois_b8db71),
            onPositive = onRetry,
            onNegative = onDismiss
        )
    }
}
