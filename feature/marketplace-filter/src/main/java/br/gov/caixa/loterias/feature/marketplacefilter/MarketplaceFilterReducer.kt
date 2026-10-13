package br.gov.caixa.loterias.feature.marketplacefilter

internal fun MarketplaceFilterUiState.reduce(action: MarketplaceFilterAction): MarketplaceFilterUiState =
    when (action) {
        is MarketplaceFilterAction.SelectModality -> {
            copy(
                selectedModality = action.value.takeUnless(MarketplaceFilterModalityUi::isAll),
                numberOptions = action.value.numberOptions,
                selectedBetQuantity = null,
                selectedNumberQuantity = null,
                wantedNumbers = emptyList(),
                unwantedNumbers = emptyList(),
            )
        }
        is MarketplaceFilterAction.ChangeLottery -> copy(
            lottery = action.value,
            selectedRetailer = selectedRetailer?.takeIf { retailerDisplayLabel(it) == action.value },
            retailerMessage = null,
        )
        is MarketplaceFilterAction.SelectRetailer -> copy(
            lottery = retailerDisplayLabel(action.value),
            selectedRetailer = action.value,
            retailerSuggestions = emptyList(),
            retailerMessage = null,
            selectedModality = null,
            numberOptions = allNumberQuantityOptions,
        )
        is MarketplaceFilterAction.ChangeMinimumValue -> copy(minimumValue = action.value)
        is MarketplaceFilterAction.ChangeMaximumValue -> copy(maximumValue = action.value)
        is MarketplaceFilterAction.ChangeMinimumShares -> copy(minimumShares = action.value.onlyDigits())
        is MarketplaceFilterAction.ChangeMaximumShares -> copy(maximumShares = action.value.onlyDigits())
        is MarketplaceFilterAction.SelectBetQuantity -> copy(
            selectedBetQuantity = action.value.takeUnless { it == selectedBetQuantity },
        )
        is MarketplaceFilterAction.SelectNumberQuantity -> copy(selectedNumberQuantity = action.value)
        is MarketplaceFilterAction.ChangeWantedNumbers -> copy(wantedNumbers = action.value.distinct().sorted())
        is MarketplaceFilterAction.ChangeUnwantedNumbers -> copy(unwantedNumbers = action.value.distinct().sorted())
        MarketplaceFilterAction.Clear -> MarketplaceFilterUiState(
            modalities = modalities,
            betOptions = betOptions,
            numberOptions = allNumberQuantityOptions,
            lotteryEnabled = lotteryEnabled,
        )
        MarketplaceFilterAction.Apply,
        MarketplaceFilterAction.Back -> this
    }

private fun String.onlyDigits(): String = filter(Char::isDigit)

internal fun MarketplaceFilterUiState.withShareValidation(): MarketplaceFilterUiState {
    val minimum = minimumShares.toIntOrNull()
    val maximum = maximumShares.toIntOrNull()
    return copy(
        minimumSharesError = when {
            minimum != null && minimum < 2 -> "Quantidade mínima não pode ser menor que 2"
            minimum != null && (minimum > 100 || maximum != null && minimum > maximum) ->
                "Quantidade mínima não pode ser maior que a máxima"
            else -> null
        },
        maximumSharesError = when {
            maximum != null && maximum < 2 -> "Quantidade máxima não pode ser menor que 2"
            maximum != null && maximum > 100 -> "Quantidade máxima não pode ser maior que 100"
            else -> null
        },
    )
}
