package br.gov.caixa.loterias.feature.retail.presentation.state
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRegistrationNotice

internal sealed interface PrizeRedemptionStateAction {
    data object DetailsLoading : PrizeRedemptionLoadingAction
    data class DetailsReceived(val details: br.gov.caixa.loterias.feature.retail.domain.model.PrizeDetails) : PrizeRedemptionLoadingAction
    data object ContestLoading : PrizeRedemptionLoadingAction
    data class ContestReceived(val parameters: br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters, val modalityId: Int) :
        PrizeRedemptionLoadingAction
    data class ContestFailed(val message: String = messageText("nao_foi_possivel_consultar_o_con_5fcb9e")) : PrizeRedemptionLoadingAction
    data object BetEntryStarted : PrizeRedemptionLoadingAction
    data class BetEntryPrepared(val decision: br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision) :
        PrizeRedemptionLoadingAction
    data class TimerTicked(val seconds: Int) : PrizeRedemptionLoadingAction
    data object ReceiptLoading : PrizeRedemptionReceiptAction
    data object ReceiptDelivered : PrizeRedemptionReceiptAction
    data object ReceiptRecoveryFailed : PrizeRedemptionReceiptAction
    data class NoticeDismissed(val expected: ServiceNoticeUiState) : PrizeRedemptionNoticeAction
    data class NoticeActionConsumed(val expected: ServiceNoticeUiState) : PrizeRedemptionNoticeAction
    data class RegistrationNoticeDismissed(val expected: PrizeRegistrationNotice) : PrizeRedemptionNoticeAction
    data class RegistrationNoticeConsumed(val expected: PrizeRegistrationNotice) : PrizeRedemptionNoticeAction
    data object RegistrationFinished : PrizeRedemptionNoticeAction
    data class RedirectConsumed(val expected: ServiceRedirect) : PrizeRedemptionNoticeAction
    data object RedirectResumed : PrizeRedemptionNoticeAction
    data class OfflineLoginBlocked(val notice: br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState) :
        PrizeRedemptionNoticeAction
    data object AgencyCodeLoading : PrizeRedemptionChannelAction
    data class AgencyCodeReceived(val code: br.gov.caixa.loterias.feature.retail.domain.model.LotteryRedemptionCode) :
        PrizeRedemptionChannelAction
    data object LotteryCodeLoading : PrizeRedemptionChannelAction
    data class LotteryCodeReceived(val code: br.gov.caixa.loterias.feature.retail.domain.model.LotteryRedemptionCode, val seconds: Int) :
        PrizeRedemptionChannelAction
    data object CpfPixKeySelected : PrizeRedemptionInputAction
    data object OtherPixKeySelected : PrizeRedemptionInputAction
    data class PixKeyChanged(val value: String) : PrizeRedemptionInputAction
    data object PixOwnershipToggled : PrizeRedemptionInputAction
    data class PixKeyRejected(val message: String = messageText("chave_invalida_22ed25")) : PrizeRedemptionChannelAction
    data object PixConsultStarted : PrizeRedemptionChannelAction
    data class PixBeneficiaryReceived(val beneficiary: br.gov.caixa.loterias.feature.retail.domain.model.PixBeneficiary) :
        PrizeRedemptionChannelAction
    data object PixCancellationStarted : PrizeRedemptionChannelAction
    data class EmailChanged(val value: String) : PrizeRedemptionInputAction
    data class EmailRejected(val message: String = messageText("e_mail_invalido_3660cb")) : PrizeRedemptionChannelAction
    data object SubmissionStarted : PrizeRedemptionSubmissionAction
    data class ConnectionFailed(val notice: br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState) :
        PrizeRedemptionSubmissionAction
    data object SubmissionUnchanged : PrizeRedemptionSubmissionAction
    data class Redeemed(val channel: br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel, val message: String?) :
        PrizeRedemptionSubmissionAction
    data class RegistrationRequired(val message: String, val email: String) : PrizeRedemptionSubmissionAction
    data class NoticeRequired(val notice: br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState) :
        PrizeRedemptionSubmissionAction
    data class RedirectRequired(val destination: br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect) :
        PrizeRedemptionSubmissionAction
    data object BetEntryConsumed : PrizeRedemptionLoadingAction
    data class ChannelSelected(val channel: br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel) :
        PrizeRedemptionNavigationAction
    data object PixEntryOpened : PrizeRedemptionNavigationAction
    data object MercadoPagoOpened : PrizeRedemptionNavigationAction
    data object ErrorDismissed : PrizeRedemptionNavigationAction
    data object SuccessNoticeDismissed : PrizeRedemptionNavigationAction
    data object ReturnedToOptions : PrizeRedemptionNavigationAction
    data class OperationFailed(val error: AppError, val message: String = capturePrizeOperationFailure(error)) :
        PrizeRedemptionNavigationAction
}

private fun capturePrizeOperationFailure(error: AppError): String = when (error) {
    AppError.Network -> messageText("nao_foi_possivel_acessar_o_servi_856170")
    is AppError.Validation -> error.reason.ifBlank { messageText("nao_foi_possivel_concluir_a_oper_c13b9b") }
    AppError.Unexpected -> messageText("nao_foi_possivel_concluir_a_oper_29014f")
}
