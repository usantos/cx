package br.gov.caixa.loterias.feature.tickets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet
import br.gov.caixa.loterias.core.domain.tickets.RecoverConfirmedBetUseCase
import br.gov.caixa.loterias.core.domain.retail.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.core.domain.betting.FavoriteBetDraft
import br.gov.caixa.loterias.core.domain.betting.AddBetToCartUseCase
import br.gov.caixa.loterias.core.domain.betting.BetDraft
import br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase
import br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters
import br.gov.caixa.loterias.core.domain.betting.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.core.domain.betting.ValidateFavoriteBetUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BetDetailUiState(
    val bet: ConfirmedBet,
    val loading: Boolean = false,
    val detailsReady: Boolean = true,
    val serviceFailure: br.gov.caixa.loterias.core.domain.service.ServiceFailure? = null,
    val errorMessage: String? = null,
    val favoriteExpanded: Boolean = false,
    val favoriteName: String = "",
    val favoriteSaved: Boolean = false,
    val favoriteOverwriteConfirmation: Boolean = false,
    val repeatContestOptions: List<RepeatContestOption> = emptyList(),
    val selectedRepeatContestIndex: Int = 0,
    val repeatConversion: RepeatContestOption? = null,
    val cartAdditionConfirmed: Boolean = false,
)

data class RepeatContestOption(
    val contestType: String,
    val label: String,
    val conversionMessage: String = "",
)

data class BetReceipt(val bytes: ByteArray, val fileName: String)

