package br.gov.caixa.loterias.feature.marketplace.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.SearchMarketplaceUseCase
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.marketplaceModalityNames
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceStateAction
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

internal class MarketplaceLoadingCoordinator(
    private val mutableState: ReduxStateStore<MarketplaceUiState, MarketplaceStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val searchMarketplace: SearchMarketplaceUseCase,
    private val filter: MarketplaceFilter,
    private val getBettingParameters: br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase?,
    private val filteredSearch: Boolean,
    private val getSpecialContests: br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceSpecialContestsUseCase? = null
) {
    private val pagination = MarketplaceSearchPagination(filter)
    private var searchJob: Job? = null
    private var headerJob: Job? = null
    private var searchGeneration = 0L
    private var headerGeneration = 0L

    fun loadContestHeader() = synchronized(mutableState) {
        if (getBettingParameters == null && getSpecialContests == null) return
        headerJob?.cancel()
        val generation = ++headerGeneration
        headerJob = scope.launch(dispatchers.io) {
            val result = getBettingParameters?.invoke()
            val configured = (getSpecialContests?.invoke() as? AppResult.Success)?.value
                ?: br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests()
            currentCoroutineContext().ensureActive()
            if (generation != headerGeneration) return@launch
            val parameters = (result as? AppResult.Success)?.value
            val games = parameters?.games.orEmpty()
            val game = games.firstOrNull {
                filter.lottery.isBlank() && it.modality.id == filter.modalityId &&
                    (it.contestType.equals("ESPECIAL", true) || it.contestType.equals("E", true)) == (filter.contestType == 2)
            }?.takeIf { it.contestNumber > 0 }
            // O cabeçalho da lista da CX obtém sua identidade e metadados do jogo de simulação.
            // Somente o título de Dia dos Pais da Loteca substitui aqui o título especial anual.
            val specialName = game?.takeIf { it.modality.id == 19 || it.modality.code.equals("LOTECA", true) }?.let {
                configured.label(it.modality.code, filter.contestType ?: 1, it.contestNumber)
                    ?.replace('\n', ' ')
            }
            val names = marketplaceModalityNames(games)
            synchronized(mutableState) {
                mutableState.dispatch(
                    MarketplaceStateAction.HeaderReceived(generation != headerGeneration, game, names, configured, specialName)
                )
            }
        }
    }

    fun search() = synchronized(mutableState) {
        searchJob?.cancel()
        searchGeneration++
        pagination.reset()
        load(page = 0, append = false)
    }

    fun loadMore() {
        val page = pagination.nextPage(mutableState.value) ?: return
        load(page, append = true)
    }

    private fun load(page: Int, append: Boolean) {
        val generation = searchGeneration
        val requestedFilter = pagination.searchFilter
        mutableState.dispatch(MarketplaceStateAction.SearchStarted(append))
        searchJob = scope.launch(dispatchers.io) {
            val outcome = searchMarketplace(requestedFilter, page)
            currentCoroutineContext().ensureActive()
            if (generation != searchGeneration) return@launch
            when (outcome) {
                is AppResult.Success -> {
                    val result = outcome.value
                    pagination.nextArea = result.nextArea
                    synchronized(mutableState) {
                        mutableState.dispatch(MarketplaceStateAction.SearchReceived(generation != searchGeneration, append, result))
                    }
                    // Uma área local vazia deve continuar para fora mesmo sem um rodapé de lista visível.
                    if (generation == searchGeneration &&
                        (result.continueAutomatically || (result.pools.isEmpty() && result.nextArea != null))
                    ) {
                        loadMore()
                    }
                }

                is AppResult.Failure -> {
                    synchronized(mutableState) {
                        mutableState.dispatch(MarketplaceStateAction.SearchFailed(generation != searchGeneration, filteredSearch))
                    }
                }
            }
        }
    }
}
