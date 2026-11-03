package br.gov.caixa.loterias.feature.support.presentation.mapper

internal fun String.legacyCapitalized(): String = lowercase().replaceFirstChar { it.titlecase() }
