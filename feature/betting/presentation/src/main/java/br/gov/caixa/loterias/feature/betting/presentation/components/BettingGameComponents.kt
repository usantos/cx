package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpoint
@Composable
internal fun BettingMessageDialog(
    title: String,
    message: String,
    positiveLabel: String,
    onPositive: () -> Unit,
    negativeLabel: String? = null,
    onNegative: () -> Unit = {},
) = CaixaDSDialog(
    title = title,
    message = message,
    positiveLabel = positiveLabel,
    onPositive = onPositive,
    negativeLabel = negativeLabel,
    onNegative = onNegative
)

/** textSelecionados possui uma margem nativa de 16dp; o contêiner do bilhete já aplica 8dp. */
@Composable
internal fun legacyPromptInset() = with(androidx.compose.ui.platform.LocalDensity.current) {
    (16.dp.roundToPx() - 8.dp.roundToPx()).toDp()
}

@Composable
internal fun BettingGameError(message: String, padding: PaddingValues, onRetry: () -> Unit, review: BettingCheckpoint? = null) {
    Column(
        Modifier.fillMaxSize().padding(padding).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, color = CaixaDS.colors.text, textAlign = TextAlign.Center, modifier = Modifier.uiId("bet.0029"))
        review?.let { BettingRecoveredDraftReview(it) }
        TextButton(onClick = onRetry, modifier = Modifier.uiId("bet.0030")) {
            Text(stringResource(R.string.betting_recovery_retry), modifier = Modifier.uiId("bet.0031"))
        }
    }
}
