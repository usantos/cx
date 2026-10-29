package br.gov.caixa.loterias.feature.tickets.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseMonth
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketPeriod
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsConfigurationUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseMonthsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasePaymentMethodsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseStatusesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasesUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import kotlinx.coroutines.CoroutineScope

internal class TicketsHistoryCoordinator(
    mutableState: ReduxStateStore<TicketsUiState, TicketsStateAction>,
    scope: CoroutineScope,
    dispatchers: CoroutineDispatchers,
    getConfirmedBets: GetConfirmedBetsUseCase,
    getPurchases: GetPurchasesUseCase,
    getBetsConfiguration: GetConfirmedBetsConfigurationUseCase,
    getModalities: GetLotteryModalitiesUseCase,
    getPurchaseMonths: GetPurchaseMonthsUseCase,
    purchaseFiltersSources: TicketsPurchaseFilterSources,
    isSessionCurrent: () -> Boolean,
    fail: (String) -> Unit
) {
    private val pagination = TicketsHistoryPagination(
        mutableState,
        scope,
        dispatchers,
        getPurchases,
        isSessionCurrent,
        fail
    )
    private val bets = TicketsHistoryBets(
        mutableState,
        scope,
        dispatchers,
        getConfirmedBets,
        getBetsConfiguration,
        getModalities,
        isSessionCurrent,
        fail
    )
    private val purchases = TicketsHistoryPurchases(
        mutableState,
        scope,
        dispatchers,
        getPurchaseMonths,
        pagination,
        isSessionCurrent,
        fail
    )
    private val betFilters = TicketsHistoryBetFilters(mutableState, bets::applyBetFilters)
    private val purchaseFilters = TicketsHistoryPurchaseFilters(
        mutableState,
        scope,
        dispatchers,
        purchaseFiltersSources.paymentMethods,
        purchaseFiltersSources.statuses,
        purchases::reloadPurchases,
        fail
    )
    private val recovery = TicketsHistoryRecovery(mutableState, bets, purchases)

    fun loadBets() = bets.loadBets()
    fun openBetFilters() = betFilters.openBetFilters()
    fun closeBetFilters() = betFilters.closeBetFilters()
    fun selectBetPeriod(value: TicketPeriod) = betFilters.selectBetPeriod(value)
    fun selectBetStatus(value: Long) = betFilters.selectBetStatus(value)
    fun selectBetOrdering(value: Long) = betFilters.selectBetOrdering(value)
    fun selectBetType(value: Long) = betFilters.selectBetType(value)
    fun selectContestType(value: Long) = betFilters.selectContestType(value)
    fun selectModality(value: Int) = betFilters.selectModality(value)
    fun toggleSurprise() = betFilters.toggleSurprise()
    fun toggleRecurring() = betFilters.toggleRecurring()
    fun toggleCombo() = betFilters.toggleCombo()
    fun clearBetFilters() = betFilters.clearBetFilters()
    fun applyBetFilters() = bets.applyBetFilters()
    fun loadPurchases() = purchases.loadPurchases()
    fun selectPurchaseMonth(month: PurchaseMonth) = purchaseFilters.selectPurchaseMonth(month)
    fun openPurchaseFilters() = purchaseFilters.openPurchaseFilters()
    fun closePurchaseFilters() = purchaseFilters.closePurchaseFilters()
    fun selectPaymentMethod(id: Long?) = purchaseFilters.selectPaymentMethod(id)
    fun selectPurchaseStatus(id: Long?) = purchaseFilters.selectPurchaseStatus(id)
    fun clearPurchaseFilters() = purchaseFilters.clearPurchaseFilters()
    fun applyPurchaseFilters() = purchaseFilters.applyPurchaseFilters()
    fun loadMorePurchases() = pagination.loadMorePurchases()
    fun retryBets() = recovery.retryBets()
    fun retryPurchases() = recovery.retryPurchases()
}

internal data class TicketsPurchaseFilterSources(
    val paymentMethods: GetPurchasePaymentMethodsUseCase,
    val statuses: GetPurchaseStatusesUseCase,
)
