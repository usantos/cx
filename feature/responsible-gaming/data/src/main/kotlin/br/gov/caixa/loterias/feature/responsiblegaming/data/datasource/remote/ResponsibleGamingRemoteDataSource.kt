package br.gov.caixa.loterias.feature.responsiblegaming.data.datasource.remote

import br.gov.caixa.loterias.feature.responsiblegaming.data.dto.SelfAssessmentAnswerDto
import br.gov.caixa.loterias.feature.responsiblegaming.data.dto.SelfAssessmentPayloadDto
import br.gov.caixa.loterias.feature.responsiblegaming.data.dto.SocialTransfersDto
import br.gov.caixa.loterias.feature.responsiblegaming.data.dto.AccountSuspensionDto
import br.gov.caixa.loterias.feature.responsiblegaming.data.dto.SuspendAccountRequestDto
import br.gov.caixa.loterias.feature.account.data.dto.UserProfileDto

interface ResponsibleGamingRemoteDataSource {
    suspend fun assessmentQuestions(): SelfAssessmentPayloadDto?
    suspend fun submitAssessment(answers: List<SelfAssessmentAnswerDto>)
    suspend fun socialTransfers(): SocialTransfersDto?
    suspend fun currentUserProfile(): UserProfileDto?
    suspend fun suspendAccount(request: SuspendAccountRequestDto): AccountSuspensionDto?
}
