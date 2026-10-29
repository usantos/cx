package br.gov.caixa.loterias.feature.marketplace.presentation.mapper

import br.gov.caixa.loterias.feature.marketplace.presentation.formatting.localizedMessage as messageText
import java.math.BigDecimal

internal fun marketplaceDetailModalityLabel(value: String): String = when (value.uppercase(java.util.Locale.ROOT).replace(" ", "_")) {
    "MEGA_SENA", "MEGA-SENA" -> "mega-sena"
    "LOTOFACIL", "LOTOFÁCIL" -> messageText("lotofacil_dbb2e4")
    "DUPLA_SENA" -> "dupla sena"
    "DIA_DE_SORTE" -> messageText("dia_de_sorte_71bc30")
    "SUPER_7", "SUPER_SETE" -> "super sete"
    "MAIS_MILIONARIA", "+MILIONÁRIA", "MAIS_MILIONÁRIA" -> messageText("milionaria_c791e4")
    else -> value.replace('_', ' ').lowercase(java.util.Locale.forLanguageTag("pt-BR"))
}

private fun marketplaceDetailDecimal(value: String): BigDecimal {
    val plain = value.trim().removePrefix("R$").trim()
    return (if (',' in plain) plain.replace(".", "").replace(',', '.') else plain).toBigDecimalOrNull() ?: BigDecimal.ZERO
}

internal fun marketplaceDetailMoney(value: String): String =
    java.text.NumberFormat.getCurrencyInstance(java.util.Locale.forLanguageTag("pt-BR"))
        .format(marketplaceDetailDecimal(value)).replace('\u00a0', ' ')

internal fun marketplaceDetailPrizeInWords(value: String): String? {
    val amount = marketplaceDetailDecimal(value)
    val (divisor, singular, plural) = when {
        amount >= BigDecimal("1000000000") -> Triple(BigDecimal("1000000000"), messageText("bilhao_d89fd6"), messageText("bilhoes_656f67"))
        amount >= BigDecimal("1000000") -> Triple(BigDecimal("1000000"), messageText("milhao_afa9f1"), messageText("milhoes_44bbbb"))
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
            word.isEmpty() || (index > 0 && word in lowerWords) -> word
            else -> word.take(1).uppercase(locale) + word.drop(1)
        }
    }.joinToString(" ")
}
