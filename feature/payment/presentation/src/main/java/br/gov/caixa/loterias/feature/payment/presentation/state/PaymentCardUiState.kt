package br.gov.caixa.loterias.feature.payment.presentation.state

import br.gov.caixa.loterias.feature.payment.domain.model.CardSubmissionOutcome
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.domain.model.cardBrand
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PurchaseNoticeUiState
import br.gov.caixa.loterias.feature.payment.presentation.mapper.acceptedBrands

enum class PaymentCardStage {
    LOADING,
    FORM,
    PREVIEW,
    CVV,
    CONFIRM,
    PROCESSING,
    FAILURE,
    SUCCESS,
    REVIEW_REQUIRED,
    REDIRECT_REQUIRED,
    NOTICE_REQUIRED,
}

data class PaymentCardUiState(
    val paymentMethodId: Long = 0,
    val minimumExpirationYear: Int = 2000,
    val stage: PaymentCardStage = PaymentCardStage.LOADING,
    val savedCard: SavedPaymentCard? = null,
    val number: String = "",
    val holderName: String = "",
    val month: String = "",
    val year: String = "",
    val save: Boolean = false,
    val cvv: String = "",
    val errorMessage: String? = null,
    val trackingPurchase: Boolean = false,
    val purchaseReview: CardSubmissionOutcome.ReviewRequired? = null,
    val redirect: PurchaseRedirect? = null,
    val redirectPurchaseId: Long? = null,
    val redirectPaused: Boolean = false,
    val notice: PurchaseNoticeUiState? = null,
    val registrationPending: Boolean = false,
) {
    val normalizedNumber: String get() = number.filter(Char::isDigit)
    val brand: String get() = savedCard?.brand?.uppercase().orEmpty().ifBlank {
        if (normalizedNumber.length >= 14) cardBrand(normalizedNumber) else ""
    }
    val lastDigits: String get() = savedCard?.lastDigits ?: normalizedNumber.takeLast(4)
    val cvvLength: Int get() = savedCard?.securityCodeLength ?: if (brand == "AMERICANEXPRESS") 4 else 3
    val providerName: String get() = if (paymentMethodId == 5L) "RecargaPay" else "Mercado Pago"
    val acceptedBrands: List<String> get() = if (paymentMethodId == 5L) {
        listOf("ELO", "MASTERCARD", "VISA")
    } else {
        listOf("AMERICANEXPRESS", "ELO", "HIPERCARD", "MASTERCARD", "VISA")
    }
    val expirationYears: List<Int> get() = (minimumExpirationYear..minimumExpirationYear + 20).toList()
    val formValid: Boolean get() =
        normalizedNumber.length in 14..16 &&
            holderName.isNotBlank() &&
            month.toIntOrNull() in 1..12 &&
            year.toIntOrNull() in expirationYears
}
