package br.gov.caixa.loterias.feature.retail.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.event.SingleConsumerEventQueue
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntryUseCase
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.domain.usecase.CancelPixRedemptionUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.ConsultPixRedemptionKeyUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GenerateLotteryRedemptionCodeUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetPrizeDetailsUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.RedeemPrizeUseCase
import br.gov.caixa.loterias.feature.retail.presentation.coordinator.PrizeLotteryChannel
import br.gov.caixa.loterias.feature.retail.presentation.coordinator.PrizeMercadoPagoChannel
import br.gov.caixa.loterias.feature.retail.presentation.coordinator.PrizePixChannel
import br.gov.caixa.loterias.feature.retail.presentation.coordinator.PrizeReceiptDelivery
import br.gov.caixa.loterias.feature.retail.presentation.coordinator.PrizeRedemptionCoordinator
import br.gov.caixa.loterias.feature.retail.presentation.coordinator.PrizeRedemptionNotices
import br.gov.caixa.loterias.feature.retail.presentation.coordinator.PrizeRedemptionReceipts
import br.gov.caixa.loterias.feature.retail.presentation.coordinator.PrizeRedemptionRedirects
import br.gov.caixa.loterias.feature.retail.presentation.coordinator.PrizeRedemptionSubmission
import br.gov.caixa.loterias.feature.retail.presentation.coordinator.PrizeRedemptionTimer
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizePixKeySource
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeReceipt
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStateAction
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRegistrationNotice
import br.gov.caixa.loterias.feature.retail.presentation.state.reducePrizeRedemptionState
import java.time.Clock

