package br.gov.caixa.loterias.feature.tickets.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseMonthsUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class TicketsHistoryPurchases(
    private val mutableState: ReduxStateStore<TicketsUiState, TicketsStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getPurchaseMonths: GetPurchaseMonthsUseCase,
    private val pagination: TicketsHistoryPagination,
    private val isSessionCurrent: () -> Boolean,
    private val fail: (String) -> Unit,
) {
    fun loadPurchases() {
        if (!isSessionCurrent()) return
        if (mutableState.value.isLoading) return
        val generation = pagination.beginQuery()
        mutableState.dispatch(TicketsStateAction.PurchasesLoading)
        scope.launch(dispatchers.io) {
            if (!isSessionCurrent()) return@launch
            val monthsResult = getPurchaseMonths()
            pagination.ifCurrent(generation) {
                when (monthsResult) {
                    is AppResult.Success -> mutableState.dispatch(TicketsStateAction.PurchaseMonthsReceived(monthsResult.value))
                    is AppResult.Failure -> fail(messageText("nao_foi_possivel_carregar_os_mes_526eac"))
                }
            }
            if (monthsResult is AppResult.Success) {
                pagination.loadPurchasesWith(mutableState.value.purchaseFilters, generation)
            }
        }
    }

    fun reloadPurchases() {
        if (!isSessionCurrent()) return
        val generation = pagination.beginQuery()
        mutableState.dispatch(TicketsStateAction.PurchasesReloading)
        scope.launch(dispatchers.io) {
            pagination.loadPurchasesWith(mutableState.value.purchaseFilters, generation)
        }
    }
}
