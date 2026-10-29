package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailUiState
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.BetDetailViewModel

@Composable
internal fun BetDetailFavoriteEditor(
    favoriteExpanded: Boolean,
    favoriteSaved: Boolean,
    favoriteName: String,
    onFavoriteNameChange: (String) -> Unit,
    onSaveFavorite: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (favoriteExpanded && !favoriteSaved) {
        br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSTextInputDialog(
            title = uiStrings.getString(LocalizedR.string.ui_favoritar_aposta_a34b1e),
            value = favoriteName,
            hint = uiStrings.getString(LocalizedR.string.ui_informe_o_nome_da_aposta_ecaf89),
            onValueChange = onFavoriteNameChange,
            onSave = onSaveFavorite,
            onDismiss = onToggleFavorite
        )
    }
}

@Composable
internal fun BetDetailFavoriteOverwriteDialog(state: BetDetailUiState, viewModel: BetDetailViewModel) {
    if (state.favoriteOverwriteConfirmation) {
        br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog(
            title = androidx.compose.ui.res.stringResource(br.gov.caixa.loterias.feature.tickets.R.string.tickets_attention),
            message = state.favoriteOverwriteMessage?.takeIf(String::isNotBlank)
                ?: androidx.compose.ui.res.stringResource(
                    br.gov.caixa.loterias.feature.tickets.R.string.tickets_favorite_overwrite_confirmation
                ),
            positiveLabel = androidx.compose.ui.res.stringResource(br.gov.caixa.loterias.feature.tickets.R.string.tickets_yes),
            onPositive = viewModel::confirmFavoriteOverwrite,
            negativeLabel = androidx.compose.ui.res.stringResource(br.gov.caixa.loterias.feature.tickets.R.string.tickets_no),
            onNegative = viewModel::dismissFavoriteOverwrite,
            onDismiss = viewModel::dismissFavoriteOverwrite
        )
    }
}
