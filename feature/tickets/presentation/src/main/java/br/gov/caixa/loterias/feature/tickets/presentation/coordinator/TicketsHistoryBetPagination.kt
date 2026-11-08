package br.gov.caixa.loterias.feature.tickets.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetFilters
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** Cada período aplicado possui seus próprios filtros, deslocamento e respostas válidas. */
internal class TicketsHistoryBetPagination(
    private val mutableState: ReduxStateStore<TicketsUiState, TicketsStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getConfirmedBets: GetConfirmedBetsUseCase,
    private val isSessionCurrent: () -> Boolean,
    private val fail: (String) -> Unit,
) {
    private val queryLock = Any()
    private var generation = 0L
    private var loadedGeneration = -1L
    private var appliedFilters = BetFilters()
    private var nextOffset = 0
    private var knownTotal: Long? = null

    fun beginQuery(): Long = synchronized(queryLock) { ++generation }

    fun isCurrent(queryGeneration: Long): Boolean = synchronized(queryLock) {
        queryGeneration == generation && isSessionCurrent()
    }

    fun ifCurrent(queryGeneration: Long, action: () -> Unit) = synchronized(queryLock) {
        if (isCurrent(queryGeneration)) action()
    }

    suspend fun loadBetsWith(filters: BetFilters, queryGeneration: Long) {
        ifCurrent(queryGeneration) {
            appliedFilters = filters
            nextOffset = 0
            knownTotal = null
        }
        loadPage(filters, offset = 0, append = false, queryGeneration)
    }

    fun loadMoreBets(): Unit = synchronized(queryLock) {
        val state = mutableState.value
        if (!isCurrent(loadedGeneration)) return@synchronized
        if (state.isLoading || state.isLoadingMoreBets || !state.canLoadMoreBets) return@synchronized
        val queryGeneration = generation
        val filters = appliedFilters
        val offset = nextOffset
        mutableState.dispatch(TicketsStateAction.BetPageLoading)
        scope.launch(dispatchers.io) { loadPage(filters, offset, append = true, queryGeneration) }
    }

    private suspend fun loadPage(filters: BetFilters, offset: Int, append: Boolean, queryGeneration: Long) {
        if (!isCurrent(queryGeneration)) return
        val result = getConfirmedBets.page(filters.query(offset))
        ifCurrent(queryGeneration) {
            when (result) {
                is AppResult.Success -> {
                    loadedGeneration = queryGeneration
                    nextOffset = offset + result.value.bets.size
                    knownTotal = result.value.totalCount ?: knownTotal
                    val hasMore = result.value.bets.isNotEmpty() && (
                        knownTotal?.let { nextOffset.toLong() < it }
                            ?: (result.value.bets.size >= filters.pageSize)
                        )
                    mutableState.dispatch(TicketsStateAction.BetsReceived(result.value, append, hasMore))
                }

                is AppResult.Failure -> {
                    val message = messageText("nao_foi_possivel_carregar_os_dad_bf517e")
                    if (append) mutableState.dispatch(TicketsStateAction.BetPageFailed(message)) else fail(message)
                }
            }
        }
    }
}
