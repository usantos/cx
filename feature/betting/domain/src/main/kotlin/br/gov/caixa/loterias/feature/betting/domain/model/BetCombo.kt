package br.gov.caixa.loterias.feature.betting.domain.model

import java.math.BigDecimal

data class BetCombo(
    val id: Int,
    val name: String,
    val description: String,
    val new: Boolean,
    val amount: BigDecimal,
    val entries: List<BetComboEntry>,
)
