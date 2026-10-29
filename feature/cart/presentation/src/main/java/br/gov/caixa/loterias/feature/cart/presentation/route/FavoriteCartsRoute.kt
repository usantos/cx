package br.gov.caixa.loterias.feature.cart.presentation.route

import br.gov.caixa.loterias.feature.cart.R as LocalizedR

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.feature.cart.presentation.screen.FavoriteCartDetailContent
import br.gov.caixa.loterias.feature.cart.presentation.components.FavoriteConfirmDialog
import br.gov.caixa.loterias.feature.cart.presentation.components.SpecialSelectionDialog
import br.gov.caixa.loterias.feature.cart.presentation.screen.FavoriteCartsActions
import br.gov.caixa.loterias.feature.cart.presentation.screen.FavoriteCartsContent
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.FavoriteCartsViewModel

@Composable
fun FavoriteCartsRoute(
    viewModel: FavoriteCartsViewModel,
    onBack: () -> Unit,
    onOpen: (Long) -> Unit,
    onCartReady: () -> Unit,
    onHelp: (() -> Unit)? = null,
    privateDataEnabled: Boolean = true,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.imported) {
        if (state.imported) {
            viewModel.consumeImported()
            onCartReady()
        }
    }
    FavoriteCartsContent(
        state = state,
        onBack = onBack,
        onOpen = onOpen,
        privateDataEnabled = privateDataEnabled,
        actions = FavoriteCartsActions(
            load = viewModel::load,
            toggleCart = viewModel::toggleCart,
            requestImport = viewModel::requestImport,
            requestCartRemoval = viewModel::requestCartRemoval
        )
    )
    if (state.pendingCartRemoval != null) {
        FavoriteConfirmDialog(
            uiStrings.getString(LocalizedR.string.ui_deseja_realmente_excluir_este_carrin_97da23),
            viewModel::confirmCartRemoval,
            viewModel::dismissConfirmation
        )
    }
    if (state.existingCartConfirmationRequired) {
        FavoriteConfirmDialog(
            uiStrings.getString(LocalizedR.string.ui_este_carrinho_favorito_ja_foi_adicio_b81250),
            viewModel::confirmExistingCartImport,
            viewModel::dismissConfirmation
        )
    }
    if (state.specialSelectionRequired) {
        SpecialSelectionDialog(
            regularName = state.regularModalityName,
            specialName = state.specialModalityName,
            onConfirm = viewModel::selectSpecialMode,
            onDismiss = viewModel::dismissConfirmation
        )
    }
}

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
