package br.gov.caixa.loterias.feature.tickets.presentation.state

import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsBackend
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseMonth
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketPeriod
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.TicketsPixNoticeUiState
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.contestType

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
            it.year?.takeIf { year -> year > 0 }?.let { year -> put("ano", year.toString()) }
        }
        put("offset", "0")
        put("size", if (period?.month == 45) "32" else "30")
        // As posições do seletor legado diferem dos códigos de pago e expirado do serviço.
        when (status) {
            1L -> put("situacao", "1")
            2L -> put("situacao", "4")
            3L -> put("situacao", "6")
        }
        if (ordering > 0) put("ordenacao", ordering.toString())
        if (betType > 0) put("tipoAposta", betType.toString())
        if (contestType > 0) put("tipoConcurso", contestType.toString())
        if (modality > 0) put("modalidade", modality.toString())
        if (surprise) put("surpresinha", "3")
        if (recurring) put("teimosinha", "3")
        if (combo) put("combo", "3")
    }

    val activeCount: Int get() = activeCount(null)

    fun activeCount(configuration: ConfirmedBetsConfiguration?): Int = listOf(
        status != (configuration?.defaultStatus ?: 1L),
        ordering != (configuration?.defaultOrdering ?: 3L),
        betType != (configuration?.betTypes?.firstOrNull()?.value ?: 1L),
        contestType != (configuration?.contestTypes?.firstOrNull()?.value ?: 0L),
        modality > 0,
        surprise,
        recurring,
        combo
    ).count { it }
}

data class PurchaseFilters(val month: PurchaseMonth? = null, val paymentMethodId: Long? = null, val statusId: Long? = null) {
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

data class PurchaseBetFilters(val contest: Int? = null, val statusId: Long? = null, val modalityId: Int? = null) {
    val activeCount: Int get() = listOfNotNull(contest, statusId, modalityId).size

    fun matches(bet: ConfirmedBet): Boolean = (contest == null || bet.contest == contest) &&
        (statusId == null || bet.statusId == statusId) &&
        (modalityId == null || bet.modalityId == modalityId)
}

enum class PurchaseDialogAction { SAVE_FAVORITE, HELP, DISMISS, NEW_PIX, CHANGE_PAYMENT, HOME }

data class PurchaseStatusDialog(val title: String, val message: String, val actions: List<Pair<PurchaseDialogAction, String>>)

private const val PURCHASE_PAGE_SIZE = 10

data class TicketsUiState(
    val bets: List<ConfirmedBet> = emptyList(),
    val ticketResult: br.gov.caixa.loterias.feature.tickets.domain.model.CheckedTicketResult? = null,
    val purchases: List<Purchase> = emptyList(),
    val selectedPurchase: Purchase? = null,
    val checkingPixPurchase: Boolean = false,
    val pixPurchaseCompleted: Boolean = false,
    val pixPurchaseRedirect: br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect? = null,
    val pixPurchasePaused: Boolean = false,
    val pixPurchaseNotice: TicketsPixNoticeUiState? = null,

    val ticketCode: String = "",
    val ticketRetailerCode: String = "",
    val betConfiguration: ConfirmedBetsConfiguration? = null,
    val historyBackend: ConfirmedBetsBackend = ConfirmedBetsBackend.SILCE,
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
