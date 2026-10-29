package br.gov.caixa.loterias.feature.results.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

fun interface GetResultByContestUseCase {
    suspend operator fun invoke(modality: String, contest: Int): AppResult<DrawResult>
}
