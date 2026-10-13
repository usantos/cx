package br.gov.caixa.loterias.feature.retail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.NetworkAvailability
import br.gov.caixa.loterias.core.presentation.connectionFailureNotice
import br.gov.caixa.loterias.core.presentation.loginConnectivityNotice
import br.gov.caixa.loterias.core.domain.retail.CancelPixRedemptionUseCase
import br.gov.caixa.loterias.core.domain.retail.ConsultPixRedemptionKeyUseCase
import br.gov.caixa.loterias.core.domain.retail.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.core.domain.retail.DownloadPrizeReceiptUseCase
import br.gov.caixa.loterias.core.domain.retail.GenerateLotteryRedemptionCodeUseCase
import br.gov.caixa.loterias.core.domain.retail.GetPrizeDetailsUseCase
import br.gov.caixa.loterias.core.domain.retail.LotteryRedemptionCode
import br.gov.caixa.loterias.core.domain.retail.PixBeneficiary
import br.gov.caixa.loterias.core.domain.retail.PrizeDetails
import br.gov.caixa.loterias.core.domain.retail.PrizeRedemptionChannel
import br.gov.caixa.loterias.core.domain.retail.PrizeRedemptionResult
import br.gov.caixa.loterias.core.domain.retail.PrizeRedirect
import java.time.Clock
import java.time.LocalDateTime
import br.gov.caixa.loterias.core.domain.retail.RedeemPrizeUseCase
import br.gov.caixa.loterias.core.domain.betting.BetEntryDecision
import br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase
import br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters
import br.gov.caixa.loterias.core.domain.betting.PrepareBetEntryUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PrizeRedemptionStage { LOADING, OPTIONS, LOTTERY, AGENCY, PIX_ENTRY, PIX_CONFIRM, MERCADO_PAGO, SUCCESS }
enum class PrizePixKeySource { CPF, OTHER }

data class PrizeRegistrationNotice(val message: String, val email: String) {
    override fun toString(): String = "PrizeRegistrationNotice(redacted)"
}

data class PrizeRedemptionUiState(
    val stage: PrizeRedemptionStage = PrizeRedemptionStage.LOADING,
    val details: PrizeDetails? = null,
    val selectedChannel: PrizeRedemptionChannel? = null,
    val lotteryCode: LotteryRedemptionCode? = null,
    val remainingSeconds: Int = 0,
    val cpf: String = "",
    val pixKeySource: PrizePixKeySource = PrizePixKeySource.CPF,
    val pixKey: String = "",
    val pixOwnershipAccepted: Boolean = false,
    val pixBeneficiary: PixBeneficiary? = null,
    val email: String = "",
    val loading: Boolean = true,
    val errorMessage: String? = null,
    val successNoticeMessage: String? = null,
    val currentContest: LotteryGameParameters? = null,
    val currentContestLoading: Boolean = false,
    val currentContestError: String? = null,
    val betEntryDecision: BetEntryDecision? = null,
    val registrationNotice: PrizeRegistrationNotice? = null,
    val registrationPending: Boolean = false,
    val notice: PrizeNoticeUiState? = null,
    val redirect: PrizeRedirect? = null,
    val redirectPaused: Boolean = false,
)

data class PrizeReceipt(val bytes: ByteArray, val fileName: String)

