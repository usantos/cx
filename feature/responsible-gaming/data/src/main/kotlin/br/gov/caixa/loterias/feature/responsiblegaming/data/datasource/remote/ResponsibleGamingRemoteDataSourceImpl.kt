package br.gov.caixa.loterias.feature.responsiblegaming.data.datasource.remote

import br.gov.caixa.loterias.core.network.dto.responsiblegaming.SelfAssessmentAnswerDto
import br.gov.caixa.loterias.core.network.dto.responsiblegaming.SuspendAccountRequestDto
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider

class ResponsibleGamingRemoteDataSourceImpl(
    private val apiProvider: LoteriasApiProvider,
) : ResponsibleGamingRemoteDataSource {
    override suspend fun assessmentQuestions() = apiProvider.current().featureResponsibleGamingApi().selfAssessmentQuestions().payload

    override suspend fun submitAssessment(answers: List<SelfAssessmentAnswerDto>) {
        apiProvider.current().featureResponsibleGamingApi().submitSelfAssessment(answers)
    }

    override suspend fun socialTransfers() = apiProvider.current().featureResponsibleGamingApi().socialTransfers().payload

    override suspend fun suspendAccount(request: SuspendAccountRequestDto) =
        apiProvider.current().featureResponsibleGamingApi().suspendAccount(request).payload

    override suspend fun currentUserProfile() = apiProvider.current().featureResponsibleGamingApi().currentUserProfile().payload
}
