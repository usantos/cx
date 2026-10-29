package br.gov.caixa.loterias.feature.results.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.results.domain.repository.ResultsRepository

class GetLatestResult(
    private val repository: ResultsRepository,
) : GetLatestResultUseCase {
    override suspend fun invoke(modality: String) = appResultOf {
        repository.latest(modality)
    }
}
