package br.gov.caixa.loterias.feature.retail.domain.model

import java.math.BigDecimal

data class PrizeAward(
    val contest: Int,
    val description: String,
    val amount: BigDecimal?,
    val status: String = "",
    val hitCount: Int? = null,
    val draw: Int? = null,
    /** False somente para marcadores concursosNaoPremiado; preserva os registros recebidos. */
    val isAwarded: Boolean = true,
)
