package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryRequest
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision

fun interface PrepareBetEntryUseCase {
    suspend operator fun invoke(request: BetEntryRequest): AppResult<BetEntryDecision>

    /** Resultados de apostas históricas solicitam o concurso atual, e não o histórico. */
    suspend operator fun invoke(modalityId: Int): AppResult<BetEntryDecision> = invoke(BetEntryRequest(modalityId))
}
