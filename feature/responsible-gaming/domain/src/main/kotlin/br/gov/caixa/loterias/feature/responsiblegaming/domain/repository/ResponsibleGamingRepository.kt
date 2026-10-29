package br.gov.caixa.loterias.feature.responsiblegaming.domain.repository

import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SocialTransfers
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentQuestion
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentAnswer
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.AccountSuspension

interface ResponsibleGamingRepository {
    suspend fun assessmentQuestions(): List<SelfAssessmentQuestion>
    suspend fun submitAssessment(answers: List<SelfAssessmentAnswer>)
    suspend fun socialTransfers(): SocialTransfers
    suspend fun suspendAccount(periodHours: Int): AccountSuspension
}
