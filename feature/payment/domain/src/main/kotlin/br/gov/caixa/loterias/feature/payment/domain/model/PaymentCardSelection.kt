package br.gov.caixa.loterias.feature.payment.domain.model

sealed interface PaymentCardSelection {
    data class New(val draft: PaymentCardDraft) : PaymentCardSelection
    data class Saved(val card: SavedPaymentCard) : PaymentCardSelection
}
