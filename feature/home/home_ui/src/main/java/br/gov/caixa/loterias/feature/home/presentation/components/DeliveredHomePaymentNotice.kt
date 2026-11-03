package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAlertDialog
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR

/** PrincipalActivity.trataPopupDeNotificacaoHandler: o aviso de pagamento precede a notificação. */
@Composable
fun RenderHomePaymentNotice(onOpenPurchases: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSAlertDialog(
        title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
        message = uiStrings.getString(DeliveryR.string.ui_voce_possui_compra_s_cancelada_s_por_c4885e),
        confirmText = uiStrings.getString(DeliveryR.string.ui_acompanhe_suas_compras_014250),
        onConfirm = onOpenPurchases,
        onDismiss = {}
    )
}
