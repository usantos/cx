package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import java.math.BigDecimal

data class RapidConfigurationUiState(
    val loading: Boolean = true,
    val serviceFailure: br.gov.caixa.loterias.core.presentation.state.ServiceFailurePresentation = br.gov.caixa.loterias.core.presentation.state.ServiceFailurePresentation(),
    val page: Int = 0,
    val minimumAmount: BigDecimal = BigDecimal.ZERO,
    val maximumAmount: BigDecimal = BigDecimal.ZERO,
    val minimumPrize: BigDecimal = BigDecimal.ZERO,
    val requiredNumbers: Set<Int> = emptySet(),
    val excludedNumbers: Set<Int> = emptySet(),
    val availableModalities: List<LotteryModality> = emptyList(),
    val selectedModalityIds: Set<Int> = emptySet(),
    val favoriteBetIds: Set<Long> = emptySet(),
    val availableFavorites: List<FavoriteBet> = emptyList(),
    val favoritesLoading: Boolean = false,
    val favoritesError: String? = null,
    val minimumAllowedAmount: BigDecimal? = null,
    val maximumAllowedAmount: BigDecimal? = null,
    val amountLimitsLoading: Boolean = false,
    val amountLimitsError: String? = null,
    val saved: Boolean = false,
    val errorMessage: String? = null,
    val warningMessage: String? = null,
) {
    val canSave: Boolean get() = minimumAmount >= BigDecimal.ZERO &&
        selectedModalityIds.isNotEmpty() &&
        requiredNumbers.intersect(excludedNumbers).isEmpty()
}
