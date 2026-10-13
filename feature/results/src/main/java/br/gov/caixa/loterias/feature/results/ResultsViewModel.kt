package br.gov.caixa.loterias.feature.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.domain.results.DrawResult
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.results.GetLatestResultUseCase
import br.gov.caixa.loterias.core.domain.results.GetResultByContestUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ResultsUiState(
    val modality: String? = null,
    val contestInput: String = "",
    val result: DrawResult? = null,
    val isLoading: Boolean = false,
    val inputError: String? = null,
    val errorMessage: String? = null,
    val latestContest: Int? = null,
)

class ResultsViewModel(
    private val getLatestResult: GetLatestResultUseCase,
    private val getResultByContest: GetResultByContestUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = MutableStateFlow(ResultsUiState())
    val state: StateFlow<ResultsUiState> = mutableState.asStateFlow()

    private val mutableHomeResults = MutableStateFlow<Map<String, DrawResult>>(emptyMap())
    val homeResults = mutableHomeResults.asStateFlow()
    private val homeJobs = mutableMapOf<String, Job>()

    fun loadHomeResults(modalities: List<String>, refresh: Boolean = false) {
        modalities.distinct().filter { it.isNotBlank() }.forEach { modality ->
            if (!refresh && (modality in mutableHomeResults.value || homeJobs[modality]?.isActive == true)) return@forEach
            homeJobs[modality]?.cancel()
            if (refresh) mutableHomeResults.value = mutableHomeResults.value - modality
            homeJobs[modality] = viewModelScope.launch {
                val outcome = kotlinx.coroutines.withContext(dispatchers.io) { getLatestResult(modality) }
                currentCoroutineContext().ensureActive()
                when (outcome) {
                    is AppResult.Success -> mutableHomeResults.value = mutableHomeResults.value + (modality to outcome.value)
                    is AppResult.Failure -> Unit // Like CX, keep the result shortcut available.
                }
            }
        }
    }

    private var loadingJob: Job? = null

    fun clearSelection() {
        loadingJob?.cancel()
        loadingJob = null
        mutableState.value = ResultsUiState()
    }

    fun selectModality(modality: String) {
        if (mutableState.value.isLoading) return
        mutableState.value = ResultsUiState(modality = modality)
        load(isLatest = true) { getLatestResult(modality) }
    }

    fun changeContest(value: String) {
        mutableState.value = mutableState.value.copy(contestInput = value.filter(Char::isDigit), inputError = null)
    }

    fun dismissInputError() {
        mutableState.value = mutableState.value.copy(inputError = null)
    }

    fun searchContest() {
        val snapshot = mutableState.value
        val contest = snapshot.contestInput.toIntOrNull()
        if (snapshot.modality == null || contest == null || contest <= 0) {
            mutableState.value = snapshot.copy(inputError = "Informe uma modalidade e um concurso válido.")
            return
        }
        if (snapshot.latestContest != null && contest > snapshot.latestContest) {
            mutableState.value = snapshot.copy(inputError = "Informe um concurso até ${snapshot.latestContest}.")
            return
        }
        load { getResultByContest(snapshot.modality, contest) }
    }

    fun loadContest(modality: String, contest: Int) {
        if (mutableState.value.isLoading) return
        if (modality.isBlank() || contest <= 0) {
            mutableState.value = mutableState.value.copy(inputError = "Informe uma modalidade e um concurso válido.")
            return
        }
        mutableState.value = mutableState.value.copy(
            modality = modality,
            contestInput = contest.toString(),
            inputError = null,
        )
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
        mutableState.value = snapshot.copy(contestInput = target.toString(), inputError = null)
        load { getResultByContest(modality, target) }
    }

    private fun load(
        isLatest: Boolean = false,
        request: suspend () -> AppResult<DrawResult>,
    ) {
        if (mutableState.value.isLoading) return
        mutableState.value = mutableState.value.copy(isLoading = true, errorMessage = null)
        loadingJob = viewModelScope.launch {
            val outcome = kotlinx.coroutines.withContext(dispatchers.io) { request() }
            currentCoroutineContext().ensureActive()
            when (outcome) {
                is AppResult.Success -> {
                    val result = outcome.value
                    mutableState.value = mutableState.value.copy(
                        result = result,
                        contestInput = result.contest.toString(),
                        latestContest = if (isLatest) result.contest else mutableState.value.latestContest,
                        isLoading = false,
                        inputError = null,
                    )
                }
                is AppResult.Failure -> {
                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        errorMessage = "Não foi possível consultar o resultado. Tente novamente.",
                    )
                }
            }
        }
    }
}
