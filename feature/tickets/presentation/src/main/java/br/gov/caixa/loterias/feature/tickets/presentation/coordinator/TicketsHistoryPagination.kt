package br.gov.caixa.loterias.feature.tickets.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasesUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseFilters
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class TicketsHistoryPagination(
    private val mutableState: ReduxStateStore<TicketsUiState, TicketsStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getPurchases: GetPurchasesUseCase,
    private val isSessionCurrent: () -> Boolean,
    private val fail: (String) -> Unit,
) {
    private val queryLock = Any()
    private var generation = 0L
    private var loadedGeneration = -1L
    private var appliedFilters = PurchaseFilters()

    fun beginQuery(): Long = synchronized(queryLock) { ++generation }

    fun ifCurrent(queryGeneration: Long, action: () -> Unit) = synchronized(queryLock) {
        if (queryGeneration == generation && isSessionCurrent()) action()
    }

    fun loadMorePurchases(): Unit = synchronized(queryLock) {
        val current = mutableState.value
        if (!isSessionCurrent() || loadedGeneration != generation) return@synchronized
        if (current.isLoading || current.isLoadingMore || !current.canLoadMore) return@synchronized
        val queryGeneration = generation
        val filters = appliedFilters
        val offset = current.purchases.size
        mutableState.dispatch(TicketsStateAction.PurchasePageLoading)
        scope.launch(dispatchers.io) {
            loadPage(filters, offset, append = true, queryGeneration)
        }
    }

    suspend fun loadPurchasesWith(filters: PurchaseFilters, queryGeneration: Long) {
        ifCurrent(queryGeneration) { appliedFilters = filters }
        loadPage(filters, offset = 0, append = false, queryGeneration)
    }

    private suspend fun loadPage(filters: PurchaseFilters, offset: Int, append: Boolean, queryGeneration: Long) {
        val current = synchronized(queryLock) { queryGeneration == generation && isSessionCurrent() }
        if (!current) return
        val result = getPurchases(filters.query(offset))
        ifCurrent(queryGeneration) {
            when (result) {
                is AppResult.Success -> {
                    if (!append) loadedGeneration = queryGeneration
                    mutableState.dispatch(TicketsStateAction.PurchasePageReceived(append, result.value))
                }

                is AppResult.Failure -> fail(messageText("nao_foi_possivel_carregar_as_com_4f57c2"))
            }
        }
    }
}
