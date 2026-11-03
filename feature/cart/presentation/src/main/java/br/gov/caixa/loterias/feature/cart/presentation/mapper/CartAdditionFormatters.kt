package br.gov.caixa.loterias.feature.cart.presentation.mapper

fun String.toDisplayModality(): String = trim().lowercase()
    .split('_', '-', ' ')
    .filter(String::isNotBlank)
    .joinToString("-")
