package br.gov.caixa.loterias.feature.support.domain.model

data class SupportSection(
    val id: Long,
    val title: String,
    val questions: List<SupportQuestion>,
)
