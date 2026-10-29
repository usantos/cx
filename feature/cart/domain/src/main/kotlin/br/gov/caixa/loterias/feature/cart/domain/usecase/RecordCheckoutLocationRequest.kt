package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.cart.domain.port.CheckoutLocationSource

class RecordCheckoutLocationRequest(private val source: CheckoutLocationSource) : RecordCheckoutLocationRequestUseCase {
    override suspend fun invoke() = appResultOf { source.recordPermissionRequest() }
}
