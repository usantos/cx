package br.gov.caixa.loterias.feature.payment.presentation.state
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.PixCharge
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PurchaseNoticeUiState
import java.math.BigDecimal

internal sealed interface PaymentAction {
    data class MethodsLoaded(val methods: List<PaymentMethod>, val cards: Map<Long, List<SavedPaymentCard>>) : PaymentAction
    data class CheckoutMethodsLoaded(
        val methods: List<PaymentMethod>,
        val cards: Map<Long, List<SavedPaymentCard>>,
        val total: BigDecimal,
    ) : PaymentAction
    data object RegistrationFinished : PaymentAction
    data class NoticeConsumed(val moreBets: Boolean, val registration: Boolean) : PaymentAction
    data object PixGenerationStarted : PaymentAction
    data object PixRecoveryRequired : PaymentAction
    data class PixGenerated(val pix: PixCharge) : PaymentAction
    data object PixStatusCheckCancelled : PaymentAction
    data object PixStatusCheckStarted : PaymentAction
    data class PixRedirectReceived(val destination: PurchaseRedirect) : PaymentAction
    data class PixNoticeReceived(val presentation: PurchaseNoticeUiState) : PaymentAction
    data class PixStatusReceived(val completed: Boolean, val rejected: Boolean) : PaymentAction
    data object PrePurchaseAccepted : PaymentAction
    data class DamValidated(val message: String?, val blocked: Boolean) : PaymentAction
    data object RedirectConsumed : PaymentAction
    data object RedirectResumed : PaymentAction
    data object CardDeletionStarted : PaymentAction
    data class CardDeleted(val methodId: Long, val cards: List<SavedPaymentCard>) : PaymentAction
    data object LoadingStarted : PaymentAction
    data object ErrorDismissed : PaymentAction
    data object DamMessageDismissed : PaymentAction
    data object PixStatusDismissed : PaymentAction
    data class CardDeletionRequested(val card: SavedPaymentCard) : PaymentAction
    data object CardDeletionCancelled : PaymentAction
    data class CardAdditionRequested(val methodId: Long) : PaymentAction
    data object CardAdditionCancelled : PaymentAction
    data class Failed(val error: AppError, val message: String = capturePaymentFailure(error)) : PaymentAction
}

private fun capturePaymentFailure(error: AppError): String = when (error) {
    AppError.Network -> messageText("nao_foi_possivel_acessar_o_pagam_5e08bc")
    is AppError.Validation -> error.reason.ifBlank { messageText("nao_foi_possivel_continuar_o_pag_2420df") }
    AppError.Unexpected -> "Ocorreu um erro inesperado no pagamento."
}
