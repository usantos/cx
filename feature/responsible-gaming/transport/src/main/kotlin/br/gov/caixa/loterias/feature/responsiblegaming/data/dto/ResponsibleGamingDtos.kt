package br.gov.caixa.loterias.feature.responsiblegaming.data.dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class SelfAssessmentPayloadDto(
    @SerializedName("retornoPerguntaResposta") val questions: List<SelfAssessmentQuestionDto>? = null,
)

data class SelfAssessmentQuestionDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("descricaoPergunta") val description: String? = null,
    @SerializedName("ordemPergunta") val order: Int? = null,
    @SerializedName("ativo") val active: Boolean? = null,
)

data class SelfAssessmentAnswerDto(
    @SerializedName("idPergunta") val questionId: Int,
    @SerializedName("descricaoResposta") val description: String,
    @SerializedName("indicadorResposta") val affirmative: Boolean,
)

data class SocialTransfersDto(
    @SerializedName("arrecadacaoAcumulada") val accumulatedRevenue: BigDecimal? = null,
    @SerializedName("arrecadacaoUltimaSemana") val lastWeekRevenue: BigDecimal? = null,
    @SerializedName("dataBase") val baseDate: String? = null,
    @SerializedName("percentualProgramasSociais") val socialProgramsPercentage: BigDecimal? = null,
    @SerializedName("percentualEducacao") val educationPercentage: BigDecimal? = null,
    @SerializedName("percentualCultura") val culturePercentage: BigDecimal? = null,
    @SerializedName("percentualEsporte") val sportPercentage: BigDecimal? = null,
    @SerializedName("percentualSeguranca") val securityPercentage: BigDecimal? = null,
    @SerializedName("percentualSeguridade") val socialSecurityPercentage: BigDecimal? = null,
    @SerializedName("percentualOutros") val otherPercentage: BigDecimal? = null,
)
