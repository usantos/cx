package br.gov.caixa.loterias.feature.home.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.home.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.home.presentation.state.HomeResultsAction
import br.gov.caixa.loterias.feature.home.presentation.state.HomeStateAction
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.results.domain.usecase.GetLatestResultUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

internal class HomeResultsCoordinator(
    private val store: ReduxStateStore<HomeUiState, HomeStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val latest: GetLatestResultUseCase?,
) : HomeResultActions {
    private val jobs = mutableMapOf<String, Job>()

    override fun toggle(page: Int, modality: String) = synchronized(store) {
        val current = store.value.results
        if (latest == null || current.pendingPage != null || modality.isBlank()) return@synchronized
        if (current.expandedPage == page) {
            store.dispatch(HomeResultsAction.ExpansionChanged(null, null))
        } else if (modality in current.cache) {
            store.dispatch(HomeResultsAction.ExpansionChanged(page, modality))
        } else {
            request(modality, page)
        }
    }

    override fun load(modalities: List<String>, refresh: Boolean) = synchronized(store) {
        if (latest == null) return@synchronized
        if (refresh) invalidate()
        modalities.distinct().filter { it.isNotBlank() }.forEach { modality ->
            if (modality !in jobs && modality !in store.value.results.cache) request(modality, null)
        }
    }

    private fun request(modality: String, page: Int?) {
        val useCase = latest ?: return
        val generation = store.value.results.generation
        jobs[modality]?.cancel()
        store.dispatch(HomeResultsAction.Requested(generation, modality, page))
        jobs[modality] = scope.launch(dispatchers.io, start = CoroutineStart.LAZY) {
            val outcome = useCase(modality)
            currentCoroutineContext().ensureActive()
            synchronized(store) {
                if (store.value.results.generation != generation) return@synchronized
                val result = (outcome as? AppResult.Success)?.value
                if (result != null && normalize(result.modality) == normalize(modality)) {
                    store.dispatch(HomeResultsAction.Received(generation, modality, page, result))
                } else {
                    val failure = (outcome as? AppResult.Failure)?.error
                    val reason = (failure as? br.gov.caixa.loterias.core.common.result.AppError.Validation)?.reason
                    store.dispatch(
                        HomeResultsAction.Failed(
                            generation,
                            modality,
                            if (page == null) null else reason ?: messageText("nao_foi_possivel_concluir_esta_o_3c7db3")
                        )
                    )
                }
            }
        }.also { it.start() }
    }

    fun invalidate() = synchronized(store) {
        jobs.values.forEach(Job::cancel)
        jobs.clear()
        store.dispatch(HomeResultsAction.Invalidated(store.value.results.generation + 1))
    }

    override fun dismissError() {
        store.dispatch(HomeResultsAction.ErrorDismissed)
    }

    override fun close() {
        store.dispatch(HomeResultsAction.ExpansionChanged(null, null))
    }

    private fun normalize(value: String) = java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "").filter(Char::isLetterOrDigit).lowercase()
        .let { if (it == "super7") "supersete" else it }
}
