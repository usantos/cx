package br.gov.caixa.loterias.feature.payment.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.payment.domain.repository.PaymentRepository

class DeleteSavedCard(private val repository: PaymentRepository) : DeleteSavedCardUseCase {
    override suspend fun invoke(cardId: String, paymentMethodId: Long) = appResultOf {
        require(cardId.isNotBlank()) { messageText("cartao_invalido_4d214d") }
        require(
            paymentMethodId in setOf(
                PaymentMethod.MERCADO_PAGO_ID,
                PaymentMethod.RECARGA_PAY_ID
            )
        ) { messageText("meio_de_pagamento_invalido_eaf8a5") }
        repository.deleteCard(cardId, paymentMethodId)
    }
}
