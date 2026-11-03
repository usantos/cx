package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailUiState

@Composable
internal fun BetDetailFavoriteOverwriteDialog(state: BetDetailUiState, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    if (state.favoriteOverwriteConfirmation) {
        br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog(
            title = androidx.compose.ui.res.stringResource(br.gov.caixa.loterias.feature.tickets.R.string.tickets_attention),
            message = state.favoriteOverwriteMessage?.takeIf(String::isNotBlank)
                ?: androidx.compose.ui.res.stringResource(
                    br.gov.caixa.loterias.feature.tickets.R.string.tickets_favorite_overwrite_confirmation
                ),
            positiveLabel = androidx.compose.ui.res.stringResource(br.gov.caixa.loterias.feature.tickets.R.string.tickets_yes),
            onPositive = onConfirm,
            negativeLabel = androidx.compose.ui.res.stringResource(br.gov.caixa.loterias.feature.tickets.R.string.tickets_no),
            onNegative = onDismiss,
            onDismiss = onDismiss
        )
    }
}
