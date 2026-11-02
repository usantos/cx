package br.gov.caixa.loterias.feature.payment.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.presentation.mapper.prePurchaseMessage
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentUiState

@Composable
internal fun PaymentConfirmationDialogs(
    state: PaymentUiState,
    onBack: () -> Unit,
    onAcceptPrePurchase: () -> Unit,
    onConfirmDelete: () -> Unit,
    onCancelDelete: () -> Unit,
    onConfirmAddCard: (Long) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (state.showPrePurchaseConfirmation) {
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = prePurchaseMessage(formatBrazilianCurrency(state.cartTotal)),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_confirmar_717bed),
            onPositive = onAcceptPrePurchase,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
            onNegative = onBack,
            onDismiss = onBack
        )
    }
    state.deleteCandidate?.let { card ->
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_excluir_cartao_84b14c),
            message = uiStrings.getString(LocalizedR.string.ui_voce_tem_certeza_de_que_deseja_exclu_693a88, card.brand, card.lastDigits),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            onPositive = onConfirmDelete,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onNegative = onCancelDelete
        )
    }
    state.pendingAddMethodId?.let { methodId ->
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_o_cartao_de_credito_adicionado_deve_3f3833),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = {
                onConfirmAddCard(methodId)
            }
        )
    }
}