class BetDetailViewModel(
    bet: ConfirmedBet,
    private val recoverBet: RecoverConfirmedBetUseCase,
    private val downloadBetReceipt: DownloadBetReceiptUseCase,
    private val validateFavoriteBet: ValidateFavoriteBetUseCase,
    private val saveFavoriteBet: SaveFavoriteBetUseCase,
    private val addBetToCart: AddBetToCartUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val getBettingParameters: GetBettingParametersUseCase? = null,
    private val loadConfirmedBet: br.gov.caixa.loterias.core.domain.tickets.LoadConfirmedBetUseCase? = null,
    private val downloadPrizeReceipt: br.gov.caixa.loterias.core.domain.retail.DownloadPrizeReceiptUseCase? = null,
) : ViewModel() {
    private val mutableState = MutableStateFlow(BetDetailUiState(bet))
    val state = mutableState.asStateFlow()
    private var pendingFavoriteDraft: FavoriteBetDraft? = null
    private val mutableReceipts = MutableSharedFlow<BetReceipt>(extraBufferCapacity = 1)
    val receipts = mutableReceipts.asSharedFlow()

    init {
        if (bet.id > 0) {
            mutableState.value = mutableState.value.copy(loading = true, detailsReady = false)
            loadDetails()
        }
    }

    fun loadDetails() = viewModelScope.launch(dispatchers.io) {
        val bet = state.value.bet
        mutableState.update { it.copy(loading = true, detailsReady = false, errorMessage = null, serviceFailure = null) }
        val loader = loadConfirmedBet
        if (loader == null) {
            mutableState.value = when (val result = recoverBet(bet)) {
                is AppResult.Success -> BetDetailUiState(result.value)
                is AppResult.Failure -> BetDetailUiState(bet, detailsReady = false, errorMessage = result.error.toReceiptMessage())
            }
        } else when (val result = loader(bet)) {
            is br.gov.caixa.loterias.core.domain.service.ServiceResult.Success -> mutableState.value = BetDetailUiState(result.value)
            is br.gov.caixa.loterias.core.domain.service.ServiceResult.Failed -> mutableState.update {
                it.copy(loading = false, errorMessage = result.error.toReceiptMessage())
            }
            is br.gov.caixa.loterias.core.domain.service.ServiceResult.Rejected -> mutableState.update {
                it.copy(loading = false, serviceFailure = result.failure)
            }
        }
    }

    fun dismissServiceFailure() = mutableState.update { it.copy(serviceFailure = null) }

    fun downloadReceipt() = viewModelScope.launch(dispatchers.io) {
        val betId = state.value.bet.id
        if (betId <= 0) {
            mutableState.update { it.copy(errorMessage = "Comprovante indisponível para esta aposta.") }
            return@launch
        }
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        val prizeReceipt = state.value.bet.redeemedPrize && downloadPrizeReceipt != null
        val result = if (prizeReceipt) requireNotNull(downloadPrizeReceipt)(betId) else downloadBetReceipt(betId)
        when (result) {
            is AppResult.Success -> {
                mutableReceipts.emit(BetReceipt(result.value, "comprovante-${if (prizeReceipt) "premio" else "aposta"}-$betId.pdf"))
                mutableState.update { it.copy(loading = false) }
            }
            is AppResult.Failure -> mutableState.update {
                it.copy(loading = false, errorMessage = result.error.toReceiptMessage())
            }
        }
    }

    fun dismissError() = mutableState.update { it.copy(errorMessage = null) }

    fun dismissCartAddition() = mutableState.update { it.copy(cartAdditionConfirmed = false) }

    fun toggleFavoriteEditor() = mutableState.update {
        it.copy(favoriteExpanded = !it.favoriteExpanded, favoriteSaved = false, errorMessage = null)
    }

    fun changeFavoriteName(value: String) = mutableState.update {
        it.copy(favoriteName = value.take(25), errorMessage = null)
    }

    fun submitFavorite() = viewModelScope.launch(dispatchers.io) {
        if (state.value.loading) return@launch
        pendingFavoriteDraft = null
        mutableState.update { it.copy(favoriteOverwriteConfirmation = false) }
        val draft = state.value.toFavoriteDraft()
        if (draft.name.isBlank()) {
            mutableState.update { it.copy(errorMessage = "Por favor, insira o nome da aposta.") }
            return@launch
        }
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        when (val result = validateFavoriteBet(draft)) {
            is AppResult.Success -> saveFavorite(draft)
            is AppResult.Failure -> when (result.error) {
                is AppError.Validation -> {
                    pendingFavoriteDraft = draft
                    mutableState.update { it.copy(loading = false, favoriteOverwriteConfirmation = true) }
                }
                else -> mutableState.update {
                    it.copy(loading = false, errorMessage = result.error.toFavoriteMessage())
                }
            }
        }
    }

    fun confirmFavoriteOverwrite() = viewModelScope.launch(dispatchers.io) {
        val draft = pendingFavoriteDraft ?: return@launch
        if (!state.value.favoriteOverwriteConfirmation || state.value.loading) return@launch
        pendingFavoriteDraft = null
        mutableState.update { it.copy(favoriteOverwriteConfirmation = false, loading = true) }
        saveFavorite(draft)
    }

    fun dismissFavoriteOverwrite() {
        pendingFavoriteDraft = null
        mutableState.update { it.copy(favoriteOverwriteConfirmation = false) }
    }

    fun addCurrentBetToCart() = viewModelScope.launch(dispatchers.io) {
        if (!state.value.detailsReady) return@launch
        val bet = state.value.bet
        val hasSelection = bet.numbers.isNotEmpty() || bet.numberColumns.any(List<Int>::isNotEmpty) ||
            bet.lotecaSelections.isNotEmpty() || bet.lotogolSelections.isNotEmpty() || bet.surprise
        if (bet.modalityId <= 0 || !hasSelection) {
            mutableState.update { it.copy(errorMessage = "Não foi possível recuperar os dados desta aposta.") }
            return@launch
        }
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        val parameters = getBettingParameters
        if (parameters == null) {
            submitRepeat(bet, bet.contestType())
            return@launch
        }
        when (val result = parameters()) {
            is AppResult.Failure -> mutableState.update {
                it.copy(loading = false, errorMessage = result.error.toCartMessage())
            }
            is AppResult.Success -> resolveRepeatContest(bet, result.value.games)
        }
    }

    fun selectRepeatContest(index: Int) = mutableState.update { current ->
        if (index !in current.repeatContestOptions.indices) current
        else current.copy(selectedRepeatContestIndex = index)
    }

    fun confirmRepeatContest() = viewModelScope.launch(dispatchers.io) {
        val current = state.value
        val option = current.repeatContestOptions.getOrNull(current.selectedRepeatContestIndex) ?: return@launch
        mutableState.update { it.copy(repeatContestOptions = emptyList(), loading = true) }
        submitRepeat(current.bet, option.contestType)
    }

    fun dismissRepeatContest() = mutableState.update {
        it.copy(repeatContestOptions = emptyList(), repeatConversion = null, loading = false)
    }

    fun confirmRepeatConversion() = viewModelScope.launch(dispatchers.io) {
        val current = state.value
        val option = current.repeatConversion ?: return@launch
        mutableState.update { it.copy(repeatConversion = null, loading = true) }
        submitRepeat(current.bet, option.contestType)
    }

    private suspend fun resolveRepeatContest(bet: ConfirmedBet, games: List<LotteryGameParameters>) {
        val available = games.filter { it.modality.id == bet.modalityId && it.open }
            .distinctBy { it.contestType.normalizedContestType() }
            .map { game -> game.toRepeatOption(bet) }
        when (available.size) {
            0 -> mutableState.update {
                it.copy(loading = false, errorMessage = "Modalidade não disponível no momento.")
            }
            1 -> {
                val option = available.single()
                if (option.contestType == bet.contestType()) {
                    submitRepeat(bet, option.contestType)
                } else {
                    mutableState.update { it.copy(loading = false, repeatConversion = option) }
                }
            }
            else -> {
                val currentType = bet.contestType()
                val selected = available.indexOfFirst { it.contestType == currentType }.coerceAtLeast(0)
                mutableState.update {
                    it.copy(loading = false, repeatContestOptions = available, selectedRepeatContestIndex = selected)
                }
            }
        }
    }

    private suspend fun submitRepeat(bet: ConfirmedBet, contestType: String) {
        val draft = BetDraft(
            modalityId = bet.modalityId,
            contestType = contestType,
            numbers = bet.numbers,
            clovers = bet.clovers,
            recurringDraws = bet.recurringDraws,
            surprise = bet.surprise,
            quantity = bet.quantity,
            numberQuantity = bet.numberColumns.sumOf(List<Int>::size).takeIf { it > 0 } ?: bet.numbers.size,
            favoriteTeam = bet.favoriteTeam,
            lotecaSelections = bet.lotecaSelections,
            lotogolSelections = bet.lotogolSelections,
            superSevenColumns = bet.numberColumns.takeIf { it.size > 1 }.orEmpty(),
            luckyMonth = bet.luckyMonth,
            generateMirror = bet.mirror,
            cloverQuantity = bet.clovers.size,
        )
        when (val result = addBetToCart(draft)) {
            is AppResult.Success -> {
                mutableState.update { it.copy(loading = false, cartAdditionConfirmed = true) }
            }
            is AppResult.Failure -> mutableState.update {
                it.copy(loading = false, errorMessage = result.error.toCartMessage())
            }
        }
    }

    private suspend fun saveFavorite(draft: FavoriteBetDraft) {
        when (val result = saveFavoriteBet(draft)) {
            is AppResult.Success -> mutableState.update {
                it.copy(loading = false, favoriteExpanded = false, favoriteSaved = true)
            }
            is AppResult.Failure -> mutableState.update {
                it.copy(loading = false, errorMessage = result.error.toFavoriteMessage())
            }
        }
    }
}

