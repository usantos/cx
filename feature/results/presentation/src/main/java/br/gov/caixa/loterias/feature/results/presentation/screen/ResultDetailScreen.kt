package br.gov.caixa.loterias.feature.results.presentation.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.results.presentation.components.ResultContestSearchAction
import br.gov.caixa.loterias.feature.results.presentation.components.ResultContestSearchField
import br.gov.caixa.loterias.feature.results.presentation.mapper.modalityName
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import br.gov.caixa.loterias.feature.results.R as LocalizedR

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
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val lotteryStyle = loteriasCaixaDSLotteryStyle(0, modalityName(state.modality))
    val color = lotteryStyle.dark
    val currentContest = state.result?.contest
    val previousEnabled = !state.isLoading && currentContest != null && currentContest > 1
    val nextEnabled = !state.isLoading && currentContest != null && state.latestContest != null &&
        currentContest < state.latestContest
    var searchVisible by remember { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(searchVisible) {
        if (searchVisible) searchFocusRequester.requestFocus() else keyboard?.hide()
    }
    LaunchedEffect(searchVisible, state.isLoading) {
        if (searchVisible && state.isLoading) searchVisible = false
    }
    state.inputError?.let { message ->
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = message,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = onDismissInputError,
            onDismiss = onDismissInputError
        )
    }
    LoteriasCaixaDSScaffold(
        title = if (searchVisible) {
            ""
        } else {
            currentContest?.let { "Concurso nº $it" } ?: uiStrings.getString(
                LocalizedR.string.ui_resultados_310561
            )
        },
        onBack = onBack,
        topBarAction = {
            ResultContestSearchAction(searchVisible) {
                searchVisible = !searchVisible
                if (searchVisible) onContestChange("")
            }
        },
        topBarTitleContent = if (searchVisible) {
            {
                ResultContestSearchField(state.contestInput, searchFocusRequester, onContestChange, onSearch)
            }
        } else {
            null
        },
        bottomBar = {
            ResultContestNavigation(
                currentContest,
                previousEnabled,
                nextEnabled,
                onPreviousContest,
                onNextContest,
                onBack
            )
        },
        containerColor = color,
        modifier = Modifier.uiId("results.0089")
    ) { padding ->
        ResultDetailBody(state, lotteryStyle, padding, onRetry)
    }
}
