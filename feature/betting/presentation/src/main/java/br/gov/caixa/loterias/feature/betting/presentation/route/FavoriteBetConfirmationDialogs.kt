package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.state.FavoriteBetsUiState
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.FavoriteBetsViewModel

@Composable
internal fun FavoriteBetConfirmationDialogs(state: FavoriteBetsUiState, viewModel: FavoriteBetsViewModel) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    state.deleteCandidate?.let { favorite ->
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_deseja_excluir_a_aposta_favorita_1_s_beff85, favorite.name),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            onPositive = viewModel::confirmDelete,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onNegative = viewModel::cancelDelete
        )
    }
    if (state.dailyLimitConfirmation) {
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_o_valor_maximo_de_apostas_permitida_0e6c55) +
                uiStrings.getString(LocalizedR.string.ui_deseja_prosseguir_com_a_inclusao_da_3e386b),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            onPositive = viewModel::confirmDailyLimit,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onNegative = viewModel::cancelAddConfirmation
        )
    }
    if (state.duplicateConfirmation) {
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_existe_uma_aposta_identica_em_seu_ca_18a78d),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            onPositive = viewModel::confirmDuplicate,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onNegative = viewModel::cancelAddConfirmation
        )
    }
}
