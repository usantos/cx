package br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentQuestion
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.responsiblegaming.domain.repository.ResponsibleGamingRepository

class GetSelfAssessmentQuestions(private val repository: ResponsibleGamingRepository) : GetSelfAssessmentQuestionsUseCase {
    override suspend fun invoke() = appResultOf {
        repository.assessmentQuestions()
            .filter { it.id > 0 && it.description.isNotBlank() }
            .sortedWith(compareBy(SelfAssessmentQuestion::order, SelfAssessmentQuestion::id))
            .also { require(it.isNotEmpty()) { messageText("autoavaliacao_sem_perguntas_84a6e1") } }
    }
}
