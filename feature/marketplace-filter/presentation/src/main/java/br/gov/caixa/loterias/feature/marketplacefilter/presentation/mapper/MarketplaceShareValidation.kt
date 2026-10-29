package br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper

import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.ShareValidationMessages

internal fun MarketplaceFilterUiState.withShareValidation(messages: ShareValidationMessages): MarketplaceFilterUiState {
    val minimum = minimumShares.toBigIntegerOrNull()
    val maximum = maximumShares.toBigIntegerOrNull()
    return copy(
        minimumSharesError = when {
            minimum != null && minimum < java.math.BigInteger.valueOf(2) -> messages.minimumTooSmall

            minimum != null && (minimum > java.math.BigInteger.valueOf(100) || (maximum != null && minimum > maximum)) ->
                messages.minimumTooLarge

            else -> null
        },
        maximumSharesError = when {
            maximum != null && maximum < java.math.BigInteger.valueOf(2) -> messages.maximumTooSmall
            maximum != null && maximum > java.math.BigInteger.valueOf(100) -> messages.maximumTooLarge
            else -> null
        }
    )
}
