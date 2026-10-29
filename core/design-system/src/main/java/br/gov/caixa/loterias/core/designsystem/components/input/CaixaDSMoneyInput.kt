package br.gov.caixa.loterias.core.designsystem.components.input

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

/** Paridade com MoneyTextWatcher: os dígitos de entrada representam centavos; separadores e moeda são decoração. */
fun caixaDSMoneyInputAmount(value: String): BigDecimal =
    value.filter { it in '0'..'9' }.toBigDecimalOrNull()?.movePointLeft(2) ?: BigDecimal.ZERO.setScale(2)

fun caixaDSMoneyInputFormat(value: String, locale: Locale = Locale.getDefault()): String =
    NumberFormat.getCurrencyInstance(locale).format(caixaDSMoneyInputAmount(value))
