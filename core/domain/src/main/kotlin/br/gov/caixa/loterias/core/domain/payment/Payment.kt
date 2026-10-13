package br.gov.caixa.loterias.core.domain.payment

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf
import java.math.BigDecimal

data class PaymentMethod(
    val id: Long,
    val purchaseName: String,
    val prizeName: String = "",
    val default: Boolean = false,
    val publicKey: String = "",
) {
    val isPix: Boolean get() = id == PIX_ID
    val isSupportedCard: Boolean get() = id == MERCADO_PAGO_ID || id == RECARGA_PAY_ID
    companion object { const val MERCADO_PAGO_ID = 1L; const val RECARGA_PAY_ID = 5L; const val PIX_ID = 6L }
}

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

data class PixCharge(
    val copyPaste: String,
    val transactionId: String,
    val qrCodeBase64: String,
    val createdAt: String,
    val expiresAt: String,
    val purchaseId: Long,
    val amount: BigDecimal,
)

/** No continuation means the card processing protocol ended, not that every bet was placed. */
data class PurchaseProgress(
    val purchaseId: Long,
    val status: Int,
    val nextPoll: PurchasePolling? = null,
    val redirect: PurchaseRedirect? = null,
    val notice: PurchaseNotice? = null,
) {
    val completed: Boolean get() = redirect == null && notice == null && purchasePaymentCompleted(status)
    val rejected: Boolean get() = redirect == null && notice == null && purchasePaymentRejected(status)
}

data class PurchasePolling(val intervalMillis: Long, val attemptLimit: Int)

data class DamValidation(
    val blocked: Boolean,
    val message: String,
)

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

sealed interface PaymentCardSelection {
    data class New(val draft: PaymentCardDraft) : PaymentCardSelection
    data class Saved(val card: SavedPaymentCard) : PaymentCardSelection
}

data class CardPaymentRequest(
    val paymentMethodId: Long,
    val selection: PaymentCardSelection,
    val securityCode: String,
)

data class PurchaseSubmission(
    val purchaseId: Long,
    val pollingIntervalMillis: Long,
    val pollingAttempts: Int,
    val status: Int,
) {
    val completed: Boolean get() = purchasePaymentCompleted(status)
    val rejected: Boolean get() = purchasePaymentRejected(status)
}

interface PaymentRepository {
    suspend fun methods(): List<PaymentMethod>
    suspend fun cards(paymentMethodId: Long): List<SavedPaymentCard>
    suspend fun deleteCard(cardId: String, paymentMethodId: Long): List<SavedPaymentCard>
    suspend fun generatePix(): PixCharge
    suspend fun purchaseProgress(purchaseId: Long): PurchaseProgress
    suspend fun validateDam(): DamValidation
    suspend fun submitCardPayment(request: CardPaymentRequest): CardSubmissionOutcome
}

fun interface GetPaymentMethodsUseCase { suspend operator fun invoke(): AppResult<List<PaymentMethod>> }
class GetPaymentMethods(private val repository: PaymentRepository) : GetPaymentMethodsUseCase {
    override suspend fun invoke() = appResultOf { repository.methods().filter { it.id > 0 } }
}

fun interface GetSavedCardsUseCase { suspend operator fun invoke(paymentMethodId: Long): AppResult<List<SavedPaymentCard>> }
class GetSavedCards(private val repository: PaymentRepository) : GetSavedCardsUseCase {
    override suspend fun invoke(paymentMethodId: Long) = appResultOf {
        require(paymentMethodId in setOf(PaymentMethod.MERCADO_PAGO_ID, PaymentMethod.RECARGA_PAY_ID)) { "Meio de pagamento inválido" }
        repository.cards(paymentMethodId)
    }
}

fun interface DeleteSavedCardUseCase { suspend operator fun invoke(cardId: String, paymentMethodId: Long): AppResult<List<SavedPaymentCard>> }
class DeleteSavedCard(private val repository: PaymentRepository) : DeleteSavedCardUseCase {
    override suspend fun invoke(cardId: String, paymentMethodId: Long) = appResultOf {
        require(cardId.isNotBlank()) { "Cartão inválido" }
        require(paymentMethodId in setOf(PaymentMethod.MERCADO_PAGO_ID, PaymentMethod.RECARGA_PAY_ID)) { "Meio de pagamento inválido" }
        repository.deleteCard(cardId, paymentMethodId)
    }
}

fun interface GeneratePixChargeUseCase { suspend operator fun invoke(): AppResult<PixCharge> }
class GeneratePixCharge(private val repository: PaymentRepository) : GeneratePixChargeUseCase {
    override suspend fun invoke() = appResultOf { repository.generatePix() }
}

fun interface GetPurchaseProgressUseCase { suspend operator fun invoke(purchaseId: Long): AppResult<PurchaseProgress> }
class GetPurchaseProgress(private val repository: PaymentRepository) : GetPurchaseProgressUseCase {
    override suspend fun invoke(purchaseId: Long) = appResultOf {
        require(purchaseId > 0) { "Compra inválida" }
        repository.purchaseProgress(purchaseId)
    }
}

