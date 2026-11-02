package br.gov.caixa.loterias.feature.marketplace.presentation.route

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceAction

@Composable
internal fun MarketplaceDialogs(
    state: br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState,
    onAction: (MarketplaceAction) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    state.pendingFavoritePoolCode?.let { code ->
        val pool = state.pools.firstOrNull { it.code == code }
        if (pool != null) {
            LoteriasCaixaDSDialog(
                title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
                message = if (pool.favoriteLottery) {
                    uiStrings.getString(LocalizedR.string.ui_deseja_excluir_1_s_da_sua_lista_de_f_a0d867, pool.lotteryName)
                } else {
                    uiStrings.getString(LocalizedR.string.ui_deseja_incluir_1_s_na_sua_lista_de_f_5f60aa, pool.lotteryName)
                },
                positiveLabel = uiStrings.getString(LocalizedR.string.ui_confirmar_717bed),
                onPositive = { onAction(MarketplaceAction.ConfirmFavorite) },
                negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
                onNegative = { onAction(MarketplaceAction.CancelFavorite) }
            )
        }
    }
    state.actionError?.let { message ->
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = message,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = { onAction(MarketplaceAction.DismissActionError) }
        )
    }
    state.pendingFavoriteRetailerRemoval?.let { retailerId ->
        val retailer = state.favoriteRetailers.firstOrNull { it.id == retailerId }
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(
                LocalizedR.string.ui_deseja_excluir_1_s_da_sua_lista_de_f_a0d867,
                retailer?.fantasyName.orEmpty()
            ),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_confirmar_717bed),
            onPositive = { onAction(MarketplaceAction.ConfirmFavoriteRetailerRemoval) },
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
            onNegative = { onAction(MarketplaceAction.CancelFavoriteRetailerRemoval) }
        )
    }
    if (state.isLoading || state.isLoadingMore || state.favoriteRetailersOpening || state.addingPoolCodes.isNotEmpty() ||
        state.updatingFavoriteLotteryIds.isNotEmpty()
    ) {
        LoteriasCaixaDSAuthenticationLoadingDialog()
    }
}
