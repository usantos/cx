package br.gov.caixa.loterias.feature.tickets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.betting.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.core.domain.betting.LotteryModality
import br.gov.caixa.loterias.core.domain.cart.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.core.domain.payment.ValidateDamUseCase
import br.gov.caixa.loterias.core.domain.tickets.CheckTicketUseCase
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.core.domain.tickets.GetConfirmedBetsConfigurationUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseDetailsUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseMonthsUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchasePaymentMethodsUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchaseStatusesUseCase
import br.gov.caixa.loterias.core.domain.tickets.GetPurchasesUseCase
import br.gov.caixa.loterias.core.domain.tickets.Purchase
import br.gov.caixa.loterias.core.domain.tickets.PurchaseMonth
import br.gov.caixa.loterias.core.domain.tickets.SavePurchaseAsFavoriteUseCase
import br.gov.caixa.loterias.core.domain.tickets.TicketFilterOption
import br.gov.caixa.loterias.core.domain.tickets.TicketPeriod
import br.gov.caixa.loterias.core.domain.tickets.TicketQuery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BetFilters(
    val period: TicketPeriod? = null,
    val status: Long = 1,
    val ordering: Long = 3,
    val betType: Long = 1,
    val contestType: Long = 0,
    val modality: Int = 0,
    val surprise: Boolean = false,
    val recurring: Boolean = false,
    val combo: Boolean = false,
) {
    fun query(): Map<String, String> = buildMap {
        period?.let {
            put("mes", it.month.toString())
            put("ano", (it.year ?: 0).toString())
        }
        put("offset", "0")
        put("size", if (period?.month == 45) "32" else "30")
        if (status > 0) put("situacao", status.toString())
        if (ordering > 0) put("ordenacao", ordering.toString())
        if (betType > 0) put("tipoAposta", betType.toString())
        if (contestType > 0) put("tipoConcurso", contestType.toString())
        if (modality > 0) put("modalidade", modality.toString())
        if (surprise) put("surpresinha", "3")
        if (recurring) put("teimosinha", "3")
        if (combo) put("combo", "3")
    }

    val activeCount: Int
        get() = listOf(
            status != 1L,
            ordering != 3L,
            betType != 1L,
            contestType > 0,
            modality > 0,
            surprise,
            recurring,
            combo,
        ).count { it }
}

data class PurchaseFilters(
    val month: PurchaseMonth? = null,
    val paymentMethodId: Long? = null,
    val statusId: Long? = null,
) {
    fun query(offset: Int): Map<String, String> = buildMap {
        month?.let {
            put("mes", it.month.toString())
            put("ano", it.year.toString())
        }
        put("offset", offset.toString())
        put("size", PURCHASE_PAGE_SIZE.toString())
        paymentMethodId?.let { put("meio", it.toString()) }
        statusId?.let { put("situacao", it.toString()) }
    }

    val activeCount: Int get() = listOfNotNull(paymentMethodId, statusId).size
}

data class PurchaseBetFilters(
    val contest: Int? = null,
    val statusId: Long? = null,
    val modalityId: Int? = null,
) {
    val activeCount: Int get() = listOfNotNull(contest, statusId, modalityId).size

    fun matches(bet: ConfirmedBet): Boolean =
        (contest == null || bet.contest == contest) &&
            (statusId == null || bet.statusId == statusId) &&
            (modalityId == null || bet.modalityId == modalityId)
}

enum class PurchaseDialogAction { SAVE_FAVORITE, HELP, DISMISS, NEW_PIX, CHANGE_PAYMENT, HOME }

data class PurchaseStatusDialog(
    val title: String,
    val message: String,
    val actions: List<Pair<PurchaseDialogAction, String>>,
)

private const val PURCHASE_PAGE_SIZE = 10