class PrizeRedemptionViewModel(
    input: PrizeRedemptionInput,
    private val getDetails: GetPrizeDetailsUseCase,
    private val generateLotteryCode: GenerateLotteryRedemptionCodeUseCase,
    private val consultPixKey: ConsultPixRedemptionKeyUseCase,
    private val redeemPrize: RedeemPrizeUseCase,
    private val cancelPix: CancelPixRedemptionUseCase,
    receiptUseCases: PrizeReceiptUseCases,
    private val getBettingParameters: GetBettingParametersUseCase,
    private val prepareBetEntry: PrepareBetEntryUseCase,
    private val localClock: Clock,
    private val networkAvailability: NetworkAvailability,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val betId = input.betId
    private val mutableState = reduxStateStore<PrizeRedemptionUiState, PrizeRedemptionStateAction>(
        PrizeRedemptionUiState(
            email = input.email,
            cpf = input.cpf,
            pixKeySource = if (input.cpf.isBlank()) PrizePixKeySource.OTHER else PrizePixKeySource.CPF
        ),
        ::reducePrizeRedemptionState
    ).also { addCloseable(it) }
    val state = mutableState.state
    internal val receiptDelivery = PrizeReceiptDelivery(viewModelScope).also { addCloseable(it) }
    private val mutableReceipts = SingleConsumerEventQueue<PrizeReceipt>()
    val receipts = mutableReceipts.events
    private val redirects = PrizeRedemptionRedirects(mutableState, networkAvailability)
    private val notices = PrizeRedemptionNotices(mutableState, redirects::blockOfflineLogin)
    private val timer = PrizeRedemptionTimer(mutableState, viewModelScope, dispatchers)
    private val submission = PrizeRedemptionSubmission(
        betId, mutableState, viewModelScope, dispatchers, redeemPrize, localClock,
        networkAvailability, notices::interactionBlocked, ::fail
    )
    private val lottery = PrizeLotteryChannel(
        betId,
        mutableState,
        viewModelScope,
        dispatchers,
        generateLotteryCode,
        timer,
        notices::interactionBlocked,
        ::fail
    )
    private val pix = PrizePixChannel(
        betId, mutableState, viewModelScope, dispatchers, consultPixKey, cancelPix,
        submission::submit, ::returnToOptions, notices::interactionBlocked, ::fail
    )
    private val mercadoPago = PrizeMercadoPagoChannel(mutableState, notices::interactionBlocked, submission::submit)
    private val receiptDownloads = PrizeRedemptionReceipts(
        betId, mutableState, viewModelScope, dispatchers, mutableReceipts,
        receiptUseCases.downloadBetReceipt, receiptUseCases.downloadPrizeReceipt, notices::interactionBlocked, ::fail
    )
    private val coordinator = PrizeRedemptionCoordinator(
        betId, mutableState, viewModelScope, dispatchers, getDetails,
        getBettingParameters, prepareBetEntry, notices::interactionBlocked, ::fail
    )

    init {
        load()
    }

    fun load() = coordinator.load()

    fun loadCurrentContest() = coordinator.loadCurrentContest()

    fun betNow() = coordinator.betNow()

    fun consumeBetEntryDecision() = mutableState.dispatch(PrizeRedemptionStateAction.BetEntryConsumed)

    fun select(channel: PrizeRedemptionChannel) {
        if (state.value.loading || notices.interactionBlocked() ||
            state.value.details?.options?.none { it.channel == channel } != false
        ) {
            return
        }
        mutableState.dispatch(PrizeRedemptionStateAction.ChannelSelected(channel))
        when (channel) {
            PrizeRedemptionChannel.LOTTERY -> refreshLotteryCode()
            PrizeRedemptionChannel.AGENCY -> lottery.openAgencyGuidance()
            PrizeRedemptionChannel.PIX -> mutableState.dispatch(PrizeRedemptionStateAction.PixEntryOpened)
            PrizeRedemptionChannel.MERCADO_PAGO -> mutableState.dispatch(PrizeRedemptionStateAction.MercadoPagoOpened)
        }
    }

    /** Retorna true quando o hospedeiro de navegação deve sair do fluxo. */
    fun back(): Boolean {
        if (notices.interactionBlocked() || (state.value.loading && state.value.stage == PrizeRedemptionStage.MERCADO_PAGO)) return false
        val stage = state.value.stage
        return when {
            stage != PrizeRedemptionStage.OPTIONS && stage != PrizeRedemptionStage.LOADING -> {
                returnToOptions()
                false
            }

            else -> true
        }
    }

    fun selectCpfPixKey() = pix.selectCpfPixKey()

    fun selectOtherPixKey() = pix.selectOtherPixKey()

    fun changePixKey(value: String) = pix.changePixKey(value)

    fun togglePixOwnership() = pix.togglePixOwnership()

    fun changeEmail(value: String) = mercadoPago.changeEmail(value)

    fun consultPix() = pix.consultPix()

    fun confirmPix() = pix.confirmPix()

    fun cancelPixAndExit() = pix.cancelPixAndExit()

    fun confirmMercadoPago() = mercadoPago.confirmMercadoPago()

    fun refreshLotteryCode() = lottery.refreshLotteryCode()

    fun downloadBetReceipt() = receiptDownloads.downloadBetReceipt()

    fun downloadPrizeReceipt() = receiptDownloads.downloadPrizeReceipt()

    internal fun recoverReceipt(fileName: String): Boolean = receiptDownloads.recoverReceipt(fileName, receiptDelivery::recoveryFailed)

    fun dismissError() = mutableState.dispatch(PrizeRedemptionStateAction.ErrorDismissed)

    fun dismissSuccessNotice() = mutableState.dispatch(PrizeRedemptionStateAction.SuccessNoticeDismissed)

    fun dismissNotice(expected: ServiceNoticeUiState): Boolean = notices.dismissNotice(expected)

    fun consumeNoticeAction(expected: ServiceNoticeUiState): ServiceRedirect? = notices.consumeNoticeAction(expected)

    fun consumeRedirect(expected: ServiceRedirect): Boolean = redirects.consumeRedirect(expected)

    fun resumeAfterRedirect() = redirects.resumeAfterRedirect()

    fun dismissRegistrationNotice(expected: PrizeRegistrationNotice): Boolean = notices.dismissRegistrationNotice(expected)

    fun consumeRegistrationNotice(expected: PrizeRegistrationNotice): String? = notices.consumeRegistrationNotice(expected)

    fun registrationFinished() = notices.registrationFinished()

    private fun returnToOptions() {
        timer.cancel()
        mutableState.dispatch(PrizeRedemptionStateAction.ReturnedToOptions)
    }

    private fun fail(error: AppError) = mutableState.dispatch(PrizeRedemptionStateAction.OperationFailed(error))
}
