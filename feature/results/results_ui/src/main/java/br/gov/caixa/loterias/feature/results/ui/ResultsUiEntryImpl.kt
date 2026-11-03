package br.gov.caixa.loterias.feature.results.ui

import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.results.presentation.delivery.ResultsUiEntry
import br.gov.caixa.loterias.feature.results.presentation.state.ConfirmedBetResultsState
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet

@Keep
class ResultsUiEntryImpl : ResultsUiEntry {
    @Composable
    override fun ResultDetailScreen(
        state: ResultsUiState,
        onBack: () -> Unit,
        onContestChange: (String) -> Unit,
        onSearch: () -> Unit,
        onRetry: () -> Unit,
        onPreviousContest: () -> Unit,
        onNextContest: () -> Unit,
        onDismissInputError: () -> Unit,
    ) {
        br.gov.caixa.loterias.feature.results.presentation.screen.DeliveredResultDetailScreen(
            state,
            onBack,
            onContestChange,
            onSearch,
            onRetry,
            onPreviousContest,
            onNextContest,
            onDismissInputError
        )
    }

    @Composable
    override fun ConfirmedBetResultScreen(
        bet: ConfirmedBet,
        state: ConfirmedBetResultsState,
        onBack: () -> Unit,
        onComplete: () -> Unit,
        onRetry: () -> Unit,
        onRedeemPrize: (() -> Unit)?,
    ) {
        br.gov.caixa.loterias.feature.results.presentation.screen.DeliveredConfirmedBetResultScreen(
            bet,
            state,
            onBack,
            onComplete,
            onRetry,
            onRedeemPrize
        )
    }

    @Composable
    override fun ResultModalityScreen(onBack: () -> Unit, onSelectModality: (String) -> Unit) {
        br.gov.caixa.loterias.feature.results.presentation.components.DeliveredResultModalityScreen(onBack, onSelectModality)
    }
}
