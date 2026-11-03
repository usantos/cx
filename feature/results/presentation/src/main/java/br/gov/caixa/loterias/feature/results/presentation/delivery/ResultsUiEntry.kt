package br.gov.caixa.loterias.feature.results.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntry
import br.gov.caixa.loterias.feature.results.presentation.state.ConfirmedBetResultsState
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet

interface ResultsUiEntry : FeatureUiEntry {
    @Composable
    fun ResultDetailScreen(
        state: ResultsUiState,
        onBack: () -> Unit,
        onContestChange: (String) -> Unit,
        onSearch: () -> Unit,
        onRetry: () -> Unit,
        onPreviousContest: () -> Unit,
        onNextContest: () -> Unit,
        onDismissInputError: () -> Unit,
    )

    @Composable
    fun ConfirmedBetResultScreen(
        bet: ConfirmedBet,
        state: ConfirmedBetResultsState,
        onBack: () -> Unit,
        onComplete: () -> Unit,
        onRetry: () -> Unit,
        onRedeemPrize: (() -> Unit)?,
    )

    @Composable
    fun ResultModalityScreen(onBack: () -> Unit, onSelectModality: (String) -> Unit)
}
