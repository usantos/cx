package br.gov.caixa.loterias.feature.betting.presentation.screen

import java.math.BigDecimal

data class RapidConfigurationActions(
    val setPage: (Int) -> Unit,
    val setMinimumPrize: (BigDecimal) -> Unit,
    val setMinimumAmount: (BigDecimal) -> Unit,
    val setMaximumAmount: (BigDecimal) -> Unit,
    val setCustomAmount: (BigDecimal?, Boolean) -> Boolean,
    val loadAmountLimits: () -> Unit,
    val toggleModality: (Int) -> Unit,
    val toggleRequiredNumber: (Int) -> Unit,
    val clearRequiredNumbers: () -> Unit,
    val toggleExcludedNumber: (Int) -> Unit,
    val clearExcludedNumbers: () -> Unit,
    val save: () -> Unit,
)
