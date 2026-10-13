package br.gov.caixa.loterias.core.domain.payment

/** A known business response, not approval and not an inconclusive transport response. */
data class PurchaseNotice(
    val reason: PurchaseNoticeReason,
    val message: String? = null,
    val code: String? = null,
)

enum class PurchaseNoticeReason {
    MORE_BETS_REQUIRED, BUSINESS_RULE, SERVER_MESSAGE, SERVER_FAILURE, INVALID_IDENTIFICATION, MERCADO_PAGO_REGISTRATION,
}
