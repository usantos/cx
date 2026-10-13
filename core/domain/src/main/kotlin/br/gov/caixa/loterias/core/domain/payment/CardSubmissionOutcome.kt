package br.gov.caixa.loterias.core.domain.payment

/** An inconclusive submission is not a rejection and must not offer another card POST. */
sealed interface CardSubmissionOutcome {
    data class Submitted(val purchase: PurchaseSubmission) : CardSubmissionOutcome
    data class ReviewRequired(val purchaseId: Long? = null) : CardSubmissionOutcome
    data class RedirectRequired(val destination: PurchaseRedirect, val purchaseId: Long? = null) : CardSubmissionOutcome
    data class NoticeRequired(val notice: PurchaseNotice) : CardSubmissionOutcome
}
