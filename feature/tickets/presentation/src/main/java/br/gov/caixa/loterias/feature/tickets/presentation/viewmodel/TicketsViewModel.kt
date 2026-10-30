package br.gov.caixa.loterias.feature.tickets.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.ValidateDamUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseMonth
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketPeriod
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketScanOrientation
import br.gov.caixa.loterias.feature.tickets.domain.usecase.CheckTicketUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsConfigurationUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseDetailsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseMonthsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasePaymentMethodsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseStatusesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.SavePurchaseAsFavoriteUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.coordinator.TicketModalitiesCoordinator
import br.gov.caixa.loterias.feature.tickets.presentation.coordinator.TicketsHistoryCoordinator
import br.gov.caixa.loterias.feature.tickets.presentation.coordinator.TicketsManualQueryCoordinator
import br.gov.caixa.loterias.feature.tickets.presentation.coordinator.TicketsPixCoordinator
import br.gov.caixa.loterias.feature.tickets.presentation.coordinator.TicketsPixDeadlineTracker
import br.gov.caixa.loterias.feature.tickets.presentation.coordinator.TicketsPurchaseCoordinator
import br.gov.caixa.loterias.feature.tickets.presentation.coordinator.TicketsPurchaseFilterSources
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.TicketsPixNoticeUiState
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import br.gov.caixa.loterias.feature.tickets.presentation.state.reduceTicketsState

