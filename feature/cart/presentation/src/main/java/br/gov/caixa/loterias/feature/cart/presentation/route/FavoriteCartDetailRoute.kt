package br.gov.caixa.loterias.feature.cart.presentation.route

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.presentation.components.FavoriteConfirmDialog
import br.gov.caixa.loterias.feature.cart.presentation.screen.FavoriteCartDetailContent
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.FavoriteCartsViewModel

@Composable
fun FavoriteCartDetailRoute(viewModel: FavoriteCartsViewModel, onBack: () -> Unit, onHelp: (() -> Unit)? = null) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    BackHandler(onBack = onBack)
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.cartDeleted) { if (state.cartDeleted) onBack() }
    FavoriteCartDetailContent(
        state = state,
        onBack = onBack,
        onHelp = onHelp,
        onReload = viewModel::load,
        onRequestBetRemoval = viewModel::requestBetRemoval
    )
    if (state.pendingBetRemoval != null) {
        FavoriteConfirmDialog(
            uiStrings.getString(LocalizedR.string.ui_deseja_realmente_excluir_esta_aposta_ac0b38),
            viewModel::confirmBetRemoval,
            viewModel::dismissConfirmation
        )
    }
}
