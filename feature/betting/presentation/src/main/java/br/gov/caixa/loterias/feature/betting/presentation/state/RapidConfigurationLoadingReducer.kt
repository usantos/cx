package br.gov.caixa.loterias.feature.betting.presentation.state

internal fun reduceRapidConfigurationLoading(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction,
): RapidConfigurationUiState = when (action) {
    RapidConfigurationAction.ConfigurationLoading -> reduceRapidConfigurationConfigurationLoading(state)
    is RapidConfigurationAction.ConfigurationLoaded -> reduceRapidConfigurationConfigurationLoaded(state, action)
    is RapidConfigurationAction.Failed -> reduceRapidConfigurationFailed(state, action)
    is RapidConfigurationAction.InvalidConfiguration -> reduceRapidConfigurationInvalidConfiguration(state, action)
    RapidConfigurationAction.SavingStarted -> reduceRapidConfigurationSavingStarted(state)
    RapidConfigurationAction.ConfigurationSaved -> reduceRapidConfigurationConfigurationSaved(state)
    else -> state
}

private fun reduceRapidConfigurationConfigurationLoading(state: RapidConfigurationUiState): RapidConfigurationUiState = run {
    val it = state
    it.copy(loading = true, errorMessage = null, saved = false)
}

private fun reduceRapidConfigurationConfigurationLoaded(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction.ConfigurationLoaded,
): RapidConfigurationUiState = run {
    val it = state
    it.copy(
        loading = false,
        minimumAmount = action.configuration.minimumAmount,
        maximumAmount = action.configuration.maximumAmount,
        minimumPrize = action.configuration.minimumPrize,
        requiredNumbers = action.configuration.requiredNumbers,
        excludedNumbers = action.configuration.excludedNumbers,
        availableModalities = action.available,
        selectedModalityIds = action.selected,
        favoriteBetIds = action.configuration.favoriteBetIds,
        errorMessage = null
    )
}

private fun reduceRapidConfigurationFailed(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction.Failed,
): RapidConfigurationUiState = run {
    val it = state
    it.copy(
        loading = false,
        errorMessage = action.message
    )
}

private fun reduceRapidConfigurationInvalidConfiguration(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction.InvalidConfiguration,
): RapidConfigurationUiState = run {
    val it = state
    it.copy(errorMessage = action.message)
}

private fun reduceRapidConfigurationSavingStarted(state: RapidConfigurationUiState): RapidConfigurationUiState = run {
    val it = state
    it.copy(loading = true, errorMessage = null)
}

private fun reduceRapidConfigurationConfigurationSaved(state: RapidConfigurationUiState): RapidConfigurationUiState = run {
    val it = state
    it.copy(loading = false, saved = true)
}
