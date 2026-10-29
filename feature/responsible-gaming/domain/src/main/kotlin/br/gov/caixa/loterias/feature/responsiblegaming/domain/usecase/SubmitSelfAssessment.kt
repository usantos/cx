package br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentAnswer
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.responsiblegaming.domain.repository.ResponsibleGamingRepository

class SubmitSelfAssessment(private val repository: ResponsibleGamingRepository) : SubmitSelfAssessmentUseCase {
    override suspend fun invoke(answers: List<SelfAssessmentAnswer>) = appResultOf {
        require(answers.isNotEmpty()) { messageText("respostas_obrigatorias_a68d44") }
        require(answers.map(SelfAssessmentAnswer::questionId).distinct().size == answers.size) {
            "Perguntas duplicadas"
        }
        repository.submitAssessment(answers)
    }
}