data class TicketsUiState(
    val bets: List<ConfirmedBet> = emptyList(),
    val ticketResult: br.gov.caixa.loterias.core.domain.tickets.CheckedTicketResult? = null,
    val purchases: List<Purchase> = emptyList(),
    val selectedPurchase: Purchase? = null,
    val checkingPixPurchase: Boolean = false,
    val pixPurchaseCompleted: Boolean = false,
    val pixPurchaseRedirect: br.gov.caixa.loterias.core.domain.payment.PurchaseRedirect? = null,
    val pixPurchasePaused: Boolean = false,
    val pixPurchaseNotice: TicketsPixNoticeUiState? = null,


    val ticketCode: String = "",
    val ticketRetailerCode: String = "",
    val betConfiguration: ConfirmedBetsConfiguration? = null,
    val historyBackend: br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsBackend = br.gov.caixa.loterias.core.domain.tickets.ConfirmedBetsBackend.SILCE,
    val modalities: List<LotteryModality> = emptyList(),
    val ticketModalities: List<LotteryModality> = emptyList(),
    val selectedTicketModalityIds: Set<Int> = emptySet(),
    val betFilters: BetFilters = BetFilters(),
    val purchaseMonths: List<PurchaseMonth> = emptyList(),
    val paymentMethods: List<TicketFilterOption> = emptyList(),
    val purchaseStatuses: List<TicketFilterOption> = emptyList(),
    val purchaseFilters: PurchaseFilters = PurchaseFilters(),
    val purchaseBetFilters: PurchaseBetFilters = PurchaseBetFilters(),
    val showPurchaseBetFilters: Boolean = false,
    val purchaseStatusDialog: PurchaseStatusDialog? = null,
    val showFavoriteDialog: Boolean = false,
    val favoriteName: String = "",
    val keepSurprises: Boolean = true,
    val favoriteOverwriteConfirmation: Boolean = false,
    val favoriteSaved: Boolean = false,
    val damMessage: String? = null,
    val showBetFilters: Boolean = false,
    val showPurchaseFilters: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = false,
    val loaded: Boolean = false,
    val errorMessage: String? = null,
)

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
    private val getPixPurchaseStatus: br.gov.caixa.loterias.core.domain.payment.GetPurchaseProgressUseCase? = null,
    private val pixNoticeClock: java.time.Clock = java.time.Clock.systemDefaultZone(),

) : ViewModel() {
    private val mutableState = MutableStateFlow(TicketsUiState())
    val state = mutableState.asStateFlow()

    fun loadBets() {
        if (mutableState.value.isLoading) return
        mutableState.value = mutableState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch(dispatchers.io) {
            val configuration = when (val result = getBetsConfiguration()) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> return@launch fail("Não foi possível carregar os filtros de apostas.")
            }
            val modalities = when (val result = getModalities()) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> return@launch fail("Não foi possível carregar as modalidades.")
            }
            val current = mutableState.value.betFilters
            val period = configuration.defaultPeriod?.takeIf { it in configuration.periods }
                ?: configuration.periods.firstOrNull()
            val filters = current.copy(
                period = current.period ?: period,
                status = configuration.defaultStatus ?: current.status,
                ordering = configuration.defaultOrdering ?: current.ordering,
            )
            mutableState.value = mutableState.value.copy(
                betConfiguration = configuration,
                modalities = modalities,
                betFilters = filters,
            )
            loadBetsWith(filters)
        }
    }

    fun loadTicketModalities() {
        if (mutableState.value.isLoading || mutableState.value.ticketModalities.isNotEmpty()) return
        mutableState.value = mutableState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch(dispatchers.io) {
            when (val result = getModalities()) {
                is AppResult.Failure -> fail("Não foi possível carregar as modalidades.")
                is AppResult.Success -> mutableState.value = mutableState.value.copy(
                    ticketModalities = result.value.filter { it.code.normalizedTicketCode() in legacyTicketModalityCodes },
                    isLoading = false,
                    loaded = true,
                )
            }
        }
    }

    fun toggleTicketModality(id: Int) {
        if (mutableState.value.ticketModalities.none { it.id == id }) return
        val selected = mutableState.value.selectedTicketModalityIds
        mutableState.value = mutableState.value.copy(
            selectedTicketModalityIds = if (id in selected) selected - id else selected + id,
        )
    }

    fun openBetFilters() { mutableState.value = mutableState.value.copy(showBetFilters = true) }
    fun closeBetFilters() { mutableState.value = mutableState.value.copy(showBetFilters = false) }
    fun selectBetPeriod(value: TicketPeriod) = updateBetFilters { copy(period = value) }
    fun selectBetStatus(value: Long) = updateBetFilters { copy(status = value) }
    fun selectBetOrdering(value: Long) = updateBetFilters { copy(ordering = value) }
    fun selectBetType(value: Long) = updateBetFilters { copy(betType = value) }
    fun selectContestType(value: Long) = updateBetFilters { copy(contestType = value) }
    fun selectModality(value: Int) = updateBetFilters { copy(modality = value) }
    fun toggleSurprise() = updateBetFilters { copy(surprise = !surprise) }
    fun toggleRecurring() = updateBetFilters { copy(recurring = !recurring) }
    fun toggleCombo() = updateBetFilters { copy(combo = !combo) }

    fun clearBetFilters() {
        val config = mutableState.value.betConfiguration
        val period = config?.defaultPeriod?.takeIf { it in config.periods } ?: config?.periods?.firstOrNull()
        mutableState.value = mutableState.value.copy(
            betFilters = BetFilters(
                period = period,
                status = config?.defaultStatus ?: 1,
                ordering = config?.defaultOrdering ?: 3,
            ),
            showBetFilters = false,
        )
        applyBetFilters()
    }

    fun applyBetFilters() {
        if (mutableState.value.isLoading) return
        mutableState.value = mutableState.value.copy(showBetFilters = false, isLoading = true, errorMessage = null)
        viewModelScope.launch(dispatchers.io) { loadBetsWith(mutableState.value.betFilters) }
    }

    fun loadPurchases() {
        if (mutableState.value.isLoading) return
        mutableState.value = mutableState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch(dispatchers.io) {
            val months = when (val result = getPurchaseMonths()) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> return@launch fail("Não foi possível carregar os meses das compras.")
            }
            val filters = mutableState.value.purchaseFilters.copy(
                month = mutableState.value.purchaseFilters.month?.takeIf { it in months } ?: months.firstOrNull(),
            )
            mutableState.value = mutableState.value.copy(purchaseMonths = months, purchaseFilters = filters)
            loadPurchasesWith(filters, append = false)
        }
    }

    fun selectPurchaseMonth(month: PurchaseMonth) {
        mutableState.value = mutableState.value.copy(
            purchaseFilters = mutableState.value.purchaseFilters.copy(month = month),
        )
        reloadPurchases()
    }

    fun openPurchaseFilters() {
        if (mutableState.value.isLoading) return
        mutableState.value = mutableState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch(dispatchers.io) {
            val methods = when (val result = getPaymentMethods()) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> return@launch fail("Não foi possível carregar os meios de pagamento.")
            }
            val statuses = when (val result = getPurchaseStatuses(mutableState.value.purchaseFilters.paymentMethodId)) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> return@launch fail("Não foi possível carregar as situações de compra.")
            }
            mutableState.value = mutableState.value.copy(
                paymentMethods = methods,
                purchaseStatuses = statuses,
                isLoading = false,
                showPurchaseFilters = true,
            )
        }
    }

    fun closePurchaseFilters() { mutableState.value = mutableState.value.copy(showPurchaseFilters = false) }

    fun selectPaymentMethod(id: Long?) {
        mutableState.value = mutableState.value.copy(
            purchaseFilters = mutableState.value.purchaseFilters.copy(paymentMethodId = id, statusId = null),
            isLoading = true,
        )
        viewModelScope.launch(dispatchers.io) {
            when (val result = getPurchaseStatuses(id)) {
                is AppResult.Success -> mutableState.value = mutableState.value.copy(
                    purchaseStatuses = result.value,
                    isLoading = false,
                    showPurchaseFilters = true,
                )
                is AppResult.Failure -> fail("Não foi possível carregar as situações de compra.")
            }
        }
    }

    fun selectPurchaseStatus(id: Long?) {
        mutableState.value = mutableState.value.copy(
            purchaseFilters = mutableState.value.purchaseFilters.copy(statusId = id),
        )
    }

    fun clearPurchaseFilters() {
        mutableState.value = mutableState.value.copy(
            purchaseFilters = mutableState.value.purchaseFilters.copy(paymentMethodId = null, statusId = null),
            showPurchaseFilters = false,
        )
        reloadPurchases()
    }

    fun applyPurchaseFilters() {
        mutableState.value = mutableState.value.copy(showPurchaseFilters = false)
        reloadPurchases()
    }

    fun loadMorePurchases() {
        val current = mutableState.value
        if (current.isLoading || current.isLoadingMore || !current.canLoadMore) return
        mutableState.value = current.copy(isLoadingMore = true)
        viewModelScope.launch(dispatchers.io) {
            loadPurchasesWith(mutableState.value.purchaseFilters, append = true)
        }
    }

    fun checkPendingPixStatus() {
        val current = mutableState.value
        val purchase = current.selectedPurchase ?: return
        if (current.isLoading || current.checkingPixPurchase || current.pixPurchaseCompleted ||
            current.pixPurchasePaused || current.pixPurchaseRedirect != null || current.pixPurchaseNotice != null ||
            !purchase.isPendingPix() || purchase.paymentSecondsRemaining() <= 0) return
        val query = getPixPurchaseStatus ?: return
        mutableState.value = current.copy(checkingPixPurchase = true, errorMessage = null)
        viewModelScope.launch(dispatchers.io) {
            when (val result = query(purchase.id)) {
                is AppResult.Failure -> if (mutableState.value.selectedPurchase?.id == purchase.id) {
                    mutableState.value = mutableState.value.copy(checkingPixPurchase = false,
                        errorMessage = when (val error = result.error) {
                            AppError.Network -> "Não foi possível consultar sua compra. Verifique sua internet."
                            is AppError.Validation -> error.reason
                            AppError.Unexpected -> "Não foi possível consultar sua compra."
                        })
                }
                is AppResult.Success -> {
                    if (mutableState.value.selectedPurchase?.id != purchase.id) return@launch
                    val progress = result.value
                    mutableState.value = mutableState.value.copy(checkingPixPurchase = false,
                        pixPurchaseCompleted = progress.completed,
                        pixPurchaseRedirect = progress.redirect,
                        pixPurchaseNotice = progress.notice?.let { ticketsPixNoticeUi(it, java.time.LocalDateTime.now(pixNoticeClock)) })
                    if (progress.completed || progress.rejected) {
                        // Only a GET for the same purchase refreshes history after a status response.
                        val details = getPurchaseDetails ?: return@launch
                        when (val refreshed = details(purchase)) {
                            is AppResult.Success -> if (mutableState.value.selectedPurchase?.id == purchase.id) {
                                showPurchaseDetails(refreshed.value.purchase, refreshed.value.bets)
                            }
                            is AppResult.Failure -> if (mutableState.value.selectedPurchase?.id == purchase.id) {
                                mutableState.value = mutableState.value.copy(
                                    errorMessage = "Não foi possível atualizar os detalhes da compra.")
                            }
                        }
                    }
                }
            }
        }
    }

    fun consumePixPurchaseRedirect(expected: br.gov.caixa.loterias.core.domain.payment.PurchaseRedirect): Boolean {
        if (mutableState.value.pixPurchaseRedirect != expected) return false
        mutableState.value = mutableState.value.copy(pixPurchaseRedirect = null, pixPurchasePaused = true)
        return true
    }

    fun consumePixPurchaseNotice(expected: TicketsPixNoticeUiState, pause: Boolean = false): Boolean {
        if (mutableState.value.pixPurchaseNotice !== expected) return false
        mutableState.value = mutableState.value.copy(pixPurchaseNotice = null, pixPurchasePaused = pause)
        return true
    }

    fun resumePixPurchaseAfterRedirect() {
        mutableState.value = mutableState.value.copy(pixPurchasePaused = false)
    }

    fun loadPurchase(purchase: Purchase) {
        if (mutableState.value.selectedPurchase?.id != purchase.id) {
            mutableState.value = mutableState.value.copy(checkingPixPurchase = false, pixPurchaseCompleted = false,
                pixPurchaseRedirect = null, pixPurchasePaused = false, pixPurchaseNotice = null)
        }

        if (mutableState.value.isLoading) return
        mutableState.value = mutableState.value.copy(
            selectedPurchase = purchase,
            purchaseBetFilters = PurchaseBetFilters(),
            purchaseStatusDialog = null,
            favoriteSaved = false,
            isLoading = true,
            errorMessage = null,
        )
        viewModelScope.launch(dispatchers.io) {
            val details = getPurchaseDetails
            if (details == null) {
                when (val result = getPurchaseBets(purchase.id)) {
                    is AppResult.Success -> showPurchaseDetails(purchase, result.value)
                    is AppResult.Failure -> fail("Não foi possível carregar os dados. Tente novamente.")
                }
            } else {
                when (val result = details(purchase)) {
                    is AppResult.Success -> showPurchaseDetails(result.value.purchase, result.value.bets)
                    is AppResult.Failure -> fail("Não foi possível carregar os dados. Tente novamente.")
                }
            }
        }
    }

    fun openPurchaseBetFilters() {
        mutableState.value = mutableState.value.copy(showPurchaseBetFilters = true)
    }

    fun closePurchaseBetFilters() {
        mutableState.value = mutableState.value.copy(showPurchaseBetFilters = false)
    }

    fun selectPurchaseBetContest(value: Int?) = updatePurchaseBetFilters { copy(contest = value) }
    fun selectPurchaseBetStatus(value: Long?) = updatePurchaseBetFilters { copy(statusId = value) }
    fun selectPurchaseBetModality(value: Int?) = updatePurchaseBetFilters { copy(modalityId = value) }

    fun clearPurchaseBetFilters() {
        mutableState.value = mutableState.value.copy(
            purchaseBetFilters = PurchaseBetFilters(),
            showPurchaseBetFilters = false,
        )
    }

    fun applyPurchaseBetFilters() {
        mutableState.value = mutableState.value.copy(showPurchaseBetFilters = false)
    }

    fun dismissPurchaseStatusDialog() {
        mutableState.value = mutableState.value.copy(purchaseStatusDialog = null)
    }

    fun openFavoriteDialog() {
        val current = mutableState.value
        if (current.bets.any(ConfirmedBet::poolShare)) return
        mutableState.value = current.copy(
            purchaseStatusDialog = null,
            showFavoriteDialog = true,
            favoriteName = "",
            keepSurprises = true,
        )
    }

    fun closeFavoriteDialog() {
        mutableState.value = mutableState.value.copy(showFavoriteDialog = false, favoriteName = "")
    }

    fun changeFavoriteName(value: String) {
        mutableState.value = mutableState.value.copy(favoriteName = value.take(60), errorMessage = null)
    }

    fun toggleKeepSurprises() {
        mutableState.value = mutableState.value.copy(keepSurprises = !mutableState.value.keepSurprises)
    }

    fun validateAndSaveFavorite() {
        val current = mutableState.value
        if (current.favoriteName.isBlank()) {
            mutableState.value = current.copy(errorMessage = "Informe o nome do carrinho favorito.")
            return
        }
        val validation = validateFavoriteName
        if (validation == null) return saveFavorite(overwrite = true)
        mutableState.value = current.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch(dispatchers.io) {
            when (validation(current.favoriteName)) {
                is AppResult.Success -> saveFavorite(overwrite = true)
                is AppResult.Failure -> mutableState.value = mutableState.value.copy(
                    isLoading = false,
                    favoriteOverwriteConfirmation = true,
                )
            }
        }
    }

    fun confirmFavoriteOverwrite() {
        mutableState.value = mutableState.value.copy(favoriteOverwriteConfirmation = false)
        saveFavorite(overwrite = true)
    }

    fun cancelFavoriteOverwrite() {
        mutableState.value = mutableState.value.copy(favoriteOverwriteConfirmation = false)
    }

    fun dismissFavoriteSaved() {
        mutableState.value = mutableState.value.copy(favoriteSaved = false)
    }

    fun dismissDamMessage() {
        mutableState.value = mutableState.value.copy(damMessage = null)
    }

    fun changeTicketCode(value: String) {
        mutableState.value = mutableState.value.copy(
            ticketCode = value.filter(Char::isLetterOrDigit).uppercase().take(23),
            errorMessage = null,
        )
    }

    fun changeTicketRetailerCode(value: String) {
        mutableState.value = mutableState.value.copy(
            ticketRetailerCode = value.filter(Char::isLetterOrDigit).uppercase().take(9),
            errorMessage = null,
        )
    }

    fun submitScannedTicketCode(value: String) {
        if (value.length < MINIMUM_SCANNED_TICKET_LENGTH) return
        mutableState.value = mutableState.value.copy(ticketCode = value, ticketRetailerCode = "", errorMessage = null)
        checkTicket(qrCode = true)
    }

    fun checkTicket(qrCode: Boolean = false) {
        val code = mutableState.value.ticketCode
        if (code.isBlank() || (!qrCode && code.length < MINIMUM_MANUAL_TICKET_LENGTH)) {
            mutableState.value = mutableState.value.copy(errorMessage = "Informe o código do bilhete.")
            return
        }
        val retailerCode = mutableState.value.ticketRetailerCode
        if (mutableState.value.isLoading) return
        mutableState.value = mutableState.value.copy(isLoading = true, loaded = false, ticketResult = null, errorMessage = null)
        viewModelScope.launch(dispatchers.io) {
            when (val result = checkTicket.result(TicketQuery(code, qrCode || retailerCode.isNotBlank(), retailerCode))) {
                is AppResult.Success -> mutableState.value = mutableState.value.copy(
                    bets = result.value.bets, ticketResult = result.value, isLoading = false, loaded = true,
                )
                is AppResult.Failure -> fail("Não foi possível carregar os dados. Tente novamente.")
            }
        }
    }

    fun retryBets() = if (mutableState.value.betConfiguration == null) loadBets() else applyBetFilters()
    fun retryPurchases() = if (mutableState.value.purchaseMonths.isEmpty()) loadPurchases() else reloadPurchases()
    fun dismissTicketError() { mutableState.value = mutableState.value.copy(errorMessage = null) }

    companion object {
        const val MINIMUM_MANUAL_TICKET_LENGTH = 21
        private const val MINIMUM_SCANNED_TICKET_LENGTH = 23
    }

    private fun updateBetFilters(block: BetFilters.() -> BetFilters) {
        mutableState.value = mutableState.value.copy(betFilters = mutableState.value.betFilters.block())
    }

    private fun updatePurchaseBetFilters(block: PurchaseBetFilters.() -> PurchaseBetFilters) {
        mutableState.value = mutableState.value.copy(purchaseBetFilters = mutableState.value.purchaseBetFilters.block())
    }

    private suspend fun showPurchaseDetails(purchase: Purchase, bets: List<ConfirmedBet>) {
        mutableState.value = mutableState.value.copy(
            selectedPurchase = purchase,
            bets = bets,
            isLoading = false,
            loaded = true,
            purchaseStatusDialog = purchase.statusDialog(hasPoolShare = bets.any(ConfirmedBet::poolShare)),
        )
        if (purchase.statusId == 9L) {
            when (val result = validateDam?.invoke()) {
                is AppResult.Success -> if (result.value.blocked) {
                    mutableState.value = mutableState.value.copy(damMessage = result.value.message)
                }
                else -> Unit
            }
        }
    }

    private fun saveFavorite(overwrite: Boolean) {
        if (!overwrite) return
        val useCase = savePurchaseAsFavorite
        val current = mutableState.value
        if (useCase == null) {
            mutableState.value = current.copy(showFavoriteDialog = false, favoriteSaved = true)
            return
        }
        mutableState.value = current.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch(dispatchers.io) {
            when (val result = useCase(
                current.selectedPurchase?.id ?: 0,
                current.favoriteName,
                current.keepSurprises,
            )) {
                is AppResult.Success -> mutableState.value = mutableState.value.copy(
                    isLoading = false,
                    showFavoriteDialog = false,
                    favoriteSaved = true,
                )
                is AppResult.Failure -> fail("Não foi possível salvar o carrinho favorito.")
            }
        }
    }

    private suspend fun loadBetsWith(filters: BetFilters) {
        when (val result = getConfirmedBets.page(filters.query())) {
            is AppResult.Success -> mutableState.value = mutableState.value.copy(
                bets = result.value.bets,
                historyBackend = result.value.backend,
                isLoading = false,
                loaded = true,
            )
            is AppResult.Failure -> fail("Não foi possível carregar os dados. Tente novamente.")
        }
    }

    private fun reloadPurchases() {
        if (mutableState.value.isLoading) return
        mutableState.value = mutableState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch(dispatchers.io) {
            loadPurchasesWith(mutableState.value.purchaseFilters, append = false)
        }
    }

    private suspend fun loadPurchasesWith(filters: PurchaseFilters, append: Boolean) {
        val offset = if (append) mutableState.value.purchases.size else 0
        when (val result = getPurchases(filters.query(offset))) {
            is AppResult.Success -> mutableState.value = mutableState.value.copy(
                purchases = if (append) {
                    (mutableState.value.purchases + result.value).distinctBy(Purchase::id)
                } else {
                    result.value
                },
                isLoading = false,
                isLoadingMore = false,
                canLoadMore = result.value.size == PURCHASE_PAGE_SIZE,
                loaded = true,
            )
            is AppResult.Failure -> fail("Não foi possível carregar as compras. Tente novamente.")
        }
    }

    private fun loadBetResult(request: suspend () -> AppResult<List<ConfirmedBet>>) {
        if (mutableState.value.isLoading) return
        mutableState.value = mutableState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch(dispatchers.io) {
            when (val result = request()) {
                is AppResult.Success -> mutableState.value = mutableState.value.copy(
                    bets = result.value,
                    isLoading = false,
                    loaded = true,
                )
                is AppResult.Failure -> fail("Não foi possível carregar os dados. Tente novamente.")
            }
        }
    }

    private fun fail(message: String) {
        mutableState.value = mutableState.value.copy(
            isLoading = false,
            isLoadingMore = false,
            loaded = true,
            errorMessage = message,
        )
    }
}

