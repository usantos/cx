package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAlertDialog
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.components.AddFavoriteOptionsDialog
import br.gov.caixa.loterias.feature.betting.presentation.state.FavoriteBetsUiState
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.FavoriteBetsViewModel

@Composable
internal fun FavoriteBetAdditionDialogs(state: FavoriteBetsUiState, viewModel: FavoriteBetsViewModel, onBack: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    state.addCandidate?.let {
        AddFavoriteOptionsDialog(
            state = state,
            onCancel = viewModel::cancelAdd,
            onConfirm = viewModel::confirmAdd,
            onContestTypeChange = viewModel::setContestType,
            onRecurringDrawsChange = viewModel::setRecurringDraws,
            onToggleMirror = viewModel::toggleMirror
        )
    }
    if (state.addedToCart) {
        LoteriasCaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_apostas_favoritas_f81885),
            uiStrings.getString(LocalizedR.string.ui_aposta_favorita_inserida_com_sucesso_eb5056),
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::consumeAdded
        )
    }
    state.errorMessage?.let {
        LoteriasCaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            onConfirm = {
                val close = state.suspensionNotice
                viewModel.dismissError()
                if (close) onBack()
            }
        )
    }
}
