package br.gov.caixa.loterias.feature.marketplacefilter

import br.gov.caixa.loterias.core.domain.betting.BettingParameters
import br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters
import br.gov.caixa.loterias.core.domain.betting.PoolModalityAvailability
import br.gov.caixa.loterias.core.domain.retail.Retailer

internal val allNumberQuantityOptions = (5..26).toList()

internal val allModalities = MarketplaceFilterModalityUi(
    id = null,
    name = "Todas",
    contestType = null,
    numberOptions = allNumberQuantityOptions,
    maximumNumber = 80,
)

internal fun marketplaceModalities(parameters: BettingParameters): List<MarketplaceFilterModalityUi> =
    listOf(allModalities) + parameters.availablePoolModalities.mapNotNull { available ->
        parameters.gameFor(available)?.toFilterModality(available)
    }.distinctBy { it.id to it.contestType }

private fun BettingParameters.gameFor(available: PoolModalityAvailability): LotteryGameParameters? {
    val candidates = games.filter { game ->
        game.modality.id == available.modalityId ||
            game.modality.code.equals(available.modalityCode, ignoreCase = true)
    }
    return candidates.firstOrNull { it.contestType.equals(available.contestType, ignoreCase = true) }
        ?: candidates.firstOrNull()
}

private fun LotteryGameParameters.toFilterModality(
    available: PoolModalityAvailability,
): MarketplaceFilterModalityUi {
    val special = available.contestType.equals("ESPECIAL", ignoreCase = true) ||
        available.contestType.equals("E", ignoreCase = true)
    return MarketplaceFilterModalityUi(
        id = available.modalityId.takeIf { it > 0 } ?: modality.id.takeIf { it > 0 },
        name = if (special) modality.specialName.ifBlank { modality.name } else modality.name,
        baseName = modality.name,
        contestType = if (special) 2 else 1,
        numberOptions = prices.map { it.numberQuantity }.filter { it > 0 }.distinct().sorted(),
        maximumNumber = maximumNumber.takeIf { it > 0 } ?: 80,
    )
}

internal fun wantedNumberOptions(modality: MarketplaceFilterModalityUi?): List<Int> =
    (1..(modality?.maximumNumber ?: 80)).toList()

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
