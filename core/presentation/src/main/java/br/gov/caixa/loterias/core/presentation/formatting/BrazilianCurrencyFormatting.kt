package br.gov.caixa.loterias.core.presentation.formatting

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

fun formatBrazilianCurrency(value: BigDecimal): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(value)
