package br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentQuestion

fun interface GetSelfAssessmentQuestionsUseCase {
    suspend operator fun invoke(): AppResult<List<SelfAssessmentQuestion>>
}
