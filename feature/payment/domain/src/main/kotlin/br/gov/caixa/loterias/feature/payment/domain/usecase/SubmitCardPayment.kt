package br.gov.caixa.loterias.feature.payment.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.payment.domain.model.CardPaymentRequest
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentCardSelection
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.payment.domain.repository.PaymentRepository

class SubmitCardPayment(private val repository: PaymentRepository) : SubmitCardPaymentUseCase {
    override suspend fun invoke(request: CardPaymentRequest) = appResultOf {
        require(request.paymentMethodId in setOf(PaymentMethod.MERCADO_PAGO_ID, PaymentMethod.RECARGA_PAY_ID)) {
            messageText("meio_de_pagamento_invalido_eaf8a5")
        }
        val expectedCvvLength = when (val selection = request.selection) {
            is PaymentCardSelection.Saved -> selection.card.securityCodeLength
            is PaymentCardSelection.New -> if (selection.draft.brand == "AMERICANEXPRESS") 4 else 3
        }
        require(request.securityCode.length == expectedCvvLength && request.securityCode.all(Char::isDigit)) {
            messageText("codigo_de_seguranca_invalido_130084")
        }
        if (request.selection is PaymentCardSelection.New) {
            val draft = request.selection.draft
            require(draft.normalizedNumber.length in 14..16) { messageText("numero_do_cartao_invalido_ce174e") }
            require(draft.holderName.isNotBlank()) { messageText("nome_do_titular_invalido_bb8e15") }
            require(draft.expirationMonth in 1..12 && draft.expirationYear >= 2000) { messageText("validade_do_cartao_invalida_0dacf3") }
        }
        repository.submitCardPayment(request)
    }
}
