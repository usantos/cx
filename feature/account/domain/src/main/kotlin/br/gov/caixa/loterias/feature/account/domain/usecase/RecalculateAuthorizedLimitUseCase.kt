package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import java.math.BigDecimal

fun interface RecalculateAuthorizedLimitUseCase {
    suspend operator fun invoke(): AppResult<BigDecimal>
}
