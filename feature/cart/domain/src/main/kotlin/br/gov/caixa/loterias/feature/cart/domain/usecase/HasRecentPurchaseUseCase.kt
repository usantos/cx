package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import java.math.BigDecimal

fun interface HasRecentPurchaseUseCase {
    suspend operator fun invoke(amount: BigDecimal): AppResult<Boolean>
}
