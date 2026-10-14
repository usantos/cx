package br.gov.caixa.loterias.feature.marketplacefilter

import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilter

internal fun MarketplaceFilterUiState.toDomain() = MarketplaceFilter(
    minimumValue.toBackendMoney(),
    maximumValue.toBackendMoney(),
    minimumShares.toIntOrNull(),
    maximumShares.toIntOrNull(),
    selectedBetQuantity,
    selectedNumberQuantity,
    selectedRetailer?.id?.toString() ?: restoredRetailerId,
    selectedModality?.name.orEmpty(),
    wantedNumbers,
    unwantedNumbers,
).withModality(selectedModality?.id, selectedModality?.contestType)

private fun String.toBackendMoney(): String {
    val normalized = trim().replace(".", "").replace(',', '.')
    return normalized.takeUnless { it.toBigDecimalOrNull()?.compareTo(java.math.BigDecimal.ZERO) == 0 }.orEmpty()
}

internal fun marketplaceFilterState(filter: MarketplaceFilter, lotteryEnabled: Boolean) = MarketplaceFilterUiState(
    selectedModality = filter.modalityId?.let { MarketplaceFilterModalityUi(it, filter.modality,
        filter.modality, filter.contestType, allNumberQuantityOptions, 80) },
    lottery = filter.lottery,
    restoredRetailerId = filter.lottery,
    minimumValue = filter.minimumValue.replace('.', ','),
    maximumValue = filter.maximumValue.replace('.', ','),
    minimumShares = filter.minimumShares?.toString().orEmpty(),
    maximumShares = filter.maximumShares?.toString().orEmpty(),
    selectedBetQuantity = filter.betQuantity,
    selectedNumberQuantity = filter.numberQuantity,
    wantedNumbers = filter.wantedNumbers,
    unwantedNumbers = filter.unwantedNumbers,
    numberOptions = allNumberQuantityOptions,
    lotteryEnabled = lotteryEnabled,
    allModalitiesHighlighted = filter.lottery.isNotBlank(),
)
