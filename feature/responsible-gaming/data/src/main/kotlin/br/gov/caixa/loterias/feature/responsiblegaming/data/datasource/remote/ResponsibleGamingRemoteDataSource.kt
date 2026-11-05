package br.gov.caixa.loterias.feature.responsiblegaming.data.datasource.remote

import br.gov.caixa.loterias.core.network.dto.account.UserProfileDto
import br.gov.caixa.loterias.core.network.dto.responsiblegaming.AccountSuspensionDto
import br.gov.caixa.loterias.core.network.dto.responsiblegaming.SelfAssessmentAnswerDto
import br.gov.caixa.loterias.core.network.dto.responsiblegaming.SelfAssessmentPayloadDto
import br.gov.caixa.loterias.core.network.dto.responsiblegaming.SocialTransfersDto
import br.gov.caixa.loterias.core.network.dto.responsiblegaming.SuspendAccountRequestDto

interface ResponsibleGamingRemoteDataSource {
    suspend fun assessmentQuestions(): SelfAssessmentPayloadDto?
    suspend fun submitAssessment(answers: List<SelfAssessmentAnswerDto>)
    suspend fun socialTransfers(): SocialTransfersDto?
    suspend fun currentUserProfile(): UserProfileDto?
    suspend fun suspendAccount(request: SuspendAccountRequestDto): AccountSuspensionDto?
}
