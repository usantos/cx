package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.cart.domain.model.CheckoutLocationStatus

fun interface CheckCheckoutLocationUseCase {
    suspend operator fun invoke(): AppResult<CheckoutLocationStatus>
}
