package br.gov.caixa.loterias.feature.marketplacefilter

import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilter

internal fun MarketplaceFilterUiState.toDomain() = MarketplaceFilter(
    minimumValue.toBackendMoney(),
    maximumValue.toBackendMoney(),
    minimumShares.toIntOrNull(),
    maximumShares.toIntOrNull(),
    selectedBetQuantity,
    selectedNumberQuantity,
    selectedRetailer?.id?.toString().orEmpty(),
    selectedModality?.name.orEmpty(),
    wantedNumbers,
    unwantedNumbers,
).withModality(selectedModality?.id, selectedModality?.contestType)

private fun String.toBackendMoney(): String = trim().replace(".", "").replace(',', '.')
