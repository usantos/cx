package br.gov.caixa.loterias.feature.responsiblegaming.data.datasource.remote

import br.gov.caixa.loterias.core.network.api.LoteriasApi
import br.gov.caixa.loterias.core.network.dto.account.UserProfileDto
import br.gov.caixa.loterias.core.network.dto.responsiblegaming.AccountSuspensionDto
import br.gov.caixa.loterias.core.network.dto.responsiblegaming.SelfAssessmentAnswerDto
import br.gov.caixa.loterias.core.network.dto.responsiblegaming.SelfAssessmentPayloadDto
import br.gov.caixa.loterias.core.network.dto.responsiblegaming.SocialTransfersDto
import br.gov.caixa.loterias.core.network.dto.responsiblegaming.SuspendAccountRequestDto
import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import retrofit2.http.*

/** Contrato HTTP pertencente a esta funcionalidade; anotações de transporte e valores padrão originais são preservados. */
interface ResponsibleGamingLoteriasApi {
    @GET("autoavaliacoes/recuperar-perguntas")
    suspend fun selfAssessmentQuestions(): ApiEnvelope<SelfAssessmentPayloadDto>

    @POST("autoavaliacoes/grava-resposta")
    suspend fun submitSelfAssessment(
        @Body answers: List<SelfAssessmentAnswerDto>,
    ): ApiEnvelope<SelfAssessmentPayloadDto>

    @GET("repassometro")
    suspend fun socialTransfers(): ApiEnvelope<SocialTransfersDto>

    @GET("usuarios/recuperar-dados")
    suspend fun currentUserProfile(): ApiEnvelope<UserProfileDto>

    @POST("usuarios/suspender-conta-apostador")
    suspend fun suspendAccount(
        @Body request: SuspendAccountRequestDto,
    ): ApiEnvelope<AccountSuspensionDto>
}

/** O receptor é o retrato do cliente já selecionado para esta operação. */
internal fun LoteriasApi.featureResponsibleGamingApi(): ResponsibleGamingLoteriasApi = ResponsibleGamingLoteriasApiProjection(this)

private class ResponsibleGamingLoteriasApiProjection(private val transport: LoteriasApi) : ResponsibleGamingLoteriasApi {
    override suspend fun selfAssessmentQuestions(): ApiEnvelope<SelfAssessmentPayloadDto> = transport.selfAssessmentQuestions()

    override suspend fun submitSelfAssessment(
        @Body answers: List<SelfAssessmentAnswerDto>,
    ): ApiEnvelope<SelfAssessmentPayloadDto> = transport.submitSelfAssessment(answers = answers)

    override suspend fun socialTransfers(): ApiEnvelope<SocialTransfersDto> = transport.socialTransfers()

    override suspend fun currentUserProfile(): ApiEnvelope<UserProfileDto> = transport.currentUserProfile()

    override suspend fun suspendAccount(
        @Body request: SuspendAccountRequestDto,
    ): ApiEnvelope<AccountSuspensionDto> = transport.suspendAccount(request = request)
}
