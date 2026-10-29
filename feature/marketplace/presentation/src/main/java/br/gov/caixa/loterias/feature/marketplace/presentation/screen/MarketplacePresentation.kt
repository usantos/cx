package br.gov.caixa.loterias.feature.marketplace.presentation.screen

import br.gov.caixa.loterias.feature.marketplace.presentation.formatting.localizedMessage as messageText

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.marketplaceDetailRetailerName

internal fun marketplaceDrawDate(value: String): String {
    Regex("\\d{2}/\\d{2}").find(value)?.let { return it.value }
    return Regex("\\d{4}-(\\d{2})-(\\d{2})").find(value)?.let {
        "${it.groupValues[2]}/${it.groupValues[1]}"
    }.orEmpty()
}

/** Mesma magnitude e truncamento em uma casa decimal de ViewUtils.getMoedaFormatPorExtenso da CX. */
internal fun marketplacePrizeWords(value: BigDecimal): String {
    val magnitude = when {
        value >= BigDecimal("1000000000") -> Triple(BigDecimal("1000000000"), messageText("bilhao_d89fd6"), messageText("bilhoes_656f67"))
        value >= BigDecimal("1000000") -> Triple(BigDecimal("1000000"), messageText("milhao_afa9f1"), messageText("milhoes_44bbbb"))
        value >= BigDecimal("1000") -> Triple(BigDecimal("1000"), "Mil", "Mil")
        else -> return ""
    }
    val amount = value.divide(magnitude.first).setScale(1, RoundingMode.DOWN)
    val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR"))
    formatter.isGroupingUsed = false
    formatter.maximumFractionDigits = if (amount.stripTrailingZeros().scale() > 0) 1 else 0
    return "(R$ ${formatter.format(amount)} ${if (amount.toInt() == 1) magnitude.second else magnitude.third})"
}

internal fun marketplaceRetailerName(value: String): String = marketplaceDetailRetailerName(value)

/** Quebra títulos de lotéricas em 30 caracteres, preservando palavras completas. */
internal fun marketplaceListRetailerName(value: String): String = wrapMarketplaceRetailerName(value, 30)

/** Títulos de favoritas usam o cartão nativo mais estreito de lotérica favorita. */
internal fun marketplaceFavoriteRetailerName(value: String): String = wrapMarketplaceRetailerName(value, FAVORITE_RETAILER_LINE_LENGTH)

private fun wrapMarketplaceRetailerName(value: String, limit: Int): String = buildString {
    var lineLength = 0
    marketplaceRetailerName(value).trim().split(Regex("\\s+")).forEach { word ->
        val length = word.codePointCount(0, word.length)
        if (lineLength > 0) {
            if (lineLength + 1 + length > limit) {
                append('\n')
                lineLength = 0
            } else {
                append(' ')
                lineLength++
            }
        }
        append(word)
        lineLength += length
    }
}

internal fun marketplaceCity(value: String): String {
    val parts = value.split(" - ", limit = 2)
    return marketplaceRetailerName(parts.first()) + parts.getOrNull(1)?.let { " - $it" }.orEmpty()
}

/** A CX busca outra página somente após a rolagem para baixo alcançar o fim da lista. */
internal fun shouldLoadMarketplacePage(
    hasScrolled: Boolean,
    canScrollForward: Boolean,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
): Boolean = hasScrolled && !canScrollForward && canLoadMore && !isLoadingMore

private const val FAVORITE_RETAILER_LINE_LENGTH = 27
