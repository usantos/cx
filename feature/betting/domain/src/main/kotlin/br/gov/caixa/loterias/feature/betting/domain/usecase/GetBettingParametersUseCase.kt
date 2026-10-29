package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters

fun interface GetBettingParametersUseCase {
    suspend operator fun invoke(): AppResult<BettingParameters>
}
