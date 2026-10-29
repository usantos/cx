package br.gov.caixa.loterias.feature.results.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.results.domain.repository.ResultsRepository

class GetResultByContest(
    private val repository: ResultsRepository,
) : GetResultByContestUseCase {
    override suspend fun invoke(modality: String, contest: Int) = appResultOf {
        repository.byContest(modality, contest)
    }
}
