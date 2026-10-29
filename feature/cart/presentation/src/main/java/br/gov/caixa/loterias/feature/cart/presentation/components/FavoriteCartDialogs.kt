package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSSelectionDialog
import br.gov.caixa.loterias.feature.cart.R as LocalizedR

@Composable
internal fun FavoriteConfirmDialog(message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSDialog(
        title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
        message = message,
        positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
        onPositive = onConfirm,
        negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
        onNegative = onDismiss,
        onDismiss = onDismiss
    )
}

@Composable
internal fun SpecialSelectionDialog(regularName: String, specialName: String, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    var selected by remember { mutableIntStateOf(-1) }
    val options = listOf("Ambas", regularName, specialName)
    CaixaDSSelectionDialog(
        title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
        message = uiStrings.getString(LocalizedR.string.ui_existem_2_concursos_abertos_para_a_1_6bb68f, regularName),
        options = options,
        selectedIndex = selected,
        onSelect = { selected = it },
        onConfirm = { if (selected >= 0) onConfirm(selected) },
        onDismiss = onDismiss
    )
}
