package br.gov.caixa.loterias.feature.marketplace

import br.gov.caixa.loterias.core.domain.marketplace.MarketplacePoolDetail
import java.math.BigDecimal

data class MarketplaceDetailUiState(
    val detail: MarketplacePoolDetail? = null,
    val selectedShares: Int = 1,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isAddingToCart: Boolean = false,
    val addToCartError: String? = null,
    val confirmation: MarketplacePoolConfirmation? = null,
    val favoriteLottery: Boolean? = null,
    val pendingFavorite: Boolean? = null,
    val isUpdatingFavorite: Boolean = false,
    val favoriteError: String? = null,
) {
    val purchaseAmount: BigDecimal get() {
        val current = detail ?: return BigDecimal.ZERO
        if (selectedShares <= 0) return BigDecimal.ZERO
        return current.lastSharePriceWithFeeValue + current.regularSharePriceWithFee.multiply((selectedShares - 1).toBigDecimal())
    }
    val canDecrease: Boolean get() = !isAddingToCart && selectedShares > 1
    val canIncrease: Boolean get() = !isAddingToCart && selectedShares < (detail?.availableShares ?: 0)
}

data class MarketplacePoolConfirmation(
    val poolCode: String,
    val modality: String,
    val expiresAt: String,
    val contest: Int = 0,
    val drawDate: String = "",
    val amount: BigDecimal = BigDecimal.ZERO,
)

sealed interface MarketplaceDetailAction {
    data object Retry : MarketplaceDetailAction
    data object IncreaseShares : MarketplaceDetailAction
    data object DecreaseShares : MarketplaceDetailAction
    data object AddToCart : MarketplaceDetailAction
}

internal fun marketplaceDetailModalityLabel(value: String): String = when (value.uppercase(java.util.Locale.ROOT).replace(" ", "_")) {
    "MEGA_SENA", "MEGA-SENA" -> "mega-sena"
    "LOTOFACIL", "LOTOFÁCIL" -> "lotofácil"
    "DUPLA_SENA" -> "dupla sena"
    "DIA_DE_SORTE" -> "dia de sorte"
    "SUPER_7", "SUPER_SETE" -> "super sete"
    "MAIS_MILIONARIA", "+MILIONÁRIA", "MAIS_MILIONÁRIA" -> "+milionária"
    else -> value.replace('_', ' ').lowercase(java.util.Locale.forLanguageTag("pt-BR"))
}

private fun marketplaceDetailDecimal(value: String): BigDecimal {
    val plain = value.trim().removePrefix("R$").trim()
    return (if (',' in plain) plain.replace(".", "").replace(',', '.') else plain).toBigDecimalOrNull() ?: BigDecimal.ZERO
}

internal fun marketplaceDetailMoney(value: String): String = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.forLanguageTag("pt-BR"))
    .format(marketplaceDetailDecimal(value)).replace('\u00a0', ' ')

internal fun marketplaceDetailPrizeInWords(value: String): String? {
    val amount = marketplaceDetailDecimal(value)
    val (divisor, singular, plural) = when {
        amount >= BigDecimal("1000000000") -> Triple(BigDecimal("1000000000"), "Bilhão", "Bilhões")
        amount >= BigDecimal("1000000") -> Triple(BigDecimal("1000000"), "Milhão", "Milhões")
        amount >= BigDecimal("1000") -> Triple(BigDecimal("1000"), "Mil", "Mil")
        else -> return null
    }
    val quantity = amount.divide(divisor).setScale(1, java.math.RoundingMode.DOWN)
    val suffix = if (quantity.toInt() == 1) singular else plural
    return "R$ ${quantity.stripTrailingZeros().toPlainString().replace('.', ',')} $suffix"
}

internal fun marketplaceDetailRetailerName(value: String): String {
    val lowerWords = setOf("de", "da", "do", "das", "dos", "a", "o", "as", "os", "e", "em", "no", "na", "nos", "nas", "por", "para")
    val locale = java.util.Locale.forLanguageTag("pt-BR")
    return value.lowercase(locale).split(" ").mapIndexed { index, word ->
        when {
            word == "s.a." -> "S.A."
            word.isEmpty() || index > 0 && word in lowerWords -> word
            else -> word.take(1).uppercase(locale) + word.drop(1)
        }
    }.joinToString(" ")
}
