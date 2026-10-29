package br.gov.caixa.loterias.feature.home.presentation.route

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.feature.home.R

@Composable
internal fun HomeExitConfirmation(enabled: Boolean = true, onExit: () -> Unit) {
    var visible by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = enabled) { visible = true }
    if (visible) {
        CaixaDSDialog(
            title = stringResource(R.string.home_exit_title),
            message = stringResource(R.string.home_exit_message),
            positiveLabel = stringResource(R.string.home_exit_confirm),
            onPositive = { visible = false; onExit() },
            negativeLabel = stringResource(R.string.home_exit_cancel),
            onNegative = { visible = false },
            onDismiss = { visible = false },
        )
    }
}
