package br.gov.caixa.loterias.core.domain.results

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf

fun interface GetLatestResultUseCase {
    suspend operator fun invoke(modality: String): AppResult<DrawResult>
}

class GetLatestResult(
    private val repository: ResultsRepository,
) : GetLatestResultUseCase {
    override suspend fun invoke(modality: String) = appResultOf {
        repository.latest(modality)
    }
}

fun interface GetResultByContestUseCase {
    suspend operator fun invoke(modality: String, contest: Int): AppResult<DrawResult>
}

class GetResultByContest(
    private val repository: ResultsRepository,
) : GetResultByContestUseCase {
    override suspend fun invoke(modality: String, contest: Int) = appResultOf {
        repository.byContest(modality, contest)
    }
}

fun interface GetConfirmedBetResultsUseCase {
    suspend operator fun invoke(bet: br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet): br.gov.caixa.loterias.core.domain.service.ServiceResult<List<DrawResult>>
}

class GetConfirmedBetResults(private val repository: ResultsRepository) : GetConfirmedBetResultsUseCase {
    override suspend fun invoke(bet: br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet) = repository.forConfirmedBet(bet)
}
