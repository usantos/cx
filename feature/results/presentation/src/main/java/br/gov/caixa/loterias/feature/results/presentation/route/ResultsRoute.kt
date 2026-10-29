package br.gov.caixa.loterias.feature.results.presentation.route

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.feature.results.presentation.screen.ResultDetailScreen
import br.gov.caixa.loterias.feature.results.presentation.components.ResultModalityScreen
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import br.gov.caixa.loterias.feature.results.presentation.viewmodel.ResultsViewModel

@Composable
fun ResultsRoute(viewModel: ResultsViewModel, onBack: () -> Unit, showDetail: Boolean = false) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(showDetail) {
        if (showDetail && state.modality == null) viewModel.selectModality("megasena")
    }
    val backToModalities = !showDetail && state.modality != null
    val navigateBack: () -> Unit = {
        if (backToModalities) viewModel.clearSelection() else onBack()
    }
    BackHandler(enabled = backToModalities, onBack = navigateBack)
    ResultsScreen(
        state = state,
        onBack = navigateBack,
        onSelectModality = viewModel::selectModality,
        onContestChange = viewModel::changeContest,
        onSearch = viewModel::searchContest,
        onRetry = viewModel::retry,
        onPreviousContest = viewModel::previousContest,
        onNextContest = viewModel::nextContest,
        onDismissInputError = viewModel::dismissInputError,
        showDetail = showDetail || state.modality != null,
    )
}

@Composable
fun ResultsScreen(
    state: ResultsUiState,
    onBack: () -> Unit,
    onSelectModality: (String) -> Unit,
    onContestChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRetry: () -> Unit,
    onPreviousContest: () -> Unit,
    onNextContest: () -> Unit,
    onDismissInputError: () -> Unit = {},
    showDetail: Boolean = false,
) {
    if (showDetail) {
        ResultDetailScreen(
            state = state,
            onBack = onBack,
            onContestChange = onContestChange,
            onSearch = onSearch,
            onRetry = onRetry,
            onPreviousContest = onPreviousContest,
            onNextContest = onNextContest,
            onDismissInputError = onDismissInputError,
        )
    } else {
        ResultModalityScreen(onBack, onSelectModality)
    }
}
