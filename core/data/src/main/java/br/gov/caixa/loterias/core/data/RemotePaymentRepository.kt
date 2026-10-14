package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.payment.PaymentMethod
import br.gov.caixa.loterias.core.domain.payment.PaymentRepository
import br.gov.caixa.loterias.core.domain.payment.DamValidation
import br.gov.caixa.loterias.core.domain.payment.CardPaymentRequest
import br.gov.caixa.loterias.core.domain.payment.CardSubmissionOutcome
import br.gov.caixa.loterias.core.domain.payment.PaymentCardSelection
import br.gov.caixa.loterias.core.domain.payment.PurchaseSubmission
import br.gov.caixa.loterias.core.domain.payment.PixCharge
import br.gov.caixa.loterias.core.domain.payment.PurchaseProgress
import br.gov.caixa.loterias.core.domain.payment.PurchasePolling
import br.gov.caixa.loterias.core.domain.payment.PurchaseRedirect
import br.gov.caixa.loterias.core.domain.payment.PurchaseNotice
import br.gov.caixa.loterias.core.domain.payment.PurchaseNoticeReason
import br.gov.caixa.loterias.core.network.PurchaseHttpException
import br.gov.caixa.loterias.core.network.PurchaseErrorEnvelopeDto
import br.gov.caixa.loterias.core.domain.payment.SavedPaymentCard
import br.gov.caixa.loterias.core.network.LegacyBackendApiProvider
import br.gov.caixa.loterias.core.network.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.PaymentApiProvider
import br.gov.caixa.loterias.core.network.PurchaseBackendApiProvider
import br.gov.caixa.loterias.core.network.PurchaseEnvelopeDto
import br.gov.caixa.loterias.core.network.PurchaseBackendResponse
import br.gov.caixa.loterias.core.network.UserProfileDto
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CancellationException

interface PaymentRemoteDataSource {
    suspend fun methods(): JsonElement?
    suspend fun cards(paymentMethodId: Long): JsonElement?
    suspend fun deleteCard(cardId: String, paymentMethodId: Long): JsonElement?
    suspend fun generatePix(body: JsonElement): JsonElement?
    suspend fun purchaseProgress(purchaseId: Long): PurchaseBackendResponse
    suspend fun validateDam(): JsonElement?
    suspend fun paymentMethod(paymentMethodId: Long): JsonElement?
    suspend fun profile(): UserProfileDto?
    suspend fun recargaPayCustomerToken(): String?
    suspend fun mercadoPagoInstallments(bin: String, publicKey: String): JsonElement
    suspend fun mercadoPagoCardToken(publicKey: String, body: JsonElement): JsonElement
    suspend fun recargaPayCreateCard(token: String, body: JsonElement): JsonElement
    suspend fun recargaPayAuthorizeCard(cardToken: String, token: String, body: JsonElement): JsonElement
    suspend fun registerCardPurchase(body: JsonElement): PurchaseBackendResponse
}

class RetrofitPaymentRemoteDataSource(
    private val provider: LegacyBackendApiProvider,
    private val loteriasProvider: LoteriasApiProvider,
    private val paymentProvider: PaymentApiProvider,
    private val purchaseProvider: PurchaseBackendApiProvider,
) : PaymentRemoteDataSource {
    override suspend fun methods() = provider.current().paymentMethods().payload
    override suspend fun cards(paymentMethodId: Long) = provider.current().cards(paymentMethodId.toString()).payload
    override suspend fun deleteCard(cardId: String, paymentMethodId: Long) =
        provider.current().deleteCard(cardId, paymentMethodId.toString()).payload
    override suspend fun generatePix(body: JsonElement) = provider.current().generatePixCharge(body).payload
    override suspend fun purchaseProgress(purchaseId: Long) = purchaseProvider.current().progress(purchaseId)
    override suspend fun validateDam() = provider.current().validateDam().payload
    override suspend fun paymentMethod(paymentMethodId: Long) = provider.current().paymentMethod(paymentMethodId).payload
    override suspend fun profile() = loteriasProvider.current().currentUserProfile().payload
    override suspend fun recargaPayCustomerToken() = provider.current().recargaPayCustomerToken().payload
    override suspend fun mercadoPagoInstallments(bin: String, publicKey: String) =
        paymentProvider.current().mercadoPago.installments(bin, publicKey)
    override suspend fun mercadoPagoCardToken(publicKey: String, body: JsonElement) =
        paymentProvider.current().mercadoPago.createCardToken(publicKey, body)
    override suspend fun recargaPayCreateCard(token: String, body: JsonElement) =
        paymentProvider.current().recargaPay.createCard("Bearer $token", body = body)
    override suspend fun recargaPayAuthorizeCard(cardToken: String, token: String, body: JsonElement) =
        paymentProvider.current().recargaPay.authorizeSavedCard(cardToken, "Bearer $token", body = body)
    override suspend fun registerCardPurchase(body: JsonElement) = purchaseProvider.current().submit(body)
}

