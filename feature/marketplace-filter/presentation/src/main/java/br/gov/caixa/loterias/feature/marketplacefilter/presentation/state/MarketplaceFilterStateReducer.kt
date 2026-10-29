package br.gov.caixa.loterias.feature.marketplacefilter.presentation.state

import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.allNumberQuantityOptions
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.filterRetailers
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.retailerDisplayLabel
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.withShareValidation
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterModalityUi

internal sealed interface MarketplaceFilterSelectionAction : MarketplaceFilterStateAction

internal sealed interface MarketplaceFilterInputsAction : MarketplaceFilterStateAction

internal sealed interface MarketplaceFilterLoadingAction : MarketplaceFilterStateAction

internal fun reduceMarketplaceFilterState(state: MarketplaceFilterUiState, action: MarketplaceFilterStateAction): MarketplaceFilterUiState =
    when (action) {
        is MarketplaceFilterSelectionAction -> reduceMarketplaceFilterSelectionState(state, action)
        is MarketplaceFilterInputsAction -> reduceMarketplaceFilterInputsState(state, action)
        is MarketplaceFilterLoadingAction -> reduceMarketplaceFilterLoadingState(state, action)
    }

private fun reduceMarketplaceFilterSelectionState(
    state: MarketplaceFilterUiState,
    action: MarketplaceFilterSelectionAction,
): MarketplaceFilterUiState = when (action) {
    is MarketplaceFilterStateAction.Cleared -> run reduce@{
        val clean = action.defaults
        val selected = clean.selectedModality?.let { previous ->
            state.modalities.firstOrNull { it.id == previous.id && it.contestType == previous.contestType } ?: previous
        }
        clean.copy(
            modalities =
                (if (!state.lotteryEnabled) listOf(action.allModality) else emptyList()) + state.modalities.filterNot { it.isAll },
            selectedModality = selected,
            numberOptions = selected?.numberOptions ?: allNumberQuantityOptions,
            betOptions = state.betOptions,
            configurationError = state.configurationError,
            lottery = if (state.lotteryEnabled) "" else state.lottery,
            selectedRetailer = if (state.lotteryEnabled) null else state.selectedRetailer,
            restoredRetailerId = if (state.lotteryEnabled) "" else state.restoredRetailerId,
            allModalitiesHighlighted = !state.lotteryEnabled
        )
    }

    is MarketplaceFilterStateAction.ModalitySelected -> state.copy(
        selectedModality = action.modality.takeUnless(MarketplaceFilterModalityUi::isAll),
        allModalitiesHighlighted = action.modality.isAll,
        numberOptions = action.modality.numberOptions,
        selectedBetQuantity = null,
        selectedNumberQuantity = null,
        wantedNumbers = emptyList(),
        unwantedNumbers = emptyList(),
        validationMessage = null
    )

    is MarketplaceFilterStateAction.RetailerSelected -> state.copy(
        lottery = retailerDisplayLabel(action.retailer),
        selectedRetailer = action.retailer,
        retailerSuggestions = emptyList(),
        retailerMessage = null,
        restoredRetailerId = "",
        modalities = listOf(action.allModality) + state.modalities.filterNot { it.isAll },
        selectedModality = null,
        allModalitiesHighlighted = true,
        numberOptions = allNumberQuantityOptions,
        selectedBetQuantity = null,
        selectedNumberQuantity = null,
        wantedNumbers = emptyList(),
        unwantedNumbers = emptyList(),
        validationMessage = null
    )
}

private fun reduceMarketplaceFilterInputsState(
    state: MarketplaceFilterUiState,
    action: MarketplaceFilterInputsAction,
): MarketplaceFilterUiState = when (action) {
    is MarketplaceFilterStateAction.MinimumValueChanged -> state.copy(minimumValue = action.value, validationMessage = null)

    is MarketplaceFilterStateAction.MaximumValueChanged -> state.copy(maximumValue = action.value, validationMessage = null)

    is MarketplaceFilterStateAction.MinimumSharesChanged -> state.copy(
        minimumShares = action.value.filter(Char::isDigit),
        validationMessage = null
    )
        .withShareValidation(action.messages)

    is MarketplaceFilterStateAction.MaximumSharesChanged -> state.copy(
        maximumShares = action.value.filter(Char::isDigit),
        validationMessage = null
    )
        .withShareValidation(action.messages)

    is MarketplaceFilterStateAction.BetQuantitySelected -> state.copy(
        selectedBetQuantity = action.quantity.takeUnless { it == state.selectedBetQuantity },
        validationMessage = null
    )

    is MarketplaceFilterStateAction.NumberQuantitySelected -> state.copy(
        selectedNumberQuantity = action.quantity,
        validationMessage = null
    )

    is MarketplaceFilterStateAction.WantedNumbersChanged -> state.copy(
        wantedNumbers = action.numbers.distinct().sorted(),
        validationMessage = null
    )

    is MarketplaceFilterStateAction.UnwantedNumbersChanged -> state.copy(
        unwantedNumbers = action.numbers.distinct().sorted(),
        validationMessage = null
    )

    is MarketplaceFilterStateAction.SharesOverflowed -> state.withShareValidation(action.messages).copy(validationMessage = action.message)

    is MarketplaceFilterStateAction.ValidationFailed -> state.copy(validationMessage = action.message)
}

