package br.gov.caixa.loterias.feature.retail.presentation.route

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeReceiptDeliveryState

@Composable
internal fun PrizeReceiptFailureDialogs(
    deliveryState: PrizeReceiptDeliveryState,
    onRecover: () -> Unit,
    onCancelRecovery: () -> Unit,
    onSave: (Uri) -> Unit,
    onCancelSave: () -> Unit,
    onOpenPdf: (Uri) -> Unit,
    onDismissViewerFailure: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (deliveryState.recoveryFailure) {
        LoteriasCaixaDSDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_recuperar_o_comprov_f02c59),
            uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            onPositive = onRecover,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
            onNegative = onCancelRecovery
        )
    }
    deliveryState.saveFailure?.let { destinationId ->
        LoteriasCaixaDSDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_salvar_o_comprovant_e315d0),
            uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            onPositive = { onSave(destinationId.toUri()) },
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
            onNegative = onCancelSave
        )
    }
    deliveryState.viewerFailure?.let { destinationId ->
        LoteriasCaixaDSDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_abrir_o_comprovante_8b45b2),
            uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            onPositive = { onOpenPdf(destinationId.toUri()) },
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
            onNegative = onDismissViewerFailure
        )
    }
}
