package br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper

import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.PoolModalityAvailability
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterModalityUi
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer

internal val allNumberQuantityOptions = (5..26).toList()

internal val allModalities get() = MarketplaceFilterModalityUi(
    id = null,
    name = messageText("todas_as_modalidades_d0b87a"),
    contestType = null,
    numberOptions = allNumberQuantityOptions,
    maximumNumber = 80
)

internal fun marketplaceModalities(
    parameters: BettingParameters,
    specialContests: MarketplaceSpecialContests = MarketplaceSpecialContests(),
): List<MarketplaceFilterModalityUi> = parameters.availablePoolModalities.mapNotNull { available ->
    val modality = parameters.gameFor(available)?.toFilterModality(available)
        ?: available.toFallbackFilterModality()
    modality?.let { mapped ->
        val code = available.modalityCode.ifBlank {
            filterModalityFallbacks.firstOrNull { it.id == mapped.id }?.code.orEmpty()
        }
        val label = specialContests.label(code, mapped.contestType ?: 1, available.contestNumber ?: 0)
        if (label != null) mapped.copy(name = label) else mapped
    }
}.distinctBy { it.id to it.contestType }

/** Alternativa nativa quando um bolão disponível não possui jogo correspondente na simulação. */
private data class FilterModalityFallback(
    val id: Int,
    val code: String,
    val name: String,
    val quantities: IntRange,
    val maximum: Int = 80,
    val specialName: String = name,
)

private val filterModalityFallbacks get() = listOf(
    FilterModalityFallback(2, "MEGA_SENA", "Mega-Sena", 6..20, 60, messageText("mega_da_virada_8d37ad")),
    FilterModalityFallback(3, "QUINA", "Quina", 5..15, 80, messageText("quina_de_sao_joao_3f2b5c")),
    FilterModalityFallback(7, "SUPER_7", "Super Sete", 7..21),
    FilterModalityFallback(8, "LOTOFACIL", messageText("lotofacil_d3ed7c"), 15..20, 25, messageText("lotofacil_da_independencia_af8f9c")),
    FilterModalityFallback(9, "MAIS_MILIONARIA", messageText("milionaria_11be79"), 6..12, 50),
    FilterModalityFallback(11, "DIA_DE_SORTE", messageText("dia_de_sorte_b5c7a5"), 7..15, 31),
    FilterModalityFallback(14, "LOTOGOL", "Lotogol", 5..25),
    FilterModalityFallback(16, "LOTOMANIA", "Lotomania", 5..25),
    FilterModalityFallback(18, "DUPLA_SENA", "Dupla Sena", 6..15, 50, messageText("dupla_de_pascoa_80ee17")),
    FilterModalityFallback(19, "LOTECA", "Loteca", 17..26, 80, "Loteca Especial"),
    FilterModalityFallback(20, "TIMEMANIA", "Timemania", 10..10),
    FilterModalityFallback(1502, "INSTANTANEA", messageText("final_instant_title"), 5..25)
)

private fun PoolModalityAvailability.toFallbackFilterModality(): MarketplaceFilterModalityUi? {
    val fallback = filterModalityFallbacks.firstOrNull {
        it.id == modalityId || it.code.equals(modalityCode, ignoreCase = true)
    } ?: return null
    val special = contestType.equals("ESPECIAL", true) || contestType.equals("E", true)
    val baseName = modality?.name?.takeIf { it.isNotBlank() } ?: fallback.name
    val name = if (special) {
        modality?.specialName?.takeIf { it.isNotBlank() } ?: fallback.specialName
    } else if (fallback.id == 2) {
        "mega sena"
    } else {
        baseName
    }
    return MarketplaceFilterModalityUi(
        id = fallback.id,
        name = name.lowercase(java.util.Locale.ROOT),
        baseName = baseName,
        contestType = if (special) 2 else 1,
        numberOptions = fallback.quantities.toList(),
        maximumNumber = fallback.maximum
    )
}

private fun BettingParameters.gameFor(available: PoolModalityAvailability): LotteryGameParameters? {
    val candidates = games.filter { game ->
        game.modality.id == available.modalityId ||
            game.modality.code.equals(available.modalityCode, ignoreCase = true)
    }
    return candidates.firstOrNull { it.contestType.equals(available.contestType, ignoreCase = true) }
        ?: candidates.firstOrNull()
}

private fun LotteryGameParameters.toFilterModality(available: PoolModalityAvailability): MarketplaceFilterModalityUi {
    val special = available.contestType.equals("ESPECIAL", ignoreCase = true) ||
        available.contestType.equals("E", ignoreCase = true)
    val displayName = when {
        special -> modality.specialName.ifBlank { modality.name }
        modality.code in setOf("MEGA_SENA", "MEGASENA") -> "mega sena"
        else -> modality.name
    }
    return MarketplaceFilterModalityUi(
        id = available.modalityId.takeIf { it > 0 } ?: modality.id.takeIf { it > 0 },
        name = displayName.lowercase(java.util.Locale.ROOT),
        baseName = modality.name,
        contestType = if (special) 2 else 1,
        numberOptions = prices.map { it.numberQuantity }.filter { it > 0 }.distinct().sorted(),
        maximumNumber = maximumNumber.takeIf { it > 0 } ?: 80
    )
}

internal fun wantedNumberOptions(modality: MarketplaceFilterModalityUi?): List<Int> = (1..(modality?.maximumNumber ?: 80)).toList()

internal fun retailerDisplayLabel(retailer: Retailer): String {
    val digits = retailer.code.filter(Char::isDigit)
    val code = when {
        digits.length >= 9 -> digits.substring(3, 9)
        digits.isNotEmpty() -> digits.takeLast(6).padStart(6, '0')
        else -> retailer.id.toString()
    }
    return "$code - ${retailer.fantasyName.trim()}"
}

internal fun filterRetailers(text: String, retailers: List<Retailer>): List<Retailer> = retailers.filter {
    it.fantasyName.contains(text, ignoreCase = true)
}
