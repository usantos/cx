package br.gov.caixa.loterias.core.domain.configuration.model

/** Números de concursos configurados em ParametrosConfiguraveisDTO; valores ausentes nunca selecionam uma substituição. */
data class HomeSpecialContests(
    val megaThirtyYearsContest: Int? = null,
    val lotecaFathersDayContest: Int? = null,
)
