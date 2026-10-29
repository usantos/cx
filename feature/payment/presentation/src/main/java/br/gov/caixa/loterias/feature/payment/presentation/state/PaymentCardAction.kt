package br.gov.caixa.loterias.feature.payment.presentation.state
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.feature.payment.domain.model.CardSubmissionOutcome
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PurchaseNoticeUiState

internal sealed interface PaymentCardAction {
    data class NumberChanged(val value: String) : PaymentCardAction
    data class HolderNameChanged(val value: String) : PaymentCardAction
    data class MonthChanged(val value: String) : PaymentCardAction
    data class YearChanged(val value: String) : PaymentCardAction
    data object SaveToggled : PaymentCardAction
    data object CvvEntryStarted : PaymentCardAction
    data class InvalidCardEntered(val message: String = messageText("preencha_corretamente_os_dados_d_47a8d5")) : PaymentCardAction
    data class CvvChanged(val value: String) : PaymentCardAction
    data object ConfirmationStarted : PaymentCardAction
    data class InvalidCvvEntered(val message: String = messageText("codigo_de_seguranca_invalido_130084")) : PaymentCardAction
    data object ProcessingStarted : PaymentCardAction
    data object PurchaseTrackingStarted : PaymentCardAction
    data object PurchaseCompleted : PaymentCardAction
    data object RegistrationFinished : PaymentCardAction
    data class NoticeConsumed(
        val tracking: Boolean,
        val moreBets: Boolean,
        val registration: Boolean,
        val message: String = messageText("nao_foi_possivel_consultar_sua_c_08c7ae"),
    ) : PaymentCardAction
    data class NoticeRequired(val presentation: PurchaseNoticeUiState) : PaymentCardAction
    data class RedirectConsumed(val tracking: Boolean) : PaymentCardAction
    data object RedirectResumed : PaymentCardAction
    data class PurchaseReviewRequired(val outcome: CardSubmissionOutcome.ReviewRequired) : PaymentCardAction
    data class RedirectRequired(val destination: PurchaseRedirect, val purchaseId: Long?) : PaymentCardAction
    data object FormStarted : PaymentCardAction
    data class SavedCardPreviewed(val card: SavedPaymentCard) : PaymentCardAction
    data class SavedCardMissing(val message: String = messageText("cadastre_um_cartao_para_continua_c53e25")) : PaymentCardAction
    data class SavedCardLoaded(val initialStage: PaymentCardStage, val card: SavedPaymentCard) : PaymentCardAction
    data object ErrorDismissed : PaymentCardAction
    data class Failed(val error: AppError, val messages: PaymentCardFailureMessages = capturePaymentCardFailureMessages()) :
        PaymentCardAction
}

internal data class PaymentCardFailureMessages(
    val networkTracking: String,
    val networkPayment: String,
    val validation: String,
    val unexpectedTracking: String,
    val unexpectedPayment: String,
)

private fun capturePaymentCardFailureMessages() = PaymentCardFailureMessages(
    messageText("nao_foi_possivel_consultar_sua_c_4f772c"),
    messageText("nao_foi_possivel_processar_o_pag_f870ef"),
    messageText("nao_foi_possivel_processar_o_pag_16bbc1"),
    messageText("nao_foi_possivel_consultar_sua_c_08c7ae"),
    messageText("ocorreu_um_erro_inesperado_ao_pr_5f8b70")
)
