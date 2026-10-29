package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel.MarketplaceDetailViewModel

@Composable
internal fun MarketplaceDetailDialogs(state: MarketplaceDetailUiState, viewModel: MarketplaceDetailViewModel, onSoldOut: () -> Unit = {}) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    state.soldOutNotice?.let { message ->
        CaixaDSDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            {
                viewModel.consumeSoldOutNotice()
                onSoldOut()
            }
        )
    }
    state.pendingFavorite?.let { target ->
        CaixaDSDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            if (target) {
                uiStrings.getString(LocalizedR.string.ui_deseja_incluir_1_s_na_sua_lista_de_f_5f60aa, state.detail?.lotteryName.orEmpty())
            } else {
                uiStrings.getString(LocalizedR.string.ui_deseja_excluir_1_s_da_sua_lista_de_f_a0d867, state.detail?.lotteryName.orEmpty())
            },
            stringResource(LocalizedR.string.ui_confirmar_717bed),
            viewModel::confirmFavorite,
            stringResource(LocalizedR.string.ui_cancelar_bb9dbb),
            viewModel::cancelFavorite
        )
    }
    state.favoriteError?.let {
        CaixaDSDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::dismissFavoriteError
        )
    }
    if (state.isUpdatingFavorite || state.isAddingToCart || state.isLoading) CaixaDSAuthenticationLoadingDialog()
}
