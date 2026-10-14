package br.gov.caixa.loterias.feature.marketplace

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

internal fun marketplaceDrawDate(value: String): String {
    Regex("\\d{2}/\\d{2}").find(value)?.let { return it.value }
    return Regex("\\d{4}-(\\d{2})-(\\d{2})").find(value)?.let {
        "${it.groupValues[2]}/${it.groupValues[1]}"
    }.orEmpty()
}

/** Same magnitude and one-decimal truncation as CX ViewUtils.getMoedaFormatPorExtenso. */
internal fun marketplacePrizeWords(value: BigDecimal): String {
    val magnitude = when {
        value >= BigDecimal("1000000000") -> Triple(BigDecimal("1000000000"), "Bilhão", "Bilhões")
        value >= BigDecimal("1000000") -> Triple(BigDecimal("1000000"), "Milhão", "Milhões")
        value >= BigDecimal("1000") -> Triple(BigDecimal("1000"), "Mil", "Mil")
        else -> return ""
    }
    val amount = value.divide(magnitude.first).setScale(1, RoundingMode.DOWN)
    val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR"))
    formatter.isGroupingUsed = false
    formatter.maximumFractionDigits = if (amount.stripTrailingZeros().scale() > 0) 1 else 0
    return "(R$ ${formatter.format(amount)} ${if (amount.toInt() == 1) magnitude.second else magnitude.third})"
}

internal fun marketplaceRetailerName(value: String): String = value.lowercase(Locale.forLanguageTag("pt-BR"))
    .split(' ').joinToString(" ") { word ->
        if (word in setOf("da", "de", "do", "das", "dos", "e")) word else word.replaceFirstChar(Char::titlecase)
    }

internal fun marketplaceCity(value: String): String {
    val parts = value.split(" - ", limit = 2)
    return marketplaceRetailerName(parts.first()) + parts.getOrNull(1)?.let { " - $it" }.orEmpty()
}

/** CX fetches another page only after downward scrolling reaches the list's bottom. */
internal fun shouldLoadMarketplacePage(
    hasScrolled: Boolean,
    canScrollForward: Boolean,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
): Boolean = hasScrolled && !canScrollForward && canLoadMore && !isLoadingMore
