package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
@Composable
internal fun BettingMessageDialog(
    title: String,
    message: String,
    positiveLabel: String,
    onPositive: () -> Unit,
    negativeLabel: String? = null,
    onNegative: () -> Unit = {},
): Unit = LoteriasCaixaDSDialog(
    title = title,
    message = message,
    positiveLabel = positiveLabel,
    onPositive = onPositive,
    negativeLabel = negativeLabel,
    onNegative = onNegative
)