private fun ConfirmedBet.contestType() = if (contestTypeId == 2L) "ESPECIAL" else "NORMAL"

private fun String.normalizedContestType() = when {
    equals("E", true) || contains("ESPECIAL", true) -> "ESPECIAL"
    else -> "NORMAL"
}

private fun LotteryGameParameters.toRepeatOption(bet: ConfirmedBet): RepeatContestOption {
    val type = contestType.normalizedContestType()
    val targetName = if (type == "ESPECIAL") modality.specialName.ifBlank { "concurso especial" } else modality.name
    val sourceName = if (bet.contestType() == "ESPECIAL") modality.specialName.ifBlank { "concurso especial" } else modality.name
    return RepeatContestOption(
        contestType = type,
        label = targetName.ifBlank { if (type == "ESPECIAL") "Especial" else "Normal" },
        conversionMessage = "A modalidade $sourceName não está vigente. A aposta será efetivada na modalidade $targetName. Deseja prosseguir?",
    )
}

private fun BetDetailUiState.toFavoriteDraft() = FavoriteBetDraft(
    modalityId = bet.modalityId,
    modalityName = bet.modality,
    name = favoriteName.trim(),
    numberColumns = bet.numberColumns.ifEmpty { listOf(bet.numbers) },
    clovers = bet.clovers,
    favoriteTeam = bet.favoriteTeam,
    luckyMonth = bet.luckyMonth,
)

private fun AppError.toReceiptMessage(): String = when (this) {
    AppError.Network -> "Não foi possível acessar o serviço. Verifique sua conexão."
    is AppError.Validation -> reason.ifBlank { "Não foi possível gerar o comprovante." }
    AppError.Unexpected -> "Não foi possível gerar o comprovante. Tente novamente."
}

private fun AppError.toFavoriteMessage(): String = when (this) {
    AppError.Network -> "Não foi possível acessar o serviço. Verifique sua conexão."
    is AppError.Validation -> reason.ifBlank { "Não foi possível salvar a aposta favorita." }
    AppError.Unexpected -> "Não foi possível salvar a aposta favorita. Tente novamente."
}

private fun AppError.toCartMessage(): String = when (this) {
    AppError.Network -> "Não foi possível acessar o serviço. Verifique sua conexão."
    is AppError.Validation -> reason.ifBlank { "Não foi possível adicionar a aposta ao carrinho." }
    AppError.Unexpected -> "Não foi possível adicionar a aposta ao carrinho. Tente novamente."
}