private val legacyTicketModalityCodes = setOf(
    "QUINA", "LOTECA", "LOTOGOL", "LOTOFACIL", "LOTOMANIA", "MEGASENA", "TIMEMANIA", "DUPLASENA",
)

private fun String.normalizedTicketCode(): String = uppercase().filter(Char::isLetterOrDigit)

internal fun Purchase.statusDialog(hasPoolShare: Boolean): PurchaseStatusDialog? {
    if (hasPoolShare) return null
    val pix = paymentMethodId == 6L || paymentMethod.contains("pix", ignoreCase = true)
    val normalizedStatus = status.uppercase().filter(Char::isLetterOrDigit)
    val id = statusId.takeIf { it > 0 } ?: when {
        "CANCEL" in normalizedStatus && "PAGAMENTONAOIDENTIFICADO" in normalizedStatus -> 12
        "CANCEL" in normalizedStatus -> 5
        "ESTORN" in normalizedStatus -> 4
        "NAOAUTORIZ" in normalizedStatus -> 3
        else -> 0
    }
    val cancelledActions = listOf(
        PurchaseDialogAction.SAVE_FAVORITE to "Salvar Carrinho",
        PurchaseDialogAction.HELP to "Ajuda",
        PurchaseDialogAction.DISMISS to "Cancelar",
    )
    return when (id) {
        3L -> if (pix) PurchaseStatusDialog(
            "Pagamento não realizado",
            "Não foi possível concluir sua compra e a aposta não foi efetivada. Deseja realizar uma nova tentativa de compra com o mesmo carrinho?",
            listOf(
                PurchaseDialogAction.NEW_PIX to "Gerar novo Código",
                PurchaseDialogAction.CHANGE_PAYMENT to "Alterar Pagamento",
                PurchaseDialogAction.HOME to "Voltar ao início",
            ),
        ) else PurchaseStatusDialog(
            "Atenção",
            "Não foi possível concluir essa compra e sua aposta não foi efetivada. Você pode salvar esse mesmo carrinho para tentar novamente. Deseja salvar esta compra como carrinho favorito?",
            cancelledActions,
        )
        4L -> PurchaseStatusDialog(
            "Atenção",
            "Não foi possível completar a sua aposta e seu Pix será devolvido.\n\nVocê pode salvar esta compra como carrinho favorito para refazer a mesma compra e gerar novo código para pagamento. Deseja salvar esta compra como carrinho favorito?",
            cancelledActions,
        )
        5L -> PurchaseStatusDialog(
            "Atenção",
            if (pix) {
                "Não recebemos seu Pix dentro do prazo previsto para efetuar sua aposta e você não está concorrendo.\n\nVocê pode salvar esta compra como carrinho favorito para refazer a mesma compra e gerar novo código para pagamento. Deseja salvar esta compra como carrinho favorito?"
            } else {
                "Não identificamos o seu pagamento dentro do prazo previsto para efetuar sua aposta e você não está concorrendo.\n\nVocê pode salvar esta compra como carrinho favorito para refazer a mesma compra e gerar novo código para pagamento. E não se preocupe! Caso o pagamento seja identificado, será estornado para a conta pagadora. Deseja salvar esta compra como carrinho favorito?"
            },
            cancelledActions,
        )
        12L -> PurchaseStatusDialog(
            "Atenção",
            "Não identificamos o seu pagamento dentro do prazo previsto para efetuar sua aposta e você não está concorrendo.\n\nVocê pode salvar esta compra como carrinho favorito para refazer a mesma compra e gerar novo código para pagamento. E não se preocupe! Caso o pagamento seja identificado, será estornado para a conta pagadora. Deseja salvar esta compra como carrinho favorito?",
            cancelledActions,
        )
        else -> null
    }
}
