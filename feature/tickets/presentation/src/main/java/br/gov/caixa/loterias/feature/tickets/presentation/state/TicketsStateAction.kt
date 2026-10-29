package br.gov.caixa.loterias.feature.tickets.presentation.state
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseMonth
import br.gov.caixa.loterias.feature.tickets.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.TicketsPixNoticeUiState

internal sealed interface TicketsStateAction {
    data object TicketNavigationConsumed : TicketsQueryAction
    data class PurchaseBetContestSelected(val value: Int?) : TicketsPurchaseFilterAction
    data class PurchaseBetStatusSelected(val value: Long?) : TicketsPurchaseFilterAction
    data class PurchaseBetModalitySelected(val value: Int?) : TicketsPurchaseFilterAction

    data class OperationFailed(val message: String) : TicketsHistoryAction
    data object BetsLoading : TicketsHistoryAction
    data class BetsConfigurationReceived(
        val configuration: br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration,
        val modalities: List<br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality>,
    ) : TicketsHistoryAction
    data object BetFiltersApplied : TicketsHistoryAction
    data class BetsReceived(val page: br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsPage) : TicketsHistoryAction
    data object PurchasesLoading : TicketsHistoryAction
    data class PurchaseMonthsReceived(val months: List<br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseMonth>) :
        TicketsHistoryAction
    data object PurchasesReloading : TicketsHistoryAction
    data object PurchasePageLoading : TicketsHistoryAction
    data class PurchasePageReceived(val append: Boolean, val purchases: List<br.gov.caixa.loterias.feature.tickets.domain.model.Purchase>) :
        TicketsHistoryAction
    data class PurchaseMonthSelected(val month: br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseMonth) :
        TicketsPurchaseFilterAction
    data object PurchaseFiltersLoading : TicketsPurchaseFilterAction
    data class PurchaseFiltersReceived(
        val methods: List<br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption>,
        val statuses: List<br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption>,
    ) : TicketsPurchaseFilterAction
    data object PurchaseFiltersClosed : TicketsPurchaseFilterAction
    data class PaymentMethodSelected(val id: Long?) : TicketsPurchaseFilterAction
    data class PurchaseStatusesReceived(val statuses: List<br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption>) :
        TicketsPurchaseFilterAction
    data class PurchaseStatusSelected(val id: Long?) : TicketsPurchaseFilterAction
    data object PurchaseFiltersCleared : TicketsPurchaseFilterAction
    data object PurchaseFiltersApplied : TicketsPurchaseFilterAction
    data class TicketCodeChanged(val value: String) : TicketsQueryAction
    data class TicketRetailerCodeChanged(val value: String) : TicketsQueryAction
    data class ScannedTicketAccepted(val value: String) : TicketsQueryAction
    data class TicketCodeRejected(val message: String = messageText("informe_o_codigo_do_bilhete_804a11")) : TicketsQueryAction
    data object TicketQueryStarted : TicketsQueryAction
    data class TicketResultReceived(val result: br.gov.caixa.loterias.feature.tickets.domain.model.CheckedTicketResult) : TicketsQueryAction
    data object TicketErrorDismissed : TicketsQueryAction
    data object TicketModalitiesLoading : TicketsQueryAction
    data class TicketModalitiesReceived(val modalities: List<br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality>) :
        TicketsQueryAction
    data class TicketModalityToggled(val id: Int) : TicketsQueryAction
    data object PixStatusLoading : TicketsPixAction
    data class PixStatusFailed(val purchaseId: Long, val error: AppError, val message: String = capturePixStatusFailure(error)) :
        TicketsPixAction
    data class PixStatusReceived(
        val purchaseId: Long,
        val progress: br.gov.caixa.loterias.feature.payment.domain.model.PurchaseProgress,
        val notice: TicketsPixNoticeUiState?,
    ) : TicketsPixAction
    data class PixDetailsRefreshFailed(val purchaseId: Long, val message: String = messageText("nao_foi_possivel_atualizar_os_de_006e16")) :
        TicketsPixAction
    data class PixRedirectConsumed(val expected: br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect) : TicketsPixAction
    data class PixNoticeConsumed(val pause: Boolean, val expected: TicketsPixNoticeUiState) : TicketsPixAction
    data object PixRedirectResumed : TicketsPixAction
    data object PixPurchaseReset : TicketsPixAction
    data class PurchaseLoading(val purchase: Purchase) : TicketsPurchaseAction
    data object PurchaseBetFiltersOpened : TicketsPurchaseFilterAction
    data object PurchaseBetFiltersClosed : TicketsPurchaseFilterAction
    data object PurchaseBetFiltersCleared : TicketsPurchaseFilterAction
    data object PurchaseBetFiltersApplied : TicketsPurchaseFilterAction
    data object PurchaseStatusDialogDismissed : TicketsPurchaseAction
    data object PurchaseFavoriteOpened : TicketsFavoriteAction
    data object PurchaseFavoriteClosed : TicketsFavoriteAction
    data class PurchaseFavoriteNameChanged(val value: String) : TicketsFavoriteAction
    data object PurchaseKeepSurprisesToggled : TicketsFavoriteAction
    data class PurchaseFavoriteNameRejected(val message: String = messageText("informe_o_nome_do_carrinho_favor_fe59d0")) :
        TicketsFavoriteAction
    data object PurchaseFavoriteValidationStarted : TicketsFavoriteAction
    data object PurchaseFavoriteOverwriteRequired : TicketsFavoriteAction
    data object PurchaseFavoriteOverwriteConfirmed : TicketsFavoriteAction
    data object PurchaseFavoriteOverwriteCancelled : TicketsFavoriteAction
    data object PurchaseFavoriteSavedDismissed : TicketsFavoriteAction
    data object DamMessageDismissed : TicketsPurchaseAction
    data class PurchaseDetailsReceived(val purchase: Purchase, val bets: List<ConfirmedBet>, val dialog: PurchaseStatusDialog?) :
        TicketsPurchaseAction
    data class DamBlocked(val message: String) : TicketsPurchaseAction
    data object LocalPurchaseFavoriteSaved : TicketsFavoriteAction
    data object PurchaseFavoriteSaveStarted : TicketsFavoriteAction
    data object PurchaseFavoriteSaved : TicketsFavoriteAction
}

private fun capturePixStatusFailure(error: AppError): String = when (error) {
    AppError.Network -> messageText("nao_foi_possivel_consultar_sua_c_4f772c")
    is AppError.Validation -> error.reason
    AppError.Unexpected -> messageText("nao_foi_possivel_consultar_sua_c_08c7ae")
}
