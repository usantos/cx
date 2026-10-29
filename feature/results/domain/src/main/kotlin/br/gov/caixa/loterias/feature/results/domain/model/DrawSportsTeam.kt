package br.gov.caixa.loterias.feature.results.domain.model

data class DrawSportsTeam(val name: String, val state: String = "", val score: String = "", val won: Boolean? = null, val shieldBase64: String? = null, val countryCode: String = "", val nationalTeam: Boolean? = null)