class PrizeRedemptionViewModel(
    private val betId: Long,
    initialEmail: String,
    initialCpf: String,
    private val getDetails: GetPrizeDetailsUseCase,
    private val generateLotteryCode: GenerateLotteryRedemptionCodeUseCase,
    private val consultPixKey: ConsultPixRedemptionKeyUseCase,
    private val redeemPrize: RedeemPrizeUseCase,
    private val cancelPix: CancelPixRedemptionUseCase,
    private val downloadBetReceiptUseCase: DownloadBetReceiptUseCase,
    private val downloadPrizeReceiptUseCase: DownloadPrizeReceiptUseCase,
    private val getBettingParameters: GetBettingParametersUseCase,
    private val prepareBetEntry: PrepareBetEntryUseCase,
    private val localClock: Clock,
    private val networkAvailability: NetworkAvailability,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = MutableStateFlow(
        PrizeRedemptionUiState(
            email = initialEmail,
            cpf = initialCpf,
            pixKeySource = if (initialCpf.isBlank()) PrizePixKeySource.OTHER else PrizePixKeySource.CPF,
        ),
    )
    val state = mutableState.asStateFlow()
    private val mutableReceipts = MutableSharedFlow<PrizeReceipt>(extraBufferCapacity = 1)
    val receipts = mutableReceipts.asSharedFlow()
    private var timer: Job? = null

    init { load() }

    fun load() = viewModelScope.launch(dispatchers.io) {
        if (interactionBlocked()) return@launch
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        when (val result = getDetails(betId)) {
            is AppResult.Success -> {
                mutableState.update { it.copy(stage = PrizeRedemptionStage.OPTIONS, details = result.value, loading = false) }
                loadCurrentContest()
            }
            is AppResult.Failure -> fail(result.error)
        }
    }

    fun loadCurrentContest() {
        val modalityId = state.value.details?.modalityId ?: return
        if (state.value.currentContestLoading) return
        mutableState.update { it.copy(currentContestLoading = true, currentContestError = null) }
        viewModelScope.launch(dispatchers.io) {
            when (val result = getBettingParameters()) {
                is AppResult.Success -> mutableState.update {
                    it.copy(currentContestLoading = false,
                        currentContest = result.value.games.firstOrNull { game -> game.modality.id == modalityId }?.copy(
                            hasNextContest = result.value.games.any { game -> game.modality.id == modalityId && game.hasNextContest },
                        ))
                }
                is AppResult.Failure -> mutableState.update {
                    it.copy(currentContestLoading = false, currentContestError = "Não foi possível consultar o concurso atual.")
                }
            }
        }
    }

    fun betNow() {
        val modalityId = state.value.details?.modalityId ?: return
        if (state.value.loading || state.value.betEntryDecision != null || interactionBlocked()) return
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        viewModelScope.launch(dispatchers.io) {
            when (val result = prepareBetEntry(modalityId)) {
                is AppResult.Success -> mutableState.update { it.copy(loading = false, betEntryDecision = result.value) }
                is AppResult.Failure -> fail(result.error)
            }
        }
    }

    fun consumeBetEntryDecision() = mutableState.update { it.copy(betEntryDecision = null) }

    fun select(channel: PrizeRedemptionChannel) {
        if (state.value.loading || interactionBlocked() || state.value.details?.options?.none { it.channel == channel } != false) return
        mutableState.update { it.copy(selectedChannel = channel, errorMessage = null) }
        when (channel) {
            PrizeRedemptionChannel.LOTTERY -> refreshLotteryCode()
            PrizeRedemptionChannel.AGENCY -> openAgencyGuidance()
            PrizeRedemptionChannel.PIX -> mutableState.update { it.copy(stage = PrizeRedemptionStage.PIX_ENTRY) }
            PrizeRedemptionChannel.MERCADO_PAGO -> mutableState.update { it.copy(stage = PrizeRedemptionStage.MERCADO_PAGO) }
        }
    }

    private fun openAgencyGuidance() {
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        viewModelScope.launch(dispatchers.io) {
            when (val result = generateLotteryCode(betId)) {
                is AppResult.Success -> mutableState.update {
                    it.copy(stage = PrizeRedemptionStage.AGENCY, lotteryCode = result.value, loading = false)
                }
                is AppResult.Failure -> fail(result.error)
            }
        }
    }

    /** Returns true when the navigation host should leave the flow. */
    fun back(): Boolean {
        if (interactionBlocked() || (state.value.loading && state.value.stage == PrizeRedemptionStage.MERCADO_PAGO)) return false
        val stage = state.value.stage
        return when {
            stage != PrizeRedemptionStage.OPTIONS && stage != PrizeRedemptionStage.LOADING -> {
                returnToOptions()
                false
            }
            else -> true
        }
    }

    fun selectCpfPixKey() = mutableState.update {
        it.copy(pixKeySource = PrizePixKeySource.CPF, errorMessage = null)
    }

    fun selectOtherPixKey() = mutableState.update {
        it.copy(pixKeySource = PrizePixKeySource.OTHER, errorMessage = null)
    }

    fun changePixKey(value: String) = mutableState.update {
        it.copy(pixKey = value.take(120), errorMessage = null)
    }

    fun togglePixOwnership() = mutableState.update {
        it.copy(pixOwnershipAccepted = !it.pixOwnershipAccepted, errorMessage = null)
    }
    fun changeEmail(value: String) {
        if (state.value.loading || interactionBlocked()) return
        mutableState.update { it.copy(email = value.take(120), errorMessage = null) }
    }

    fun consultPix() = viewModelScope.launch(dispatchers.io) {
        if (interactionBlocked()) return@launch
        val key = state.value.selectedPixKey()
        if (!state.value.canContinuePix()) {
            mutableState.update { it.copy(errorMessage = "Chave inválida!") }
            return@launch
        }
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        when (val result = consultPixKey(betId, key)) {
            is AppResult.Success -> mutableState.update { it.copy(stage = PrizeRedemptionStage.PIX_CONFIRM, pixBeneficiary = result.value, loading = false) }
            is AppResult.Failure -> fail(result.error)
        }
    }

    fun confirmPix() = submit(
        PrizeRedemptionChannel.PIX,
        state.value.pixBeneficiary?.key?.ifBlank { state.value.selectedPixKey() }
            ?: state.value.selectedPixKey(),
    )

    fun cancelPixAndExit() = viewModelScope.launch(dispatchers.io) {
        if (interactionBlocked()) return@launch
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        when (val result = cancelPix(betId)) {
            is AppResult.Success -> returnToOptions()
            is AppResult.Failure -> fail(result.error)
        }
    }

    fun confirmMercadoPago() {
        if (state.value.loading || interactionBlocked()) return
        if (!isValidPrizeEmail(state.value.email)) {
            mutableState.update { it.copy(errorMessage = "E-mail inválido.") }
            return
        }
        submit(PrizeRedemptionChannel.MERCADO_PAGO, state.value.email)
    }

    fun refreshLotteryCode() {
        if (interactionBlocked()) return
        timer?.cancel()
        viewModelScope.launch(dispatchers.io) {
            mutableState.update { it.copy(stage = PrizeRedemptionStage.LOTTERY, loading = true, errorMessage = null, lotteryCode = null) }
            when (val result = generateLotteryCode(betId)) {
                is AppResult.Success -> {
                    // Agency guidance uses the same service, but only the lottery screen
                    // requires QR content. Do not reject an agency response for lacking it.
                    if (result.value.qrCode.isBlank()) {
                        fail(AppError.Validation("Código de resgate indisponível"))
                        return@launch
                    }
                    val seconds = result.value.validityMinutes * 60
                    mutableState.update { it.copy(lotteryCode = result.value, remainingSeconds = seconds, loading = false) }
                    startTimer(seconds)
                }
                is AppResult.Failure -> fail(result.error)
            }
        }
    }

    fun downloadBetReceipt() = download("comprovante-aposta-$betId.pdf") { downloadBetReceiptUseCase(it) }
    fun downloadPrizeReceipt() = download("comprovante-premio-$betId.pdf") { downloadPrizeReceiptUseCase(it) }
    fun dismissError() = mutableState.update { it.copy(errorMessage = null) }
    fun dismissSuccessNotice() = mutableState.update { it.copy(successNoticeMessage = null) }

    private fun interactionBlocked() = state.value.registrationNotice != null || state.value.registrationPending ||
        state.value.notice != null || state.value.redirect != null || state.value.redirectPaused

    fun dismissNotice(expected: PrizeNoticeUiState): Boolean {
        if (state.value.notice !== expected) return false
        mutableState.update { it.copy(notice = null) }
        return true
    }

    fun consumeNoticeAction(expected: PrizeNoticeUiState): PrizeRedirect? {
        if (state.value.notice !== expected) return null
        val destination = expected.destination ?: return null
        if (blockOfflineLogin(destination)) return null
        mutableState.update { it.copy(notice = null, redirectPaused = true) }
        return destination
    }

    fun consumeRedirect(expected: PrizeRedirect): Boolean {
        if (state.value.redirect != expected) return false
        if (blockOfflineLogin(expected)) return false
        mutableState.update { it.copy(redirect = null, redirectPaused = true) }
        return true
    }

    fun resumeAfterRedirect() = mutableState.update { it.copy(redirectPaused = false) }

    private fun blockOfflineLogin(destination: PrizeRedirect): Boolean {
        val notice = loginConnectivityNotice(destination, networkAvailability) ?: return false
        mutableState.update { it.copy(notice = notice, redirect = null, redirectPaused = false) }
        return true
    }

    fun dismissRegistrationNotice(expected: PrizeRegistrationNotice): Boolean {
        if (state.value.registrationNotice !== expected) return false
        mutableState.update { it.copy(registrationNotice = null) }
        return true
    }

    fun consumeRegistrationNotice(expected: PrizeRegistrationNotice): String? {
        if (state.value.registrationNotice !== expected) return null
        mutableState.update { it.copy(registrationNotice = null, registrationPending = true) }
        return expected.email
    }

    fun registrationFinished() = mutableState.update { it.copy(registrationPending = false) }

    private fun submit(channel: PrizeRedemptionChannel, value: String) {
        if (state.value.loading || interactionBlocked() || state.value.stage == PrizeRedemptionStage.SUCCESS) return
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        viewModelScope.launch(dispatchers.io) {
            when (val result = redeemPrize(betId, channel, value)) {
                is AppResult.Success -> when (val outcome = result.value) {
                    is PrizeRedemptionResult.ConnectionFailed -> mutableState.update {
                        it.copy(loading = false, notice = connectionFailureNotice(outcome.reason, networkAvailability.current()))
                    }
                    PrizeRedemptionResult.Unchanged -> mutableState.update { it.copy(loading = false) }
                    is PrizeRedemptionResult.Redeemed -> mutableState.update {
                        it.copy(stage = PrizeRedemptionStage.SUCCESS, selectedChannel = channel, loading = false,
                            successNoticeMessage = outcome.noticeCode.toLegacyNoticeMessage())
                    }
                    is PrizeRedemptionResult.RegistrationRequired -> {
                        if (channel != PrizeRedemptionChannel.MERCADO_PAGO) fail(AppError.Unexpected)
                        else mutableState.update {
                            it.copy(loading = false, registrationNotice = PrizeRegistrationNotice(outcome.message, value))
                        }
                    }
                    is PrizeRedemptionResult.NoticeRequired -> mutableState.update {
                        it.copy(loading = false, notice = prizeNoticeUi(outcome.notice, LocalDateTime.now(localClock)))
                    }
                    is PrizeRedemptionResult.RedirectRequired -> mutableState.update {
                        it.copy(loading = false, redirect = outcome.destination)
                    }
                }
                is AppResult.Failure -> fail(result.error)
            }
        }
    }

    private fun returnToOptions() {
        timer?.cancel()
        mutableState.update {
            it.copy(
                stage = PrizeRedemptionStage.OPTIONS,
                selectedChannel = null,
                pixBeneficiary = null,
                loading = false,
                errorMessage = null,
            )
        }
    }

    private fun startTimer(seconds: Int) {
        timer = viewModelScope.launch(dispatchers.io) {
            var remaining = seconds
            while (remaining > 0) {
                delay(1_000)
                remaining--
                mutableState.update { it.copy(remainingSeconds = remaining) }
            }
        }
    }

    private fun download(fileName: String, useCase: suspend (Long) -> AppResult<ByteArray>) = viewModelScope.launch(dispatchers.io) {
        if (interactionBlocked()) return@launch
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        when (val result = useCase(betId)) {
            is AppResult.Success -> {
                mutableReceipts.emit(PrizeReceipt(result.value, fileName))
                mutableState.update { it.copy(loading = false) }
            }
            is AppResult.Failure -> fail(result.error)
        }
    }

    private fun fail(error: AppError) = mutableState.update {
        it.copy(
            loading = false,
            errorMessage = when (error) {
                AppError.Network -> "Não foi possível acessar o serviço. Verifique sua conexão."
                is AppError.Validation -> error.reason.ifBlank { "Não foi possível concluir a operação." }
                AppError.Unexpected -> "Não foi possível concluir a operação. Tente novamente."
            },
        )
    }
}

private fun String?.toLegacyNoticeMessage(): String? = when (this?.uppercase()) {
    "MI053", "MIO53" -> "Prêmio resgatado com sucesso! Sua aposta possui Teimosinha e continuará participando dos próximos concursos. Foi gerado um recibo de troca válido para os próximos concursos, e este já está disponível em 'Minhas Apostas'."
    else -> null
}
