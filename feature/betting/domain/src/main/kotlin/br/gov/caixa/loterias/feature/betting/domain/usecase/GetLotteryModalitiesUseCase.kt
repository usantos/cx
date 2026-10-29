package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality

fun interface GetLotteryModalitiesUseCase {
    suspend operator fun invoke(): AppResult<List<LotteryModality>>
}
