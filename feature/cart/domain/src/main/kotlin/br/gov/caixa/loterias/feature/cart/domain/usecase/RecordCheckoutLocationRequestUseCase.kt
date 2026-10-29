package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult

fun interface RecordCheckoutLocationRequestUseCase {
    suspend operator fun invoke(): AppResult<Unit>
}
