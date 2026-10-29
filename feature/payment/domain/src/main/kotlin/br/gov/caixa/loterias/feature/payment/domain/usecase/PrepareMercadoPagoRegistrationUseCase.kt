package br.gov.caixa.loterias.feature.payment.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.payment.domain.model.MercadoPagoRegistrationResult

fun interface PrepareMercadoPagoRegistrationUseCase {
    suspend operator fun invoke(email: String): AppResult<MercadoPagoRegistrationResult>
}
