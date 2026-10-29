package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.compose.ui.res.stringResource

import br.gov.caixa.loterias.feature.betting.R as LocalizedR

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.feature.betting.presentation.components.AddFavoriteOptionsDialog
import br.gov.caixa.loterias.feature.betting.presentation.components.ModalityFilterDialog
import br.gov.caixa.loterias.feature.betting.presentation.screen.FavoriteBetsContent
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.FavoriteBetsViewModel

// Fronteira declarativa existente da interface; a formatação revelou complexidade da baseline sem alterar a lógica.
@Suppress("LongMethod")
@Composable
fun FavoriteBetsRoute(
    viewModel: FavoriteBetsViewModel,
    onBack: () -> Unit,
    onTerms: () -> Unit,
    onCreateFavorite: (Int) -> Unit,
    privateDataEnabled: Boolean = true,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showFilter = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    FavoriteBetsContent(
        state = state,
        onBack = onBack,
        onTerms = onTerms,
        onCreateFavorite = onCreateFavorite,
        privateDataEnabled = privateDataEnabled,
        onFilter = { showFilter.value = true },
        onAdd = viewModel::requestAdd,
        onDelete = viewModel::requestDelete,
        onLoadMore = viewModel::loadMore
    )

    if (showFilter.value) {
        ModalityFilterDialog(
            selected = state.selectedModalityId,
            modalities = state.modalities.map { it.id to it.name },
            onDismiss = { showFilter.value = false },
            onSelect = {
                showFilter.value = false
                viewModel.selectModality(it)
            }
        )
    }
    state.deleteCandidate?.let { favorite ->
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_deseja_excluir_a_aposta_favorita_1_s_beff85, favorite.name),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            onPositive = viewModel::confirmDelete,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onNegative = viewModel::cancelDelete
        )
    }
    if (state.dailyLimitConfirmation) {
        CaixaDSDialog(
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
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_existe_uma_aposta_identica_em_seu_ca_18a78d),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            onPositive = viewModel::confirmDuplicate,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onNegative = viewModel::cancelAddConfirmation
        )
    }
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
        CaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_apostas_favoritas_f81885),
            uiStrings.getString(LocalizedR.string.ui_aposta_favorita_inserida_com_sucesso_eb5056),
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::consumeAdded
        )
    }
    state.errorMessage?.let {
        CaixaDSAlertDialog(
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