class TicketsViewModel(
    private val getConfirmedBets: GetConfirmedBetsUseCase,
    private val getPurchases: GetPurchasesUseCase,
    private val getPurchaseBets: GetPurchaseBetsUseCase,
    private val checkTicket: CheckTicketUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val getBetsConfiguration: GetConfirmedBetsConfigurationUseCase =
        GetConfirmedBetsConfigurationUseCase { AppResult.Success(ConfirmedBetsConfiguration()) },
    private val getModalities: GetLotteryModalitiesUseCase = GetLotteryModalitiesUseCase { AppResult.Success(emptyList()) },
    private val getPurchaseMonths: GetPurchaseMonthsUseCase = GetPurchaseMonthsUseCase { AppResult.Success(emptyList()) },
    private val getPaymentMethods: GetPurchasePaymentMethodsUseCase =
        GetPurchasePaymentMethodsUseCase { AppResult.Success(emptyList()) },
    private val getPurchaseStatuses: GetPurchaseStatusesUseCase =
        GetPurchaseStatusesUseCase { AppResult.Success(emptyList()) },
    private val getPurchaseDetails: GetPurchaseDetailsUseCase? = null,
    private val validateFavoriteName: ValidateFavoriteCartNameUseCase? = null,
    private val savePurchaseAsFavorite: SavePurchaseAsFavoriteUseCase? = null,
    private val validateDam: ValidateDamUseCase? = null,
    private val getPixPurchaseStatus: br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase? = null,
    private val pixNoticeClock: java.time.Clock = java.time.Clock.systemDefaultZone(),
    private val elapsedMillis: () -> Long = { br.gov.caixa.loterias.core.presentation.time.elapsedRealtimeMillis() },

    private val isSessionCurrent: () -> Boolean = { true },
) : ViewModel() {
    private val mutableState = reduxStateStore<TicketsUiState, TicketsStateAction>(
        TicketsUiState(),
        ::reduceTicketsState
    ).also { addCloseable(it) }
    val state = mutableState.state

    private val history = TicketsHistoryCoordinator(
        mutableState, viewModelScope, dispatchers, getConfirmedBets, getPurchases,
        getBetsConfiguration, getModalities, getPurchaseMonths,
        TicketsPurchaseFilterSources(getPaymentMethods, getPurchaseStatuses), isSessionCurrent, ::fail
    )
    private val pixDeadline = TicketsPixDeadlineTracker(pixNoticeClock, elapsedMillis)
    private val purchases = TicketsPurchaseCoordinator(
        mutableState, viewModelScope, dispatchers, getPurchaseBets, getPurchaseDetails,
        validateFavoriteName, savePurchaseAsFavorite, validateDam,
        pixDeadline::capture, ::fail
    )
    private val pix = TicketsPixCoordinator(
        mutableState,
        viewModelScope,
        dispatchers,
        getPixPurchaseStatus,
        getPurchaseDetails,
        pixNoticeClock,
        pixDeadline,
        purchases::showPurchaseDetails
    )

    private val manualQuery = TicketsManualQueryCoordinator(mutableState, viewModelScope, dispatchers, ::fail, checkTicket)
    private val modalities = TicketModalitiesCoordinator(mutableState, viewModelScope, dispatchers, ::fail, getModalities)

    fun loadBets() = history.loadBets()

    fun loadTicketModalities() = modalities.loadTicketModalities()

    fun toggleTicketModality(id: Int) = modalities.toggleTicketModality(id)

    fun openBetFilters() = history.openBetFilters()

    fun closeBetFilters() = history.closeBetFilters()

    fun selectBetPeriod(value: TicketPeriod) = history.selectBetPeriod(value)

    fun selectBetStatus(value: Long) = history.selectBetStatus(value)

    fun selectBetOrdering(value: Long) = history.selectBetOrdering(value)

    fun selectBetType(value: Long) = history.selectBetType(value)

    fun selectContestType(value: Long) = history.selectContestType(value)

    fun selectModality(value: Int) = history.selectModality(value)

    fun toggleSurprise() = history.toggleSurprise()

    fun toggleRecurring() = history.toggleRecurring()

    fun toggleCombo() = history.toggleCombo()

    fun clearBetFilters() = history.clearBetFilters()

    fun applyBetFilters() = history.applyBetFilters()

    fun loadPurchases() = history.loadPurchases()

    fun selectPurchaseMonth(month: PurchaseMonth) = history.selectPurchaseMonth(month)

    fun openPurchaseFilters() = history.openPurchaseFilters()

    fun closePurchaseFilters() = history.closePurchaseFilters()

    fun selectPaymentMethod(id: Long?) = history.selectPaymentMethod(id)

    fun selectPurchaseStatus(id: Long?) = history.selectPurchaseStatus(id)

    fun clearPurchaseFilters() = history.clearPurchaseFilters()

    fun applyPurchaseFilters() = history.applyPurchaseFilters()

    fun loadMorePurchases() = history.loadMorePurchases()

    fun setPixScreenActive(active: Boolean) = pix.setScreenActive(active)

    fun checkPendingPixStatus() = pix.checkPendingPixStatus()

    fun consumePixPurchaseRedirect(expected: br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect): Boolean =
        pix.consumePixPurchaseRedirect(expected)

    fun consumePixPurchaseNotice(expected: TicketsPixNoticeUiState, pause: Boolean = false): Boolean =
        pix.consumePixPurchaseNotice(expected, pause)

    fun resumePixPurchaseAfterRedirect() = pix.resumePixPurchaseAfterRedirect()

    fun loadPurchase(purchase: Purchase) = purchases.loadPurchase(purchase)

    fun openPurchaseBetFilters() = purchases.openPurchaseBetFilters()

    fun closePurchaseBetFilters() = purchases.closePurchaseBetFilters()

    fun selectPurchaseBetContest(value: Int?) = purchases.selectPurchaseBetContest(value)

    fun selectPurchaseBetStatus(value: Long?) = purchases.selectPurchaseBetStatus(value)

    fun selectPurchaseBetModality(value: Int?) = purchases.selectPurchaseBetModality(value)

    fun clearPurchaseBetFilters() = purchases.clearPurchaseBetFilters()

    fun applyPurchaseBetFilters() = purchases.applyPurchaseBetFilters()

    fun dismissPurchaseStatusDialog() = purchases.dismissPurchaseStatusDialog()

    fun openFavoriteDialog() = purchases.openFavoriteDialog()

    fun closeFavoriteDialog() = purchases.closeFavoriteDialog()

    fun changeFavoriteName(value: String) = purchases.changeFavoriteName(value)

    fun toggleKeepSurprises() = purchases.toggleKeepSurprises()

    fun validateAndSaveFavorite() = purchases.validateAndSaveFavorite()

    fun confirmFavoriteOverwrite() = purchases.confirmFavoriteOverwrite()

    fun cancelFavoriteOverwrite() = purchases.cancelFavoriteOverwrite()

    fun dismissFavoriteSaved() = purchases.dismissFavoriteSaved()

    fun dismissDamMessage() = purchases.dismissDamMessage()

    fun changeTicketCode(value: String) = manualQuery.changeTicketCode(value)

    fun changeTicketRetailerCode(value: String) = manualQuery.changeTicketRetailerCode(value)

    fun submitScannedTicketCode(value: String, orientation: TicketScanOrientation = TicketScanOrientation.PORTRAIT) =
        manualQuery.submitScannedTicketCode(value, orientation)

    fun consumeTicketResultNavigation(): Boolean = manualQuery.consumeTicketResultNavigation()

    fun checkTicket(qrCode: Boolean = false) = manualQuery.checkTicket(qrCode)

    fun retryBets() = history.retryBets()

    fun retryPurchases() = history.retryPurchases()

    fun dismissTicketError() = manualQuery.dismissTicketError()

    fun pendingPixSecondsRemaining(): Long = pix.pendingPixSecondsRemaining()

    private fun fail(message: String) {
        mutableState.dispatch(TicketsStateAction.OperationFailed(message))
    }

    companion object {
        const val MINIMUM_MANUAL_TICKET_LENGTH = 21
        private const val MINIMUM_SCANNED_TICKET_LENGTH = 23
    }
}
