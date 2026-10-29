package br.gov.caixa.loterias.feature.payment.domain.model

data class CardPaymentRequest(
    val paymentMethodId: Long,
    val selection: PaymentCardSelection,
    val securityCode: String,
)
