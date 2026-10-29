package br.gov.caixa.loterias.feature.payment.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.payment.domain.repository.PaymentRepository

class GetPaymentMethods(private val repository: PaymentRepository) : GetPaymentMethodsUseCase {
    override suspend fun invoke() = appResultOf { repository.methods().filter { it.id > 0 } }
}
