package br.gov.caixa.loterias.feature.payment.domain.model

/** Um envio inconclusivo não é uma rejeição e não deve oferecer outro POST de cartão. */
sealed interface CardSubmissionOutcome {
    data class Submitted(val purchase: PurchaseSubmission) : CardSubmissionOutcome
    data class ReviewRequired(val purchaseId: Long? = null) : CardSubmissionOutcome
    data class RedirectRequired(val destination: PurchaseRedirect, val purchaseId: Long? = null) : CardSubmissionOutcome
    data class NoticeRequired(val notice: PurchaseNotice) : CardSubmissionOutcome
}
