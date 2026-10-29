package br.gov.caixa.loterias.feature.results.domain.model

data class UpcomingDrawContest(val number: Int, val date: String, val estimatedPrize: String, val special: Boolean = false)
