package br.gov.caixa.loterias.feature.support.data.dto

import com.google.gson.annotations.SerializedName

data class SupportSectionDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nome") val name: String? = null,
    @SerializedName("duvidas") val questions: List<SupportQuestionDto>? = null,
)

data class SupportQuestionDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("pergunta") val question: String? = null,
    @SerializedName("resposta") val answer: String? = null,
)

data class TermsDto(
    @SerializedName("conteudo") val content: String? = null,
)
