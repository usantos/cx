package br.gov.caixa.loterias.feature.betting.presentation.state

internal fun reduceRapidConfigurationSelection(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction,
): RapidConfigurationUiState = when (action) {
    RapidConfigurationAction.RequiredNumbersCleared -> reduceRapidConfigurationRequiredNumbersCleared(state)
    RapidConfigurationAction.ExcludedNumbersCleared -> reduceRapidConfigurationExcludedNumbersCleared(state)
    is RapidConfigurationAction.NumberToggled -> reduceRapidConfigurationNumberToggled(state, action)
    is RapidConfigurationAction.PageChanged -> reduceRapidConfigurationPageChanged(state, action)
    is RapidConfigurationAction.ModalityToggled -> reduceRapidConfigurationModalityToggled(state, action)
    RapidConfigurationAction.MessageDismissed -> reduceRapidConfigurationMessageDismissed(state)
    else -> state
}

private fun reduceRapidConfigurationRequiredNumbersCleared(state: RapidConfigurationUiState): RapidConfigurationUiState = run {
    val it = state
    it.copy(requiredNumbers = emptySet())
}

private fun reduceRapidConfigurationExcludedNumbersCleared(state: RapidConfigurationUiState): RapidConfigurationUiState = run {
    val it = state
    it.copy(excludedNumbers = emptySet())
}

private fun reduceRapidConfigurationNumberToggled(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction.NumberToggled,
): RapidConfigurationUiState = run {
    val current = state
    if (action.number !in 1..RAPID_MAXIMUM_NUMBER) return@run current
    val selected = if (action.required) current.requiredNumbers else current.excludedNumbers
    val opposite = if (action.required) current.excludedNumbers else current.requiredNumbers
    if (action.number in opposite) {
        current.copy(warningMessage = action.message)
    } else if (action.required) {
        current.copy(requiredNumbers = if (action.number in selected) selected - action.number else selected + action.number)
    } else {
        current.copy(excludedNumbers = if (action.number in selected) selected - action.number else selected + action.number)
    }
}

private fun reduceRapidConfigurationPageChanged(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction.PageChanged,
): RapidConfigurationUiState = run {
    val it = state
    it.copy(page = action.page.coerceIn(0, LAST_CONFIGURATION_PAGE))
}

private fun reduceRapidConfigurationModalityToggled(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction.ModalityToggled,
): RapidConfigurationUiState = run {
    val current = state
    if (current.availableModalities.none { it.id == action.id }) return@run current
    current.copy(
        selectedModalityIds = if (action.id in current.selectedModalityIds) {
            current.selectedModalityIds - action.id
        } else {
            current.selectedModalityIds + action.id
        }
    )
}

private fun reduceRapidConfigurationMessageDismissed(state: RapidConfigurationUiState): RapidConfigurationUiState = run {
    val it = state
    it.copy(errorMessage = null, warningMessage = null)
}

private const val LAST_CONFIGURATION_PAGE = 6
private const val RAPID_MAXIMUM_NUMBER = 100
