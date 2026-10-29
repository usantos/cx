package br.gov.caixa.loterias.feature.betting.presentation.state

import java.math.BigDecimal

internal fun reduceRapidConfigurationAmounts(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction,
): RapidConfigurationUiState = when (action) {
    RapidConfigurationAction.AmountLimitsLoading -> reduceRapidConfigurationAmountLimitsLoading(state)
    is RapidConfigurationAction.AmountLimitsLoaded -> reduceRapidConfigurationAmountLimitsLoaded(state, action)
    is RapidConfigurationAction.AmountLimitsFailed -> reduceRapidConfigurationAmountLimitsFailed(state, action)
    is RapidConfigurationAction.MinimumPrizeChanged -> reduceRapidConfigurationMinimumPrizeChanged(state, action)
    is RapidConfigurationAction.MinimumAmountChanged -> reduceRapidConfigurationMinimumAmountChanged(state, action)
    is RapidConfigurationAction.MaximumAmountChanged -> reduceRapidConfigurationMaximumAmountChanged(state, action)
    is RapidConfigurationAction.MinimumIntervalCorrected -> reduceRapidConfigurationMinimumIntervalCorrected(state, action)
    else -> state
}

private fun reduceRapidConfigurationAmountLimitsLoading(state: RapidConfigurationUiState): RapidConfigurationUiState = run {
    val it = state
    it.copy(amountLimitsLoading = true, amountLimitsError = null)
}

private fun reduceRapidConfigurationAmountLimitsLoaded(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction.AmountLimitsLoaded,
): RapidConfigurationUiState = run {
    val it = state
    it.copy(amountLimitsLoading = false, minimumAllowedAmount = action.minimum, maximumAllowedAmount = action.maximum)
}

private fun reduceRapidConfigurationAmountLimitsFailed(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction.AmountLimitsFailed,
): RapidConfigurationUiState = run {
    val it = state
    it.copy(
        amountLimitsLoading = false,
        amountLimitsError = action.message
    )
}

private fun reduceRapidConfigurationMinimumPrizeChanged(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction.MinimumPrizeChanged,
): RapidConfigurationUiState = run {
    val it = state
    it.copy(minimumPrize = action.value.coerceAtLeast(BigDecimal.ZERO))
}

private fun reduceRapidConfigurationMinimumAmountChanged(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction.MinimumAmountChanged,
): RapidConfigurationUiState = run {
    val it = state
    if (it.minimumAllowedAmount == null || it.maximumAllowedAmount == null) {
        it.copy(minimumAmount = action.value.coerceAtLeast(BigDecimal.ZERO).coerceAtMost(it.maximumAmount))
    } else {
        adjustNativeInterval(it.copy(minimumAmount = action.value), minimum = true)
    }
}

private fun reduceRapidConfigurationMaximumAmountChanged(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction.MaximumAmountChanged,
): RapidConfigurationUiState = run {
    val it = state
    if (it.minimumAllowedAmount == null || it.maximumAllowedAmount == null) {
        it.copy(maximumAmount = action.value.coerceAtLeast(it.minimumAmount))
    } else {
        adjustNativeInterval(it.copy(maximumAmount = action.value), minimum = false)
    }
}

private fun reduceRapidConfigurationMinimumIntervalCorrected(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction.MinimumIntervalCorrected,
): RapidConfigurationUiState = run {
    val it = state
    it.copy(
        minimumAmount = action.floor,
        maximumAmount = action.maximum,
        warningMessage = action.message
    )
}

// ConfiguracaoRapidaoFragment3 usa deliberadamente < e > estritos em torno de 4x.
// Sua correção de cinco reais não é restringida pelo mínimo do carrinho nem pelo teto diário.
private fun adjustNativeInterval(state: RapidConfigurationUiState, minimum: Boolean): RapidConfigurationUiState {
    val ceiling = state.maximumAllowedAmount ?: return state
    fun correct(current: RapidConfigurationUiState): RapidConfigurationUiState = if (current.minimumAmount < current.maximumAmount) {
        current
    } else if (minimum) {
        current.copy(maximumAmount = current.minimumAmount + BigDecimal("5"))
    } else {
        current.copy(minimumAmount = current.maximumAmount - BigDecimal("5"))
    }
    val afterValues = if (state.minimumAmount >= state.maximumAmount &&
        state.minimumAmount * BigDecimal("4") < ceiling
    ) {
        correct(state)
    } else {
        state
    }
    return if (afterValues.minimumAmount * BigDecimal("4") > ceiling) correct(afterValues) else afterValues
}
