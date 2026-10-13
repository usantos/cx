package br.gov.caixa.loterias.feature.home

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.CaixaDSAlertDialog

/** PrincipalActivity.trataPopupDeNotificacaoHandler: payment notice precedes the notification. */
@Composable
fun HomePaymentNotice(onOpenPurchases: () -> Unit) {
    CaixaDSAlertDialog(
        title = "Atenção",
        message = "Você possui compra(s) cancelada(s) por falta de identificação do pagamento dentro do tempo limite para efetivação das apostas. As apostas contidas na compra não concorrem a qualquer concurso. Não se preocupe, caso tenha realizado o pagamento realizaremos o estorno do valor para a conta pagadora.",
        confirmText = "Acompanhe suas compras",
        onConfirm = onOpenPurchases,
        onDismiss = {},
    )
}
