package br.gov.caixa.loterias.feature.results.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.usecase.GetLatestResultUseCase
import br.gov.caixa.loterias.feature.results.domain.usecase.GetResultByContestUseCase
import br.gov.caixa.loterias.feature.results.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.results.presentation.state.HomeResultsAction
import br.gov.caixa.loterias.feature.results.presentation.state.HomeResultsLoadingAction
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsAction
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import br.gov.caixa.loterias.feature.results.presentation.state.reduceHomeResultsLoadingState
import br.gov.caixa.loterias.feature.results.presentation.state.reduceHomeResultsState
import br.gov.caixa.loterias.feature.results.presentation.state.reduceResultsState
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ResultsViewModel(
    private val getLatestResult: GetLatestResultUseCase,
    private val getResultByContest: GetResultByContestUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = reduxStateStore<ResultsUiState, ResultsAction>(ResultsUiState(), ::reduceResultsState).also {
        addCloseable(it)
    }
    val state: StateFlow<ResultsUiState> = mutableState.state

    private val mutableHomeResults = reduxStateStore<Map<String, DrawResult>, HomeResultsAction>(
        emptyMap(),
        ::reduceHomeResultsState
    ).also {
        addCloseable(it)
    }
    val homeResults = mutableHomeResults.state
    private val homeJobs = mutableMapOf<String, Job>()
    private val mutableHomeResultsLoading = reduxStateStore<Boolean, HomeResultsLoadingAction>(
        false,
        ::reduceHomeResultsLoadingState
    ).also {
        addCloseable(it)
    }
    val homeResultsLoading = mutableHomeResultsLoading.state

    fun loadHomeResults(modalities: List<String>, refresh: Boolean = false) {
        modalities.distinct().filter { it.isNotBlank() }.forEach { modality ->
            if (!refresh && modality in homeJobs) return@forEach
            homeJobs[modality]?.cancel()
            if (refresh) mutableHomeResults.dispatch(HomeResultsAction.RefreshStarted(modality))
            val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
                val outcome = kotlinx.coroutines.withContext(dispatchers.io) { getLatestResult(modality) }
                currentCoroutineContext().ensureActive()
                when (outcome) {
                    is AppResult.Success -> mutableHomeResults.dispatch(HomeResultsAction.Loaded(modality, outcome.value))
                    is AppResult.Failure -> Unit // Como na CX, mantém o atalho de resultados disponível.
                }
            }
            homeJobs[modality] = job
            mutableHomeResultsLoading.dispatch(HomeResultsLoadingAction.LoadStarted)
            job.invokeOnCompletion {
                mutableHomeResultsLoading.dispatch(HomeResultsLoadingAction.JobsChanged(homeJobs.values.any { it.isActive }))
            }
            job.start()
        }
    }

    private var loadingJob: Job? = null

    fun clearSelection() {
        loadingJob?.cancel()
        loadingJob = null
        mutableState.dispatch(ResultsAction.SelectionCleared)
    }

    fun selectModality(modality: String) {
        if (mutableState.value.isLoading) return
        mutableState.dispatch(ResultsAction.ModalitySelected(modality))
        load(isLatest = true) { getLatestResult(modality) }
    }

    fun changeContest(value: String) {
        mutableState.dispatch(ResultsAction.ContestChanged(value))
    }

    fun dismissInputError() {
        mutableState.dispatch(ResultsAction.InputErrorDismissed)
    }

    fun searchContest() {
        val snapshot = mutableState.value
        val contest = snapshot.contestInput.toIntOrNull()
        if (snapshot.modality == null || contest == null || contest <= 0) {
            mutableState.dispatch(ResultsAction.InputRejected(messageText("informe_uma_modalidade_e_um_conc_a51e1e")))
            return
        }
        if (snapshot.latestContest != null && contest > snapshot.latestContest) {
            mutableState.dispatch(
                ResultsAction.InputRejected(messageText("o_ultimo_concurso_apurado_e_o_1_df14d0", snapshot.latestContest))
            )
            return
        }
        load { getResultByContest(snapshot.modality, contest) }
    }

    fun loadContest(modality: String, contest: Int) {
        if (mutableState.value.isLoading) return
        if (modality.isBlank() || contest <= 0) {
            mutableState.dispatch(ResultsAction.InputRejected(messageText("informe_uma_modalidade_e_um_conc_a51e1e")))
            return
        }
        mutableState.dispatch(ResultsAction.ContestSelected(modality, contest))
        load { getResultByContest(modality, contest) }
    }

    fun retry() {
        val snapshot = mutableState.value
        val modality = snapshot.modality ?: return
        val contest = snapshot.contestInput.toIntOrNull()
        load(isLatest = contest == null) {
            if (contest == null) getLatestResult(modality) else getResultByContest(modality, contest)
        }
    }

    fun previousContest() = adjacentContest(-1)

    fun nextContest() = adjacentContest(1)

    private fun adjacentContest(offset: Int) {
        val snapshot = mutableState.value
        val modality = snapshot.modality ?: return
        val current = snapshot.result?.contest ?: return
        val target = current + offset
        if (target <= 0 || (snapshot.latestContest != null && target > snapshot.latestContest)) return
        mutableState.dispatch(ResultsAction.AdjacentContestSelected(target))
        load { getResultByContest(modality, target) }
    }

    private fun load(isLatest: Boolean = false, request: suspend () -> AppResult<DrawResult>) {
        if (mutableState.value.isLoading) return
        mutableState.dispatch(ResultsAction.LoadStarted)
        loadingJob = viewModelScope.launch {
            val outcome = kotlinx.coroutines.withContext(dispatchers.io) { request() }
            currentCoroutineContext().ensureActive()
            when (outcome) {
                is AppResult.Success -> {
                    val result = outcome.value
                    mutableState.dispatch(ResultsAction.Loaded(result, isLatest))
                }

                is AppResult.Failure -> {
                    val modality = mutableState.value.modality
                    if (!isLatest && modality != null) {
                        // VisualizarResultadosActivity recupera uma consulta histórica malsucedida
                        // limpando os limites de concurso e carregando o sorteio mais recente.
                        mutableState.dispatch(ResultsAction.HistoricalLoadFailed)
                        load(isLatest = true) { getLatestResult(modality) }
                    } else {
                        mutableState.dispatch(ResultsAction.LoadFailed(messageText("nao_foi_possivel_consultar_o_res_465180")))
                    }
                }
            }
        }
    }
}