class RemotePaymentRepository(private val remote: PaymentRemoteDataSource) : PaymentRepository {
    override suspend fun methods(): List<PaymentMethod> = remote.methods().objects().map { value ->
        PaymentMethod(
            id = value.long("id"),
            purchaseName = value.text("nomePagamentoCompra"),
            prizeName = value.text("nomePagamentoPremio"),
            default = value.boolean("padrao"),
            publicKey = value.text("publicKey"),
        )
    }

    override suspend fun cards(paymentMethodId: Long): List<SavedPaymentCard> =
        remote.cards(paymentMethodId).toCards(paymentMethodId)

    override suspend fun deleteCard(cardId: String, paymentMethodId: Long): List<SavedPaymentCard> =
        remote.deleteCard(cardId, paymentMethodId).toCards(paymentMethodId)

    override suspend fun generatePix(): PixCharge {
        val request = JsonObject().apply {
            addProperty("meioPagamento", PaymentMethod.PIX_ID)
            add("parametrosPagamento", null)
            addProperty("salvarCarrinhoFavorito", false)
            addProperty("nomeCarrinhoFavorito", "")
        }
        val value = requireNotNull(remote.generatePix(request)?.takeIf { it.isJsonObject }?.asJsonObject) {
            "Cobrança Pix sem payload"
        }
        return PixCharge(
            copyPaste = value.text("pixCopiaECola"),
            transactionId = value.text("txId"),
            qrCodeBase64 = value.text("qrcodeArrByte"),
            createdAt = value.text("dataHoraCriacao"),
            expiresAt = value.text("dataHoraExpiracao"),
            purchaseId = value.long("idCompra"),
            amount = value.decimal("valorCobranca"),
        )
    }

    override suspend fun purchaseProgress(purchaseId: Long): PurchaseProgress {
        val response = try {
            remote.purchaseProgress(purchaseId)
        } catch (failure: PurchaseHttpException) {
            failure.purchaseErrorRedirect()?.let { return PurchaseProgress(purchaseId, 0, redirect = it) }
            failure.purchaseErrorNotice()?.let { return PurchaseProgress(purchaseId, 0, notice = it) }
            throw failure
        }
        purchaseRedirect(response.envelope.redirect)?.let { destination ->
            return PurchaseProgress(response.envelope.payload?.id?.takeIf { it > 0 } ?: purchaseId, 0, redirect = destination)
        }
        response.registrationNotice()?.let { return PurchaseProgress(purchaseId, 0, notice = it) }
        val envelope = response.envelope.validated()
        val value = envelope.payload
        require(value?.id?.takeIf { it > 0 }?.let { it == purchaseId } != false) {
            "Resposta de processamento pertence a outra compra"
        }
        val nextPoll = if (response.processing) {
            requireNotNull(value) { "Compra sem payload de processamento" }
            PurchasePolling(
                requireNotNull(value.intervalMillis) { "Compra sem periodicidade de consulta" }.coerceAtLeast(0),
                requireNotNull(value.repetitions) { "Compra sem limite de consultas" }.coerceAtLeast(0),
            )
        } else null
        return PurchaseProgress(value?.id?.takeIf { it > 0 } ?: purchaseId, value?.status ?: 0, nextPoll)
    }

    override suspend fun validateDam(): DamValidation {
        val value = remote.validateDam()?.takeIf { it.isJsonObject }?.asJsonObject
        return DamValidation(
            blocked = value?.boolean("payload") == true,
            message = value?.text("mensagem").orEmpty(),
        )
    }

