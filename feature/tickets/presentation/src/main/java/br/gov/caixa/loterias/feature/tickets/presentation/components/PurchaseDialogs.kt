package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseDialogAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseStatusDialog

@Composable
internal fun PurchaseStatusDialogView(dialog: PurchaseStatusDialog, onAction: (PurchaseDialogAction) -> Unit) {
    val positive = dialog.actions.getOrNull(0) ?: return
    val negative = dialog.actions.getOrNull(1)
    val neutral = dialog.actions.getOrNull(2)
    LoteriasCaixaDSDialog(
        title = dialog.title,
        message = dialog.message,
        positiveLabel = positive.second,
        onPositive = { onAction(positive.first) },
        negativeLabel = negative?.second,
        onNegative = { negative?.let { onAction(it.first) } },
        neutralLabel = neutral?.second,
        onNeutral = { neutral?.let { onAction(it.first) } },
        onDismiss = { onAction(PurchaseDialogAction.DISMISS) }
    )
}

data class FavoritePurchaseActions(
    val closeFavoriteDialog: () -> Unit,
    val changeFavoriteName: (String) -> Unit,
    val toggleKeepSurprises: () -> Unit,
    val validateAndSaveFavorite: () -> Unit,
)
