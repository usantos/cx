package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.betting.domain.repository.BettingRepository

class GetLotteryModalities(private val repository: BettingRepository) : GetLotteryModalitiesUseCase {
    override suspend fun invoke() = appResultOf { repository.modalities() }
}
