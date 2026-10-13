package br.gov.caixa.loterias.core.designsystem

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

/** Visibility of SomadorCarrinhoFragment; the count alone never makes the bar visible. */
fun isCartSummaryVisible(total: BigDecimal): Boolean = total.signum() > 0

internal fun cartSummaryCountLabel(count: Int): String =
    if (count > 999) "..." else count.coerceAtLeast(0).toString()

internal fun cartSummaryAmount(total: BigDecimal): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(total).replace('\u00a0', ' ')
