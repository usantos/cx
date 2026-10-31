package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpoint

/** O aviso usa o padrão do legado; o rascunho continua preservado fora da interface. */
@Composable
internal fun BettingRecoveredDraftReview(draft: BettingCheckpoint, onBack: () -> Unit) {
    CaixaDSDialog(
        title = stringResource(R.string.ui_atencao_b0f8a4),
        message = stringResource(R.string.betting_recovery_contest_closed, draft.contestNumber.toString()),
        positiveLabel = stringResource(R.string.ui_entendi_9f9373),
        onPositive = onBack,
        onDismiss = onBack
    )
}
