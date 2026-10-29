package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.betting.domain.repository.BettingRepository

class GetBettingParameters(private val repository: BettingRepository) : GetBettingParametersUseCase {
    override suspend fun invoke() = appResultOf { repository.parameters() }
}
