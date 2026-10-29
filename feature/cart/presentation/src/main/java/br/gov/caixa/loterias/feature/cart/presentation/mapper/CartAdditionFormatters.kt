package br.gov.caixa.loterias.feature.cart.presentation.mapper

internal fun String.toDisplayModality(): String = trim().lowercase()
    .split('_', '-', ' ')
    .filter(String::isNotBlank)
    .joinToString("-")
