package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpoint
/** Seleções antigas ficam visíveis apenas para revisão, sem controles de envio ou seleção. */
@Composable
internal fun BettingRecoveredDraftReview(draft: BettingCheckpoint) {
    Column(Modifier.heightIn(max = 360.dp).uiId("bet.0049").verticalScroll(rememberScrollState())) {
        RecoveredDraftIdentity(draft)
        RecoveredSportsSelections(draft)
        RecoveredDraftQuantities(draft)
    }
}

@Composable
private fun RecoveredDraftIdentity(draft: BettingCheckpoint) {
    CaixaDSText(stringResource(R.string.betting_recovery_review_title), modifier = Modifier.uiId("bet.0050"))
    CaixaDSText(
        stringResource(
            R.string.betting_recovery_review_contest,
            draft.modalityId,
            draft.contestNumber,
            stringResource(
                if (draft.contestType ==
                    "NORMAL"
                ) {
                    R.string.betting_recovery_contest_normal
                } else {
                    R.string.betting_recovery_contest_special
                }
            )
        ),
        modifier = Modifier.uiId("bet.0051")
    )
    val mode = stringResource(
        if (draft.mode ==
            "MANUAL"
        ) {
            R.string.betting_recovery_mode_manual
        } else {
            R.string.betting_recovery_mode_surprise
        }
    )
    val step = stringResource(
        if (draft.step ==
            "NUMBERS"
        ) {
            R.string.betting_recovery_step_numbers
        } else {
            R.string.betting_recovery_step_complement
        }
    )
    CaixaDSText(stringResource(R.string.betting_recovery_review_mode, mode, step), modifier = Modifier.uiId("bet.0052"))
    CaixaDSText(
        stringResource(R.string.betting_recovery_review_numbers, draft.numbers.sorted().joinToString(" · ")),
        modifier = Modifier
            .uiId("bet.0053")
    )
    CaixaDSText(
        stringResource(R.string.betting_recovery_review_clovers, draft.clovers.sorted().joinToString(" · ")),
        modifier = Modifier
            .uiId("bet.0054")
    )
    draft.teamNumber?.let { CaixaDSText(stringResource(R.string.betting_recovery_review_team, it), modifier = Modifier.uiId("bet.0055")) }
    if (draft.teamQuery.isNotBlank()) CaixaDSText(draft.teamQuery, modifier = Modifier.uiId("bet.0056"))
    draft.monthNumber?.let { CaixaDSText(stringResource(R.string.betting_recovery_review_month, it), modifier = Modifier.uiId("bet.0057")) }
}

@Composable
private fun RecoveredSportsSelections(draft: BettingCheckpoint) {
    draft.loteca.toSortedMap().forEach { (match, outcomes) ->
        val labels = outcomes.sorted().map { outcome ->
            stringResource(
                when (outcome) {
                    "HOME" -> R.string.betting_recovery_home
                    "DRAW" -> R.string.betting_recovery_draw
                    else -> R.string.betting_recovery_away
                }
            )
        }
        CaixaDSText(
            stringResource(R.string.betting_recovery_review_loteca, match, labels.joinToString(" · ")),
            modifier = Modifier
                .uiId("bet.0058")
        )
    }
    draft.lotogol.toSortedMap().forEach { (match, scores) ->
        CaixaDSText(
            stringResource(
                R.string.betting_recovery_review_lotogol,
                match,
                scores.first?.toString() ?: "—",
                scores.second?.toString() ?: "—"
            ),
            modifier = Modifier.uiId("bet.0059")
        )
    }
    draft.superSeven.forEachIndexed { index, column ->
        if (column.isNotEmpty()) {
            CaixaDSText(
                stringResource(
                    R.string.betting_recovery_review_column,
                    index + 1,
                    column.sorted().joinToString(" · ")
                ),
                modifier = Modifier.uiId("bet.0060")
            )
        }
    }
}

@Composable
private fun RecoveredDraftQuantities(draft: BettingCheckpoint) {
    CaixaDSText(
        stringResource(
            R.string.betting_recovery_review_quantities,
            draft.numberQuantity,
            draft.surpriseQuantity,
            draft.recurringDraws
        ),
        modifier = Modifier.uiId("bet.0061")
    )
    CaixaDSText(
        stringResource(
            R.string.betting_recovery_review_clover_quantities,
            draft.manualCloverQuantity,
            draft.surpriseCloverQuantity
        ),
        modifier = Modifier.uiId("bet.0062")
    )
    CaixaDSText(
        stringResource(
            R.string.betting_recovery_review_mirror,
            stringResource(if (draft.mirror) R.string.betting_recovery_yes else R.string.betting_recovery_no)
        ),
        modifier = Modifier.uiId("bet.0063")
    )
    if (draft.favoriteName.isNotBlank()) {
        CaixaDSText(
            stringResource(R.string.betting_recovery_review_favorite, draft.favoriteName),
            modifier = Modifier.uiId("bet.0064")
        )
    }
}
