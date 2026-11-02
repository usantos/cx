package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.state.HomeDrawerConfirmation

@Composable
internal fun DrawerConfirmation(confirmation: HomeDrawerConfirmation, onPositive: () -> Unit, onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSDialog(
        title = confirmation.title,
        message = confirmation.message,
        positiveLabel = if (
            confirmation == HomeDrawerConfirmation.LOGOUT
        ) {
            uiStrings.getString(LocalizedR.string.ui_sim_931345)
        } else {
            uiStrings.getString(LocalizedR.string.ui_confirmar_717bed)
        },
        onPositive = onPositive,
        negativeLabel = if (
            confirmation == HomeDrawerConfirmation.LOGOUT
        ) {
            uiStrings.getString(LocalizedR.string.ui_nao_9bc947)
        } else {
            uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb)
        },
        onNegative = onDismiss
    )
}