    override suspend fun submitCardPayment(request: CardPaymentRequest): CardSubmissionOutcome {
        val tokenAndMethod = when (request.paymentMethodId) {
            PaymentMethod.MERCADO_PAGO_ID -> mercadoPagoToken(request)
            PaymentMethod.RECARGA_PAY_ID -> recargaPayToken(request)
            else -> error("Meio de pagamento não suportado")
        }
        val saved = request.selection is PaymentCardSelection.Saved
        val save = (request.selection as? PaymentCardSelection.New)?.draft?.save
        val body = JsonObject().apply {
            addProperty("meioPagamento", request.paymentMethodId)
            add("parametrosPagamento", JsonObject().apply {
                addProperty("TOKEN", tokenAndMethod.first)
                if (saved) addProperty("SAVED_CARD", "true") else add("SAVED_CARD", JsonNull.INSTANCE)
                addProperty("PAYMENT_METHOD_ID", tokenAndMethod.second)
                if (save != null) addProperty("SAVE_CARD", save.toString()) else add("SAVE_CARD", JsonNull.INSTANCE)
            })
        }
        // Tokenization/preparation above cannot register a purchase through this endpoint.
        // Once submission starts, a lost/invalid reply cannot prove that the purchase failed.
        var knownPurchaseId: Long? = null
        return try {
            val response = remote.registerCardPurchase(body)
            val envelope = response.envelope
            knownPurchaseId = envelope.payload?.id?.takeIf { it > 0 }
            purchaseRedirect(envelope.redirect)?.let { destination ->
                return CardSubmissionOutcome.RedirectRequired(destination, knownPurchaseId)
            }
            response.registrationNotice()?.let { return CardSubmissionOutcome.NoticeRequired(it) }
            val value = requireNotNull(envelope.validated().payload) { "Compra sem payload de processamento" }
            CardSubmissionOutcome.Submitted(PurchaseSubmission(
                purchaseId = requireNotNull(knownPurchaseId) { "Compra sem identificador válido" },
                pollingIntervalMillis = requireNotNull(value.intervalMillis) { "Compra sem periodicidade de consulta" }.coerceAtLeast(0),
                pollingAttempts = requireNotNull(value.repetitions) { "Compra sem limite de consultas" }.coerceAtLeast(0),
                status = value.status ?: 0,
            ))
        } catch (failure: PurchaseHttpException) {
            failure.purchaseErrorRedirect()?.let { CardSubmissionOutcome.RedirectRequired(it, knownPurchaseId) }
                ?: failure.purchaseErrorNotice()?.let(CardSubmissionOutcome::NoticeRequired)
                ?: CardSubmissionOutcome.ReviewRequired(knownPurchaseId)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            // Never log/persist card data, payment tokens, error bodies or raw exceptions here.
            CardSubmissionOutcome.ReviewRequired(knownPurchaseId)
        }
    }

    private fun PurchaseEnvelopeDto.validated(): PurchaseEnvelopeDto {
        // A business redirect is not an HTTP URL and must never be treated as payment success.
        // Navigation for non-processing directives is audited separately from this transport.
        require(redirect == null || redirect == "/carrinhos/verifica-compra-processamento") {
            "Consulte Minhas Compras para acompanhar esta compra."
        }
        require(code == null) { message?.takeIf(String::isNotBlank) ?: "Não foi possível processar a compra." }
        return this
    }

    private fun purchaseRedirect(value: String?): PurchaseRedirect? = when (value) {
        "login" -> PurchaseRedirect.LOGIN
        "aceitar-termo-de-uso" -> PurchaseRedirect.ACCEPT_TERMS
        "cadastrar-apostador" -> PurchaseRedirect.REGISTER_ACCOUNT
        "indisponibilidade" -> PurchaseRedirect.UNAVAILABLE
        else -> null
    }

    /** Error redirects take priority over the status fallback, as in checkRedirectCompraAsync. */
    private fun PurchaseHttpException.purchaseErrorRedirect(): PurchaseRedirect? {
        val directive = error?.redirect
        return when (directive) {
            "carrinho" -> PurchaseRedirect.CART
            "carrinhos-favoritos" -> PurchaseRedirect.FAVORITE_CARTS
            null -> PurchaseRedirect.LOGIN.takeIf { statusCode == 401 }
            else -> purchaseRedirect(directive)
        }
    }

