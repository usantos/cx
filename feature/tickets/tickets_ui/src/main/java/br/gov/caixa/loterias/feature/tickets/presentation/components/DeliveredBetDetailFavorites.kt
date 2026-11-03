package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR

@Composable
fun BetDetailFavoriteEditor(
    favoriteExpanded: Boolean,
    favoriteSaved: Boolean,
    favoriteName: String,
    onFavoriteNameChange: (String) -> Unit,
    onSaveFavorite: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (favoriteExpanded && !favoriteSaved) {
        br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSTextInputDialog(
            title = uiStrings.getString(DeliveryR.string.ui_favoritar_aposta_a34b1e),
            value = favoriteName,
            hint = uiStrings.getString(DeliveryR.string.ui_informe_o_nome_da_aposta_ecaf89),
            onValueChange = onFavoriteNameChange,
            onSave = onSaveFavorite,
            onDismiss = onToggleFavorite
        )
    }
}