private fun reduceMarketplaceFilterLoadingState(
    state: MarketplaceFilterUiState,
    action: MarketplaceFilterLoadingAction,
): MarketplaceFilterUiState = when (action) {
    is MarketplaceFilterStateAction.ParametersReceived -> reduceMarketplaceFilterParametersReceived(state, action)

    is MarketplaceFilterStateAction.LotteryChanged -> reduceMarketplaceFilterLotteryChanged(state, action)

    is MarketplaceFilterStateAction.RetailersReceived -> reduceMarketplaceFilterRetailersReceived(state, action)

    is MarketplaceFilterStateAction.RetailersFailed -> if (state.lottery.startsWith(action.query, ignoreCase = true)) {
        state.copy(retailerLoading = false, retailerSuggestions = emptyList())
    } else {
        state
    }
}

private fun reduceMarketplaceFilterParametersReceived(
    state: MarketplaceFilterUiState,
    action: MarketplaceFilterStateAction.ParametersReceived,
): MarketplaceFilterUiState = run reduce@{
    val betOptions = action.betOptions
    val modalities = action.modalities
    val selected = state.selectedModality?.let { previous ->
        modalities.firstOrNull { it.id == previous.id && it.contestType == previous.contestType } ?: previous
    }
    state.copy(
        modalities =
            (
                if (state.selectedRetailer != null ||
                    state.restoredRetailerId.isNotBlank()
                ) {
                    listOf(action.allModality)
                } else {
                    emptyList()
                }
                ) +
                modalities,
        selectedModality = selected,
        betOptions = betOptions,
        numberOptions = selected?.numberOptions ?: allNumberQuantityOptions,
        configurationError = action.configurationError
    )
}

private fun reduceMarketplaceFilterLotteryChanged(
    state: MarketplaceFilterUiState,
    action: MarketplaceFilterStateAction.LotteryChanged,
): MarketplaceFilterUiState = run reduce@{
    val changed = state.copy(
        lottery = action.value,
        selectedRetailer = state.selectedRetailer?.takeIf { retailerDisplayLabel(it) == action.value },
        retailerMessage = null,
        restoredRetailerId = state.restoredRetailerId.takeIf { action.value == state.lottery }.orEmpty(),
        modalities = if (action.value.isEmpty()) state.modalities.filterNot { it.isAll } else state.modalities,
        allModalitiesHighlighted = if (action.value.isEmpty()) false else state.allModalitiesHighlighted
    )
    if (action.value.isEmpty() && state.lottery.isNotEmpty()) {
        val restored = action.lastIndividualModality?.let { previous ->
            state.modalities.firstOrNull { it.id == previous.id && it.contestType == previous.contestType }
        } ?: state.modalities.firstOrNull { !it.isAll }
        return@reduce changed.copy(
            selectedModality = restored,
            numberOptions = restored?.numberOptions ?: allNumberQuantityOptions,
            selectedBetQuantity = null,
            selectedNumberQuantity = null,
            wantedNumbers = emptyList(),
            unwantedNumbers = emptyList(),
            retailerSuggestions = emptyList(),
            retailerLoading = false,
            validationMessage = null
        )
    }
    when {
        action.value.length < RETAILER_QUERY_LENGTH -> changed.copy(
            retailerSuggestions = emptyList(),
            retailerLoading = false
        )

        action.value.length > RETAILER_QUERY_LENGTH -> changed.copy(
            retailerSuggestions = filterRetailers(action.value, changed.cachedRetailers),
            retailerLoading = false
        )

        else -> changed.copy(
            retailerSuggestions = emptyList(),
            retailerLoading = true
        )
    }
}

private fun reduceMarketplaceFilterRetailersReceived(
    state: MarketplaceFilterUiState,
    action: MarketplaceFilterStateAction.RetailersReceived,
): MarketplaceFilterUiState = if (!state.lottery.startsWith(action.query, ignoreCase = true) ||
    state.selectedRetailer != null
) {
    state
} else {
    val suggestions = if (state.lottery.length == RETAILER_QUERY_LENGTH) {
        action.retailers
    } else {
        filterRetailers(state.lottery, action.retailers)
    }
    state.copy(
        cachedRetailers = action.retailers,
        retailerSuggestions = suggestions,
        retailerLoading = false,
        retailerMessage = action.emptyMessage
            .takeIf { state.lottery.length == RETAILER_QUERY_LENGTH && suggestions.isEmpty() }
    )
}

private const val RETAILER_QUERY_LENGTH = 6