    private fun PurchaseHttpException.purchaseErrorNotice(): PurchaseNotice? {
        val metadata = error ?: return null
        if (metadata.redirect != null) return null
        val reason = when (statusCode) {
            400, 404 -> when (metadata.code) {
                "011010", "011011", "011012" -> PurchaseNoticeReason.MORE_BETS_REQUIRED
                "018003" -> PurchaseNoticeReason.MERCADO_PAGO_REGISTRATION
                else -> PurchaseNoticeReason.BUSINESS_RULE
            }
            500 -> when {
                metadata.code == "2067" -> PurchaseNoticeReason.INVALID_IDENTIFICATION
                metadata.type == "1" -> PurchaseNoticeReason.SERVER_MESSAGE
                else -> PurchaseNoticeReason.SERVER_FAILURE
            }
            else -> if (metadata.code == "018003") PurchaseNoticeReason.MERCADO_PAGO_REGISTRATION else return null
        }
        val message = if (statusCode == 408) "Tempo de resposta esgotou" else metadata.message
        return PurchaseNotice(reason, message, metadata.code)
    }

    private fun PurchaseBackendResponse.registrationNotice(): PurchaseNotice? =
        if (processing && envelope.code == "018003" && envelope.redirect == null) {
            PurchaseHttpException(303, PurchaseErrorEnvelopeDto(code = envelope.code, message = envelope.message)).purchaseErrorNotice()
        } else null

    private suspend fun mercadoPagoToken(request: CardPaymentRequest): Pair<String, String> {
        val method = requireNotNull(remote.paymentMethod(PaymentMethod.MERCADO_PAGO_ID)?.asJsonObject) {
            "Configuração do Mercado Pago indisponível"
        }
        val publicKey = method.text("publicKey")
        require(publicKey.isNotBlank()) { "Chave pública do Mercado Pago indisponível" }
        val profile = requireNotNull(remote.profile()) { "Dados do apostador indisponíveis" }
        val selection = request.selection
        val bin = when (selection) {
            is PaymentCardSelection.New -> selection.draft.normalizedNumber.take(7)
            is PaymentCardSelection.Saved -> selection.card.firstDigits
        }
        val installments = remote.mercadoPagoInstallments(bin, publicKey).objects().firstOrNull()
        val paymentMethodCode = installments?.text("payment_method_id").orEmpty().ifBlank {
            (selection as? PaymentCardSelection.Saved)?.card?.paymentMethodCode.orEmpty()
        }
        require(paymentMethodCode.isNotBlank()) { "Bandeira do cartão não identificada" }
        val tokenRequest = JsonObject().apply {
            when (selection) {
                is PaymentCardSelection.New -> {
                    addProperty("card_number", selection.draft.normalizedNumber)
                    addProperty("expiration_year", selection.draft.expirationYear)
                    addProperty("expiration_month", selection.draft.expirationMonth)
                }
                is PaymentCardSelection.Saved -> {
                    addProperty("card_id", selection.card.id)
                    addProperty("expiration_year", selection.card.expirationYear)
                    addProperty("expiration_month", selection.card.expirationMonth)
                }
            }
            addProperty("security_code", request.securityCode)
            add("cardholder", JsonObject().apply {
                if (selection is PaymentCardSelection.New) addProperty("name", selection.draft.holderName)
                add("identification", JsonObject().apply {
                    addProperty("type", "CPF")
                    addProperty("number", profile.cpf.orEmpty())
                })
            })
        }
        val token = remote.mercadoPagoCardToken(publicKey, tokenRequest).asJsonObject
        require(token.get("luhn_validation")?.asBoolean != false) { "Número do cartão inválido" }
        val tokenId = token.text("id")
        require(tokenId.isNotBlank()) { "Token do cartão indisponível" }
        return tokenId to paymentMethodCode
    }

