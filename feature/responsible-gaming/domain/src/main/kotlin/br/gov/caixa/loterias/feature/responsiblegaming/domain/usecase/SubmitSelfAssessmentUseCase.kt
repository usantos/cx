package br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentAnswer

fun interface SubmitSelfAssessmentUseCase {
    suspend operator fun invoke(answers: List<SelfAssessmentAnswer>): AppResult<Unit>
}
