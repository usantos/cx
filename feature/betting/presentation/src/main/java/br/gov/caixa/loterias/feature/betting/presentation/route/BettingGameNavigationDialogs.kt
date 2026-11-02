package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSHtmlDialog
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingMessageDialog
import br.gov.caixa.loterias.feature.betting.presentation.mapper.legacyBettingHelp
import br.gov.caixa.loterias.feature.betting.presentation.mapper.legacyBettingHelpTitle
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

@Composable
internal fun BettingExitConfirmation(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    BettingMessageDialog(
        title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
        message = uiStrings.getString(LocalizedR.string.ui_ao_voltar_para_a_pagina_inicial_sua_128dc3),
        positiveLabel = uiStrings.getString(LocalizedR.string.ui_confirmar_717bed),
        onPositive = onConfirm,
        negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
        onNegative = onDismiss
    )
}

@Composable
internal fun BettingHelpDialog(state: BettingGameUiState, onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSHtmlDialog(
        title = legacyBettingHelpTitle(state.game?.modality?.id ?: 0, state.game?.modality?.name.orEmpty()),
        html = legacyBettingHelp(state.game?.modality?.id ?: 0),
        positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
        onPositive = onDismiss
    )
}
