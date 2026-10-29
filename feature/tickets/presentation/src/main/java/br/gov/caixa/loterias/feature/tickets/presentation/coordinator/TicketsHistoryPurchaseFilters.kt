package br.gov.caixa.loterias.feature.tickets.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseMonth
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasePaymentMethodsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseStatusesUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class TicketsHistoryPurchaseFilters(
    private val mutableState: ReduxStateStore<TicketsUiState, TicketsStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getPaymentMethods: GetPurchasePaymentMethodsUseCase,
    private val getPurchaseStatuses: GetPurchaseStatusesUseCase,
    private val reloadPurchases: () -> Unit,
    private val fail: (String) -> Unit,
) {
    fun selectPurchaseMonth(month: PurchaseMonth) {
        mutableState.dispatch(TicketsStateAction.PurchaseMonthSelected(month))
        reloadPurchases()
    }

    fun openPurchaseFilters() {
        if (mutableState.value.isLoading) return
        mutableState.dispatch(TicketsStateAction.PurchaseFiltersLoading)
        scope.launch(dispatchers.io) {
            val methods = when (val result = getPaymentMethods()) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> return@launch fail(messageText("nao_foi_possivel_carregar_os_mei_09d0c5"))
            }
            val statuses = when (val result = getPurchaseStatuses(mutableState.value.purchaseFilters.paymentMethodId)) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> return@launch fail(messageText("nao_foi_possivel_carregar_as_sit_30d9f2"))
            }
            mutableState.dispatch(TicketsStateAction.PurchaseFiltersReceived(methods, statuses))
        }
    }

    fun closePurchaseFilters() {
        mutableState.dispatch(TicketsStateAction.PurchaseFiltersClosed)
    }

    fun selectPaymentMethod(id: Long?) {
        mutableState.dispatch(TicketsStateAction.PaymentMethodSelected(id))
        scope.launch(dispatchers.io) {
            when (val result = getPurchaseStatuses(id)) {
                is AppResult.Success -> mutableState.dispatch(TicketsStateAction.PurchaseStatusesReceived(result.value))
                is AppResult.Failure -> fail(messageText("nao_foi_possivel_carregar_as_sit_30d9f2"))
            }
        }
    }

    fun selectPurchaseStatus(id: Long?) {
        mutableState.dispatch(TicketsStateAction.PurchaseStatusSelected(id))
    }

    fun clearPurchaseFilters() {
        mutableState.dispatch(TicketsStateAction.PurchaseFiltersCleared)
        reloadPurchases()
    }

    fun applyPurchaseFilters() {
        mutableState.dispatch(TicketsStateAction.PurchaseFiltersApplied)
        reloadPurchases()
    }
}
