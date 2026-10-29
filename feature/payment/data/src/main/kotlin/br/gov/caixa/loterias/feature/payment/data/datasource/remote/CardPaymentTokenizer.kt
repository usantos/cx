package br.gov.caixa.loterias.feature.payment.data.datasource.remote

import br.gov.caixa.loterias.feature.payment.data.localizedMessage as messageText
import br.gov.caixa.loterias.feature.payment.data.mapper.paymentObjects
import br.gov.caixa.loterias.feature.payment.data.mapper.paymentText
import br.gov.caixa.loterias.feature.payment.domain.model.CardPaymentRequest
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentCardSelection
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import com.google.gson.JsonObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

/** Prepara tokens do processador; este colaborador nunca registra uma compra. */
internal class CardPaymentTokenizer(private val remote: PaymentRemoteDataSource) {
    suspend fun tokenize(request: CardPaymentRequest): Pair<String, String> = when (request.paymentMethodId) {
        PaymentMethod.MERCADO_PAGO_ID -> mercadoPagoToken(request)
        PaymentMethod.RECARGA_PAY_ID -> recargaPayToken(request)
        else -> error("Meio de pagamento não suportado")
    }

    private suspend fun mercadoPagoToken(request: CardPaymentRequest): Pair<String, String> {
        val method = requireNotNull(remote.paymentMethod(PaymentMethod.MERCADO_PAGO_ID)?.asJsonObject) {
            messageText("configuracao_do_mercado_pago_ind_b60829")
        }
        val publicKey = method.paymentText("publicKey")
        require(publicKey.isNotBlank()) { messageText("chave_publica_do_mercado_pago_in_5458c9") }
        val profile = requireNotNull(remote.profile()) { messageText("dados_do_apostador_indisponiveis_a42aa2") }
        val selection = request.selection
        val bin = when (selection) {
            is PaymentCardSelection.New -> selection.draft.normalizedNumber.take(7)
            is PaymentCardSelection.Saved -> selection.card.firstDigits
        }
        val installments = remote.mercadoPagoInstallments(bin, publicKey).paymentObjects().firstOrNull()
        val paymentMethodCode = installments?.paymentText("payment_method_id").orEmpty().ifBlank {
            (selection as? PaymentCardSelection.Saved)?.card?.paymentMethodCode.orEmpty()
        }
        require(paymentMethodCode.isNotBlank()) { messageText("bandeira_do_cartao_nao_identific_08cc16") }
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
            add(
                "cardholder",
                JsonObject().apply {
                    if (selection is PaymentCardSelection.New) addProperty("name", selection.draft.holderName)
                    add(
                        "identification",
                        JsonObject().apply {
                            addProperty("type", "CPF")
                            addProperty("number", profile.cpf.orEmpty())
                        }
                    )
                }
            )
        }
        val token = remote.mercadoPagoCardToken(publicKey, tokenRequest).asJsonObject
        require(token.get("luhn_validation")?.asBoolean != false) { messageText("numero_do_cartao_invalido_ce174e") }
        val tokenId = token.paymentText("id")
        require(tokenId.isNotBlank()) { messageText("token_do_cartao_indisponivel_497900") }
        return tokenId to paymentMethodCode
    }

    private suspend fun recargaPayToken(request: CardPaymentRequest): Pair<String, String> {
        val customerToken = requireNotNull(remote.recargaPayCustomerToken()).takeIf(String::isNotBlank)
            ?: error("Token do RecargaPay indisponível")
        val response = when (val selection = request.selection) {
            is PaymentCardSelection.Saved -> remote.recargaPayAuthorizeCard(
                selection.card.customerToken,
                customerToken,
                JsonObject().apply { addProperty("securityCode", request.securityCode) }
            )

            is PaymentCardSelection.New -> {
                val profile = requireNotNull(remote.profile()) { messageText("dados_do_apostador_indisponiveis_a42aa2") }
                remote.recargaPayCreateCard(
                    customerToken,
                    JsonObject().apply {
                        addProperty("type", "CREDIT")
                        addProperty("brand", selection.draft.brand)
                        addProperty("number", selection.draft.normalizedNumber)
                        add(
                            "cardHolder",
                            JsonObject().apply {
                                addProperty("type", "NATURAL")
                                addProperty("name", profile.name.orEmpty())
                                addProperty("email", profile.email.orEmpty())
                                addProperty("document", profile.cpf.orEmpty())
                                addProperty("birthdate", recargaPayBirthdate(profile.birthDate))
                                add(
                                    "phones",
                                    com.google.gson.JsonArray().apply {
                                        add(
                                            listOfNotNull(profile.phone?.areaCode, profile.phone?.number).joinToString("").ifBlank {
                                                "+55999999999"
                                            }
                                        )
                                    }
                                )
                            }
                        )
                        add(
                            "expirationDate",
                            JsonObject().apply {
                                addProperty("month", selection.draft.expirationMonth)
                                addProperty("year", selection.draft.expirationYear)
                            }
                        )
                        addProperty("securityCode", request.securityCode)
                        addProperty("shouldSave", selection.draft.save)
                    }
                )
            }
        }.asJsonObject
        val cardToken = response.paymentText("customerCardToken")
        val brand = response.paymentText("brand").lowercase()
        require(cardToken.isNotBlank()) { messageText("token_do_cartao_indisponivel_497900") }
        require(brand.isNotBlank()) { messageText("bandeira_do_cartao_nao_identific_08cc16") }
        return cardToken to brand
    }

    @Suppress("DEPRECATION")
    private fun recargaPayBirthdate(value: String?): String {
        if (value.isNullOrEmpty()) return ""
        // Datas ISO são datas de calendário; preserve-as sem conversão de fuso horário.
        runCatching { LocalDate.parse(value) }.getOrNull()?.let { return it.toString() }
        // DataUtil da CX aceita representações Date(String) do serviço de perfil.
        return SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date(value))
    }
}
