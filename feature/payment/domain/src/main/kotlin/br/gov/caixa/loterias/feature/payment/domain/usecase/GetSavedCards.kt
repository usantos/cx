package br.gov.caixa.loterias.feature.payment.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.payment.domain.repository.PaymentRepository

class GetSavedCards(private val repository: PaymentRepository) : GetSavedCardsUseCase {
    override suspend fun invoke(paymentMethodId: Long) = appResultOf {
        require(
            paymentMethodId in setOf(
                PaymentMethod.MERCADO_PAGO_ID,
                PaymentMethod.RECARGA_PAY_ID
            )
        ) { messageText("meio_de_pagamento_invalido_eaf8a5") }
        repository.cards(paymentMethodId)
    }
}
