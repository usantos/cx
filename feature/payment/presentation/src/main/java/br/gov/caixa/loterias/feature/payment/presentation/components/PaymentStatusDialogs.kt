package br.gov.caixa.loterias.feature.payment.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PIX_PENDING_EXIT_MESSAGE
import br.gov.caixa.loterias.feature.payment.presentation.mapper.paymentOperatingHours
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentUiState

@Composable
internal fun PaymentStatusDialogs(
    state: PaymentUiState,
    showPixExitConfirmation: Boolean,
    onKeepPix: () -> Unit,
    onHome: () -> Unit,
    onDismissPixStatus: () -> Unit,
    onDismissDamMessage: () -> Unit,
    onDismissError: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (state.pixRejected) {
        LoteriasCaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_pagamento_nao_aprovado_b8c8b6),
            uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_confirmar_o_pagamen_baf7ad),
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            onDismissPixStatus
        )
    }
    if (showPixExitConfirmation) {
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = PIX_PENDING_EXIT_MESSAGE,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            onPositive = { onKeepPix() },
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_voltar_ao_inicio_372787),
            onNegative = onHome
        )
    }
    state.damMessage?.let {
        LoteriasCaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            paymentOperatingHours(it),
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            onDismissDamMessage
        )
    }
    state.errorMessage?.let {
        LoteriasCaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            onDismissError
        )
    }
}
