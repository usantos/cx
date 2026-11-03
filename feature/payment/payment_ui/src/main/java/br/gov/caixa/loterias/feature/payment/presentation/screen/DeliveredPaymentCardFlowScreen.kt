package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.feature.payment.presentation.components.CenteredPaymentProgress
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState
import br.gov.caixa.loterias.feature.payment.ui.R as DeliveryR

@Composable
fun PaymentCardFlowScreenDelivered(state: PaymentCardUiState, actions: PaymentCardActions, onBack: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(DeliveryR.string.ui_forma_de_pagamento_faf708),
        onBack = onBack,
        modifier = Modifier
            .uiId("pay.0055")
    ) { padding ->
        when (state.stage) {
            PaymentCardStage.LOADING -> CenteredPaymentProgress((Modifier.padding(padding)).uiId("pay.0056"))

            PaymentCardStage.FORM -> CardFormDelivered(state, actions, onBack, (Modifier.padding(padding)).uiId("pay.0057"))

            PaymentCardStage.PREVIEW -> CardPreviewDelivered(state, actions, (Modifier.padding(padding)).uiId("pay.0058"))

            PaymentCardStage.CVV -> CardCvvDelivered(state, actions, (Modifier.padding(padding)).uiId("pay.0059"))

            PaymentCardStage.CONFIRM -> CardConfirmationDelivered(state, actions, (Modifier.padding(padding)).uiId("pay.0060"))

            PaymentCardStage.REVIEW_REQUIRED -> CardConfirmationDelivered(state, actions, (Modifier.padding(padding)).uiId("pay.0061"))

            PaymentCardStage.REDIRECT_REQUIRED -> CardConfirmationDelivered(state, actions, (Modifier.padding(padding)).uiId("pay.0062"))

            PaymentCardStage.NOTICE_REQUIRED -> CardConfirmationDelivered(state, actions, (Modifier.padding(padding)).uiId("pay.0063"))

            PaymentCardStage.PROCESSING,
            PaymentCardStage.SUCCESS,
            -> CenteredPaymentProgress(
                (Modifier.padding(padding)).uiId("pay.0064"),
                uiStrings.getString(DeliveryR.string.ui_estamos_processando_sua_compra_952655)
            )

            PaymentCardStage.FAILURE -> CardFailureDelivered(state, actions, onBack, (Modifier.padding(padding)).uiId("pay.0065"))
        }
    }
}
