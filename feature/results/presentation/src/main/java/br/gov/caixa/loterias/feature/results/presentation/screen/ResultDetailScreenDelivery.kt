package br.gov.caixa.loterias.feature.results.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.results.presentation.delivery.ResultsUiEntry
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState

@Composable
internal fun ResultDetailScreen(
    state: ResultsUiState,
    onBack: () -> Unit,
    onContestChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRetry: () -> Unit,
    onPreviousContest: () -> Unit,
    onNextContest: () -> Unit,
    onDismissInputError: () -> Unit,
) {
    FeatureModuleContent(
        module = AppModule.RESULTS,
        entryType = ResultsUiEntry::class.java,
        onBack = onBack
    ) { entry ->
        entry.ResultDetailScreen(state, onBack, onContestChange, onSearch, onRetry, onPreviousContest, onNextContest, onDismissInputError)
    }
}
