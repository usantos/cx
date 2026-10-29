package br.gov.caixa.loterias.core.designsystem.components.cart

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

/** Visibilidade de SomadorCarrinhoFragment; a contagem sozinha nunca torna a barra visível. */
fun isCartSummaryVisible(total: BigDecimal): Boolean = total.signum() > 0

internal fun cartSummaryCountLabel(count: Int): String =
    if (count > 999) "..." else count.coerceAtLeast(0).toString()

internal fun cartSummaryAmount(total: BigDecimal): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(total).replace('\u00a0', ' ')
