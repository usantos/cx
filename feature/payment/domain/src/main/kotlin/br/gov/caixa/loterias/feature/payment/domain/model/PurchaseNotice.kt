package br.gov.caixa.loterias.feature.payment.domain.model

/** Resposta de negócio conhecida, sem significar aprovação nem resposta inconclusiva de transporte. */
data class PurchaseNotice(
    val reason: PurchaseNoticeReason,
    val message: String? = null,
    val code: String? = null,
)
