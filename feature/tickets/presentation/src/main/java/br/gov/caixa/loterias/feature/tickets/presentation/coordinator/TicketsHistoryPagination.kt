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
    fun loadMorePurchases() {
        val current = mutableState.value
        if (current.isLoading || current.isLoadingMore || !current.canLoadMore) return
        mutableState.dispatch(TicketsStateAction.PurchasePageLoading)
        scope.launch(dispatchers.io) {
            loadPurchasesWith(mutableState.value.purchaseFilters, append = true)
        }
    }

    suspend fun loadPurchasesWith(filters: PurchaseFilters, append: Boolean) {
        if (!isSessionCurrent()) return
        val offset = if (append) mutableState.value.purchases.size else 0
        val result = getPurchases(filters.query(offset))
        if (!isSessionCurrent()) return
        when (result) {
            is AppResult.Success -> mutableState.dispatch(TicketsStateAction.PurchasePageReceived(append, result.value))
            is AppResult.Failure -> fail(messageText("nao_foi_possivel_carregar_as_com_4f57c2"))
        }
    }
}

private const val PURCHASE_PAGE_SIZE = 10
