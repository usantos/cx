package br.gov.caixa.loterias.feature.retail.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionResult

fun interface RedeemPrizeUseCase {
    suspend operator fun invoke(betId: Long, channel: PrizeRedemptionChannel, value: String): AppResult<PrizeRedemptionResult>
}
