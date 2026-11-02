package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.presentation.components.CenteredPaymentProgress
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState

internal data class PaymentCardActions(
    val changeNumber: (String) -> Unit,
    val changeHolderName: (String) -> Unit,
    val changeMonth: (String) -> Unit,
    val changeYear: (String) -> Unit,
    val toggleSave: () -> Unit,
    val continueFromCard: () -> Unit,
    val changeCvv: (String) -> Unit,
    val continueFromCvv: () -> Unit,
    val submit: () -> Unit,
    val retry: () -> Unit,
)

@Composable
internal fun PaymentCardFlowScreen(state: PaymentCardUiState, actions: PaymentCardActions, onBack: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_forma_de_pagamento_faf708),
        onBack = onBack,
        modifier = Modifier
            .uiId("pay.0055")
    ) { padding ->
        when (state.stage) {
            PaymentCardStage.LOADING -> CenteredPaymentProgress((Modifier.padding(padding)).uiId("pay.0056"))

            PaymentCardStage.FORM -> CardForm(state, actions, onBack, (Modifier.padding(padding)).uiId("pay.0057"))

            PaymentCardStage.PREVIEW -> CardPreview(state, actions, (Modifier.padding(padding)).uiId("pay.0058"))

            PaymentCardStage.CVV -> CardCvv(state, actions, (Modifier.padding(padding)).uiId("pay.0059"))

            PaymentCardStage.CONFIRM -> CardConfirmation(state, actions, (Modifier.padding(padding)).uiId("pay.0060"))

            PaymentCardStage.REVIEW_REQUIRED -> CardConfirmation(state, actions, (Modifier.padding(padding)).uiId("pay.0061"))

            PaymentCardStage.REDIRECT_REQUIRED -> CardConfirmation(state, actions, (Modifier.padding(padding)).uiId("pay.0062"))

            PaymentCardStage.NOTICE_REQUIRED -> CardConfirmation(state, actions, (Modifier.padding(padding)).uiId("pay.0063"))

            PaymentCardStage.PROCESSING,
            PaymentCardStage.SUCCESS,
            -> CenteredPaymentProgress(
                (Modifier.padding(padding)).uiId("pay.0064"),
                uiStrings.getString(LocalizedR.string.ui_estamos_processando_sua_compra_952655)
            )

            PaymentCardStage.FAILURE -> CardFailure(state, actions, onBack, (Modifier.padding(padding)).uiId("pay.0065"))
        }
    }
}
