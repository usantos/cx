package br.gov.caixa.loterias.feature.payment.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.payment.domain.repository.PaymentRepository

class ValidateDam(private val repository: PaymentRepository) : ValidateDamUseCase {
    override suspend fun invoke() = appResultOf { repository.validateDam() }
}
