package br.gov.caixa.loterias.feature.responsiblegaming.domain.model

data class SelfAssessmentAnswer(
    val questionId: Int,
    val description: String,
    val affirmative: Boolean,
)
