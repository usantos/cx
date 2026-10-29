package br.gov.caixa.loterias.feature.payment.domain.model

data class PaymentCardDraft(
    val number: String,
    val holderName: String,
    val expirationMonth: Int,
    val expirationYear: Int,
    val save: Boolean,
) {
    val normalizedNumber: String get() = number.filter(Char::isDigit)
    val lastDigits: String get() = normalizedNumber.takeLast(4)
    val brand: String get() = cardBrand(normalizedNumber)
}