    private suspend fun recargaPayToken(request: CardPaymentRequest): Pair<String, String> {
        val customerToken = requireNotNull(remote.recargaPayCustomerToken()).takeIf(String::isNotBlank)
            ?: error("Token do RecargaPay indisponível")
        val response = when (val selection = request.selection) {
            is PaymentCardSelection.Saved -> remote.recargaPayAuthorizeCard(
                selection.card.customerToken,
                customerToken,
                JsonObject().apply { addProperty("securityCode", request.securityCode) },
            )
            is PaymentCardSelection.New -> {
                val profile = requireNotNull(remote.profile()) { "Dados do apostador indisponíveis" }
                remote.recargaPayCreateCard(customerToken, JsonObject().apply {
                    addProperty("type", "CREDIT")
                    addProperty("brand", selection.draft.brand)
                    addProperty("number", selection.draft.normalizedNumber)
                    add("cardHolder", JsonObject().apply {
                        addProperty("type", "NATURAL")
                        addProperty("name", profile.name.orEmpty())
                        addProperty("email", profile.email.orEmpty())
                        addProperty("document", profile.cpf.orEmpty())
                        addProperty("birthdate", recargaPayBirthdate(profile.birthDate))
                        add("phones", com.google.gson.JsonArray().apply {
                            add(listOfNotNull(profile.phone?.areaCode, profile.phone?.number).joinToString("").ifBlank { "+55999999999" })
                        })
                    })
                    add("expirationDate", JsonObject().apply {
                        addProperty("month", selection.draft.expirationMonth)
                        addProperty("year", selection.draft.expirationYear)
                    })
                    addProperty("securityCode", request.securityCode)
                    addProperty("shouldSave", selection.draft.save)
                })
            }
        }.asJsonObject
        val cardToken = response.text("customerCardToken")
        val brand = response.text("brand").lowercase()
        require(cardToken.isNotBlank()) { "Token do cartão indisponível" }
        require(brand.isNotBlank()) { "Bandeira do cartão não identificada" }
        return cardToken to brand
    }

    @Suppress("DEPRECATION")
    private fun recargaPayBirthdate(value: String?): String {
        if (value.isNullOrEmpty()) return ""
        // ISO dates are calendar dates; preserve them without a time zone conversion.
        runCatching { LocalDate.parse(value) }.getOrNull()?.let { return it.toString() }
        // CX DataUtil accepts Date(String) representations from the profile service.
        return SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date(value))
    }

    private fun JsonElement?.toCards(paymentMethodId: Long) = objects().map { value ->
        SavedPaymentCard(
            id = value.text("idCartao").ifBlank { value.long("idCartao").toString() },
            paymentMethodId = paymentMethodId,
            firstDigits = value.text("primeirosDigitos"),
            lastDigits = value.text("ultimosDigitos"),
            expirationMonth = value.int("mesValidade"),
            expirationYear = value.int("anoValidade"),
            securityCodeLength = value.int("tamanhoCodigoSeguranca").takeIf { it > 0 } ?: 3,
            brand = value.text("nomeMetodoPagamento"),
            paymentMethodCode = value.text("idMetodoPagamento"),
            customerToken = value.text("tokenCartao"),
        )
    }

    private fun JsonElement?.objects(): List<JsonObject> = when {
        this == null || isJsonNull -> emptyList()
        isJsonArray -> asJsonArray.mapNotNull { it.takeIf(JsonElement::isJsonObject)?.asJsonObject }
        isJsonObject -> listOf(asJsonObject)
        else -> emptyList()
    }

    private fun JsonObject.text(name: String): String = get(name)?.takeIf { !it.isJsonNull && it.isJsonPrimitive }?.asString.orEmpty()
    private fun JsonObject.long(name: String): Long = get(name)?.takeIf { !it.isJsonNull && it.isJsonPrimitive }?.runCatching { asLong }?.getOrDefault(0) ?: 0
    private fun JsonObject.int(name: String): Int = long(name).toInt()
    private fun JsonObject.boolean(name: String): Boolean = get(name)?.takeIf { !it.isJsonNull && it.isJsonPrimitive }?.runCatching { asBoolean }?.getOrDefault(false) ?: false
    private fun JsonObject.decimal(name: String): BigDecimal = get(name)?.takeIf { !it.isJsonNull && it.isJsonPrimitive }?.runCatching { asBigDecimal }?.getOrDefault(BigDecimal.ZERO) ?: BigDecimal.ZERO
}