fun interface ValidateDamUseCase { suspend operator fun invoke(): AppResult<DamValidation> }
class ValidateDam(private val repository: PaymentRepository) : ValidateDamUseCase {
    override suspend fun invoke() = appResultOf { repository.validateDam() }
}

fun interface SubmitCardPaymentUseCase { suspend operator fun invoke(request: CardPaymentRequest): AppResult<CardSubmissionOutcome> }
class SubmitCardPayment(private val repository: PaymentRepository) : SubmitCardPaymentUseCase {
    override suspend fun invoke(request: CardPaymentRequest) = appResultOf {
        require(request.paymentMethodId in setOf(PaymentMethod.MERCADO_PAGO_ID, PaymentMethod.RECARGA_PAY_ID)) {
            "Meio de pagamento inválido"
        }
        val expectedCvvLength = when (val selection = request.selection) {
            is PaymentCardSelection.Saved -> selection.card.securityCodeLength
            is PaymentCardSelection.New -> if (selection.draft.brand == "AMERICANEXPRESS") 4 else 3
        }
        require(request.securityCode.length == expectedCvvLength && request.securityCode.all(Char::isDigit)) {
            "Código de segurança inválido"
        }
        if (request.selection is PaymentCardSelection.New) {
            val draft = request.selection.draft
            require(draft.normalizedNumber.length in 14..16) { "Número do cartão inválido" }
            require(draft.holderName.isNotBlank()) { "Nome do titular inválido" }
            require(draft.expirationMonth in 1..12 && draft.expirationYear >= 2000) { "Validade do cartão inválida" }
        }
        repository.submitCardPayment(request)
    }
}

fun cardBrand(number: String): String {
    val digits = number.filter(Char::isDigit)
    return when {
        isElo(digits) -> "ELO"
        isMastercard(digits) -> "MASTERCARD"
        digits.startsWith("605032") -> "GRANDCARD"
        digits.startsWith("9792") -> "TROY"
        digits.startsWith("5067") -> "VERVE"
        digits.startsWith("604") || digits.startsWith("637") -> "CABAL"
        digits.startsWith("6062") || digits.startsWith("384") -> "HIPERCARD"
        digits.startsWith("34") || digits.startsWith("37") -> "AMERICANEXPRESS"
        listOf("30", "36", "38", "39").any(digits::startsWith) -> "DINERS"
        digits.startsWith("35") -> "JCB"
        digits.startsWith("6011") || digits.startsWith("65") -> "DISCOVER"
        digits.startsWith("4") -> "VISA"
        else -> "VISA"
    }
}

private fun isMastercard(number: String): Boolean {
    val prefix2 = number.take(2).toIntOrNull()
    val prefix4 = number.take(4).toIntOrNull()
    return prefix2 in 51..55 ||
        prefix4 in 2221..2720 ||
        listOf("5031", "5081", "5021", "5899", "5063").any(number::startsWith)
}

private val eloBinPattern = Regex(
    "^(509(508|509|518|519|918|921|926|929|932|935|938|940|946|948|951|952|957|961|963|971|975|978|979|980|983|986|996|999|" +
        "900|920|924|925|927|930|937|939|943|947|949|950|955|956|958|959|960|972|973|974|977|982|984|998|919|922|923|928|931|" +
        "933|934|936|941|942|944|945|953|954|962|964|976|981|985|995|997)|" +
        "650(948|949|953|955|956|957|960|968|971|946|952|961|962|966|973|977|947|950|951|954|958|959|963|964|965|967|970|972|" +
        "974|975|976|978|058|059|048)|" +
        "(4011(78|79)|431274|438935|451416|45(7393|7631|7632)|5067(00|01|02|04|05|06|08|10|11|12|14|15|16|17|18|19|20|24|25|26|27|28|" +
        "29|30|31|32|33|37|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|60|61|62|63|64|65|70|71|72|73|74|75|77|78)|5090(00|01|" +
        "02|04|05|06|07|09|10|11|12|13|14|15|16|17|18|19|20|21|22|24|25|26|27|28|29)|509(0[3-9][0-9]|[1-4][0-9]{2}|5[0-2][0-7]|" +
        "5[2-9][0-9]|6[0-9][0-9]|7[0-9][0-9]|8[0-7][0-9]|88[0-7]|8[8-9][0-9]|9[0-9]{3})|636(297|368)|650(4[0-9]{2}|5[0-9]{2}|" +
        "6[0-9]{2}|7[0-9]{2}|8[0-9]{2}|9[0-3][0-9]|9[4-9]|580|581|582|583)|651(6[5-9][0-9]|[7-9][0-9]{2})|655(0[0-9]{2}|1[0-9]{2}|" +
        "2[0-9]{2}|3[0-9]{2}|4[0-9]{2}|5[0-9]{2}|6[0-9]{2}|7[0-9]{2}|8[0-9]{2}|9[0-9]{2})|6500580[1-3]))$",
)

private fun isElo(number: String): Boolean = number.length >= 6 && eloBinPattern.matches(number.take(6))
