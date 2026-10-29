package br.gov.caixa.loterias.feature.betting.presentation.mapper

import java.math.BigDecimal

internal fun formatRapidAmount(value: BigDecimal) = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.forLanguageTag("pt-BR")).format(value).replace('\u00a0', ' ')
