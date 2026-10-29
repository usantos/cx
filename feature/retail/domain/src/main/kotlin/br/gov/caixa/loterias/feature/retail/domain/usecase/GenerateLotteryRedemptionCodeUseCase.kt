package br.gov.caixa.loterias.feature.retail.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.retail.domain.model.LotteryRedemptionCode

fun interface GenerateLotteryRedemptionCodeUseCase {
    suspend operator fun invoke(betId: Long): AppResult<LotteryRedemptionCode>
}
