package br.gov.caixa.loterias.feature.payment.domain.model

data class SavedPaymentCard(
    val id: String,
    val paymentMethodId: Long,
    val firstDigits: String,
    val lastDigits: String,
    val expirationMonth: Int,
    val expirationYear: Int,
    val securityCodeLength: Int,
    val brand: String,
    val paymentMethodCode: String,
    val customerToken: String = "",
) {
    val deletionIdentifier: String get() =
        if (paymentMethodId == PaymentMethod.RECARGA_PAY_ID) customerToken.ifBlank { id } else id
}
