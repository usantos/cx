package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSTextInputDialog
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState

@Composable
internal fun FavoriteNameDialog(state: CartUiState, onNameChange: (String) -> Unit, onSave: () -> Unit, onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSTextInputDialog(
        title = uiStrings.getString(LocalizedR.string.ui_carrinho_favorito_fc14c7),
        value = state.favoriteName,
        hint = uiStrings.getString(LocalizedR.string.ui_nome_do_carrinho_favorito_4c48f5),
        onValueChange = onNameChange,
        onSave = onSave,
        onDismiss = onDismiss,
        enabled = !state.loading,
        errorMessage = state.errorMessage
    )
}

@Composable
internal fun ConfirmCartDialog(message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSDialog(
        title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
        message = message,
        positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
        onPositive = onConfirm,
        negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
        onNegative = onDismiss
    )
}
