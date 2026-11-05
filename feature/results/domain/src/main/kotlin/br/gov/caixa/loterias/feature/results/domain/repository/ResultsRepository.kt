package br.gov.caixa.loterias.feature.results.domain.repository

import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

interface ResultsRepository {
    suspend fun latest(modality: String): DrawResult
    suspend fun byContest(modality: String, contest: Int): DrawResult
    suspend fun forConfirmedBet(bet: ConfirmedBet): br.gov.caixa.loterias.core.domain.service.model.ServiceResult<List<DrawResult>> =
        br.gov.caixa.loterias.core.domain.service.model.ServiceResult.Success(
            (bet.contest until bet.contest + bet.recurringDraws.coerceAtLeast(1)).map { byContest(bet.modality, it) },
        )
}
