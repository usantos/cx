package br.gov.caixa.loterias.core.domain.contract.cart.model

/** Resultados independentes permitem seleções simples, duplas e triplas na Loteca. */
data class CartLotecaSelection(
    val number: Int,
    val homeLabel: String,
    val awayLabel: String,
    val homeSelected: Boolean,
    val drawSelected: Boolean,
    val awaySelected: Boolean,
    val description: String = "",
)
