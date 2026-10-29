package br.gov.caixa.loterias.feature.tickets.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.event.SingleConsumerEventQueue
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.usecase.RecoverConfirmedBetUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.coordinator.BetDetailDetailsCoordinator
import br.gov.caixa.loterias.feature.tickets.presentation.coordinator.BetDetailFavoriteCoordinator
import br.gov.caixa.loterias.feature.tickets.presentation.coordinator.BetDetailReceiptCoordinator
import br.gov.caixa.loterias.feature.tickets.presentation.coordinator.BetDetailRepeatCoordinator
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailUiState
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetReceipt
import br.gov.caixa.loterias.feature.tickets.presentation.state.reduceBetDetailState

class BetDetailViewModel(
    bet: ConfirmedBet,
    private val recoverBet: RecoverConfirmedBetUseCase,
    private val downloadBetReceipt: DownloadBetReceiptUseCase,
    private val validateFavoriteBet: ValidateFavoriteBetUseCase,
    private val saveFavoriteBet: SaveFavoriteBetUseCase,
    private val addBetToCart: AddBetToCartUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val getBettingParameters: GetBettingParametersUseCase? = null,
    private val loadConfirmedBet: br.gov.caixa.loterias.feature.tickets.domain.usecase.LoadConfirmedBetUseCase? = null,
    private val downloadPrizeReceipt: br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadPrizeReceiptUseCase? = null,
    private val getConfirmedBetResults: br.gov.caixa.loterias.feature.results.domain.usecase.GetConfirmedBetResultsUseCase? = null
) : ViewModel() {
    private val mutableState = reduxStateStore<BetDetailUiState, BetDetailStateAction>(
        BetDetailUiState(bet),
        ::reduceBetDetailState
    ).also { addCloseable(it) }
    val state = mutableState.state
    private val mutableReceipts = SingleConsumerEventQueue<BetReceipt>()
    val receipts = mutableReceipts.events
    private val details = BetDetailDetailsCoordinator(
        mutableState,
        viewModelScope,
        dispatchers,
        recoverBet,
        loadConfirmedBet,
        getConfirmedBetResults
    )
    private val receipt = BetDetailReceiptCoordinator(
        mutableState,
        viewModelScope,
        dispatchers,
        mutableReceipts,
        downloadBetReceipt,
        downloadPrizeReceipt
    )
    private val favorites = BetDetailFavoriteCoordinator(mutableState, viewModelScope, dispatchers, validateFavoriteBet, saveFavoriteBet)
    private val repeat = BetDetailRepeatCoordinator(mutableState, viewModelScope, dispatchers, addBetToCart, getBettingParameters)

    init {
        if (bet.id > 0) {
            mutableState.dispatch(BetDetailStateAction.InitialDetailsLoading)
            loadDetails()
        }
    }

    fun loadDetails() = details.loadDetails()

    fun dismissServiceFailure() = details.dismissServiceFailure()

    fun downloadReceipt() = receipt.downloadReceipt()

    fun dismissError() = mutableState.dispatch(BetDetailStateAction.ErrorDismissed)

    fun reportReceiptOpenFailure() = receipt.reportReceiptOpenFailure()

    fun dismissCartAddition() = repeat.dismissCartAddition()

    fun toggleFavoriteEditor() = favorites.toggleFavoriteEditor()

    fun changeFavoriteName(value: String) = favorites.changeFavoriteName(value)

    fun submitFavorite() = favorites.submitFavorite()

    fun confirmFavoriteOverwrite() = favorites.confirmFavoriteOverwrite()

    fun dismissFavoriteOverwrite() = favorites.dismissFavoriteOverwrite()

    fun addCurrentBetToCart() = repeat.addCurrentBetToCart()

    fun selectRepeatContest(index: Int) = repeat.selectRepeatContest(index)

    fun confirmRepeatContest() = repeat.confirmRepeatContest()

    fun dismissRepeatContest() = repeat.dismissRepeatContest()

    fun confirmRepeatConversion() = repeat.confirmRepeatConversion()
}
