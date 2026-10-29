package br.gov.caixa.loterias.feature.results.domain.model

data class DrawSportsMatch(
    val number: Int?, val home: DrawSportsTeam, val away: DrawSportsTeam,
    val draw: Boolean? = null, val homeScoreIndex: Int? = null, val awayScoreIndex: Int? = null,
    val legendLetter: String = "", val legendDescription: String = "",
)
