package br.gov.caixa.loterias.feature.payment.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.payment.domain.repository.PaymentRepository

class GeneratePixCharge(private val repository: PaymentRepository) : GeneratePixChargeUseCase {
    override suspend fun invoke() = appResultOf { repository.generatePix() }
}
