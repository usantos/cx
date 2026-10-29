package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.feature.home.R as LocalizedR

/** PrincipalActivity.trataPopupDeNotificacaoHandler: o aviso de pagamento precede a notificação. */
@Composable
fun HomePaymentNotice(onOpenPurchases: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSAlertDialog(
        title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
        message = uiStrings.getString(LocalizedR.string.ui_voce_possui_compra_s_cancelada_s_por_c4885e),
        confirmText = uiStrings.getString(LocalizedR.string.ui_acompanhe_suas_compras_014250),
        onConfirm = onOpenPurchases,
        onDismiss = {}
    )
}
